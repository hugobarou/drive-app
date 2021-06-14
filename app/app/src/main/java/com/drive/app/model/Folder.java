package com.drive.app.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class Folder {
    private String name;
    private String savePath;
    private String folderPath;
    private ArrayList<Folder> lstFolder;
    private ArrayList<File> lstFile;

    public Folder() {
    }

    public String getName() {
        return name;
    }

    public String getSavePath() {
        return savePath;
    }

    public String getFolderPath() {
        return folderPath;
    }

    public ArrayList<Folder> getLstFolder() {
        return lstFolder;
    }

    public ArrayList<File> getLstFile() {
        return lstFile;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    public void setFolderPath(String folderPath) {
        this.folderPath = folderPath;
    }

    public void setLstFolder(ArrayList<Folder> lstFolder) {
        this.lstFolder = lstFolder;
    }

    public void setLstFile(ArrayList<File> lstFile) {
        this.lstFile = lstFile;
    }

    public static Folder fromJson(JSONObject jsonObject) {
        Folder f = new Folder();
        ArrayList<Folder> lstFolder = new ArrayList<Folder>();
        ArrayList<File> lstFile = new ArrayList<File>();
        Folder folder;
        File file;

        try {
            JSONObject folderJSON = jsonObject.getJSONObject("folder");
            JSONArray ArrayLstFolders = jsonObject.getJSONArray("lstFolders");
            JSONArray ArrayLstFiles = jsonObject.getJSONArray("lstFiles");

            f.setName(folderJSON.getString("name"));
            f.setSavePath(folderJSON.getString("savePath"));
            f.setFolderPath(folderJSON.getString("folderPath"));

            for (int i = 0; i < ArrayLstFolders.length(); i++){
                folderJSON = ArrayLstFolders.getJSONObject(i);
                folder = new Folder();
                folder.setName(folderJSON.getString("name"));
                folder.setSavePath(folderJSON.getString("savePath"));
                folder.setFolderPath(folderJSON.getString("folderPath"));
                lstFolder.add(folder);
            }

            JSONObject fileJSON;
            for (int i = 0; i < ArrayLstFiles.length(); i++){
                fileJSON = ArrayLstFiles.getJSONObject(i);
                file = new File();
                file.setName(fileJSON.getString("name"));
                file.setSavePath(fileJSON.getString("savePath"));
                file.setFilePath(fileJSON.getString("filePath"));
                file.setFileType(fileJSON.getString("fileType"));
                lstFile.add(file);
            }
            f.setLstFolder(lstFolder);
            f.setLstFile(lstFile);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return f;
    }

    @Override
    public String toString() {
        return "Folder{" +
                "folderName='" + name + '\'' +
                ", savePath='" + savePath + '\'' +
                ", folderPath='" + folderPath + '\'' +
                '}';
    }
}
