package me.playgamesgo.petworldbackend.repository;

import me.playgamesgo.petworldbackend.models.PetProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Repository interface for PetProperty entities.
 * <p>
 * Extends JpaRepository to provide CRUD operations.
 */
public interface PetPropertyRepository extends JpaRepository<PetProperty, Long> {
    @Transactional
    void deleteAllByAdId(Long adId);
}