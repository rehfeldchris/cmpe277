package edu.cmpe277.teamgoat.photoapp;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;


public class PhotoAlbums extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)     {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_albums);

        GridView view = (GridView) findViewById(R.id.albums_grid);
        view.setAdapter(new AlbumImageAdapter(this));

//        Button addAlbum = new Button(this);
//        addAlbum.setText("Add New Album");
//        view.addView(addAlbum);
        view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                //go to album
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
