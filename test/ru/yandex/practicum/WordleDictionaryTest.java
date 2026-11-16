package ru.yandex.practicum;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class WordleDictionaryTest {

    private static final String KNOWN_WORD = "класс";

    private static List<String> words;

    private WordleDictionary dictionary;

    @BeforeAll
    static void createWords() {
        words = List.of(KNOWN_WORD, "слезы", "котик");
    }

    @BeforeEach
    void createDictionary() {
        dictionary = new WordleDictionary(words);
    }

    @Test
    void contains_knownWord() {
        assertTrue(dictionary.contains(KNOWN_WORD));
    }

    @Test
    void contains_unknownWord() {
        assertFalse(dictionary.contains("школа"));
    }

    @Test
    void contains_isCaseSensitive() {
        assertFalse(dictionary.contains("КЛАСС"));
    }

    @Test
    void contains_isYoSensitive() {
        assertTrue(dictionary.contains("слезы"));
        assertFalse(dictionary.contains("слёзы"));
    }

    @Test
    void contains_emptyDictionary() {
        assertFalse(new WordleDictionary(List.of()).contains(KNOWN_WORD));
    }

    @Test
    void words_keepsSourceOrder() {
        assertEquals(words, dictionary.words());
    }
}