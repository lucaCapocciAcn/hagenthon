# Hagenthon — Accessibilità Digitale

## Regola sul codice

**Nessuna modifica al codice senza richiesta esplicita dell'utente.** Niente
implementazioni decise in autonomia, niente refactor non chiesti, niente file
creati "perché servivano". Si propone, l'utente decide.

## Cosa stiamo costruendo

Uno strumento che affianca **una persona con una difficoltà precisa** mentre usa
un servizio digitale reale, e le permette di arrivare fino in fondo al suo
obiettivo — cosa che oggi, da sola, non riesce a fare.
Hackathon interno Accenture, 14 settembre 2026, team da 2 persone, 5 ore.

Il problema specifico dentro il tema **non è ancora stato definito**: è la prima
cosa da decidere, e la traccia lo chiede esplicitamente.

## PERIMETRO

### Dentro

- Tema **01 — Accessibilità Digitale**, e solo quello.
- Una **persona concreta** e una **barriera precisa**: chi è, cosa sta cercando
  di fare, in quale momento esatto si blocca.
- Un **servizio o contenuto digitale reale o realistico**: un sito pubblico, un
  modulo, una bolletta, un'app, una procedura online.
- La soluzione dev'essere usabile **dalla persona stessa**, non da uno
  sviluppatore o da un tecnico.

### Fuori

- I temi **02 (inclusione finanziaria)** e **03 (educazione digitale
  inclusiva)**: scartati, con i loro vincoli e deliverable.
- **Audit di conformità e checker automatici.** La traccia è esplicita: *«il
  focus non è l'audit tecnico del codice né la conformità formale alle linee
  guida: è la persona»*. A voce era stata nominata la legge Stanca — il
  documento scritto prevale.
- Strumenti pensati per sviluppatori invece che per la persona con la difficoltà.
- Soluzioni che si fermano alla **diagnosi** del problema senza aiutare nessuno
  a superarlo.
- Restyling grafici che non fanno guadagnare autonomia.
- Profili utente generici: «un utente disabile» non è un profilo.

## Consegne e scadenze

| Quando | Cosa |
| --- | --- |
| 13:00 | pausa pranzo, un'ora, flessibile |
| ~16:00 | fine evento |
| durante | debriefing intermedi, orari non dichiarati |

**Cosa si consegna** — push su repository GitHub, tre parti:

1. `app/` — il software prodotto (un **prototipo**, più la **demo finale**)
2. `agents/` — le risorse agentiche che hanno supportato la creazione
3. la **presentazione** — PPT su template brandizzato Accenture

Più un **README con le istruzioni per eseguire il software**: i valutatori lo
scaricano e lo eseguono.

Più i **tre deliverable specifici del tema 01**: *Persona & Barriera* ·
*Percorso Assistito* · *Autonomia & Limiti*.

## Vincoli non negoziabili

- **Semplificare senza tradire**: il significato delle informazioni originali non
  deve cambiare.
- In demo va mostrato il percorso dell'utente **prima e dopo**: cosa non riusciva
  a fare, cosa riesce a fare adesso.
- Va dichiarato **dove ha contribuito l'AI e dove è servita revisione umana**.
  L'uso dell'AI dev'essere spiegabile dal team.
- Si valuta anche la **qualità delle risorse agentiche** e l'**efficienza dei
  token**, non solo il software.
- La **capacità espositiva pesa quanto quella tecnica**.

## Dove sta il resto

- **`docs/requisiti-consolidati.md`** — il quadro completo: tutte le consegne, i
  criteri di valutazione, i vincoli della traccia, le domande ancora aperte.
- **`docs/aggiornamenti.md`** — vedi qui sotto.

## Prima di implementare, leggi il taccuino

I requisiti in `docs/requisiti-consolidati.md` sono fermi al momento in cui la
raccolta è stata chiusa. La conversazione però è proseguita: **prima di
implementare qualcosa, leggi `docs/aggiornamenti.md`**, dove il listener annota
ciò che è stato detto dopo.
