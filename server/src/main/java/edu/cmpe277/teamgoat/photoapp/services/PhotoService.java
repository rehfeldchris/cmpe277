package edu.cmpe277.teamgoat.photoapp.services;

import edu.cmpe277.teamgoat.photoapp.dto.Image;
import edu.cmpe277.teamgoat.photoapp.dto.ImageInfo;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;

@Service
public class PhotoService {



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





}
