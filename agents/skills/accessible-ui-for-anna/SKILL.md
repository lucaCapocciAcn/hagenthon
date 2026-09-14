---
name: accessible-ui-for-anna
description: Pattern di accessibilità per la UI Angular di "Un campo alla volta", tarati su Anna (71 anni, bassa alfabetizzazione digitale, lieve calo visivo). Usala quando costruisci qualsiasi schermata del frontend.
---

# UI accessibile per Anna

Principio guida: **una cosa sola per schermata**, testo grande, nessuna ambiguità.
Anna non deve mai chiedersi "cosa devo fare adesso".

## Tipografia e colore
- Domanda principale ≥ 28px; testo secondario ("Testo del modulo") ≥ 18px; niente < 16px.
- Contrasto AAA sul testo principale (≥ 7:1). Testo scuro su fondo chiaro.
- Un solo font di sistema sans-serif, line-height ≥ 1.5.

## Layout
- Un campo alla volta, centrato, a schermo intero. Niente menu, niente sidebar.
- Indicatore di avanzamento in alto ("Campo 3 di 12") — dà sicurezza.
- Riquadro fisso **"Testo del modulo"** con l'etichetta originale: non tradire mai
  il significato originale (vincolo della traccia). Sempre visibile, secondario.
- Un solo pulsante d'azione grande ("Avanti →", ≥ 56px altezza, colore contrastato).

## Interazione
- Focus automatico sul campo di risposta all'apertura di ogni domanda.
- `Invio` = Avanti. Pulsante sempre raggiungibile senza scroll.
- Nessun timeout, nessun popup, nessuna finestra di dialogo bloccante.
- Messaggi in italiano semplice; mai codici d'errore tecnici verso l'utente.

## A11y tecnica
- HTML semantico: `<label>` legato a ogni input, `<button>` veri.
- `aria-live="polite"` sull'area della domanda per gli screen reader.
- `lang="it"` sull'`<html>`. Navigabile da tastiera, focus visibile.

## Cosa NON fare
- Niente selezione lingua all'avvio (hardcode italiano).
- Niente più campi nella stessa schermata, niente animazioni distraenti.
