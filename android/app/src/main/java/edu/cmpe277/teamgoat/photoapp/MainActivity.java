package edu.cmpe277.teamgoat.photoapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

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

        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);

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
            launchMainPhotoAppActivity();
        }
    }

    private void launchMainPhotoAppActivity() {
        // THIS IS A TEST CALL
        // WE NEED TO UPDATE THIS TO THE CORRECT LAYOUT/VIEW
        // NOTE: This function call causes a bug: missing layout files
//            Intent i = new Intent(this, LayoutTest.class);
//            startActivity(i);
        startActivity(new Intent(this, PhotoAlbums.class));
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
}
