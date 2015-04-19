package edu.cmpe277.teamgoat.photoapp.web;

import edu.cmpe277.teamgoat.photoapp.repos.*;
import edu.cmpe277.teamgoat.photoapp.dto.*;
import edu.cmpe277.teamgoat.photoapp.errors.*;
import edu.cmpe277.teamgoat.photoapp.services.PhotoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api/v1/{userId}")
public class ApiRestController {

    @Autowired
    private AlbumMongoRepository albumRepo;
    @Autowired
    private CommentMongoRepository commentRepo;
    @Autowired
    private ImageMongoRepository imageRepo;
	@Autowired
	private PhotoService photoService;

	@RequestMapping(value = "/albums", method = RequestMethod.GET)
	public List<Album> getListOfAlbums(
			@PathVariable("userId") String facebookUserId)
			throws MissingUserInformation {
		if (facebookUserId == null) {
			throw new MissingUserInformation();
		}
		return albumRepo.findAll();
	}

    @RequestMapping(value="/albums", method = RequestMethod.POST)
    public Album createAlbum(@PathVariable("userId") String facebookUserId, @RequestBody Album album) throws MissingAlbumInformation {
		if (album != null) {
    		albumRepo.save(album);
    	}
    	else{
    		throw new MissingAlbumInformation();
    	}
    	return album;
    }
    
	@RequestMapping(value = "/albums/{album_id}", method = RequestMethod.GET)
	public Album getAlbumInfo(@PathVariable("userId") String facebookUserId, @PathVariable("album_id") String album_id) {
		return albumRepo.findBy_ID(album_id);
	}

//	@RequestMapping(value = "/albums/{album_id}", method = RequestMethod.POST)
//	public void setAlbumInfo(@PathVariable("userId") String facebookUserId, @PathVariable("album_id") String album_id) {
//		
//	}

	@RequestMapping(value = "/albums/{album_id}/images", method = RequestMethod.POST)
	public Image addImage(@PathVariable("userId") String facebookUserId, @PathVariable("album_id") String album_id, @RequestBody Image image) {
		if(image != null) {
			Album album = albumRepo.findBy_ID(album_id);
			album.getImages().add(image);
	    	return image;
		}
		return null;
	}
		
	@RequestMapping(value = "/albums/{album_id}/images/{image_id}/comments", method = RequestMethod.POST)
	public Comment postComments(@PathVariable("userId") String facebookUserId, @PathVariable("album_id") String album_id, @PathVariable("image_id") String image_id, @RequestBody Comment comment) {
		if (comment != null) {
			Image image = imageRepo.findByImageId(image_id);
			image.getComments().add(comment);
			return comment;
		}
		return null;
	}
}
