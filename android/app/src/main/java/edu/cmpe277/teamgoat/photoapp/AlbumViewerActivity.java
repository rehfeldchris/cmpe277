package edu.cmpe277.teamgoat.photoapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.mashape.unirest.http.exceptions.UnirestException;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import edu.cmpe277.teamgoat.photoapp.model.Album;
import edu.cmpe277.teamgoat.photoapp.model.ApiBroker;
import edu.cmpe277.teamgoat.photoapp.model.Image;

public class AlbumViewerActivity extends ActionBarActivity {
    private static int RESULT_LOAD_IMG = 1;

    private PhotoApp photoApp;
    private ApiBroker apiBroker;

    GridView mGridView;
    private Album albumCurrentlyBeingViewed;
    public static Image imageMostRecentlyClicked;
    public static int imageMostRecentlyClickedIndex;
    public static ImageAdapter imageAdapter;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_gridview_view_image);

        photoApp = (PhotoApp) getApplication();
        apiBroker = photoApp.getApiBroker();

//        Fragment frag = getSupportFragmentManager().findFragmentById(R.id.layout_fragment_gridview_view_image_container);
//        if (frag == null) {
//            getSupportFragmentManager().beginTransaction()
//                    .add(R.id.layout_fragment_gridview_view_image_container, new ImagesGridviewFragment())
//                    .commit();
//        }
        albumCurrentlyBeingViewed = PhotoAlbums.albumUserMostRecentlyClicked;
        setTitle(albumCurrentlyBeingViewed.getName());
        mGridView = (GridView) findViewById(R.id.grid_images);

        imageAdapter = new ImageAdapter(this);
        mGridView.setAdapter(imageAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                handleItemClick(position);
            }
        });
        mGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                handleItemLongClick(position);
                return true;
            }
        });

        // Pull to refresh
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.images_swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                handleImagesRefresh();
            }
        });
        // Adds color to the refresh
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    private void handleItemClick(int position) {
        imageMostRecentlyClickedIndex = position;
        imageMostRecentlyClicked = albumCurrentlyBeingViewed.getImages().get(position);
        Intent i = new Intent(this, ImageTabActivity.class);
        startActivity(i);
    }

    private void handleItemLongClick(int position) {
        final Album albumToModify = albumCurrentlyBeingViewed;
        final Image imageToDelete = albumToModify.getImages().get(position);

        if (!imageToDelete.getOwnerId().equals(photoApp.getFacebookUserId())) {
            Toast.makeText(this, "You're not the original uploader of this image, so you can't delete it.", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Confirm Delete")
                .setMessage("Are you sure you want to delete this image?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteImage(imageToDelete, albumToModify);

                    }
                })
                .setNegativeButton("No", null)
                .show()
        ;
    }

    private void deleteImage(final Image imageToDelete, final Album albumToModify) {
        new AsyncTask<Void, Void, Boolean>() {
            protected Boolean doInBackground(Void... params) {
                try {
                    return apiBroker.deleteImage(imageToDelete);
                } catch (IOException|UnirestException e) {
                    Log.d("main", "failed to delete image", e);
                    return Boolean.FALSE;
                }
            }

            protected void onPostExecute(Boolean success) {
                if (!success) {
                    Toast.makeText(getApplicationContext(), "Couldn't delete image. Maybe you aren't the owner?", Toast.LENGTH_SHORT).show();
                } else {
                    albumToModify.getImages().remove(imageToDelete);
                    imageAdapter.notifyDataSetChanged();
                    Toast.makeText(getApplicationContext(), "Image deleted", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }


    private void handleImagesRefresh() {
        setRefreshingStateForSwipeView(true);
        Toast.makeText(getApplicationContext(), "TODO Handle Refresh", Toast.LENGTH_SHORT).show();
        // TODO handle refresh --> async task -> get album -> update global variable album --> update adapter

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setRefreshingStateForSwipeView(false);
            }
        }, 5000);
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_album, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.upload_photo) {
            Intent i = new Intent(AlbumViewerActivity.this, ImageUploadActivity.class);
            startActivity(i);

//            Intent galleryIntent = new Intent();
//            galleryIntent.setType("image/*");
//            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
//
//            startActivityForResult(Intent.createChooser(galleryIntent, "Select Picture"), RESULT_LOAD_IMG);
        }

        return super.onOptionsItemSelected(item);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG) {
                if (resultCode == RESULT_OK && null != data) {
                    // Get the Image from data

                    Uri selectedImage = data.getData();
//                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
//
//                    // Get the cursor
//                    Cursor cursor = getContentResolver().query(selectedImage,
//                            filePathColumn, null, null, null);
//
//                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//                    // Move to first row
//                    cursor.moveToFirst();
//                    imagePath = cursor.getString(columnIndex);
//                    cursor.close();
//
//                    String foo = "Path  ->" + BitmapFactory.decodeFile(imagePath).toString();

                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                    Album currentAlbum = PhotoAlbums.albumUserMostRecentlyClicked;
                    Toast.makeText(this, "Uploading Image", Toast.LENGTH_SHORT).show();

                    uploadImage(getApplication(), bitmap, currentAlbum);


                } else {
                    Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_LONG).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void uploadImage(final Context context, final Bitmap bitmap, final Album album) {
        new AsyncTask<Void, Void, Void>() {
            protected Void doInBackground(Void... params) {
                try {

                    //create a file to write bitmap data
                    File f = new File(context.getCacheDir(), "fileupload-" + System.currentTimeMillis());
                    f.createNewFile();

                    //Convert bitmap to byte array
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
                    byte[] bitmapdata = bos.toByteArray();

                    //write the bytes in file
                    FileOutputStream fos = new FileOutputStream(f);
                    fos.write(bitmapdata);
                    fos.flush();
                    fos.close();

                    apiBroker.uploadImage(album, f, "Image", "Photo at" + new Date(), 0.0, 0.0);
                } catch (IOException |UnirestException e) {
                    Log.d("main", "failed to load album list", e);
                }
                return null;
            }

            protected void onPostExecute() {
                Toast.makeText(context, "Image Uploaded.", Toast.LENGTH_SHORT).show();

//                viewableAlbums = albums;
//                if (albums == null) {
//                    Toast.makeText(PhotoAlbums.this, "Couldn't load album list. Sorry.", Toast.LENGTH_SHORT).show();
//                } else {
//                    //gridView.invalidateViews();
//                    AlbumImageAdapter adapter = new AlbumImageAdapter(PhotoAlbums.this, albums);
//                    gridView.setAdapter(adapter);
//                    adapter.notifyDataSetChanged();
//                }
            }
        }.execute();

    }

    class ImageAdapter extends BaseAdapter {
        private Context mContext;
        private LayoutInflater inflater;

        public ImageAdapter(Context c) {
            super();
            mContext = c;
            inflater = LayoutInflater.from(mContext);
        }

        public int getCount() {
            return albumCurrentlyBeingViewed.getImages().size();
        }

        public Object getItem(int position) {
            return albumCurrentlyBeingViewed.getImages().get(position);
        }

        public long getItemId(int position) {
            return albumCurrentlyBeingViewed.getImages().get(position).get_drawable_id();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.photo_image, parent, false);
                convertView.setTag(R.id.picture_photo, convertView.findViewById(R.id.picture_photo));
            }

            imageView = (ImageView) convertView.findViewById(R.id.picture_photo);

            try {
                Image image = albumCurrentlyBeingViewed.getImages().get(position);
                String imageUrl = apiBroker.getUrlForImage(image);
                ImageLoader.getInstance().displayImage(
                        imageUrl,
                        imageView
                );
                //imageView.setImageBitmap(ImageLoader.getInstance().loadImageSync(imageUrl, new ImageSize(50, 50)));
            } catch (ArrayIndexOutOfBoundsException e) {
                e.printStackTrace();
                imageView.setImageResource(R.drawable.ic_delete);
                Toast.makeText(getApplicationContext(), "Couldn't load image.", Toast.LENGTH_SHORT).show();
            }

            return convertView;
        }
    }
}
