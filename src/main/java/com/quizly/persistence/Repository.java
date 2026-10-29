/*
 * Repository.java
 * Rozhranie pre ukladanie a načítanie dát zo/do trvalého úložiska.
 * Vďaka nemu je zvyšok aplikácie nezávislý od formátu súboru – dnes CSV,
 * no dá sa doplniť napr. implementácia pre JSON bez zmeny ostatného kódu.
 *
 * @param <T> typ ukladaných objektov
 */
package com.quizly.persistence;

import java.util.Collection;
import java.util.List;

public interface Repository<T> {

    /**
     * Načíta všetky uložené objekty. Ak úložisko ešte neexistuje, vráti prázdny zoznam.
     *
     * @throws DataAccessException ak sa úložisko nedá prečítať
     */
    List<T> loadAll() throws DataAccessException;

    /**
     * Uloží všetky zadané objekty (pôvodný obsah úložiska sa prepíše).
     *
     * @throws DataAccessException ak sa do úložiska nedá zapisovať
     */
    void saveAll(Collection<? extends T> items) throws DataAccessException;
}
