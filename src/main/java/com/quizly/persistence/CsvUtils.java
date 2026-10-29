/*
 * CsvUtils.java
 * Pomocné funkcie na prácu s CSV riadkami. Ako oddeľovač stĺpcov sa používa
 * bodkočiarka (;). Hodnoty obsahujúce oddeľovač alebo úvodzovky sa zapíšu
 * v úvodzovkách a vnútorné úvodzovky sa zdvoja (podľa štandardu RFC 4180).
 * Okrem toho poskytuje spájanie/rozdeľovanie zoznamov hodnôt v jednom stĺpci (znakom |).
 */
package com.quizly.persistence;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class CsvUtils {

    public static final char SEPARATOR = ';';
    public static final String LIST_SEPARATOR = "|";

    /**
     * Súkromný konštruktor – trieda obsahuje iba statické metódy.
     */
    private CsvUtils() {
    }

    /**
     * Spojí hodnoty do jedného CSV riadku. Hodnoty, ktoré to potrebujú, ošetrí úvodzovkami.
     */
    public static String join(List<String> values) {
        return values.stream()
                .map(CsvUtils::escape)
                .collect(Collectors.joining(String.valueOf(SEPARATOR)));
    }

    /**
     * Rozdelí CSV riadok na jednotlivé hodnoty. Správne spracuje hodnoty
     * v úvodzovkách (vrátane oddeľovača a zdvojených úvodzoviek vo vnútri).
     *
     * @throws IllegalArgumentException ak riadok obsahuje neuzavreté úvodzovky
     */
    public static List<String> split(String line) {
        List<String> values = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (inQuotes) {
                if (c == '"') {
                    if (i + 1 < line.length() && line.charAt(i + 1) == '"') {
                        current.append('"');
                        i++;
                    } else {
                        inQuotes = false;
                    }
                } else {
                    current.append(c);
                }
            } else if (c == '"') {
                inQuotes = true;
            } else if (c == SEPARATOR) {
                values.add(current.toString());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        if (inQuotes) {
            throw new IllegalArgumentException("Neuzavreté úvodzovky v riadku: " + line);
        }
        values.add(current.toString());
        return values;
    }

    /**
     * Spojí zoznam hodnôt do jedného stĺpca pomocou znaku "|"
     * (napr. možnosti odpovede: "Praha|Brno|Ostrava").
     */
    public static String joinList(List<String> items) {
        return String.join(LIST_SEPARATOR, items);
    }

    /**
     * Rozdelí hodnotu stĺpca podľa znaku "|" na zoznam (orezaný o medzery, bez prázdnych položiek).
     */
    public static List<String> splitList(String value) {
        return Arrays.stream(value.split("\\" + LIST_SEPARATOR))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    /**
     * Ošetrí jednu hodnotu pre zápis do CSV. Zalomenia riadkov nahradí medzerou
     * a ak hodnota obsahuje oddeľovač alebo úvodzovky, obalí ju úvodzovkami.
     */
    static String escape(String value) {
        if (value == null) {
            return "";
        }
        String singleLine = value.replace("\r", " ").replace("\n", " ");
        if (singleLine.indexOf(SEPARATOR) >= 0 || singleLine.indexOf('"') >= 0) {
            return '"' + singleLine.replace("\"", "\"\"") + '"';
        }
        return singleLine;
    }
}
