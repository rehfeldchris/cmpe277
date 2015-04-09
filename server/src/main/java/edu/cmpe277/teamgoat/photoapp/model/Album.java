package edu.cmpe277.teamgoat.photoapp.model;

import edu.cmpe277.teamgoat.photoapp.dto.Image;
import edu.cmpe277.teamgoat.photoapp.repos.AlbumMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class Album {

    @Autowired
    AlbumMongoRepository repo;

    public boolean createAlbum(String name, String ownerId) {

        return true;
    }

    public boolean deleteAlbum(String albumId, String requesterUserId) {
        return true;
    }
}
