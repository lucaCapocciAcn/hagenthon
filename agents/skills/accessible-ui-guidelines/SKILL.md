---
name: accessible-ui-guidelines
description: Linee guida di accessibilità per la UI Angular di "Un campo alla volta". Applica quando costruisci qualsiasi schermata del frontend — definisce tipografia, layout, interazione e requisiti tecnici a11y.
---

# UI accessibile — linee guida

Principio guida: **una sola cosa per schermata**, testo grande, nessuna ambiguità.
L'utente non deve mai chiedersi "cosa devo fare adesso".

## Tipografia e colore

- Domanda principale ≥ 28px; testo secondario ("Testo del modulo") ≥ 18px; niente < 16px.
- Contrasto AAA sul testo principale (≥ 7:1). Testo scuro su fondo chiaro.
- Un solo font di sistema sans-serif, `line-height` ≥ 1.5.

## Layout

- Un campo alla volta, centrato, a schermo intero. Niente menu, niente sidebar.
- Indicatore di avanzamento in alto ("Campo 3 di 12").
- Riquadro fisso **"Testo del modulo"** con l'etichetta originale del campo: non alterare
  mai il significato originale. Sempre visibile, visivamente secondario.
- Un solo pulsante d'azione principale ("Avanti →", altezza ≥ 56px, colore contrastato).

## Interazione

- Focus automatico sul campo di risposta all'apertura di ogni domanda.
- `Invio` = Avanti. Pulsante sempre raggiungibile senza scroll.
- Nessun timeout, nessun popup, nessuna finestra di dialogo bloccante.
- Messaggi in italiano semplice; mai codici di errore tecnici verso l'utente.

## A11y tecnica

- HTML semantico: `<label>` collegato a ogni `<input>`, `<button>` reali (non `<div>`).
- `aria-live="polite"` sull'area della domanda per gli screen reader.
- `lang="it"` sull'`<html>`. Navigabile da tastiera con focus visibile.

## Cosa NON fare

- Niente selezione lingua all'avvio (lingua hardcoded).
- Mai più campi nella stessa schermata.
- Niente animazioni distraenti.
