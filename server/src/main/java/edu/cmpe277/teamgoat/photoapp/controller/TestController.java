package edu.cmpe277.teamgoat.photoapp.controller;

import edu.cmpe277.teamgoat.photoapp.services.AlbumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class TestController {

    @Autowired
    AlbumService albumService;

    @RequestMapping("/test")
    public Map<String, Object> greeting() throws Exception {
        //albumService.createAlbum("goats", "owner", "descrip");
        return new HashMap<>();
    }
}