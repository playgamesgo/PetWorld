package me.playgamesgo.petworldbackend.payload.response;

import lombok.Getter;
import lombok.Setter;
import me.playgamesgo.petworldbackend.models.Dictionary;

import java.util.ArrayList;
import java.util.List;

/**
 * Response payload for property definition details.
 */
@Getter
@Setter
public final class PropertyDefinitionResponse {
    private Long id;
    private String name;
    private Integer propertyDefinitionType;
    private Boolean isMandatory;
    private List<PropertyResponse> propertyValues = new ArrayList<>();

    public static PropertyDefinitionResponse fromPropertyDefinition(Dictionary propertyDefinition) {
        PropertyDefinitionResponse propertyDefinitionResponse = new PropertyDefinitionResponse();
        propertyDefinitionResponse.setId(propertyDefinition.getId());
        propertyDefinitionResponse.setName(propertyDefinition.getName());
        propertyDefinitionResponse.setPropertyDefinitionType(propertyDefinition.getPropertyDefinitionType());
        propertyDefinitionResponse.setIsMandatory(propertyDefinition.isMandatory());
        return propertyDefinitionResponse;
    }
}