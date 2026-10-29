/*
 * Main.java
 * Vstupný bod aplikácie Quizly. Pripraví priečinok s dátami (pri prvom
 * spustení doň skopíruje predvolené otázky), načíta otázky a výsledky
 * zo súborov, vytvorí všetky objekty aplikácie a spustí konzolové menu.
 *
 * Použitie: java -jar quizly.jar [priečinok_s_dátami]   (predvolene "data")
 */
package com.quizly;

import com.quizly.model.GameResult;
import com.quizly.model.Question;
import com.quizly.persistence.DataAccessException;
import com.quizly.persistence.QuestionCsvRepository;
import com.quizly.persistence.ResultCsvRepository;
import com.quizly.service.Leaderboard;
import com.quizly.service.QuestionBank;
import com.quizly.ui.ConsoleApp;
import com.quizly.ui.ConsoleInput;

import java.io.BufferedReader;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Random;

public class Main {

    private static final String DEFAULT_QUESTIONS_RESOURCE = "/default_questions.csv";

    /**
     * Spustí aplikáciu. Prvý argument (nepovinný) je cesta k priečinku s dátami.
     */
    public static void main(String[] args) {
        PrintStream out = new PrintStream(new FileOutputStream(FileDescriptor.out), true, StandardCharsets.UTF_8);
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));

        Path dataDir = Path.of(args.length > 0 ? args[0] : "data");
        QuestionCsvRepository questionRepository = new QuestionCsvRepository(dataDir.resolve("questions.csv"));
        ResultCsvRepository resultRepository = new ResultCsvRepository(dataDir.resolve("results.csv"));

        ensureDefaultQuestions(questionRepository.getFile(), out);
        QuestionBank questionBank = loadQuestions(questionRepository, out);
        Leaderboard leaderboard = loadResults(resultRepository, out);

        ConsoleApp app = new ConsoleApp(questionBank, leaderboard, questionRepository, resultRepository,
                new ConsoleInput(in, out), out, new Random());
        app.run();
    }

    /**
     * Ak súbor s otázkami ešte neexistuje (prvé spustenie), skopíruje doň
     * predvolené otázky pribalené v aplikácii.
     */
    private static void ensureDefaultQuestions(Path questionsFile, PrintStream out) {
        if (Files.exists(questionsFile)) {
            return;
        }
        try (InputStream defaults = Main.class.getResourceAsStream(DEFAULT_QUESTIONS_RESOURCE)) {
            if (defaults == null) {
                out.println("Upozornenie: predvolené otázky sa nenašli, začína sa s prázdnou databázou.");
                return;
            }
            Path parent = questionsFile.toAbsolutePath().getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.copy(defaults, questionsFile);
        } catch (IOException e) {
            out.println("Upozornenie: nepodarilo sa vytvoriť súbor s otázkami: " + e.getMessage());
        }
    }

    /**
     * Načíta otázky zo súboru do zásobníka. Chybné riadky a duplicitné ID
     * preskočí s upozornením; pri chybe čítania začne s prázdnym zásobníkom.
     */
    private static QuestionBank loadQuestions(QuestionCsvRepository repository, PrintStream out) {
        QuestionBank bank = new QuestionBank();
        try {
            List<Question> questions = repository.loadAll();
            repository.getWarnings().forEach(w -> out.println("Upozornenie (otázky): " + w));
            for (Question question : questions) {
                try {
                    bank.add(question);
                } catch (IllegalArgumentException e) {
                    out.println("Upozornenie (otázky): " + e.getMessage() + " – preskočené.");
                }
            }
        } catch (DataAccessException e) {
            out.println("Chyba: " + e.getMessage());
        }
        return bank;
    }

    /**
     * Načíta históriu výsledkov zo súboru do rebríčka. Pri chybe čítania
     * začne s prázdnym rebríčkom.
     */
    private static Leaderboard loadResults(ResultCsvRepository repository, PrintStream out) {
        try {
            List<GameResult> results = repository.loadAll();
            repository.getWarnings().forEach(w -> out.println("Upozornenie (výsledky): " + w));
            return new Leaderboard(results);
        } catch (DataAccessException e) {
            out.println("Chyba: " + e.getMessage());
            return new Leaderboard();
        }
    }
}
