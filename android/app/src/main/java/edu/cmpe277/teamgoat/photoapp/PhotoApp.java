package edu.cmpe277.teamgoat.photoapp;

import android.app.Application;
import android.content.SharedPreferences;

import java.util.logging.Logger;

import edu.cmpe277.teamgoat.photoapp.util.IDs;
import edu.cmpe277.teamgoat.photoapp.util.PhotoAppLog;

public class PhotoApp extends Application {

    private SharedPreferences masterPreferences;

    private PhotoAppLog masterLogger;

    public SharedPreferences getMasterPreferences() {
        if (masterPreferences == null) {
            masterPreferences = getPreferences(IDs.BASE_PACKAGE_NAME);
        }

        return masterPreferences;
    }

    public SharedPreferences getPreferences(String preferences) {
        return getSharedPreferences(preferences, MODE_PRIVATE);
    }

    public PhotoAppLog getMasterLogger() {
        if (masterLogger == null) {
            masterLogger = createLogger(IDs.BASE_PACKAGE_NAME);
        }

        return masterLogger;
    }

    public PhotoAppLog createLogger(String loggingPackage) {
        return new PhotoAppLog(Logger.getLogger(loggingPackage));
    }
}
