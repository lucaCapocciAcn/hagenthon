# Wayfinder Map — "Un campo alla volta"
label: wayfinder:map
id: MAP-001

## Destination

Demo funzionante alle ~16:00: l'app "Un campo alla volta" guida Anna (71 anni,
pensionata) nella compilazione del modulo burocratico PDF un campo alla volta,
in italiano semplice, con caratteri grandi — e restituisce il PDF compilato.
Consegnabile: `app/` (Angular 21 + Spring Boot + Ollama) · `agents/` (struttura
agenti Claude giudicabile) · presentazione PPT Accenture.

**Baricentro del punteggio (decisione utente):** l'app è un veicolo dimostrativo
SEMPLICE. Il valore giudicato sta nella **struttura agentica** (`agents/`: agenti
+ skill, orchestratore) e nell'**economia sui modelli** (modello giusto per ogni
compito, efficienza token). Costruire con l'AI > software che usa l'AI (DEL-028).

## Notes

Stack: Angular 21 (FE) · Java Spring Boot (BE, package controller/service/config) · Ollama (LLM locale, nessuna subscription utente).
Persona: Anna, 71 anni, pensionata, bassa alfabetizzazione digitale, lieve calo visivo.
Modulo: cambio di residenza (Ministero dell'Interno) o contributo affitto Comune.
Criteri valutazione pesanti: profondità agentica · qualità istruzioni · efficienza token · qualità idea.
Riferimenti: docs/caso-uso.md · docs/aggiornamenti.md · docs/requisiti-consolidati.md.
Priorità: T001 e T002 sono research avviabili subito in parallelo.

## Decisions so far

- [T002 — Modello Ollama per italiano](.wayfinder/T002-modello-ollama-italiano.md): **`qwen2.5:7b`** (4.7 GB RAM, 2-4 s su Apple Silicon). Alternativa leggera `phi3:mini` se < 8 GB RAM. API: `POST localhost:11434/api/chat`, stream false, temperature 0.3, num_predict 60.
- [T001 — Formato PDF modulo residenza](.wayfinder/T001-formato-pdf-modulo.md): il modulo Ministero è **SCANSIONATO, zero AcroForm**. Libreria: **PDFBox 3.x**.
- **Strategia PDF (decisa dall'utente)**: i PDF di test li genera Claude come **AcroForm veri**, in un secondo momento. La pipeline resta **generica e semplice** (leggi campi AcroForm → semplifica via Ollama → ricompila). Nessuna dipendenza dal modulo scansionato reale.
- **Priorità del progetto (decisa dall'utente)**: l'app è un **veicolo dimostrativo semplice**. Il deliverable centrale e giudicato è la **struttura agentica** (agents + skills) e l'**economia sui modelli** (modello giusto per compito, efficienza token). Coerente con DEL-028.
- [T005 — Architettura pipeline](.wayfinder/T005-architettura-pipeline.md): pipeline generica AcroForm semplice. `POST /api/forms/upload` (estrae campi + genera domande upfront via Ollama) → `POST /api/forms/{id}/answers` (compila e restituisce PDF). State in memoria, PDFBox, RestClient nativo.
- [T003 — Struttura agenti](.wayfinder/T003-struttura-agenti.md): **6 agenti + 3 skill + orchestratore**. opus (orchestrator) · sonnet (5 builder) · haiku (test-pdf-generator). File autoritativi in `agents/`, symlink in `.claude/`. Questa è anche la scelta modelli per i subagent di build.

## Not yet specified

Le decisioni sono chiuse. Ciò che resta è **esecuzione**, delegata agli agenti di T003:

- BUILD-1: creare i file agenti/skill in `agents/` + symlink `.claude/` — _in corso_
- BUILD-2: `orchestrator` scaffolda Spring Boot (`spring-backend-builder`) + Angular 21 (`angular-frontend-builder`)
- BUILD-3: `pdf-form-engineer` (estrai/compila AcroForm) + `ollama-integration-builder` (client Ollama)
- BUILD-4: `test-pdf-generator` (haiku) genera 1-2 PDF AcroForm di test
- BUILD-5: `presentation-builder` genera il PPT + i 3 deliverable tema 01
- BUILD-6: README con istruzioni di esecuzione (avvio Ollama, BE, FE)

Restano due nodi di conferma (non bloccanti per iniziare):
- T004 (UX): mockup proposto nel ticket, da validare col team
- OQ: quali 1-2 casi d'uso PDF concreti (residenza + contributo affitto?)

## Out of scope

- Risposta vocale (OQ-001): speechRecognition Chrome dipende da Google server → fuori scope, dichiarare in presentazione
- Moduli PDF scansionati / immagine: OCR + positioning = ordine di grandezza diverso → fuori scope, se il modulo reale è scansionato si prepara un PDF AcroForm ad hoc
- Selezione lingua all'avvio: hardcodare italiano, rimuovere questo step per semplicità e robustezza
- Registrazione / autenticazione utente
