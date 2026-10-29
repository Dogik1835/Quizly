package com.quizly;
import java.util.List;

public class ChoiceQuestion extends Question {
    private List<String> options;
    private int correctOption;

    public ChoiceQuestion(String text, String category, List<String> options, int correctOption) {
        super(text, category);
        this.options = options;
        this.correctOption = correctOption;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public int getCorrectOption() {
        return correctOption;
    }

    public void setCorrectOption(int correctOption) {
        this.correctOption = correctOption;
    }

    @Override
    public boolean isCorrect(String answer) {
        return answer.trim().equals(String.valueOf(correctOption));
    }

    @Override
    public String getHint() {
        String hint = "";
        for (int i = 0; i < options.size(); i++) {
            hint += (i + 1) + ") " + options.get(i) + "   ";
        }
        return hint;
    }

    @Override
    public String getCorrectAnswer() {
        return options.get(correctOption - 1);
    }
}
