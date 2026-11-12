package ru.yandex.practicum;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WordleDictionaryLoaderTest {

    private static final String MESSAGE_BROKEN_FILE = "Произошла ошибка во время чтения файла словаря.";

    private static final List<String> FILE_LINES = List.of("КЛАСС", "слезы", "", "дом", "словами");

    @TempDir
    Path tempDir;

    private final WordleDictionaryLoader loader = new WordleDictionaryLoader();

    private Path wordsFile;

    @BeforeEach
    void writeWordsFile() throws IOException {
        wordsFile = tempDir.resolve("words_ru.txt");
        Files.writeString(wordsFile, String.join("\n", FILE_LINES));
    }

    @Test
    void readWordsFromFile_normalizesAndFilters() {
        WordleDictionary dictionary = loader.readWordsFromFile(wordsFile.toString());

        assertEquals(List.of("класс", "слезы"), dictionary.words());
    }

    @Test
    void readWordsFromFile_findsNormalizedForm() {
        WordleDictionary dictionary = loader.readWordsFromFile(wordsFile.toString());

        assertTrue(dictionary.contains("слезы"));
        assertFalse(dictionary.contains("слёзы"));
    }

    @Test
    void readWordsFromFile_emptyFile() throws IOException {
        Path emptyFile = tempDir.resolve("empty.txt");
        Files.write(emptyFile, new byte[0]);

        assertTrue(loader.readWordsFromFile(emptyFile.toString()).words().isEmpty());
    }

    @Test
    void readWordsFromFile_missingFile() {
        RuntimeException e = assertThrows(RuntimeException.class,
            () -> loader.readWordsFromFile(tempDir.resolve("absent.txt").toString()));

        assertEquals(MESSAGE_BROKEN_FILE, e.getMessage());
        assertEquals(FileNotFoundException.class, e.getCause().getClass());
    }

}