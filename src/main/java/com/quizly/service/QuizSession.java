/*
 * QuizSession.java
 * Priebeh jednej hry. Drží vybrané otázky, aktuálnu pozíciu v kvíze,
 * vyhodnocuje odpovede hráča, počíta body a správne odpovede
 * a na konci vytvorí objekt GameResult s výsledkom hry.
 */
package com.quizly.service;

import com.quizly.model.GameResult;
import com.quizly.model.Question;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class QuizSession {

    private final String playerName;
    private final String categoryLabel;
    private final List<Question> questions;
    private final List<Question> mistakes = new ArrayList<>();
    private int currentIndex;
    private int score;
    private int correctCount;

    /**
     * Začne novú hru pre hráča so zadanými otázkami.
     *
     * @param categoryLabel názov kategórie hry (alebo "Všetky")
     * @throws IllegalArgumentException ak je meno prázdne alebo chýbajú otázky
     */
    public QuizSession(String playerName, String categoryLabel, List<Question> questions) {
        if (playerName == null || playerName.isBlank()) {
            throw new IllegalArgumentException("Meno hráča nesmie byť prázdne.");
        }
        if (questions == null || questions.isEmpty()) {
            throw new IllegalArgumentException("Hra musí obsahovať aspoň jednu otázku.");
        }
        this.playerName = playerName.trim();
        this.categoryLabel = (categoryLabel == null || categoryLabel.isBlank())
                ? GameResult.ALL_CATEGORIES : categoryLabel.trim();
        this.questions = List.copyOf(questions);
    }

    /**
     * Vráti true, ak v hre zostáva ešte nezodpovedaná otázka.
     */
    public boolean hasNextQuestion() {
        return currentIndex < questions.size();
    }

    /**
     * Vráti true, ak boli zodpovedané všetky otázky.
     */
    public boolean isFinished() {
        return !hasNextQuestion();
    }

    /**
     * Vráti aktuálnu otázku, na ktorú má hráč odpovedať.
     *
     * @throws IllegalStateException ak je hra už skončená
     */
    public Question getCurrentQuestion() {
        if (isFinished()) {
            throw new IllegalStateException("Hra už skončila, nie sú ďalšie otázky.");
        }
        return questions.get(currentIndex);
    }

    /**
     * Vráti poradové číslo aktuálnej otázky (od 1) na zobrazenie hráčovi.
     */
    public int getCurrentQuestionNumber() {
        return currentIndex + 1;
    }

    /**
     * Vráti celkový počet otázok v hre.
     */
    public int getTotalQuestions() {
        return questions.size();
    }

    /**
     * Vyhodnotí odpoveď na aktuálnu otázku, pripočíta body pri správnej
     * odpovedi a posunie hru na ďalšiu otázku.
     *
     * @return true, ak bola odpoveď správna
     * @throws IllegalStateException ak je hra už skončená
     */
    public boolean submitAnswer(String answer) {
        Question question = getCurrentQuestion();
        boolean correct = question.isCorrect(answer);
        if (correct) {
            score += question.getPoints();
            correctCount++;
        } else {
            mistakes.add(question);
        }
        currentIndex++;
        return correct;
    }

    /**
     * Vráti meno hráča.
     */
    public String getPlayerName() {
        return playerName;
    }

    /**
     * Vráti doteraz získané body.
     */
    public int getScore() {
        return score;
    }

    /**
     * Vráti maximálny počet bodov, ktorý sa dá v tejto hre získať.
     */
    public int getMaxScore() {
        return questions.stream()
                .mapToInt(Question::getPoints)
                .sum();
    }

    /**
     * Vráti doterajší počet správnych odpovedí.
     */
    public int getCorrectCount() {
        return correctCount;
    }

    /**
     * Vráti otázky, na ktoré hráč odpovedal nesprávne (na zhrnutie po hre).
     */
    public List<Question> getMistakes() {
        return List.copyOf(mistakes);
    }

    /**
     * Vytvorí výsledok hry s aktuálnym časom.
     *
     * @throws IllegalStateException ak hra ešte neskončila
     */
    public GameResult toResult() {
        return toResult(LocalDateTime.now());
    }

    /**
     * Vytvorí výsledok hry so zadaným časom (užitočné pri testovaní).
     *
     * @throws IllegalStateException ak hra ešte neskončila
     */
    public GameResult toResult(LocalDateTime playedAt) {
        if (!isFinished()) {
            throw new IllegalStateException("Výsledok je možné vytvoriť až po skončení hry.");
        }
        return new GameResult(playerName, categoryLabel, score, getMaxScore(),
                correctCount, questions.size(), playedAt);
    }
}
