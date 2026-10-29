# Quizly

Jednoduchá konzolová kvízová aplikácia v Jave (semestrálna práca).
Hráč odpovedá na 5 náhodných otázok z vybranej kategórie a jeho výsledok sa uloží do rebríčka.

## Spustenie

```bash
mvn test        # spustí testy
mvn package     # skompiluje projekt
```

Alebo v IntelliJ spusti `Main.java` (pracovný priečinok = koreň projektu).

## Triedy

| Trieda | Čo robí |
|---|---|
| `Question` | abstraktná otázka (text, kategória) |
| `ChoiceQuestion` | otázka s možnosťami – dedí z `Question` |
| `TrueFalseQuestion` | otázka áno/nie – dedí z `Question` |
| `Result` | výsledok hry (meno, skóre, počet otázok) |
| `Quiz` | zoznam otázok, kategórie, náhodný výber (Stream API) |
| `Leaderboard` | rebríček, top hráči, výsledky hráča (Stream API) |
| `FileManager` | načítanie otázok, načítanie a uloženie výsledkov (try-catch) |
| `Main` | menu a hra |

## Súbory s dátami

- `questions.csv` – otázky, napr. `CHOICE;Geografia;Hlavné mesto SR?;Košice,Bratislava;2` alebo `TF;Veda;Slnko je hviezda.;true`
- `results.csv` – výsledky hier (vytvorí sa automaticky), napr. `Jozef;4;5`
