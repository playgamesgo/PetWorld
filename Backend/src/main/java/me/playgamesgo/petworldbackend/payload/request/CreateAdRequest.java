package me.playgamesgo.petworldbackend.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.relational.core.mapping.Embedded;

import java.util.List;

@Getter
@Setter
public final class CreateAdRequest {
    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @NotBlank
    private String type;

    @NotBlank
    private Float price;

    @NotBlank
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

    @NotBlank
    private List<String> images;
}
