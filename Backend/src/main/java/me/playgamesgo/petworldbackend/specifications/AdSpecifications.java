package me.playgamesgo.petworldbackend.specifications;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import me.playgamesgo.petworldbackend.models.Ad;
import me.playgamesgo.petworldbackend.models.PetProperty;
import org.springframework.data.jpa.domain.Specification;

public class AdSpecifications {

    public static Specification<Ad> isActive(Boolean isActive) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("isActive"), isActive);
    }

    public static Specification<Ad> locationEquals(String location) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("location"), location);
    }

    public static Specification<Ad> priceBetween(Integer minPrice, Integer maxPrice) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.between(root.get("price"), minPrice, maxPrice);
    }

    public static Specification<Ad> ageBetween(Integer minAge, Integer maxAge) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.between(root.get("age"), minAge, maxAge);
}

    public static Specification<Ad> petOriginEquals(Integer petOrigin) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("petOrigin"), petOrigin);
    }

    public static Specification<Ad> propertyEquals(String propertyName, String propertyValue) {
        return (root, query, criteriaBuilder) -> {
            Join<Ad, PetProperty> properties = root.join("properties", JoinType.INNER);
            return criteriaBuilder.and(
                    criteriaBuilder.equal(properties.get("propertyDefinition").get("name"), propertyName),
                    criteriaBuilder.equal(properties.get("predefinedValue").get("value"), propertyValue)
            );
        };
    }

    public static Specification<Ad> propertyExists(Long propertyDefinitionId) {
        return (root, query, criteriaBuilder) -> {
            Join<Ad, PetProperty> properties = root.join("properties", JoinType.INNER);
            return criteriaBuilder.equal(properties.get("propertyDefinition").get("id"), propertyDefinitionId);
        };
    }
}