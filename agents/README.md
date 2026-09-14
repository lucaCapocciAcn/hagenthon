# Risorse agentiche — "Un campo alla volta"

Gli agenti, le skill e l'orchestrazione con cui questa applicazione è stata
**costruita**. Non un artefatto confezionato a posteriori: sono gli strumenti con
cui il lavoro è stato fatto. L'obiettivo dell'hackathon è usare l'AI per *creare*
il software (DEL-028), e questa cartella lo documenta.

I file qui in `agents/` sono **autoritativi**; Claude Code li esegue tramite symlink
(`.claude/agents → agents/subagents`, `.claude/skills → agents/skills`): ciò che i
giudici leggono è esattamente ciò che ha girato.

## Architettura a 3 strati

```
                    ┌───────────────┐
                    │ orchestrator  │  opus — ragiona, decompone, delega, integra
                    └───────┬───────┘
        ┌───────────────────┼───────────────────────────┐
        ▼                   ▼                            ▼
 spring-backend-     angular-frontend-      pdf-form-engineer / ollama-
   builder (S)          builder (S)         integration-builder (S)
        └──────── test-pdf-generator (H) ──── presentation-builder (S) ┘

 skill: pdf-acroform-toolkit · ollama-italian-prompting · accessible-ui-for-anna
              (procedure riusabili → meno token ripetuti)
```

## Economia sui modelli

Il modello **più economico che regge il compito**. Opus solo dove serve ragionamento
sull'intero progetto; sonnet per il coding; haiku per il task ripetitivo. Questa
gradazione è la risposta concreta al criterio "efficienza dei token".

| Risorsa | A cosa serve | Modello | Perché quel modello |
| --- | --- | --- | --- |
| `orchestrator` | Decompone la spec, delega, integra, verifica | **opus** | Unico punto con ragionamento su dipendenze e trade-off |
| `spring-backend-builder` | Spring Boot: controller/service/config, 2 endpoint | **sonnet** | Coding strutturato, nessun ragionamento cross-progetto |
| `angular-frontend-builder` | Angular 21: UI accessibile per Anna | **sonnet** | Coding UI |
| `pdf-form-engineer` | PDFBox: estrai/compila AcroForm | **sonnet** | Coding focalizzato su libreria |
| `ollama-integration-builder` | Client Ollama + generazione domande | **sonnet** | Coding + integrazione |
| `test-pdf-generator` | Genera PDF AcroForm di test | **haiku** | Task ripetitivo, template-driven → costo minimo |
| `presentation-builder` | Contenuto PPT + deliverable tema 01 | **sonnet** | Generazione testo strutturato |

Skill = procedure incapsulate una volta e richiamate da più agenti: evitano di
ri-spiegare gli stessi pattern in ogni prompt (risparmio token diretto).

| Skill | Incapsula |
| --- | --- |
| `pdf-acroform-toolkit` | Pattern PDFBox read/fill/flatten AcroForm |
| `ollama-italian-prompting` | Prompt validato + client + validazione output |
| `accessible-ui-for-anna` | Regole a11y (font, contrasto, un campo per schermo) |

## Dove ha contribuito l'AI, dove è servita revisione umana

Log aggiornato durante il lavoro (vincolo della traccia).

| Fase | Contributo AI | Revisione / decisione umana |
| --- | --- | --- |
| Ricerca formato PDF (T001) | subagent `research` | scelta della strategia PDF (PDF generati da noi) |
| Scelta modello Ollama (T002) | subagent `research` → `qwen2.5:7b` | conferma hardware disponibile |
| Struttura agentica (T003) | proposta agenti + skill + tabella modelli | conferma profondità (6+3) e posizione file |
| Scaffolding backend | `spring-backend-builder` (sonnet) — Spring Boot, 2 endpoint, interfacce; `mvn compile` verde | — |
| Scaffolding frontend | `angular-frontend-builder` (sonnet) + skill `accessible-ui-for-anna` — 2 schermate, build pulito | — |
| Logica PDF | `pdf-form-engineer` (sonnet) + skill `pdf-acroform-toolkit` — PDFBox reale, `mvn test` 3/3 | — |
| Integrazione Ollama | `ollama-integration-builder` (sonnet) + skill `ollama-italian-prompting` — client + fallback | _in corso_ |
| PDF di test | `test-pdf-generator` (haiku) | _in coda_ |
| Presentazione + deliverable | `presentation-builder` (sonnet) | _in coda_ |

Nota di trasparenza: le **decisioni** (tema, strategia PDF, profondità agentica, scelta modelli, go/no-go di ogni fase) sono state prese dall'umano; l'AI ha svolto ricerca e generazione di codice, con verifica oggettiva a ogni passo (`mvn compile`/`mvn test` verdi).
