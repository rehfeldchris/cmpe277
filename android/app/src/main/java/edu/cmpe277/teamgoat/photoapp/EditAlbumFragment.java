package edu.cmpe277.teamgoat.photoapp;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

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

public class EditAlbumFragment extends Fragment {
    private View view;
    private EditText titleTextInput;
    private EditText descriptionTextInput;
    private CheckBox isPublicCheckbox;
    private Button submitButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.layout_fragment_create_album, container, false);
        titleTextInput = (EditText) view.findViewById(R.id.album_title_input);
        descriptionTextInput = (EditText) view.findViewById(R.id.album_description_input);
        isPublicCheckbox = (CheckBox) view.findViewById(R.id.checkbox_album_public_private);
        submitButton = (Button) view.findViewById(R.id.create_album_button);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAlbum();
            }
        });

        return view;
    }

    private void createAlbum() {
        String apiUrl = R.string.api_host + "/api/v1/albums";

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

    // example
    public void postData() {
        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://www.yoursite.com/script.php");
        httppost.addHeader("X-Facebook-Token", LolGlobalVariables.facebookAccessToken);

        try {
            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<>();
            nameValuePairs.add(new BasicNameValuePair("id", "12345"));
            nameValuePairs.add(new BasicNameValuePair("stringdata", "AndDev is Cool!"));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);

        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
        } catch (IOException e) {
            // TODO Auto-generated catch block
        }
    }

}
