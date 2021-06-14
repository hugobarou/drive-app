const router = require("express").Router();
const verify = require("./verifyToken");
const Folder = require("../model/Folder");
const File = require("../model/File");
const fs = require("fs");
const path = require("path");
const {
    createFolderValidation,
    getFolderValidation,
} = require("../utils/validation");

router.post("/create", verify, async (req, res) => {
    // Data validation
    const { error } = createFolderValidation(req.body);
    if (error) return res.status(400).send({ error: error.details[0].message });

    // Checking if folder name is too big for path
    if ((req.body.name + req.body.path).length >= 260)
        return res.status(400).send({ error: "Name too big for the path" });

    // Checking if folder already exists
    const folderExist = await Folder.findOne({
        name: req.body.name,
        savePath: req.body.path,
    });
    if (folderExist)
        return res
            .status(400)
            .send({ msg: "Folder already exist in this path" });

    try {
        // Checking if path exists
        const pathExist = path.join(__dirname, "../drives/", req.body.path);
        if (!fs.existsSync(pathExist)) {
            throw new Error("Path does not exist");
        }

        // Saving folder
        const folder = new Folder({
            name: req.body.name,
            savePath: req.body.path,
            folderPath: req.body.path + req.body.name + "/",
        });
        const savedFolder = await folder.save();

        // Creating new folder
        fs.mkdirSync(pathExist + savedFolder.name);

        res.status(200).send({
            msg: `The folder ${savedFolder.name} as been created at this path ${savedFolder.savePath}`,
        });
    } catch (err) {
        console.error(err);
        return res.status(400).send({ error: err.message });
    }
});

router.post("/", verify, async (req, res) => {
    // Data validation
    const { error } = getFolderValidation(req.body);
    if (error) return res.status(400).send({ error: error.details[0].message });
    try {
        // Checking if path exists
        const pathExist = path.join(__dirname, "../drives/", req.body.path);
        if (!fs.existsSync(pathExist)) {
            throw new Error("Path does not exist");
        }

        // Checking if folder exists
        const folder = await Folder.findOne({
            folderPath: req.body.path,
        });
        if (!folder) return res.status(400).send({ error: "Invalid path" });

        // Finding all folder
        const lstFolders = await Folder.find({
            savePath: req.body.path,
        });

        // Finding all files
        const lstFiles = await File.find({
            savePath: req.body.path,
        });

        res.status(200).send({
            folder: folder,
            lstFolders: lstFolders,
            lstFiles: lstFiles,
        });
    } catch (err) {
        console.error(err);
        return res.status(400).send({ error: err.message });
    }
});

router.post("/delete", verify, async (req, res) => {
    try {
        // Checking if path exists
        const pathExist = path.join(__dirname, "../drives/", req.body.path);
        if (!fs.existsSync(pathExist)) {
            throw new Error("Path does not exist");
        }

        // Checking folder exists
        const folder = await Folder.findOne({ folderPath: req.body.path });
        if (!folder) throw new Error("Folder does not exist");

        await Folder.deleteOne({ folderPath: req.body.path });
        await Folder.deleteMany({ savePath: req.body.path });
        await File.deleteMany({ savePath: req.body.path });

        fs.rmdir(pathExist, { recursive: true }, (err) => {
            if (err) {
                throw new Error("Unable to delete de folder");
            }
        });

        res.status(200).send({
            msg: `The folder ${folder.name} as been deleted`,
        });
    } catch (err) {
        console.error(err);
        return res.status(400).send({ error: err.message });
    }
});

module.exports = router;
