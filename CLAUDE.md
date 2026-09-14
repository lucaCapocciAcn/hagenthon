# Un campo alla volta

Un assistente che permette a persone con bassa alfabetizzazione digitale di
compilare moduli PDF burocratici attraverso una conversazione guidata: una
domanda alla volta in linguaggio semplice, poi il PDF compilato in download.

Il flusso è intenzionalmente lineare: nessuna registrazione, nessuna
configurazione, nessun account.

---

## Le tre regole che non si violano

**1. Nessuna modifica al codice senza richiesta esplicita.** Niente
implementazioni decise in autonomia, niente refactor non chiesti, niente file
creati "perché servivano". Si propone, l'utente decide.

**2. Mai committare su `main`.** Ogni lavoro passa da un branch e da una PR.
L'hook `agents/hooks/guard-main.sh` lo impedisce, e lo fa apposta.

**3. Niente rimandi silenziosi.** Se tagli, rinvii o fai solo metà del lavoro:
dillo sul momento e **apri una issue** per il pezzo rinviato. Un rimando
tracciato è lavoro schedulato; un rimando taciuto è lavoro perso.

---

## Quale regola leggere, e quando

**Non leggerle tutte.** Carica solo quella dell'area che stai toccando: il
contesto che consumi è un costo, e su questo progetto è un criterio di
valutazione dichiarato.

| Se stai toccando… | Leggi prima |
| --- | --- |
| `app/backend/**` (Java, Spring, PDFBox) | [`agents/rules/spring-boot.md`](agents/rules/spring-boot.md) |
| `app/frontend/**` (Angular, UI) | [`agents/rules/angular.md`](agents/rules/angular.md) |
| Qualsiasi UI visibile all'utente finale | anche la skill **`accessible-ui-guidelines`** |
| Logica PDF / AcroForm | anche la skill **`pdf-acroform-toolkit`** |
| Prompt verso il modello locale | anche la skill **`ollama-italian-prompting`** |
| Issue, branch, PR, stati | [`agents/rules/workflow-issue.md`](agents/rules/workflow-issue.md) |
| `agents/**` o `.claude/**` (l'harness stesso) | [`agents/rules/harness.md`](agents/rules/harness.md) |

---

## Architettura

- **Backend** — Spring Boot 3.5.x, Java 21, Maven. Package
  `controller` / `service` (+`impl`) / `dto` / `model` / `config`.
  2 endpoint: `POST /api/forms/upload`, `POST /api/forms/{sessionId}/answers`.
  Stato sessione in `ConcurrentHashMap` (singleton, no DB).
- **Frontend** — Angular 21, standalone components, signals, `HttpClient`.
  2 schermate: upload → chat guidata, una domanda per schermata.
- **PDF** — Apache PDFBox 3.x: lettura campi AcroForm, compilazione, flatten.
- **LLM** — Ollama locale (`qwen2.5:7b`). Se non raggiungibile, fallback
  all'etichetta originale del campo: **il flusso non si blocca mai.**

## Comandi

| Comando | Cosa fa |
| --- | --- |
| `/issue-take <n>` | Prende in carico una issue: stato, **branch derivato dal titolo**, contesto |
| `/issue-done <n>` | Build AOT + lint, **review obbligatoria del `code-reviewer`**, commit, PR, `agent:review` |
| `/harness-check` | Controlla che l'harness sia integro prima di consegnare |

Verifiche: `mvn clean verify` (backend) · `npx ng build --configuration production` (build AOT) ·
`npx ng lint` (frontend). Leggi sempre l'exit code **vero**: `comando | tail`
restituisce quello di `tail`, non del comando.

## Fuori scope — decisioni prese, non dimenticanze

PDF scansionati / OCR · risposta vocale · selezione lingua (italiano hardcoded) ·
autenticazione · persistenza oltre la sessione.

Non reintrodurle senza una decisione esplicita.

## Dove leggere prima di implementare

- [`agents/README.md`](agents/README.md) — struttura agentica, scelte di modello,
  strumenti abilitati e perché, log contributo AI
- [`app/samples/README.md`](app/samples/README.md) — i PDF di esempio
