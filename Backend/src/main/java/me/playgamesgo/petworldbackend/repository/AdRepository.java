package me.playgamesgo.petworldbackend.repository;

import me.playgamesgo.petworldbackend.models.Ad;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdRepository extends JpaRepository<Ad, Long> {
}
