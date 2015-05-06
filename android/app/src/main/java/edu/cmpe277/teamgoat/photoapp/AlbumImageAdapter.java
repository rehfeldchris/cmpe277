package edu.cmpe277.teamgoat.photoapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import edu.cmpe277.teamgoat.photoapp.model.Album;

/**
 * Created by Carita on 4/29/2015.
 */
public class AlbumImageAdapter extends BaseAdapter {
    private Context mContext;
    private List<AlbumCover> albumCovers;
    private LayoutInflater inflater;
    private List<Album> albums;

    public AlbumImageAdapter(Context c, List<Album> albums) {
        mContext = c;
        inflater = LayoutInflater.from(mContext);
        this.albums = albums;

        addAlbumButton();
        initAlbumCovers();
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
        AlbumCover albumCover = (AlbumCover) getItem(position);
        if (albumCover.imageUrl != null) {
            ImageLoader.getInstance().displayImage(
                albumCover.imageUrl,
                imageView
            );
        } else {
            imageView.setImageResource(albumCover.id);
        }

        tag.setText(albumCover.text);

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

    public void addAlbumButton() {
        albumCovers.add(new AlbumCover("New Album", R.drawable.plus, null));
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

        albumCovers.add(new AlbumCover("Album 1", R.drawable.albumcovertrivia, null));
        albumCovers.add(new AlbumCover("Album 2", R.drawable.music, null));
        albumCovers.add(new AlbumCover("Album 3", R.drawable.tree, null));
    }

    private void initAlbumCovers() {
        albumCovers  = new ArrayList<AlbumCover>();
        for (Album album : albums) {
            albumCovers.add(new AlbumCover(album.getName(), 0, null));
        }
    }

    private class AlbumCover {
        final String text;
        final int id;
        final String imageUrl;

        AlbumCover(String text, int id, String imageUrl) {
            this.text = text;
            this.id = id;
            this.imageUrl = imageUrl;
        }
    }
}