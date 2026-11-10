package ru.yandex.practicum;

import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

import static ru.yandex.practicum.Constants.*;

/*
в главном классе нам нужно:
    создать лог-файл (он должен передаваться во все классы)
    создать загрузчик словарей WordleDictionaryLoader
    загрузить словарь WordleDictionary с помощью класса WordleDictionaryLoader
    затем создать игру WordleGame и передать ей словарь
    вызвать игровой метод в котором в цикле опрашивать пользователя и передавать информацию в игру
    вывести состояние игры и конечный результат
 */
public class Wordle {

    public static void main(String[] args) {

        Logger log = new Logger(LOG_LEVEL);

        try {
            Random random = new Random();
            Scanner scanner = new Scanner(System.in);

            WordleDictionaryLoader loader = new WordleDictionaryLoader();
            WordleDictionary dictionary = loader.readWordsFromFile(WORDS_FILENAME);
            WordleGame game = new WordleGame(
                dictionary.words().get(random.nextInt(dictionary.words().size())), dictionary);

            System.out.println("Hello, Wordle!\n");

            System.out.println("Начинаем игру!");
            log.info("Начинаем игру!");

            System.out.println("Введите слово или 'Выход' для завершения игры. Нажмите Enter для подсказки.");

            while(true) {

                var input = scanner.nextLine();

                if (input.isBlank()) {
                    log.debug("Пользователь ввел пустую строку, выводим подсказку");

                    input = game.getHint();
                    System.out.println(input);
                }

                var input_word = input.trim().toLowerCase().replace("ё", "е");
                if (EXIT_WORDS.contains(input_word)) {
                    break;
                }

                try {
                    game.checkInput(input_word);
                } catch (WordleInputException e) {
                    log.debug(e.getMessage());
                    System.out.println(e.getMessage() + " Повторите ввод.");
                    continue;
                }


                if (game.isWin(input_word)) {
                    log.info(String.format("Пользователь угадал слово '%s'", input_word));
                    System.out.println("Поздравляем! Вы угадали слово!");
                    break;
                }

                System.out.println(game.step(input_word));

                game.newStep();

                if (game.isLose()) {
                    log.info(String.format("Пользователь не смог угадать слово '%s'", game.getAnswer()));
                    System.out.printf("К сожалению, вы не угадали слово с %d попыток. Попробуйте еще раз.\n",
                        MAX_STEPS);
                    break;
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage() + "\n\t" + e.getCause() + Arrays.toString(e.getStackTrace()));
            System.err.printf("Критическая ошибка: %s\n", e.getMessage());
        }
        finally {
            System.out.println("Игра завершена.");
        }
    }

}
