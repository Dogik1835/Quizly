# Quizly

Konzolová kvízová aplikácia v Jave – semestrálna práca. Hráč si zvolí kategóriu,
obťažnosť a počet otázok, odpovedá na náhodne vybrané otázky a jeho výsledok
sa uloží do rebríčka. Otázky aj výsledky sa ukladajú do CSV súborov, takže stav
aplikácie zostane zachovaný aj po jej vypnutí.

## Funkcie

- **Hra** – náhodný výber otázok podľa kategórie a obťažnosti, bodovanie podľa
  obťažnosti (ľahká 1 b., stredná 2 b., ťažká 3 b.), validácia formátu odpovede,
  zhrnutie chýb na konci hry.
- **Tri typy otázok** – výber z možností, pravda/nepravda, otvorená odpoveď
  (porovnanie ignoruje veľkosť písmen, diakritiku a medzery).
- **Rebríček** – TOP 10 celkovo alebo podľa kategórie, história hráča, najlepší výsledok.
- **Štatistiky** – počet otázok v kategóriách, priemerná úspešnosť, priemer hráčov.
- **Správa otázok** – výpis, filtrovanie, hľadanie, detail, pridanie, úprava, mazanie.

## Spustenie

Požiadavky: JDK 17+ a Maven.

```bash
mvn package                                  # kompilácia + testy + JAR
java -jar target/quizly-1.0-SNAPSHOT.jar     # spustenie (dáta v priečinku ./data)
java -jar target/quizly-1.0-SNAPSHOT.jar moje_data   # iný priečinok s dátami
mvn test                                     # iba unit testy
```

Pri prvom spustení sa do `data/questions.csv` skopírujú predvolené otázky
zo `src/main/resources/default_questions.csv`. Výsledky hier sa ukladajú do `data/results.csv`.

## Štruktúra projektu

```
src/main/java/com/quizly/
├── Main.java                     vstupný bod, načítanie dát a spustenie menu
├── model/                        doménový model
│   ├── Question.java             abstraktná otázka (spoločné údaje + validácia)
│   ├── MultipleChoiceQuestion    výber z možností
│   ├── TrueFalseQuestion         pravda / nepravda
│   ├── OpenQuestion              otvorená odpoveď
│   ├── GameResult.java           výsledok jednej hry
│   ├── Difficulty.java           obťažnosť a body
│   └── QuestionType.java         typy otázok a ich kódy v CSV
├── service/                      byznys logika
│   ├── QuestionBank.java         správa, filtrovanie a náhodný výber otázok
│   ├── QuizSession.java          priebeh jednej hry a počítanie skóre
│   └── Leaderboard.java          rebríček, história a štatistiky
├── persistence/                  práca so súbormi
│   ├── Repository.java           rozhranie úložiska
│   ├── AbstractCsvRepository     spoločné čítanie/zápis CSV
│   ├── QuestionCsvRepository     CSV otázok
│   ├── ResultCsvRepository       CSV výsledkov
│   ├── CsvUtils.java             parsovanie a escapovanie CSV
│   └── DataAccessException.java  chyba pri práci s dátami
└── ui/
    ├── ConsoleApp.java           textové menu a obsluha akcií
    └── ConsoleInput.java         načítanie a validácia vstupu
```

## Splnenie požiadaviek

| Požiadavka | Kde |
|---|---|
| OOP, zapuzdrenie | `private` atribúty, gettery/settery s validáciou (`Question`, `GameResult`) |
| Dedičnosť | `Question` → `MultipleChoiceQuestion`, `TrueFalseQuestion`, `OpenQuestion`; `AbstractCsvRepository` → repozitáre |
| Rozhranie | `Repository<T>` – úložisko nezávislé od formátu |
| Kolekcie | `ArrayList`, `HashMap`, `TreeSet`, `TreeMap` (`QuestionBank`, `Leaderboard`) |
| Stream API, lambdy | filtrovanie, triedenie, `groupingBy`, priemery (`QuestionBank`, `Leaderboard`, `QuizSession`) |
| Súborové I/O | CSV súbory, bezpečný zápis cez dočasný súbor (`AbstractCsvRepository`) |
| Výnimky | `DataAccessException`, `try-catch` pri súboroch aj vstupe od používateľa |
| Build systém | Maven (`pom.xml`) |
| Unit testy | JUnit 5 v `src/test/java` |

## Formát CSV s otázkami

```
typ;id;kategoria;obtiaznost;text;spravna_odpoved;moznosti
MC;1;Geografia;EASY;Aké je hlavné mesto Slovenska?;2;Košice|Bratislava|Žilina|Nitra
TF;3;Geografia;EASY;Česko hraničí s Rakúskom.;true;
OPEN;4;Geografia;MEDIUM;Ako sa volá najvyšší vrch Slovenska?;Gerlachovský štít|Gerlach;
```
