package edu.cmpe277.teamgoat.photoapp.services;

import edu.cmpe277.teamgoat.photoapp.dto.User;

import java.util.Date;
import java.util.List;

import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import edu.cmpe277.teamgoat.photoapp.repos.UserMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserProfileService {

	private static final long MAX_CACHE_LIFETIME_MILLIS = 1000 * 60 * 60 * 1;

	@Autowired
	private UserMongoRepository userMongoRepository;

	@Autowired
	private FacebookTokenToUserIdCache facebookTokenToUserIdCache;

//	http://restfb.com/
//    public void getAllFriends(String facebookToken) {
//    	FacebookTemplate user = new FacebookTemplate(facebookToken);
//		return user.getRestTemplate().getForObject("https://graph.facebook.com/v2.3/me/friends?fields=name,picture", JSON.class);
//    }
	
	/**
	 * Get the current user's information
	 * @param facebookAccessToken The app user's accessToken
	 * @return User object that contains the app user's information
	 */
	public User getCurrentUser(String facebookAccessToken) {
		String facebookUserId = facebookTokenToUserIdCache.getFacebookUserId(facebookAccessToken);
		if (facebookUserId == null) {
			// The token wasn't in our cache, but the user might be in our db. We must go to facebook to find
			// the user id, unfortunately.
			User user = getUserFreshFromFacebook(facebookAccessToken);

			// Since we have fresh data at this point, we update our existing record, if we have one.
			return updateOrSave(user);
		}

		// Try to get user from db.
		User user = userMongoRepository.findByFacebookUserId(facebookUserId);

		if (user == null) {
			// User wasn't in the db, so fetch this user info for the first time ever.
			return updateOrSave(getUserFreshFromFacebook(facebookUserId));
		}

		if (user.getMillisSinceLastFacebookFetch() < MAX_CACHE_LIFETIME_MILLIS) {
			// User was in db, and was fresh. Serve from cache without refreshing the data.
			return user;
		}

		// This user has old stale data, refresh it.
		User newUserData = getUserFreshFromFacebook(facebookUserId);
		user.setName(newUserData.getName());
		user.setWhenDataFetchedFromFacebook(new Date());
		user.setProfilePhotoUrl(newUserData.getProfilePhotoUrl());
		userMongoRepository.save(user);
		return user;
	}

	private User getUserFreshFromFacebook(String facebookToken) {
		FacebookClient facebookClient = new DefaultFacebookClient(facebookToken);
		com.restfb.types.User fbuser = facebookClient.fetchObject("me", com.restfb.types.User.class);

		User currentUser = new User();
		currentUser.setFacebookUserId(fbuser.getId());
		currentUser.setName(fbuser.getUsername());
		currentUser.setProfilePhotoUrl(fbuser.getPicture() != null ? fbuser.getPicture().getUrl() : null);
		currentUser.setWhenDataFetchedFromFacebook(new Date());

		// Update our cache.
		facebookTokenToUserIdCache.setFacebookUserId(facebookToken, currentUser.getFacebookUserId());

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


	private User updateOrSave(User userInfo) {
		// i know a girl with a broken water heater. she smells like a goat.
		User existingUserRecord = userMongoRepository.findByFacebookUserId(userInfo.getFacebookUserId());
		if (existingUserRecord == null) {
			userMongoRepository.save(userInfo);
			return userInfo;
		}

		existingUserRecord.setName(userInfo.getName());
		existingUserRecord.setWhenDataFetchedFromFacebook(new Date());
		existingUserRecord.setProfilePhotoUrl(userInfo.getProfilePhotoUrl() != null ? userInfo.getProfilePhotoUrl().toString() : null);
		userMongoRepository.save(existingUserRecord);

		return existingUserRecord;
	}
}
