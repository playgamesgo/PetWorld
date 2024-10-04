package me.playgamesgo.petworldbackend.services;

import me.playgamesgo.petworldbackend.models.Dictionary;
import me.playgamesgo.petworldbackend.repository.DictionaryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DictionaryService {
    private final DictionaryRepository dictionaryRepository;

    public DictionaryService(DictionaryRepository dictionaryRepository) {
        this.dictionaryRepository = dictionaryRepository;
    }

    public void saveAll(List<Dictionary> dictionaries) {
        dictionaryRepository.saveAll(dictionaries);
    }

    public List<Dictionary> getAllDictionaries() {
        return dictionaryRepository.findAll();
    }


}