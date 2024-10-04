package me.playgamesgo.petworldbackend.repository;

import me.playgamesgo.petworldbackend.models.Property;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PropertyRepository extends JpaRepository<Property, Long> {
}