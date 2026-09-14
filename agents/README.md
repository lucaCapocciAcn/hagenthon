# Architettura agentica — Un campo alla volta

Documentazione della struttura ad agenti usata per progettare e costruire
l'applicazione. Non è un artefatto prodotto a posteriori: questi file sono
gli strumenti con cui il lavoro è stato eseguito.

I file in `agents/` sono **autoritativi**; Claude Code li esegue tramite symlink
(`.claude/agents → agents/subagents`, `.claude/skills → agents/skills`,
`.claude/commands → agents/commands`). Un solo posto da leggere per chi arriva sul repo,
e gli strumenti trovano comunque tutto dove se lo aspettano. L'unico file reale
dentro `.claude/` è `settings.json`, perché deve stare lì.

```
agents/
├── subagents/   7 agenti specializzati        → chi esegue
├── skills/      3 pattern riusabili           → come si fa una cosa specifica
├── rules/       4 regole per area             → cosa è ammesso in quest'area
├── commands/    3 comandi                     → il flusso di lavoro, eseguibile
└── hooks/       5 hook + suite di test        → i vincoli, non negoziabili
```

La differenza fra i quattro: una **skill** si carica quando serve un pattern
tecnico; una **regola** vincola chi tocca un'area; un **comando** esegue un
flusso; un **hook** non si può ignorare perché gira da solo.

---

## Il vincolo che ha guidato ogni scelta: il costo del contesto

Tutto ciò che l'harness dichiara viene caricato in **ogni** sessione, anche in
quelle che non ne hanno bisogno. Un harness grande non è un harness migliore:
è un harness che diluisce le istruzioni che contano. Quindi è progettato per
**caricare poco e su richiesta**:

- `CLAUDE.md` non incolla le regole: ha una **tabella di routing** che dice
  quale file leggere per quale area. Chi tocca il backend non paga le regole Angular.
- **Solo i plugin effettivamente usati sono abilitati** (vedi sotto).
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
 be-orchestrator    angular-frontend-    pdf-form-engineer
        (S)            builder (S)        ollama-integration-builder (S)
         └───────── test-pdf-generator (H) ──────────────────────────┘

  skill: pdf-acroform-toolkit · ollama-italian-prompting · accessible-ui-guidelines
```

**Principio:** il modello più economico che regge il compito.
Opus solo dove serve ragionamento sull'intero progetto; sonnet per il coding;
haiku per task ripetitivi e template-driven.

| Agente | Responsabilità | Modello | Motivazione |
| --- | --- | --- | --- |
| `orchestrator` | Decompone la spec, delega, integra, verifica | **opus** | Unico punto con ragionamento su dipendenze e trade-off dell'intero progetto |
| `be-orchestrator` | Decompone il lavoro Spring Boot in task atomici, delega ai micro-agenti, valida il BE | **sonnet** | Coordina il backend senza scrivere codice applicativo |
| `angular-frontend-builder` | Angular: UI accessibile | **sonnet** | Coding UI |
| `pdf-form-engineer` | PDFBox: estrai e compila campi AcroForm | **sonnet** | Coding focalizzato su libreria specifica |
| `ollama-integration-builder` | Client Ollama + generazione domande semplificate | **sonnet** | Coding + integrazione HTTP |
| `test-pdf-generator` | Genera PDF AcroForm di esempio | **haiku** | Task ripetitivo e template-driven → costo token minimo |
| `code-reviewer` | Revisiona il diff prima della PR ed emette un verdetto | **sonnet** | Giudizio su un diff circoscritto, con le regole già scritte come riferimento. Non ha `Write`/`Edit`: un revisore che aggiusta non sta revisionando |

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
| `/issue-take <n>` | Verifica che sia prendibile, passa a `agent:in-progress`, **crea il branch derivandone il nome dal titolo della issue**, indica quali regole leggere |
| `/issue-done <n>` | Build AOT + lint verdi → **review obbligatoria del subagent `code-reviewer`** → commit → PR → `agent:review`. Non fa merge: decide una persona |
| `/harness-check` | Symlink, hook eseguibili, regole orfane, label mancanti |

## Hook — i vincoli che non si possono ignorare

| Hook | Evento | Cosa impone |
| --- | --- | --- |
| `guard-main.sh` | `PreToolUse(Bash)` | Blocca `git commit`/`push` su `main`. Il contributo degli agenti deve restare leggibile in diff separati |
| `session-brief.sh` | `SessionStart` | Poche righe: branch (e se è indietro), **PR aperte**, issue `agent:ready`. Volutamente cortissimo |
| `guard-branch-base.sh` | `PreToolUse(Bash)` | Un branch nuovo nasce **solo da `main` allineato a `origin/main`**. Offline non blocca: non potendo verificare, non impedisce di lavorare |
| `pr-link.sh` | `PostToolUse(Bash)` | Intercetta l'URL di una PR appena aperta, lo stampa e lo registra in `.claude/pr-links.log`. Una PR mai più nominata è lavoro che nessuno chiude |
| `format-touched.sh` | `PostToolUse(Write\|Edit)` | Formatta solo il file toccato, in silenzio |

Sono scritti per girare **su qualsiasi macchina che faccia checkout**: escono `0`
quando non hanno niente da fare e non assumono che `jq`, `mvn` o `prettier`
esistano. `_lib.sh` fornisce un `run_timeout` portabile perché **`timeout` è
GNU e su macOS non esiste** — un hook che lo usa fallisce in silenzio. Verificabili a mano:

```bash
./agents/hooks/test-hooks.sh      # gira anche in CI
```

Il match sui comandi git è **ancorato**, non su sottostringa: `echo "un git
commit"` e `grep -rn "git push"` non vengono bloccati, mentre `cd app && git
commit` e `git -c user.name=x commit` sì. Con il match ingenuo che c'era prima,
l'autotest di questa stessa pagina si autobloccava.

## Il gate di review

`/issue-done` **non apre la PR** finché il subagent `code-reviewer` non emette
`VERDETTO: APPROVATO`. Gli altri due esiti — `MODIFICHE RICHIESTE` e `BLOCCATO`
— fermano il flusso. È la risposta a un problema concreto: chi ha scritto il
codice è la persona peggio posizionata per revisionarlo, e un agente che
revisiona sé stesso approva sempre.

Il revisore non ha `Write` né `Edit`. Non è una dimenticanza: senza la
possibilità di correggere, l'unica cosa che può fare è **dire** cosa non va, e
il rilievo resta agli atti invece di sparire in una modifica silenziosa.

Chiede sei cose, in ordine: fa quello che la issue chiedeva (né meno né più) ·
viola una regola dell'area · quale input concreto la rompe · i test coprono il
comportamento nuovo · il flusso si blocca mai · l'errore resta leggibile da chi
usa l'app. E **riesegue** build AOT, lint e test invece di fidarsi del diff.

## Ciclo di vita delle issue

`agent:ready` → `agent:in-progress` → `agent:review` → chiusa, con
`agent:blocked` come uscita laterale quando serve una decisione umana.
Aree (`area:backend|frontend|harness|docs`) e priorità (`prio:p0|p1|p2`).
Regola completa: [`rules/workflow-issue.md`](rules/workflow-issue.md).

## Plugin abilitati: tre, e perché proprio questi

`.claude/settings.json` ne abilita **tre**. Non è una lista da allungare a
piacere: ogni plugin abilitato porta con sé le sue skill e i suoi comandi, che
entrano nel contesto di **ogni** sessione. Aggiungerne uno è una decisione con
un costo ricorrente, non un default.

| Plugin | Perché è dentro |
| --- | --- |
| `github` | Il flusso issue → branch → PR passa da qui |
| `superpowers` | TDD e scrittura dei piani |
| `mattpocock-skills` | `grilling`: interrogare una richiesta prima di implementarla |

Tutti e tre sono **usati davvero**. Un plugin abilitato e mai usato è costo
ricorrente a fronte di zero beneficio: prima di aggiungerne uno, chiediti quale
lavoro concreto sblocca.

---

## Log contributo AI

| Fase | Contributo AI | Decisione umana |
| --- | --- | --- |
| Scelta modello LLM | Analisi comparativa modelli Ollama | Conferma `qwen2.5:7b` e hardware disponibile |
| Struttura agentica | Proposta agenti + skill + tabella modelli | Conferma profondità (6 agenti + 3 skill) e posizione file |
| Scaffolding backend | `spring-backend-builder` (poi sostituito da `be-orchestrator`) — Spring Boot, 2 endpoint, interfacce | — |
| Scaffolding frontend | `angular-frontend-builder` + skill `accessible-ui-guidelines` | — |
| Logica PDF | `pdf-form-engineer` + skill `pdf-acroform-toolkit` — PDFBox, test verdi | — |
| Integrazione Ollama | `ollama-integration-builder` + skill `ollama-italian-prompting` — client + fallback | — |
| PDF di esempio | `test-pdf-generator` — moduli AcroForm realistici | — |
| Harness (regole, hook, comandi, CI) | Interrogazione della richiesta, ricerca delle versioni su registry primari, stesura | Scelta di ogni bivio: upgrade Angular, ramo Spring, quali plugin abilitare |

| Validazione BE orchestrata | `be-orchestrator` (sonnet) — verifica tutti e 5 i task (scaffold Maven, CORS, domain model, service layer, controller), compilazione `mvn compile`, avvio server Spring Boot 3.5.16 su porta 8080, test endpoint `/api/forms/upload` → 415 corretto. Micro-agente haiku per Task 1 (miglioramento pom.xml + application.yml). Java 25 richiede `-Denforcer.skip=true` (enforcer impone Java 21 LTS). | Conferma go/no-go su ogni PR |

Le **decisioni** (obiettivo, scelta tecnologie, strategia PDF, profondità
agentica, go/no-go di ogni fase) sono state prese dall'umano. L'AI ha svolto
ricerca, proposta e generazione di codice, con verifica oggettiva a ogni passo
(`mvn verify` / `ng build` verdi, hook provati su input reale).
