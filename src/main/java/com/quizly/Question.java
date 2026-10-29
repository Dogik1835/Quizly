// Question.java
// Abstraktná trieda pre jednu otázku v kvíze. Má text otázky a kategóriu.
// Konkrétne typy otázok (ChoiceQuestion, TrueFalseQuestion) z nej dedia.
package com.quizly;

public abstract class Question {
    private String text;
    private String category;

    // Konštruktor - vytvorí otázku s textom a kategóriou
    public Question(String text, String category) {
        this.text = text;
        this.category = category;
    }

    // Vráti text otázky
    public String getText() {
        return text;
    }

    // Nastaví text otázky
    public void setText(String text) {
        this.text = text;
    }

    // Vráti kategóriu otázky
    public String getCategory() {
        return category;
    }

    // Nastaví kategóriu otázky
    public void setCategory(String category) {
        this.category = category;
    }

    // Skontroluje, či je odpoveď správna (každý typ otázky to robí inak)
    public abstract boolean isCorrect(String answer);

    // Vráti nápovedu, ako odpovedať (napr. zoznam možností)
    public abstract String getHint();

    // Vráti správnu odpoveď ako text
    public abstract String getCorrectAnswer();
}
