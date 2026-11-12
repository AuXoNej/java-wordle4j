package ru.yandex.practicum;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static ru.yandex.practicum.Constants.MAX_STEPS;
import static ru.yandex.practicum.Constants.WORD_LENGTH;

/*
в этом классе хранится словарь и состояние игры
    текущий шаг
    всё что пользователь вводил
    правильный ответ

в этом классе нужны методы, которые
    проанализируют совпадение слова с ответом
    предложат слово-подсказку с учётом всего, что вводил пользователь ранее

не забудьте про специальные типы исключений для игровых и неигровых ошибок
 */
public class WordleGame {

    private final String answer;

    private final WordleHint wordleHint;

    private final WordleDictionary dictionary;

    private int steps;

    private final Map<String, char[]> inputWords;

    Random random = new Random();


    public WordleGame(String answer, WordleDictionary dictionary) {
        this.steps = 0;
        this.answer = answer;
        this.dictionary = dictionary;
        this.inputWords = new HashMap<>();
        this.wordleHint = new WordleHint(dictionary);
    }

    public String getAnswer() {
        return answer;
    }

    public void newStep() {
        steps++;
    }

    public void checkInput(String input) throws WordleInputException {
        if (input.length() != WORD_LENGTH) {
            throw new WordleInputException(
                String.format("Длина введенного слова не равна %s символам.", WORD_LENGTH));
        }
        if (!input.matches("^\\p{IsCyrillic}+$")) {
            throw new WordleInputException(
                "Введенное слово должно содержать только буквы русского алфавита.");
        }
        if (!dictionary.contains(input)) {
            throw new WordleInputException("Введенное слово не найдено в словаре.");
        }
    }

    public char[] step(String input) {
        var result = mark(input, answer);
        inputWords.put(input, result);
        return result;
    }

    public boolean isWin(String input) {
        return input.equals(answer);
    }

    public boolean isLose() {
        return steps >= MAX_STEPS;
    }

    public String getHint() {

        if (inputWords.isEmpty()) {
            return dictionary.words().get(random.nextInt(dictionary.words().size()));
        }
        var result = wordleHint.getHints(inputWords);

        return result.get(random.nextInt(result.size()));

    }

    public static char[] mark(String input, String answer) {
        var result = new char[WORD_LENGTH];
        Arrays.fill(result, '-');

        var charCount = new HashMap<Character, Integer>();
        for (var c : answer.toCharArray()) {
            charCount.put(c, charCount.getOrDefault(c, 0) + 1);
        }
        for (int i = 0; i < WORD_LENGTH; i++) {
            if (input.charAt(i) == answer.charAt(i)) {
                result[i] = '+';
                charCount.put(input.charAt(i), charCount.get(input.charAt(i)) - 1);
            }
        }
        for (int i = 0; i < WORD_LENGTH; i++) {
            if (result[i] == '-') {
                if (charCount.getOrDefault(input.charAt(i), 0) > 0) {
                    result[i] = '^';
                    charCount.put(input.charAt(i), charCount.get(input.charAt(i)) - 1);
                }
            }
        }

        return result;
    }
}
