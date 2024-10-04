package me.playgamesgo.petworldbackend.repository;

import me.playgamesgo.petworldbackend.models.PetProperty;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetPropertyRepository extends JpaRepository<PetProperty, Long> {
    void deleteAllByAdId(Long adId);
}