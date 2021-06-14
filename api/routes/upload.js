const router = require("express").Router();
const verify = require("./verifyToken");
const File = require("../model/File");
const path = require("path");
const fs = require("fs");
const { uploadFileValidation } = require("../utils/validation");

router.post("/single", verify, async (req, res) => {
    // Data validation
    const { error } = uploadFileValidation(req.body);
    if (error) return res.status(400).send({ error: error.details[0].message });

    try {
        // Checking if path exists
        const pathExist = path.join(__dirname, "../drives/", req.body.path);
        if (!fs.existsSync(pathExist)) {
            throw new Error("Path does not exist");
        }

        // Checking if file name is too big for path && if file size is too big
        const file = req.files.file;
        if ((file.name + req.body.path).length >= 260) {
            throw new Error("Name too big for the path : " + file.name);
        }
        if (file.truncated) {
            throw new Error("Size of the file is too big : " + file.name);
        }

        // Saving file
        const fileToSave = new File({
            name: file.name,
            savePath: req.body.path,
            filePath: req.body.path + file.name,
            fileType: file.mimetype,
        });
        const savedFile = await fileToSave.save();

        // Moving file
        await file.mv(pathExist + savedFile.name);

        res.status(200).send({
            msg: "File has been uploaded",
        });
    } catch (err) {
        console.error(err);
        return res.status(400).send({ error: err.message });
    }
});
router.post("/multiple", verify, async (req, res) => {
    // Data validation
    const { error } = uploadFileValidation(req.body);
    if (error) return res.status(400).send({ error: error.details[0].message });

    try {
        // Checking if path exists
        const pathExist = path.join(__dirname, "../drives/", req.body.path);
        if (!fs.existsSync(pathExist)) {
            throw new Error("Path does not exist");
        }

        // Checking if file name is too big for path && if file size is too big
        const files = req.files.files;
        Object.keys(files).forEach((file) => {
            if ((files[file].name + req.body.path).length >= 260) {
                throw new Error(
                    "Name too big for the path : " + files[file].name
                );
            }
            if (files[file].truncated) {
                throw new Error(
                    "Size of the file is too big : " + files[file].name
                );
            }
        });

        let promises = [];
        files.forEach(async (file) => {
            // Saving file
            const fileToSave = new File({
                name: file.name,
                savePath: req.body.path,
                filePath: req.body.path + file.name,
                fileType: file.mimetype,
            });
            const savedFile = await fileToSave.save();

            // Moving file
            promises.push(file.mv(pathExist + savedFile.name));
        });
        await Promise.all(promises);

        res.status(200).send({
            msg: "Files has been uploaded",
        });
    } catch (err) {
        console.error(err);
        return res.status(400).send({ error: err.message });
    }
});

router.post("/encoded", verify, async (req, res) => {
    try {
        // Checking if path exists
        const pathExist = path.join(__dirname, "../drives/", req.body.path);
        if (!fs.existsSync(pathExist)) {
            throw new Error("Path does not exist");
        }

        // Saving file
        const fileToSave = new File({
            name: req.body.fileName,
            savePath: req.body.path,
            filePath: req.body.path + req.body.fileName,
            fileType: "image/jpeg",
        });
        const savedFile = await fileToSave.save();

        // Moving file
        const data = req.body.file;
        const buf = Buffer.from(data, "base64");
        fs.writeFileSync(pathExist + savedFile.name, buf);

        res.status(200).send({
            msg: "File has been uploaded",
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

        // Checking file exists
        const file = await File.findOne({ filePath: req.body.path });
        if (!file) throw new Error("File does not exist");

        await File.deleteOne({ filePath: req.body.path });

        fs.rmdir(pathExist, { recursive: true }, (err) => {
            if (err) {
                throw new Error("Unable to delete de file");
            }
        });

        res.status(200).send({
            msg: `The file ${file.name} as been deleted`,
        });
    } catch (err) {
        console.error(err);
        return res.status(400).send({ error: err.message });
    }
});

module.exports = router;
