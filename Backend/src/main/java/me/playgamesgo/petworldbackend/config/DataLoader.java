package me.playgamesgo.petworldbackend.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import me.playgamesgo.petworldbackend.models.*;
import me.playgamesgo.petworldbackend.payload.request.AddAdRequest;
import me.playgamesgo.petworldbackend.repository.RoleRepository;
import me.playgamesgo.petworldbackend.repository.UserRepository;
import me.playgamesgo.petworldbackend.services.AdService;
import me.playgamesgo.petworldbackend.services.DictionaryService;
import me.playgamesgo.petworldbackend.services.PropertyService;
import me.playgamesgo.petworldbackend.services.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * DataLoader is a component that initializes the database with default data.
 * It implements CommandLineRunner to execute the data loading logic on application startup.
 */
@Slf4j
@Component
public class DataLoader implements CommandLineRunner {
    private final DictionaryService dictionaryService;
    private final PropertyService propertyService;
    private final UserService userService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AdService adService;
    private final PasswordEncoder encoder;

    public DataLoader(DictionaryService dictionaryService, PropertyService propertyService, UserService userService,
                      UserRepository userRepository, RoleRepository roleRepository, AdService adService, PasswordEncoder encoder) {
        this.dictionaryService = dictionaryService;
        this.propertyService = propertyService;
        this.userService = userService;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.adService = adService;
        this.encoder = encoder;
    }

    /**
     * Runs the data loading logic on application startup.
     *
     * @param args command line arguments
     * @throws Exception if an error occurs during data loading
     */
    @Override
    public void run(String... args) throws Exception {
        // Create default roles if they don't exist
        for (Roles roleEnum : Roles.values()) {
            if (!roleRepository.existsByName(roleEnum)) {
                Role role = new Role(roleEnum);
                roleRepository.save(role);
            }
        }

        // Import dictionaries and ads
        if (dictionaryService.getAllDictionaries().isEmpty()) {
            importDictionary();
        }

        importAds();
    }

    /**
     * Imports dictionary data from a JSON file.
     *
     * @throws IOException if an error occurs during file reading
     */
    private void importDictionary() throws IOException {
        // Check if the dictionary file exists
        if (!new ClassPathResource("dictionary.json").exists()) {
            log.warn("Dictionary file not found, skipping import");
            return;
        }

        // Check if dictionaries already exist
        if (!dictionaryService.getAllDictionaries().isEmpty()) return;

        // Read dictionaries from the JSON file
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

    /**
     * Imports ad data from a JSON file.
     */
    private void importAds() {
        // Check if the data file exists
        if (!new ClassPathResource("data.json").exists()) {
            log.warn("Data file not found, skipping import");
            return;
        }

        // Check if ads already exist
        if (!adService.findAll().isEmpty()) return;

        // Create a default user if it doesn't exist
        Optional<User> user = userService.findById(1L);

        if (user.isEmpty()) {
            User newUser = new User("HenSh", "Hen", "Sh",
                    "hensh-mail@gmail.com", "+380507153061", "Дніпро", encoder.encode("jtqzsE5eGQiRrWxhw8PnLh6#"));
            Role userRole = roleRepository.findByName(Roles.USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            newUser.setRole(userRole);
            userRepository.save(newUser);
        }

        // Read ads from the JSON file
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<List<AddAdRequest>> typeReference = new TypeReference<>() {};
        try {
            InputStream inputStream = new ClassPathResource("data.json").getInputStream();
            List<AddAdRequest> ads = mapper.readValue(inputStream, typeReference);

            user = userService.findById(1L);
            if (user.isEmpty()) {
                log.error("Unable to import ads: User not found");
                return;
            }

            for (AddAdRequest ad : ads) {
                adService.createAd(ad, user.get());
            }
        } catch (IOException e) {
            log.error("Unable to import ads: {}", e.getMessage());
        }
    }
}