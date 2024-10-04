package me.playgamesgo.petworldbackend.repository;

import me.playgamesgo.petworldbackend.models.PetProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface PetPropertyRepository extends JpaRepository<PetProperty, Long> {
    @Transactional
    void deleteAllByAdId(Long adId);
}