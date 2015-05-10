package edu.cmpe277.teamgoat.photoapp.model;

import java.util.Date;

/**
 * Created by squall on 4/29/15.
 */
public class User
{

    private String facebookUserId;

    private String name;

    private String profilePhotoUrl;

    private Date whenDataFetchedFromFacebook;

    public User() {
    }

    public User(String facebookUserId, String name, String profilePhotoUrl, Date whenDataFetchedFromFacebook) {
        this.facebookUserId = facebookUserId;
        this.name = name;
        this.profilePhotoUrl = profilePhotoUrl;
        this.whenDataFetchedFromFacebook = whenDataFetchedFromFacebook;
    }

    public String getFacebookUserId() {
        return facebookUserId;
    }

    public void setFacebookUserId(String facebookUserId) {
        this.facebookUserId = facebookUserId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfilePhotoUrl() {
        return profilePhotoUrl;
    }

    public void setProfilePhotoUrl(String profilePhotoUrl) {
        this.profilePhotoUrl = profilePhotoUrl;
    }

    public Date getWhenDataFetchedFromFacebook() {
        return whenDataFetchedFromFacebook;
    }

    public void setWhenDataFetchedFromFacebook(Date whenDataFetchedFromFacebook) {
        this.whenDataFetchedFromFacebook = whenDataFetchedFromFacebook;
    }

    public long getMillisSinceLastFacebookFetch() {
        return new Date().getTime() - whenDataFetchedFromFacebook.getTime();
    }
}
