package edu.cmpe277.teamgoat.photoapp.services;

import org.springframework.social.facebook.api.PagedList;
import org.springframework.social.facebook.api.User;
import org.springframework.social.facebook.api.UserInvitableFriend;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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
 * 
 * 
 */
@Service
public class UserIdentityDiscoveryService {
	RestTemplate restT = new RestTemplate();

	//Get facebook id for user
    public String getUserId(String facebookToken) {
    	FacebookTemplate user = new FacebookTemplate(facebookToken);
    	return user.userOperations().getUserProfile().getId();
    }
    
    //Get the facebook user name
    public String getUserName(String facebookToken) {
    	FacebookTemplate user = new FacebookTemplate(facebookToken);
    	return user.userOperations().getUserProfile().getName();
    }
    
    public PagedList<UserInvitableFriend> getInvitableFriendsList(String facebookToken) {
    	FacebookTemplate user = new FacebookTemplate(facebookToken);
    	PagedList<UserInvitableFriend> friends = user.friendOperations().getInvitableFriends();
    	return friends;
    }
    
    public PagedList<User> getFriendsList(String facebookToken) {
    	FacebookTemplate user = new FacebookTemplate(facebookToken);
    	PagedList<User> friends = user.friendOperations().getFriendProfiles();
    	return friends;
    }
    
    
}
