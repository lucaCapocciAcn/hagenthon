# Architettura agentica — Un campo alla volta

Documentazione della struttura ad agenti usata per progettare e costruire
l'applicazione. Non è un artefatto prodotto a posteriori: questi file sono
gli strumenti con cui il lavoro è stato eseguito.

I file in `agents/` sono **autoritativi**; Claude Code li esegue tramite
symlink (`.claude/agents → agents/subagents`, `.claude/skills → agents/skills`).

---

## Struttura a 3 livelli

```
                    ┌─────────────────┐
                    │   orchestrator  │  opus — ragiona, decompone, delega, integra
                    └────────┬────────┘
         ┌──────────────────┬┴─────────────────────┐
         ▼                  ▼                       ▼
 spring-backend-    angular-frontend-    pdf-form-engineer
   builder (S)        builder (S)        ollama-integration-builder (S)
         └───────── test-pdf-generator (H) ──────────────────────────┘

  skill: pdf-acroform-toolkit · ollama-italian-prompting · accessible-ui-guidelines
```

**Principio:** il modello più economico che regge il compito.
Opus solo dove serve ragionamento sull'intero progetto; sonnet per il coding;
haiku per task ripetitivi e template-driven.

---

## Economia sui modelli

| Agente | Responsabilità | Modello | Motivazione |
| --- | --- | --- | --- |
| `orchestrator` | Decompone la spec, delega, integra, verifica | **opus** | Unico punto con ragionamento su dipendenze e trade-off dell'intero progetto |
| `spring-backend-builder` | Spring Boot: controller/service/config, 2 endpoint | **sonnet** | Coding strutturato; nessun ragionamento cross-progetto |
| `angular-frontend-builder` | Angular 21: UI accessibile | **sonnet** | Coding UI |
| `pdf-form-engineer` | PDFBox: estrai e compila campi AcroForm | **sonnet** | Coding focalizzato su libreria specifica |
| `ollama-integration-builder` | Client Ollama + generazione domande semplificate | **sonnet** | Coding + integrazione HTTP |
| `test-pdf-generator` | Genera PDF AcroForm di esempio | **haiku** | Task ripetitivo e template-driven → costo token minimo |

---

## Skill condivise

Le skill incapsulano pattern riusabili: definiti una volta, richiamati da più
agenti senza ri-spiegare gli stessi dettagli in ogni prompt.

| Skill | Cosa incapsula |
| --- | --- |
| `pdf-acroform-toolkit` | Pattern PDFBox 3.x: read / fill / flatten AcroForm |
| `ollama-italian-prompting` | Prompt validato, client HTTP, validazione output, fallback |
| `accessible-ui-guidelines` | Regole di accessibilità: font, contrasto, singolo campo per schermata |

---

## Log contributo AI

| Fase | Contributo AI | Decisione umana |
| --- | --- | --- |
| Scelta modello LLM | Analisi comparativa modelli Ollama | Conferma `qwen2.5:7b` e hardware disponibile |
| Struttura agentica | Proposta agenti + skill + tabella modelli | Conferma profondità (6 agenti + 3 skill) e posizione file |
| Scaffolding backend | `spring-backend-builder` — Spring Boot, 2 endpoint, interfacce | — |
| Scaffolding frontend | `angular-frontend-builder` + skill `accessible-ui-guidelines` | — |
| Logica PDF | `pdf-form-engineer` + skill `pdf-acroform-toolkit` — PDFBox, test verdi | — |
| Integrazione Ollama | `ollama-integration-builder` + skill `ollama-italian-prompting` — client + fallback | — |
| PDF di esempio | `test-pdf-generator` — moduli AcroForm realistici | — |

Le **decisioni** (obiettivo, scelta tecnologie, strategia PDF, profondità agentica,
go/no-go di ogni fase) sono state prese dall'umano. L'AI ha svolto ricerca,
proposta e generazione di codice, con verifica oggettiva a ogni passo
(`mvn compile` / `mvn test` verdi).
