package edu.cmpe277.teamgoat.photoapp;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.mashape.unirest.http.exceptions.UnirestException;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.IOException;
import java.util.List;

import edu.cmpe277.teamgoat.photoapp.model.ApiBroker;
import edu.cmpe277.teamgoat.photoapp.model.Comment;
import edu.cmpe277.teamgoat.photoapp.model.Friend;
import edu.cmpe277.teamgoat.photoapp.model.Image;


public class SingleImageViewActivity extends ActionBarActivity {

    public static final String IMAGE_TAG = "SingleImage";
    private Image imageBeingDisplayed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_image_view);
        imageBeingDisplayed = ImagesGridviewFragment.imageMostRecentlyClicked;

        ImageView img = (ImageView)findViewById(R.id.img_singleImage_view);

        if (imageBeingDisplayed != null) {
            ImageLoader.getInstance().displayImage(
                ApiBroker.singleton().getUrlForImage(imageBeingDisplayed),
                img
            );
        } else {
            img.setImageResource(R.drawable.ic_delete);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_single_image_view, menu);
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


    private void addComment(final String comment) {
        new AsyncTask<Void, Void, Comment>() {
            protected Comment doInBackground(Void... params) {
                try {
                    return ApiBroker.singleton().commentOnImage(imageBeingDisplayed, comment);
                } catch (IOException |UnirestException e) {
                    Log.d("main", "failed to load friend list", e);
                    return null;
                }
            }

            protected void onPostExecute(Comment comment) {
                if (comment == null) {
                    Toast.makeText(getApplicationContext(), "Couldn't add comment. Sorry. We suxorz..", Toast.LENGTH_SHORT).show();
                } else {
                    // Maybe reload data somehow to make the comment show up? or just add the comment manually.
                    Toast.makeText(getApplicationContext(), "Comment added", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }
}
