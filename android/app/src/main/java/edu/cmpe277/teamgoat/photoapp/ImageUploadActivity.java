package edu.cmpe277.teamgoat.photoapp;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mashape.unirest.http.exceptions.UnirestException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import edu.cmpe277.teamgoat.photoapp.model.Album;
import edu.cmpe277.teamgoat.photoapp.model.ApiBroker;

public class ImageUploadActivity extends Activity {
    private static int RESULT_LOAD_IMG = 1;
    private Button chooseFileButton;
    private Button uploadButton;
    private TextView fileNameDisplay;
    private EditText imageDescription;
    private Uri selectedImage;
    private PhotoApp photoApp;
    private ApiBroker apiBroker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_upload_image);

        photoApp = (PhotoApp) getApplication();
        apiBroker = photoApp.getApiBroker();

        imageDescription = (EditText) findViewById(R.id.image_upload_description);
        chooseFileButton = (Button) findViewById(R.id.choose_image_button);
        uploadButton = (Button) findViewById(R.id.upload_image);
        fileNameDisplay = (TextView) findViewById(R.id.image_filename);
        uploadButton.setEnabled(selectedImage != null);

        chooseFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent, "Select Picture"), RESULT_LOAD_IMG);
            }
        });

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedImage == null) {
                    Toast.makeText(ImageUploadActivity.this, "omfg you didn't pick an image", Toast.LENGTH_LONG).show();
                    return;
                }

                try {
                    uploadImage();
                } catch (IOException e) {
                    Toast.makeText(ImageUploadActivity.this, "Failed to upload image", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG) {
                if (resultCode == RESULT_OK && null != data) {
                    selectedImage = data.getData();
                    if (selectedImage != null && selectedImage.getPath() != null) {
                        // Show the base file name instead of the entire path
                        String path = selectedImage.getPath();
                        String[] parts = path.split("/");
                        fileNameDisplay.setText(parts[parts.length - 1]);
                    } else {
                        fileNameDisplay.setText("");
                    }
                } else {
                    Toast.makeText(this, "omfg you didn't pick an image", Toast.LENGTH_LONG).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        uploadButton.setEnabled(selectedImage != null);
    }

    private void uploadImage() throws IOException {
        final Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
        final Album currentAlbum = PhotoAlbums.albumUserMostRecentlyClicked;
        Toast.makeText(this, "Uploading Image", Toast.LENGTH_SHORT).show();
        final Context context = getApplicationContext();

        new AsyncTask<Void, Void, Void>() {
            protected Void doInBackground(Void... params) {
                try {

                    String[] parts = selectedImage.getPath().split("\\.");
                    String fileExtension = parts[parts.length - 1];

                    //create a file to write bitmap data
                    File f = new File(context.getCacheDir(), "fileupload-" + System.currentTimeMillis() + "." + fileExtension);
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

                    apiBroker.uploadImage(currentAlbum, f, "Image", imageDescription.getText().toString(), 0.0, 0.0);
                } catch (IOException |UnirestException e) {
                    Log.d("main", "failed to upload image", e);
                }
                return null;
            }

            protected void onPostExecute() {
                informUserUploadCompleteAndInviteMoreUploads(context);
            }
        }.execute();
    }

    private void informUserUploadCompleteAndInviteMoreUploads(Context context) {
        Toast.makeText(context, "Image Uploaded. Upload another, or press the back button.", Toast.LENGTH_SHORT).show();
        imageDescription.setText("");
        uploadButton.setEnabled(false);
        fileNameDisplay.setText("");
    }
}
