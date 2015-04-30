package edu.cmpe277.teamgoat.photoapp;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;

/**
 * Created by Carita on 4/29/2015.
 */
public class AlbumImageAdapter extends BaseAdapter {
    private Context mContext;

    public AlbumImageAdapter(Context c) {
        mContext = c;
    }

    public int getCount() {
        return albumCovers.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(5, 5, 5, 5);
        } else {
            imageView = (ImageView) convertView;
        }

//        if(imageView != null) {
//            ((BitmapDrawable)imageView.getDrawable()).getBitmap().recycle();
//            imageView.setImageResource(albumCovers[position]);
//        }
//        else {
//            imageView.setImageResource(albumCovers[position]);
//        }
        imageView.setImageResource(albumCovers[position]);
        return imageView;
    }

    private void getAlbums() {
        String apiUrl = R.string.api_host + "/albums";
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet(apiUrl);
        httpget.addHeader("X-Facebook-Token", LolGlobalVariables.facebookAccessToken);

        try {
            HttpResponse response = httpclient.execute(httpget);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // references to test images
    private Integer[] albumCovers = {
            R.drawable.albumcovertrivia, R.drawable.music
//            R.drawable.sample_2, R.drawable.sample_3,
//            R.drawable.sample_4, R.drawable.sample_5,
//            R.drawable.sample_6, R.drawable.sample_7
    };
}