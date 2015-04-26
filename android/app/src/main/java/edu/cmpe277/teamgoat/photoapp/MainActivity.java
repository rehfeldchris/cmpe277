package edu.cmpe277.teamgoat.photoapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
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

import java.lang.reflect.Array;
import java.util.List;

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
    private SharedPreferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);

        /*
        * Test Layout: for testing for the layout only, comment out this part when
        * testing other app's functions.
        * */
//
//            Intent i = new Intent(this, LayoutTest.class);
//            startActivity(i);
//
        /* ****** end of testing layout ****** */


        photoApp = (PhotoApp) getApplication();
        logger = photoApp.getMasterLogger();
        preferences = photoApp.getMasterPreferences();

        // TODO handle login logic when we already have a token
        // TODO handle login logic when user clicks on the logout button

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
                Toast.makeText(getApplicationContext(), R.string.facebook_login_successful, Toast.LENGTH_SHORT).show();
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
