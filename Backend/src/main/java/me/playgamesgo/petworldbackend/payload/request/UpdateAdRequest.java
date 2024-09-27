package me.playgamesgo.petworldbackend.payload.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.relational.core.mapping.Embedded;

import java.util.List;

@Getter
@Setter
public final class UpdateAdRequest {
    @Embedded.Nullable
    private String title;

    @Embedded.Nullable
    private String description;

    @Embedded.Nullable
    private String type;

    @Embedded.Nullable
    private Float price;

    @Embedded.Nullable
    private String location;

    @Embedded.Nullable
    private Integer age;

    @Embedded.Nullable
    private String breed;

    @Embedded.Nullable
    private String gender;

    @Embedded.Nullable
    private Boolean vaccinated;

    @Embedded.Nullable
    private String health;

    @Embedded.Nullable
    private String color;

    @Embedded.Nullable
    private String size;

    @Embedded.Nullable
    private String furLength;

    @Embedded.Nullable
    private List<String> images;
}
