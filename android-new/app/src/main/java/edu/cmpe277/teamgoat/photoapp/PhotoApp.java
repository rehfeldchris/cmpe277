package edu.cmpe277.teamgoat.photoapp;

import android.app.Application;
import android.content.SharedPreferences;

import edu.cmpe277.teamgoat.photoapp.model.ApiBroker;
import edu.cmpe277.teamgoat.photoapp.util.IDs;

public class PhotoApp extends Application {

    private  static String debugFacebookAccessToken = "CAAWuZCfKYoIYBABNJ9aZC383P9XW6Ffl219kkfoU6lZBHx5AXz8ClLjVgPg2ZB0sAYEKOZB7GJk6qNLDiKqUzi5ZAkwfRlLmuI80BrbIWwDkbO07CZB5N1JbouWGOp1HDVRbmawVDwUoi9AugQIKOyOtyZBtdtVovpxH8ocuzDxqyHX9mVQasHxa4JL74O7AHZBrTw6fw5mLhsepEJwxA6jey";
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
        return getFacebookAccessToken(false);  // TODO change for production
    }

    public String getFacebookAccessToken(boolean production) {
        return production ? facebookAccessToken : debugFacebookAccessToken;
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
