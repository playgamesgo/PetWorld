package me.playgamesgo.petworldbackend.repository;

import me.playgamesgo.petworldbackend.models.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface PhotoRepository extends JpaRepository<Photo, Long> {
    @Transactional
    void deleteAllByAdId(Long adId);
}