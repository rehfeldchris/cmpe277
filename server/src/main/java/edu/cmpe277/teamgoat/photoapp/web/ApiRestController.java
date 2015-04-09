package edu.cmpe277.teamgoat.photoapp.web;

import edu.cmpe277.teamgoat.photoapp.dto.Album;
import edu.cmpe277.teamgoat.photoapp.errors.MissingUserInformation;
import edu.cmpe277.teamgoat.photoapp.services.PhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/{userId}")
public class ApiRestController {

    @Autowired
    private PhotoService photoService;

    @RequestMapping(value = "/albums", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Album> getListOfAlbums(
            @PathVariable("userId") String facebookUserId
    ) throws MissingUserInformation {
        if (facebookUserId == null) {
            throw new MissingUserInformation();
        }

        return null;
    }


}
