/*
 * QuestionBank.java
 * Zásobník (databáza) otázok v pamäti. Spravuje zoznam otázok, umožňuje
 * ich pridávanie, mazanie a vyhľadávanie podľa ID, filtrovanie podľa
 * kategórie a obťažnosti, fulltextové hľadanie a náhodný výber otázok do hry.
 * Na operácie nad dátami používa Stream API a lambda výrazy.
 */
package com.quizly.service;

import com.quizly.model.Difficulty;
import com.quizly.model.Question;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class QuestionBank {

    private final List<Question> questions = new ArrayList<>();
    private final Map<Integer, Question> questionsById = new HashMap<>();

    /**
     * Vytvorí prázdny zásobník otázok.
     */
    public QuestionBank() {
    }

    /**
     * Vytvorí zásobník a naplní ho zadanými otázkami.
     *
     * @throws IllegalArgumentException ak sa v kolekcii opakuje ID otázky
     */
    public QuestionBank(Collection<? extends Question> initialQuestions) {
        initialQuestions.forEach(this::add);
    }

    /**
     * Pridá otázku do zásobníka.
     *
     * @throws IllegalArgumentException ak už existuje otázka s rovnakým ID
     */
    public void add(Question question) {
        if (question == null) {
            throw new IllegalArgumentException("Otázka nesmie byť null.");
        }
        if (questionsById.containsKey(question.getId())) {
            throw new IllegalArgumentException("Otázka s ID " + question.getId() + " už existuje.");
        }
        questions.add(question);
        questionsById.put(question.getId(), question);
    }

    /**
     * Odstráni otázku podľa ID. Vráti true, ak otázka existovala a bola odstránená.
     */
    public boolean remove(int id) {
        Question removed = questionsById.remove(id);
        if (removed == null) {
            return false;
        }
        questions.remove(removed);
        return true;
    }

    /**
     * Nájde otázku podľa ID. Ak neexistuje, vráti prázdny Optional.
     */
    public Optional<Question> findById(int id) {
        return Optional.ofNullable(questionsById.get(id));
    }

    /**
     * Vráti všetky otázky zoradené podľa ID (nemenná kópia zoznamu).
     */
    public List<Question> getAll() {
        return questions.stream()
                .sorted(Comparator.comparingInt(Question::getId))
                .collect(Collectors.toUnmodifiableList());
    }

    /**
     * Vráti počet otázok v zásobníku.
     */
    public int size() {
        return questions.size();
    }

    /**
     * Vráti true, ak zásobník neobsahuje žiadne otázky.
     */
    public boolean isEmpty() {
        return questions.isEmpty();
    }

    /**
     * Vráti ďalšie voľné ID (najvyššie existujúce ID + 1, pri prázdnom zásobníku 1).
     */
    public int nextId() {
        return questions.stream()
                .mapToInt(Question::getId)
                .max()
                .orElse(0) + 1;
    }

    /**
     * Vráti abecedne zoradenú množinu všetkých kategórií otázok.
     */
    public Set<String> getCategories() {
        return questions.stream()
                .map(Question::getCategory)
                .collect(Collectors.toCollection(TreeSet::new));
    }

    /**
     * Vyfiltruje otázky podľa kategórie a obťažnosti. Hodnota null znamená
     * "ľubovoľná" (filter sa nepoužije). Kategória sa porovnáva bez ohľadu
     * na veľkosť písmen.
     */
    public List<Question> filter(String category, Difficulty difficulty) {
        return questions.stream()
                .filter(matches(category, difficulty))
                .sorted(Comparator.comparingInt(Question::getId))
                .collect(Collectors.toList());
    }

    /**
     * Vyhľadá otázky, ktorých text obsahuje zadané slovo (bez ohľadu na veľkosť písmen).
     */
    public List<Question> search(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return getAll();
        }
        String needle = keyword.trim().toLowerCase(Locale.ROOT);
        return questions.stream()
                .filter(q -> q.getText().toLowerCase(Locale.ROOT).contains(needle))
                .sorted(Comparator.comparingInt(Question::getId))
                .collect(Collectors.toList());
    }

    /**
     * Spočíta otázky v jednotlivých kategóriách. Výsledná mapa je zoradená podľa názvu kategórie.
     */
    public Map<String, Long> countByCategory() {
        return questions.stream()
                .collect(Collectors.groupingBy(Question::getCategory, TreeMap::new, Collectors.counting()));
    }

    /**
     * Náhodne vyberie najviac {@code count} otázok vyhovujúcich filtru
     * (kategória a obťažnosť, null = ľubovoľná). Ak je vyhovujúcich otázok
     * menej, vráti všetky v náhodnom poradí.
     *
     * @throws IllegalArgumentException ak count nie je kladné číslo
     */
    public List<Question> pickRandom(String category, Difficulty difficulty, int count, Random random) {
        if (count <= 0) {
            throw new IllegalArgumentException("Počet otázok musí byť kladný.");
        }
        List<Question> candidates = filter(category, difficulty);
        Collections.shuffle(candidates, random);
        return candidates.stream()
                .limit(count)
                .collect(Collectors.toList());
    }

    /**
     * Vytvorí podmienku (predikát) pre filtrovanie podľa kategórie a obťažnosti.
     */
    private static Predicate<Question> matches(String category, Difficulty difficulty) {
        Predicate<Question> byCategory = q -> category == null || q.getCategory().equalsIgnoreCase(category.trim());
        Predicate<Question> byDifficulty = q -> difficulty == null || q.getDifficulty() == difficulty;
        return byCategory.and(byDifficulty);
    }
}
