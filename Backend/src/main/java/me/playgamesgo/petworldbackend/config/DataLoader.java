package me.playgamesgo.petworldbackend.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.playgamesgo.petworldbackend.models.Dictionary;
import me.playgamesgo.petworldbackend.services.DictionaryService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {
    private final DictionaryService dictionaryService;

    public DataLoader(DictionaryService dictionaryService) {
        this.dictionaryService = dictionaryService;
    }

    @Override
    public void run(String... args) throws Exception {
        if (dictionaryService.getAllDictionaries().isEmpty()) {
            importData();
        }
    }

    private void importData() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<List<Dictionary>> typeReference = new TypeReference<>() {};
        InputStream inputStream = new ClassPathResource("dictionary.json").getInputStream();
        List<Dictionary> dictionaries = mapper.readValue(inputStream, typeReference);
        dictionaryService.saveAll(dictionaries);
    }
}