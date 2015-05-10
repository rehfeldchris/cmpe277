package edu.cmpe277.teamgoat.photoapp;

import android.app.Application;
import android.content.SharedPreferences;

import java.util.logging.Logger;

import edu.cmpe277.teamgoat.photoapp.util.IDs;

public class PhotoApp extends Application {

    private SharedPreferences masterPreferences;

    private String facebookAccessToken;

    public SharedPreferences getMasterPreferences() {
        if (masterPreferences == null) {
            masterPreferences = getPreferences(IDs.BASE_PACKAGE_NAME);
        }

        return masterPreferences;
    }

    public SharedPreferences getPreferences(String preferences) {
        return getSharedPreferences(preferences, MODE_PRIVATE);
    }

    public String getFacebookAccessToken() {
        return facebookAccessToken;
    }

    public void setFacebookAccessToken(String facebookAccessToken) {
        this.facebookAccessToken = facebookAccessToken;
    }
}
