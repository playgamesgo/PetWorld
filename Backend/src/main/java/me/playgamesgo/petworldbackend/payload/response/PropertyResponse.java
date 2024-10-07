package me.playgamesgo.petworldbackend.payload.response;

import lombok.Getter;
import lombok.Setter;
import me.playgamesgo.petworldbackend.models.Property;

/**
 * Response payload for property details.
 */
@Getter
@Setter
public final class PropertyResponse {
    private Long id;
    private String value;
    private PropertyResponse parentPropertyValue;

    public static PropertyResponse fromProperty(Property property) {
        PropertyResponse propertyResponse = new PropertyResponse();
        if (property == null) return null;
        propertyResponse.setId(property.getId());
        propertyResponse.setValue(property.getValue());
        if (property.getParentPropertyValue() != null) {
            propertyResponse.setParentPropertyValue(fromProperty(property.getParentPropertyValue()));
        }
        return propertyResponse;
    }
}