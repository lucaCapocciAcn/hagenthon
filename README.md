# Hagenthon — Assistente alla compilazione di documenti burocratici

Hackathon Agentic Coding · Accenture Application Engineering · 14 settembre 2026
Tema 01 — **Accessibilità Digitale**

## Il problema

Una persona riceve un modulo della pubblica amministrazione e non riesce a
compilarlo: i nomi dei campi sono in nomenclatura burocratica e non dicono
quale informazione stiano chiedendo. Oggi si ferma lì, o deve chiedere aiuto.

## La soluzione

Carichi il PDF. Il sistema lo legge, ti fa **una domanda alla volta in
linguaggio semplice**, tu rispondi, e ti restituisce il **PDF compilato**.

Non spiega il documento: lo porta a termine.

## Come si esegue

Requisiti: **Java 21**, **Maven 3.9+**, **Node 22 / npm 10**. Ollama è consigliato
ma facoltativo (senza, l'app funziona lo stesso: mostra l'etichetta originale del
campo al posto della domanda semplificata — nessun crash).

### 1. Ollama (consigliato, per le domande in linguaggio semplice)

```bash
# macOS: brew install ollama   —   altrimenti https://ollama.com/download
ollama pull qwen2.5:7b
ollama serve   # se non è già attivo come servizio
```

Il modello e l'indirizzo sono configurabili in
`app/backend/src/main/resources/application.properties`
(`ollama.model`, `ollama.base-url`).

### 2. Backend (Spring Boot, porta 8080)

```bash
cd app/backend
mvn spring-boot:run
```

### 3. Frontend (Angular, porta 4200)

```bash
cd app/frontend
npm install
npm start
```

### 4. Uso

Apri **http://localhost:4200**, carica un PDF di prova da
[`app/samples/`](app/samples/), rispondi a una domanda alla volta e scarica il
PDF compilato.

### Test

```bash
cd app/backend && mvn test     # logica PDFBox (estrazione + compilazione campi)
```

## Struttura del repository

| Cartella | Contenuto |
| --- | --- |
| `app/backend/` | backend Spring Boot (controller / service / config) |
| `app/frontend/` | frontend Angular 21 (UI accessibile) |
| `app/samples/` | PDF AcroForm di test per la demo |
| `agents/` | le risorse agentiche usate per costruirlo (agenti, skill, tabella economia modelli) |
| `docs/` | requisiti, caso d'uso, aggiornamenti |
| `.wayfinder/` | la mappa delle decisioni (dall'idea alla realizzazione) |

## Documentazione

- [`docs/caso-uso.md`](docs/caso-uso.md) — obiettivo, persona, percorso, limiti
- [`docs/requisiti-consolidati.md`](docs/requisiti-consolidati.md) — regole dell'evento, criteri di valutazione, vincoli della traccia
- [`docs/aggiornamenti.md`](docs/aggiornamenti.md) — cosa è cambiato dopo la chiusura dei requisiti

## Team

Hackathon a coppie — 5 ore di sviluppo.
