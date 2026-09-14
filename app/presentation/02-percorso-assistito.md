# Deliverable 2 — Percorso Assistito

Hackathon Accenture · 14 settembre 2026 · Tema 01 — Accessibilità Digitale

---

## Il percorso prima: cosa non riesce a fare

Anna apre il sito del Comune. Trova il link al modulo PDF di cambio di residenza. Lo scarica o lo apre nel browser.

Davanti ha un documento con campi da compilare. I campi hanno etichette burocratiche. Non capisce cosa vogliono da lei in almeno uno o due campi già nella prima sezione. Non sa se può lasciare un campo vuoto senza che la pratica venga respinta. Non sa in che formato scrivere alcune informazioni (data, codice fiscale, nome del Comune per esteso o con sigla).

Si ferma. Chiude il browser, o chiama qualcuno, o prende appuntamento al CAF.

**Il risultato: la pratica non parte. Il modulo resta bianco.**

---

## Il percorso dopo: passo per passo

### Passo 1 — Aprire il sistema

Anna apre il browser e va all'indirizzo del sistema. Vede una schermata semplice: uno sfondo chiaro, un titolo leggibile, un solo pulsante.

Non è richiesta registrazione. Non è richiesta nessuna configurazione. Non deve creare un account.

### Passo 2 — Caricare il modulo

Anna preme il pulsante "Carica il tuo modulo". Si apre la finestra di selezione file del suo sistema operativo — la stessa che usa per allegare foto nelle email. Seleziona il PDF del modulo di cambio di residenza che aveva scaricato dal sito del Comune.

### Passo 3 — Il sistema legge il modulo

Il sistema analizza il PDF. Individua tutti i campi AcroForm presenti nel documento (il formato standard dei moduli PDF compilabili). Costruisce la lista dei campi nell'ordine in cui compaiono nel documento.

Questa operazione avviene in pochi secondi, localmente, senza inviare il file a nessun servizio esterno.

### Passo 4 — Una domanda alla volta

Per il primo campo, il sistema mostra ad Anna una schermata semplice:

- In alto, in grigio, il nome tecnico del campo così come appare nel documento originale — sempre visibile, non nascosto.
- Al centro, in caratteri grandi, una domanda in italiano semplice generata dal modello linguistico locale (Ollama, qwen2.5:7b) a partire dall'etichetta del campo.
- Sotto, un campo di testo dove scrivere la risposta.
- Un pulsante "Avanti".

Esempio:
> Campo originale: **Luogo di iscrizione anagrafica**
> Domanda mostrata ad Anna: **In quale città sei iscritta all'anagrafe?**

Anna capisce la domanda. Sa rispondere. Scrive la risposta. Preme "Avanti".

### Passo 5 — Campo successivo

Il sistema mostra il campo successivo, con la stessa struttura. Una domanda per schermo: nessuna lista, nessun modulo lungo da scorrere, nessuna distrazione visiva.

Anna risponde campo per campo, al suo ritmo. Se sbaglia a digitare può tornare indietro.

### Passo 6 — Scaricare il PDF compilato

Quando tutti i campi sono stati risposti, il sistema recompone il PDF originale inserendo le risposte di Anna nei campi corrispondenti. Il documento risultante è lo stesso modulo del Comune, compilato, nel formato che l'ente si aspetta.

Anna preme "Scarica il modulo compilato". Il file viene salvato sul suo computer.

### Passo 7 — Invio (fuori dal sistema)

Anna invia il PDF al Comune tramite il canale previsto dalla procedura (email certificata, sportello online, upload sul portale). Questo passaggio è fuori dal perimetro del sistema — il sistema si occupa solo della compilazione.

---

## Il confronto in sintesi

| | Prima | Dopo |
|---|---|---|
| Primo contatto col modulo | Si blocca al primo campo burocratico | Vede una domanda in italiano semplice |
| Orientamento | Non sa cosa scrivere né in che ordine | Una domanda alla volta, al suo ritmo |
| Dubbio sul significato | Rimane irrisolto, blocca il progresso | Il campo originale è sempre visibile sopra |
| Completamento | Il modulo resta bianco | Il PDF è compilato e pronto |
| Autonomia | Dipende da un figlio, un CAF, un patronato | Completa la pratica da sola |

---

## Note tecniche sul flusso (per i valutatori)

Il sistema è costruito su tre componenti:

- **Frontend** (Angular 21): interfaccia accessibile, un campo per schermata, caratteri grandi, contrasto elevato.
- **Backend** (Spring Boot): riceve il PDF, estrae i campi AcroForm con PDFBox, ricompone il documento compilato.
- **Modello linguistico** (Ollama locale, qwen2.5:7b): genera la domanda in italiano semplice a partire dall'etichetta del campo. Gira localmente — nessun dato di Anna lascia il dispositivo.
