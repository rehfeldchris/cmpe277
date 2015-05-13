package edu.cmpe277.teamgoat.photoapp;

import android.location.Location;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mashape.unirest.http.exceptions.UnirestException;

import java.io.IOException;
import java.util.List;

import edu.cmpe277.teamgoat.photoapp.model.ApiBroker;
import edu.cmpe277.teamgoat.photoapp.model.Image;
import edu.cmpe277.teamgoat.photoapp.util.PaLog;


public class ImageSearchActivity extends ActionBarActivity {

    private PhotoApp photoApp;
    private ApiBroker apiBroker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_search);
        photoApp = (PhotoApp) getApplication();
        apiBroker = photoApp.getApiBroker();

        Button useCurrentLocationButton = (Button) findViewById(R.id.button_use_current_location);
        final EditText keyword = (EditText) findViewById(R.id.keyword_search);
        final EditText lat = (EditText) findViewById(R.id.edit_txt_lat);
        final EditText lon = (EditText) findViewById(R.id.edit_txt_lon);
        final EditText distance = (EditText) findViewById(R.id.edit_txt_dis);
        useCurrentLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Location location = photoApp.getLocationManager().getLastKnownLocationFromService();
                    lat.setText(String.format("%.3f", location.getLatitude()));
                    lon.setText(String.format("%.3f", location.getLongitude()));
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Error getting your location", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            }
        });

        Button searchButton = (Button) findViewById(R.id.btn_image_search);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Keyboard Search
                Editable keywordEdit = keyword.getText();
                String keywordSearch = keyword == null ? null : keywordEdit.toString();
                keywordSearch = keywordSearch == null || keywordSearch.trim().isEmpty() ? null : keywordSearch.trim();

                // Latitude
                Double locationLat = null;
                try {
                    locationLat = Double.parseDouble(lat.getText().toString());
                } catch (NumberFormatException e) {
                    locationLat = null;
                }

                // Longitude
                Double locationLon = null;
                try {
                    locationLon = Double.parseDouble(lon.getText().toString());
                } catch (NumberFormatException e) {
                    locationLon = null;
                }

                // Meters
                Double miles = null;
                try {
                    miles = Double.parseDouble(distance.getText().toString());
                } catch (NumberFormatException e) {
                    miles = null;
                }

                // Now call api
                handleSearch(keywordSearch, locationLat, locationLon, miles);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_image_search, menu);
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


    private void handleSearch(final String keyword, final Double lat, final Double lon, final Double dist) {
        new AsyncTask<Void, Void, List<Image>>() {

            @Override
            protected List<Image> doInBackground(Void... params) {
                try {
                    List<Image> images = apiBroker.findViewableImagesWithCriteriaInMiles(lat, lon, dist, keyword);

                    return images;
                } catch (UnirestException | IOException e) {
                    PaLog.error(String.format("There was an error fetching images via search: lat: '%s', long: '%s', dist: '%s', keyword: '%s', msg class: '%s', Msg: '%s'.", lat, lon, dist, keyword, e.getClass().toString(), e.getMessage()), e);
                }
                return null;
            }

            @Override
            protected void onPostExecute(List<Image> images) {
                super.onPostExecute(images);
                PaLog.info(String.format("Finished Searching Images. Size of images: '%s'.", (images == null) ? null : images.size()));
            }
        }.execute();
    }
}
