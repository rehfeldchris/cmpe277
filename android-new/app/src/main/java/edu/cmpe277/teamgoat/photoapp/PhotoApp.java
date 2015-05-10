package edu.cmpe277.teamgoat.photoapp;

import android.app.Application;
import android.content.SharedPreferences;

import java.util.logging.Logger;

import edu.cmpe277.teamgoat.photoapp.model.ApiBroker;
import edu.cmpe277.teamgoat.photoapp.util.IDs;

public class PhotoApp extends Application {

    private SharedPreferences masterPreferences;

    private String facebookAccessToken;

    private ApiBroker apiBroker;

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

    public String getServerUrl() {
        return getServerUrl(true);
    }

    public String getServerUrl(boolean production) {
        return production ? "https://srkarra.com:444" : "http://10.0.2.2:80";
    }

    public void forceRecreateApiInstance() {
        apiBroker = new ApiBroker(this);
    }

    public ApiBroker getApiBroker() {
        if(apiBroker == null) {
            forceRecreateApiInstance();
        }

        return apiBroker;
    }
}
