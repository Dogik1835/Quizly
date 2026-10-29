// Leaderboard.java
// Rebríček hráčov. Drží výsledky všetkých hier, vie vrátiť najlepších hráčov
// a výsledky jedného hráča.
package com.quizly;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Leaderboard {
    private List<Result> results;

    // Konštruktor - vytvorí rebríček so zoznamom výsledkov
    public Leaderboard(List<Result> results) {
        this.results = results;
    }

    // Vráti všetky výsledky
    public List<Result> getResults() {
        return results;
    }

    // Nastaví zoznam výsledkov
    public void setResults(List<Result> results) {
        this.results = results;
    }

    // Pridá nový výsledok do rebríčka
    public void addResult(Result result) {
        results.add(result);
    }

    // Vráti najlepšie výsledky zoradené od najvyššieho skóre
    public List<Result> getTop(int count) {
        return results.stream()
                .sorted(Comparator.comparingInt(Result::getScore).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    // Vráti všetky výsledky jedného hráča (veľké/malé písmená nehrajú rolu)
    public List<Result> getResultsOf(String playerName) {
        return results.stream()
                .filter(r -> r.getPlayerName().equalsIgnoreCase(playerName))
                .collect(Collectors.toList());
    }
}
