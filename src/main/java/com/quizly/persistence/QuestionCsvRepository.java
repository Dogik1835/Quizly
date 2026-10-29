/*
 * QuestionCsvRepository.java
 * Ukladanie a načítanie otázok z CSV súboru. Formát riadku:
 *   typ;id;kategória;obťažnosť;text;správna odpoveď;možnosti
 * - MC   (výber z možností): správna odpoveď = číslo možnosti od 1, možnosti oddelené "|"
 * - TF   (pravda/nepravda):  správna odpoveď = true / false, možnosti prázdne
 * - OPEN (otvorená):         správna odpoveď = akceptované odpovede oddelené "|"
 */
package com.quizly.persistence;

import com.quizly.model.Difficulty;
import com.quizly.model.MultipleChoiceQuestion;
import com.quizly.model.OpenQuestion;
import com.quizly.model.Question;
import com.quizly.model.QuestionType;
import com.quizly.model.TrueFalseQuestion;

import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

public class QuestionCsvRepository extends AbstractCsvRepository<Question> {

    private static final int COLUMNS = 7;

    /**
     * Vytvorí repozitár otázok pracujúci so zadaným CSV súborom.
     */
    public QuestionCsvRepository(Path file) {
        super(file);
    }

    /**
     * Vráti hlavičku CSV súboru s otázkami.
     */
    @Override
    protected List<String> header() {
        return List.of("typ", "id", "kategoria", "obtiaznost", "text", "spravna_odpoved", "moznosti");
    }

    /**
     * Vytvorí otázku správneho typu z hodnôt jedného riadku.
     *
     * @throws IllegalArgumentException ak riadok obsahuje neplatné údaje
     */
    @Override
    protected Question parse(List<String> fields) {
        requireColumns(fields, COLUMNS - 1);
        QuestionType type = QuestionType.fromCode(fields.get(0));
        int id = parseInt(fields.get(1), "ID");
        String category = fields.get(2);
        Difficulty difficulty = Difficulty.parse(fields.get(3));
        String text = fields.get(4);
        String answer = fields.get(5).trim();
        String options = fields.size() > 6 ? fields.get(6) : "";

        switch (type) {
            case MULTIPLE_CHOICE:
                int correctIndex = parseInt(answer, "Číslo správnej možnosti") - 1;
                return new MultipleChoiceQuestion(id, text, category, difficulty,
                        CsvUtils.splitList(options), correctIndex);
            case TRUE_FALSE:
                return new TrueFalseQuestion(id, text, category, difficulty, parseBoolean(answer));
            case OPEN:
                return new OpenQuestion(id, text, category, difficulty, CsvUtils.splitList(answer));
            default:
                throw new IllegalArgumentException("Nepodporovaný typ otázky: " + type);
        }
    }

    /**
     * Prevedie otázku na hodnoty jedného riadku podľa jej typu.
     */
    @Override
    protected List<String> format(Question question) {
        String answer;
        String options = "";
        if (question instanceof MultipleChoiceQuestion) {
            MultipleChoiceQuestion mc = (MultipleChoiceQuestion) question;
            answer = String.valueOf(mc.getCorrectIndex() + 1);
            options = CsvUtils.joinList(mc.getOptions());
        } else if (question instanceof TrueFalseQuestion) {
            answer = String.valueOf(((TrueFalseQuestion) question).getCorrectAnswer());
        } else if (question instanceof OpenQuestion) {
            answer = CsvUtils.joinList(((OpenQuestion) question).getAcceptedAnswers());
        } else {
            throw new IllegalArgumentException("Nepodporovaný typ otázky: " + question.getClass());
        }
        return List.of(
                question.getType().getCode(),
                String.valueOf(question.getId()),
                question.getCategory(),
                question.getDifficulty().name(),
                question.getText(),
                answer,
                options);
    }

    /**
     * Prevedie text "true"/"false" na boolean. Iné hodnoty považuje za chybu.
     *
     * @throws IllegalArgumentException ak text nie je true ani false
     */
    private static boolean parseBoolean(String value) {
        String normalized = value.trim().toLowerCase(Locale.ROOT);
        if (normalized.equals("true")) {
            return true;
        }
        if (normalized.equals("false")) {
            return false;
        }
        throw new IllegalArgumentException("Odpoveď musí byť true alebo false: '" + value + "'");
    }
}
