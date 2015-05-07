package edu.cmpe277.teamgoat.photoapp;

/**
 * Created by squall on 4/29/15.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
//import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;
//import android.widget.ListAdapter;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

import edu.cmpe277.teamgoat.photoapp.model.Album;
import edu.cmpe277.teamgoat.photoapp.model.Image;

/**
 * A placeholder fragment containing a simple view.
 */
public class ImagesGridviewFragment extends Fragment implements AdapterView.OnItemClickListener
{

    //        GridLayout mGridLayout;
    GridView mGridView;
    ArrayList<Image> images;    // for dynamic view
    private Integer [] images_static = {
            R.drawable.ic_launcher, R.drawable.sample_0,
            R.drawable.sample_1, R.drawable.sample_2,
            R.drawable.sample_3, R.drawable.sample_4,
            R.drawable.sample_5, R.drawable.sample_6,
            R.drawable.sample_7, R.drawable.goat_cool
    };  // static view: code demo

    private Album albumCurrentlyBeingViewed;

    public ImagesGridviewFragment()
    {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_gridview_view_image, container, false);

        albumCurrentlyBeingViewed = PhotoAlbums.albumUserMostRecentlyClicked;

        mGridView = (GridView)getActivity().findViewById(R.id.grid_images);
        ImageAdapter adapter = new ImageAdapter(getActivity());
        mGridView.setAdapter(adapter);

        mGridView.setOnItemClickListener(this);


        return rootView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        int clickedPosition = position;
        Intent i = new Intent(getActivity(), SingleImageViewActivity.class);
        i.putExtra(SingleImageViewActivity.IMAGE_TAG, images_static[clickedPosition].intValue());
        startActivity(i);
    }

    class ImageAdapter extends BaseAdapter
    {
        private Context mContext;

        public ImageAdapter(Context c)
        {
            super();
            mContext = c;
        }

        public int getCount() { return images_static.length; }

        public Object getItem(int position)
        {
            return null;
        }

        public long getItemId(int position)
        {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {

            ImageView imageView;

            if (convertView == null)
            {
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(8, 8, 8, 8);
            }else
                imageView = (ImageView)convertView;

            imageView.setImageResource(images_static[position]);

            try {
                Image image = albumCurrentlyBeingViewed.getImages().get(position);
                ImageLoader.getInstance().displayImage(
                        image.,
                        imageView
                );
            } catch (ArrayIndexOutOfBoundsException e) {
                e.printStackTrace();
                imageView.setImageResource(R.drawable.ic_delete);
                Toast.makeText(getActivity(), "Couldn't load image.", Toast.LENGTH_SHORT).show();
            }




            return imageView;
        }
    }
}