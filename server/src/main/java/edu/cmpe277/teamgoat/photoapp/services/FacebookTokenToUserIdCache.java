package edu.cmpe277.teamgoat.photoapp.services;


import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class FacebookTokenToUserIdCache {
    private static final long MAX_CACHE_LIFETIME_MILLIS = 1000 * 60 * 60 * 8;

    private Map<String, FacebookTokenToUserIdMapping> facebookTokenToUserIdMap = new HashMap<>();
    private static class FacebookTokenToUserIdMapping {
        Date whenFacebookApiWasCalled;
        String facebookToken;
        String facebookUserId;

        public FacebookTokenToUserIdMapping(String facebookToken, String facebookUserId) {
            this.whenFacebookApiWasCalled = new Date();
            this.facebookToken = facebookToken;
            this.facebookUserId = facebookUserId;
        }
    }

    public String getFacebookUserId(String facebookAccessToken) {
        FacebookTokenToUserIdMapping info = facebookTokenToUserIdMap.get(facebookAccessToken);
        if (info == null || new Date().getTime() - info.whenFacebookApiWasCalled.getTime() > MAX_CACHE_LIFETIME_MILLIS) {
            return null;
        }
        return info.facebookUserId;
    }

    public void setFacebookUserId(String facebookAccessToken, String facebookUserId) {
        facebookTokenToUserIdMap.put(facebookAccessToken, new FacebookTokenToUserIdMapping(facebookAccessToken, facebookUserId));
    }
}
