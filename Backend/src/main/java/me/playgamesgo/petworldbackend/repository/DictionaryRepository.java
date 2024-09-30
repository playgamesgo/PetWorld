package me.playgamesgo.petworldbackend.repository;

import me.playgamesgo.petworldbackend.models.Dictionary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DictionaryRepository extends JpaRepository<Dictionary, Long> {
}