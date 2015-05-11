package edu.cmpe277.teamgoat.photoapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.mashape.unirest.http.exceptions.UnirestException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import edu.cmpe277.teamgoat.photoapp.model.Album;
import edu.cmpe277.teamgoat.photoapp.model.ApiBroker;

public class AlbumViewerActivity extends ActionBarActivity {
    private static int RESULT_LOAD_IMG = 1;

    private PhotoApp photoApp;
    private ApiBroker apiBroker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_gridview_view_image);

        photoApp = (PhotoApp) getApplication();
        apiBroker = photoApp.getApiBroker();

        Fragment frag = getSupportFragmentManager().findFragmentById(R.id.layout_fragment_gridview_view_image_container);
        if (frag == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.layout_fragment_gridview_view_image_container, new ImagesGridviewFragment())
                    .commit();
        }
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
}
