package edu.cmpe277.teamgoat.photoapp.dto;

import org.springframework.data.annotation.Id;

import java.util.Date;

public class User {

    @Id
    private String facebookUserId;

    private String name;

    private String profilePhotoUrl;

    private Date whenDataFetchedFromFacebook;


}
