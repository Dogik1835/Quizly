/*
 * Question.java
 * Abstraktná trieda reprezentujúca jednu kvízovú otázku. Obsahuje spoločné
 * údaje všetkých otázok (ID, text, kategória, obťažnosť) a ich validáciu.
 * Konkrétne typy otázok (výber z možností, pravda/nepravda, otvorená)
 * z nej dedia a implementujú vlastné vyhodnotenie odpovede.
 */
package com.quizly.model;

import java.util.List;
import java.util.Objects;

public abstract class Question {

    private final int id;
    private String text;
    private String category;
    private Difficulty difficulty;

    /**
     * Spoločný konštruktor pre všetky typy otázok. Overí, že ID je kladné
     * a text ani kategória nie sú prázdne.
     *
     * @throws IllegalArgumentException pri neplatných údajoch
     */
    protected Question(int id, String text, String category, Difficulty difficulty) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID otázky musí byť kladné číslo.");
        }
        this.id = id;
        setText(text);
        setCategory(category);
        setDifficulty(difficulty);
    }

    /**
     * Vráti jedinečný identifikátor otázky.
     */
    public int getId() {
        return id;
    }

    /**
     * Vráti znenie otázky.
     */
    public String getText() {
        return text;
    }

    /**
     * Nastaví nové znenie otázky. Text nesmie byť prázdny.
     */
    public void setText(String text) {
        this.text = requireNonBlank(text, "Text otázky");
    }

    /**
     * Vráti kategóriu (tému), do ktorej otázka patrí.
     */
    public String getCategory() {
        return category;
    }

    /**
     * Nastaví kategóriu otázky. Kategória nesmie byť prázdna.
     */
    public void setCategory(String category) {
        this.category = requireNonBlank(category, "Kategória");
    }

    /**
     * Vráti obťažnosť otázky.
     */
    public Difficulty getDifficulty() {
        return difficulty;
    }

    /**
     * Nastaví obťažnosť otázky. Hodnota nesmie byť null.
     */
    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = Objects.requireNonNull(difficulty, "Obťažnosť nesmie byť null.");
    }

    /**
     * Vráti počet bodov, ktoré hráč získa za správnu odpoveď
     * (závisí od obťažnosti otázky).
     */
    public int getPoints() {
        return difficulty.getPoints();
    }

    /**
     * Vráti typ otázky (každá podtrieda vracia svoj typ).
     */
    public abstract QuestionType getType();

    /**
     * Zistí, či je zadaná odpoveď používateľa správna.
     */
    public abstract boolean isCorrect(String answer);

    /**
     * Zistí, či má odpoveď správny formát pre tento typ otázky
     * (napr. číslo možnosti pri výbere z možností). Predvolene je
     * platná každá neprázdna odpoveď.
     */
    public boolean isValidAnswerFormat(String answer) {
        return answer != null && !answer.isBlank();
    }

    /**
     * Vráti textový popis správnej odpovede (zobrazí sa po chybnej odpovedi).
     */
    public abstract String getCorrectAnswerText();

    /**
     * Vráti riadky s nápovedou, ktoré sa zobrazia pod otázkou
     * (napr. zoznam možností alebo spôsob zadania odpovede).
     */
    public abstract List<String> getAnswerHints();

    /**
     * Pomocná metóda: overí, že text nie je prázdny a vráti ho orezaný o medzery.
     */
    protected static String requireNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " nesmie byť prázdny/a.");
        }
        return value.trim();
    }

    /**
     * Dve otázky sú rovnaké, ak majú rovnaké ID.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Question)) {
            return false;
        }
        return id == ((Question) o).id;
    }

    /**
     * Vráti hash kód odvodený od ID otázky (v súlade s equals).
     */
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    /**
     * Vráti stručný jednoriadkový popis otázky pre výpisy v zozname.
     */
    @Override
    public String toString() {
        return String.format("#%d [%s | %s | %s] %s",
                id, category, difficulty.getLabel(), getType().getLabel(), text);
    }
}
