# Architettura agentica — Un campo alla volta

Documentazione della struttura ad agenti usata per progettare e costruire
l'applicazione. Non è un artefatto prodotto a posteriori: questi file sono
gli strumenti con cui il lavoro è stato eseguito.

I file in `agents/` sono **autoritativi**; Claude Code li esegue tramite symlink
(`.claude/agents → agents/subagents`, `.claude/skills → agents/skills`,
`.claude/commands → agents/commands`). Un solo posto da leggere per chi valuta,
e gli strumenti trovano comunque tutto dove se lo aspettano. L'unico file reale
dentro `.claude/` è `settings.json`, perché deve stare lì.

```
agents/
├── subagents/   6 agenti specializzati        → chi esegue
├── skills/      3 pattern riusabili           → come si fa una cosa specifica
├── rules/       4 regole per area             → cosa è ammesso in quest'area
├── commands/    3 comandi                     → il flusso di lavoro, eseguibile
└── hooks/       3 hook                        → i vincoli, non negoziabili
```

La differenza fra i quattro: una **skill** si carica quando serve un pattern
tecnico; una **regola** vincola chi tocca un'area; un **comando** esegue un
flusso; un **hook** non si può ignorare perché gira da solo.

---

## Il vincolo che ha guidato ogni scelta: il costo in token

L'efficienza dei token è un criterio di valutazione dichiarato. Di conseguenza
l'harness è progettato per **caricare poco e su richiesta**, non per sembrare
grande:

- `CLAUDE.md` non incolla le regole: ha una **tabella di routing** che dice
  quale file leggere per quale area. Chi tocca il backend non paga le regole Angular.
- **3 plugin abilitati su 21 disponibili** (vedi sotto).
- Ogni agente ha il **modello più economico che regge il compito**.
- Nessuna regola orfana: `/harness-check` segnala i file che nessuno carica.

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

| Agente | Responsabilità | Modello | Motivazione |
| --- | --- | --- | --- |
| `orchestrator` | Decompone la spec, delega, integra, verifica | **opus** | Unico punto con ragionamento su dipendenze e trade-off dell'intero progetto |
| `spring-backend-builder` | Spring Boot: controller/service/config, 2 endpoint | **sonnet** | Coding strutturato; nessun ragionamento cross-progetto |
| `angular-frontend-builder` | Angular: UI accessibile | **sonnet** | Coding UI |
| `pdf-form-engineer` | PDFBox: estrai e compila campi AcroForm | **sonnet** | Coding focalizzato su libreria specifica |
| `ollama-integration-builder` | Client Ollama + generazione domande semplificate | **sonnet** | Coding + integrazione HTTP |
| `test-pdf-generator` | Genera PDF AcroForm di esempio | **haiku** | Task ripetitivo e template-driven → costo token minimo |

---

## Skill condivise

Pattern riusabili: definiti una volta, richiamati da più agenti senza
ri-spiegare gli stessi dettagli in ogni prompt.

| Skill | Cosa incapsula |
| --- | --- |
| `pdf-acroform-toolkit` | Pattern PDFBox 3.x: read / fill / flatten AcroForm |
| `ollama-italian-prompting` | Prompt validato, client HTTP, validazione output, fallback |
| `accessible-ui-guidelines` | Accessibilità: font, contrasto, singolo campo per schermata |

## Regole per area

Caricate **solo** dall'area pertinente, via la tabella in `CLAUDE.md`.

| Regola | Quando si carica |
| --- | --- |
| [`rules/spring-boot.md`](rules/spring-boot.md) | Si tocca `app/backend/**` |
| [`rules/angular.md`](rules/angular.md) | Si tocca `app/frontend/**` |
| [`rules/workflow-issue.md`](rules/workflow-issue.md) | Si lavora su issue, branch, PR |
| [`rules/harness.md`](rules/harness.md) | Si estende l'harness stesso |

Le regole Angular derivano dal file ufficiale Angular per LLM
(<https://angular.dev/assets/context/best-practices.md>) **adattate alla v21**:
tre sue regole valgono solo da v22 e copiarle così com'erano avrebbe introdotto
istruzioni false. Per Spring un equivalente ufficiale **non esiste** (verificato):
quelle regole sono scritte a mano per questo progetto.

## Comandi

| Comando | Cosa fa |
| --- | --- |
| `/issue-take <n>` | Verifica che sia prendibile, passa a `agent:in-progress`, crea il branch, indica quali regole leggere |
| `/issue-done <n>` | Build verde → commit → PR → `agent:review`. Non fa merge: decide una persona |
| `/harness-check` | Symlink, hook eseguibili, regole orfane, label mancanti |

## Hook — i vincoli che non si possono ignorare

| Hook | Evento | Cosa impone |
| --- | --- | --- |
| `guard-main.sh` | `PreToolUse(Bash)` | Blocca `git commit`/`push` su `main`. Il contributo degli agenti deve restare leggibile in diff separati |
| `session-brief.sh` | `SessionStart` | Cinque righe: branch e issue `agent:ready`. Volutamente cortissimo |
| `format-touched.sh` | `PostToolUse(Write\|Edit)` | Formatta solo il file toccato, in silenzio |

Sono scritti per girare **anche sulla macchina di chi valuta**: escono `0`
quando non hanno niente da fare e non assumono che `jq`, `mvn` o `prettier`
esistano. Verificabili a mano:

```bash
echo '{"tool_input":{"command":"git commit -m x"}}' | ./agents/hooks/guard-main.sh
echo "exit=$?"   # 2 = bloccato correttamente
```

## Ciclo di vita delle issue

`agent:ready` → `agent:in-progress` → `agent:review` → chiusa, con
`agent:blocked` come uscita laterale quando serve una decisione umana.
Aree (`area:backend|frontend|harness|docs`) e priorità (`prio:p0|p1|p2`).
Regola completa: [`rules/workflow-issue.md`](rules/workflow-issue.md).

## Plugin abilitati: 3 su 21 — e perché proprio questi

`.claude/settings.json` abilita a livello di progetto **solo tre** dei plugin
disponibili nell'ambiente. Ogni plugin abilitato è contesto caricato a **ogni**
sessione: abilitarli tutti sarebbe costato token su un criterio che viene misurato.

| Plugin | Perché è dentro |
| --- | --- |
| `github` | Il flusso issue → branch → PR passa da qui |
| `superpowers` | TDD e scrittura dei piani |
| `mattpocock-skills` | `grilling`: interrogare una richiesta prima di implementarla |

Tutti e tre sono **usati davvero** nel lavoro su questo repo. Un plugin abilitato
e mai usato sarebbe costo puro.

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
| Harness (regole, hook, comandi, CI) | Interrogazione della richiesta con `grilling`, ricerca versioni verificate, stesura | Scelta di ogni bivio: GitHub, upgrade Angular, ramo Spring, 3 plugin su 21 |

Le **decisioni** (obiettivo, scelta tecnologie, strategia PDF, profondità
agentica, go/no-go di ogni fase) sono state prese dall'umano. L'AI ha svolto
ricerca, proposta e generazione di codice, con verifica oggettiva a ogni passo
(`mvn verify` / `ng build` verdi, hook provati su input reale).
