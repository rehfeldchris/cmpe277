package edu.cmpe277.teamgoat.photoapp.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.List;

import edu.cmpe277.teamgoat.photoapp.LolGlobalVariables;

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

    public List<Friend> getFriends() throws IOException {
        HttpClient httpclient = new DefaultHttpClient();
        String url = apiHost + "/api/v1/friends";
        HttpGet req = new HttpGet(url);
        req.addHeader("X-Facebook-Token", facebookAccessToken);
        HttpResponse response = httpclient.execute(req);
        String json = EntityUtils.toString(response.getEntity(), "UTF-8");
        List<Friend> friends = mapper.readValue(json, new TypeReference<List<Friend>>(){});
        return friends;
    }
}
