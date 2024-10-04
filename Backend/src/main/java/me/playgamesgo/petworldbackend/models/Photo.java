package me.playgamesgo.petworldbackend.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "photos")
public class Photo {
    @Id
    @GeneratedValue
    private Long id;

    private String url;

    private String image;

    @ManyToOne
    @JoinColumn(name = "ad_id")
    private Ad ad;
}