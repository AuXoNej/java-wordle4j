package ru.yandex.practicum;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static ru.yandex.practicum.Constants.MAX_STEPS;

class WordleGameTest {

    private static final String ANSWER = "класс";

    private static final List<String> TEST_WORDS = List.of(
        "адрес", "ветер", "время", "город", "доска", "жизнь",
        "котик", "касса", "класс", "слово", "книга", "ручка",
        "дверь", "земля", "огонь", "песня", "школа", "улица");

    private static final List<String> LOSING_GUESSES =
        List.of("адрес", "ветер", "город", "доска", "жизнь", "слово");

    private static WordleDictionary dictionary;

    private WordleGame game;

    @BeforeAll
    static void createDictionary() {
        dictionary = new WordleDictionary(TEST_WORDS);
    }

    @BeforeEach
    void createGame() {
        game = new WordleGame(ANSWER, dictionary);
    }

    @Nested
    class MarkTests {

        @Test
        void mark_everyLetterOnItsPlace() {
            assertArrayEquals(new char[]{'+', '+', '+', '+', '+'}, WordleGame.mark("адрес", "адрес"));
        }

        @Test
        void mark_noCommonLetters() {
            assertArrayEquals(new char[]{'-', '-', '-', '-', '-'}, WordleGame.mark("жизнь", "адрес"));
        }

        @Test
        void mark_exactMatchWins() {
            assertArrayEquals(new char[]{'-', '+', '+', '+', '+'}, WordleGame.mark("тотик", "котик"));
        }

        @Test
        void mark_respectsLetterMultiplicity() {
            assertArrayEquals(new char[]{'+', '^', '^', '+', '-'}, WordleGame.mark("касса", "класс"));
        }

        @Test
        void mark_extraRepeatedLetterIsNotYellow() {
            assertArrayEquals(new char[]{'+', '-', '-', '-', '-'}, WordleGame.mark("ааааа", "адрес"));
        }

        @Test
        void step_marksLikeMark() {
            assertArrayEquals(WordleGame.mark("касса", ANSWER), game.step("касса"));
        }
    }

    @Nested
    class CheckInputTests {

        @ParameterizedTest()
        @ValueSource(strings = {"", "а", "адр", "привет", "адресок"})
        void checkInput_rejectsWrongLength(String input) {
            WordleInputException e = assertThrows(WordleInputException.class, () -> game.checkInput(input));

            assertTrue(e.getMessage().contains("Длина введенного слова"), e.getMessage());
        }

        @ParameterizedTest()
        @ValueSource(strings = {"adres", "adr1s", "адр1с", "а-ля-", "5букв", "адр с", "адр'с"})
        void checkInput_rejectsNonCyrillicLetters(String input) {
            WordleInputException e = assertThrows(WordleInputException.class, () -> game.checkInput(input));

            assertTrue(e.getMessage().contains("русского алфавита"), e.getMessage());
        }

        @Test
        void checkInput_acceptsDictionaryWord() {
            assertDoesNotThrow(() -> game.checkInput("котик"));
        }

        @Test
        void checkInput_rejectsWordAbsentInDictionary() {
            WordleInputException e = assertThrows(WordleInputException.class, () -> game.checkInput("слезы"));

            assertTrue(e.getMessage().contains("не найдено в словаре"), e.getMessage());
        }

        @Test
        void checkInput_doesNotNormalizeRegister() {
            WordleInputException e = assertThrows(WordleInputException.class, () -> game.checkInput("КЛАСС"));

            assertTrue(e.getMessage().contains("не найдено в словаре"), e.getMessage());
        }

        @Test
        void checkInput_acceptsNormalizedYo() {
            WordleDictionary normalized = new WordleDictionary(List.of("класс", "слезы"));
            WordleGame normalizedGame = new WordleGame("класс", normalized);

            assertDoesNotThrow(() -> normalizedGame.checkInput("слезы"));
        }
    }

    @Nested
    class StateTests {

        @Test
        void isWin_onAnswer() {
            assertTrue(game.isWin(ANSWER));
        }

        @Test
        void isWin_onOtherWord() {
            assertFalse(game.isWin("касса"));
        }

        @Test
        void isLose_onFreshGame() {
            assertFalse(game.isLose());
        }

        @Test
        void isLose_beforeMaxSteps() {
            for (int attempt = 1; attempt < MAX_STEPS; attempt++) {
                game.newStep();
                assertFalse(game.isLose());
            }
        }
    }

    @Nested
    class GameLoopTests {

        @Test
        void gameLoop_winOnThirdAttempt() {
            assertFalse(game.isWin("касса"));
            game.step("касса");
            game.newStep();

            assertFalse(game.isWin("слово"));
            game.step("слово");
            game.newStep();

            assertTrue(game.isWin(ANSWER));
            assertFalse(game.isLose());
        }

        @Test
        void gameLoop_loseExactlyOnLastAttempt() {
            for (int attempt = 1; attempt <= MAX_STEPS; attempt++) {
                String guess = LOSING_GUESSES.get(attempt - 1);
                assertFalse(game.isWin(guess), guess);

                game.step(guess);
                game.newStep();

                if (attempt == MAX_STEPS) {
                    assertTrue(game.isLose());
                } else {
                    assertFalse(game.isLose());
                }
            }
        }

        @Test
        void gameLoop_invalidInputDoesNotConsumeAttempt() {
            for (String invalid : List.of("адр", "adres", "слезы")) {
                assertThrows(WordleInputException.class, () -> game.checkInput(invalid), invalid);
                assertFalse(game.isLose(), invalid);
            }
        }
    }

    @Nested
    class GetHintTests {

        @Test
        void getHint_withoutHistory() {
            assertTrue(dictionary.contains(game.getHint()));
        }

        @Test
        void getHint_withDuplicateLetters_doesNotThrow() {
            game.step("касса");
            game.newStep();

            String hint = assertDoesNotThrow(game::getHint);

            assertTrue(dictionary.contains(hint));
        }

        @Test
        void getHint_matchesEveryGuess() {
            List<String> guesses = List.of("касса", "ветер", "город");
            for (String guess : guesses) {
                game.step(guess);
                game.newStep();
            }

            String hint = game.getHint();

            for (String guess : guesses) {
                assertArrayEquals(WordleGame.mark(guess, ANSWER), WordleGame.mark(guess, hint));
            }
        }

        @Test
        void getHint_keepsAnswerReachable() {
            WordleHint wordleHint = new WordleHint(dictionary);
            Map<String, char[]> inputWords = new HashMap<>();

            for (String guess : LOSING_GUESSES.subList(0, MAX_STEPS - 1)) {
                inputWords.put(guess, WordleGame.mark(guess, ANSWER));

                List<String> hints = wordleHint.getHints(inputWords);
                assertTrue(hints.contains(ANSWER));
            }
        }
    }
}
