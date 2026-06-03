# ⚔️ Dungeon Explorer RPG

Un gioco di ruolo (RPG) a turni in cui esplori un dungeon generato proceduralmente,
affronti nemici di difficoltà crescente, potenzi il tuo eroe e cerchi di raggiungere
la porta del tesoro sconfiggendo i 3 draghi che la custodiscono.

Sviluppato in Java con interfaccia grafica JavaFX e sistema di build Gradle.

Autore: Filippo Mangialardo
Matricola: 129876
Progetto sviluppato per l'esame di Metodologie di Programmazione
A.A. 2025/26 - Università di Camerino
---

## 🎮 Caratteristiche principali

- ✅ Mappa del dungeon 8x8 con esplorazione
- ✅ Sistema di combattimento a turni
- ✅ 3 classi giocatore (Guerriero, Mago, Ladro)
- ✅ 6 tipi di nemici + drago boss finale
- ✅ Sistema di livelli ed esperienza
- ✅ Mercanti per acquistare pozioni
- ✅ Tesori (oro e pozioni) nelle stanze
- ✅ Salvataggio punteggi in classifica (Top 10)
- ✅ Controlli da tastiera (WASD/frecce, Z=attacca, X=pozione, C=fuggi)

🎮 Come giocare

## Creazione del personaggio
    -Inserisci il nome del tuo eroe
    -Scegli una classe

## Obiettivo del gioco
    -Sconfiggi i 3 draghi che circondano la porta
    -Entra nella porta del tesoro per vincere

---

## 🚀 Come eseguire il progetto

### Prerequisiti
- **Java 21** o superiore 
- - Gradle 

### Clona il repository

```bash
git clone https://github.com/filippomangialardo5/rpg-MPGC_129876.git
cd rpg-MPGC_129876
```

### Build del progetto
```bash
./gradlew build
```

### Esecuzione
```bash
./gradlew run
```
---

## 🤖 Uso di strumenti di AI

ChatGPT (OpenAI) utilizzato per:

    -Comprendere concetti teorici (pattern MVC, binding JavaFX, gestione eventi)
    -Chiarire errori di compilazione (classi mancanti, import)
    -Suggerimenti su struttura del codice e organizzazione delle classi
    -Generazione di bozze per i metodi complessi (combattimento, gestione mappa, salvataggio punteggi)
    -Debug e risoluzione di problemi (key bindings, refresh UI, gestione stato)

GitHub Copilot utilizzato per:

    -Autocompletamento di metodi ripetitivi (getter/setter, metodi factory)
    -Generazione rapida di codice

Approccio personale:
Ogni suggerimento AI è stato:

    -Compreso e adattato alle esigenze specifiche del progetto
    -Testato manualmente
    -Modificato per correggere comportamenti indesiderati
    -Il codice finale è il risultato di un continuo feedback loop tra scrittura manuale, test e ottimizzazione con supporto AI.