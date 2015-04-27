package edu.cmpe277.teamgoat.photoapp.services;

import edu.cmpe277.teamgoat.photoapp.dto.User;

import java.util.Date;
import java.util.List;

import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;

public class UserProfileService {
//	http://restfb.com/
//    public void getAllFriends(String facebookToken) {
//    	FacebookTemplate user = new FacebookTemplate(facebookToken);
//		return user.getRestTemplate().getForObject("https://graph.facebook.com/v2.3/me/friends?fields=name,picture", JSON.class);
//    }
	
	/**
	 * Get the current user's information
	 * @param facebookToken The app user's accessToken
	 * @return User object that contains the app user's information
	 */
	public User getCurrentUser(String facebookToken) {
		FacebookClient facebookClient = new DefaultFacebookClient(facebookToken);
		com.restfb.types.User fbuser = facebookClient.fetchObject("me", com.restfb.types.User.class);
		
		User currentUser = new User();
		currentUser.setFacebookUserId(fbuser.getId());
		currentUser.setName(fbuser.getUsername());
        currentUser.setProfilePhotoUrl(fbuser.getPicture().getUrl());
        currentUser.setWhenDataFetchedFromFacebook(new Date());
        return currentUser;
	}
	
	/**
	 * Retrieve the app user's list of friends
	 * @param facebookToken The app user's accessToken
	 * @return A List of Users that are friends with the app user
	 */
	public List<User> getUserFriends(String facebookToken) {
		FacebookClient facebookClient = new DefaultFacebookClient(facebookToken);
		Connection<User> myFriends = facebookClient.fetchConnection("me/friends", User.class);
		return myFriends.getData();
	}
	
}
