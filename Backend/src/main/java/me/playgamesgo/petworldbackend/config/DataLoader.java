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

    @Override
    public void run(String... args) throws Exception {
        for (Roles roleEnum : Roles.values()) {
            if (!roleRepository.existsByName(roleEnum)) {
                Role role = new Role(roleEnum);
                roleRepository.save(role);
            }
        }

        if (dictionaryService.getAllDictionaries().isEmpty()) {
            importDictionary();
        }

        importAds();
    }

    private void importDictionary() throws IOException {
        if (!new ClassPathResource("dictionary.json").exists()) {
            log.warn("Dictionary file not found, skipping import");
            return;
        }

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

    private void importAds() {
        if (!new ClassPathResource("data.json").exists()) {
            log.warn("Data file not found, skipping import");
            return;
        }

        Optional<User> user = userService.findById(1L);

        if (user.isEmpty()) {
            User newUser = new User("HenSh", "Hen", "Sh",
                    "hensh-mail@gmail.com", "+380507153061", "Дніпро", encoder.encode("jtqzsE5eGQiRrWxhw8PnLh6#"));
            Role userRole = roleRepository.findByName(Roles.USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            newUser.setRole(userRole);
            userRepository.save(newUser);
        }

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