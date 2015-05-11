package edu.cmpe277.teamgoat.photoapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
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
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.util.HashMap;
import java.util.Map;

import bolts.AppLinks;
import edu.cmpe277.teamgoat.photoapp.util.CustomImageDownloader;
import edu.cmpe277.teamgoat.photoapp.util.IDs;
import edu.cmpe277.teamgoat.photoapp.util.PaLog;

public class MainActivity extends Activity {

    // Facebook instance variables
    private CallbackManager callbackManager;
    private LoginManager loginManager;
    private AccessTokenTracker accessTokenTracker;
    private PhotoApp photoApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        photoApp = (PhotoApp) getApplication();

        // Initialize the Facebook SDK, must be done before the UI is loaded or else it crashes
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);

        // What is this? - Sai
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

//        LolGlobalVariables.facebookAccessToken = accessTokenString;
//        LolGlobalVariables.currentlyLoggedInFacebookUserId = accessToken.getUserId();

        photoApp.setFacebookAccessToken(accessTokenString);
        photoApp.setFacebookUserId(accessToken.getUserId());
        photoApp.forceRecreateApiInstance();

        initImageLoader(getApplicationContext(), accessTokenString);
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

    public static void initImageLoader(Context context, String facebookAccessToken) {
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
}
