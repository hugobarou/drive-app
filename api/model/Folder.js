const mongoose = require("mongoose");

const folderSchema = new mongoose.Schema({
    name: {
        type: String,
        required: true,
        min: 1,
        max: 255,
    },
    savePath: {
        type: String,
        required: true,
        min: 1,
        max: 260,
    },
    folderPath: {
        type: String,
        required: true,
        min: 1,
        max: 260,
    },
    date: {
        type: Date,
        default: Date.now,
    },
});

module.exports = mongoose.model("Folder", folderSchema);
