package me.playgamesgo.petworldbackend.repository;

import me.playgamesgo.petworldbackend.models.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Repository interface for Photo entities.
 * <p>
 * Extends JpaRepository to provide CRUD operations.
 */
public interface PhotoRepository extends JpaRepository<Photo, Long> {

    /**
     * Deletes all Photo entities associated with the given Ad ID.
     *
     * @param adId the ID of the Ad whose associated Photo entities are to be deleted.
     */
    @Transactional
    void deleteAllByAdId(Long adId);
}