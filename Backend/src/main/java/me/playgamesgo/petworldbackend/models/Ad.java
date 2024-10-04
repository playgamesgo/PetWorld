package me.playgamesgo.petworldbackend.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "ads")
public class Ad {
    @Id
    @GeneratedValue
    private Long id;

    private Date createdOn = new Date();

    private Date lastModifiedOn;

    @JsonProperty("isActive")
    private Boolean isActive = true;

    private Integer petOrigin;

    private String title;

    private String summary;

    private Integer price;

    private String location;

    private Integer age;

    private Integer ageUnits;

    @OneToMany(mappedBy = "ad")
    private List<Photo> photos;

    @OneToMany
    @JoinColumn(name = "properties_id")
    private List<PetProperty> properties;

    @ManyToOne
    @JoinColumn(name = "app_user_id")
    private User appUser;
}