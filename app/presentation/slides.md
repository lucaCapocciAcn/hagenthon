# Un campo alla volta
### Presentazione hackathon — Accenture, 14 settembre 2026
> Struttura per impaginazione su template brandizzato Accenture.
> Per ogni slide: titolo, bullet essenziali, speaker notes.

---

## Slide 1 — Titolo

**Un campo alla volta**

- Hagenthon 2026 · Tema 01 — Accessibilità Digitale
- Team: Luca Capocci
- Un modulo alla volta. Una domanda alla volta. Da soli, fino in fondo.

**Speaker notes.**
Aprire con la frase chiave del progetto: "un campo alla volta". Non è un claim pubblicitario — è la logica esatta del sistema. Il modulo viene smontato campo per campo, e per ciascuno viene posta una sola domanda, in italiano semplice. Niente di più.

---

## Slide 2 — Persona & Barriera _(Deliverable 1)_

**Anna, 71 anni. Il modulo che la blocca.**

- Pensionata, bassa alfabetizzazione digitale, lieve calo visivo
- Deve presentare la dichiarazione di cambio di residenza al Comune
- Il modulo è online, ha campi in linguaggio burocratico
- Anna si ferma prima di iniziare — e chiede aiuto a qualcuno

**Speaker notes.**
Anna è reale come profilo: anziana, non fragile in senso clinico, ma digitalmente esclusa. Il blocco non è fisico né cognitivo: è linguistico e di orientamento. Il modulo non le dice cosa vuole da lei. "Luogo di iscrizione anagrafica" non significa niente per chi non ha mai sentito questo termine. Sottolineare: il problema non è l'età, è il vocabolario del modulo.

---

## Slide 3 — Il momento esatto del blocco

**"Luogo di iscrizione anagrafica" — cosa devo scrivere?**

- I campi hanno nomi tecnici, non spiegazioni
- Una persona non sa se "residenza attuale" e "domicilio" sono la stessa cosa
- Il dubbio blocca: sbagliare su un documento ufficiale ha conseguenze
- Risultato: Anna chiude il browser e chiama il figlio o va al CAF

**Speaker notes.**
Questo è il momento esatto della barriera. Non mancanza di volontà, non mancanza di tempo: mancanza di traduzione tra il linguaggio istituzionale e il linguaggio quotidiano. Ogni campo del modulo presuppone un sapere che Anna non ha. La soluzione non deve spiegare il modulo — deve portarla a riempirlo.

---

## Slide 4 — La soluzione in una frase

**Il sistema trasforma ogni campo in una domanda che Anna sa rispondere.**

- Anna carica il PDF: un solo pulsante, nessuna registrazione
- Per ogni campo del modulo appare una domanda in italiano semplice
- Un campo per schermo, caratteri grandi, nessuna distrazione
- Alla fine scarica il PDF compilato, pronto da consegnare

> Esempio: invece di "Luogo di iscrizione anagrafica" → **"In quale città sei registrata all'anagrafe?"**

**Speaker notes.**
Mostrare lo screenshot della chat guidata. La domanda è semplice perché è stata generata da un modello linguistico (Ollama, qwen2.5:7b, locale) a partire dal nome del campo AcroForm del PDF. Non è una riscrittura manuale: è generata al volo per quel campo, in quel documento. Il testo originale del campo resta visibile sopra la domanda, per non tradire il significato.

---

## Slide 5 — Percorso Assistito: prima e dopo _(Deliverable 2)_

**Prima: Anna non arriva alla fine. Dopo: il PDF è compilato.**

| Prima | Dopo |
|---|---|
| Apre il modulo, non capisce i campi | Carica il PDF con un pulsante |
| Si blocca al primo termine burocratico | Risponde a una domanda alla volta |
| Chiude il browser e chiede aiuto | Scarica il PDF compilato |
| La pratica non parte | La pratica è pronta |

> _[Demo live: caricare il PDF di test, percorrere i campi, scaricare il compilato]_

**Speaker notes.**
Questa è la slide centrale. Il prima e il dopo sono netti: non "migliora l'esperienza", ma "la pratica che non partiva ora è completata". Fare la demo live partendo dal PDF di test generato durante lo sviluppo. Mostrare il flusso completo: upload → prima domanda → risposta → campo successivo → download. Tempo stimato della demo: 2-3 minuti.

---

## Slide 6 — Autonomia & Limiti _(Deliverable 3)_

**Cosa guadagna Anna. Cosa il sistema non fa.**

**Guadagna:**
- Completa da sola una pratica che prima richiedeva un intermediario
- Il guadagno è misurabile: prima il modulo restava bianco, ora è compilato

**Non fa:**
- Non verifica che i dati inseriti siano corretti
- Non dà consulenza sulla pratica o sulle sue conseguenze
- La responsabilità di quanto dichiarato resta di Anna

**Vincolo rispettato:** il testo originale del campo è sempre visibile — semplificare non significa nascondere.

**Speaker notes.**
Dichiarare i limiti non è una debolezza: è onestà tecnica e rispetto della traccia. Il sistema è un compilatore assistito, non un patronato digitale. Anna è responsabile di ciò che scrive. Il vincolo "semplificare senza tradire" è garantito mostrando sempre il campo originale sopra la domanda semplificata: l'utente vede entrambi.

---

## Slide 7 — Come l'abbiamo costruita: struttura agentica

**Orchestrator + 6 agenti + 3 skill**

```
orchestrator (opus)
    ├── spring-backend-builder (sonnet)
    ├── angular-frontend-builder (sonnet)
    ├── pdf-form-engineer (sonnet)
    ├── ollama-integration-builder (sonnet)
    ├── test-pdf-generator (haiku)
    └── presentation-builder (sonnet)

skill: pdf-acroform-toolkit · ollama-italian-prompting · accessible-ui-for-anna
```

- Le skill incapsulano pattern riusabili: evitano di rispiegare gli stessi pattern in ogni prompt
- Il codice generato è stato verificato a ogni passo (`mvn compile`, `mvn test` 3/3 verdi)

**Speaker notes.**
L'architettura agentica non è decorativa: è il modo in cui abbiamo costruito il software in 5 ore. L'orchestrator (opus) ha tenuto il quadro e delegato. Ogni builder ha lavorato su uno strato preciso. Le skill sono procedure incapsulate: sono state scritte una volta e richiamate da più agenti, riducendo la ripetizione nei prompt. Il backend parte in 0,57 secondi, i test PDFBox sono tutti verdi.

---

## Slide 8 — Economia sui modelli

**Il modello più economico che regge il compito.**

| Risorsa | Ruolo | Modello | Perché |
|---|---|---|---|
| `orchestrator` | Decompone, delega, integra | opus | Unico punto con ragionamento su tutto il progetto |
| builder (×4) | Coding backend, frontend, PDF, Ollama | sonnet | Coding strutturato, senza ragionamento cross-progetto |
| `test-pdf-generator` | Genera PDF AcroForm di test | haiku | Task ripetitivo, template-driven → costo minimo |
| `presentation-builder` | Contenuto PPT e deliverable | sonnet | Generazione testo strutturato |

- Opus: usato **una volta sola**, per il coordinamento
- Haiku: task ripetitivo → costo minimo senza perdita di qualità
- Le skill riducono ulteriormente i token: un pattern scritto una volta, richiamato molte

**Speaker notes.**
Questa è la risposta concreta al criterio "efficienza dei token". Non si usa il modello più potente per tutto: si usa il modello più adeguato per ciascun compito. Opus ragiona sull'intero progetto e sulle dipendenze tra componenti — nessun altro agente lo fa. Sonnet scrive codice strutturato. Haiku genera un PDF di test secondo un template: non serve ragionamento. Le skill abbattono i token ripetuti: invece di rispiegare il pattern PDFBox in ogni prompt, la skill lo incapsula una volta.

---

## Slide 9 — Contributo AI e revisione umana

**Dove ha lavorato l'AI. Dove ha deciso l'umano.**

| Fase | AI | Umano |
|---|---|---|
| Ricerca formato PDF | subagent `research` | Scelta strategia: PDF generati da noi |
| Scelta modello Ollama | subagent → `qwen2.5:7b` | Conferma hardware disponibile |
| Struttura agentica | Proposta agenti, skill, tabella modelli | Conferma profondità e posizione file |
| Scaffolding backend/frontend | Builder sonnet — codice e build | — |
| Logica PDF | pdf-form-engineer + skill — PDFBox, test verdi | — |
| Presentazione e deliverable | presentation-builder sonnet | Revisione finale |

**Le decisioni** (tema, strategia, go/no-go di ogni fase) sono state prese dall'umano.
**La generazione** (codice, testo, struttura) è stata fatta dall'AI, con verifica a ogni passo.

**Speaker notes.**
Questa slide risponde a un vincolo esplicito della traccia: "dichiarare dove ha contribuito l'AI e dove è servita revisione umana". Non è una formalità — è parte dei criteri di valutazione. La distinzione è reale: nessuna riga di codice è stata scritta a mano, ma nessuna scelta strategica è stata delegata. L'AI ha eseguito e proposto; il team ha deciso e verificato.

---

## Slide 10 — Impatto e riproducibilità

**Un modulo oggi. Qualsiasi modulo AcroForm domani.**

- Tutto locale: Ollama + qwen2.5:7b, nessuna subscription, nessun dato inviato fuori
- Il pattern si applica a qualsiasi PDF con campi AcroForm: moduli INPS, ASL, bandi
- Anna non ha bisogno di sapere cos'è un AcroForm — vede solo le domande
- Il codice è nel repository; le istruzioni di esecuzione sono nel README

**Speaker notes.**
Chiudere sul perimetro di applicabilità. La scelta di usare Ollama locale non è solo economica: è una garanzia di privacy — i dati di Anna (nome, indirizzo, dati anagrafici) non escono mai dalla macchina. Il sistema non è legato a un modulo specifico: funziona su qualsiasi PDF con campi AcroForm. La riproducibilità è reale: i valutatori possono eseguire il software seguendo il README.
