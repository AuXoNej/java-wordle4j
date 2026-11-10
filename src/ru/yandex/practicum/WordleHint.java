package ru.yandex.practicum;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class WordleHint {

    private final WordleDictionary dictionary;

    WordleHint(WordleDictionary dictionary) {
        this.dictionary = dictionary;
    }

    public List<String> getHints(Map<String, char[]> inputWords) {

        List<String> result = new ArrayList<>();

        for (var candidate : dictionary.words()) {
            if (inputWords.containsKey(candidate)) {
                continue;
            }
            if (isConsistent(candidate, inputWords)) {
                result.add(candidate);
            }
        }

        return result;
    }

    private boolean isConsistent(String candidate, Map<String, char[]> inputWords) {
        for (var entry : inputWords.entrySet()) {
            if (!Arrays.equals(entry.getValue(), WordleGame.mark(entry.getKey(), candidate))) {
                return false;
            }
        }
        return true;
    }

}
