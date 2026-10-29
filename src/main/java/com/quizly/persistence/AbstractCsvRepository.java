/*
 * AbstractCsvRepository.java
 * Spoločný základ pre repozitáre ukladajúce dáta do CSV súboru.
 * Rieši čítanie a zápis súboru (kódovanie UTF-8, hlavička, prázdne riadky,
 * komentáre začínajúce '#', bezpečný zápis cez dočasný súbor) a ošetrenie výnimiek.
 * Podtriedy iba definujú, ako sa jeden objekt prevedie na riadok a späť
 * (návrhový vzor Template Method).
 *
 * @param <T> typ ukladaných objektov
 */
package com.quizly.persistence;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class AbstractCsvRepository<T> implements Repository<T> {

    private final Path file;
    private final List<String> warnings = new ArrayList<>();

    /**
     * Vytvorí repozitár pracujúci so zadaným súborom.
     */
    protected AbstractCsvRepository(Path file) {
        if (file == null) {
            throw new IllegalArgumentException("Cesta k súboru nesmie byť null.");
        }
        this.file = file;
    }

    /**
     * Vráti cestu k súboru, s ktorým repozitár pracuje.
     */
    public Path getFile() {
        return file;
    }

    /**
     * Vráti upozornenia z posledného načítania (napr. preskočené chybné riadky).
     */
    public List<String> getWarnings() {
        return List.copyOf(warnings);
    }

    /**
     * Načíta všetky záznamy zo súboru. Prvý riadok (hlavička), prázdne riadky
     * a komentáre sa preskočia. Chybný riadok sa nezahodí celé načítanie –
     * iba sa preskočí a zapíše sa upozornenie (viď {@link #getWarnings()}).
     *
     * @throws DataAccessException ak sa súbor nedá prečítať
     */
    @Override
    public List<T> loadAll() throws DataAccessException {
        warnings.clear();
        List<T> items = new ArrayList<>();
        if (!Files.exists(file)) {
            return items;
        }
        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String line;
            int lineNumber = 0;
            boolean headerSkipped = false;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.isBlank() || line.trim().startsWith("#")) {
                    continue;
                }
                if (!headerSkipped) {
                    headerSkipped = true;
                    continue;
                }
                try {
                    items.add(parse(CsvUtils.split(line)));
                } catch (IllegalArgumentException | IndexOutOfBoundsException e) {
                    warnings.add("Riadok " + lineNumber + " bol preskočený: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new DataAccessException("Nepodarilo sa načítať súbor " + file + ": " + e.getMessage(), e);
        }
        return items;
    }

    /**
     * Uloží všetky záznamy do súboru. Najprv zapíše dočasný súbor a až potom
     * ním nahradí pôvodný, aby sa pri chybe počas zápisu nepoškodili existujúce dáta.
     *
     * @throws DataAccessException ak sa súbor nedá zapísať
     */
    @Override
    public void saveAll(Collection<? extends T> items) throws DataAccessException {
        try {
            Path parent = file.toAbsolutePath().getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Path temp = Files.createTempFile(parent, "quizly", ".tmp");
            try (BufferedWriter writer = Files.newBufferedWriter(temp, StandardCharsets.UTF_8)) {
                writer.write(CsvUtils.join(header()));
                writer.newLine();
                for (T item : items) {
                    writer.write(CsvUtils.join(format(item)));
                    writer.newLine();
                }
            }
            Files.move(temp, file, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new DataAccessException("Nepodarilo sa uložiť súbor " + file + ": " + e.getMessage(), e);
        }
    }

    /**
     * Vráti názvy stĺpcov, ktoré sa zapíšu ako hlavička súboru.
     */
    protected abstract List<String> header();

    /**
     * Prevedie hodnoty z jedného riadku súboru na objekt.
     *
     * @throws IllegalArgumentException ak riadok obsahuje neplatné údaje
     */
    protected abstract T parse(List<String> fields);

    /**
     * Prevedie objekt na zoznam hodnôt jedného riadku súboru.
     */
    protected abstract List<String> format(T item);

    /**
     * Pomocná metóda: overí, že riadok má aspoň požadovaný počet stĺpcov.
     *
     * @throws IllegalArgumentException ak stĺpcov je menej
     */
    protected static void requireColumns(List<String> fields, int count) {
        if (fields.size() < count) {
            throw new IllegalArgumentException(
                    "očakávaných " + count + " stĺpcov, nájdených " + fields.size());
        }
    }

    /**
     * Pomocná metóda: prevedie text na celé číslo so zrozumiteľnou chybovou správou.
     *
     * @throws IllegalArgumentException ak text nie je celé číslo
     */
    protected static int parseInt(String value, String fieldName) {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(fieldName + " nie je platné číslo: '" + value + "'");
        }
    }
}
