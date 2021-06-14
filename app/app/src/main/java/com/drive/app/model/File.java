package com.drive.app.model;

import com.drive.app.R;

public class File {
    private String name;
    private String savePath;
    private String filePath;
    private String fileType;
    private int image;

    public File() {
    }

    public String getName() {
        return name;
    }

    public String getSavePath() {
        return savePath;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getFileType() { return fileType; }

    public int getImage() { return image; }

    public void setName(String name) {
        this.name = name;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
        String[] mimeType = fileType.split("/");
        switch (mimeType[0]){
            case "image":
                this.image = R.drawable.photo;
                break;
            case "text":
                this.image = R.drawable.document;
                break;
            default:
                this.image = R.drawable.file;
        }
    }

    @Override
    public String toString() {
        return "File{" +
                "name='" + name + '\'' +
                ", savePath='" + savePath + '\'' +
                ", filePath='" + filePath + '\'' +
                ", fileType='" + fileType + '\'' +
                '}';
    }
}
