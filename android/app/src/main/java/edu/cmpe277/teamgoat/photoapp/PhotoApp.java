package edu.cmpe277.teamgoat.photoapp;

import android.app.Application;
import android.content.SharedPreferences;
import android.location.LocationManager;

import java.util.List;

import edu.cmpe277.teamgoat.photoapp.model.Album;
import edu.cmpe277.teamgoat.photoapp.model.ApiBroker;
import edu.cmpe277.teamgoat.photoapp.util.AppLocationServices;
import edu.cmpe277.teamgoat.photoapp.util.IDs;

public class PhotoApp extends Application {

    ////////////////////////////////////////////
    // Preferences
    ////////////////////////////////////////////
    private SharedPreferences masterPreferences;

    public SharedPreferences getMasterPreferences() {
        if (masterPreferences == null) {
            masterPreferences = getPreferences(IDs.BASE_PACKAGE_NAME);
        }

        return masterPreferences;
    }

    public SharedPreferences getPreferences(String preferences) {
        return getSharedPreferences(preferences, MODE_PRIVATE);
    }


    ////////////////////////////////////////////
    // Facebook access token
    ////////////////////////////////////////////
    private String facebookAccessToken;
    private String debugFacebookAccessToken = "CAAWuZCfKYoIYBABNJ9aZC383P9XW6Ffl219kkfoU6lZBHx5AXz8ClLjVgPg2ZB0sAYEKOZB7GJk6qNLDiKqUzi5ZAkwfRlLmuI80BrbIWwDkbO07CZB5N1JbouWGOp1HDVRbmawVDwUoi9AugQIKOyOtyZBtdtVovpxH8ocuzDxqyHX9mVQasHxa4JL74O7AHZBrTw6fw5mLhsepEJwxA6jey"; // Chris
    private boolean mockTheToken = true;

    public String getFacebookAccessToken() {
        return getFacebookAccessToken(mockTheToken);  // TODO change for production
    }

    public String getFacebookAccessToken(boolean production) {
        return production ? facebookAccessToken : debugFacebookAccessToken;
    }

    public void setFacebookAccessToken(String facebookAccessToken) {
        this.facebookAccessToken = facebookAccessToken;
    }

    private String facebookUserId;

    private String debugFacebookUserId = "823579151066492"; // Chris

    public String getFacebookUserId() {
        return getFacebookUserId(mockTheToken);  // TODO change for production
    }

    public String getFacebookUserId(boolean production) {
        return production ? facebookUserId : debugFacebookUserId;
    }

    public void setFacebookUserId(String facebookUserId) {
        this.facebookUserId = facebookUserId;
    }


    ////////////////////////////////////////////
    // API Server URL
    ////////////////////////////////////////////
    public String getServerUrl() {
        return getServerUrl(true);
    }

    public String getServerUrl(boolean production) {
        return production ? "https://srkarra.com:444" : "http://10.0.2.2:80";
    }

    ////////////////////////////////////////////
    // API Broker
    ////////////////////////////////////////////
    private ApiBroker apiBroker;

    public void forceRecreateApiInstance() {
        apiBroker = new ApiBroker(this);
    }

    public ApiBroker getApiBroker() {
        if(apiBroker == null) {
            forceRecreateApiInstance();
        }

        return apiBroker;
    }

    ////////////////////////////////////////////
    // Album Data
    ////////////////////////////////////////////
    private List<Album> userViewableAlbums;

    private int mostRecentSelectedAlbumIndex;

    public List<Album> getUserViewableAlbums() {
        return userViewableAlbums;
    }

    public void setUserViewableAlbums(List<Album> userViewableAlbums) {
        this.userViewableAlbums = userViewableAlbums;
    }

    public int getMostRecentSelectedAlbumIndex() {
        return mostRecentSelectedAlbumIndex;
    }

    public Album getMostRecentSelectedAlbum() {
        return userViewableAlbums == null ? null : userViewableAlbums.get(mostRecentSelectedAlbumIndex);
    }

    public void setMostRecentSelectedAlbumIndex(int mostRecentSelectedAlbumIndex) {
        this.mostRecentSelectedAlbumIndex = mostRecentSelectedAlbumIndex;
    }


    ////////////////////////////////////////////
    // Android Location Manager Service
    ////////////////////////////////////////////
    private AppLocationServices locationManager;

    public AppLocationServices getLocationManager() {
        return locationManager;
    }

    public void setLocationManager(AppLocationServices locationManager) {
        this.locationManager = locationManager;
    }
}
