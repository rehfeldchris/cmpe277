package edu.cmpe277.teamgoat.photoapp;

import android.location.Location;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class ImageSearchActivity extends ActionBarActivity {

    private PhotoApp photoApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_search);
        photoApp = (PhotoApp) getApplication();

        Button useCurrentLocationButton = (Button) findViewById(R.id.button_use_current_location);
        final EditText distance = (EditText) findViewById(R.id.edit_txt_dis);
        useCurrentLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Location location = photoApp.getLocationManager().getLastKnownLocationFromService();
                EditText lat = (EditText) findViewById(R.id.edit_txt_lat);
                lat.setText(String.format("%.3f", location.getLatitude()));

                EditText lon = (EditText) findViewById(R.id.edit_txt_lon);
                lon.setText(String.format("%.3f", location.getLongitude()));
            }
        });

        Button searchButton = (Button) findViewById(R.id.btn_image_search);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Double meters = null;
                try {
                    meters = Double.parseDouble(distance.getText().toString());
                    // Convert miles to meters.
                    meters *= 1609;
                } catch (NumberFormatException e) {
                    meters = null;
                }

                // Now call api
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
}
