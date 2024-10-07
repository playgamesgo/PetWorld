package me.playgamesgo.petworldbackend.specifications;

import me.playgamesgo.petworldbackend.models.Ad;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class to parse the filter string and create specifications for querying Ad entities.
 */
public class FilterParser {

    /**
     * Parses the filter string and creates a Specification for querying Ad entities.
     *
     * @param filter the filter string to parse.
     * @return a Specification for querying Ad entities.
     */
    public static Specification<Ad> parseFilter(String filter) {
        // Create an empty specification
        Specification<Ad> spec = Specification.where(null);

        // If the filter is null or empty, return null
        if (filter == null || filter.isEmpty()) {
            return null;
        }

        // Initialize the min and max price and age to null
        Integer minPrice = null;
        Integer maxPrice = null;
        Integer minAge = null;
        Integer maxAge = null;

        //Split the filter string by 'and' to get the individual filters
        List<String> filters = splitFilter(filter);

        //Iterate over the filters
        for (String f : filters) {
            //Check if the filter contains 'IsActive eq'
            if (f.contains("IsActive eq")) {
                //If it does, parse the boolean value and add the specification to the spec
                spec = spec.and(AdSpecifications.isActive(Boolean.parseBoolean(f.split("IsActive eq ")[1])));
            }

            //Check if the filter contains 'Location eq'
            if (f.contains("Location eq")) {
                //If it does, parse the location value and add the specification to the spec
                spec = spec.and(AdSpecifications.locationEquals(f.split("Location eq ")[1].replace("'", "")));
            }

            //Check if the filter contains 'Price'
            if (f.contains("Price ge")) {
                minPrice = Integer.parseInt(f.split("Price ge ")[1]);
            }
            if (f.contains("Price le")) {
                maxPrice = Integer.parseInt(f.split("Price le ")[1]);
            }

            //Check if the filter contains 'Age'
            if (f.contains("age ge")) {
                minAge = Integer.parseInt(f.split("age ge ")[1]);
            }
            if (f.contains("age le")) {
                maxAge = Integer.parseInt(f.split("age le ")[1]);
            }

            //petOrigin eq ItMarathon.Dal.Enums.PetOrigin'3'
            if (f.contains("PetOrigin eq")) {
                //If it does, parse the pet origin value and add the specification to the spec
                spec = spec.and(AdSpecifications.petOriginEquals(Integer.parseInt(f.split("PetOrigin eq ")[1].replace("'", "").replace("ItMarathon.Dal.Enums.PetOrigin", ""))));
            }

            //Check if the filter contains 'Properties/any(p:p/PropertyDefinition/Name eq'
            if (f.contains("Properties/any(p:p/PropertyDefinition/Name eq")) {
                //If it does, parse the property name and value and add the specification to the spec
                String propertyName = f.split("Properties/any\\(p:p/PropertyDefinition/Name eq '")[1]
                        .split("' and p/PredefinedValue/Value eq '")[0];
                String propertyValue = f.split("' and p/PredefinedValue/Value eq '")[1].split("'\\)")[0];
                spec = spec.and(AdSpecifications.propertyEquals(propertyName, propertyValue));
            }

            //Check if the filter contains 'Properties/any(p:p/PropertyDefinition/Id eq'
            if (f.contains("Properties/any(p:p/PropertyDefinition/Id eq")) {
                //If it does, parse the property definition id and add the specification to the spec
                Long propertyDefinitionId = Long.parseLong(f.split("Properties/any\\(p:p/PropertyDefinition/Id eq ")[1].split("\\)")[0]);
                spec = spec.and(AdSpecifications.propertyExists(propertyDefinitionId));
            }
        }

        // Add the price and age specifications to the spec
        if (minPrice != null && maxPrice != null) {
            spec = spec.and(AdSpecifications.priceBetween(minPrice, maxPrice));
        } else if (minPrice != null) {
            spec = spec.and(AdSpecifications.priceBetween(minPrice, Integer.MAX_VALUE));
        } else if (maxPrice != null) {
            spec = spec.and(AdSpecifications.priceBetween(0, maxPrice));
        }

        // Add the age specifications to the spec
        if (minAge != null && maxAge != null) {
            spec = spec.and(AdSpecifications.ageBetween(minAge, maxAge));
        } else if (minAge != null) {
            spec = spec.and(AdSpecifications.ageBetween(minAge, Integer.MAX_VALUE));
        } else if (maxAge != null) {
            spec = spec.and(AdSpecifications.ageBetween(0, maxAge));
        }

        return spec;
    }

    /**
     * Splits the filter string by 'and' to get the individual filters.
     *
     * @param filter the filter string to split.
     * @return a list of individual filters.
     */
    private static List<String> splitFilter(String filter) {
        // Create a list to store the individual filters
        List<String> result = new ArrayList<>();
        // Create a pattern to match ' and ' that is not inside parentheses
        Pattern pattern = Pattern.compile(" and (?![^()]*\\))");
        Matcher matcher = pattern.matcher(filter);
        int lastEnd = 0;
        while (matcher.find()) {
            result.add(filter.substring(lastEnd, matcher.start()));
            lastEnd = matcher.end();
        }
        result.add(filter.substring(lastEnd));
        return result;
    }
}