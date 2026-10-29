/*
 * Difficulty.java
 * Výčtový typ obťažnosti otázky. Každá úroveň má slovenský popis
 * a počet bodov, ktoré hráč získa za správnu odpoveď na otázku.
 */
package com.quizly.model;

import java.util.Locale;

public enum Difficulty {
    EASY("Ľahká", 1),
    MEDIUM("Stredná", 2),
    HARD("Ťažká", 3);

    private final String label;
    private final int points;

    /**
     * Vytvorí úroveň obťažnosti s popisom a bodovou hodnotou.
     */
    Difficulty(String label, int points) {
        this.label = label;
        this.points = points;
    }

    /**
     * Vráti popis obťažnosti v slovenčine (na výpis používateľovi).
     */
    public String getLabel() {
        return label;
    }

    /**
     * Vráti počet bodov za správnu odpoveď na otázku tejto obťažnosti.
     */
    public int getPoints() {
        return points;
    }

    /**
     * Prevedie text na obťažnosť. Akceptuje názov konštanty ("EASY", "easy")
     * aj poradové číslo úrovne ("1" = EASY, "2" = MEDIUM, "3" = HARD).
     *
     * @throws IllegalArgumentException ak text nezodpovedá žiadnej úrovni
     */
    public static Difficulty parse(String text) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Obťažnosť nesmie byť prázdna.");
        }
        String normalized = text.trim().toUpperCase(Locale.ROOT);
        Difficulty[] all = values();
        if (normalized.matches("\\d+")) {
            int index = Integer.parseInt(normalized) - 1;
            if (index >= 0 && index < all.length) {
                return all[index];
            }
        }
        for (Difficulty difficulty : all) {
            if (difficulty.name().equals(normalized)) {
                return difficulty;
            }
        }
        throw new IllegalArgumentException("Neznáma obťažnosť: " + text);
    }
}
