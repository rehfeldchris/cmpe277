package edu.cmpe277.teamgoat.photoapp.model;

import java.util.List;

/**
 * Created by squall on 4/29/15.
 */
public class Album
{
    private String _id;
    private String title;
    private int total_image;
    private String description;
    private String ownerId;
    private List<String> grantedUserIds;
    private boolean isPubliclyAccessible = false;

    public Album(String title, String _id, String ownerId) {
        this.title = title;
        this._id = _id;
        this.ownerId = ownerId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String get_id() {

        return _id;
    }

    public String getTitle() {
        return title;
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
}
