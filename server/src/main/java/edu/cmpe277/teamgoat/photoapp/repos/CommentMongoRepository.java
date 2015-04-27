package edu.cmpe277.teamgoat.photoapp.repos;


import edu.cmpe277.teamgoat.photoapp.dto.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;



public interface CommentMongoRepository extends MongoRepository<Comment, String> {

    
}
