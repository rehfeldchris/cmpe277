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
import android.widget.Toast;

import com.facebook.AccessToken;
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
import edu.cmpe277.teamgoat.photoapp.util.IDs;
import edu.cmpe277.teamgoat.photoapp.util.PhotoAppLog;

public class MainActivity extends Activity {

    // Facebook instance variables
    CallbackManager callbackManager;
    LoginManager loginManager;
    private AccessToken accessToken;
    private String accessTokenString;

    private PhotoApp photoApp;
    private PhotoAppLog logger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        Intent i = new Intent(this, LayoutTest.class);
//        startActivity(i);

//        startActivity(new Intent(this, PhotoAlbums.class));




        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);

        Uri targetUrl =
                AppLinks.getTargetUrlFromInboundIntent(this, getIntent());
        if (targetUrl != null) {
            Log.i("Activity", "App Link Target URL: " + targetUrl.toString());
        } else {
            AppLinkData.fetchDeferredAppLinkData(
                    this,
//                    activity,
                    new AppLinkData.CompletionHandler() {
                        @Override
                        public void onDeferredAppLinkDataFetched(AppLinkData appLinkData) {
                            //process applink data
                        }
                    });
        }

        boolean forceShowLoginScreen = false;
        Intent launchIntent = getIntent();
        if (launchIntent != null ) {
            Bundle launchBundle = launchIntent.getExtras();
            forceShowLoginScreen = launchBundle != null && launchBundle.getBoolean(IDs.INTENT_LAUNCH_LOGIN_VIEW_FORCE_VIEW_PARAMETER_KEY, false);
        }

        photoApp = (PhotoApp) getApplication();
        logger = photoApp.getMasterLogger();

        // Get the current access token, if it's null or expired, we will show the login view, else direct to the main app
        // NOTE: To show the logout screen, launch this activity with the force intent key set using key INTENT_LAUNCH_LOGIN_VIEW_FORCE_VIEW_PARAMETER_KEY
        AccessToken currentAccessToken = AccessToken.getCurrentAccessToken();
        if (forceShowLoginScreen || currentAccessToken == null || currentAccessToken.isExpired()) {
            callbackManager = CallbackManager.Factory.create();
            loginManager = LoginManager.getInstance();

            LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
            loginButton.setReadPermissions(IDs.FACEBOOK_LOGIN_PERMISSIONS);
            loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    // App code
                    accessToken = loginResult.getAccessToken();
                    accessTokenString = accessToken.getToken();
                    LolGlobalVariables.facebookAccessToken = accessTokenString;
                    initImageLoader(getApplicationContext());
                    Toast.makeText(getApplicationContext(), R.string.facebook_login_successful, Toast.LENGTH_SHORT).show();

                    // Debug
                    logger.info(String.format("\n=================\n" +
                                    "Application Id: '%s'\n" +
                                    "UserId: '%s'\n" +
                                    "Access Token: '%s'\n" +
                                    "Token: '%s'\n" +
                                    "Expiration: '%s'\n" +
                                    "Last Refresh: '%s'\n=================\n",
                            accessToken.getApplicationId(),
                            accessToken.getUserId(),
                            accessTokenString,
                            accessToken.getToken(),
                            accessToken.getExpires(),
                            accessToken.getLastRefresh()));
                    launchMainPhotoAppActivity();
                }

                @Override
                public void onCancel() {
                    Toast.makeText(getApplicationContext(), R.string.facebook_login_cancelled, Toast.LENGTH_SHORT).show();
                    logger.info("User cancelled Facebook Authentication.");
                }

                @Override
                public void onError(FacebookException exception) {
                    Toast.makeText(getApplicationContext(), R.string.facebook_login_failed, Toast.LENGTH_SHORT).show();
                    logger.error("Facebook authentication failed, Msg: " + exception.getMessage(), exception);
                }
            });
        } else {
            LolGlobalVariables.facebookAccessToken = AccessToken.getCurrentAccessToken().getToken();
            initImageLoader(getApplicationContext());
            launchMainPhotoAppActivity();
        }
    }

    private void launchMainPhotoAppActivity() {
        // THIS IS A TEST CALL
        // WE NEED TO UPDATE THIS TO THE CORRECT LAYOUT/VIEW
        // NOTE: This function call causes a bug: missing layout files
//        Intent i = new Intent(this, LayoutTest.class);
//        startActivity(i);

        // App Test - Thong
//        Intent i = new Intent(this, AlbumViewerActivity.class);
//        startActivity(i);
        // End Test

        startActivity(new Intent(this, PhotoAlbums.class));
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Logs 'install' and 'app activate' App Events.
//        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Logs 'app deactivate' App Event.
//        AppEventsLogger.deactivateApp(this);
    }

//    @Override
//    public void onDestroy() {
//        System.gc();
//        super.onDestroy();
//    }

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


    public static void initImageLoader(Context context) {
        Map<String, String> headers = new HashMap<>();
        headers.put("X-Facebook-Token", LolGlobalVariables.facebookAccessToken);
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_contact_picture) // resource or drawable
                .showImageForEmptyUri(R.drawable.ic_menu_help) // resource or drawable
                .showImageOnFail(R.drawable.ic_delete) // resource or drawable
                .resetViewBeforeLoading(!false)  // default
                .delayBeforeLoading(10)
                .cacheInMemory(true) // default
                .cacheOnDisk(true) // default
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default
                .bitmapConfig(Bitmap.Config.ARGB_8888) // default
                .extraForDownloader(headers)
                .build();

        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
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
