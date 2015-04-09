package edu.cmpe277.teamgoat.photoapp.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Arrays;

@Document
@JsonIgnoreProperties({"_ID"})
public class Image {

    @Id
    private String _ID;

    @Field("OWNERID")
    private String ownerId;

    @Field("IMAGEID")
    private String imageId; // Generate a uuid for each image and store the file on the system

    @Field("LOCATION")
    private String location;

    @Field("DESCRIPTION")
    private String description;

    @DBRef
    private Comment[] comments;

    public Image() {

    }

    public Image(String ownerId, String imageId, String location, String description, Comment[] comments) {
        this.ownerId = ownerId;
        this.imageId = imageId;
        this.location = location;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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
