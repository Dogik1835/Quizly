/*
 * ResultCsvRepository.java
 * Ukladanie a načítanie histórie výsledkov hier z CSV súboru. Formát riadku:
 *   hráč;kategória;body;max_body;správne;počet_otázok;dátum_a_čas (ISO-8601)
 */
package com.quizly.persistence;

import com.quizly.model.GameResult;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

public class ResultCsvRepository extends AbstractCsvRepository<GameResult> {

    private static final int COLUMNS = 7;

    /**
     * Vytvorí repozitár výsledkov pracujúci so zadaným CSV súborom.
     */
    public ResultCsvRepository(Path file) {
        super(file);
    }

    /**
     * Vráti hlavičku CSV súboru s výsledkami.
     */
    @Override
    protected List<String> header() {
        return List.of("hrac", "kategoria", "body", "max_body", "spravne", "pocet_otazok", "datum");
    }

    /**
     * Vytvorí výsledok hry z hodnôt jedného riadku.
     *
     * @throws IllegalArgumentException ak riadok obsahuje neplatné údaje
     */
    @Override
    protected GameResult parse(List<String> fields) {
        requireColumns(fields, COLUMNS);
        LocalDateTime playedAt;
        try {
            playedAt = LocalDateTime.parse(fields.get(6).trim());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Neplatný dátum: '" + fields.get(6) + "'");
        }
        return new GameResult(
                fields.get(0),
                fields.get(1),
                parseInt(fields.get(2), "Body"),
                parseInt(fields.get(3), "Maximum bodov"),
                parseInt(fields.get(4), "Počet správnych odpovedí"),
                parseInt(fields.get(5), "Počet otázok"),
                playedAt);
    }

    /**
     * Prevedie výsledok hry na hodnoty jedného riadku.
     */
    @Override
    protected List<String> format(GameResult result) {
        return List.of(
                result.getPlayerName(),
                result.getCategory(),
                String.valueOf(result.getScore()),
                String.valueOf(result.getMaxScore()),
                String.valueOf(result.getCorrectAnswers()),
                String.valueOf(result.getTotalQuestions()),
                result.getPlayedAt().withNano(0).toString());
    }
}
