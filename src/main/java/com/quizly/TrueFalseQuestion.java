// TrueFalseQuestion.java
// Otázka typu áno/nie. Hráč odpovedá "a" (áno) alebo "n" (nie).
package com.quizly;

public class TrueFalseQuestion extends Question {
    private boolean trueStatement;

    // Konštruktor - vytvorí otázku a nastaví, či je tvrdenie pravdivé
    public TrueFalseQuestion(String text, String category, boolean trueStatement) {
        super(text, category);
        this.trueStatement = trueStatement;
    }

    // Vráti, či je tvrdenie pravdivé
    public boolean isTrueStatement() {
        return trueStatement;
    }

    // Nastaví, či je tvrdenie pravdivé
    public void setTrueStatement(boolean trueStatement) {
        this.trueStatement = trueStatement;
    }

    // Odpoveď "a" je správna pri pravdivom tvrdení, "n" pri nepravdivom
    @Override
    public boolean isCorrect(String answer) {
        if (answer.trim().equalsIgnoreCase("a")) {
            return trueStatement;
        }
        if (answer.trim().equalsIgnoreCase("n")) {
            return !trueStatement;
        }
        return false;
    }

    // Vráti nápovedu, ako odpovedať
    @Override
    public String getHint() {
        return "a = áno, n = nie";
    }

    // Vráti správnu odpoveď ako "áno" alebo "nie"
    @Override
    public String getCorrectAnswer() {
        if (trueStatement) {
            return "áno";
        }
        return "nie";
    }
}
