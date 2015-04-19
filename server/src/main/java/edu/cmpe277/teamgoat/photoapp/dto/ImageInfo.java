package edu.cmpe277.teamgoat.photoapp.dto;

public class ImageInfo {
    public int width;
    public int height;
    public int sizeBytes;
    public String mimeType;

    public ImageInfo(int width, int height, int sizeBytes, String mimeType) {
        this.width = width;
        this.height = height;
        this.sizeBytes = sizeBytes;
        this.mimeType = mimeType;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getSizeBytes() {
        return sizeBytes;
    }

    public void setSizeBytes(int sizeBytes) {
        this.sizeBytes = sizeBytes;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
}
