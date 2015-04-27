package edu.cmpe277.teamgoat.photoapp.web;

import edu.cmpe277.teamgoat.photoapp.dto.Album;
import edu.cmpe277.teamgoat.photoapp.dto.Comment;
import edu.cmpe277.teamgoat.photoapp.dto.Image;
import edu.cmpe277.teamgoat.photoapp.dto.ImageInfo;
import edu.cmpe277.teamgoat.photoapp.errors.BadApiRequestException;
import edu.cmpe277.teamgoat.photoapp.repos.AlbumMongoRepository;
import edu.cmpe277.teamgoat.photoapp.repos.CommentMongoRepository;
import edu.cmpe277.teamgoat.photoapp.repos.ImageMongoRepository;
import edu.cmpe277.teamgoat.photoapp.services.AlbumService;
import edu.cmpe277.teamgoat.photoapp.services.PhotoService;
import edu.cmpe277.teamgoat.photoapp.services.UserIdentityDiscoveryService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.PathParam;
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
	@Autowired
	private AlbumService albumService;
	@Value("${imageFileSaveDir}")
	private String imageFileSaveDir;
	@Value("${tempDir}")
	private String tempDir;
	@Autowired
	private HttpServletRequest request;
	@Autowired
	private HttpServletResponse response;

	private static SimpleDateFormat imageFilenameDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS");
	private static Random random = new Random();
	private static List<String> allowedImageMimeTypes = new ArrayList<>(Arrays.asList(new String[] {"image/jpeg", "image/gif", "image/png"}));
	private static final Logger LOG = Logger.getLogger(ApiRestController.class);


	@RequestMapping(value = "/albums", method = RequestMethod.GET)
	public List<Album> getListOfViewableAlbums(
			@RequestHeader("X-Facebook-Token") String facebookToken
	) {
		String userId = userIdentityDiscoveryService.getUserId(facebookToken);
		LOG.info(String.format("listing all albums userid=%s", userId));
		return albumRepo.findViewable(userId);
	}

	@RequestMapping(value = "/images-near-point", method = RequestMethod.GET)
	public List<Image> findViewableImagesNearPoint(
			@RequestHeader("X-Facebook-Token") String facebookToken,
			@RequestParam("lat") double lat,
			@RequestParam("lon") double lon,
			@RequestParam("maxDistanceMeters") double maxDistanceMeters
	) {
		String userId = userIdentityDiscoveryService.getUserId(facebookToken);
		LOG.info(String.format("listing all images near userid=%s lat=%.4f lon=%.4f maxdis=%.4f", userId, lat, lon, maxDistanceMeters));
		return photoService.findViewableImagesNearPoint(userId, lat, lon, maxDistanceMeters);
	}

	@RequestMapping(value = "/friends", method = RequestMethod.GET)
	public Object getFriendList(
			@RequestHeader("X-Facebook-Token") String facebookToken
	) {
		String userId = userIdentityDiscoveryService.getUserId(facebookToken);

		return "";
	}

    @RequestMapping(value="/albums", method = RequestMethod.POST)
    public Object createAlbum(
			@RequestHeader("X-Facebook-Token") String facebookToken,
			@RequestParam("title") String title,
			@RequestParam("description") String description,
			@RequestParam("grantedUserIds") List<String> grantedUserIds,
			@RequestParam("isPubliclyAccessible") boolean isPubliclyAccessible
	) {
		String userId = userIdentityDiscoveryService.getUserId(facebookToken);
		LOG.info(String.format("attempt create album userid=%s", userId));
		try {
			Album album = albumService.createAlbum(title, userId, description, grantedUserIds, isPubliclyAccessible);
			LOG.info(String.format("created album userid=%s album id=%s", userId, album.get_ID()));
			return album;
		} catch (BadApiRequestException ex) {
			LOG.error(String.format("failed to create album userid=%s", userId), ex);
			response.setStatus(400);
			return new ApiErrorResponse(ex.getMessage(), "");
		}
    }

	@RequestMapping(value="/albums/{albumId}", method = RequestMethod.PUT)
	public Object updateAlbum(
			@RequestHeader("X-Facebook-Token") String facebookToken,
			@PathVariable("albumId") String albumId,
			@RequestParam("title") String title,
			@RequestParam("description") String description,
			@RequestParam("grantedUserIds") List<String> grantedUserIds,
			@RequestParam("isPubliclyAccessible") boolean isPubliclyAccessible
	) {
		String userId = userIdentityDiscoveryService.getUserId(facebookToken);
		LOG.info(String.format("attempt update album userid=%s album id=%s", userId, albumId));
		try {
			Album album = albumService.updateAlbum(userId, albumId, title, description, grantedUserIds, isPubliclyAccessible);
			LOG.info(String.format("update album userid=%s album id=%s", userId, albumId));
			return album;
		} catch (BadApiRequestException ex) {
			LOG.error(String.format("failed to update album userid=%s album id=%s", userId, albumId), ex);
			response.setStatus(400);
			return new ApiErrorResponse(ex.getMessage(), "");
		}
	}

	@RequestMapping(value="/albums/{albumId}", method = RequestMethod.DELETE)
	public Object deleteAlbum(
			@RequestHeader("X-Facebook-Token") String facebookToken,
			@PathVariable("albumId") String albumId
	) {
		String userId = userIdentityDiscoveryService.getUserId(facebookToken);
		LOG.info(String.format("attempt delete album userid=%s album id = %s", userId, albumId));
		try {
			albumService.deleteAlbum(albumId, userId);
			LOG.info(String.format("delete album userid=%s album id = %s", userId, albumId));
			return null;
		} catch (BadApiRequestException ex) {
			LOG.error(String.format("failed to delete album userid=%s album id = %s", userId, albumId), ex);
			response.setStatus(400);
			return new ApiErrorResponse(ex.getMessage(), "");
		}
	}
    
	@RequestMapping(value = "/albums/{albumId}", method = RequestMethod.GET)
	public Object getAlbumInfo(
			@RequestHeader("X-Facebook-Token") String facebookToken,
			@PathVariable("albumId") String albumId
	) {
		String userId = userIdentityDiscoveryService.getUserId(facebookToken);
		LOG.info(String.format("attempt view album userid=%s album id = %s", userId, albumId));
		try {
			Album album = albumService.findAlbumAndAssertViewableByUser(albumId, userId);
			LOG.info(String.format("view album userid=%s album id = %s", userId, albumId));
			return album;
		} catch (BadApiRequestException ex) {
			LOG.info(String.format("failed to view album userid=%s album id = %s", userId, albumId));
			response.setStatus(400);
			return new ApiErrorResponse(ex.getMessage(), "");
		}
	}

//	@RequestMapping(value = "/albums/{album_id}", method = RequestMethod.POST)
//	public void setAlbumInfo(@PathVariable("userId") String facebookUserId, @PathVariable("album_id") String album_id) {
//		
//	}

	@RequestMapping(value = "/albums/{album_id}/images", method = RequestMethod.POST)
	public Object addImage(
			@PathVariable("album_id") String album_id,
			@RequestParam("title") String title,
			@RequestParam("description") String description,
			@RequestParam(value = "lat", required = false) Double lat,
			@RequestParam(value = "lon", required = false) Double lon,
			@RequestParam("file") MultipartFile file,
			@RequestHeader ("X-Facebook-Token") String facebookToken,
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
		Image image = photoService.createImage(file, lat, lon, title, description, userIdentityDiscoveryService.getUserId(facebookToken), savedFileName);
		album.addImage(image);
		imageRepo.save(image);
		albumRepo.save(album);
		return image;
	}

		
	@RequestMapping(value = "/albums/{album_id}/images/{image_id}/comments", method = RequestMethod.POST)
	public Comment postComments(@PathVariable("userId") String facebookUserId, @PathVariable("album_id ") String album_id, @PathVariable("image_id") String image_id, @RequestBody Comment comment) {
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

	private Map<String, String> getHeaders() {
		Map<String, String> map = new HashMap<>();
		Enumeration headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String key = (String) headerNames.nextElement();
			String value = request.getHeader(key);
			map.put(key, value);
		}
		return map;
	}



}
