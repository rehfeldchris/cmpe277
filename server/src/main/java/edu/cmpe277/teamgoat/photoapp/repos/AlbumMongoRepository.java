package edu.cmpe277.teamgoat.photoapp.repos;


import edu.cmpe277.teamgoat.photoapp.dto.Album;
import edu.cmpe277.teamgoat.photoapp.dto.Image;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AlbumMongoRepository extends MongoRepository<Album, String> {

    
}
