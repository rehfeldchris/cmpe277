package edu.cmpe277.teamgoat.photoapp.services;

import edu.cmpe277.teamgoat.photoapp.dto.Image;
import edu.cmpe277.teamgoat.photoapp.dto.ImageInfo;
import edu.cmpe277.teamgoat.photoapp.repos.AlbumMongoRepository;
import edu.cmpe277.teamgoat.photoapp.repos.ImageMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PhotoService {


    @Autowired
    private AlbumMongoRepository albumMongoRepository;

    @Autowired
    private ImageMongoRepository imageMongoRepository;

    public Image createImage(MultipartFile file, Double lat, Double lon, String title, String description, String ownerUserId, String fileNameToSaveAs) throws IOException {
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
            imageInfo.getMimeType()
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
        Set<Image> imagesInVieableAlbums =  albumMongoRepository.findViewable(userId).stream().flatMap(album -> album.getImages().stream()).collect(Collectors.toSet());
        return imageMongoRepository.findImagesNearLocation(lat, lon, maxDistance).stream().filter(imagesInVieableAlbums::contains).collect(Collectors.toList());
    }


}
