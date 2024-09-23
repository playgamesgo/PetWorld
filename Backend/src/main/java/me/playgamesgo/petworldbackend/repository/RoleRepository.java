package me.playgamesgo.petworldbackend.repository;

import me.playgamesgo.petworldbackend.models.Role;
import me.playgamesgo.petworldbackend.models.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(Roles name);

    Boolean existsByName(Roles name);
}
