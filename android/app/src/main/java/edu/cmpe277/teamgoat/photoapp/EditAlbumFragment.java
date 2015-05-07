package edu.cmpe277.teamgoat.photoapp;

import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mashape.unirest.http.exceptions.UnirestException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.cmpe277.teamgoat.photoapp.model.Album;
import edu.cmpe277.teamgoat.photoapp.model.ApiBroker;
import edu.cmpe277.teamgoat.photoapp.model.Friend;

public class EditAlbumFragment extends Fragment {
    private View view;
    private EditText titleTextInput;
    private EditText descriptionTextInput;
    private CheckBox isPublicCheckbox;
    private Button submitButton;
    private ListView friendListView;
    private List<Friend> friends;
    private FriendListAdapter friendListAdapter;

    public EditAlbumFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.layout_fragment_create_album, container, false);
        titleTextInput = (EditText) view.findViewById(R.id.album_title_input);
        descriptionTextInput = (EditText) view.findViewById(R.id.album_description_input);
        isPublicCheckbox = (CheckBox) view.findViewById(R.id.checkbox_album_public_private);
        submitButton = (Button) view.findViewById(R.id.create_album_button);
        friendListView = (ListView) view.findViewById(R.id.listview_granted_album_viewers);

        // Add a title/header to the list
        TextView textView = new TextView(getActivity());
        textView.setText("Pick friends who can view this album");
        friendListView.addHeaderView(textView);

        // Make it so you can click anywhere on the row to tick the checkbox.
        friendListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckBox cb = (CheckBox) view.findViewById(R.id.grant_album_access_checkbox);
                cb.setChecked(!cb.isChecked());
            }
        });

        // Handler for form submit.
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewAlbum();
                Intent activity = new Intent(getActivity(), PhotoAlbums.class);
                activity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(activity);
            }
        });

        // When they make the album public, we don't want to show them the friend list, because its not needed.
        isPublicCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setProperFriendListVisibility();
            }
        });

        setProperFriendListVisibility();
        loadFriendList();

        return view;
    }

    private void setProperFriendListVisibility() {
        if (isPublicCheckbox.isChecked()) {
            friendListView.setVisibility(View.GONE);
        } else {
            friendListView.setVisibility(View.VISIBLE);
        }
    }

    private void loadFriendList() {
        new AsyncTask<Void, Void, List<Friend>>() {
            protected List<Friend> doInBackground(Void... params) {
                try {
                    return getFriends();
                } catch (IOException|UnirestException  e) {
                    Log.d("main", "failed to load friend list", e);
                    return null;
                }
            }

            protected void onPostExecute(List<Friend> friends) {
                EditAlbumFragment.this.friends = friends;
                if (friends == null) {
                    Toast.makeText(getActivity(), "Couldn't load friend list, so you can select which friends can see your album. Sorry.", Toast.LENGTH_SHORT).show();
                } else {
                    friendListAdapter = new FriendListAdapter(getActivity(), R.layout.layout_fragment_friendlist, friends)
                    friendListView.setAdapter(friendListAdapter);
                }
            }
        }.execute();
    }

    private List<String> getUserIdsOfFriendsWhoCanViewAlbum() {
        List<String> userIds = new ArrayList<>();
        if (friendListAdapter == null) {
            return userIds;
        }
        for (Friend friend : friendListAdapter.getFriendsWhoCanViewAlbum()) {
            userIds.add((friend.getId()));
        }
        return userIds;
    }

    private void createNewAlbum() {
        new AsyncTask<Void, Void, Album>() {
            protected Album doInBackground(Void... params) {
                try {
                    return ApiBroker.singleton().createAlbum(titleTextInput.getText().toString(), descriptionTextInput.getText().toString(), isPublicCheckbox.isChecked(), getUserIdsOfFriendsWhoCanViewAlbum());
                } catch (IOException  e) {
                    Log.d("main", "failed to create album", e);
                    return null;
                }
            }

            protected void onPostExecute(Album album) {
                if (album == null) {
                    Toast.makeText(getActivity(), "Couldn't create your album. Maybe its a duplicate name?", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Album created", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    private void createAlbum() {
        String apiUrl = ApiBroker.apiHost + "/api/v1/albums";

        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(apiUrl);
        httppost.addHeader("X-Facebook-Token", LolGlobalVariables.facebookAccessToken);

        try {
            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<>();
            nameValuePairs.add(new BasicNameValuePair("title", titleTextInput.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("description", descriptionTextInput.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("isPubliclyAccessible", String.valueOf(isPublicCheckbox.isChecked())));
            for (String s : new String[]{}) {
                nameValuePairs.add(new BasicNameValuePair("grantedUserIds", s));
            }
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            String responseString = EntityUtils.toString(entity, "UTF-8");
            Toast.makeText(getActivity(), responseString, Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private List<Friend> getFriends() throws IOException, UnirestException {
        return ApiBroker.singleton().getFriends();
    }

}
