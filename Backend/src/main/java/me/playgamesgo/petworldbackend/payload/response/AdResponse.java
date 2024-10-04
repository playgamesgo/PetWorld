package me.playgamesgo.petworldbackend.payload.response;

import lombok.Getter;
import lombok.Setter;
import me.playgamesgo.petworldbackend.models.Ad;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class AdResponse {
    private Boolean isActive;
    private Integer petOrigin;
    private String title;
    private Integer price;
    private String summary;
    private String location;
    private Long id;
    private Date createdOn;
    private Date lastModifiedOn;
    private Integer age;
    private Integer ageUnits;
    private List<PhotoResponse> photos;
    private List<PetPropertyResponse> properties;
    private UserResponse appUser;

    public AdResponse(Boolean isActive, Integer petOrigin, String title, Integer price, String summary,
                      String location, Long id, Date createdOn, Date lastModifiedOn, Integer age, Integer ageUnits,
                      List<PhotoResponse> photos, List<PetPropertyResponse> properties, UserResponse appUser) {
        this.isActive = isActive;
        this.petOrigin = petOrigin;
        this.title = title;
        this.price = price;
        this.summary = summary;
        this.location = location;
        this.id = id;
        this.createdOn = createdOn;
        this.lastModifiedOn = lastModifiedOn;
        this.age = age;
        this.ageUnits = ageUnits;
        this.photos = photos;
        this.properties = properties;
        this.appUser = appUser;
    }

    public static ListAdResponse fromList(List<Ad> ads) {
        return new ListAdResponse(ads.stream().map(AdResponse::fromAd).collect(Collectors.toList()));
    }

    public static AdResponse fromAd(Ad ad) {
        return new AdResponse(ad.getIsActive(), ad.getPetOrigin(), ad.getTitle(), ad.getPrice(), ad.getSummary(),
                ad.getLocation(), ad.getId(), ad.getCreatedOn(), ad.getLastModifiedOn(), ad.getAge(), ad.getAgeUnits(),
                PhotoResponse.fromList(ad.getPhotos()), PetPropertyResponse.fromList(ad.getProperties()), UserResponse.fromUser(ad.getAppUser()));
    }
}