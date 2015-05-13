package edu.cmpe277.teamgoat.photoapp;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.Toast;

import com.facebook.share.model.AppInviteContent;
import com.facebook.share.widget.AppInviteDialog;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import edu.cmpe277.teamgoat.photoapp.model.Album;
import edu.cmpe277.teamgoat.photoapp.model.ApiBroker;
import edu.cmpe277.teamgoat.photoapp.util.AppLocationServices;
import edu.cmpe277.teamgoat.photoapp.util.CustomImageDownloader;
import edu.cmpe277.teamgoat.photoapp.util.IDs;
import edu.cmpe277.teamgoat.photoapp.util.PaLog;

public class PhotoAlbums extends ActionBarActivity implements AdapterView.OnItemLongClickListener {

    private PhotoApp photoApp;
    private ApiBroker apiBroker;

    private List<Album> viewableAlbums;
    private GridView gridView;
    private AlbumImageAdapter albumImageAdapter;

    // public static so that other activity can easily access to determine which album to display.
    public static Album albumUserMostRecentlyClicked;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_albums);

        photoApp = (PhotoApp) getApplication();
        apiBroker = photoApp.getApiBroker();
        photoApp.setLocationManager(new AppLocationServices((LocationManager) getSystemService(Context.LOCATION_SERVICE)));

        try {
            Location lastKnownLocation = photoApp.getLocationManager().getLastKnownLocationFromService();
            PaLog.info(String.format("Do we have a location: '%s'", lastKnownLocation != null));
            if (lastKnownLocation != null) {
                PaLog.info(String.format("Lat: '%s', Long: '%s'.", lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()));
            }

        } catch (Exception e) {
            PaLog.error(String.format("Failed to get user location: Msg: '%s'.", e.getMessage()), e);
        }

        initImageLoader(getApplicationContext(), photoApp.getFacebookAccessToken());

        // Pull to refresh
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.album_swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadAlbumsThenSetAdapter();
            }
        });
        // Adds color to the refresh
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        gridView = (GridView) findViewById(R.id.albums_grid);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                albumUserMostRecentlyClicked = viewableAlbums.get(position);
                Intent i = new Intent(PhotoAlbums.this, AlbumViewerActivity.class);
                startActivity(i);
            }
        });

        loadAlbumsThenSetAdapter();

        gridView.setOnItemLongClickListener(this);
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
        if (id == R.id.album_action_create_album) {
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

        //Invite Friends
        if (id == R.id.album_action_invite_friends) {
            if (AppInviteDialog.canShow()) {
                AppInviteContent content = new AppInviteContent.Builder()
                        .setApplinkUrl("@string/facebook_app_url")
                        .setPreviewImageUrl("@string/facebook_app_image_preview_url")
                        .build();
                AppInviteDialog.show(PhotoAlbums.this, content);
            }
        }

        if (id == R.id.album_action_login_screen) {
            Intent intent = new Intent(this, MainActivity.class);
            Bundle b = new Bundle();
            b.putBoolean(IDs.INTENT_LAUNCH_LOGIN_VIEW_FORCE_VIEW_PARAMETER_KEY, true);
            intent.putExtras(b);
            startActivity(intent);
            finish();
        }

        // ID = search for image
//        if (id == R.id.toogle_search)
//        {
//            onSearchRequested();
//        }

        if (id == R.id.toogle_search_2)
        {
            Intent intent = new Intent(this, ImageSearchActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        final Album albumToDelete = viewableAlbums.get(position);

        if (!albumToDelete.getOwnerId().equals(photoApp.getFacebookUserId())) {
            Toast.makeText(this, "You're not the original creator of this album, so you can't delete it.", Toast.LENGTH_SHORT).show();
            return true;
        }

        new AlertDialog.Builder(this)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle("Confirm Delete")
            .setMessage("Are you sure you want to delete this album, and all the images and comments within it?")
            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    deleteAlbum(albumToDelete);
                }
            })
            .setNegativeButton("No", null)
            .show()
        ;

        return true;
    }

    private void deleteAlbum(final Album albumToDelete) {
        new AsyncTask<Void, Void, Boolean>() {
            protected Boolean doInBackground(Void... params) {
                try {
                    return apiBroker.deleteAlbum(albumToDelete);
                } catch (IOException|UnirestException e) {
                    Log.d("main", "failed to delete album", e);
                    return Boolean.FALSE;
                }
            }

            protected void onPostExecute(Boolean success) {
                if (!success) {
                    Toast.makeText(PhotoAlbums.this, "Couldn't delete album. Maybe you aren't the owner?", Toast.LENGTH_SHORT).show();
                } else {
                    loadAlbumsThenSetAdapter();
//                    viewableAlbums.remove(albumToDelete);
//                    albumImageAdapter.notifyDataSetChanged();
                    Toast.makeText(PhotoAlbums.this, "Album deleted", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    private void loadAlbumsThenSetAdapter() {
        new AsyncTask<Void, Void, List<Album>>() {
            protected List<Album> doInBackground(Void... params) {
                setRefreshingStateForSwipeView(true);
                try {
                    return apiBroker.getViewableAlbums();
                } catch (IOException|UnirestException  e) {
                    Log.d("main", "failed to load album list", e);
                    return null;
                }
            }

            protected void onPostExecute(List<Album> albums) {
                viewableAlbums = albums;
                photoApp.setUserViewableAlbums(albums);
                if (albums == null) {
                    Toast.makeText(PhotoAlbums.this, "Couldn't load album list. Sorry.", Toast.LENGTH_SHORT).show();
                } else {
                    gridView.invalidateViews();
                    albumImageAdapter = new AlbumImageAdapter(PhotoAlbums.this, albums, apiBroker);
                    gridView.setAdapter(albumImageAdapter);
                    albumImageAdapter.notifyDataSetChanged();
                }
                setRefreshingStateForSwipeView(false);
            }
        }.execute();
    }


    private void setRefreshingStateForSwipeView(final boolean isRefreshing) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mSwipeRefreshLayout != null) {
                    mSwipeRefreshLayout.setRefreshing(isRefreshing);
                }
            }
        });
    }


    public static void initImageLoader(Context context, String facebookAccessToken) {
        Map<String, String> headers = new HashMap<>();
        headers.put("X-Facebook-Token", facebookAccessToken);
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_image_placeholder) // resource or drawable
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
