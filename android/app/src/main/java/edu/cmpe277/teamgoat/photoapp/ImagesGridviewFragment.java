package edu.cmpe277.teamgoat.photoapp;

/**
 * Created by squall on 4/29/15.
 */

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import edu.cmpe277.teamgoat.photoapp.model.Album;
import edu.cmpe277.teamgoat.photoapp.model.ApiBroker;
import edu.cmpe277.teamgoat.photoapp.model.Image;

/**
 * A placeholder fragment containing a simple view.
 */
public class ImagesGridviewFragment extends Fragment implements AdapterView.OnItemClickListener {

    GridView mGridView;
    private Album albumCurrentlyBeingViewed;
    public static Image imageMostRecentlyClicked;

    public ImagesGridviewFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_gridview_view_image, container, false);
        albumCurrentlyBeingViewed = PhotoAlbums.albumUserMostRecentlyClicked;
        mGridView = (GridView) getActivity().findViewById(R.id.grid_images);
        ImageAdapter adapter = new ImageAdapter(getActivity());
        mGridView.setAdapter(adapter);
        mGridView.setOnItemClickListener(this);

        return rootView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        imageMostRecentlyClicked = albumCurrentlyBeingViewed.getImages().get(position);
        Intent i = new Intent(getActivity(), SingleImageViewActivity.class);
        startActivity(i);
    }

    class ImageAdapter extends BaseAdapter {
        private Context mContext;
        private LayoutInflater inflater;

        public ImageAdapter(Context c) {
            super();
            mContext = c;
            inflater = LayoutInflater.from(mContext);
        }

        public int getCount() {
            return albumCurrentlyBeingViewed.getImages().size();
        }

        public Object getItem(int position) {
            return albumCurrentlyBeingViewed.getImages().get(position);
        }

        public long getItemId(int position) {
            return albumCurrentlyBeingViewed.getImages().get(position).get_drawable_id();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.square_image, parent, false);
                convertView.setTag(R.id.picture_photo, convertView.findViewById(R.id.picture));
            }

            imageView = (ImageView) convertView.findViewById(R.id.picture);

            try {
                Image image = albumCurrentlyBeingViewed.getImages().get(position);
                String imageUrl = ApiBroker.singleton().getUrlForImage(image);
                ImageLoader.getInstance().displayImage(
                        imageUrl,
                        imageView
                );
            } catch (ArrayIndexOutOfBoundsException e) {
                e.printStackTrace();
                imageView.setImageResource(R.drawable.ic_delete);
                Toast.makeText(getActivity(), "Couldn't load image.", Toast.LENGTH_SHORT).show();
            }

            return convertView;
        }
    }
}
