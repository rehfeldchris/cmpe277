package edu.cmpe277.teamgoat.photoapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import org.apache.http.util.LangUtils;

import java.util.HashMap;
import java.util.Map;

import edu.cmpe277.teamgoat.photoapp.util.CustomImageDownloader;
import edu.cmpe277.teamgoat.photoapp.util.IDs;
import edu.cmpe277.teamgoat.photoapp.util.PaLog;


public class LoginActivity extends Activity {

    // Facebook instance variables
    private CallbackManager callbackManager;
    private LoginManager loginManager;
    private AccessTokenTracker accessTokenTracker;
    private String accessTokenString;

    private PhotoApp photoApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        photoApp = (PhotoApp) getApplication();
        initializeExternalFrameworks();
        setContentView(R.layout.activity_login);


        // This lets us show the login screen if the user selected it
        // or goes directly to the main activity if the user is just launching the app
        boolean forceShowLoginScreen = false;
        Intent launchIntent = getIntent();
        if (launchIntent != null ) {
            Bundle launchBundle = launchIntent.getExtras();
            forceShowLoginScreen = launchBundle != null && launchBundle.getBoolean(IDs.INTENT_LAUNCH_LOGIN_VIEW_FORCE_VIEW_PARAMETER_KEY, false);
        }

        // Facebook Access Token Tracker
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, final AccessToken newAccessToken) {
                // The user has logged out of Facebook, hide the button
                runOnUiThread(new Runnable() {
                    public void run() {
                        setGoToMainAppButtonState(newAccessToken == null ? View.INVISIBLE : View.VISIBLE);
                    }
                });
            }
        };

        // Get the current access token, if it's null or expired, we will show the login view, else redirect to the main app
        // NOTE: To show the logout screen, launch this activity with the force intent key set using key INTENT_LAUNCH_LOGIN_VIEW_FORCE_VIEW_PARAMETER_KEY
        AccessToken currentAccessToken = AccessToken.getCurrentAccessToken();

        // we need to show the go to album button if the token is
        if (currentAccessToken != null && !currentAccessToken.isExpired()) {
            setGoToMainAppButtonState(View.VISIBLE);
        }

        if (forceShowLoginScreen || currentAccessToken == null || currentAccessToken.isExpired()) {
            PaLog.debug("Need to show the login screen");

            LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
            loginButton.setReadPermissions(IDs.FACEBOOK_LOGIN_PERMISSIONS);
            loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    assignAndDoThingsWithFacebookAccessToken(loginResult.getAccessToken());
                    Toast.makeText(getApplicationContext(), R.string.login_facebook_toast_successful, Toast.LENGTH_SHORT).show();
                    launchMainPhotoAppActivity();
                }

                @Override
                public void onCancel() {
                    Toast.makeText(getApplicationContext(), R.string.login_facebook_toast_cancelled, Toast.LENGTH_SHORT).show();
                    PaLog.info("User cancelled Facebook Authentication.");
                }

                @Override
                public void onError(FacebookException exception) {
                    Toast.makeText(getApplicationContext(), R.string.login_facebook_toast_failed, Toast.LENGTH_SHORT).show();
                    PaLog.error("Facebook authentication failed, Msg: " + exception.getMessage(), exception);
                }
            });
        } else {
            PaLog.debug("Already have the facebook token, show the albums screen");
            assignAndDoThingsWithFacebookAccessToken(currentAccessToken);
            launchMainPhotoAppActivity();
        }

        setContentView(R.layout.activity_login);
    }

    /**
     * Android Activity Lifecycle Listener
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Initialize any and all third party apps
     * Facebook
     */
    private void initializeExternalFrameworks() {
        // Initialize the Facebook SDK
        FacebookSdk.sdkInitialize(getApplicationContext());

        callbackManager = CallbackManager.Factory.create();
        loginManager = LoginManager.getInstance();
    }


    private void setGoToMainAppButtonState(int status) {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.login_linear_layout);
        Button button = (Button) findViewById(R.id.login_btn_launch_app);
        if (linearLayout != null && button != null) {
            PaLog.info("Button Visibility Before Change: " + button.getVisibility());
            button.setVisibility(status);
            PaLog.info(String.format("Button Visibility After Change : %s, Requested: %s ", button.getVisibility(), status));
            PaLog.info(String.format("%15s: %-4s, %15s: %-4s, %15s: %-4s", "Visibile", View.VISIBLE, "Invisible", View.INVISIBLE, "GONE", View.GONE));
            linearLayout.invalidate();
        }
    }


    private void assignAndDoThingsWithFacebookAccessToken(AccessToken accessToken) {
        accessTokenString = accessToken.getToken();

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
        setGoToMainAppButtonState(View.VISIBLE);

        // TODO Update PhotoApp Application -> API BROKER AND STUFF

        initImageLoader(getApplicationContext(), accessTokenString);
    }

    public void launchMainPhotoAppActivityButton (View view) {
        AccessToken currentAccessToken = AccessToken.getCurrentAccessToken();
        if (currentAccessToken != null && !currentAccessToken.isExpired()) {
            launchMainPhotoAppActivity();
        } else {
            // This should never happen
            PaLog.error("Button clicked w/o access token. This should never have happened");
            setGoToMainAppButtonState(View.INVISIBLE);
            // reload the view?
        }
    }

    private void launchMainPhotoAppActivity() {
        PaLog.info("Launching Main PhotoApp Activity");

        //
        Intent i = new Intent(this, AlbumActivity.class);
        startActivity(i);

        // Finish the current activity
        finish();
    }


    /**
     * We need to wait until the user is logged in because we need to set the Facebook Header
     * @param context Application Context
     * @param facebookAccessToken Facebook Access Token
     */
    private void initImageLoader(Context context, String facebookAccessToken) {
        Map<String, String> headers = new HashMap<>();
        headers.put("X-Facebook-Token", facebookAccessToken);
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_contact_picture) // resource or drawable
                .showImageForEmptyUri(R.drawable.ic_menu_help) // resource or drawable
                .showImageOnFail(R.drawable.ic_delete) // resource or drawable
                .resetViewBeforeLoading(true)  // default
                .delayBeforeLoading(10)
                .cacheInMemory(true) // default
                .cacheOnDisk(true) // default
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default
                .bitmapConfig(Bitmap.Config.ARGB_8888) // default
                .extraForDownloader(headers)

                .build();

        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.imageDownloader(new CustomImageDownloader(context));
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.defaultDisplayImageOptions(options);

        //config.writeDebugLogs(); // Remove for release app

        ImageLoader.getInstance().init(config.build());
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
        return super.onOptionsItemSelected(item);
    }

}
