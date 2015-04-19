package edu.cmpe277.teamgoat.photoapp.services;

import org.springframework.stereotype.Service;

/**
 * This class is the thing that the rest of the app will use to translate a facebook auth token into the details of
 * its corresponding user, like name, id etc..
 *
 * When we actually implement the facebook auth stuff, we will have the user send the facebook
 * auth token to the server for all requests. Then, the server can contact facebook to get the user's
 * name/id etc...
 *
 * We should cache the results of contacting facebook for a given auth token.
 *
 * This class will eventually do all that stuff.
 */
@Service
public class UserIdentityDiscoveryService {

    public String getUserId(String facebookToken) {
        // For now, we just mock this method.
        return facebookToken;
    }
}
