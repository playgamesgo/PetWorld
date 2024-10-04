package me.playgamesgo.petworldbackend.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.playgamesgo.petworldbackend.models.Dictionary;
import me.playgamesgo.petworldbackend.models.Property;
import me.playgamesgo.petworldbackend.services.DictionaryService;
import me.playgamesgo.petworldbackend.services.PropertyService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DataLoader implements CommandLineRunner {
    private final DictionaryService dictionaryService;
    private final PropertyService propertyService;

    public DataLoader(DictionaryService dictionaryService, PropertyService propertyService) {
        this.dictionaryService = dictionaryService;
        this.propertyService = propertyService;
    }

    @Override
    public void run(String... args) throws Exception {
        if (dictionaryService.getAllDictionaries().isEmpty()) {
            importData();
        }
    }

    private void importData() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<List<Dictionary>> typeReference = new TypeReference<>() {};
        InputStream inputStream = new ClassPathResource("dictionary.json").getInputStream();
        List<Dictionary> dictionaries = mapper.readValue(inputStream, typeReference);

        // Save dictionaries first
        dictionaryService.saveAll(dictionaries);

        // Create a map to store properties by their ID
        Map<Long, Property> propertyMap = new HashMap<>();
        for (Dictionary dictionary : dictionaries) {
            for (Property property : dictionary.getPropertyValues()) {
                propertyMap.put(property.getId(), property);
            }
        }

        // Set parent properties
        for (Dictionary dictionary : dictionaries) {
            for (Property property : dictionary.getPropertyValues()) {
                if (property.getParentPropertyValue() != null) {
                    Property parent = propertyMap.get(property.getParentPropertyValue().getId());
                    property.setParentPropertyValue(parent);
                }
            }
        }

        // Save properties with parent relationships
        for (Dictionary dictionary : dictionaries) {
            propertyService.saveAll(dictionary.getPropertyValues());
        }
    }
}