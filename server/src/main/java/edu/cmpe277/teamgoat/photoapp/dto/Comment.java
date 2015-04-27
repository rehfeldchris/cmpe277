package edu.cmpe277.teamgoat.photoapp.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Document
//@JsonIgnoreProperties({"_ID"})
public class Comment {

    @Id
    private String _ID;

    private String userId;

    private String comment;

    private String imageId;

    private Date timeStamp;

    public Comment() {

    }

    public Comment(String userId, String comment, String imageId, Date timeStamp) {
        this.userId = userId;
        this.comment = comment;
        this.imageId = imageId;
        this.timeStamp = timeStamp;
    }

    public String get_ID() {
        return _ID;
    }

    public String getUserId() {
        return userId;
    }

    public String getComment() {
        return comment;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public String getImageId() {
        return imageId;
    }
}
