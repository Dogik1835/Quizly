package com.quizly;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Quiz {
    private List<Question> questions;

    public Quiz(List<Question> questions) {
        this.questions = questions;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public List<String> getCategories() {
        return questions.stream()
                .map(Question::getCategory)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

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
