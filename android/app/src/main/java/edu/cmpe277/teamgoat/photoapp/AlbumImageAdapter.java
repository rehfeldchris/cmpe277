package edu.cmpe277.teamgoat.photoapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Carita on 4/29/2015.
 */
public class AlbumImageAdapter extends BaseAdapter {
    private Context mContext;
    private List<AlbumCover> albumCovers = new ArrayList<AlbumCover>();
    private LayoutInflater inflater;

    public AlbumImageAdapter(Context c) {
        mContext = c;
        inflater = LayoutInflater.from(mContext);

        getAlbums();
    }

    public int getCount() {
        return albumCovers.size();
    }

    public Object getItem(int position) {
        return albumCovers.get(position);
    }

    public long getItemId(int position) {
        return albumCovers.get(position).id;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        TextView tag;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.square_image, parent, false);
            convertView.setTag(R.id.picture, convertView.findViewById(R.id.picture));
            convertView.setTag(R.id.photo_caption, convertView.findViewById(R.id.photo_caption));
            // if it's not recycled, initialize some attributes
//            imageView = new ImageView(mContext);
//            imageView.setLayoutParams(new GridView.LayoutParams(100, 100));
//            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//            imageView.setPadding(3, 3, 3, 3);
//        } else {
//            imageView = (ImageView) convertView;
        }

        imageView = (ImageView) convertView.getTag(R.id.picture);
        tag = (TextView) convertView.getTag(R.id.photo_caption);
        AlbumCover album = (AlbumCover) getItem(position);
        imageView.setImageResource(album.id);
        tag.setText(album.text);

//        if(imageView != null) {
//            ((BitmapDrawable)imageView.getDrawable()).getBitmap().recycle();
//            imageView.setImageResource(albumCovers[position]);
//        }
//        else {
//            imageView.setImageResource(albumCovers[position]);
//        }
//        imageView.setImageResource(albumCovers[position]);
        return convertView;
    }

    private void getAlbums() {
//        String apiUrl = R.string.api_host + "/albums";
//        HttpClient httpclient = new DefaultHttpClient();
//        HttpGet httpget = new HttpGet(apiUrl);
//        httpget.addHeader("X-Facebook-Token", LolGlobalVariables.facebookAccessToken);
//
//        try {
//            HttpResponse response = httpclient.execute(httpget);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        albumCovers.add(new AlbumCover("Album 1", R.drawable.albumcovertrivia));
        albumCovers.add(new AlbumCover("Album 2", R.drawable.music));
        albumCovers.add(new AlbumCover("Album 3", R.drawable.tree));
    }

    private class AlbumCover {
        final String text;
        final int id;

        AlbumCover(String text, int id) {
            this.text = text;
            this.id = id;
        }
    }
}