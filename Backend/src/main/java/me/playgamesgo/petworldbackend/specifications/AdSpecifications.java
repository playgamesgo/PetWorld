package me.playgamesgo.petworldbackend.specifications;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import me.playgamesgo.petworldbackend.models.Ad;
import me.playgamesgo.petworldbackend.models.PetProperty;
import org.springframework.data.jpa.domain.Specification;

/**
 * Class containing specifications for querying Ad entities.
 */
public class AdSpecifications {

    /**
     * Specification to filter ads by their active status.
     *
     * @param isActive the active status to filter by.
     * @return a Specification for filtering ads by active status.
     */
    public static Specification<Ad> isActive(Boolean isActive) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("isActive"), isActive);
    }

    /**
     * Specification to filter ads by location.
     *
     * @param location the location to filter by.
     * @return a Specification for filtering ads by location.
     */
    public static Specification<Ad> locationEquals(String location) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("location"), location);
    }

    /**
     * Specification to filter ads by price range.
     *
     * @param minPrice the minimum price.
     * @param maxPrice the maximum price.
     * @return a Specification for filtering ads by price range.
     */
    public static Specification<Ad> priceBetween(Integer minPrice, Integer maxPrice) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.between(root.get("price"), minPrice, maxPrice);
    }

    /**
     * Specification to filter ads by age range.
     *
     * @param minAge the minimum age.
     * @param maxAge the maximum age.
     * @return a Specification for filtering ads by age range.
     */
    public static Specification<Ad> ageBetween(Integer minAge, Integer maxAge) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.between(root.get("age"), minAge, maxAge);
    }

    /**
     * Specification to filter ads by pet origin.
     *
     * @param petOrigin the pet origin to filter by.
     * @return a Specification for filtering ads by pet origin.
     */
    public static Specification<Ad> petOriginEquals(Integer petOrigin) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("petOrigin"), petOrigin);
    }

    /**
     * Specification to filter ads by a specific property.
     *
     * @param propertyName the name of the property.
     * @param propertyValue the value of the property.
     * @return a Specification for filtering ads by a specific property.
     */
    public static Specification<Ad> propertyEquals(String propertyName, String propertyValue) {
        return (root, query, criteriaBuilder) -> {
            Join<Ad, PetProperty> properties = root.join("properties", JoinType.INNER);
            return criteriaBuilder.and(
                    criteriaBuilder.equal(properties.get("propertyDefinition").get("name"), propertyName),
                    criteriaBuilder.equal(properties.get("predefinedValue").get("value"), propertyValue)
            );
        };
    }

    /**
     * Specification to filter ads by the existence of a specific property.
     *
     * @param propertyDefinitionId the ID of the property definition.
     * @return a Specification for filtering ads by the existence of a specific property.
     */
    public static Specification<Ad> propertyExists(Long propertyDefinitionId) {
        return (root, query, criteriaBuilder) -> {
            Join<Ad, PetProperty> properties = root.join("properties", JoinType.INNER);
            return criteriaBuilder.equal(properties.get("propertyDefinition").get("id"), propertyDefinitionId);
        };
    }
}