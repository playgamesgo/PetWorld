package me.playgamesgo.petworldbackend.services;

import me.playgamesgo.petworldbackend.models.Property;
import me.playgamesgo.petworldbackend.repository.PropertyRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PropertyService {
    private final PropertyRepository propertyRepository;

    public PropertyService(PropertyRepository propertyRepository) {
        this.propertyRepository = propertyRepository;
    }

    public void saveAll(List<Property> properties) {
        propertyRepository.saveAll(properties);
    }
}