package edu.cmpe277.teamgoat.photoapp.repos;


import edu.cmpe277.teamgoat.photoapp.dto.Album;
import edu.cmpe277.teamgoat.photoapp.dto.Image;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mongodb.BasicDBObject;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface AlbumMongoRepository extends MongoRepository<Album, String> {

	Album findBy_ID(String albumId);
    List<Album> findByOwnerId(String ownerId);
    List<Album> findByOwnerIdIgnoreCase(String ownerId);
    List<Album> findByOwnerIdAndName(String ownerId, String name);
    List<Album> findByGrantedUserIdsOrIsPubliclyAccessibleIsTrue(String userId);

    @Query("{$or: [{grantedUserIds: ?0}, {isPubliclyAccessible: true}]}")
    List<Album> findViewable(String userId);

    @Query("{$and: [{$or: [{grantedUserIds: ?0}, {isPubliclyAccessible: true}]},{$near: {$geometry: {type: \"Point\", coordinates: [?1, ?2]}, $maxDistance: ?3, $minDistance: 0}}]}")
    List<Album> findViewableImagesNearLocation(String userId, double lat, double lon, double maxDistance);
}
