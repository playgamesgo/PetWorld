package me.playgamesgo.petworldbackend.payload.response;

import lombok.Getter;
import lombok.Setter;
import me.playgamesgo.petworldbackend.models.PetProperty;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Response payload for pet property details.
 */
@Getter
@Setter
public final class PetPropertyResponse {
    private Long id;
    private String customValue;
    private PropertyDefinitionResponse propertyDefinition;
    private PropertyResponse predefinedValue;

    public PetPropertyResponse(Long id, String customValue, PropertyDefinitionResponse propertyDefinition, PropertyResponse predefinedValue) {
        this.id = id;
        this.customValue = customValue;
        this.propertyDefinition = propertyDefinition;
        this.predefinedValue = predefinedValue;
    }

    public static List<PetPropertyResponse> fromList(List<PetProperty> properties) {
        if (properties == null) {
            properties = Collections.emptyList();
        }
        return properties.stream().map(PetPropertyResponse::fromProperty).collect(Collectors.toList());
    }

    public static PetPropertyResponse fromProperty(PetProperty property) {
        return new PetPropertyResponse(property.getId(), property.getCustomValue(),
                PropertyDefinitionResponse.fromPropertyDefinition(property.getPropertyDefinition()),
                PropertyResponse.fromProperty(property.getPredefinedValue()));
    }
}