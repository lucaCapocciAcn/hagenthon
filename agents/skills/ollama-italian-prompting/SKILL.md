---
name: ollama-italian-prompting
description: Template di prompt validato e pattern client per chiamare Ollama (qwen2.5:7b) dal backend Java e trasformare un'etichetta di campo burocratico in una domanda semplice in italiano. Usala per l'integrazione LLM in "Un campo alla volta".
---

# Ollama · italiano semplice · da etichetta a domanda

Modello consigliato: `qwen2.5:7b`. Endpoint: `POST http://localhost:11434/api/chat`.

## Prompt (validato)

**System** (breve → efficienza token):
```
Sei un assistente che semplifica il linguaggio burocratico italiano. Rispondi sempre e solo in italiano.
```

**User**:
```
Il modulo contiene il campo: "<originalLabel>".
Scrivi UNA sola domanda, in italiano semplice, per una persona di 70 anni che non usa spesso il computer.
Massimo 15 parole. Niente spiegazioni, solo la domanda.
```

## Payload

```json
{
  "model": "qwen2.5:7b",
  "stream": false,
  "options": { "temperature": 0.3, "num_predict": 60 },
  "messages": [
    { "role": "system", "content": "..." },
    { "role": "user",   "content": "..." }
  ]
}
```

Risposta: `message.content` → `.trim()`.

## Regole

- `temperature 0.3` e `num_predict 60`: output stabile e corto, latenza contenuta.
- La clausola "Niente spiegazioni, solo la domanda" è critica: evita testo extra
  che rompe il parsing.
- Validazione: se `content.split("\\s+").length > 18` o vuoto → re-prompt una volta;
  se fallisce ancora → **fallback** = restituisci `originalLabel` as-is (il flusso
  non si blocca mai).
- Rimuovi eventuali virgolette residue attorno alla risposta.
- Esternalizza `ollama.base-url` e `ollama.model` in `application.properties`.

## Verifica rapida da CLI

```bash
ollama run qwen2.5:7b \
  "Il campo si chiama 'estremi del titolo di occupazione dell\'alloggio'. \
   Scrivi una domanda semplice, max 15 parole. Solo la domanda."
```
