# Obiettivo e caso d'uso — consolidato

Hagenthon, tema 01 Accessibilità Digitale · 14 settembre 2026, ore 10:20
Fonte: `transcript/2026-09-14/09-01-06.jsonl`, righe 1010-1123.

---

## Obiettivo

Permettere a una persona che **non comprende il linguaggio burocratico** di
compilare da sola, fino in fondo, un modulo che oggi non riuscirebbe a
compilare senza aiuto.

Non si spiega il documento: si **porta a termine la pratica**.

---

## 01 · Persona & Barriera

**Chi.** Una persona anziana che riceve un modulo della pubblica
amministrazione e deve compilarlo. *(REQ-002, riga 1016)*

**La barriera.** Il modulo è scritto in nomenclatura burocratica: i nomi dei
campi non dicono, a chi legge, quale informazione stiano chiedendo. La persona
non sa *cosa* scrivere, non *come* scriverlo.

**Dove si ferma oggi.** Davanti al modulo, prima di iniziare. Oppure chiede a
un figlio, a un patronato, a un CAF — cioè perde autonomia.

> ⚠️ **Questo profilo non è ancora abbastanza concreto.** La traccia è esplicita:
> «profili utente generici: "un utente disabile" non è un profilo». Serve
> decidere *quale* modulo, *quale* persona, *quale* passaggio esatto la blocca.
> È il primo deliverable del tema e al momento è il punto più debole.

---

## 02 · Percorso Assistito

Il flusso deciso *(REQ-002, REQ-004, righe 1010-1063)*:

1. **Un pulsante.** L'interfaccia è un solo pulsante per caricare il documento.
   Niente registrazione, niente configurazione.
2. **Il sistema legge il documento** e ne ricava i campi da riempire.
3. **Si apre una chat.** Il sistema pone **una domanda alla volta**, in
   linguaggio semplice, derivata dal campo effettivo del documento.
   *Esempio citato: invece di un'etichetta burocratica, «chi è la persona che è
   venuta a mancare?»* (riga 1031).
4. **La persona risponde** — da tastiera, e forse anche a voce (OQ-001, aperta).
5. **Il sistema restituisce il PDF compilato** con le risposte al posto giusto.

Il valore sta nel passo 5: non un riassunto, non una spiegazione, ma **il
documento pronto**.

---

## 03 · Autonomia & Limiti

**Cosa guadagna la persona.** Completa da sola una pratica per cui oggi dipende
da qualcun altro. Il guadagno è misurabile in modo netto: prima il modulo resta
bianco, dopo è compilato.

**Cosa non deve cambiare.** Il significato dei campi. Semplificare la domanda
non deve alterare l'informazione che l'ente sta chiedendo — è un vincolo
esplicito della traccia, e su un documento legale è anche una questione di
sostanza.

**Limiti da dichiarare in demo.** Il sistema non verifica la correttezza dei
dati inseriti, non dà consulenza sulla pratica, e la responsabilità di quanto
dichiarato resta della persona.

---

## Decisioni prese

| Id | Decisione | Stato |
| --- | --- | --- |
| DEL-027 | Tema 01 — Accessibilità Digitale | fermo |
| D-001 | Lo scaffolding del repo va rispettato alla lettera | fermo |
| REQ-004 | UI minimale: un pulsante, poi una chat | fermo |
| D-002 | Frontend in Angular | **da confermare** (trascrizione corrotta) |
| REQ-001 | Nessuna sottoscrizione AI richiesta all'utente finale | **in discussione** |

---

## Nodi ancora aperti

1. **La persona concreta.** Quale modulo reale, quale persona, quale blocco
   esatto. Senza questo il deliverable 01 non esiste. *(il più urgente)*
2. **OQ-001 — risposta vocale?** `speechSynthesis` per leggere ad alta voce è
   locale e gratuito; `SpeechRecognition` in Chrome manda l'audio ai server
   Google — nessun account richiesto, ma non è on-device.
3. **Il motore.** REQ-002 chiede di capire un documento burocratico; REQ-001
   chiede di non dipendere da un LLM a pagamento. Si tiene insieme con un
   modello on-device, con template pre-analizzati, o rinunciando a uno dei due.
4. **Il PDF.** Il flusso regge in 5 ore **solo se il modulo ha campi AcroForm**
   (`pdf-lib` li legge e li riscrive). Su un PDF scansionato servono OCR e
   posizionamento: altro ordine di grandezza.
5. **L'acronimo** citato come esempio («D.V.U.» nella trascrizione) non è
   ricostruibile: il contesto rimanda a una successione.
