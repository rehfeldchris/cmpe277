package edu.cmpe277.teamgoat.photoapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcel;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.applinks.AppLinkData;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;


import java.io.IOException;

import bolts.AppLinks;
import edu.cmpe277.teamgoat.photoapp.util.IDs;
import edu.cmpe277.teamgoat.photoapp.util.PaLog;

public class MainActivity extends Activity {

    // Facebook instance variables
    private CallbackManager callbackManager;
    private LoginManager loginManager;
    private AccessTokenTracker accessTokenTracker;
    private PhotoApp photoApp;
    //Google Cloud Messaging
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    GoogleCloudMessaging gcm;
    Context context;
    String regid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        photoApp = (PhotoApp) getApplication();
        context = getApplicationContext();

        // Initialize the Facebook SDK, must be done before the UI is loaded or else it crashes
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);

        // Handles what happens when user accepts app invite from link sent
        // this is triggered from friend clicking on link
        Uri targetUrl = AppLinks.getTargetUrlFromInboundIntent(this, getIntent());
        if (targetUrl != null) {
            Log.i("Activity", "App Link Target URL: " + targetUrl.toString());
        } else {
            AppLinkData.fetchDeferredAppLinkData(
                this,
                new AppLinkData.CompletionHandler() {
                    @Override
                    public void onDeferredAppLinkDataFetched(AppLinkData appLinkData) {
                        //process applink data
                    }
                }
            );
        }

        // Check device for Play Services APK. Required for GCM
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            regid = getRegistrationId(context);

            if (regid.isEmpty()) {
                registerInBackground();
            }
        } else {
            PaLog.info("No valid Google Play Services APK found.");
        }

        // This lets us show the login screen if the user selected it or goes directly to the main activity if the user is just launching the app
        // NOTE: To show the logout screen, launch this activity with the force intent key set using key INTENT_LAUNCH_LOGIN_VIEW_FORCE_VIEW_PARAMETER_KEY
        boolean forceShowLoginScreen = false;
        Intent launchIntent = getIntent();
        if (launchIntent != null ) {
            Bundle launchBundle = launchIntent.getExtras();
            forceShowLoginScreen = launchBundle != null && launchBundle.getBoolean(IDs.INTENT_LAUNCH_LOGIN_VIEW_FORCE_VIEW_PARAMETER_KEY, false);
        }

        AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                verifyLoginStateAndUpdateButtonVisibility();
            }
        };


        // Check if the user is logged in by getting the current access token
        AccessToken currentAccessToken = AccessToken.getCurrentAccessToken();
        if (forceShowLoginScreen || currentAccessToken == null || currentAccessToken.isExpired()) {
            PaLog.debug("Need to show the login screen");

            callbackManager = CallbackManager.Factory.create();
            loginManager = LoginManager.getInstance();

            LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
            loginButton.setReadPermissions(IDs.FACEBOOK_LOGIN_PERMISSIONS);
            loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

                @Override
                public void onSuccess(LoginResult loginResult) {
                    assignAndDoThingsWithFacebookAccessToken(loginResult.getAccessToken());
                    Toast.makeText(getApplicationContext(), R.string.login_facebook_successful, Toast.LENGTH_SHORT).show();
                    launchMainPhotoAppActivity();
                }

                @Override
                public void onCancel() {
                    Toast.makeText(getApplicationContext(), R.string.login_facebook_cancelled, Toast.LENGTH_SHORT).show();
                    PaLog.info("User cancelled Facebook Authentication.");
                }

                @Override
                public void onError(FacebookException exception) {
                    Toast.makeText(getApplicationContext(), R.string.login_facebook_failed, Toast.LENGTH_SHORT).show();
                    PaLog.error("Facebook authentication failed, Msg: " + exception.getMessage(), exception);
                }
            });
        } else {
            PaLog.debug("Already have the facebook token, show the albums screen");
            assignAndDoThingsWithFacebookAccessToken(currentAccessToken);
            launchMainPhotoAppActivity();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
        verifyLoginStateAndUpdateButtonVisibility();
    }

    public void goToAlbumsButtonClicked(View view) {
        // One last sanity check, just to be sure
        AccessToken currentAccessToken = AccessToken.getCurrentAccessToken();
        if (currentAccessToken == null || currentAccessToken.isExpired()) {
            verifyLoginStateAndUpdateButtonVisibility();
        } else {
            launchMainPhotoAppActivity();
        }

    }

    private void verifyLoginStateAndUpdateButtonVisibility() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Button goToAlbumsBtn = (Button) findViewById(R.id.login_btn_launch_app);
                AccessToken currentAccessToken = AccessToken.getCurrentAccessToken();
                goToAlbumsBtn.setVisibility( currentAccessToken == null || currentAccessToken.isExpired() ? View.GONE : View.VISIBLE);
                goToAlbumsBtn.setText(R.string.login_facebook_launch_app_button_text); // Force redraw
            }
        });
    }

    private void assignAndDoThingsWithFacebookAccessToken(AccessToken accessToken) {
        String accessTokenString = accessToken.getToken();

        // Debug
        PaLog.info(String.format("\n=================\n" +
                        "Application Id: '%s'\n" +
                        "UserId: '%s'\n" +
                        "Access Token: '%s'\n" +
                        "Expiration: '%s'\n" +
                        "Last Refresh: '%s'\n" +
                        "=================\n",
                accessToken.getApplicationId(),
                accessToken.getUserId(),
                accessTokenString,
                accessToken.getExpires(),
                accessToken.getLastRefresh()));

        photoApp.setFacebookAccessToken(accessTokenString);
        photoApp.setFacebookUserId(accessToken.getUserId());
        photoApp.forceRecreateApiInstance();
    }

    private void launchMainPhotoAppActivity() {
        PaLog.info("Launching Main PhotoApp Activity");

        // Launch main activity
        Intent i = new Intent(this, PhotoAlbums.class);
        startActivity(i);

        // Finish the current activity
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                //This device is not supported
                finish();
            }
            return false;
        }
        return true;
    }

    /**
     * Gets the current registration ID for application on GCM service.
     * If result is empty, the app needs to register.
     */
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            PaLog.info("Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing registration ID is not guaranteed to work with
        // the new app version.
//        int registration = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
//        int currentVersion = getAppVersion(context);
//        if (registeredVersion != currentVersion) {
//            Log.i(TAG, "App version changed.");
//            return "";
//        }
        return registrationId;
    }

    /**
     * Stores the registration ID and the app versionCode in the application
     */
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGcmPreferences(context);
        int appVersion = getAppVersion(context);
        PaLog.info("Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGcmPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * Stores the registration ID and the app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register();
                    msg = "Device registered, registration ID=" + regid;

                    // You should send the registration ID to your server over HTTP, so it
                    // can use GCM/HTTP or CCS to send messages to your app.
//                    sendRegistrationIdToBackend();

                    // Persist the regID - no need to register again.
                    storeRegistrationId(context, regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }
        }.execute(null, null, null);
    }

    /**
     * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP
     * or CCS to send messages to your app. Not needed for this demo since the
     * device sends upstream messages to a server that echoes back the message
     * using the 'from' address in the message.
     */
    private void sendRegistrationIdToBackend() {
        // Your implementation here.
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the registration ID in your app is up to you.
        return getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }
}
