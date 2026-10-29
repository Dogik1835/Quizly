/*
 * QuestionType.java
 * Výčtový typ druhov otázok. Každý typ má krátky kód, ktorý sa používa
 * v CSV súbore na rozlíšenie typu otázky, a popis pre používateľa.
 */
package com.quizly.model;

import java.util.Locale;

public enum QuestionType {
    MULTIPLE_CHOICE("MC", "Výber z možností"),
    TRUE_FALSE("TF", "Pravda / nepravda"),
    OPEN("OPEN", "Otvorená odpoveď");

    private final String code;
    private final String label;

    /**
     * Vytvorí typ otázky s kódom pre súbor a popisom pre používateľa.
     */
    QuestionType(String code, String label) {
        this.code = code;
        this.label = label;
    }

    /**
     * Vráti krátky kód typu, ktorý sa zapisuje do CSV súboru.
     */
    public String getCode() {
        return code;
    }

    /**
     * Vráti popis typu otázky v slovenčine.
     */
    public String getLabel() {
        return label;
    }

    /**
     * Nájde typ otázky podľa kódu zo súboru (bez ohľadu na veľkosť písmen).
     *
     * @throws IllegalArgumentException ak kód nepatrí žiadnemu typu
     */
    public static QuestionType fromCode(String code) {
        if (code != null) {
            String normalized = code.trim().toUpperCase(Locale.ROOT);
            for (QuestionType type : values()) {
                if (type.code.equals(normalized)) {
                    return type;
                }
            }
        }
        throw new IllegalArgumentException("Neznámy typ otázky: " + code);
    }
}
