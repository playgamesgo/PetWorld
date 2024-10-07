package me.playgamesgo.petworldbackend.repository;

import me.playgamesgo.petworldbackend.models.Property;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for Property entities.
 * <p>
 * Extends JpaRepository to provide CRUD operations.
 */
public interface PropertyRepository extends JpaRepository<Property, Long> {
}