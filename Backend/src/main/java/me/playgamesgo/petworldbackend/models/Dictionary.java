package me.playgamesgo.petworldbackend.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "dictionary")
public final class Dictionary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    private int propertyDefinitionType;

    @JsonProperty("isMandatory")
    private boolean mandatory = false;

    private String category;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "dictionary_id")
    private List<Property> propertyValues = new ArrayList<>();
}
