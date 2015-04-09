package edu.cmpe277.teamgoat.photoapp.repos;


import edu.cmpe277.teamgoat.photoapp.dto.Album;
import edu.cmpe277.teamgoat.photoapp.dto.Image;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AlbumMongoRepository extends MongoRepository<Album, String> {


    List<Album> findByOwnerId(String ownerId);
    List<Album> findByOwnerIdIgnoreCase(String ownerId);
    
}
