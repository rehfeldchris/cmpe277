package edu.cmpe277.teamgoat.photoapp.model;

import java.util.List;

/**
 * Created by squall on 4/29/15.
 */
public class Album
{
    private String _ID;
    private String name;
    private int total_image;
    private String description;
    private String ownerId;
    private List<String> grantedUserIds;
    private boolean isPubliclyAccessible = false;
    private String coverPhotoUrl;
    private List<Image> images;

    public Album(String name, String _ID, String ownerId) {
        this.name = name;
        this._ID = _ID;
        this.ownerId = ownerId;
    }

    public Album() {

    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String get_ID() {

        return _ID;
    }

    public String getName() {
        return name;
    }

    public int getTotal_image() {
        return total_image;
    }

    public String getDescription() {
        return description;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public List<String> getGrantedUserIds() {
        return grantedUserIds;
    }

    public boolean isPubliclyAccessible() {
        return isPubliclyAccessible;
    }

    public void addGrantedUserId(String id)
    {
        grantedUserIds.add(id);
    }

    public String getCoverPhotoUrl() {
        return coverPhotoUrl;
    }

    public List<Image> getImages() {
        return images;
    }
}
