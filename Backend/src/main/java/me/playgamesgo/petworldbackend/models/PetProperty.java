package me.playgamesgo.petworldbackend.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.relational.core.mapping.Embedded;

@Entity
@Getter
@Setter
@Table(name = "pet_properties")
public class PetProperty {
    @Id
    @GeneratedValue
    private Long id;

    @Embedded.Nullable
    private String customValue = "customValue";

    @ManyToOne
    @JoinColumn(name = "property_definition_id")
    private Dictionary propertyDefinition;

    @ManyToOne
    @JoinColumn(name = "predefined_value_id")
    private Property predefinedValue;

    @ManyToOne
    @JoinColumn(name = "ad_id")
    private Ad ad;
}
