# Un campo alla volta — Deliverable Tema 01 · Accessibilità Digitale

Strumento che affianca una persona che non comprende il linguaggio burocratico mentre
compila un modulo reale della PA, e la porta fino al documento compilato. Non spiega il
documento: **chiude la pratica**.

---

## 01 · Persona & Barriera

**Il modulo è reale.** `app/samples/dichiarazione-residenza.pdf` — Dichiarazione di
Residenza, 13 campi AcroForm, etichette burocratiche autentiche. Asset di riserva:
`app/samples/contributo-affitto.pdf` (11 campi, Domanda di Contributo Affitto).

> ### ⚠️ DA CONFERMARE — scegliere UNA delle due persone prima della demo
> Il team ha già rilevato che la persona attuale («una persona anziana che riceve un
> modulo della PA») è **troppo generica** e verrebbe scartata: la commissione vieta
> esplicitamente i profili generici. Le due proposte qui sotto sono alternative **non
> ancora decise**: vanno scelte, non sommate.

**Proposta A — Rosa Fabbri, 74 anni.** Vedova da tre mesi. Lascia la casa dove viveva col
marito e va ad abitare dalla figlia, in un altro comune. Entro 20 giorni deve presentare
la **Dichiarazione di Residenza**, altrimenti perde il medico di base assegnato e la
posta non la raggiunge. Usa WhatsApp, non ha mai compilato un modulo al computer.
*Dove si blocca — campo `titolo_occupazione`:* «Estremi del titolo di occupazione
dell'alloggio ai sensi dell'art. 5 D.L. 47/2014». Rosa non è né proprietaria né
affittuaria: è **ospite della figlia**. Non sa cosa sia un "titolo di occupazione", né
che nel suo caso la risposta è il nome della figlia proprietaria e il suo consenso. Il
campo resta bianco, il modulo resta bianco. Va al CAF: perde autonomia su casa sua.

**Proposta B — Driss El Amrani, 52 anni.** In Italia da nove anni, muratore, parla
italiano correntemente in cantiere. Si trasferisce in un appartamento in affitto e deve
presentare la stessa **Dichiarazione di Residenza**: senza residenza non rinnova la carta
d'identità né iscrive il figlio a scuola nel nuovo quartiere.
*Dove si blocca — campi `titolo_occupazione` e `dichiarazione_47`:* legge le singole
parole ma non il registro. «Estremi del titolo di occupazione dell'alloggio ai sensi
dell'art. 5 D.L. 47/2014» non gli dice che deve copiare **numero e data di registrazione
del suo contratto d'affitto**. E «Dichiarazione sostitutiva ai sensi dell'art. 47 DPR
445/2000» lo spaventa: non firma ciò che non capisce, per paura di dichiarare il falso.
Si ferma e chiede a un collega, con la sua busta paga in mano.

**Cosa hanno in comune, ed è il punto della traccia:** la barriera non è la vista, non è
il mouse, non è il contrasto. È che **il nome del campo non dice quale informazione
chiede**. Barriera cognitiva e linguistica su un servizio reale e obbligatorio.

---

## 02 · Percorso Assistito

1. **Un pulsante.** Carica il PDF. Nessuna registrazione, nessun account, nessuna
   configurazione (`POST /api/forms/upload`).
2. **Il sistema legge il documento.** PDFBox 3.x estrae i campi AcroForm con la loro
   etichetta reale (`/TU`): non un questionario preconfezionato, le domande nascono **dal
   documento che la persona ha in mano**.
3. **Una domanda alla volta.** Chat guidata: una schermata, una domanda, in italiano
   semplice, derivata dall'etichetta effettiva del campo.
4. **La persona risponde** da tastiera (`POST /api/forms/{sessionId}/answers`).
5. **Torna indietro il PDF compilato**, con ogni risposta nel campo giusto. Non un
   riassunto, non una spiegazione: **il documento pronto da consegnare**.

### Prima / dopo — etichette vere dai PDF in `app/samples/`

| Campo | Prima (etichetta del modulo) | Dopo (domanda posta alla persona) |
|---|---|---|
| `titolo_occupazione` | Estremi del titolo di occupazione dell'alloggio ai sensi dell'art. 5 D.L. 47/2014 | La casa dove va ad abitare è sua, in affitto, o è ospite di qualcuno? |
| `dichiarazione_47` | Dichiarazione sostitutiva ai sensi dell'art. 47 DPR 445/2000 | Conferma che quanto ha scritto è vero? Chi dichiara il falso ne risponde per legge. |
| `via_nuova` | Indirizzo di nuova dimora abituale - Via/Piazza | In che via si trova la casa dove andrà ad abitare? |
| `data_dichiarazione` | Data della dichiarazione (gg/mm/aaaa) | Che giorno è oggi? |
| `codice_fiscale` | Codice fiscale | Qual è il suo codice fiscale? Lo trova sulla tessera sanitaria. |
| `contratto_numero` * | Numero di registrazione contratto | Qual è il numero di registrazione scritto sul contratto d'affitto? |
| `isee_valore` * | Valore ISEE (euro) | Qual è l'importo ISEE scritto sull'attestazione che le ha dato il CAF? |

\* da `contributo-affitto.pdf`; gli altri da `dichiarazione-residenza.pdf`. La colonna
"prima" è il testo letterale del PDF, verificato sul file.

> **DA CONFERMARE:** la colonna "dopo" è generata a runtime dall'LLM, quindi la
> formulazione **varia a ogni esecuzione**; gli esempi qui sopra sono riformulazioni
> riviste a mano. Decisione aperta: *(a)* generare dal vivo in demo, oppure *(b)* fissare
> con testi rivisti da umano le domande dei campi a valenza legale e lasciare l'LLM sugli
> altri. Raccomandazione: **(b)**, per il motivo della sezione 03.

---

## 03 · Autonomia & Limiti

**Il guadagno è binario, non un'opinione.** Prima: il modulo resta bianco e la persona va
al CAF o chiama un figlio. Dopo: il PDF è compilato, scaricato, pronto da consegnare.
La misura della demo è questa, e si vede in 30 secondi.

**Semplificare senza tradire il significato.** Su un atto legale la formulazione è
sostanza, non forma. Riscrivere «Dichiarazione sostitutiva ai sensi dell'art. 47 DPR
445/2000» come «metti la spunta qui» sarebbe accessibile e **sbagliato**: toglierebbe
alla persona la consapevolezza di ciò che sta dichiarando. La domanda semplificata deve
restare **la stessa domanda**, non una più comoda. Per questo i campi di dichiarazione e
quelli che richiedono estremi di atti sono candidati a revisione umana fissa.

**Cosa il sistema non fa (dichiarato, non nascosto):**

- **Non verifica la correttezza dei dati.** Codice fiscale sbagliato in ingresso = PDF
  sbagliato in uscita. Nessuna validazione semantica, nessun controllo incrociato.
- **Non dà consulenza sulla pratica.** Non dice se hai diritto al contributo né quale
  documento allegare: non sostituisce il CAF su ciò che il CAF fa davvero.
- **La responsabilità di quanto dichiarato resta della persona.** Le mette in mano un
  documento compilato con le sue parole, non un parere.
- **Solo PDF con campi AcroForm.** Scansioni e OCR sono fuori scope: senza campi
  strutturati il flusso non parte. È il limite tecnico più duro, va detto in demo.
- **Nessuna risposta vocale.** Fuori scope, dichiarato.
- **Nessuna persistenza.** Stato in memoria per la durata della sessione, nessun
  database: i dati di una pratica della PA non restano su un server.
- **Se l'LLM non risponde, il flusso non si ferma:** fallback all'etichetta originale.
  Più difficile da leggere, ma la persona arriva comunque al PDF compilato.

---

## Contributo AI e revisione umana

**AI nel prodotto (runtime).** Un LLM **locale**, `qwen2.5:7b` via Ollama, trasforma ogni
etichetta burocratica in una domanda semplice. Prompt vincolato (italiano, una sola
domanda, max ~15 parole, temperature 0.3); output vuoto o oltre 18 parole → **re-prompt**;
secondo fallimento o Ollama irraggiungibile → **fallback all'etichetta originale**. Il
modello gira in locale: i dati del modulo non lasciano la macchina. L'AI **non** compila,
**non** valida, **non** interpreta le risposte: tocca solo la formulazione della domanda.

**AI nella costruzione.** Claude Code per lo scaffolding del backend Spring Boot e del
frontend Angular, l'integrazione PDFBox, la generazione riproducibile dei due PDF di
esempio (`SampleFormGenerator.java`) e la stesura di questo documento. Struttura agentica
e scelte di modello in `agents/README.md`.

**Dove è servita — e serve ancora — revisione umana:**

- **Scelta della persona.** Non decisa dall'AI: le due proposte della sezione 01 sono
  alternative aperte. È il punto più debole del lavoro e lo dichiariamo invece di
  mascherarlo.
- **Riformulazione dei campi a valenza legale** (`dichiarazione_47`,
  `titolo_occupazione`): l'output dell'LLM va letto da un umano, perché è lì che
  "semplificare" può diventare "alterare".
- **Verifica del PDF di uscita**: ogni risposta nel campo giusto — controllato a mano sui
  due sample e coperto da test (`PdfBoxFormServiceTest`).
- **Definizione dei limiti** della sezione 03: decisioni di prodotto del team, non
  suggerimenti del modello.
