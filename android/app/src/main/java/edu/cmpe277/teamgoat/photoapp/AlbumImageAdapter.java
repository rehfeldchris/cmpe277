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
import edu.cmpe277.teamgoat.photoapp.model.ApiBroker;

/**
 * Created by Carita on 4/29/2015.
 */
public class AlbumImageAdapter extends BaseAdapter {
    private Context mContext;
    private List<AlbumCover> albumCovers  = new ArrayList<>();
    private LayoutInflater inflater;
    private List<Album> albums;
    private ApiBroker apiBroker;

    public AlbumImageAdapter(Context c, List<Album> albums, ApiBroker apiBroker) {
        mContext = c;
        inflater = LayoutInflater.from(mContext);
        this.albums = albums;
        this.apiBroker = apiBroker;

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
        tag.setCompoundDrawablesWithIntrinsicBounds(0, 0, albumCover.showLockOnAlbum ? R.drawable.ic_album_lock : 0, 0);

        return convertView;
    }

    private void initAlbumCovers() {
        for (Album album : albums) {
            // For now, we just use a placeholder image, we need to create a collage or just use the first image in the album
            int albumCoverPhotoId = R.drawable.ic_image_placeholder;
            String coverPhotoUrl = album.getImages().size() > 0 ? apiBroker.getUrlForImage(album.getImages().get(0)) : null;
            albumCovers.add(new AlbumCover(album.getName(), albumCoverPhotoId, coverPhotoUrl, !album.isPubliclyAccessible()));
        }
    }

    private class AlbumCover {
        final String text;
        final int id;
        final String imageUrl;
        final boolean showLockOnAlbum;

        AlbumCover(String text, int id, String imageUrl, boolean showLockOnAlbum) {
            this.text = text;
            this.id = id;
            this.imageUrl = imageUrl;
            this.showLockOnAlbum = showLockOnAlbum;
        }

    }
}