/*
 * MultipleChoiceQuestion.java
 * Otázka s výberom jednej správnej odpovede z viacerých možností.
 * Používateľ odpovedá číslom možnosti (1, 2, 3, ...) alebo písmenom (A, B, C, ...).
 */
package com.quizly.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MultipleChoiceQuestion extends Question {

    public static final int MIN_OPTIONS = 2;
    public static final int MAX_OPTIONS = 6;

    private final List<String> options;
    private final int correctIndex;

    /**
     * Vytvorí otázku s možnosťami. Index správnej možnosti je číslovaný od 0.
     *
     * @throws IllegalArgumentException ak je možností málo/veľa, niektorá je prázdna
     *                                  alebo index správnej odpovede je mimo rozsahu
     */
    public MultipleChoiceQuestion(int id, String text, String category, Difficulty difficulty,
                                  List<String> options, int correctIndex) {
        super(id, text, category, difficulty);
        if (options == null || options.size() < MIN_OPTIONS || options.size() > MAX_OPTIONS) {
            throw new IllegalArgumentException(
                    "Otázka musí mať " + MIN_OPTIONS + " až " + MAX_OPTIONS + " možností.");
        }
        List<String> cleaned = new ArrayList<>();
        for (String option : options) {
            cleaned.add(requireNonBlank(option, "Možnosť odpovede"));
        }
        if (correctIndex < 0 || correctIndex >= cleaned.size()) {
            throw new IllegalArgumentException("Index správnej odpovede je mimo rozsahu možností.");
        }
        this.options = List.copyOf(cleaned);
        this.correctIndex = correctIndex;
    }

    /**
     * Vráti nemenný zoznam možností odpovede.
     */
    public List<String> getOptions() {
        return options;
    }

    /**
     * Vráti index správnej možnosti (číslovaný od 0).
     */
    public int getCorrectIndex() {
        return correctIndex;
    }

    /**
     * Vráti typ otázky MULTIPLE_CHOICE.
     */
    @Override
    public QuestionType getType() {
        return QuestionType.MULTIPLE_CHOICE;
    }

    /**
     * Odpoveď je správna, ak zvolená možnosť zodpovedá správnemu indexu.
     */
    @Override
    public boolean isCorrect(String answer) {
        return parseChoice(answer) == correctIndex;
    }

    /**
     * Odpoveď má platný formát, ak ide o číslo alebo písmeno existujúcej možnosti.
     */
    @Override
    public boolean isValidAnswerFormat(String answer) {
        return parseChoice(answer) >= 0;
    }

    /**
     * Vráti správnu odpoveď v tvare "číslo) text možnosti".
     */
    @Override
    public String getCorrectAnswerText() {
        return (correctIndex + 1) + ") " + options.get(correctIndex);
    }

    /**
     * Vráti očíslované možnosti odpovede na zobrazenie pod otázkou.
     */
    @Override
    public List<String> getAnswerHints() {
        List<String> hints = new ArrayList<>();
        for (int i = 0; i < options.size(); i++) {
            hints.add((i + 1) + ") " + options.get(i));
        }
        return hints;
    }

    /**
     * Prevedie odpoveď používateľa na index možnosti (od 0).
     * Akceptuje číslo ("2") aj písmeno ("b"/"B"). Pri neplatnom vstupe vráti -1.
     */
    private int parseChoice(String answer) {
        if (answer == null) {
            return -1;
        }
        String trimmed = answer.trim().toUpperCase(Locale.ROOT);
        int index = -1;
        if (trimmed.matches("\\d+")) {
            index = Integer.parseInt(trimmed) - 1;
        } else if (trimmed.length() == 1 && Character.isLetter(trimmed.charAt(0))) {
            index = trimmed.charAt(0) - 'A';
        }
        return (index >= 0 && index < options.size()) ? index : -1;
    }
}
