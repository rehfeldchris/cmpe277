package edu.cmpe277.teamgoat.photoapp.repos;


import edu.cmpe277.teamgoat.photoapp.dto.Image;
import org.springframework.data.mongodb.repository.MongoRepository;

import javax.xml.stream.events.Comment;

public interface CommentMongoRepository extends MongoRepository<Comment, String> {

    
}
