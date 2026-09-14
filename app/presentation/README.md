# Presentazione — "Un campo alla volta"

Hackathon Accenture · 14 settembre 2026 · Tema 01 — Accessibilità Digitale

---

## Due presentazioni, due scopi diversi

In questa cartella convivono due materiali. Non sono alternative: rispondono a
domande diverse e vanno usati in momenti diversi.

| | `un-campo-alla-volta.pptx` | `slides.md` + i tre deliverable |
|---|---|---|
| **A chi parla** | Chi deve capire il valore del prodotto | Chi valuta il progetto e il metodo |
| **Cosa racconta** | Il problema, la soluzione, lo stack, la privacy | Persona, percorso, limiti, struttura agentica, contributo AI |
| **Durata** | 3 minuti, 7 slide, minutaggio nelle note del relatore | 10 slide |
| **Formato** | PowerPoint 16:9 già impaginato | Markdown, da impaginare sul template |

Il `.pptx` si rigenera con `python genera-deck.py` (servono `python-pptx` e
`Pillow`): le slide si modificano nello script, non a mano nel file, così il
deck resta allineato a ciò che l'app fa davvero.

Le immagini in `schermate/` sono **catture reali dell'applicazione in esecuzione**
— non mockup: `dichiarazione-residenza.pdf` caricato, 13 campi compilati, PDF
finale scaricato. Rifarle significa rieseguire l'app e ricatturarle.

---

## Contenuto di questa cartella

| File | Cosa contiene |
|---|---|
| `un-campo-alla-volta.pptx` | **Presentazione di vendita**, 7 slide, 3 minuti, con note del relatore |
| `genera-deck.py` | Sorgente del `.pptx`: la presentazione si modifica qui |
| `schermate/` | Schermate reali dell'app e il PDF compilato usati nel deck |
| `slides.md` | Struttura delle slide (10 slide), bullet essenziali e speaker notes per ciascuna |
| `01-persona-e-barriera.md` | Deliverable 1: chi è Anna, la barriera precisa, il momento esatto del blocco |
| `02-percorso-assistito.md` | Deliverable 2: il percorso prima e dopo, passo per passo |
| `03-autonomia-e-limiti.md` | Deliverable 3: cosa guadagna Anna, cosa il sistema non fa, il vincolo "semplificare senza tradire" |

---

## Come usare questi file

**Il PPT va impaginato dal team sul template brandizzato Accenture.**

Il file `slides.md` è la fonte di contenuto: contiene, per ciascuna delle 10 slide, il titolo, i bullet essenziali e le speaker notes. L'impaginazione grafica, la scelta dei colori, l'inserimento degli screenshot e degli asset visivi vengono fatti a parte, sul template Accenture ufficiale, a partire da questo file.

I tre file `01-`, `02-`, `03-` sono i deliverable del tema 01 nella loro forma estesa. Possono essere consegnati come allegati separati o usati come base per le slide corrispondenti.

---

## Struttura delle slide (indice)

1. **Titolo** — "Un campo alla volta", team, tema
2. **Persona & Barriera** — Anna, 71 anni, il modulo che la blocca _(Deliverable 1)_
3. **Il momento esatto del blocco** — il campo burocratico, il dubbio, il blocco
4. **La soluzione in una frase** — una domanda per campo, in italiano semplice
5. **Percorso Assistito: prima e dopo** — la tabella comparativa, demo live _(Deliverable 2)_
6. **Autonomia & Limiti** — cosa guadagna, cosa il sistema non fa _(Deliverable 3)_
7. **Struttura agentica** — orchestrator + 6 agenti + 3 skill
8. **Economia sui modelli** — tabella opus/sonnet/haiku, efficienza dei token
9. **Contributo AI e revisione umana** — disclosure completa per fase
10. **Impatto e riproducibilità** — locale, privato, applicabile a qualsiasi AcroForm

---

## Nota sulle fonti

I contenuti sono stati ricavati dalle seguenti fonti del progetto, senza aggiungere dati non presenti:

- `docs/caso-uso.md` — persona Anna, percorso, limiti
- `agents/README.md` — architettura agentica, tabella economia modelli, log contributo AI
- `docs/requisiti-consolidati.md` — criteri di valutazione, vincoli della traccia
- Contesto tecnico confermato durante lo sviluppo (stack, risultati dei test, tempi di avvio)
