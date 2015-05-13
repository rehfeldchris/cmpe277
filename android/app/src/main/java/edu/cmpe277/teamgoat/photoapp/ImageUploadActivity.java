package edu.cmpe277.teamgoat.photoapp;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import edu.cmpe277.teamgoat.photoapp.model.Album;
import edu.cmpe277.teamgoat.photoapp.model.ApiBroker;
import edu.cmpe277.teamgoat.photoapp.model.Image;

public class ImageUploadActivity extends Activity {
    private static final int RESULT_LOAD_IMG = 1;
    private static final int REQUEST_TAKE_PHOTO = 2;
    private Button chooseFileButton;
    private Button takePictureButton;
    private Button uploadButton;
    private TextView fileNameDisplay;
    private EditText imageDescription;
    private Uri selectedImage;
    private PhotoApp photoApp;
    private ApiBroker apiBroker;
    private boolean imageIsFromCamera = false;
    private File imageFromCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_upload_image);

        photoApp = (PhotoApp) getApplication();
        apiBroker = photoApp.getApiBroker();
        photoApp.getLocationManager().startLocationListener();

        imageDescription = (EditText) findViewById(R.id.image_upload_description);
        chooseFileButton = (Button) findViewById(R.id.choose_image_button);
        takePictureButton = (Button) findViewById(R.id.take_picture_button);
        uploadButton = (Button) findViewById(R.id.upload_image);
        fileNameDisplay = (TextView) findViewById(R.id.image_filename);
        uploadButton.setEnabled(selectedImage != null);

        chooseFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageIsFromCamera = false;
                uploadButton.setEnabled(false);
                fileNameDisplay.setText("");

                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent, "Select Picture"), RESULT_LOAD_IMG);
            }
        });

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (imageIsFromCamera) {
                        uploadImageTakenFromCamera();
                    } else {
                        uploadImageTakenFromDisk();
                    }
                } catch (IOException e) {
                    Toast.makeText(ImageUploadActivity.this, "Failed to upload image", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });

        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageIsFromCamera = true;
                uploadButton.setEnabled(false);
                fileNameDisplay.setText("");
                dispatchTakePictureIntent();
            }
        });

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            Toast.makeText(this, "omfg you didn't pick an image", Toast.LENGTH_LONG).show();
            uploadButton.setEnabled(false);
            fileNameDisplay.setText("");
            selectedImage = null;
            return;
        }

        try {
            if (requestCode == RESULT_LOAD_IMG) {
                selectedImage = data.getData();
                if (selectedImage != null && selectedImage.getPath() != null) {
                    // Show the base file name instead of the entire path
                    String path = selectedImage.getPath();
                    String[] parts = path.split("/");
                    fileNameDisplay.setText(parts[parts.length - 1]);
                    uploadButton.setEnabled(true);
                }
            } else if (requestCode == REQUEST_TAKE_PHOTO) {
                fileNameDisplay.setText("Picture from camera");
                uploadButton.setEnabled(true);
            }
        } catch (Exception e) {
            Toast.makeText(this, "error", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }



    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }

        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        return image;
    }

    private void copyInputStreamToFile(InputStream in, File file) {
        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while((len = in.read(buf)) > 0){
                out.write(buf, 0, len);
            }
            out.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void uploadImageTakenFromDisk() throws IOException {
        final Album currentAlbum = PhotoAlbums.albumUserMostRecentlyClicked;
        Toast.makeText(this, "Uploading Image", Toast.LENGTH_SHORT).show();
        final Context context = getApplicationContext();
        InputStream is = getContentResolver().openInputStream(selectedImage);
        final File imageFile = createImageFile();
        copyInputStreamToFile(is, imageFile);

        new AsyncTask<Void, Void, Image>() {
            protected Image doInBackground(Void... params) {
                try {
                    Double[] latLon = getLatLon(imageFile, false);
                    apiBroker.uploadImage(currentAlbum, imageFile, "Image", imageDescription.getText().toString(), latLon[0], latLon[1]);
                } catch (IOException |UnirestException e) {
                    Log.d("main", "failed to upload image", e);
                }
                return null;
            }

            protected void onPostExecute(Image image) {
                if (image == null || image.get_ID() == null) {
                    informUserUploadCompleteAndInviteMoreUploads(context);
                    currentAlbum.getImages().add(image);
                    AlbumViewerActivity.imageAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(context, "Image Upload failed.", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    private void uploadImageTakenFromCamera() throws IOException {
        final Album currentAlbum = PhotoAlbums.albumUserMostRecentlyClicked;
        Toast.makeText(this, "Uploading Image", Toast.LENGTH_SHORT).show();
        final Context context = getApplicationContext();

        new AsyncTask<Void, Void, Image>() {
            protected Image doInBackground(Void... params) {
                try {
                    Double[] latLon = getLatLon(imageFromCamera, true);
                    return apiBroker.uploadImage(currentAlbum, imageFromCamera, "Image", imageDescription.getText().toString(), latLon[0], latLon[1]);
                } catch (IOException |UnirestException e) {
                    Log.d("main", "failed to upload image", e);
                }
                return null;
            }

            protected void onPostExecute(Image image) {
                if (image == null || image.get_ID() == null) {
                    informUserUploadCompleteAndInviteMoreUploads(context);
                    currentAlbum.getImages().add(image);
                    AlbumViewerActivity.imageAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(context, "Image Upload failed.", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    private void informUserUploadCompleteAndInviteMoreUploads(Context context) {
        Toast.makeText(context, "Image Uploaded. Upload another, or press the back button.", Toast.LENGTH_SHORT).show();
        imageDescription.setText("");
        uploadButton.setEnabled(false);
        fileNameDisplay.setText("");
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            imageFromCamera = null;
            try {
                imageFromCamera = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            }
            // Continue only if the File was successfully created
            if (imageFromCamera != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFromCamera));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private Double[] getLatLon(File imageFile, boolean addCoordsIfNeeded) {
        try {
            ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
            float[] latLon = new float[2];
            if (exif.getLatLong(latLon)) {
                return new Double[]{(double) latLon[0], (double) latLon[1]};
            } else if (addCoordsIfNeeded) {
                Location location = photoApp.getLocationManager().getLastKnownLocationFromService();
                if (location != null) {
                    return new Double[]{location.getLatitude(), location.getLongitude()};
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new Double[]{null, null};
    }
}
