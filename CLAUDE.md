# Un campo alla volta

Un assistente che permette a una persona in difficoltà con il linguaggio
burocratico di compilare da sola un modulo PDF che oggi non riuscirebbe a
compilare: carica il documento, risponde a **una domanda alla volta** in
linguaggio semplice, scarica il **PDF compilato**.

Non spiega il documento: **porta a termine la pratica**.

Il flusso è intenzionalmente lineare: nessuna registrazione, nessuna
configurazione, nessun account.

---

## Le regole che non si violano

**1. Nessuna modifica al codice senza richiesta esplicita.** Niente
implementazioni decise in autonomia, niente refactor non chiesti, niente file
creati "perché servivano". Si propone, l'utente decide.

**2. Mai committare su `main`.** Ogni lavoro passa da un branch e da una PR, e
il branch nasce da `main` aggiornato. Un branch che parte da `main` vecchio
produce una PR piena di commit che non c'entrano. Gli hook
`guard-main.sh` e `guard-branch-base.sh` lo impongono.

**3. Niente rimandi silenziosi.** Se tagli, rinvii o fai solo metà del lavoro:
dillo sul momento e **apri una issue** per il pezzo rinviato. Un rimando
tracciato è lavoro schedulato; un rimando taciuto è lavoro perso.

**4. Semplificare senza tradire il significato.** La domanda semplice non deve
cambiare l'informazione che l'ente sta chiedendo. Su un documento che ha valore
legale questa non è una questione di stile: una semplificazione che altera il
senso produce una dichiarazione sbagliata, e la responsabilità resta della
persona che l'ha firmata.

**5. L'utente finale non è uno sviluppatore.** Ogni messaggio che arriva a
schermo lo legge una persona che si è bloccata davanti a un modulo. Niente
gergo, niente codici di errore, niente stack trace: quelli vanno nei log.

---

## Quale regola leggere, e quando

**Non leggerle tutte.** Carica solo quella dell'area che stai toccando: ogni
file che apri è contesto che paghi, e un agente sommerso di regole irrilevanti
le applica peggio di uno che ne ha tre pertinenti.

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
  ⚠️ Le checkbox non accettano `setValue("sì")`: vogliono l'export value
  (`getOnValue()`) oppure `"Off"`. I sample ne contengono.
- **LLM** — Ollama locale (`qwen2.5:7b`). Se non raggiungibile, fallback
  all'etichetta originale del campo: **il flusso non si blocca mai.** Ogni
  integrazione esterna che aggiungi segue la stessa regola — timeout esplicito,
  fallback definito, nessuna eccezione che arriva all'utente.

## Comandi

| Comando | Cosa fa |
| --- | --- |
| `/issue-take <n>` | Prende in carico una issue: stato, branch derivato dal titolo, contesto |
| `/issue-done <n>` | Build AOT + lint, review obbligatoria del `code-reviewer`, commit, PR |
| `/harness-check` | Controlla che l'harness sia integro |

```bash
cd app/backend  && mvn clean verify                          # test + Spotless + Error Prone
cd app/frontend && npx ng build --configuration production   # build AOT
cd app/frontend && npx ng lint                               # ESLint + regole di accessibilità
./agents/hooks/test-hooks.sh                                 # 21 casi sugli hook
```

⚠️ Leggi sempre l'exit code **vero**: `comando | tail` restituisce quello di
`tail`, non del comando. Usa `set -o pipefail`, o cattura `$?` senza pipe.

## Fuori scope — decisioni prese, non dimenticanze

PDF scansionati / OCR · risposta vocale · selezione lingua (italiano hardcoded) ·
autenticazione · persistenza oltre la sessione.

Non reintrodurle senza una decisione esplicita. In particolare lo stato in
memoria è una scelta: non introdurre JPA, Redis o un database per aggirare un
problema puntuale — se la mappa cresce senza limite, aprine una issue.

## Documentazione

- [`agents/README.md`](agents/README.md) — architettura agentica, scelte di
  modello, regole, hook, comandi
- [`docs/caso-uso.md`](docs/caso-uso.md) — persona, percorso, limiti
- [`docs/requisiti-consolidati.md`](docs/requisiti-consolidati.md) — requisiti e vincoli
- [`docs/aggiornamenti.md`](docs/aggiornamenti.md) — decisioni prese **dopo** la
  chiusura dei requisiti. Leggilo prima di implementare: i requisiti consolidati
  sono fermi al momento in cui sono stati scritti, questo no.
- [`app/samples/README.md`](app/samples/README.md) — i PDF di esempio
