package edu.cmpe277.teamgoat.photoapp.services;

import edu.cmpe277.teamgoat.photoapp.dto.*;
import edu.cmpe277.teamgoat.photoapp.errors.BadApiRequestException;
import edu.cmpe277.teamgoat.photoapp.repos.AlbumMongoRepository;
import edu.cmpe277.teamgoat.photoapp.repos.CommentMongoRepository;
import edu.cmpe277.teamgoat.photoapp.repos.ImageMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PhotoService {


    @Autowired
    private AlbumMongoRepository albumMongoRepository;

    @Autowired
    private ImageMongoRepository imageMongoRepository;

    @Autowired
    private CommentMongoRepository commentMongoRepository;

    public Image createImage(MultipartFile file, Double lat, Double lon, String title, String description, String ownerUserId, String fileNameToSaveAs, String albumId) throws IOException {
        ImageInfo imageInfo = getImageInfo(file);
        double[] coords = lat != null && lon != null ? (new double[]{lat, lon}) : null;
        return new Image(
            ownerUserId,
            fileNameToSaveAs,
            coords,
            description,
            Collections.emptyList(),
            imageInfo.getWidth(),
            imageInfo.getHeight(),
            imageInfo.getSizeBytes(),
            imageInfo.getMimeType(),
            albumId
        );
    }

    public ImageInfo getImageInfo(MultipartFile file) throws IOException {
        try (ImageInputStream in = ImageIO.createImageInputStream(file.getInputStream())) {
            final Iterator<ImageReader> readers = ImageIO.getImageReaders(in);
            if (readers.hasNext()) {
                ImageReader reader = readers.next();
                try {
                    reader.setInput(in);
                    int width = reader.getWidth(0);
                    int height = reader.getHeight(0);
                    int size = (int) file.getSize();
                    String mimeType = file.getContentType();
                    return new ImageInfo(width, height, size, mimeType);
                } finally {
                    reader.dispose();
                }
            }
        }

        return null;
    }

    public List<Image> findViewableImagesNearPoint(String userId, double lat, double lon, double maxDistance) {
        // This uses mongo to find images near the point.
        // But, these images may or may not be viewable by the user, so we filter the images that aren't in an album viewable by the user.
        Set<Image> imagesInViewableAlbums = getViewableImages(userId);
        return imageMongoRepository.findImagesNearLocation(lat, lon, maxDistance).stream().filter(imagesInViewableAlbums::contains).collect(Collectors.toList());
    }

    public Comment addComment(User user, String imageId, String commentText) {
        Comment comment = new Comment(user.getFacebookUserId(), commentText, imageId, new Date(), user.getName());
        Image image = imageMongoRepository.findBy_ID(imageId);
        commentMongoRepository.save(comment);
        image.addComment(comment);
        imageMongoRepository.save(image);
        return comment;
    }

    public boolean isImageViewableByUser(Image image, String userId) {
        return getViewableImages(userId).contains(image);
    }

    public Set<Image> getViewableImages(String userId) {
        return albumMongoRepository.findViewable(userId).stream().flatMap(album -> album.getImages().stream()).collect(Collectors.toSet());
    }

    public void deleteImage(Image image) {
        // Remove the image from the album.
        Album album = albumMongoRepository.findBy_ID(image.getAlbumId());
        album.getImages().remove(image);

        // Delete all the comments for the image.
        for (Comment comment : image.getComments()) {
            commentMongoRepository.delete(comment);
        }

        // Finally, delete the image.
        imageMongoRepository.delete(image);
        albumMongoRepository.save(album);
    }


}
