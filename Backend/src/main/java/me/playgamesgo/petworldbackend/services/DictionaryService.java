package me.playgamesgo.petworldbackend.services;

import me.playgamesgo.petworldbackend.models.Dictionary;
import me.playgamesgo.petworldbackend.repository.DictionaryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class for managing dictionaries.
 */
@Service
public class DictionaryService {
    private final DictionaryRepository dictionaryRepository;

    public DictionaryService(DictionaryRepository dictionaryRepository) {
        this.dictionaryRepository = dictionaryRepository;
    }

    /**
     * Saves a list of dictionaries.
     *
     * @param dictionaries the list of Dictionary entities to save.
     */
    public void saveAll(List<Dictionary> dictionaries) {
        dictionaryRepository.saveAll(dictionaries);
    }

    /**
     * Retrieves all dictionaries.
     *
     * @return a list of all Dictionary entities.
     */
    public List<Dictionary> getAllDictionaries() {
        return dictionaryRepository.findAll();
    }
}