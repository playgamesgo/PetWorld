package me.playgamesgo.petworldbackend.payload.request;

import lombok.Getter;
import lombok.Setter;
import me.playgamesgo.petworldbackend.models.PetProperty;
import me.playgamesgo.petworldbackend.models.Photo;
import org.springframework.data.relational.core.mapping.Embedded;

import java.util.List;

@Getter
@Setter
public final class UpdateAdRequest {
    @Embedded.Nullable
    private Boolean isActive;

    @Embedded.Nullable
    private Integer petOrigin;

    @Embedded.Nullable
    private String title;

    @Embedded.Nullable
    private Integer price;

    @Embedded.Nullable
    private String summary;

    @Embedded.Nullable
    private String location;

    @Embedded.Nullable
    private Integer age;

    @Embedded.Nullable
    private Integer ageUnits;

    @Embedded.Nullable
    private List<Photo> photos;

    @Embedded.Nullable
    private List<PetProperty> properties;
}