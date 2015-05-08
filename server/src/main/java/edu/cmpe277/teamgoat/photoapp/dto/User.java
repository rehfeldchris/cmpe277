package edu.cmpe277.teamgoat.photoapp.dto;

import org.springframework.data.annotation.Id;

import java.net.URLEncoder;
import java.util.Date;

public class User {

    @Id
    private String facebookUserId;

    private String name;

    private String profilePhotoUrl;

    private Date whenDataFetchedFromFacebook;

    public User() {
    }

    public User(com.restfb.types.User fbuser) {
        setFacebookUserId(fbuser.getId());
        setName(fbuser.getName());
        setWhenDataFetchedFromFacebook(new Date());
        setProfilePhotoUrl();
    }

    public User(String facebookUserId, String name, Date whenDataFetchedFromFacebook) {
        this.facebookUserId = facebookUserId;
        this.name = name;
        this.whenDataFetchedFromFacebook = whenDataFetchedFromFacebook;
        setProfilePhotoUrl();
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
        setProfilePhotoUrl();
        return profilePhotoUrl;
    }

    private void setProfilePhotoUrl() {
        profilePhotoUrl = String.format("https://graph.facebook.com/%s/picture", URLEncoder.encode(facebookUserId));
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
