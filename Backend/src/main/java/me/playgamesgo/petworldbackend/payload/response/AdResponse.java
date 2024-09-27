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
    long id;
    UserResponse user;
    String title;
    String description;
    String type;
    Float price;
    String location;
    Integer age;
    String breed;
    String gender;
    Boolean vaccinated;
    String health;
    String color;
    String size;
    String furLength;
    List<String> images;
    Boolean active;
    Date createdAt;

    public AdResponse(long id, UserResponse user, String title, String description, String type, Float price, String location,
                      Integer age, String breed, String gender, Boolean vaccinated, String health,
                      String color, String size, String furLength, List<String> images, Boolean active, Date createdAt) {
        this.id = id;
        this.user = user;
        this.title = title;
        this.description = description;
        this.type = type;
        this.price = price;
        this.location = location;
        this.age = age;
        this.breed = breed;
        this.gender = gender;
        this.vaccinated = vaccinated;
        this.health = health;
        this.color = color;
        this.size = size;
        this.furLength = furLength;
        this.images = images;
        this.active = active;
        this.createdAt = createdAt;
    }

    public static List<AdResponse> fromList(List<Ad> ads) {
        return ads.stream().map(AdResponse::fromAd).collect(Collectors.toList());
    }

    public static AdResponse fromAd(Ad ad) {
        return new AdResponse(ad.getId(),
                new UserResponse(ad.getUser().getId(), ad.getUser().getFirstName(), ad.getUser().getLastName(),
                        ad.getUser().getEmail(), ad.getUser().getLocation(), ad.getUser().getCreatedAt()),
                ad.getTitle(), ad.getDescription(), ad.getType(), ad.getPrice(), ad.getLocation(),
                ad.getAge(), ad.getBreed(), ad.getGender(), ad.isVaccinated(), ad.getHealth(),
                ad.getColor(), ad.getSize(), ad.getFurLength(), ad.getImages(), ad.isActive(), ad.getCreatedAt());
    }
}
