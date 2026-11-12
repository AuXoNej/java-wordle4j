package ru.yandex.practicum;

import java.time.format.DateTimeFormatter;
import java.util.Set;

public class Constants {

    public static final String WORDS_FILENAME = "words_ru.txt";
    public static final Integer WORD_LENGTH = 5;

    public static final Set<String> EXIT_WORDS = Set.of("exit", "quit", "выход", "q", "0");
    public static final Integer MAX_STEPS = 6;

    public static final String LOG_LEVEL = "INFO";
    public static final String LOG_FILENAME = "log_game.txt";
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
}
