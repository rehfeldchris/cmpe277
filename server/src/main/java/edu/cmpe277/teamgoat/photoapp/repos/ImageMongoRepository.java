package edu.cmpe277.teamgoat.photoapp.repos;


import edu.cmpe277.teamgoat.photoapp.dto.Album;
import edu.cmpe277.teamgoat.photoapp.dto.Image;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ImageMongoRepository extends MongoRepository<Image, String> {

	Image findByImageId(String image_id);

	@Query("{location: {$near: {$geometry: {type: \"Point\", coordinates: [?0, ?1]}, $maxDistance: ?2, $minDistance: 0}}}")
	List<Album> findImagesNearLocation(double lat, double lon, double maxDistance);
}
