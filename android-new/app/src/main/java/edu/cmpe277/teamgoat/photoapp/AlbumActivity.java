package edu.cmpe277.teamgoat.photoapp;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.facebook.share.model.AppInviteContent;
import com.facebook.share.widget.AppInviteDialog;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.io.IOException;
import java.util.List;

import edu.cmpe277.teamgoat.photoapp.adapters.AlbumImageAdapter;
import edu.cmpe277.teamgoat.photoapp.model.Album;
import edu.cmpe277.teamgoat.photoapp.model.ApiBroker;
import edu.cmpe277.teamgoat.photoapp.util.IDs;
import edu.cmpe277.teamgoat.photoapp.util.PaLog;


public class AlbumActivity extends ActionBarActivity {

    private PhotoApp photoApp;
    private ApiBroker apiBroker;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private GridView mGridView;

    private List<Album> viewableAlbums;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        photoApp = (PhotoApp) getApplication();
        apiBroker = photoApp.getApiBroker();

        setContentView(R.layout.activity_album);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.album_swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadAlbums();
            }
        });
        // Adds color to the refresh
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


        mGridView = (GridView) findViewById(R.id.albums_grid);
        // Launch an Album
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                handleAlbumSelection(position);
            }
        });

        // Delete an album
        mGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                 @Override
                 public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                     handleAlbumDeleteSelect(position);
                     return true;
                 }
             }
        );

        loadAlbums();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_album, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // Facebook App Invite
        if (id == R.id.action_invite_friends) {
            if (AppInviteDialog.canShow()) {
                AppInviteContent content = new AppInviteContent.Builder()
                        .setApplinkUrl("@string/facebook_app_url")
                        .setPreviewImageUrl("@string/facebook_app_invite_image_url")
                        .build();
                AppInviteDialog.show(this, content);
            }
        }

        // Launch the Login Activity and force it to stay
        if (id == R.id.album_action_login_screen) {
            Intent intent = new Intent(this, LoginActivity.class);
            Bundle b = new Bundle();
            b.putBoolean(IDs.INTENT_LAUNCH_LOGIN_VIEW_FORCE_VIEW_PARAMETER_KEY, true);
            intent.putExtras(b);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }



    private void loadAlbums() {
        setRefreshingStateForSwipeView(true);
        final Context applicationContext = getApplicationContext();

        new AsyncTask<Void, Void, List<Album>>() {
            protected List<Album> doInBackground(Void... params) {
                try {
                    return apiBroker.getViewableAlbums();
                } catch (IOException | UnirestException e) {
                    PaLog.error("failed to load album list", e);
                    return null;
                }
            }

            protected void onPostExecute(List<Album> albums) {
                viewableAlbums = albums;
                if (albums == null) {
                    Toast.makeText(applicationContext, "Couldn't load album list. Sorry.", Toast.LENGTH_SHORT).show();
                } else {
                    mGridView.invalidateViews();
                    AlbumImageAdapter adapter = new AlbumImageAdapter(applicationContext, albums);
                    mGridView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }

                setRefreshingStateForSwipeView(false);
            }
        }.execute();
    }

    private void setRefreshingStateForSwipeView(final boolean isRefreshing) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(isRefreshing);
            }
        });
    }


    private void handleAlbumSelection(int position) {

    }

    private void handleAlbumDeleteSelect(int position) {

    }
}
