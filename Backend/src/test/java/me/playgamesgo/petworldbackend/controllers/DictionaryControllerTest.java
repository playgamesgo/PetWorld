package me.playgamesgo.petworldbackend.controllers;

import me.playgamesgo.petworldbackend.models.Dictionary;
import me.playgamesgo.petworldbackend.services.DictionaryService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DictionaryControllerTest {
    private AutoCloseable closeable;

    @Mock
    private DictionaryService dictionaryService;

    @InjectMocks
    private DictionaryController dictionaryController;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void getAllDictionaries_returnsDictionaries() {
        Dictionary dictionary = new Dictionary();
        dictionary.setId(1L);
        dictionary.setName("Test Dictionary");
        when(dictionaryService.getAllDictionaries()).thenReturn(List.of(dictionary));

        List<Dictionary> response = dictionaryController.getAllDictionaries();

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals("Test Dictionary", response.getFirst().getName());
    }

    @Test
    void getAllDictionaries_returnsEmptyList() {
        when(dictionaryService.getAllDictionaries()).thenReturn(Collections.emptyList());

        List<Dictionary> response = dictionaryController.getAllDictionaries();

        assertNotNull(response);
        assertTrue(response.isEmpty());
    }
}