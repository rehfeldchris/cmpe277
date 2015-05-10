package edu.cmpe277.teamgoat.photoapp;

/**
 * Created by squall on 4/29/15.
 */

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.mashape.unirest.http.exceptions.UnirestException;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.IOException;

import edu.cmpe277.teamgoat.photoapp.model.Album;
import edu.cmpe277.teamgoat.photoapp.model.ApiBroker;
import edu.cmpe277.teamgoat.photoapp.model.Image;

/**
 * A placeholder fragment containing a simple view.
 */
public class ImagesGridviewFragment extends Fragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    GridView mGridView;
    private Album albumCurrentlyBeingViewed;
    public static Image imageMostRecentlyClicked;
    private ImageAdapter imageAdapter;

    public ImagesGridviewFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_gridview_view_image, container, false);
        albumCurrentlyBeingViewed = PhotoAlbums.albumUserMostRecentlyClicked;
        mGridView = (GridView) getActivity().findViewById(R.id.grid_images);
        imageAdapter = new ImageAdapter(getActivity());
        mGridView.setAdapter(imageAdapter);
        mGridView.setOnItemClickListener(this);
        mGridView.setOnItemLongClickListener(this);

        return rootView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        imageMostRecentlyClicked = albumCurrentlyBeingViewed.getImages().get(position);
        Intent i = new Intent(getActivity(), SingleImageViewActivity.class);
        startActivity(i);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        final Album albumToModify = albumCurrentlyBeingViewed;
        final Image imageToDelete = albumToModify.getImages().get(position);

        if (!imageToDelete.getOwnerId().equals(LolGlobalVariables.currentlyLoggedInFacebookUserId)) {
            Toast.makeText(getActivity(), "You're not the original uploader of this image, so you can't delete it.", Toast.LENGTH_SHORT).show();
            return true;
        }

        new AlertDialog.Builder(getActivity())
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle("Confirm Delete")
            .setMessage("Are you sure you want to delete this image?")
            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    deleteImage(imageToDelete, albumToModify);

                }
            })
            .setNegativeButton("No", null)
            .show()
        ;

        return true;
    }

    private void deleteImage(final Image imageToDelete, final Album albumToModify) {
        new AsyncTask<Void, Void, Boolean>() {
            protected Boolean doInBackground(Void... params) {
                try {
                    return ApiBroker.singleton().deleteImage(imageToDelete);
                } catch (IOException|UnirestException e) {
                    Log.d("main", "failed to delete image", e);
                    return Boolean.FALSE;
                }
            }

            protected void onPostExecute(Boolean success) {
                if (!success) {
                    Toast.makeText(getActivity(), "Couldn't delete image. Maybe you aren't the owner?", Toast.LENGTH_SHORT).show();
                } else {
                    albumToModify.getImages().remove(imageToDelete);
                    imageAdapter.notifyDataSetChanged();
                    Toast.makeText(getActivity(), "Image deleted", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
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
                convertView = inflater.inflate(R.layout.photo_image, parent, false);
                convertView.setTag(R.id.picture_photo, convertView.findViewById(R.id.picture_photo));
            }

            imageView = (ImageView) convertView.findViewById(R.id.picture_photo);

            try {
                Image image = albumCurrentlyBeingViewed.getImages().get(position);
                String imageUrl = ApiBroker.singleton().getUrlForImage(image);
                ImageLoader.getInstance().displayImage(
                        imageUrl,
                        imageView
                );
                //imageView.setImageBitmap(ImageLoader.getInstance().loadImageSync(imageUrl, new ImageSize(50, 50)));
            } catch (ArrayIndexOutOfBoundsException e) {
                e.printStackTrace();
                imageView.setImageResource(R.drawable.ic_delete);
                Toast.makeText(getActivity(), "Couldn't load image.", Toast.LENGTH_SHORT).show();
            }

            return convertView;
        }
    }
}
