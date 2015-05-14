package edu.cmpe277.teamgoat.photoapp;

import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mashape.unirest.http.exceptions.UnirestException;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.cmpe277.teamgoat.photoapp.model.ApiBroker;
import edu.cmpe277.teamgoat.photoapp.model.Image;
import edu.cmpe277.teamgoat.photoapp.util.PaLog;


public class ImageSearchActivity extends ActionBarActivity{

    private PhotoApp photoApp;
    private ApiBroker apiBroker;
    private ListView mSearch_container;
    public static Image found_image_clicked;

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

        SearchResultAdapter adapter = new SearchResultAdapter(new ArrayList<Image>());
        mSearch_container = (ListView)findViewById(R.id.list_search_item);
        mSearch_container.setAdapter(adapter);
        mSearch_container.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(ImageSearchActivity.this, SingleImageViewActivity.class);
                        found_image_clicked =
                                (Image)mSearch_container.getAdapter().getItem(position);
                        startActivity(intent);
                    }
                }
        );

    }

//    @Override
//    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        Intent intent = new Intent(this, ImageTabActivity.class);
//        AlbumViewerActivity.imageMostRecentlyClicked =
//                (Image)mSearch_container.getAdapter().getItem(position);
//        startActivity(intent);
//    }

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
                ListView search_container =
                        (ListView)ImageSearchActivity.this.findViewById(R.id.list_search_item);
                SearchResultAdapter resultAdapter = (SearchResultAdapter)search_container.getAdapter();
                resultAdapter.resetAdapter();
                resultAdapter.updateData(images);
            }
        }.execute();
    }

    private class SearchResultAdapter extends ArrayAdapter
    {
        List<Image> images;
        SearchResultAdapter(List<Image> images)
        {
            super(ImageSearchActivity.this, 0, images);
            this.images = images;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            if (convertView == null)
                convertView = getLayoutInflater().inflate(R.layout.search_result_item, parent, false);

            ImageView img = (ImageView) convertView.findViewById(R.id.image_found);
            TextView txt_title = (TextView)convertView.findViewById(R.id.single_image_title);

            try {
                Image image = (Image) getItem(position);
                txt_title.setText(image.getDescription());
                String imgUrl = apiBroker.getUrlForImage(image);
                ImageLoader.getInstance().displayImage(imgUrl, img);
            }
            catch (ArrayIndexOutOfBoundsException e)
            {
                img.setImageResource(R.drawable.ic_delete);
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "No result found.", Toast.LENGTH_SHORT).show();
            }

            return convertView;
        }

        void updateData(List<Image> images) {
            for (Image image : images)
                this.images.add(image);
            notifyDataSetChanged();
        }

        void resetAdapter()
        {
            this.images.clear();
        }
    }
}
