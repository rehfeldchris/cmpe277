package edu.cmpe277.teamgoat.photoapp.model;


import java.util.Date;

public class Comment {

    private String _ID;

    private String userId;

    private String comment;

    private String imageId;

    private Date timeStamp;

    private String userName;

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

    public String getUserName() {
        return userName;
    }
}