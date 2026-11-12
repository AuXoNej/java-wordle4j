package ru.yandex.practicum;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static ru.yandex.practicum.Constants.WORD_LENGTH;

/*
этот класс содержит в себе всю рутину по работе с файлами словарей и с кодировками
    ему нужны методы по загрузке списка слов из файла по имени файла
    на выходе должен быть класс WordleDictionary
 */
public class WordleDictionaryLoader {

    public WordleDictionary readWordsFromFile(String filename) {
        List<String> result = new ArrayList<>();
        try (BufferedReader fileReader = new BufferedReader(
            new InputStreamReader(new FileInputStream(filename), StandardCharsets.UTF_8)
        )) {
            String line;
            while ((line = fileReader.readLine()) != null) {
                if (!checkWordFormat(line)) continue;
                result.add(line.toLowerCase().replace("ё", "е"));
            }
        } catch (Exception e) {
            throw new RuntimeException("Произошла ошибка во время чтения файла словаря.", e);
        }
        return new WordleDictionary(result);
    }

    private boolean checkWordFormat(String word) {
        return word.length() == WORD_LENGTH;
    }
}
