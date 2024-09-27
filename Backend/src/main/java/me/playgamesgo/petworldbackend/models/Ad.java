package me.playgamesgo.petworldbackend.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "ads")
public final class Ad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotBlank
    private User user;

    @NotBlank
    @Size(min = 8, max = 128)
    private String title;

    @NotBlank
    @Size(min = 64, max = 2048)
    private String description;

    @NotBlank
    @Size(min = 1, max = 128)
    private String type;

    @NotBlank
    private float price;

    @NotBlank
    private String location;

    private int age;

    private String breed;

    private String gender;

    private boolean vaccinated;

    private String health;

    private String color;

    private String size;

    private String furLength;

    @ElementCollection
    @CollectionTable(name = "ad_images", joinColumns = @JoinColumn(name = "ad_id"))
    @Column(name = "image_base64")
    @NotBlank
    private List<String> images;

    private boolean active = true;

    private Date createdAt = new Date();

    public Ad(User user, String title, String description, String type, float price, String location,
              int age, String breed, String gender, boolean vaccinated, String health,
              String color, String size, String furLength, List<String> images) {
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
    }

    public Ad() {

    }
}