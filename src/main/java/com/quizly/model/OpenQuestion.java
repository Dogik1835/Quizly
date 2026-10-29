/*
 * OpenQuestion.java
 * Otázka s otvorenou (vpisovanou) odpoveďou. Otázka môže mať viac
 * akceptovaných odpovedí. Porovnanie ignoruje veľkosť písmen, diakritiku
 * a nadbytočné medzery, takže "Bratislava", "bratislava " aj "BRATISLAVA" sú správne.
 */
package com.quizly.model;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OpenQuestion extends Question {

    private final List<String> acceptedAnswers;

    /**
     * Vytvorí otvorenú otázku so zoznamom akceptovaných odpovedí.
     *
     * @throws IllegalArgumentException ak nie je zadaná žiadna odpoveď alebo je niektorá prázdna
     */
    public OpenQuestion(int id, String text, String category, Difficulty difficulty,
                        List<String> acceptedAnswers) {
        super(id, text, category, difficulty);
        if (acceptedAnswers == null || acceptedAnswers.isEmpty()) {
            throw new IllegalArgumentException("Otvorená otázka musí mať aspoň jednu správnu odpoveď.");
        }
        List<String> cleaned = new ArrayList<>();
        for (String answer : acceptedAnswers) {
            cleaned.add(requireNonBlank(answer, "Správna odpoveď"));
        }
        this.acceptedAnswers = List.copyOf(cleaned);
    }

    /**
     * Vráti nemenný zoznam akceptovaných odpovedí v pôvodnom tvare.
     */
    public List<String> getAcceptedAnswers() {
        return acceptedAnswers;
    }

    /**
     * Vráti typ otázky OPEN.
     */
    @Override
    public QuestionType getType() {
        return QuestionType.OPEN;
    }

    /**
     * Odpoveď je správna, ak sa po normalizácii zhoduje s niektorou akceptovanou odpoveďou.
     */
    @Override
    public boolean isCorrect(String answer) {
        if (answer == null) {
            return false;
        }
        String normalized = normalize(answer);
        return acceptedAnswers.stream()
                .map(OpenQuestion::normalize)
                .anyMatch(normalized::equals);
    }

    /**
     * Vráti prvú akceptovanú odpoveď (hlavný tvar správnej odpovede).
     */
    @Override
    public String getCorrectAnswerText() {
        return acceptedAnswers.get(0);
    }

    /**
     * Vráti nápovedu, že odpoveď sa má napísať.
     */
    @Override
    public List<String> getAnswerHints() {
        return List.of("Napíš odpoveď:");
    }

    /**
     * Normalizuje text na porovnanie: odstráni diakritiku, prevedie na malé
     * písmená a zlúči viacnásobné medzery do jednej.
     */
    static String normalize(String text) {
        String withoutDiacritics = Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return withoutDiacritics.trim().replaceAll("\\s+", " ").toLowerCase(Locale.ROOT);
    }
}
