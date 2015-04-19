package edu.cmpe277.teamgoat.photoapp.services;

import edu.cmpe277.teamgoat.photoapp.dto.Album;
import edu.cmpe277.teamgoat.photoapp.dto.Image;
import edu.cmpe277.teamgoat.photoapp.errors.BadApiRequestException;
import edu.cmpe277.teamgoat.photoapp.repos.AlbumMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
public class AlbumService {

    @Autowired
    AlbumMongoRepository repo;

    public Album createAlbum(String albumName, String ownerUserId, String description) throws BadApiRequestException {
        Objects.requireNonNull(albumName);
        Objects.requireNonNull(description);
        Objects.requireNonNull(ownerUserId);
        assertAlbumDoesntAlreadyExistForThisUser(albumName, ownerUserId);

        Album album = new Album(albumName, ownerUserId, description, Collections.<String>emptyList(), Collections.<Image>emptyList());
        repo.save(album);
        return album;
    }

    public void addImagesToAlbum(String albumId, String requesterUserId, List<Image> images) throws BadApiRequestException {
        Objects.requireNonNull(albumId);
        Objects.requireNonNull(requesterUserId);
        Objects.requireNonNull(images);
        Album album = findAlbumOrThrow(albumId);
        assertAlbumCreatedByuser(album, requesterUserId);



    }

    public boolean deleteAlbum(String albumId, String requesterUserId) throws BadApiRequestException {
        Objects.requireNonNull(albumId);
        Objects.requireNonNull(requesterUserId);
        Album album = findAlbumOrThrow(albumId);
        assertAlbumCreatedByuser(album, requesterUserId);
        repo.delete(album);
        return true;
    }

    public void setUsersAbleToViewAlbum(String albumId, List<String> userIds) throws BadApiRequestException {
        Album album = findAlbumOrThrow(albumId);
        album.setGrantedUserIds(userIds);
    }

    private Album findAlbumOrThrow(String albumId) throws BadApiRequestException {
        Album album = repo.findBy_ID(albumId);
        if (album == null) {
            throw new BadApiRequestException(String.format(
                "album id '%s' not found",
                albumId
            ));
        }
        return album;
    }

    private void assertAlbumCreatedByuser(Album album, String userId) throws BadApiRequestException {
        if (!album.getOwnerId().equals(userId)) {
            throw new BadApiRequestException(String.format(
                "album id '%s' with name '%s' not owned by user id '%s', so cant be modified or deleted",
                album.get_ID(),
                album.getName(),
                userId
            ));
        }
    }

    private void assertAlbumDoesntAlreadyExistForThisUser(String albumName, String ownerUserId) throws BadApiRequestException {
        List<Album> matches = repo.findByOwnerIdAndName(ownerUserId, albumName);
        if (matches.size() > 0) {
            throw new BadApiRequestException(String.format(
                "album name '%s' already exists",
                albumName
            ));
        }
    }
}
