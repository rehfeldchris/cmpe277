package edu.cmpe277.teamgoat.photoapp.services;

import edu.cmpe277.teamgoat.photoapp.dto.Album;
import edu.cmpe277.teamgoat.photoapp.dto.Image;
import edu.cmpe277.teamgoat.photoapp.errors.BadApiRequestException;
import edu.cmpe277.teamgoat.photoapp.repos.AlbumMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
public class AlbumService {

    @Autowired
    AlbumMongoRepository repo;

    public Album createAlbum(String albumName, String ownerUserId, String description, List<String> grantedUserIds, boolean isPubliclyAccessible) throws BadApiRequestException {
        Objects.requireNonNull(albumName);
        Objects.requireNonNull(description);
        Objects.requireNonNull(ownerUserId);
        Objects.requireNonNull(grantedUserIds);
        assertAlbumDoesntAlreadyExistForThisUser(albumName, ownerUserId);

        Album album = new Album(albumName, ownerUserId, description, new ArrayList<>(grantedUserIds), Collections.<Image>emptyList(), isPubliclyAccessible);
        repo.save(album);
        return album;
    }

    public Album updateAlbum(String userId, String albumId, String albumName, String description, List<String> grantedUserIds, boolean isPubliclyAccessible) throws BadApiRequestException {
        Objects.requireNonNull(albumName);
        Objects.requireNonNull(description);
        Objects.requireNonNull(grantedUserIds);
        Album album = findAlbumOrThrow(albumId);
        assertAlbumCreatedByUser(album, userId);
        album.setName(albumName);
        album.setGrantedUserIds(grantedUserIds);
        album.setDescription(description);
        album.setIsPubliclyAccessible(isPubliclyAccessible);

        repo.save(album);
        return album;
    }

    public void addImagesToAlbum(String albumId, String requesterUserId, List<Image> images) throws BadApiRequestException {
        Objects.requireNonNull(albumId);
        Objects.requireNonNull(requesterUserId);
        Objects.requireNonNull(images);
        Album album = findAlbumOrThrow(albumId);
        assertAlbumCreatedByUser(album, requesterUserId);
    }

    public boolean deleteAlbum(String albumId, String requesterUserId) throws BadApiRequestException {
        Objects.requireNonNull(albumId);
        Objects.requireNonNull(requesterUserId);
        Album album = findAlbumOrThrow(albumId);
        assertAlbumCreatedByUser(album, requesterUserId);
        repo.delete(album);
        return true;
    }

    public void setUsersAbleToViewAlbum(String albumId, List<String> userIds) throws BadApiRequestException {
        Album album = findAlbumOrThrow(albumId);
        album.setGrantedUserIds(userIds);
    }

    public Album findAlbumOrThrow(String albumId) throws BadApiRequestException {
        Album album = repo.findBy_ID(albumId);
        if (album == null) {
            throw new BadApiRequestException(String.format(
                "album id '%s' not found",
                albumId
            ));
        }
        return album;
    }

    public Album findAlbumAndAssertViewableByUser(String albumId, String userId) throws BadApiRequestException {
        Album album = findAlbumOrThrow(albumId);
        if (!album.getGrantedUserIds().contains(userId)) {
            throw new BadApiRequestException(String.format(
                "album id '%s' not viewable by user id '%s',",
                album.get_ID(),
                userId
            ));
        }
        return album;
    }

    private void assertAlbumCreatedByUser(Album album, String userId) throws BadApiRequestException {
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
