package edu.cmpe277.teamgoat.photoapp.repos;


import edu.cmpe277.teamgoat.photoapp.dto.Image;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ImageMongoRepository extends MongoRepository<Image, String> {

	Image findBy_ID(String imageId);

	@Query("{location: {$near: {$geometry: {type: \"Point\", coordinates: [?1, ?0]}, $maxDistance: ?2, $minDistance: 0}}}")
	List<Image> findImagesNearLocation(double lat, double lon, double maxDistance);
}
