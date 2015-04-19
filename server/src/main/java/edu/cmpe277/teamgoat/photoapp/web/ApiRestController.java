package edu.cmpe277.teamgoat.photoapp.web;

import edu.cmpe277.teamgoat.photoapp.repos.*;
import edu.cmpe277.teamgoat.photoapp.dto.*;
import edu.cmpe277.teamgoat.photoapp.errors.*;
import edu.cmpe277.teamgoat.photoapp.services.PhotoService;

import edu.cmpe277.teamgoat.photoapp.services.UserIdentityDiscoveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;


@RestController
@RequestMapping("/api/v1/{userId}")
public class ApiRestController {

	@Autowired
	private UserIdentityDiscoveryService userIdentityDiscoveryService;
    @Autowired
    private AlbumMongoRepository albumRepo;
    @Autowired
    private CommentMongoRepository commentRepo;
    @Autowired
    private ImageMongoRepository imageRepo;
	@Autowired
	private PhotoService photoService;
	@Value("${imageFileSaveDir}")
	private static String imageFileSaveDir;
	@Value("${tempDir}")
	private static String tempDir;

	private static SimpleDateFormat imageFilenameDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS");
	private static Random random = new Random();
	private static List<String> allowedImageMimeTypes = new ArrayList<>(Arrays.asList(new String[] {"image/jpeg", "image/gif", "image/png"}));


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
	public Object addImage(
			@PathVariable("userId") String facebookUserId,
			@PathVariable("album_id") String album_id,
			@RequestParam("title") String title,
			@RequestParam("description") String description,
			@RequestParam(value = "lat", required = false) Double lat,
			@RequestParam(value = "lon", required = false) Double lon,
			@RequestParam("file") MultipartFile file,
			HttpServletResponse response
	) throws IOException {

		ImageInfo imageInfo = photoService.getImageInfo(file);
		if (imageInfo.getHeight() > 10_000) {
			response.setStatus(400);
			return new ApiErrorResponse("height too big. must be under 10,000", "");
		}
		if (imageInfo.getWidth() > 10_000) {
			response.setStatus(400);
			return new ApiErrorResponse("width too big. must be under 10,000", "");
		}
		if (imageInfo.getSizeBytes() > 1_000_000_000) {
			response.setStatus(400);
			return new ApiErrorResponse("size too big. must be under 1,000,000,000 bytes", "");
		}
		if (!allowedImageMimeTypes.contains(imageInfo.getMimeType())) {
			response.setStatus(400);
			return new ApiErrorResponse("mime type not allowed. must be one of " + String.join(", ", allowedImageMimeTypes), "");
		}
		Album album = albumRepo.findBy_ID(album_id);
		if (album == null) {
			response.setStatus(400);
			return new ApiErrorResponse("album not found", "");
		}

		String savedFileName = saveImageToFileSystem(file);
		Image image = photoService.createImage(file, lat, lon, title, description, facebookUserId, savedFileName);
		album.getImages().add(image);
		return image;
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

	private synchronized String generateUniqueFilename() {
		return String.format(
			"%s-%10s",
			imageFilenameDateFormat.format(new Date()),
			random.nextLong()
		);
	}

	private String saveImageToFileSystem(MultipartFile uploadedFile) throws IOException {
		String fileName = generateUniqueFilename();
		String tempName = tempDir + "/" + fileName;
		String finalName = imageFileSaveDir + "/" + fileName;

		File imageFile = new File(tempName);

		BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(imageFile));
		stream.write(uploadedFile.getBytes());
		stream.close();

		if (!imageFile.renameTo(new File(finalName))) {
			throw new IOException("couldnt rename image file");
		}

		return fileName;
	}


}
