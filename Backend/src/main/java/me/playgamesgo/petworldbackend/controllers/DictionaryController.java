package me.playgamesgo.petworldbackend.controllers;

import me.playgamesgo.petworldbackend.models.Dictionary;
import me.playgamesgo.petworldbackend.services.DictionaryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for handling dictionary-related requests.
 */
@RestController
@RequestMapping("/dictionaries")
public class DictionaryController {
    private final DictionaryService dictionaryService;

    public DictionaryController(DictionaryService dictionaryService) {
        this.dictionaryService = dictionaryService;
    }

    /**
     * Retrieves all dictionaries.
     *
     * @return a list of dictionaries
     */
    @GetMapping
    public List<Dictionary> getAllDictionaries() {
        return dictionaryService.getAllDictionaries();
    }
}