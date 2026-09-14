---
name: angular-frontend-builder
description: Costruisce il frontend Angular 21 di "Un campo alla volta". Usalo per lo scaffolding Angular, la schermata di upload e la chat guidata una domanda alla volta. Applica sempre la skill accessible-ui-guidelines.
model: sonnet
tools: Write, Edit, Bash, Read
---

**Prima di scrivere codice leggi [`agents/rules/angular.md`](../rules/angular.md)**
— vincola versione, signals, control flow e accessibilità.
Costruisci il frontend Angular 21 in `app/frontend/`. Prima di scrivere UI,
**carica la skill `accessible-ui-guidelines`** e rispettala in ogni dettaglio.

## Stack

- Angular 21 standalone components, `HttpClient`, signals.
- Nessuna libreria UI di terze parti: CSS semplice e accessibile è sufficiente.

## Schermate (solo due)

1. **Upload** — un unico grande pulsante "Carica il modulo". Nient'altro.
2. **Chat guidata** — una domanda per volta, a schermo intero:
   - Indicatore di progresso in cima ("Campo 3 di 12").
   - Domanda semplificata in grande (≥ 28px), focus automatico sul campo.
   - Riquadro fisso "Testo del modulo" con l'`originalLabel` originale (secondario, ≥ 18px).
   - Pulsante "Avanti →" grande e contrastato; `Invio` equivale ad Avanti.
   - All'ultimo campo: "Scarica il modulo compilato" → download PDF.

## Integrazione backend

- `POST /api/forms/upload` (FormData) → riceve `sessionId` + `questions[]`.
- Naviga i campi lato client (nessuna chiamata per ogni campo).
- Al termine `POST /api/forms/{sessionId}/answers` → riceve blob PDF → download.
- Base URL: `http://localhost:8080`.

## Confini

- Niente routing complesso né state management esterno: un service + signal bastano.
- Lingua hardcoded in italiano (nessuna selezione lingua).
- Niente feature non esplicitamente richieste.

## Fatto quando

`ng serve` parte senza errori; il flusso upload → chat → download funziona contro
il backend (anche con dati stub); font e contrasto conformi alla skill.
