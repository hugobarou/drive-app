const mongoose = require("mongoose");

const fileSchema = new mongoose.Schema({
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
    filePath: {
        type: String,
        required: true,
        min: 1,
        max: 260,
    },
    fileType: {
        type: String,
        required: true,
        min: 1,
        max: 255,
    },
    date: {
        type: Date,
        default: Date.now,
    },
});

module.exports = mongoose.model("File", fileSchema);
