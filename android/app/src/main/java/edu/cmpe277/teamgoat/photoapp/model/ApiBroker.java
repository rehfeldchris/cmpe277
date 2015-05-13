package edu.cmpe277.teamgoat.photoapp.model;

import android.content.ContentResolver;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.GetRequest;
import com.mashape.unirest.request.HttpRequest;
import com.mashape.unirest.request.HttpRequestWithBody;
import com.mashape.unirest.request.body.MultipartBody;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
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

    private PhotoApp application;

    private String apiHost;
    private String facebookAccessToken;

    private ObjectMapper mapper;

//    private static ApiBroker instance;

    public ApiBroker(PhotoApp application) {
        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        this.application = application;
        facebookAccessToken = application.getFacebookAccessToken();
        apiHost = application.getServerUrl();
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

    public Album getAlbumById(String albumId) throws UnirestException, IOException {
        String url = String.format("%s/api/v1/albums/%s", apiHost, URLEncoder.encode(albumId));

        String json = Unirest
                .get(url)
                .header("X-Facebook-Token", facebookAccessToken)
                .asString()
                .getBody()
                ;

        Album album = mapper.readValue(json, new TypeReference<Album>() {});
        return album;
    }

    public Album createAlbum(String title, String description, boolean isPubliclyAccessible, List<String> grantedUserIds) throws IOException {
        String apiUrl = apiHost + "/api/v1/albums";

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

    public Image uploadImage(Album album, File imageFile, String title, String description, Double lat, Double lon) throws IOException, UnirestException {
        String url = String.format("%s/api/v1/albums/%s/images", apiHost, URLEncoder.encode(album.get_ID()));
        String mime = getMimeTypeOfFile(imageFile.getAbsolutePath());

        MultipartBody req =  Unirest
            .post(url)
            .header("X-Facebook-Token", facebookAccessToken)
            .field("file", imageFile)
            .field("title", title)
            .field("description", description)
            .field("fileMimeType", mime != null ? mime : "")
        ;


        if (lat != null && lon != null) {
            req
                .field("lat", lat.toString())
                .field("lon", lon.toString())
            ;
        }

        com.mashape.unirest.http.HttpResponse<String> response = req.asString();
        String jsonReply = response.getBody();

        Image image = mapper.readValue(jsonReply, new TypeReference<Image>(){});
        return image;
    }

    public static String getMimeTypeOfFile(String pathName) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, opt);
        return opt.outMimeType;
    }

    public boolean deleteAlbum(Album album) throws IOException, UnirestException {
        String url = String.format("%s/api/v1/albums/%s", apiHost, URLEncoder.encode(album.get_ID()));
        com.mashape.unirest.http.HttpResponse<String> response = Unirest
                .delete(url)
                .header("X-Facebook-Token", facebookAccessToken)
                .asString()
                ;

        return response.getCode() == 204;
    }

    public boolean deleteImage(Image image) throws IOException, UnirestException {
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

    /**
     * Takes the list of images viewable by the current user, and filters them by your criteria.
     *
     * Pass null for any argument if you dont want to specify a certain criteria.
     * The distance filter will only be performed if you pass all 3 lat, lon, and distance.
     *
     * @param lat
     * @param lon
     * @param maxDistanceMeters
     * @param keyWords - a space separated list of keywords. eg, "goat tiger" will filter the images, only returning the image if it has the word goat OR tiger. The image description, the images comments, and the comments' author name are searched.
     * @return the filtered list of viewable images
     * @throws UnirestException
     */
    public List<Image> findViewableImagesWithCriteria(Double lat, Double lon, Double maxDistanceMeters, String keyWords) throws UnirestException, IOException {
        String url = String.format("%s/api/v1/search-images", apiHost);

        GetRequest gr = Unirest
            .get(url)
            ;

        if (lat != null) {
            gr.field("lat", lat);
        }

        if (lon != null) {
            gr.field("lon", lon);
        }

        if (maxDistanceMeters != null) {
            gr.field("maxDistanceMeters", maxDistanceMeters);
        }

        if (keyWords != null) {
            gr.field("keyWords", keyWords);
        }

        String jsonReply = gr
                .header("X-Facebook-Token", facebookAccessToken)
                .asString()
                .getBody()
                ;

        return mapper.readValue(jsonReply, new TypeReference<List<Image>>() {});
    }

}
