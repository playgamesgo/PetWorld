package me.playgamesgo.petworldbackend.repository;

import me.playgamesgo.petworldbackend.models.Ad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Repository interface for Ad entities.
 * <p>
 * Extends JpaRepository to provide CRUD operations and
 * JpaSpecificationExecutor to support query by specification.
 */
public interface AdRepository extends JpaRepository<Ad, Long>, JpaSpecificationExecutor<Ad> {
}