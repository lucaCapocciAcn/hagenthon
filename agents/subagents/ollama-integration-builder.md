---
name: ollama-integration-builder
description: Implementa l'integrazione con Ollama nel backend Spring Boot — il client HTTP verso il modello locale qwen2.5:7b e la generazione delle domande semplici a partire dalle etichette burocratiche dei campi. Applica la skill ollama-italian-prompting.
model: sonnet
tools: Write, Edit, Bash, Read
---

Implementa `QuestionGenerator` nel backend Spring Boot.
**Carica la skill `ollama-italian-prompting`** per il template di prompt validato.

## Responsabilità

Dato `originalLabel` (es. "estremi del titolo di occupazione dell'alloggio"),
chiama Ollama e ritorna una `simpleQuestion` in italiano semplice (≤ 15 parole).

## Client HTTP

- Endpoint: `POST http://localhost:11434/api/chat`
- Client: `RestClient` nativo Spring (nessuna libreria esterna).
- Payload:
  ```json
  {
    "model": "qwen2.5:7b",
    "stream": false,
    "options": { "temperature": 0.3, "num_predict": 60 },
    "messages": [{ "role": "system", "content": "..." }, { "role": "user", "content": "..." }]
  }
  ```
- URL e modello configurabili in `application.properties`
  (`ollama.base-url`, `ollama.model`).

## Robustezza

- Timeout esplicito (30s) con messaggio chiaro in caso di mancata risposta.
- Validazione output: se `content` ha più di 18 parole o è vuoto → re-prompt una volta.
- Se fallisce ancora → **fallback**: restituisce `originalLabel` as-is. Il flusso
  non si blocca mai.
- Genera tutte le domande **upfront** al momento dell'upload (un batch, non on-demand).
- `trim()` sull'output; rimuovi eventuali virgolette residue.

## Confini

- Non gestisce PDF né controller: solo il generatore di domande e la sua configurazione.
- Niente streaming, niente selezione lingua.

## Fatto quando

Con Ollama attivo e `qwen2.5:7b` scaricato, 3-4 etichette burocratiche vengono
trasformate in domande semplici corrette in italiano. Senza Ollama, il fallback
restituisce le etichette originali senza errori.
