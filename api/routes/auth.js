const { signUpValidation, signInValidation } = require("../utils/validation");
const router = require("express").Router();
const User = require("../model/User");
const Folder = require("../model/Folder");
const jwt = require("jsonwebtoken");
const bcrypt = require("bcryptjs");
const fs = require("fs");
const path = require("path");

router.post("/signup", async (req, res) => {
    // Data validation
    const { error } = signUpValidation(req.body);
    if (error) return res.status(400).send({ error: error.details[0].message });

    // Checking unique email
    const emailExist = await User.findOne({ email: req.body.email });
    if (emailExist)
        return res.status(400).send({ msg: "Email already exists" });

    // Hash password
    const salt = await bcrypt.genSalt(10);
    const hashedPassword = await bcrypt.hash(req.body.password, salt);

    try {
        // Saving user
        const user = new User({
            name: req.body.name,
            email: req.body.email,
            password: hashedPassword,
        });
        const savedUser = await user.save();

        // Creating a drive for the user
        const drivePath = path.join(__dirname, "../drives/", req.body.email);
        if (!fs.existsSync(drivePath)) {
            fs.mkdirSync(drivePath);
        }
        // Creating root folder
        const folder = new Folder({
            name: "/",
            savePath: req.body.email,
            folderPath: req.body.email + "/",
        });
        await folder.save();

        res.status(200).send({
            msg: `Welcome ${savedUser.name} ! Your drive has been created.`,
        });
    } catch (err) {
        console.error(err);
        return res.status(400).send({ error: err.message });
    }
});

router.post("/signin", async (req, res) => {
    // Data validation
    const { error } = signInValidation(req.body);
    if (error) return res.status(400).send({ error: error.details[0].message });

    // Checking if user exists
    const user = await User.findOne({ email: req.body.email });
    if (!user) return res.status(400).send({ error: "Invalid email" });

    // Checking valid password
    const validPass = await bcrypt.compare(req.body.password, user.password);
    if (!validPass) return res.status(400).send({ error: "Invalid password" });

    // Creating token
    const token = jwt.sign({ email: user.email }, process.env.TOKEN_SECRET);
    res.header("Authorization", token)
        .status(200)
        .send({ token: token, email: user.email });
});

module.exports = router;
