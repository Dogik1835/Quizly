// FileManager.java
// Načítanie otázok zo súboru a načítanie/uloženie výsledkov do súboru (CSV, oddeľovač ;).
//
// Formát otázok:   CHOICE;kategória;text;možnosť1,možnosť2,možnosť3;číslo správnej
//                  TF;kategória;text;true/false
// Formát výsledkov: meno;skóre;počet otázok
package com.quizly;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileManager {

    // Načíta otázky zo súboru. Chybné riadky preskočí.
    public static List<Question> loadQuestions(String fileName) {
        List<Question> questions = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(Path.of(fileName));
            for (String line : lines) {
                try {
                    String[] parts = line.split(";");
                    if (parts[0].equals("CHOICE")) {
                        List<String> options = Arrays.asList(parts[3].split(","));
                        int correct = Integer.parseInt(parts[4]);
                        questions.add(new ChoiceQuestion(parts[2], parts[1], options, correct));
                    } else if (parts[0].equals("TF")) {
                        boolean correct = Boolean.parseBoolean(parts[3]);
                        questions.add(new TrueFalseQuestion(parts[2], parts[1], correct));
                    }
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    System.out.println("Chybný riadok v súbore, preskakujem: " + line);
                }
            }
        } catch (IOException e) {
            System.out.println("Nepodarilo sa načítať otázky: " + e.getMessage());
        }
        return questions;
    }

    // Načíta výsledky zo súboru. Ak súbor ešte neexistuje, vráti prázdny zoznam.
    public static List<Result> loadResults(String fileName) {
        List<Result> results = new ArrayList<>();
        if (!Files.exists(Path.of(fileName))) {
            return results;
        }
        try {
            List<String> lines = Files.readAllLines(Path.of(fileName));
            for (String line : lines) {
                try {
                    String[] parts = line.split(";");
                    results.add(new Result(parts[0], Integer.parseInt(parts[1]), Integer.parseInt(parts[2])));
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    System.out.println("Chybný riadok v súbore, preskakujem: " + line);
                }
            }
        } catch (IOException e) {
            System.out.println("Nepodarilo sa načítať výsledky: " + e.getMessage());
        }
        return results;
    }

    // Uloží všetky výsledky do súboru (prepíše starý obsah)
    public static void saveResults(String fileName, List<Result> results) {
        List<String> lines = new ArrayList<>();
        for (Result r : results) {
            lines.add(r.getPlayerName() + ";" + r.getScore() + ";" + r.getTotal());
        }
        try {
            Files.write(Path.of(fileName), lines);
        } catch (IOException e) {
            System.out.println("Nepodarilo sa uložiť výsledky: " + e.getMessage());
        }
    }
}
