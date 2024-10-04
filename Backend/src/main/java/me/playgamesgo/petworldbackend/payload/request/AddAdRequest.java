package me.playgamesgo.petworldbackend.payload.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import me.playgamesgo.petworldbackend.models.Photo;
import me.playgamesgo.petworldbackend.models.PetProperty;

import java.util.List;

@Getter
@Setter
public final class AddAdRequest {
    @NotNull
    private Boolean isActive;

    @NotNull
    private Integer petOrigin;

    @NotBlank
    private String title;

    @NotNull
    private Integer price;

    @NotBlank
    private String summary;

    @NotBlank
    private String location;

    @NotNull
    private Integer age;

    @NotNull
    private Integer ageUnits;

    @NotEmpty
    private List<Photo> photos;

    @NotEmpty
    private List<PetProperty> properties;
}