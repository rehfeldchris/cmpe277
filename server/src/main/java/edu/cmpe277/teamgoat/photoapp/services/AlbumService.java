package edu.cmpe277.teamgoat.photoapp.services;

import edu.cmpe277.teamgoat.photoapp.dto.Album;
import edu.cmpe277.teamgoat.photoapp.dto.Image;
import edu.cmpe277.teamgoat.photoapp.errors.BadApiRequestException;
import edu.cmpe277.teamgoat.photoapp.repos.AlbumMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class AlbumService {

    @Autowired
    AlbumMongoRepository repo;

    public boolean createAlbum(String albumName, String ownerUserId) throws Exception {
        Objects.requireNonNull(albumName, ownerUserId);
        assertAlbumDoesntAlreadyExistForThisUser(albumName, ownerUserId);

        Album album = new Album(albumName, ownerUserId, new String[] {},  new Image[] {});
        repo.save(album);
        return true;
    }

    public boolean deleteAlbum(String albumId, String requesterUserId) {
        return true;
    }

    private void assertAlbumDoesntAlreadyExistForThisUser(String albumName, String ownerUserId) throws Exception {
        List<Album> matches = repo.findByOwnerIdAndName(ownerUserId, albumName);
        if (matches.size() > 0) {
            throw new BadApiRequestException(String.format(
                    "album name '%s' already exists",
                    albumName
            ));
        }
    }
}
