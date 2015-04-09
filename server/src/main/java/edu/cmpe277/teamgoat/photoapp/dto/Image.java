package edu.cmpe277.teamgoat.photoapp.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Arrays;

@Document
@JsonIgnoreProperties({"_ID"})
public class Image {

    @Id
    private String _ID;

    private String ownerId;

    private String imageId; // Generate a uuid for each image and store the file on the system

    private Double latitude;
    private String location;

    private Double longitude;

    private String description;

    @DBRef
    private Comment[] comments;

    public Image() {

    }

    public Image(String ownerId, String imageId, Double latitude, Double longitude, String description, Comment[] comments) {
        this.ownerId = ownerId;
        this.imageId = imageId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.description = description;
        this.comments = comments;
    }

    public Image(String _ID, String ownerId, String imageId, String location, String description, Comment[] comments) {
        this._ID = _ID;
        this.ownerId = ownerId;
        this.imageId = imageId;
        this.location = location;
        this.description = description;
        this.comments = comments;
    }

    public String get_ID() {
        return _ID;
    }

    public void set_ID(String _ID) {
        this._ID = _ID;
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

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Comment[] getComments() {
        return comments;
    }

    public void setComments(Comment[] comments) {
        this.comments = comments;
    }

    @Override
    public String toString() {
        return "Image{" +
                "_ID='" + _ID + '\'' +
                ", ownerId='" + ownerId + '\'' +
                ", imageId='" + imageId + '\'' +
                ", location='" + location + '\'' +
                ", description='" + description + '\'' +
                ", comments=" + Arrays.toString(comments) +
                '}';
    }
}
