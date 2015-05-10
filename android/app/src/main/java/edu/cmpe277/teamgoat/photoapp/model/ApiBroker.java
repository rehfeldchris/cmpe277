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

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import edu.cmpe277.teamgoat.photoapp.PhotoApp;

public class ApiBroker {

    private static PhotoApp application;

    private ObjectMapper mapper = new ObjectMapper();
    public static String facebookAccessToken = "CAAWuZCfKYoIYBABNJ9aZC383P9XW6Ffl219kkfoU6lZBHx5AXz8ClLjVgPg2ZB0sAYEKOZB7GJk6qNLDiKqUzi5ZAkwfRlLmuI80BrbIWwDkbO07CZB5N1JbouWGOp1HDVRbmawVDwUoi9AugQIKOyOtyZBtdtVovpxH8ocuzDxqyHX9mVQasHxa4JL74O7AHZBrTw6fw5mLhsepEJwxA6jey";
    // 10.0.2.2 is localhost on the machine hosting the emulator.
    public static String apiHost = "http://10.0.2.2:80";
    //public static String apiHost = "https://srkarra.com:444";
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

    public static void setApplication(PhotoApp photoApp) {
        ApiBroker.application = photoApp;
        // ApiBroker.facebookAccessToken = photoApp.getFacebookAccessToken(); // TODO TEST
    }

    public List<User> getFriends() throws UnirestException, IOException {
        String url = apiHost + "/api/v1/friends";
        String json = Unirest
            .get(url)
            .header("X-Facebook-Token", facebookAccessToken)
            .asString()
            .getBody()
        ;

        List<User> friends = mapper.readValue(json, new TypeReference<List<User>>(){});
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
        String jsonAlbum = mapper.writeValueAsString(newAlbum);

        String url = String.format("%s/api/v1/albums/%s", apiHost, URLEncoder.encode(newAlbum.get_ID()));
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

    // untested
    public Image uploadImage(Album album, File imageFile, String title, String description, Double lat, Double lon) throws IOException, UnirestException {
        String url = String.format("%s/api/v1/albums/%s/images", apiHost, URLEncoder.encode(album.get_ID()));
        com.mashape.unirest.http.HttpResponse<String> response = Unirest
                .post(url)
                .header("X-Facebook-Token", facebookAccessToken)
                .field("file", imageFile)
                .field("title", title)
                .field("description", description)
                .field("lat", lat.toString())
                .field("lon", lon.toString())
                .asString()
                ;

        String jsonReply = response.getBody();

        Image image = mapper.readValue(jsonReply, new TypeReference<Image>(){});
        return image;
    }

    public boolean deleteAlbum(Album album) throws IOException, UnirestException {
        String url = String.format("%s/api/v1/albums/%s", apiHost, URLEncoder.encode(album.get_ID()));
        com.mashape.unirest.http.HttpResponse<String> response = Unirest
                .delete(url)
                .header("X-Facebook-Token", facebookAccessToken)
                .asString()
                ;

        return response.getCode() == 200;
    }

    public boolean deleteImage(Image image) throws IOException, UnirestException {
        String jsonAlbum = mapper.writeValueAsString(image);

        String url = String.format("%s/api/v1/images/%s", apiHost, URLEncoder.encode(image.get_ID()));
        com.mashape.unirest.http.HttpResponse<String> response = Unirest
                .delete(url)
                .header("X-Facebook-Token", facebookAccessToken)
                .asString()
                ;

        return response.getCode() == 204;
    }

    public String getUrlForImage(Image image) {
        return String.format("%s/api/v1/raw-images/%s", apiHost, URLEncoder.encode(image.get_ID()));
    }

    public Comment commentOnImage(Image image, String commentText) throws IOException, UnirestException {
        String url = String.format("%s/api/v1/images/%s/comments", apiHost, URLEncoder.encode(image.get_ID()));

        com.mashape.unirest.http.HttpResponse<String> response = Unirest
                .post(url)
                .header("X-Facebook-Token", facebookAccessToken)
                .field("comment", commentText)
                .asString()
                ;

        String jsonReply = response.getBody();
        return mapper.readValue(jsonReply, new TypeReference<Comment>() {});
    }


}
