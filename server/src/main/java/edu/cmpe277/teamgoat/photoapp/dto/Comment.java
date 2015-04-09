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

    private Date timeStamp;

    public Comment() {

    }

    public Comment(String _ID, String userId, String comment, Date timeStamp) {
        this._ID = _ID;
        this.userId = userId;
        this.comment = comment;
        this.timeStamp = timeStamp;
    }

    public String get_ID() {
        return _ID;
    }

    public void set_ID(String _ID) {
        this._ID = _ID;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }
}
