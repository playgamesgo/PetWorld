package me.playgamesgo.petworldbackend.config;

import me.playgamesgo.petworldbackend.models.Role;
import me.playgamesgo.petworldbackend.models.Roles;
import me.playgamesgo.petworldbackend.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public final class DataInitializer implements CommandLineRunner {
    private final RoleRepository roleRepository;

    @Autowired
    public DataInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) {
        for (Roles roleEnum : Roles.values()) {
            if (!roleRepository.existsByName(roleEnum)) {
                Role role = new Role(roleEnum);
                roleRepository.save(role);
            }
        }
    }
}