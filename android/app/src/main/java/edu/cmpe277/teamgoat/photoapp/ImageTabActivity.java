package edu.cmpe277.teamgoat.photoapp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mashape.unirest.http.exceptions.UnirestException;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.ocpsoft.pretty.time.PrettyTime;

import edu.cmpe277.teamgoat.photoapp.model.Album;
import edu.cmpe277.teamgoat.photoapp.model.ApiBroker;
import edu.cmpe277.teamgoat.photoapp.model.Comment;
import edu.cmpe277.teamgoat.photoapp.model.Image;


public class ImageTabActivity extends ActionBarActivity {

    private ImagesPagerAdapter mImagesPagerAdapter;
    private ViewPager mViewPager;

    private PhotoApp photoApp;
    private ApiBroker apiBroker;
    private Album currentlyViewingAlbum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_tab);

        photoApp = (PhotoApp) getApplication();
        apiBroker = photoApp.getApiBroker();
        currentlyViewingAlbum = PhotoAlbums.albumUserMostRecentlyClicked;

        // Create the adapter that will return a fragment for each of the Image View sections of the activity.
        mImagesPagerAdapter = new ImagesPagerAdapter(getSupportFragmentManager(), currentlyViewingAlbum);

        setTitle(currentlyViewingAlbum.getName());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mImagesPagerAdapter);
        // mViewPager.setCurrentItem(0); // TODO set the default image view

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_image_tab, menu);
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


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class ImagesPagerAdapter extends FragmentPagerAdapter {

        private Album album;

        private List<ImageViewFragment> imageViewFragments;

        public ImagesPagerAdapter(FragmentManager fm, Album album) {
            super(fm);
            this.album = album;
            imageViewFragments = new ArrayList<>();

            int index = 0;
            for (Image image : album.getImages()) {
                imageViewFragments.add(ImageViewFragment.newInstance(index++));
            }
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return imageViewFragments.get(position);
        }

        @Override
        public int getCount() {
            return imageViewFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return album.getName();
        }
    }

    public static class ImageViewFragment extends Fragment {
        private static final String IMAGE_INDEX_NUMBER = "image_index_number";

        public static ImageViewFragment newInstance(int imageIndex) {
            ImageViewFragment fragment = new ImageViewFragment();
            Bundle args = new Bundle();
            args.putInt(IMAGE_INDEX_NUMBER, imageIndex);
            fragment.setArguments(args);
            return fragment;
        }

        private int imageIndex;

        private PhotoApp photoApp;
        private ApiBroker apiBroker;
        private Album currentlyViewingAlbum;
        private Image image;
        private List<Comment> comments;


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_image_tab, container, false);
            imageIndex = getArguments().getInt(IMAGE_INDEX_NUMBER);

            photoApp = (PhotoApp) getActivity().getApplication();
            apiBroker = photoApp.getApiBroker();
            currentlyViewingAlbum = PhotoAlbums.albumUserMostRecentlyClicked;
            image = currentlyViewingAlbum.getImages().get(imageIndex);

            initializeComponents(
                    (ImageView) rootView.findViewById(R.id.imageView),
                    (TextView) rootView.findViewById(R.id.image_description),
                    (ListView) rootView.findViewById(R.id.listView_singleImage_comments),
                    (EditText) rootView.findViewById(R.id.image_view_add_comment),
                    (Button) rootView.findViewById(R.id.btn_comment_send));

            return rootView;
        }

        private void initializeComponents(final ImageView mImage, final TextView descriptionField, final ListView commentsListView, final EditText commentsEditText, final Button addCommentsButton) {
            // Load Image
            ImageLoader.getInstance().displayImage(
                apiBroker.getUrlForImage(image),
                mImage
            );

            // Set Description
            descriptionField.setText(image.getDescription());

            // Load Comments
            comments = image.getComments();
            if (image.getComments() == null) {
                comments = new ArrayList<>();
            }
            commentsListView.setAdapter(new CommentBaseAdapter(getActivity().getLayoutInflater()).addComments(comments));

            // Add Comment Section
            commentsEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    addCommentsButton.setEnabled(s.length() > 0);
                }
            });
            addCommentsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String newCommentText = commentsEditText.getText().toString();

                    CommentBaseAdapter adapter = (CommentBaseAdapter) commentsListView.getAdapter();
                    adapter.getComments().add(new Comment(null, newCommentText, null, new Date(), "you"));
                    adapter.notifyDataSetChanged();

                    addComment(newCommentText);

                    // Clear comment field
                    commentsEditText.setText("");
                }
            });
        }

        private void addComment(final String comment) {
            new AsyncTask<Void, Void, Comment>() {
                protected Comment doInBackground(Void... params) {
                    try {
                        return apiBroker.commentOnImage(image, comment);
                    } catch (IOException | UnirestException e) {
                        Log.d("main", "failed to load friend list", e);
                        return null;
                    }
                }

                protected void onPostExecute(Comment comment) {
                    if (comment == null) {
                        Toast.makeText(getActivity(), "Couldn't add comment. Sorry. We suxorz..", Toast.LENGTH_SHORT).show();
                    } else {
                        // Maybe reload data somehow to make the comment show up? or just add the comment manually.
                        Toast.makeText(getActivity(), "Comment added", Toast.LENGTH_SHORT).show();
                    }
                }
            }.execute();
        }
    }

    public static class CommentBaseAdapter extends BaseAdapter {
        private PrettyTime prettyTime;
        private List<Comment> comments;

        private final LayoutInflater layoutInflater;

        public CommentBaseAdapter(LayoutInflater layoutInflater) {
            this.layoutInflater = layoutInflater;
            prettyTime = new PrettyTime();
        }

        public CommentBaseAdapter addComments(List<Comment> comments) {
            this.comments = comments;
            notifyDataSetChanged();

            return this;
        }

        public List<Comment> getComments() {
            return comments;
        }

        @Override
        public int getCount() {
            return comments.size();
        }

        @Override
        public Object getItem(int position) {
            return comments.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = layoutInflater.inflate(
                        R.layout.layout_comment, null
                );
            }

            Comment comment = (Comment) getItem(position);

            TextView who = (TextView) convertView.findViewById(R.id.comment_who);
            who.setText(comment.getUserName() == null ? "Unknown" : comment.getUserName());

            TextView what = (TextView) convertView.findViewById(R.id.comment_what);
            what.setText(comment.getComment());

            TextView when = (TextView) convertView.findViewById(R.id.comment_when);
            when.setText(prettyTime.format(comment.getTimeStamp()));

            return convertView;
        }
    }

}
