# Un campo alla volta — CLAUDE.md

## Regola sul codice

**Nessuna modifica al codice senza richiesta esplicita.** Niente implementazioni
decise in autonomia, niente refactor non chiesti, niente file creati "perché
servivano". Si propone, l'utente decide.

## Cosa fa il progetto

Un assistente che permette a persone con bassa alfabetizzazione digitale di
compilare moduli PDF burocratici attraverso una conversazione guidata: una
domanda alla volta in linguaggio semplice, poi il PDF compilato in download.

Il flusso è intenzionalmente lineare: nessuna registrazione, nessuna
configurazione, nessun account.

## Architettura

- **Backend**: Spring Boot 3.x, Java 21, Maven.
  Package `controller` / `service` / `config`.
  2 endpoint: `POST /api/forms/upload`, `POST /api/forms/{sessionId}/answers`.
  Stato sessione in `ConcurrentHashMap` (singleton, no DB).
- **Frontend**: Angular 21 standalone components, signals, `HttpClient`.
  2 schermate: upload → chat guidata (una domanda per schermata).
- **PDF**: Apache PDFBox 3.x — lettura campi AcroForm, compilazione, flatten.
- **LLM**: Ollama locale (`qwen2.5:7b`). Se non raggiungibile, fallback
  all'etichetta originale del campo — il flusso non si blocca mai.

## Fuori scope (decisioni prese)

- PDF scansionati / OCR
- Risposta vocale
- Selezione lingua (hardcode italiano)
- Autenticazione utente
- Persistenza dati (no DB, niente oltre la sessione)

## Dove leggere prima di implementare

- `agents/README.md` — struttura agentica, scelte di modello, log contributo AI
- `app/samples/README.md` — descrizione dei PDF di esempio inclusi nel repo
