package edu.cmpe277.teamgoat.photoapp.dto;

import edu.cmpe277.teamgoat.photoapp.dto.CascadingMongoEventListener;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;

@Document
//@JsonIgnoreProperties({"_ID"})
@CompoundIndexes({
        @CompoundIndex(name = "unique_album_idx", def = "{'name': 1, 'ownerId': 1}")
})
public class Album {

    @Id
    private String _ID;

    private String name;

    @Indexed
    private String ownerId;

	@Indexed
    private ArrayList<String> grantedUserIds;

    @DBRef
    @CascadeSave
    private ArrayList<Image> images;

    public Album() {

    }

    public Album(String name, String ownerId, ArrayList<String> grantedUserIds, ArrayList<Image> images) {
        this.name = name;
        this.ownerId = ownerId;
        this.grantedUserIds = grantedUserIds;
        this.images = images;
    }

    public Album(String _ID, String name, String ownerId, ArrayList<String> grantedUserIds, ArrayList<Image> images) {
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

    public ArrayList<String> getGrantedUserIds() {
        return grantedUserIds;
    }

    public void setGrantedUserIds(String grantUser) {
        this.grantedUserIds.add(grantUser);
    }

    public ArrayList<Image> getImages() {
        return images;
    }

    public void setImages(Image image) {
        this.images.add(image);
    }

//    @Override
//    public String toString() {
//        return "Album{" +
//                "_ID='" + _ID + '\'' +
//                ", name='" + name + '\'' +
//                ", ownerId='" + ownerId + '\'' +
//                ", grantedUserIds='" + Arrays.toString(grantedUserIds) + '\'' +
//                ", images=" + Arrays.toString(images) +
//                '}';
//    }
}
