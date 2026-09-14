# Hagenthon — Assistente alla compilazione di documenti burocratici

Compilazione guidata di moduli PDF burocratici, una domanda alla volta.
Tema 01 — **Accessibilità Digitale**

## Il problema

Una persona riceve un modulo della pubblica amministrazione e non riesce a
compilarlo: i nomi dei campi sono in nomenclatura burocratica e non dicono
quale informazione stiano chiedendo. Oggi si ferma lì, o deve chiedere aiuto.

## La soluzione

Carichi il PDF. Il sistema lo legge, ti fa **una domanda alla volta in
linguaggio semplice**, tu rispondi, e ti restituisce il **PDF compilato**.

Non spiega il documento: lo porta a termine.

## Stack

| Livello | Tecnologia |
| --- | --- |
| Frontend | Angular 21 (standalone components, signals) |
| Backend | Spring Boot 3.5, Java 21, Maven |
| PDF | Apache PDFBox 3.x (AcroForm read/write) |
| LLM | Ollama locale (`qwen2.5:7b`) — opzionale, con fallback |

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

### Verifiche

```bash
cd app/backend  && mvn verify                          # test + Spotless + Error Prone
cd app/frontend && npx ng build --configuration production   # build AOT
cd app/frontend && npx ng lint                         # ESLint + regole di accessibilità
```

`mvn verify` è verde. `npx ng test` è **rosso** per uno spec scaffold mai adattato,
guasto noto e tracciato: [issue #7](../../issues/7).

## Struttura del repository

| Cartella | Contenuto |
| --- | --- |
| `app/backend/` | Backend Spring Boot (controller / service / config) |
| `app/frontend/` | Frontend Angular 21 (UI accessibile) |
| `app/samples/` | PDF AcroForm di esempio, usati come fixture di test e per la demo |
| `app/presentation/` | Materiale di presentazione del progetto |
| `agents/` | Risorse agentiche: subagent, skill, regole, comandi, hook |
| `docs/` | Documentazione di progetto: caso d'uso, requisiti, decisioni |
| `.wayfinder/` | Mappa delle decisioni, dall'idea alla realizzazione |

## Documentazione

- [`CLAUDE.md`](CLAUDE.md) — istruzioni per chi sviluppa, persona o agente:
  regole, architettura, quale regola leggere per quale area
- [`docs/caso-uso.md`](docs/caso-uso.md) — obiettivo, persona, percorso, limiti
- [`docs/requisiti-consolidati.md`](docs/requisiti-consolidati.md) — requisiti e vincoli
- [`docs/aggiornamenti.md`](docs/aggiornamenti.md) — cosa è cambiato dopo la chiusura dei requisiti
- [`agents/README.md`](agents/README.md) — architettura agentica, scelte di modello,
  regole, hook, comandi
- [`app/samples/README.md`](app/samples/README.md) — descrizione dei PDF di esempio
