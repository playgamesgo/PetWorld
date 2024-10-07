package me.playgamesgo.petworldbackend.payload.response;

import lombok.Getter;
import lombok.Setter;
import me.playgamesgo.petworldbackend.models.Photo;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Response payload for photo details.
 */
@Getter
@Setter
public final class PhotoResponse {
    private Long id;
    private String url;
    private String image;

    public static List<PhotoResponse> fromList(List<Photo> photos) {
        if (photos == null) {
            photos = Collections.emptyList();
        }
        return photos.stream().map(PhotoResponse::fromPhoto).collect(Collectors.toList());
    }

    private static PhotoResponse fromPhoto(Photo photo) {
        PhotoResponse photoResponse = new PhotoResponse();
        photoResponse.setId(photo.getId());
        photoResponse.setUrl(photo.getUrl());
        photoResponse.setImage(photo.getImage());
        return photoResponse;
    }
}