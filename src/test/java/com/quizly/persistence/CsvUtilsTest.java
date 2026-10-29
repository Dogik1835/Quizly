/*
 * CsvUtilsTest.java
 * Unit testy pomocných CSV funkcií: rozdelenie a spojenie riadku vrátane
 * hodnôt s oddeľovačom a úvodzovkami a práca so zoznamami v jednom stĺpci.
 */
package com.quizly.persistence;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CsvUtilsTest {

    /**
     * Overí rozdelenie jednoduchého riadku vrátane prázdnej poslednej hodnoty.
     */
    @Test
    void splitSimpleLine() {
        assertEquals(List.of("a", "b", ""), CsvUtils.split("a;b;"));
    }

    /**
     * Overí, že hodnota s bodkočiarkou a úvodzovkami prejde zápisom a čítaním bez zmeny.
     */
    @Test
    void joinAndSplitRoundTrip() {
        List<String> values = List.of("Čo je \"OOP\"?", "a;b", "obyčajný text", "");
        String line = CsvUtils.join(values);
        assertEquals(values, CsvUtils.split(line));
    }

    /**
     * Overí, že zalomenie riadku v hodnote sa pri zápise nahradí medzerou.
     */
    @Test
    void newlinesAreReplaced() {
        assertEquals("riadok1 riadok2", CsvUtils.join(List.of("riadok1\nriadok2")));
    }

    /**
     * Overí, že neuzavreté úvodzovky vyhodia výnimku.
     */
    @Test
    void unclosedQuotesFail() {
        assertThrows(IllegalArgumentException.class, () -> CsvUtils.split("\"neuzavreté;b"));
    }

    /**
     * Overí rozdelenie a spojenie zoznamu hodnôt oddelených znakom "|".
     */
    @Test
    void listHelpers() {
        assertEquals(List.of("Praha", "Brno"), CsvUtils.splitList(" Praha | | Brno "));
        assertEquals("Praha|Brno", CsvUtils.joinList(List.of("Praha", "Brno")));
    }
}
