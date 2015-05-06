package edu.cmpe277.teamgoat.photoapp;

import android.content.Intent;
import android.os.AsyncTask;
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
import java.util.ArrayList;
import java.util.List;

import edu.cmpe277.teamgoat.photoapp.model.Album;
import edu.cmpe277.teamgoat.photoapp.model.ApiBroker;


public class PhotoAlbums extends ActionBarActivity {

    private List<Album> viewableAlbums;
    private GridView gridView;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_albums);

        gridView = (GridView) findViewById(R.id.albums_grid);
        loadAlbumsThenSetAdapter();

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                if (position == 0) { //new album
//                    Intent i = new Intent(PhotoAlbums.this, EditAlbumFragment.class);
//                    startActivity(i);

//                    if ( savedInstanceState == null) {
//                        getFragmentManager()
//                                .beginTransaction()
//                                .add(R.id.fragment_create_album, new EditAlbumFragment())
//                                .commit();
//                    }
                } else { //go to album
                    // todo send the album over to the view so it can list the images.
                    Intent i = new Intent(PhotoAlbums.this, AlbumViewerActivity.class);
                    startActivity(i);
                }
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
                    gridView.setAdapter(new AlbumImageAdapter(PhotoAlbums.this, albums));
                }
            }
        }.execute();
    }
}
