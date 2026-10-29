package com.quizly;

import java.util.List;
import java.util.Scanner;

public class Main {
    private static final String QUESTIONS_FILE = "questions.csv";
    private static final String RESULTS_FILE = "results.csv";
    private static final int QUESTIONS_PER_GAME = 5;

    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        Quiz quiz = new Quiz(FileManager.loadQuestions(QUESTIONS_FILE));
        Leaderboard leaderboard = new Leaderboard(FileManager.loadResults(RESULTS_FILE));

        while (true) {
            System.out.println();
            System.out.println("===== QUIZLY =====");
            System.out.println("1) Hrať kvíz");
            System.out.println("2) Rebríček (top 5)");
            System.out.println("3) Moje výsledky");
            System.out.println("0) Koniec");
            int choice = readNumber("Voľba: ");

            if (choice == 1) {
                play(quiz, leaderboard);
            } else if (choice == 2) {
                showTop(leaderboard);
            } else if (choice == 3) {
                showPlayerResults(leaderboard);
            } else if (choice == 0) {
                System.out.println("Dovidenia!");
                break;
            } else {
                System.out.println("Neplatná voľba.");
            }
        }
    }

    private static void play(Quiz quiz, Leaderboard leaderboard) {
        String name = readName();

        List<String> categories = quiz.getCategories();
        System.out.println("0) Všetky kategórie");
        for (int i = 0; i < categories.size(); i++) {
            System.out.println((i + 1) + ") " + categories.get(i));
        }
        int categoryChoice = readNumber("Kategória: ");
        String category = null;

        if (categoryChoice >= 1 && categoryChoice <= categories.size()) {
            category = categories.get(categoryChoice - 1);
        }

        List<Question> questions = quiz.getRandomQuestions(category, QUESTIONS_PER_GAME);
        int score = 0;
        for (Question question : questions) {
            System.out.println();
            System.out.println(question.getText());
            System.out.println(question.getHint());
            System.out.print("Odpoveď: ");
            String answer = scanner.nextLine();
            if (question.isCorrect(answer)) {
                System.out.println("Správne!");
                score++;
            } else {
                System.out.println("Zle. Správna odpoveď: " + question.getCorrectAnswer());
            }
        }

        System.out.println();
        System.out.println("Tvoje skóre: " + score + "/" + questions.size());
        leaderboard.addResult(new Result(name, score, questions.size()));
        FileManager.saveResults(RESULTS_FILE, leaderboard.getResults());
    }

    private static void showTop(Leaderboard leaderboard) {
        List<Result> top = leaderboard.getTop(5);
        for (int i = 0; i < top.size(); i++) {
            Result r = top.get(i);
            System.out.println((i + 1) + ". " + r.getPlayerName() + " - " + r.getScore() + "/" + r.getTotal());
        }
    }

    private static void showPlayerResults(Leaderboard leaderboard) {
        String name = readName();
        List<Result> results = leaderboard.getResultsOf(name);
        if (results.isEmpty()) {
            System.out.println("Tento hráč ešte nehral.");
        }
        for (Result r : results) {
            System.out.println(r.getScore() + "/" + r.getTotal());
        }
    }

    private static String readName() {
        while (true) {
            System.out.print("Meno hráča: ");
            String name = scanner.nextLine().trim();
            if (!name.isEmpty() && !name.contains(";")) {
                return name;
            }
            System.out.println("Neplatné meno.");
        }
    }

    private static int readNumber(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Zadaj číslo!");
            }
        }
    }
}
