package me.playgamesgo.petworldbackend.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a property for dictionary values.
 */
@Entity
@Getter
@Setter
@Table(name = "property")
public final class Property {
    @Id
    @GeneratedValue
    private Long id;

    @NotBlank
    private String value;

    @ManyToOne
    @JoinColumn(name = "parent_property_id")
    private Property parentPropertyValue;
}