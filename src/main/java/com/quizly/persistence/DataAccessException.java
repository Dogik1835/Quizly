/*
 * DataAccessException.java
 * Vlastná kontrolovaná výnimka, ktorá signalizuje chybu pri čítaní
 * alebo zápise dát (napr. súbor sa nedá otvoriť). Obaľuje nízkoúrovňové
 * IOException, aby volajúci kód nemusel poznať detaily práce so súbormi.
 */
package com.quizly.persistence;

public class DataAccessException extends Exception {

    /**
     * Vytvorí výnimku so správou a pôvodnou príčinou chyby.
     */
    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
