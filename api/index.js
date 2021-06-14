const upload = require("express-fileupload");
const express = require("express");
const mongoose = require("mongoose");
const dotenv = require("dotenv");
const cors = require("cors");
const path = require("path");
dotenv.config();

const app = express();

// Routes
const authRoute = require("./routes/auth");
const uploadRoute = require("./routes/upload");
const folderRoute = require("./routes/folder");

// Middlewares
app.use(express.json({ limit: "50mb", extended: true }));
app.use(cors());
app.use(
    upload({
        useTempFiles: true,
        tempFileDir: path.join(__dirname, "tmp"),
        limits: { fileSize: 2 * 1024 * 1024 },
    })
);

app.use("/api/user", authRoute);
app.use("/api/upload", uploadRoute);
app.use("/api/folder", folderRoute);
app.use(express.static("drives"));

// Mongoose
mongoose.connect(
    process.env.DB_CONNECT,
    { useUnifiedTopology: true, useNewUrlParser: true },
    () => console.log("Connected to mongoose")
);

app.listen(3000, () => console.log("API up and running"));
