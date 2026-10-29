// Quiz.java
// Drží všetky otázky. Vie vrátiť zoznam kategórií a vybrať náhodné otázky do hry.
package com.quizly;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Quiz {
    private List<Question> questions;

    // Konštruktor - vytvorí kvíz so zoznamom otázok
    public Quiz(List<Question> questions) {
        this.questions = questions;
    }

    // Vráti všetky otázky
    public List<Question> getQuestions() {
        return questions;
    }

    // Nastaví zoznam otázok
    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    // Vráti kategórie bez opakovania, zoradené podľa abecedy
    public List<String> getCategories() {
        return questions.stream()
                .map(Question::getCategory)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    // Vyberie náhodné otázky z kategórie (ak je category null, tak zo všetkých)
    public List<Question> getRandomQuestions(String category, int count) {
        List<Question> filtered = questions.stream()
                .filter(q -> category == null || q.getCategory().equals(category))
                .collect(Collectors.toList());
        Collections.shuffle(filtered);
        return filtered.stream()
                .limit(count)
                .collect(Collectors.toList());
    }
}
