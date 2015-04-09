package edu.cmpe277.teamgoat.photoapp.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Arrays;

@Document
//@JsonIgnoreProperties({"_ID"})
public class Album {

    @Id
    private String _ID;

    private String name;

    private String ownerId;

    private String grantedUserIds;

    @DBRef
    private Image[] images;

    public Album() {

    }

    public Album(String name, String ownerId, String grantedUserIds, Image[] images) {
        this.name = name;
        this.ownerId = ownerId;
        this.grantedUserIds = grantedUserIds;
        this.images = images;
    }

    public Album(String _ID, String name, String ownerId, String grantedUserIds, Image[] images) {
        this._ID = _ID;
        this.name = name;
        this.ownerId = ownerId;
        this.grantedUserIds = grantedUserIds;
        this.images = images;
    }

    public String get_ID() {
        return _ID;
    }

    public void set_ID(String _ID) {
        this._ID = _ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getGrantedUserIds() {
        return grantedUserIds;
    }

    public void setGrantedUserIds(String grantedUserIds) {
        this.grantedUserIds = grantedUserIds;
    }

    public Image[] getImages() {
        return images;
    }

    public void setImages(Image[] images) {
        this.images = images;
    }

    @Override
    public String toString() {
        return "Album{" +
                "_ID='" + _ID + '\'' +
                ", name='" + name + '\'' +
                ", ownerId='" + ownerId + '\'' +
                ", grantedUserIds='" + grantedUserIds + '\'' +
                ", images=" + Arrays.toString(images) +
                '}';
    }
}
