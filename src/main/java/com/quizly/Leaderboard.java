package com.quizly;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Leaderboard {
    private List<Result> results;

    public Leaderboard(List<Result> results) {
        this.results = results;
    }

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }

    public void addResult(Result result) {
        results.add(result);
    }

    public List<Result> getTop(int count) {
        return results.stream()
                .sorted(Comparator.comparingInt(Result::getScore).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    public List<Result> getResultsOf(String playerName) {
        return results.stream()
                .filter(r -> r.getPlayerName().equalsIgnoreCase(playerName))
                .collect(Collectors.toList());
    }
}
