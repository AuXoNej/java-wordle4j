package ru.yandex.practicum;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


class WordleHintTest {

    private static final String ANSWER = "класс";

    private static final List<String> TEST_WORDS = List.of(
        "адрес", "ветер", "время", "город", "доска", "жизнь",
        "котик", "касса", "класс", "слово", "книга", "ручка",
        "дверь", "земля", "огонь", "песня", "школа", "улица");

    private static WordleDictionary dictionary;

    private WordleHint wordleHint;

    @BeforeAll
    static void createDictionary() {
        dictionary = new WordleDictionary(TEST_WORDS);
    }

    @BeforeEach
    void createHint() {
        wordleHint = new WordleHint(dictionary);
    }

    @Test
    void getHints_keepsAnswer_withDuplicatedLetters() {
        List<String> hints = wordleHint.getHints(historyOf("касса", ANSWER));

        assertTrue(hints.contains(ANSWER));
    }

    @Test
    void getHints_excludesEnteredWords() {
        List<String> hints = wordleHint.getHints(historyOf("касса", ANSWER));

        assertFalse(hints.contains("касса"));
    }

    @Test
    void getHints_respectsGreenLetters() {
        List<String> hints = wordleHint.getHints(historyOf("касса", ANSWER));

        assertTrue(hints.stream().allMatch(hint -> hint.charAt(0) == 'к' && hint.charAt(3) == 'с'));
    }

    @Test
    void getHints_excludesYellowLetterFromItsOwnPosition() {
        List<String> hints = wordleHint.getHints(historyOf("адрес", "ветер"));

        assertTrue(hints.contains("ветер"));
        assertTrue(hints.stream().allMatch(hint -> hint.charAt(2) != 'р'));
        assertTrue(hints.stream().allMatch(hint -> hint.charAt(3) == 'е'));
    }

    @Test
    void getHints_respectsLetterMultiplicity() {
        List<String> hints = wordleHint.getHints(historyOf("касса", ANSWER));

        assertTrue(hints.stream().allMatch(hint -> count(hint, 'а') == 1));
        assertTrue(hints.stream().allMatch(hint -> count(hint, 'с') >= 2));
    }

    @Test
    void getHints_reproducesEveryRecordedMark() {
        Map<String, char[]> inputWords = Map.of(
            "касса", WordleGame.mark("касса", ANSWER),
            "ветер", WordleGame.mark("ветер", ANSWER));

        List<String> hints = wordleHint.getHints(inputWords);

        assertTrue(hints.contains(ANSWER));
        for (String hint : hints) {
            assertMarked(inputWords, hint);
        }
    }

    @Test
    void getHints_withoutHistory_returnsWholeDictionary() {
        assertEquals(TEST_WORDS.size(), wordleHint.getHints(Map.of()).size());
    }

    private void assertMarked(Map<String, char[]> inputWords, String candidate) {
        for (var entry : inputWords.entrySet()) {
            assertArrayEquals(entry.getValue(), WordleGame.mark(entry.getKey(), candidate));
        }
    }

    private static Map<String, char[]> historyOf(String guess, String answer) {
        return Map.of(guess, WordleGame.mark(guess, answer));
    }

    private static long count(String word, char letter) {
        return word.chars().filter(current -> current == letter).count();
    }
}
