package edu.cmpe277.teamgoat.photoapp;

import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.facebook.share.model.AppInviteContent;
import com.facebook.share.widget.AppInviteDialog;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.cmpe277.teamgoat.photoapp.model.Album;
import edu.cmpe277.teamgoat.photoapp.model.ApiBroker;


public class PhotoAlbums extends ActionBarActivity {

    private List<Album> viewableAlbums;
    private GridView gridView;

    // public static so that other activity can easily access to determine which album to display.
    public static Album albumUserMostRecentlyClicked;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_albums);

        gridView = (GridView) findViewById(R.id.albums_grid);
        loadAlbumsThenSetAdapter();

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                albumUserMostRecentlyClicked = viewableAlbums.get(position);
                Intent i = new Intent(PhotoAlbums.this, AlbumViewerActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_photo_albums, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //Create new album
        if (id == R.id.action_create_album) {
            FrameLayout frame = new FrameLayout(this);
            frame.setId(R.id.fragment_create_album);
            setContentView(frame, new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
            Fragment frag = getFragmentManager().findFragmentById(R.id.fragment_create_album);
            if (frag == null) {
                getFragmentManager().beginTransaction()
                        .add(R.id.fragment_create_album, new EditAlbumFragment())
                        .commit();
            }
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_invite_friends) {
            String appLinkUrl, previewImageUrl;

            appLinkUrl = "@string/app_url";
            previewImageUrl = "@string/preview_image_url";

            if (AppInviteDialog.canShow()) {
                AppInviteContent content = new AppInviteContent.Builder()
                        .setApplinkUrl(appLinkUrl)
                        .setPreviewImageUrl(previewImageUrl)
                        .build();
                AppInviteDialog.show(PhotoAlbums.this, content);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadAlbumsThenSetAdapter() {
        new AsyncTask<Void, Void, List<Album>>() {
            protected List<Album> doInBackground(Void... params) {
                try {
                    return ApiBroker.singleton().getViewableAlbums();
                } catch (IOException|UnirestException  e) {
                    Log.d("main", "failed to load album list", e);
                    return null;
                }
            }

            protected void onPostExecute(List<Album> albums) {
                viewableAlbums = albums;
                if (albums == null) {
                    Toast.makeText(PhotoAlbums.this, "Couldn't load album list. Sorry.", Toast.LENGTH_SHORT).show();
                } else {
                    //gridView.invalidateViews();
                    AlbumImageAdapter adapter = new AlbumImageAdapter(PhotoAlbums.this, albums);
                    gridView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }
        }.execute();
    }
}
