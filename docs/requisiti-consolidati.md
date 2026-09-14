# Hackathon interno Accenture — 14 settembre 2026 — stato consolidato

Consolidato alle 09:52 del 2026-09-14.
Trascrizione di origine: `transcript/2026-09-14/09-01-06.jsonl` (righe 1-861).
Fase della sessione: `attesa` per tutta la durata — quindi qui c'è **solo
l'ingaggio** (consegne, scadenze, perimetro, regole). Nessun requisito di
prodotto è stato raccolto dal parlato, perché il tema non è ancora stato
scelto — ma il perimetro qui sotto viene ora dal **documento ufficiale delle
tracce**, non più dal solo riassunto a voce.

---

## Consegne e scadenze

| Id | Cosa | Quando / dettaglio | Righe |
| --- | --- | --- | --- |
| DEL-003 | Pausa pranzo | 13:00, un'ora, liberi di uscire o continuare | 197-206 |
| DEL-004 | Fine evento | verso le 16:00; poi si può restare a lavorare | 207-216 |
| DEL-006 | Tempo di lavoro | 5 ore pulite, pranzo escluso | 528-547 |
| DEL-021 | Debriefing intermedi | nel corso della giornata, orari non dichiarati | 177-181 |

**Cosa va consegnato** — tre parti, push su repository GitHub (DEL-020, righe
596-603 e 737-755):

1. **l'applicazione** — cartella suggerita `app/`;
2. **le risorse agentiche** — cartella suggerita `agents/`;
3. **la presentazione** — PPT su template brandizzato Accenture (DEL-012, riga 603).

La struttura di cartelle è **consigliata, non bloccante**: l'agente valutatore
«sa dove andare a leggere», ma rispettarla è meglio.

Serve inoltre un **README con le istruzioni per eseguire il software** (DEL-017,
righe 763-776): i valutatori scaricano ed eseguono se lo ritengono necessario.

Serve un **account GitHub**, già richiesto nella mail di presentazione della
manifestazione (DEL-020, righe 583-596).

---

## Perimetro — cosa tocca a noi

Fonte autorevole: **`fonti/hagenthon-temi-sfida-2.html`** (documento ufficiale
"Hagenthon · Temi della sfida", Accenture Application Engineering, 14 settembre
2026), copiato nella knowledge perché l'originale sta in una cartella temporanea
di Outlook. Dove il documento e il parlato divergono, **prevale il documento**.

**Struttura comune a tutti i temi** (DEL-022): scegliere un tema → **definire un
problema specifico** → costruire un **prototipo** → preparare la **demo finale**.
Team da 2 persone, 5 ore di sviluppo.

### Tema 01 — Accessibilità Digitale (DEL-023)

Uno strumento che affianchi **una persona con una difficoltà precisa** mentre usa
un servizio digitale reale, e la porti fino in fondo al suo obiettivo.

- **Vincoli**: persona concreta e barriera precisa (chi è, cosa sta facendo, dove
  si blocca); servizio o contenuto reale o realistico; usabile **dalla persona
  stessa**, non da uno sviluppatore; semplificare senza tradire il significato;
  in demo il percorso **prima/dopo**; dichiarare dove ha contribuito l'AI e dove
  è servita revisione umana.
- **Deliverable**: 1) Persona & Barriera · 2) Percorso Assistito · 3) Autonomia & Limiti.
- **Da evitare**: strumenti per sviluppatori, checker di conformità, soluzioni che
  si fermano alla diagnosi, restyling grafici, profili utente generici («un utente
  disabile» non è un profilo), uso dell'AI non spiegabile dal team.

### Tema 02 — Inclusione Finanziaria (DEL-024)

Supportare l'**educazione alla finanza personale di base** per chi ha bassa
alfabetizzazione finanziaria. Non un consulente finanziario AI.

- **Vincoli**: scenario educativo preciso; miglioramento **tangibile** di
  comprensione o capacità; **vietato** dare raccomandazioni di investimento,
  consulenza personalizzata o indicazioni su cosa comprare/vendere/scegliere;
  serve una **capability software concreta**, non solo riscrittura di testi.
- **Deliverable**: 1) User Difficulty Statement · 2) Before/After Simplicity
  Evidence · 3) Risk & Clarity Note.
- **Da evitare**: chatbot generici, pura riscrittura, consigli finanziari,
  semplificazioni che cambiano il significato, demo scollegate da un processo reale.

### Tema 03 — Educazione Digitale Inclusiva (DEL-025)

Rendere più accessibile, comprensibile o personalizzato un **percorso di
apprendimento digitale** per utenti in difficoltà (bassa alfabetizzazione
digitale, difficoltà cognitive o linguistiche, DSA, anziani, lavoratori in
riqualificazione).

- **Vincoli**: profilo utente preciso; scenario di apprendimento concreto;
  miglioramento **misurabile** in almeno uno fra comprensione, autonomia,
  completamento del task, riduzione errori, capacità di ripetere un'azione;
  **vietato** trattare temi sensibili (sanità clinica, fiscalità personalizzata,
  ambito legale) come consigli professionali; serve **almeno una capability
  agentica concreta nel prodotto** — adattamento dinamico, valutazione della
  comprensione, percorso personalizzato, rilevamento del blocco, feedback mirato.
- **Deliverable**: 1) Learner Profile Statement · 2) Adaptive Evidence ·
  3) Learning Outcome Note.
- **Da evitare**: generatori generici di lezioni, tutor conversazionali aperti
  senza percorso strutturato, pura traduzione automatica, soluzioni non legate a
  un utente fragile specifico, contenuti sensibili trattati come consulenza.

### Una contraddizione, risolta (DEL-026)

A voce (riga 624) è stato detto «rendere il software accessibile per rispettare
la **legge Stanca**». Il documento scritto dice l'opposto come inquadramento del
tema 01: *«Il focus non è l'audit tecnico del codice né la conformità formale
alle linee guida: è la persona»*, e mette i checker di conformità fra le cose da
evitare. **Prevale il documento.** Se scegliete il tema 01, vale comunque la pena
farselo confermare a voce.

---

## Regole dell'evento

- **Si lavora in coppia** (DEL-005, DEL-019, righe 516-533 e 816-856): 19 coppie
  estratte a sorte, con spostamento di postazione.
- **Rendicontazione** (DEL-002, righe 197-201, `uncertain`): caricare 4 ore di
  training + 2 ore sui progetti. Detto una volta sola e con «probabilmente»; la
  natura formale di training è però confermata alla riga 511.
- **Premi** (DEL-016, riga 719, `uncertain`): punti performance — 500 € alla
  prima coppia, 300 € alla seconda, la terza non è determinabile (la
  trascrizione dice «500», incoerente con la scala decrescente).
- **Format della giornata** (DEL-001, righe 189-196): 10 minuti di training
  introduttivo su Claude Code (Fabio), poi lavoro.

---

## Come si viene valutati

La valutazione ha **tre componenti** (DEL-009, righe 553-576), la cui somma
determina le coppie vincitrici:

1. un **agente automatico** costruito dagli organizzatori, che valuta su
   parametri prestabiliti;
2. la **valutazione soggettiva** dei cinque organizzatori;
3. la **presentazione** esposta dal vivo.

**Gli otto criteri dichiarati a schermo** (DEL-015, righe 689-718):

- profondità agentica
- qualità delle istruzioni
- robustezza delle istruzioni
- **efficienza dei token** — sottolineata esplicitamente come «cosa da non sottovalutare»
- qualità tecnica
- adeguatezza degli strumenti
- documentazione
- qualità dell'idea

**Due precisazioni che pesano** (DEL-008, DEL-013, DEL-018):

- si valuta **anche la qualità delle risorse agentiche**, non solo il software
  prodotto (riga 551);
- si valuta **il cosa, non solo il come**: la scelta stessa di cosa costruire
  deve reggere un ragionamento, e contano originalità e utilità reale della
  soluzione (righe 642-659);
- la **capacità espositiva pesa quanto quella tecnica** (righe 779-792): va
  rispettato il tempo prestabilito e raccontato il lavoro in maniera semplice.

---

## Chi valuta

Cinque organizzatori (DEL-010, righe 561-565) — nomi trascritti foneticamente,
grafia da confermare:

Francesco Le Filippi · Fabio Di Beri · Francesco Malagisi · Gianluca Luongo · Giuseppe Sarno

---

## Domande aperte

Nessuna di queste è stata scritta in `open-questions.json`: la fase è `attesa`,
dove l'unico file scrivibile è `delivery.json`. Vivono qui.

| # | Domanda | Perché è aperta |
| --- | --- | --- |
| 1 | **Con chi sei in coppia?** | L'estrazione nomina sia «Luca e Cristian» sia «Adriana e Luca», e all'appello ci sono più Luca (righe 846-850). Dimmelo e lo registro. |
| 2 | Dentro `agents/` va messa anche la cartella `.claude`? | Domanda posta da un partecipante (riga 759); la risposta non è intelligibile nella trascrizione (righe 760-762). |
| 3 | Quanto dura la presentazione? | Si parla di «tempo prestabilito» (riga 788) ma il tempo non è mai stato dichiarato. |
| 4 | A che ora si presenta, e presentano tutti o solo i selezionati? | La riga 603 dice «nel momento in cui sarete selezionati tra i primi»; la riga 565 sembra invece riferirsi a tutti. |
| 5 | Cosa succede «dalle 18»? | Righe 211-213: «e dalle 18 faremo la…» — la parola finale è irrecuperabile dalla trascrizione. |
| 6 | Quali sono i parametri esatti dell'agente valutatore? | Annunciati (riga 559) ma mai elencati oltre agli otto criteri a schermo. |
| 7 | Premio della terza coppia | Vedi DEL-016. |

---

## Nota sulla qualità della fonte

La trascrizione di questa sessione è **molto degradata** (microfono su Aggregate
Device in stanza affollata, frasi spezzate e ripetute, nomi propri storpiati).
Ogni voce qui sopra porta le righe esatte da cui viene: in caso di dubbio, la
verifica va fatta sul `.jsonl`, non su questo documento.
