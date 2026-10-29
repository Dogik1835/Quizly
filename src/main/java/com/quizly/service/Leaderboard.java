/*
 * Leaderboard.java
 * Rebríček a história výsledkov hier. Uchováva všetky odohrané hry,
 * vie vrátiť najlepšie výsledky (celkovo aj podľa kategórie), históriu
 * konkrétneho hráča a štatistiky (priemerná úspešnosť, počet hier v kategóriách).
 * Na triedenie, filtrovanie a zoskupovanie používa Stream API.
 */
package com.quizly.service;

import com.quizly.model.GameResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class Leaderboard {

    /**
     * Poradie v rebríčku: vyššia percentuálna úspešnosť, potom viac bodov,
     * potom skorší dátum (kto dosiahol výsledok skôr, je vyššie).
     */
    public static final Comparator<GameResult> RANKING = Comparator
            .comparingDouble(GameResult::getPercentage).reversed()
            .thenComparing(Comparator.comparingInt(GameResult::getScore).reversed())
            .thenComparing(GameResult::getPlayedAt);

    private final List<GameResult> results = new ArrayList<>();

    /**
     * Vytvorí prázdny rebríček.
     */
    public Leaderboard() {
    }

    /**
     * Vytvorí rebríček a naplní ho existujúcimi výsledkami (napr. načítanými zo súboru).
     */
    public Leaderboard(Collection<GameResult> initialResults) {
        initialResults.forEach(this::add);
    }

    /**
     * Pridá nový výsledok hry.
     */
    public void add(GameResult result) {
        if (result == null) {
            throw new IllegalArgumentException("Výsledok nesmie byť null.");
        }
        results.add(result);
    }

    /**
     * Vráti všetky výsledky v poradí, v akom boli pridané (nemenná kópia).
     */
    public List<GameResult> getAll() {
        return List.copyOf(results);
    }

    /**
     * Vráti počet uložených výsledkov.
     */
    public int size() {
        return results.size();
    }

    /**
     * Vráti najlepších {@code limit} výsledkov zo všetkých hier.
     */
    public List<GameResult> top(int limit) {
        return results.stream()
                .sorted(RANKING)
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Vráti najlepších {@code limit} výsledkov v zadanej kategórii.
     */
    public List<GameResult> topByCategory(String category, int limit) {
        return results.stream()
                .filter(r -> r.getCategory().equalsIgnoreCase(category))
                .sorted(RANKING)
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Vráti históriu hier daného hráča, od najnovšej po najstaršiu.
     * Meno sa porovnáva bez ohľadu na veľkosť písmen.
     */
    public List<GameResult> historyOf(String playerName) {
        return results.stream()
                .filter(r -> r.getPlayerName().equalsIgnoreCase(playerName.trim()))
                .sorted(Comparator.comparing(GameResult::getPlayedAt).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Vráti najlepší výsledok hráča, ak nejakú hru odohral.
     */
    public Optional<GameResult> bestOf(String playerName) {
        return historyOf(playerName).stream()
                .min(RANKING);
    }

    /**
     * Vypočíta priemernú úspešnosť (v %) zo všetkých hier.
     * Ak nebola odohraná žiadna hra, vráti prázdny OptionalDouble.
     */
    public OptionalDouble averagePercentage() {
        return results.stream()
                .mapToDouble(GameResult::getPercentage)
                .average();
    }

    /**
     * Vypočíta priemernú úspešnosť každého hráča. Mapa je zoradená podľa mena hráča.
     */
    public Map<String, Double> averagePercentageByPlayer() {
        return results.stream()
                .collect(Collectors.groupingBy(GameResult::getPlayerName, TreeMap::new,
                        Collectors.averagingDouble(GameResult::getPercentage)));
    }

    /**
     * Spočíta, koľko hier bolo odohraných v jednotlivých kategóriách.
     */
    public Map<String, Long> gamesPerCategory() {
        return results.stream()
                .collect(Collectors.groupingBy(GameResult::getCategory, TreeMap::new, Collectors.counting()));
    }
}
