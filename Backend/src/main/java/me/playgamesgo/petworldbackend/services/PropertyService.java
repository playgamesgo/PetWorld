package me.playgamesgo.petworldbackend.services;

import me.playgamesgo.petworldbackend.models.Property;
import me.playgamesgo.petworldbackend.repository.PropertyRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class for managing properties.
 */
@Service
public class PropertyService {
    private final PropertyRepository propertyRepository;

    public PropertyService(PropertyRepository propertyRepository) {
        this.propertyRepository = propertyRepository;
    }

    /**
     * Saves a list of properties.
     *
     * @param properties the list of Property entities to save.
     */
    public void saveAll(List<Property> properties) {
        propertyRepository.saveAll(properties);
    }
}