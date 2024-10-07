package me.playgamesgo.petworldbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main class for the PetWorldBackend application.
 * This is the entry point for the Spring Boot application.
 */
@SpringBootApplication
public class PetWorldBackendApplication {

    /**
     * Main method to run the Spring Boot application.
     *
     * @param args command-line arguments passed to the application.
     */
    public static void main(String[] args) {
        SpringApplication.run(PetWorldBackendApplication.class, args);
    }
}
