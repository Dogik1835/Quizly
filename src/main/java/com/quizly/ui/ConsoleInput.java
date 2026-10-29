/*
 * ConsoleInput.java
 * Načítanie a validácia vstupu od používateľa z konzoly. Ponúka metódy
 * na načítanie textu, neprázdneho textu, čísla v rozsahu a odpovede áno/nie.
 * Pri neplatnom vstupe vypíše chybu a otázku zopakuje. Ak sa vstup
 * ukončí (napr. Ctrl+D), vyhodí InputClosedException.
 */
package com.quizly.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Locale;

public class ConsoleInput {

    /**
     * Výnimka signalizujúca, že vstup bol ukončený a nie je možné čítať ďalej.
     */
    public static class InputClosedException extends RuntimeException {

        /**
         * Vytvorí výnimku s predvolenou správou.
         */
        public InputClosedException() {
            super("Vstup bol ukončený.");
        }
    }

    private final BufferedReader in;
    private final PrintStream out;

    /**
     * Vytvorí čítač vstupu nad zadaným vstupom a výstupom (výstup slúži na výpis výziev a chýb).
     */
    public ConsoleInput(BufferedReader in, PrintStream out) {
        this.in = in;
        this.out = out;
    }

    /**
     * Vypíše výzvu a načíta jeden riadok textu (môže byť aj prázdny). Vráti ho orezaný o medzery.
     *
     * @throws InputClosedException ak bol vstup ukončený
     */
    public String readLine(String prompt) {
        out.print(prompt);
        out.flush();
        try {
            String line = in.readLine();
            if (line == null) {
                throw new InputClosedException();
            }
            return line.trim();
        } catch (IOException e) {
            throw new InputClosedException();
        }
    }

    /**
     * Načíta neprázdny text. Kým používateľ nič nezadá, výzvu opakuje.
     */
    public String readNonBlank(String prompt) {
        while (true) {
            String value = readLine(prompt);
            if (!value.isEmpty()) {
                return value;
            }
            out.println("  ! Hodnota nesmie byť prázdna.");
        }
    }

    /**
     * Načíta celé číslo v rozsahu min až max (vrátane). Pri neplatnom vstupe
     * (nie je číslo alebo je mimo rozsahu) vypíše chybu a opýta sa znova.
     */
    public int readInt(String prompt, int min, int max) {
        while (true) {
            Integer number = parseInRange(readLine(prompt), min, max);
            if (number != null) {
                return number;
            }
        }
    }

    /**
     * Načíta celé číslo v rozsahu, pričom prázdny vstup znamená predvolenú hodnotu.
     */
    public int readIntOrDefault(String prompt, int min, int max, int defaultValue) {
        while (true) {
            String value = readLine(prompt);
            if (value.isEmpty()) {
                return defaultValue;
            }
            Integer number = parseInRange(value, min, max);
            if (number != null) {
                return number;
            }
        }
    }

    /**
     * Načíta odpoveď áno/nie. Akceptuje "a", "áno", "ano", "y", "yes" a "n", "nie", "no".
     */
    public boolean readYesNo(String prompt) {
        while (true) {
            String value = readLine(prompt + " (a/n): ").toLowerCase(Locale.ROOT);
            switch (value) {
                case "a":
                case "áno":
                case "ano":
                case "y":
                case "yes":
                    return true;
                case "n":
                case "nie":
                case "no":
                    return false;
                default:
                    out.println("  ! Odpovedz 'a' (áno) alebo 'n' (nie).");
            }
        }
    }

    /**
     * Prevedie text na číslo a overí rozsah. Pri chybe vypíše hlásenie a vráti null.
     */
    private Integer parseInRange(String value, int min, int max) {
        try {
            int number = Integer.parseInt(value);
            if (number >= min && number <= max) {
                return number;
            }
            out.printf("  ! Zadaj číslo od %d do %d.%n", min, max);
        } catch (NumberFormatException e) {
            out.println("  ! '" + value + "' nie je platné číslo.");
        }
        return null;
    }
}
