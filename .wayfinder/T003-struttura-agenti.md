# T003 — Struttura agenti in `agents/`: quali agenti, quali modelli, come documentarli
label: wayfinder:grilling
status: open (ticket CENTRALE — baricentro del punteggio)
parent: MAP-001

## Question

I giudici valutano `agents/` su: profondità agentica · qualità istruzioni ·
robustezza istruzioni · **efficienza token** · adeguatezza strumenti · documentazione.
L'app è un veicolo semplice; QUESTA è la parte che vince.

## Proposta (da confermare col team)

Architettura a 3 strati che dimostra profondità + economia sui modelli.

### Strato 1 — Orchestrazione
| Agente | Compito | Modello | Perché quel modello | Tools |
|--------|---------|---------|---------------------|-------|
| `orchestrator` | Legge la spec, decompone in task di build, dispaccia ai builder, integra e verifica | **opus** | Coordinamento e ragionamento su dipendenze: l'unico punto che giustifica opus | Agent, Read, Write, TodoWrite |

### Strato 2 — Builder specializzati (modello più economico che regge il compito)
| Agente | Compito | Modello | Perché quel modello | Tools |
|--------|---------|---------|---------------------|-------|
| `spring-backend-builder` | Spring Boot: controller/service/config, endpoint REST | **sonnet** | Coding strutturato | Write, Edit, Bash, Read |
| `angular-frontend-builder` | Angular 21: UI accessibile (font grandi, un campo alla volta, riquadro testo originale) | **sonnet** | Coding UI | Write, Edit, Bash, Read |
| `pdf-form-engineer` | PDFBox: estrai campi AcroForm + compila/flatten | **sonnet** | Coding focalizzato su libreria | Write, Edit, Bash, Read |
| `ollama-integration-builder` | Client Java per Ollama /api/chat + wiring prompt | **sonnet** | Coding + integrazione | Write, Edit, Bash |
| `test-pdf-generator` | Genera PDF AcroForm di test (casi demo) | **haiku** | Task ripetitivo, template-driven → vetrina dell'economia token | Write, Bash |
| `presentation-builder` | Contenuto PPT + speaker notes + disclosure contributo AI | **sonnet** | Generazione testo strutturato | Write, Read |

### Strato 3 — Skill riusabili (evitano di ri-spiegare → risparmio token)
| Skill | Cosa incapsula |
|-------|----------------|
| `pdf-acroform-toolkit` | Pattern PDFBox read/fill AcroForm, snippet, gotcha |
| `ollama-italian-prompting` | Template prompt validato (T002) + pattern client + validazione conteggio parole |
| `accessible-ui-for-anna` | Pattern a11y Angular (font min, contrasto, focus, una domanda a schermo) |

### Cross-cutting (differenziatore)
- `agents/README.md` con: diagramma architettura · **tabella economia modelli** (agente|compito|modello|perché) · log "dove ha contribuito l'AI / dove è servita revisione umana" (vincolo traccia) · note efficienza token.

Copre 6 criteri su 8 in modo diretto.

## Domande aperte per il team
1. Profondità giusta? 6 agenti + 3 skill, oppure più snello (4 agenti) o più profondo?
2. Gli agenti vivono in `.claude/agents/` (per farli girare in Claude Code) con copia/link in `agents/` per i giudici — ok?

## Resolution

**CONFERMATA dall'utente:**
- Struttura: **6 agenti + 3 skill + orchestratore** (come da proposta sopra)
- Posizione: **`agents/` autoritativo + symlink in `.claude/`** (i file veri vivono in `agents/`, Claude Code li esegue via symlink `.claude/agents` → `../agents/subagents` e `.claude/skills` → `../agents/skills`)
- La tabella modelli è approvata: opus (orchestrator) · sonnet (5 builder) · haiku (test-pdf-generator). Questa è la scelta di modello per i subagent in fase di build.

Layout fisico:
```
agents/
├── README.md                 ← indice giudici + tabella economia modelli + log contributo AI
├── subagents/
│   ├── orchestrator.md            (opus)
│   ├── spring-backend-builder.md  (sonnet)
│   ├── angular-frontend-builder.md(sonnet)
│   ├── pdf-form-engineer.md       (sonnet)
│   ├── ollama-integration-builder.md (sonnet)
│   ├── test-pdf-generator.md      (haiku)
│   └── presentation-builder.md    (sonnet)
└── skills/
    ├── pdf-acroform-toolkit/SKILL.md
    ├── ollama-italian-prompting/SKILL.md
    └── accessible-ui-for-anna/SKILL.md
.claude/agents  → ../agents/subagents (symlink)
.claude/skills  → ../agents/skills   (symlink)
```

Status: CHIUSO ✅
