package ru.yandex.practicum;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static ru.yandex.practicum.Constants.DATE_TIME_FORMATTER;
import static ru.yandex.practicum.Constants.LOG_FILENAME;

public class Logger {

    private static final int DEBUG = 0;
    private static final int INFO = 1;
    private static final int ERROR = 2;

    private final int logLevel;

    private final PrintWriter out;


    public Logger(String logLevel) {
        this(logLevel, fileWriter());
    }

    public Logger(String logLevel, PrintWriter out) {

        this.out = out;
        this.logLevel = switch (logLevel) {
            case "DEBUG" -> DEBUG;
            case "INFO" -> INFO;
            case "ERROR" -> ERROR;
            default -> {
                out.print("Несуществующий уровень логирования в настройках. Используется уровень INFO.\n\n");
                out.flush();
                yield INFO;
            }
        };
    }

    public void debug(String message) {
        if (logLevel > DEBUG) {
            return;
        }
        log("DEBUG", message);
    }

    public void info(String message) {
        if (logLevel > INFO) {
            return;
        }
        log("INFO", message);
    }

    public void error(String message) {
        if (logLevel > ERROR) {
            return;
        }
        log("ERROR", message);
    }

    private void log(String level, String message) {

        String timestamp = LocalDateTime.now().format(DATE_TIME_FORMATTER);
        out.printf("%s %s %s\n", timestamp, level, message);
        out.flush();

    }

    private static PrintWriter fileWriter() {
        try {
            PrintWriter writer = new PrintWriter(
                new OutputStreamWriter(new FileOutputStream(LOG_FILENAME), StandardCharsets.UTF_8)
            );
            writer.write("Log file for Wordle game. Encoding: UTF-8\n\n");
            writer.flush();
            return writer;
        } catch (FileNotFoundException e) {
            System.err.println(
                "Не удалось создать файл для записи логов: " + e.getMessage() + ".\nЛоги не будут записаны.");
            return new PrintWriter(new StringWriter());
        }
    }
}
