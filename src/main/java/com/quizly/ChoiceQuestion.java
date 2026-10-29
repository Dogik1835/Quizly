// ChoiceQuestion.java
// Otázka s možnosťami. Hráč odpovedá číslom možnosti (1, 2, 3...).
package com.quizly;

import java.util.List;

public class ChoiceQuestion extends Question {
    private List<String> options;
    private int correctOption;

    // Konštruktor - vytvorí otázku s možnosťami a číslom správnej možnosti (od 1)
    public ChoiceQuestion(String text, String category, List<String> options, int correctOption) {
        super(text, category);
        this.options = options;
        this.correctOption = correctOption;
    }

    // Vráti zoznam možností
    public List<String> getOptions() {
        return options;
    }

    // Nastaví zoznam možností
    public void setOptions(List<String> options) {
        this.options = options;
    }

    // Vráti číslo správnej možnosti
    public int getCorrectOption() {
        return correctOption;
    }

    // Nastaví číslo správnej možnosti
    public void setCorrectOption(int correctOption) {
        this.correctOption = correctOption;
    }

    // Odpoveď je správna, ak hráč napísal číslo správnej možnosti
    @Override
    public boolean isCorrect(String answer) {
        return answer.trim().equals(String.valueOf(correctOption));
    }

    // Vráti očíslované možnosti, napr. "1) Praha   2) Brno"
    @Override
    public String getHint() {
        String hint = "";
        for (int i = 0; i < options.size(); i++) {
            hint += (i + 1) + ") " + options.get(i) + "   ";
        }
        return hint;
    }

    // Vráti text správnej možnosti
    @Override
    public String getCorrectAnswer() {
        return options.get(correctOption - 1);
    }
}
