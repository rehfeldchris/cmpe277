package edu.cmpe277.teamgoat.photoapp;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mashape.unirest.http.exceptions.UnirestException;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.ocpsoft.pretty.time.PrettyTime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.cmpe277.teamgoat.photoapp.model.ApiBroker;
import edu.cmpe277.teamgoat.photoapp.model.Comment;
import edu.cmpe277.teamgoat.photoapp.model.Image;


public class SingleImageViewActivity extends ActionBarActivity {

    private Image       imageBeingDisplayed;
    private ListView    listview_container_comment;
    private Button      btn_add_comment;
    private ImageView   mImage;
    private PrettyTime prettyTime = new PrettyTime();
    private List<Comment> comments;
    private TextView textView;

    private PhotoApp photoApp;
    private ApiBroker apiBroker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.linearlayout_activity_single_image_view);

        photoApp = (PhotoApp) getApplication();
        apiBroker = photoApp.getApiBroker();

        // Load components' views and setup controllers.
        initializeComponents();

        // Set title/description
        if (imageBeingDisplayed != null) {
            textView = (TextView) findViewById(R.id.single_image_title);
            textView.setText(imageBeingDisplayed.getDescription());
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_single_image_view, menu);
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


    private void addComment(final String comment) {
        new AsyncTask<Void, Void, Comment>() {
            protected Comment doInBackground(Void... params) {
                try {
                    return apiBroker.commentOnImage(imageBeingDisplayed, comment);
                } catch (IOException | UnirestException e) {
                    Log.d("main", "failed to load friend list", e);
                    return null;
                }
            }

            protected void onPostExecute(Comment comment) {
                if (comment == null) {
                    Toast.makeText(getApplicationContext(), "Couldn't add comment. Sorry. We suxorz..", Toast.LENGTH_SHORT).show();
                } else {
                    // Maybe reload data somehow to make the comment show up? or just add the comment manually.
                    Toast.makeText(getApplicationContext(), "Comment added", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    private class CommentBaseAdapter extends BaseAdapter
    {
        private List<Comment> comments;

        private final Context context;

        public CommentBaseAdapter(Context context)
        {
            this.context = context;
        }

        public CommentBaseAdapter addComments(List<Comment> comments)
        {
            this.comments = comments;
            notifyDataSetChanged();

            return this;
        }

        public List<Comment> getComments()
        {
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
        public View getView(int position, View convertView, ViewGroup parent)
        {
            if (convertView == null) {
                convertView = SingleImageViewActivity.this.getLayoutInflater().inflate(
                    R.layout.layout_comment, null
                );
            }

            Comment comment = (Comment) getItem(position);

            TextView who = (TextView)convertView.findViewById(R.id.comment_who);
            who.setText(comment.getUserName() == null ? "Unknown" : comment.getUserName());

            TextView what = (TextView)convertView.findViewById(R.id.comment_what);
            what.setText(comment.getComment());

            TextView when = (TextView)convertView.findViewById(R.id.comment_when);
            when.setText(prettyTime.format(comment.getTimeStamp()));

            return convertView;
        }
    }

    private void initializeComponents()
    {
        imageBeingDisplayed = ImagesGridviewFragment.imageMostRecentlyClicked;

        initComments();

        mImage = (ImageView)findViewById(R.id.img_singleImage_view);

        if (imageBeingDisplayed != null) {
            ImageLoader.getInstance().displayImage(
                   apiBroker.getUrlForImage(imageBeingDisplayed),
                    mImage
            );
        } else {
            mImage.setImageResource(R.drawable.ic_delete);
        }

        listview_container_comment = (ListView)findViewById(R.id.listView_singleImage_comments);
        listview_container_comment.setAdapter(new CommentBaseAdapter(this).addComments(comments));

        btn_add_comment = (Button)findViewById(R.id.btn_comment_send);
        btn_add_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText edittext_comment = (EditText) findViewById(R.id.edit_txt_comment);

                Comment newComment = new Comment(null, edittext_comment.getText().toString(), null, new Date(), "you");

                ((CommentBaseAdapter) listview_container_comment.getAdapter()).getComments().add(newComment);
                ((CommentBaseAdapter) listview_container_comment.getAdapter()).notifyDataSetChanged();

                addComment(edittext_comment.getText().toString());

                // Clear edittext comment
                edittext_comment.setText("");
            }
        });
    }

    private void initComments()
    {
        if (imageBeingDisplayed == null || imageBeingDisplayed.getComments() == null) {
            comments = new ArrayList<>();
        } else {
            comments = imageBeingDisplayed.getComments();
        }
    }

    @Override
    protected void onDestroy() {
        mImage.destroyDrawingCache();
        listview_container_comment.destroyDrawingCache();
        System.gc();
        super.onDestroy();
    }

}
