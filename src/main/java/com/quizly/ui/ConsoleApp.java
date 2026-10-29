/*
 * ConsoleApp.java
 * Textové používateľské rozhranie aplikácie Quizly. Zobrazuje hlavné menu
 * a obsluhuje jednotlivé akcie: hranie kvízu, rebríček, históriu hráča,
 * štatistiky a správu otázok (výpis, filtrovanie, hľadanie, pridanie,
 * úprava, mazanie). Po každej zmene ukladá dáta do súborov cez repozitáre.
 */
package com.quizly.ui;

import com.quizly.model.Difficulty;
import com.quizly.model.GameResult;
import com.quizly.model.MultipleChoiceQuestion;
import com.quizly.model.OpenQuestion;
import com.quizly.model.Question;
import com.quizly.model.TrueFalseQuestion;
import com.quizly.persistence.CsvUtils;
import com.quizly.persistence.DataAccessException;
import com.quizly.persistence.Repository;
import com.quizly.service.Leaderboard;
import com.quizly.service.QuestionBank;
import com.quizly.service.QuizSession;

import java.io.PrintStream;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.Random;

public class ConsoleApp {

    private static final int DEFAULT_QUESTION_COUNT = 10;
    private static final int LEADERBOARD_SIZE = 10;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("d.M.yyyy HH:mm");
    private static final String LINE = "------------------------------------------------------------";

    private final QuestionBank questionBank;
    private final Leaderboard leaderboard;
    private final Repository<Question> questionRepository;
    private final Repository<GameResult> resultRepository;
    private final ConsoleInput input;
    private final PrintStream out;
    private final Random random;

    /**
     * Vytvorí aplikáciu so všetkými potrebnými závislosťami
     * (dáta v pamäti, repozitáre na ukladanie, vstup/výstup a generátor náhody).
     */
    public ConsoleApp(QuestionBank questionBank, Leaderboard leaderboard,
                      Repository<Question> questionRepository, Repository<GameResult> resultRepository,
                      ConsoleInput input, PrintStream out, Random random) {
        this.questionBank = questionBank;
        this.leaderboard = leaderboard;
        this.questionRepository = questionRepository;
        this.resultRepository = resultRepository;
        this.input = input;
        this.out = out;
        this.random = random;
    }

    /**
     * Spustí hlavnú slučku aplikácie – zobrazuje menu a vykonáva zvolené akcie,
     * kým používateľ nezvolí koniec alebo sa neukončí vstup.
     */
    public void run() {
        out.println("============================================================");
        out.println("                 Q U I Z L Y  –  kvízová hra");
        out.println("============================================================");
        try {
            boolean running = true;
            while (running) {
                running = mainMenu();
            }
        } catch (ConsoleInput.InputClosedException e) {
            out.println();
        }
        out.println("Dovidenia!");
    }

    /**
     * Zobrazí hlavné menu a vykoná vybranú akciu. Vráti false, ak chce používateľ skončiť.
     */
    private boolean mainMenu() {
        out.println();
        out.println("HLAVNÉ MENU");
        out.println("  1) Hrať kvíz");
        out.println("  2) Rebríček najlepších hráčov");
        out.println("  3) História hráča");
        out.println("  4) Štatistiky");
        out.println("  5) Správa otázok");
        out.println("  0) Koniec");
        int choice = input.readInt("Voľba: ", 0, 5);
        switch (choice) {
            case 1:
                playQuiz();
                break;
            case 2:
                showLeaderboard();
                break;
            case 3:
                showPlayerHistory();
                break;
            case 4:
                showStatistics();
                break;
            case 5:
                manageQuestions();
                break;
            default:
                return false;
        }
        return true;
    }

    // ------------------------------------------------------------------ hra

    /**
     * Prevedie hráča celou hrou: výber kategórie, obťažnosti a počtu otázok,
     * kladenie otázok, vyhodnotenie a uloženie výsledku do rebríčka.
     */
    private void playQuiz() {
        if (questionBank.isEmpty()) {
            out.println("V databáze nie sú žiadne otázky. Najprv nejaké pridaj v správe otázok.");
            return;
        }
        out.println();
        String playerName = input.readNonBlank("Meno hráča: ");
        String category = chooseCategory(true);
        Difficulty difficulty = chooseDifficulty(true);

        int available = questionBank.filter(category, difficulty).size();
        if (available == 0) {
            out.println("Pre zvolenú kategóriu a obťažnosť neexistujú žiadne otázky.");
            return;
        }
        int defaultCount = Math.min(DEFAULT_QUESTION_COUNT, available);
        int count = input.readIntOrDefault(
                String.format("Počet otázok (1–%d, Enter = %d): ", available, defaultCount),
                1, available, defaultCount);

        List<Question> selected = questionBank.pickRandom(category, difficulty, count, random);
        QuizSession session = new QuizSession(playerName, category, selected);

        while (session.hasNextQuestion()) {
            askQuestion(session);
        }
        finishQuiz(session);
    }

    /**
     * Zobrazí aktuálnu otázku, načíta odpoveď v platnom formáte,
     * vyhodnotí ju a vypíše, či bola správna.
     */
    private void askQuestion(QuizSession session) {
        Question question = session.getCurrentQuestion();
        out.println();
        out.println(LINE);
        out.printf("Otázka %d/%d  [%s, %s, %d b.]%n", session.getCurrentQuestionNumber(),
                session.getTotalQuestions(), question.getCategory(),
                question.getDifficulty().getLabel(), question.getPoints());
        out.println(question.getText());
        question.getAnswerHints().forEach(hint -> out.println("   " + hint));

        String answer = input.readLine("> ");
        while (!question.isValidAnswerFormat(answer)) {
            out.println("  ! Neplatný formát odpovede, skús znova.");
            answer = input.readLine("> ");
        }
        if (session.submitAnswer(answer)) {
            out.println("✔ Správne! +" + question.getPoints() + " b.");
        } else {
            out.println("✘ Nesprávne. Správna odpoveď: " + question.getCorrectAnswerText());
        }
    }

    /**
     * Vypíše zhrnutie hry, uloží výsledok do rebríčka a do súboru
     * a oznámi hráčovi jeho umiestnenie.
     */
    private void finishQuiz(QuizSession session) {
        GameResult result = session.toResult();
        leaderboard.add(result);
        saveResults();

        out.println();
        out.println("============================================================");
        out.printf("Koniec hry, %s!%n", result.getPlayerName());
        out.printf("Správne odpovede: %d z %d%n", result.getCorrectAnswers(), result.getTotalQuestions());
        out.printf("Body: %d z %d (%.1f %%)%n", result.getScore(), result.getMaxScore(), result.getPercentage());
        out.println(rating(result.getPercentage()));
        int position = leaderboard.top(leaderboard.size()).indexOf(result) + 1;
        out.printf("Umiestnenie v celkovom rebríčku: %d. z %d%n", position, leaderboard.size());

        List<Question> mistakes = session.getMistakes();
        if (!mistakes.isEmpty()) {
            out.println();
            out.println("Otázky, na ktoré si odpovedal/a nesprávne:");
            mistakes.forEach(q -> out.println("  - " + q.getText() + "  →  " + q.getCorrectAnswerText()));
        }
        out.println("============================================================");
    }

    /**
     * Vráti slovné hodnotenie výkonu hráča podľa percentuálnej úspešnosti.
     */
    static String rating(double percentage) {
        if (percentage >= 90) {
            return "Hodnotenie: Výborne, si expert! 🏆";
        } else if (percentage >= 70) {
            return "Hodnotenie: Veľmi dobre!";
        } else if (percentage >= 50) {
            return "Hodnotenie: Dobre, ale je čo zlepšovať.";
        }
        return "Hodnotenie: Skús to znova, nabudúce to pôjde lepšie.";
    }

    // ------------------------------------------------------- rebríček a štatistiky

    /**
     * Zobrazí rebríček najlepších výsledkov – celkový alebo pre zvolenú kategóriu.
     */
    private void showLeaderboard() {
        if (leaderboard.size() == 0) {
            out.println("Zatiaľ nebola odohraná žiadna hra.");
            return;
        }
        out.println();
        out.println("Rebríček: 0) celkový, alebo vyber kategóriu hry:");
        List<String> categories = new ArrayList<>(leaderboard.gamesPerCategory().keySet());
        for (int i = 0; i < categories.size(); i++) {
            out.printf("  %d) %s%n", i + 1, categories.get(i));
        }
        int choice = input.readInt("Voľba: ", 0, categories.size());
        List<GameResult> top = choice == 0
                ? leaderboard.top(LEADERBOARD_SIZE)
                : leaderboard.topByCategory(categories.get(choice - 1), LEADERBOARD_SIZE);

        out.println();
        out.println(choice == 0 ? "TOP " + LEADERBOARD_SIZE + " – celkovo"
                : "TOP " + LEADERBOARD_SIZE + " – " + categories.get(choice - 1));
        printResultsTable(top, true);
    }

    /**
     * Zobrazí históriu hier zadaného hráča a jeho najlepší výsledok.
     */
    private void showPlayerHistory() {
        String name = input.readNonBlank("Meno hráča: ");
        List<GameResult> history = leaderboard.historyOf(name);
        if (history.isEmpty()) {
            out.println("Hráč '" + name + "' zatiaľ nehral.");
            return;
        }
        out.println();
        out.printf("História hráča %s (počet hier: %d):%n", name, history.size());
        printResultsTable(history, false);
        Optional<GameResult> best = leaderboard.bestOf(name);
        best.ifPresent(r -> out.printf("Najlepší výsledok: %.1f %% (%s, %s)%n",
                r.getPercentage(), r.getCategory(), r.getPlayedAt().format(DATE_FORMAT)));
    }

    /**
     * Vypíše tabuľku výsledkov. Ak je {@code numbered} true, vypíše aj poradie.
     */
    private void printResultsTable(List<GameResult> results, boolean numbered) {
        out.printf("%-4s %-16s %-14s %9s %8s  %s%n", "#", "Hráč", "Kategória", "Body", "Úspešn.", "Dátum");
        out.println(LINE);
        for (int i = 0; i < results.size(); i++) {
            GameResult r = results.get(i);
            out.printf("%-4s %-16s %-14s %9s %7.1f%%  %s%n",
                    numbered ? (i + 1) + "." : "-",
                    shorten(r.getPlayerName(), 16), shorten(r.getCategory(), 14),
                    r.getScore() + "/" + r.getMaxScore(), r.getPercentage(),
                    r.getPlayedAt().format(DATE_FORMAT));
        }
    }

    /**
     * Zobrazí súhrnné štatistiky o otázkach a odohraných hrách.
     */
    private void showStatistics() {
        out.println();
        out.println("ŠTATISTIKY");
        out.println(LINE);
        out.println("Počet otázok: " + questionBank.size());
        questionBank.countByCategory()
                .forEach((category, count) -> out.printf("  %-20s %d%n", category, count));

        out.println();
        out.println("Počet odohraných hier: " + leaderboard.size());
        OptionalDouble average = leaderboard.averagePercentage();
        if (average.isEmpty()) {
            return;
        }
        out.printf("Priemerná úspešnosť: %.1f %%%n", average.getAsDouble());

        out.println();
        out.println("Hry podľa kategórie:");
        leaderboard.gamesPerCategory()
                .forEach((category, count) -> out.printf("  %-20s %d%n", category, count));

        out.println();
        out.println("Priemerná úspešnosť hráčov:");
        Map<String, Double> byPlayer = leaderboard.averagePercentageByPlayer();
        byPlayer.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .forEach(e -> out.printf("  %-20s %.1f %%%n", e.getKey(), e.getValue()));
    }

    // ------------------------------------------------------------ správa otázok

    /**
     * Zobrazuje podmenu správy otázok, kým sa používateľ nevráti späť.
     */
    private void manageQuestions() {
        while (true) {
            out.println();
            out.println("SPRÁVA OTÁZOK (" + questionBank.size() + " otázok)");
            out.println("  1) Vypísať všetky otázky");
            out.println("  2) Filtrovať podľa kategórie a obťažnosti");
            out.println("  3) Hľadať v texte otázok");
            out.println("  4) Zobraziť detail otázky");
            out.println("  5) Pridať otázku");
            out.println("  6) Upraviť otázku");
            out.println("  7) Zmazať otázku");
            out.println("  0) Späť");
            int choice = input.readInt("Voľba: ", 0, 7);
            switch (choice) {
                case 1:
                    printQuestions(questionBank.getAll());
                    break;
                case 2:
                    printQuestions(questionBank.filter(chooseCategory(true), chooseDifficulty(true)));
                    break;
                case 3:
                    printQuestions(questionBank.search(input.readLine("Hľadaný text: ")));
                    break;
                case 4:
                    findQuestion().ifPresent(this::printQuestionDetail);
                    break;
                case 5:
                    addQuestion();
                    break;
                case 6:
                    editQuestion();
                    break;
                case 7:
                    deleteQuestion();
                    break;
                default:
                    return;
            }
        }
    }

    /**
     * Vypíše zoznam otázok (alebo informáciu, že žiadne nevyhovujú).
     */
    private void printQuestions(List<Question> questions) {
        out.println();
        if (questions.isEmpty()) {
            out.println("Žiadne otázky nevyhovujú.");
            return;
        }
        questions.forEach(q -> out.println(q));
        out.println("Spolu: " + questions.size());
    }

    /**
     * Vypíše všetky údaje o otázke vrátane možností a správnej odpovede.
     */
    private void printQuestionDetail(Question question) {
        out.println();
        out.println(question);
        question.getAnswerHints().forEach(hint -> out.println("   " + hint));
        if (question instanceof OpenQuestion) {
            out.println("Akceptované odpovede: "
                    + String.join(", ", ((OpenQuestion) question).getAcceptedAnswers()));
        } else {
            out.println("Správna odpoveď: " + question.getCorrectAnswerText());
        }
    }

    /**
     * Načíta ID otázky a nájde ju. Ak neexistuje, vypíše chybu a vráti prázdny Optional.
     */
    private Optional<Question> findQuestion() {
        int id = input.readInt("ID otázky: ", 1, Integer.MAX_VALUE);
        Optional<Question> question = questionBank.findById(id);
        if (question.isEmpty()) {
            out.println("Otázka s ID " + id + " neexistuje.");
        }
        return question;
    }

    /**
     * Prevedie používateľa vytvorením novej otázky zvoleného typu.
     * Neplatné údaje odchytí a vypíše chybu; platnú otázku pridá a uloží.
     */
    private void addQuestion() {
        out.println();
        out.println("Typ otázky: 1) výber z možností  2) pravda/nepravda  3) otvorená odpoveď");
        int type = input.readInt("Typ: ", 1, 3);
        String text = input.readNonBlank("Text otázky: ");
        String category = readCategoryName();
        Difficulty difficulty = chooseDifficulty(false);
        int id = questionBank.nextId();

        try {
            Question question;
            if (type == 1) {
                int optionCount = input.readInt(String.format("Počet možností (%d–%d): ",
                        MultipleChoiceQuestion.MIN_OPTIONS, MultipleChoiceQuestion.MAX_OPTIONS),
                        MultipleChoiceQuestion.MIN_OPTIONS, MultipleChoiceQuestion.MAX_OPTIONS);
                List<String> options = new ArrayList<>();
                for (int i = 1; i <= optionCount; i++) {
                    options.add(readListItem("Možnosť " + i + ": "));
                }
                int correct = input.readInt("Číslo správnej možnosti: ", 1, optionCount);
                question = new MultipleChoiceQuestion(id, text, category, difficulty, options, correct - 1);
            } else if (type == 2) {
                boolean correct = input.readYesNo("Je tvrdenie pravdivé?");
                question = new TrueFalseQuestion(id, text, category, difficulty, correct);
            } else {
                String answers = input.readNonBlank("Správne odpovede (viac možností oddeľ znakom |): ");
                question = new OpenQuestion(id, text, category, difficulty, CsvUtils.splitList(answers));
            }
            questionBank.add(question);
            saveQuestions();
            out.println("Otázka bola pridaná pod ID " + id + ".");
        } catch (IllegalArgumentException e) {
            out.println("Otázku sa nepodarilo vytvoriť: " + e.getMessage());
        }
    }

    /**
     * Umožní upraviť text, kategóriu a obťažnosť existujúcej otázky.
     * Prázdny vstup ponechá pôvodnú hodnotu.
     */
    private void editQuestion() {
        Optional<Question> found = findQuestion();
        if (found.isEmpty()) {
            return;
        }
        Question question = found.get();
        printQuestionDetail(question);
        out.println("(Enter = ponechať pôvodnú hodnotu)");
        String text = input.readLine("Nový text: ");
        String category = input.readLine("Nová kategória: ");
        String difficulty = input.readLine("Nová obťažnosť (1 = ľahká, 2 = stredná, 3 = ťažká): ");
        try {
            Difficulty newDifficulty = difficulty.isEmpty() ? question.getDifficulty() : Difficulty.parse(difficulty);
            if (!text.isEmpty()) {
                question.setText(text);
            }
            if (!category.isEmpty()) {
                question.setCategory(category);
            }
            question.setDifficulty(newDifficulty);
            saveQuestions();
            out.println("Otázka bola upravená.");
        } catch (IllegalArgumentException e) {
            out.println("Úprava zlyhala: " + e.getMessage());
        }
    }

    /**
     * Po potvrdení zmaže otázku podľa ID a uloží zmeny.
     */
    private void deleteQuestion() {
        Optional<Question> found = findQuestion();
        if (found.isEmpty()) {
            return;
        }
        out.println(found.get());
        if (input.readYesNo("Naozaj zmazať?")) {
            questionBank.remove(found.get().getId());
            saveQuestions();
            out.println("Otázka bola zmazaná.");
        }
    }

    // ------------------------------------------------------------ pomocné metódy

    /**
     * Nechá používateľa vybrať jednu z existujúcich kategórií.
     * Ak {@code allowAny} je true, voľba 0 znamená "všetky kategórie" a vráti null.
     */
    private String chooseCategory(boolean allowAny) {
        List<String> categories = new ArrayList<>(questionBank.getCategories());
        out.println("Kategória:");
        if (allowAny) {
            out.println("  0) Všetky");
        }
        for (int i = 0; i < categories.size(); i++) {
            out.printf("  %d) %s%n", i + 1, categories.get(i));
        }
        int choice = input.readInt("Voľba: ", allowAny ? 0 : 1, categories.size());
        return choice == 0 ? null : categories.get(choice - 1);
    }

    /**
     * Nechá používateľa vybrať obťažnosť. Ak {@code allowAny} je true,
     * voľba 0 znamená "ľubovoľná" a vráti null.
     */
    private Difficulty chooseDifficulty(boolean allowAny) {
        Difficulty[] values = Difficulty.values();
        out.println("Obťažnosť:");
        if (allowAny) {
            out.println("  0) Ľubovoľná");
        }
        for (int i = 0; i < values.length; i++) {
            out.printf("  %d) %s (%d b.)%n", i + 1, values[i].getLabel(), values[i].getPoints());
        }
        int choice = input.readInt("Voľba: ", allowAny ? 0 : 1, values.length);
        return choice == 0 ? null : values[choice - 1];
    }

    /**
     * Načíta názov kategórie pre novú otázku. Vypíše existujúce kategórie
     * ako nápovedu, ale dovolí zadať aj úplne novú.
     */
    private String readCategoryName() {
        if (!questionBank.getCategories().isEmpty()) {
            out.println("Existujúce kategórie: " + String.join(", ", questionBank.getCategories()));
        }
        return input.readNonBlank("Kategória: ");
    }

    /**
     * Načíta neprázdnu položku zoznamu, ktorá nesmie obsahovať znak "|"
     * (ten sa v súbore používa ako oddeľovač možností).
     */
    private String readListItem(String prompt) {
        while (true) {
            String value = input.readNonBlank(prompt);
            if (!value.contains(CsvUtils.LIST_SEPARATOR)) {
                return value;
            }
            out.println("  ! Text nesmie obsahovať znak '" + CsvUtils.LIST_SEPARATOR + "'.");
        }
    }

    /**
     * Skráti text na zadanú dĺžku (pre zarovnanie stĺpcov v tabuľke).
     */
    private static String shorten(String text, int maxLength) {
        return text.length() <= maxLength ? text : text.substring(0, maxLength - 1) + "…";
    }

    /**
     * Uloží všetky otázky do súboru. Pri chybe iba vypíše hlásenie – aplikácia beží ďalej.
     */
    private void saveQuestions() {
        try {
            questionRepository.saveAll(questionBank.getAll());
        } catch (DataAccessException e) {
            out.println("  ! Chyba pri ukladaní otázok: " + e.getMessage());
        }
    }

    /**
     * Uloží všetky výsledky hier do súboru. Pri chybe iba vypíše hlásenie.
     */
    private void saveResults() {
        try {
            resultRepository.saveAll(leaderboard.getAll());
        } catch (DataAccessException e) {
            out.println("  ! Chyba pri ukladaní výsledkov: " + e.getMessage());
        }
    }
}
