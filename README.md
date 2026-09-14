# Un campo alla volta

Assistente intelligente per la compilazione guidata di moduli PDF burocratici.

## Il problema

Chi riceve un modulo della pubblica amministrazione spesso non riesce a compilarlo autonomamente: i nomi dei campi sono in nomenclatura tecnica e non indicano quale informazione stiano richiedendo. La persona si blocca, o deve chiedere aiuto a qualcun altro.

## La soluzione

Carichi il PDF. Il sistema lo legge, pone **una domanda alla volta in linguaggio semplice**, e restituisce il **PDF compilato** con le risposte al posto giusto.

Non spiega il documento: lo porta a termine.

## Stack

| Livello | Tecnologia |
| --- | --- |
| Frontend | Angular 21 (standalone components, signals) |
| Backend | Spring Boot 3.x, Java 21, Maven |
| PDF | Apache PDFBox 3.x (AcroForm read/write) |
| LLM | Ollama locale (`qwen2.5:7b`) — opzionale, con fallback |

## Come si esegue

**Prerequisiti:** Java 21, Maven 3.9+, Node 22 / npm 10.
Ollama è consigliato ma facoltativo: senza di esso, l'app mostra l'etichetta originale del campo invece della domanda semplificata — nessun crash.

### 1. Ollama (consigliato)

```bash
# macOS
brew install ollama
# oppure https://ollama.com/download

ollama pull qwen2.5:7b
ollama serve   # se non è già attivo come servizio
```

Modello e indirizzo configurabili in
`app/backend/src/main/resources/application.properties`
(`ollama.model`, `ollama.base-url`).

### 2. Backend — porta 8080

```bash
cd app/backend
mvn spring-boot:run
```

### 3. Frontend — porta 4200

```bash
cd app/frontend
npm install
npm start
```

### 4. Uso

Apri **http://localhost:4200**, carica un PDF AcroForm da
[`app/samples/`](app/samples/), rispondi a una domanda alla volta e
scarica il PDF compilato.

### Test

```bash
cd app/backend && mvn test   # logica PDFBox: estrazione + compilazione campi
```

## Struttura del repository

| Cartella | Contenuto |
| --- | --- |
| `app/backend/` | Backend Spring Boot (controller / service / config) |
| `app/frontend/` | Frontend Angular 21 (UI accessibile) |
| `app/samples/` | PDF AcroForm di esempio |
| `agents/` | Struttura agentica usata per costruire il progetto |

## Documentazione

- [`agents/README.md`](agents/README.md) — architettura agentica, scelte di modello, log AI/umano
- [`app/samples/README.md`](app/samples/README.md) — descrizione dei PDF di esempio
