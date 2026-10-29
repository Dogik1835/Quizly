package com.quizly;

public class TrueFalseQuestion extends Question {
    private boolean trueStatement;

    public TrueFalseQuestion(String text, String category, boolean trueStatement) {
        super(text, category);
        this.trueStatement = trueStatement;
    }

    public boolean isTrueStatement() {
        return trueStatement;
    }

    public void setTrueStatement(boolean trueStatement) {
        this.trueStatement = trueStatement;
    }

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

    @Override
    public String getHint() {
        return "a = áno, n = nie";
    }

    @Override
    public String getCorrectAnswer() {
        if (trueStatement) {
            return "áno";
        }
        return "nie";
    }
}
