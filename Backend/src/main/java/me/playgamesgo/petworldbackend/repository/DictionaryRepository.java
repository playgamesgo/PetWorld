package me.playgamesgo.petworldbackend.repository;

import me.playgamesgo.petworldbackend.models.Dictionary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Dictionary entities.
 * <p>
 * Extends JpaRepository to provide CRUD operations.
 */
@Repository
public interface DictionaryRepository extends JpaRepository<Dictionary, Long> {
}