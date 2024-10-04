package me.playgamesgo.petworldbackend.repository;

import me.playgamesgo.petworldbackend.models.Photo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhotoRepository extends JpaRepository<Photo, Long> {
    void deleteAllByAdId(Long adId);
}