package edu.cmpe277.teamgoat.photoapp.model;

/**
 * Created by squall on 4/29/15.
 */
public class Image
{
//    private String id;
    private String ownerId;
    private String imageId;
    private double[] locations;
    private String description;
    private int width;
    private int height;
    private int sizeBytes;
    private String mimeType;
    private String albumId;


    public Image(String ownerId, String imageId, String albumId) {
        this.ownerId = ownerId;
        this.imageId = imageId;
        this.albumId = albumId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public double[] getLocations() {
        return locations;
    }

    public void setLocations(double[] locations) {
        this.locations = locations;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }
}
