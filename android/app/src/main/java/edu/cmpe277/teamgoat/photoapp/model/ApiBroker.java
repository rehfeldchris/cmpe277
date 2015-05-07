package edu.cmpe277.teamgoat.photoapp.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ApiBroker {
    private ObjectMapper mapper = new ObjectMapper();
    private String facebookAccessToken = "CAAWuZCfKYoIYBABNJ9aZC383P9XW6Ffl219kkfoU6lZBHx5AXz8ClLjVgPg2ZB0sAYEKOZB7GJk6qNLDiKqUzi5ZAkwfRlLmuI80BrbIWwDkbO07CZB5N1JbouWGOp1HDVRbmawVDwUoi9AugQIKOyOtyZBtdtVovpxH8ocuzDxqyHX9mVQasHxa4JL74O7AHZBrTw6fw5mLhsepEJwxA6jey";
    // 10.0.2.2 is localhost on the machine hosting the emulator.
    public static String apiHost = "http://10.0.2.2:80";
    private static ApiBroker instance;

    public ApiBroker() {
        //facebookAccessToken = LolGlobalVariables.facebookAccessToken;
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static ApiBroker singleton() {
        if (instance == null) {
            instance = new ApiBroker();
        }
        return instance;
    }

    public List<Friend> getFriends() throws UnirestException, IOException {
        String url = apiHost + "/api/v1/friends";
        String json = Unirest
            .get(url)
            .header("X-Facebook-Token", facebookAccessToken)
            .asString()
            .getBody()
        ;

        List<Friend> friends = mapper.readValue(json, new TypeReference<List<Friend>>(){});
        return friends;
    }

    public List<Album> getViewableAlbums() throws UnirestException, IOException {
        String url = apiHost + "/api/v1/albums";
        String json = Unirest
                .get(url)
                .header("X-Facebook-Token", facebookAccessToken)
                .asString()
                .getBody()
                ;

        List<Album> albums = mapper.readValue(json, new TypeReference<List<Album>>(){});
        return albums;
    }

    public Album createAlbum(String title, String description, boolean isPubliclyAccessible, List<String> grantedUserIds) throws IOException {
        String apiUrl = ApiBroker.apiHost + "/api/v1/albums";

        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(apiUrl);
        httppost.addHeader("X-Facebook-Token", facebookAccessToken);

        List<NameValuePair> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(new BasicNameValuePair("title", title));
        nameValuePairs.add(new BasicNameValuePair("description", description));
        nameValuePairs.add(new BasicNameValuePair("isPubliclyAccessible", String.valueOf(isPubliclyAccessible)));
        for (String s : grantedUserIds) {
            nameValuePairs.add(new BasicNameValuePair("grantedUserIds", s));
        }
        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

        HttpResponse response = httpclient.execute(httppost);
        HttpEntity entity = response.getEntity();
        String json = EntityUtils.toString(entity, "UTF-8");
        Album album = mapper.readValue(json, new TypeReference<Album>(){});
        return album;
    }

    public Album updateAlbum(Album newAlbum) throws IOException, UnirestException {
        String apiUrl = ApiBroker.apiHost + "/api/v1/albums";

        String jsonAlbum = mapper.writeValueAsString(newAlbum);

        String url = apiHost + "/api/v1/albums/" + newAlbum.get_ID();
        com.mashape.unirest.http.HttpResponse<String> response = Unirest
            .put(url)
            .header("X-Facebook-Token", facebookAccessToken)
            .header("Content-type", "application/json")
            .body(jsonAlbum)
            .asString()
            ;

        String jsonReply = response.getBody();

        Album album = mapper.readValue(jsonReply, new TypeReference<Album>(){});
        return album;
    }

    public boolean deleteAlbum(Album album) throws IOException, UnirestException {
        String apiUrl = ApiBroker.apiHost + "/api/v1/albums";

        String jsonAlbum = mapper.writeValueAsString(album);

        String url = apiHost + "/api/v1/albums/" + album.get_ID();
        com.mashape.unirest.http.HttpResponse<String> response = Unirest
                .delete(url)
                .header("X-Facebook-Token", facebookAccessToken)
                .asString()
                ;

        return response.getCode() == 200;
    }

    public List<Friend> _getFriends() throws IOException {
        HttpClient httpclient = new DefaultHttpClient();
        String url = apiHost + "/api/v1/friends";
        HttpGet req = new HttpGet(url);
        req.addHeader("X-Facebook-Token", facebookAccessToken);
        HttpResponse response = httpclient.execute(req);
        String json = EntityUtils.toString(response.getEntity(), "UTF-8");
        List<Friend> friends = mapper.readValue(json, new TypeReference<List<Friend>>() {
        });
        return friends;
    }

    public String getUrlForImage(Image image) {
        return apiHost + "/api/v1/raw-images/" + image.get_ID();
    }

}
