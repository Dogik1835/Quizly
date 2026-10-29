/*
 * TrueFalseQuestion.java
 * Otázka typu pravda/nepravda. Používateľ odpovedá napr. "p", "pravda", "áno"
 * alebo "n", "nepravda", "nie". Trieda rozpoznáva viacero zápisov odpovede.
 */
package com.quizly.model;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

public class TrueFalseQuestion extends Question {

    private static final Set<String> TRUE_WORDS =
            Set.of("p", "pravda", "a", "áno", "ano", "true", "t", "1");
    private static final Set<String> FALSE_WORDS =
            Set.of("n", "nepravda", "nie", "false", "f", "0");

    private final boolean correctAnswer;

    /**
     * Vytvorí otázku pravda/nepravda so zadanou správnou odpoveďou.
     */
    public TrueFalseQuestion(int id, String text, String category, Difficulty difficulty,
                             boolean correctAnswer) {
        super(id, text, category, difficulty);
        this.correctAnswer = correctAnswer;
    }

    /**
     * Vráti správnu odpoveď (true = pravda, false = nepravda).
     */
    public boolean getCorrectAnswer() {
        return correctAnswer;
    }

    /**
     * Vráti typ otázky TRUE_FALSE.
     */
    @Override
    public QuestionType getType() {
        return QuestionType.TRUE_FALSE;
    }

    /**
     * Odpoveď je správna, ak ju vieme rozpoznať a zhoduje sa so správnou hodnotou.
     */
    @Override
    public boolean isCorrect(String answer) {
        return parseAnswer(answer).map(value -> value == correctAnswer).orElse(false);
    }

    /**
     * Odpoveď má platný formát, ak ju vieme rozpoznať ako pravdu alebo nepravdu.
     */
    @Override
    public boolean isValidAnswerFormat(String answer) {
        return parseAnswer(answer).isPresent();
    }

    /**
     * Vráti správnu odpoveď slovom ("Pravda" alebo "Nepravda").
     */
    @Override
    public String getCorrectAnswerText() {
        return correctAnswer ? "Pravda" : "Nepravda";
    }

    /**
     * Vráti nápovedu, ako zadať odpoveď.
     */
    @Override
    public List<String> getAnswerHints() {
        return List.of("Odpovedz: P = pravda, N = nepravda");
    }

    /**
     * Prevedie textovú odpoveď na boolean. Ak odpoveď nie je rozpoznaná,
     * vráti prázdny Optional.
     */
    public static Optional<Boolean> parseAnswer(String answer) {
        if (answer == null) {
            return Optional.empty();
        }
        String normalized = answer.trim().toLowerCase(Locale.ROOT);
        if (TRUE_WORDS.contains(normalized)) {
            return Optional.of(true);
        }
        if (FALSE_WORDS.contains(normalized)) {
            return Optional.of(false);
        }
        return Optional.empty();
    }
}
