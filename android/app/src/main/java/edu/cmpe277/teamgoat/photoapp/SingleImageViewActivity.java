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
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mashape.unirest.http.exceptions.UnirestException;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.cmpe277.teamgoat.photoapp.model.ApiBroker;
import edu.cmpe277.teamgoat.photoapp.model.Comment;
import edu.cmpe277.teamgoat.photoapp.model.Image;


public class SingleImageViewActivity extends ActionBarActivity {

    public static final String IMAGE_TAG = "SingleImage";
    private Image       imageBeingDisplayed;
    private ListView    listview_container_comment;
    private Button      btn_add_comment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.relativelayout_activity_single_image_view);


        // Load components' views and setup controllers.
        initializeComponents();

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
                    return ApiBroker.singleton().commentOnImage(imageBeingDisplayed, comment);
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
        private List<String> comments;

        private final Context context;

        public CommentBaseAdapter(Context context)
        {
            this.context = context;
        }

        public CommentBaseAdapter addComments(List<String> comments)
        {
            this.comments = comments;
            notifyDataSetChanged();

            return this;
        }

        public List<String> getComments()
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
            // TODO: setup the view and load fetched comments

            if (convertView == null)
                convertView = SingleImageViewActivity.this.getLayoutInflater().inflate(
                        R.layout.textview_single_layout, null
                );

            TextView comment = (TextView)convertView.findViewById(R.id.textview_comment);
            comment.setText((String)getItem(position));

            return convertView;
        }
    }

    private void initializeComponents()
    {
        imageBeingDisplayed = ImagesGridviewFragment.imageMostRecentlyClicked;

        generateTestComments();

        ImageView img = (ImageView)findViewById(R.id.img_singleImage_view);

        if (imageBeingDisplayed != null) {
            ImageLoader.getInstance().displayImage(
                    ApiBroker.singleton().getUrlForImage(imageBeingDisplayed),
                    img
            );
        } else {
            img.setImageResource(R.drawable.ic_delete);
        }

        listview_container_comment = (ListView)findViewById(R.id.listView_singleImage_comments);
        listview_container_comment.setAdapter(new CommentBaseAdapter(this).addComments(test_comments));

        btn_add_comment = (Button)findViewById(R.id.btn_comment_send);
        btn_add_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Add comment to the current view
                EditText edittext_comment = (EditText) findViewById(R.id.edit_txt_comment);

                ((CommentBaseAdapter) listview_container_comment.getAdapter())
                        .getComments()
                        .add("you - " + edittext_comment.getText().toString());
                ((CommentBaseAdapter) listview_container_comment.getAdapter()).notifyDataSetChanged();
                // TODO: upload comment to the server
                addComment(edittext_comment.getText().toString());

                // Clear edittext comment
                edittext_comment.setText("");
            }
        });
    }

    private void generateTestComments()
    {
        test_comments = new ArrayList<String>();
//        test_comments.add(new String("Test comment "));
//        test_comments.add(new String("Test comment "));
//        test_comments.add(new String("Test comment "));
//        test_comments.add(new String("Test comment "));
//        test_comments.add(new String("Test comment "));
//        test_comments.add(new String("Test comment "));
//        test_comments.add(new String("Test comment "));
//        test_comments.add(new String("Test comment "));
//        test_comments.add(new String("Test comment "));
//        test_comments.add(new String("Test comment "));
//        test_comments.add(new String("Test comment "));
//        test_comments.add(new String("Test comment "));
//        test_comments.add(new String("Test comment "));

        if (imageBeingDisplayed == null || imageBeingDisplayed.getComments() == null) {
            return;
        }

        for (Comment comment : imageBeingDisplayed.getComments()) {
            test_comments.add(comment.getUserName() + "-" + comment.getComment());
        }
    }
    private List<String> test_comments;


}
