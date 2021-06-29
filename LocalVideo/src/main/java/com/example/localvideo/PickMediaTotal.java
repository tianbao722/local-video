package com.example.localvideo;

/**
 * Created by hupei on 2016/7/14.
 */
public class PickMediaTotal {
    private String topPhotoPath;//文件夹的第一张图片路径
    private String folderName;//文件夹名
    private int PhotoCount;//文件夹中的图片数


    public String getTopPhotoPath() {
        return topPhotoPath;
    }

    public void setTopPhotoPath(String topPhotoPath) {
        this.topPhotoPath = topPhotoPath;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public int getPhotoCount() {
        return PhotoCount;
    }

    public void setPhotoCount(int PhotoCount) {
        this.PhotoCount = PhotoCount;
    }

}
