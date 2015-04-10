package edu.cmpe277.teamgoat.photoapp.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;

@Document
//@JsonIgnoreProperties({"_ID"})
public class Image {

    @Id
    private String _ID;

    private String ownerId;

    private String imageId; // Generate a uuid for each image and store the file on the system

    @GeoSpatialIndexed
    private double[] location;  // lat and long

    private String description;

    @DBRef
    @CascadeSave
    private ArrayList<Comment> comments;

    public Image() {

    }

    public Image(String ownerId, String imageId, double[] location, String description, ArrayList<Comment> comments) {
        this.ownerId = ownerId;
        this.imageId = imageId;
        this.location = location;
        this.description = description;
        this.comments = comments;
    }

    public Image(String _ID, String ownerId, String imageId, double[] location, String description, ArrayList<Comment> comments) {
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

    public double[] getLocation() {
        return location;
    }

    public void setLocation(double[] location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public void setComments(Comment comment) {
        this.comments.add(comment);
    }

//    @Override
//    public String toString() {
//        return "Image{" +
//                "_ID='" + _ID + '\'' +
//                ", ownerId='" + ownerId + '\'' +
//                ", imageId='" + imageId + '\'' +
//                ", location='" + Arrays.toString(location) + '\'' +
//                ", description='" + description + '\'' +
//                ", comments=" + Arrays.toString(comments) +
//                '}';
//    }
}
