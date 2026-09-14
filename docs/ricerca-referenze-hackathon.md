# Referenze all'evento nel repo — inventario e piano di bonifica

Ricerca di sola lettura. Obiettivo: individuare ogni punto in cui una regola,
un'istruzione o una documentazione si giustifica con l'hackathon («la giuria lo
valuta», «lo chiede la traccia», «anche sulla macchina di chi valuta») invece
che con il proprio merito tecnico.

Principio guida: **una regola motivata con «la giuria lo misura» smette di
valere il giorno dopo; la stessa regola motivata con «costa contesto a ogni
sessione» è vera per sempre.** Nessuna regola va cancellata — va cambiata la
motivazione.

## Comando dello sweep (ripetibile)

```bash
git grep -n -I -E -i \
  'hackathon|hagenthon|giuri|valutator|valutazion|chi valuta|commission|traccia|tema.?0|deliverable|criteri|premi[oa]|accenture|presentazion|slide|demo finale|organizzator|coppia|team da 2|5 ore|consegn|efficienza dei token|token' \
  <branch> -- . \
  ':!*package-lock.json' ':!*/node_modules/*' ':!*/target/*' ':!*/dist/*' ':!*.angular/*'
```

Eseguito su `origin/main`, su `origin/chore/harness-e-qualita` e (controllo
incrociato) su `HEAD` = `fix/regressione-checkbox-e-reset-input`.

**Nota sul terzo branch.** `HEAD` è un discendente di `chore/harness-e-qualita`:
produce esattamente gli stessi hit della sezione «chore», con l'unica aggiunta
di `app/backend/.../PdfBoxFormService.java:58` (falso positivo, vedi in fondo).
Chi bonifica `chore/harness-e-qualita` bonifica anche questo branch.

---

## Branch `origin/chore/harness-e-qualita`

È il branch con l'harness maturo, ed è quello dove le referenze all'evento sono
**dentro le istruzioni operative** — cioè il caso peggiore. Priorità massima.

### DA RIMUOVERE — giustificazione basata sull'evento dentro un'istruzione

La regola resta identica, sparisce solo la mezza frase che la lega all'evento.

| File:riga | Testo | Azione |
| --- | --- | --- |
| `agents/commands/issue-done.md:69` | «**dove ha contribuito l'AI e dove è servita revisione umana** (lo chiede la traccia).» | Togliere la parentesi. L'istruzione regge da sola. |
| `agents/rules/workflow-issue.md:110` | «Il corpo della PR dichiara dove ha contribuito l'AI… Non è burocrazia: **è un requisito della traccia**.» | Sostituire la seconda frase: «Non è burocrazia: chi rivede il diff deve sapere quale parte nessun umano ha letto.» |
| `.github/PULL_REQUEST_TEMPLATE.md:36` | `<!-- Obbligatorio: la traccia dell'hackathon richiede di dichiarare dove ha contribuito l'AI. -->` | `<!-- Obbligatorio: distingue ciò che è stato generato da ciò che è stato letto da una persona. -->` |
| `.github/ISSUE_TEMPLATE/harness.yml:70` | `description: L'efficienza dei token è un criterio di valutazione del progetto.` | `description: Ogni risorsa dell'harness è contesto caricato a ogni sessione.` |
| `agents/hooks/session-brief.sh:3` | `# Volutamente cortissimo: "efficienza dei token" è un criterio di valutazione,`<br>`# quindi l'harness non versa contesto inutile a ogni avvio.` | `# Volutamente cortissimo: gira a ogni avvio di sessione, e tutto ciò che`<br>`# stampa entra nel contesto prima ancora che l'utente scriva qualcosa.` |
| `agents/hooks/_lib.sh:2` | `# gli hook girano anche sulla macchina di chi valuta.` | `# gli hook girano su qualsiasi macchina che apra il repo, anche senza`<br>`# l'ambiente di sviluppo completo installato.` |
| `agents/README.md:119-122` | «Sono scritti per girare **anche sulla macchina di chi valuta**: escono `0` quando non hanno niente da fare e non assumono che `jq`, `mvn` o `prettier` esistano.» | «Sono scritti per girare su **qualsiasi macchina che cloni il repo**: escono `0` quando non hanno niente da fare e non assumono che `jq`, `mvn` o `prettier` esistano.» |
| `agents/rules/harness.md:40` | «Un hook è codice che gira **sempre**, anche sulla macchina di chi valuta.» | «Un hook è codice che gira **sempre**, su ogni macchina che apre il repo — inclusa quella di chi non ha ancora installato niente.» |
| `agents/README.md:9-10` | «Un solo posto da leggere **per chi valuta**, e gli strumenti trovano comunque tutto dove se lo aspettano.» | «Un solo posto da leggere **per chi arriva sul repo**, e gli strumenti trovano comunque tutto dove se lo aspettano.» |
| `agents/rules/harness.md:9-10` | «Un solo posto da leggere **per chi valuta il repo**, e Claude Code trova comunque tutto dove si aspetta.» | Stessa sostituzione: «per chi legge il repo». |
| `.gitignore:27-28` | `# Sono deliverable richiesti dalla traccia e il repo viene letto da un agente`<br>`# valutatore: ciò che non è committato, per lui non esiste.` | `# Sono documentazione di progetto: ciò che non è committato non esiste`<br>`# per chi clona il repo.` (Aggiornare anche il riferimento a `app/presentation/`, cartella che su questo branch non esiste più.) |

### DA RIFORMULARE — affermazione tecnicamente valida, espressa in termini dell'evento

| File:riga | Testo | Riformulazione proposta |
| --- | --- | --- |
| **`CLAUDE.md:29-31`** | «**Non leggerle tutte.** Carica solo quella dell'area che stai toccando: il contesto che consumi è un costo, **e su questo progetto è un criterio di valutazione dichiarato**.» | «**Non leggerle tutte.** Carica solo quella dell'area che stai toccando: ogni file caricato è contesto che paghi a ogni sessione, e che toglie spazio al lavoro vero.» |
| `CLAUDE.md:63` | \| `/harness-check` \| Controlla che l'harness sia integro **prima di consegnare** \| | \| `/harness-check` \| Controlla che l'harness sia integro: symlink, hook eseguibili, regole orfane \| |
| `agents/rules/harness.md:55` | `## Prima di consegnare` | `## Prima di aprire una PR che tocca l'harness` |
| `agents/README.md:28-32` | «## Il vincolo che ha guidato ogni scelta: il costo in token — **L'efficienza dei token è un criterio di valutazione dichiarato.** Di conseguenza l'harness è progettato per caricare poco e su richiesta, non per sembrare grande» | «## Il vincolo che ha guidato ogni scelta: il costo in token — **Ogni file che l'harness carica è contesto che paghi a ogni sessione, su ogni richiesta.** Per questo è progettato per caricare poco e su richiesta, non per sembrare grande» |
| `agents/README.md:159-161` | «Ogni plugin abilitato è contesto caricato a **ogni** sessione: abilitarli tutti sarebbe costato token **su un criterio che viene misurato**.» | «Ogni plugin abilitato è contesto caricato a **ogni** sessione, anche in quelle in cui non serve: abilitarli tutti significa pagarli tutti, sempre.» |
| `README.md:83` | \| `docs/` \| **Deliverable della traccia** \| | \| `docs/` \| Documentazione di progetto: persona, percorso d'uso, limiti dichiarati \| |
| `README.md:87-88` | «[`docs/deliverable-tema-01.md`] — **Persona & Barriera · Percorso Assistito · Autonomia & Limiti**, **i tre deliverable del tema**, più il contributo AI» | «[`docs/contesto-utente.md`] — chi è l'utente, dove si blocca, cosa il sistema fa e cosa non fa» (vedi sezione sotto per il rename). |
| `.github/ISSUE_TEMPLATE/harness.yml:67-74` | campo `token-impact` con le tre opzioni | Le opzioni vanno bene così: descrivono un trade-off reale. Cambia solo la `description` (riga 70, sopra). |

### DA SPOSTARE / LEGITTIMA

| File:riga | Cosa è | Dove collocarlo |
| --- | --- | --- |
| `docs/deliverable-tema-01.md` (intero) | Contenuto di merito: persona, barriera, percorso prima/dopo, limiti dichiarati, log contributo AI. È buona documentazione di prodotto travestita da deliverable. | **Rinominare** in `docs/contesto-utente.md`. Togliere dal titolo (riga 1) «Deliverable Tema 01 · Accessibilità Digitale», togliere il riferimento alla «commissione» (riga 17) e al «punto della traccia» (riga 42). Il contenuto resta. |
| `docs/deliverable-tema-01.md:15-19` | Blocco `⚠️ DA CONFERMARE — scegliere UNA delle due persone prima della demo`, con «la commissione vieta esplicitamente i profili generici» | **Pericoloso** — vedi sezione «Affermazioni che diventano false». Va risolto (scegliere una persona) o convertito in issue, non semplicemente riscritto. |
| `agents/README.md:174-189` | «Log contributo AI» — tabella fase/contributo AI/decisione umana | **Legittimo e da tenere dov'è.** È tracciabilità di provenienza del codice, utile a prescindere dall'evento. Va corretta solo la riga 185 («3 plugin su 21», vedi sotto). |
| `agents/README.md:68`, `agents/subagents/test-pdf-generator.md:3`, `agents/skills/ollama-italian-prompting/SKILL.md:12` | «costo token minimo», «per efficienza token», «(breve → efficienza token)» | **Già motivate sul merito tecnico.** Nessuna azione. |
| `app/backend/pom.xml:12` (`<groupId>com.hagenthon</groupId>`) e il package `com.hagenthon.uncampoallavolta` in **16 file Java** | Il nome dell'evento è cablato nel namespace del codice. | Decisione a parte, non parte della bonifica testuale: è un rename invasivo (16 file + pom + eventuali import). **Aprire una issue dedicata**, non farlo insieme al resto. |

---

## Branch `origin/main`

Su questo branch il problema è più profondo: `CLAUDE.md` **è** il documento
dell'evento, non un documento di progetto con qualche riferimento di troppo.
Delle 88 righe, circa 45 descrivono regole d'ingaggio dell'hackathon.

### DA RIMUOVERE — giustificazione basata sull'evento dentro un'istruzione

| File:riga | Testo | Azione |
| --- | --- | --- |
| **`CLAUDE.md:1`** | `# Hagenthon — Accessibilità Digitale` | `# Un campo alla volta` |
| **`CLAUDE.md:14`** | «Hackathon interno Accenture, 14 settembre 2026, team da 2 persone, 5 ore.» | Riga intera da eliminare. |
| **`CLAUDE.md:16-17`** | «Il problema specifico dentro il tema **non è ancora stato definito**: è la prima cosa da decidere, e **la traccia lo chiede esplicitamente**.» | Eliminare: è anche **falsa** oggi (vedi sotto). Sostituire con la descrizione del prodotto già presente sul branch `chore`. |
| **`CLAUDE.md:19-23`** | `## PERIMETRO / ### Dentro / - Tema **01 — Accessibilità Digitale**, e solo quello.` | Togliere il riferimento al tema; il resto dell'elenco («una persona concreta e una barriera precisa», «servizio reale», «usabile dalla persona stessa») è merito di prodotto e resta. |
| **`CLAUDE.md:33-34`** | «I temi **02 (inclusione finanziaria)** e **03 (educazione digitale inclusiva)**: scartati, con i loro vincoli e deliverable.» | Eliminare: fuori scope di temi che non esistono più. |
| **`CLAUDE.md:35-38`** | «**Audit di conformità e checker automatici.** **La traccia è esplicita**: *«il focus non è l'audit tecnico… è la persona»*. A voce era stata nominata la legge Stanca — il documento scritto prevale.» | La regola resta, la motivazione cambia: «**Audit di conformità e checker automatici.** Fuori scope: il prodotto serve la persona che deve compilare il modulo, non lo sviluppatore che deve certificarlo.» |
| **`CLAUDE.md:45-63`** | Intera sezione `## Consegne e scadenze` (tabella `13:00 pranzo` / `~16:00 fine evento`, «push su repository GitHub, tre parti», «PPT su template brandizzato Accenture», «i **valutatori** lo scaricano e lo eseguono», «i **tre deliverable specifici del tema 01**») | **Sezione intera da eliminare.** L'unico contenuto che sopravvive è «un README con le istruzioni per eseguire il software», che va riscritto come requisito permanente. |
| **`CLAUDE.md:71-75`** | «Va dichiarato **dove ha contribuito l'AI**…», «**Si valuta anche** la qualità delle risorse agentiche e l'**efficienza dei token**…», «La **capacità espositiva pesa quanto quella tecnica**.» | Prime due: tenere la regola, cambiare la motivazione (le formulazioni del branch `chore` vanno bene). Terza: eliminare, non ha significato fuori dall'evento. |
| **`CLAUDE.md:79-88`** | Rimandi a `docs/requisiti-consolidati.md` («criteri di valutazione, vincoli della traccia») e istruzione «prima di implementare qualcosa, **leggi `docs/aggiornamenti.md`**, dove il listener annota ciò che è stato detto dopo» | Eliminare entrambi: sono istruzioni operative che puntano al registro dell'evento. |
| `README.md:3-4` | «Hackathon Agentic Coding · Accenture Application Engineering · 14 settembre 2026 / Tema 01 — **Accessibilità Digitale**» | Eliminare le due righe di intestazione. |
| `README.md:78` | «[`docs/requisiti-consolidati.md`] — **regole dell'evento, criteri di valutazione, vincoli della traccia**» | Eliminare il link (il file va archiviato, vedi sotto). |
| `README.md:81-83` | `## Team` → «Hackathon a coppie — 5 ore di sviluppo.» | Sezione intera da eliminare. |
| `agents/subagents/orchestrator.md:35` | «5. `presentation-builder` → PPT + 3 deliverable tema 01» | Eliminare il passo: l'agente `presentation-builder` **non esiste nel repo** (vedi sotto). |
| `app/samples/README.md:1-3` | «# Sample PDF Forms **for Hagenthon Demo** … for testing the `pdf-form-engineer` **demo**.» | Già corretto su `chore` in «# Sample PDF Forms … for testing the PDF extraction and compilation pipeline». Allineare. |
| `.wayfinder/T004-ux-prototipo.md:16` | «Pulsante "Avanti" grande, colore contrastato (**#0056a6 Accenture** o simile)» | «Pulsante "Avanti" grande, colore con contrasto ≥ 4.5:1 sul fondo.» |

### DA RIFORMULARE

| File:riga | Testo | Riformulazione proposta |
| --- | --- | --- |
| `agents/README.md:42` | «`test-pdf-generator` … **haiku** … Task ripetitivo e template-driven → costo token minimo» | Già a merito tecnico. Nessuna azione. |
| `agents/README.md:71-74` | «Le **decisioni** … sono state prese dall'umano. L'AI ha svolto ricerca, proposta e generazione di codice, con verifica oggettiva a ogni passo» | Legittima e da tenere. Nessun riferimento all'evento. |
| `.wayfinder/T002-modello-ollama-italiano.md:19` | «**Criteri di valutazione**: qualità italiano · comprensione burocratico → semplice ·» | Sono criteri di scelta di un modello, non di gara: rinominare in «Criteri di scelta». (Se il file viene archiviato, non serve.) |
| `.wayfinder/T003-struttura-agenti.md:9` | «robustezza istruzioni · **efficienza token** · adeguatezza strumenti · documentazione.» | Elenco di criteri di gara. Da archiviare col resto di `.wayfinder/`. |
| `.wayfinder/T003-struttura-agenti.md:39` | «log "dove ha contribuito l'AI…" (**vincolo traccia**)» | Togliere la parentesi se il file sopravvive. |
| `.wayfinder/map.md:11,16,23,32,44,53` | «presentazione PPT Accenture», «**Criteri valutazione pesanti**: profondità agentica · qualità istruzioni · efficienza token · qualità idea», «il deliverable centrale **e giudicato**», «BUILD-5: `presentation-builder` genera il PPT + i 3 deliverable tema 01» | Il file è interamente un piano d'evento. Archiviare, non riformulare. |

### DA SPOSTARE / LEGITTIMA

| File | Cosa è | Dove collocarlo |
| --- | --- | --- |
| `docs/requisiti-consolidati.md` | Il regolamento dell'evento trascritto: consegne, orari, premi («500 € alla prima coppia, 300 € alla seconda»), «Chi valuta», gli «otto criteri dichiarati a schermo», le domande aperte agli organizzatori. Non contiene requisiti di prodotto. | **Archivio storico.** `docs/archivio-hackathon/requisiti-consolidati.md`, con una riga in testa: «Documento storico dell'hackathon del 14 settembre 2026. Non è una fonte di requisiti per il progetto.» Rimuovere ogni link da `CLAUDE.md` e `README.md`. |
| `docs/aggiornamenti.md` | Log a timestamp di ciò che è stato detto in sala («10:36 — il repository va pubblico, così gli organizzatori possono…»). | Stesso archivio. **Deve smettere di essere linkato da `CLAUDE.md` come lettura obbligatoria prima di implementare.** |
| `docs/caso-uso.md` | Misto: contiene il caso d'uso reale (valido) e commenti sul deliverable («Senza questo il deliverable 01 non esiste», «il flusso regge in 5 ore»). | Il contenuto di merito è già confluito in `docs/deliverable-tema-01.md` sul branch `chore`. Qui: archiviare, oppure ripulire i riferimenti e fondere. |
| `.wayfinder/T001…T005`, `.wayfinder/map.md` | Diario decisionale dell'evento. Sul branch `chore` è **già in `.gitignore`** e rimosso dal tracking. | Coerente: lasciarlo fuori dal repo, o archiviarlo integralmente in `docs/archivio-hackathon/wayfinder/`. La scelta del branch `chore` è quella giusta. |
| `app/presentation/**` (5 file: `slides.md`, `README.md`, `01-` `02-` `03-`) | Deliverable veri: tre documenti di prodotto + la struttura delle 10 slide con speaker notes. Ogni file ha in testa «Hackathon Accenture · 14 settembre 2026 · Tema 01». Sul branch `chore` **sono stati eliminati** e il contenuto condensato in `docs/deliverable-tema-01.md`. | Se si vuole conservare la versione estesa: `docs/archivio-hackathon/presentazione/`. Non va lasciata in `app/`, dove suggerisce che sia un artefatto del software. |

---

## Affermazioni numeriche e affermazioni che diventano false

Sono i casi più pericolosi: tolto il contesto dell'evento, non sono ambigue —
sono sbagliate.

| Branch · File:riga | Testo | Perché è un problema |
| --- | --- | --- |
| `chore` · `agents/README.md:36`, `:157`, `:185` e `agents/rules/harness.md:23` | «**3 plugin abilitati su 21 disponibili**», «## Plugin abilitati: 3 su 21 — e perché proprio questi», «I plugin abilitati sono 3, scelti fra i 21 disponibili» | «21» è il numero di plugin installati **nell'ambiente di un singolo sviluppatore in un dato momento**, non una proprietà del repo. È già falso per chiunque altro cloni. Il fatto verificabile è solo il numeratore: `.claude/settings.json` abilita `github`, `superpowers`, `mattpocock-skills`. Riformulare come «**Tre plugin abilitati a livello di progetto, e perché proprio questi**» ed eliminare il denominatore in tutte e quattro le occorrenze. |
| `main` · `.wayfinder/T003-struttura-agenti.md:41` | «Copre **6 criteri su 8** in modo diretto.» | Gli «8 criteri» erano la griglia di valutazione dell'evento. Fuori da quel contesto la frase non è interpretabile. |
| `main` · `CLAUDE.md:16-17` | «Il problema specifico dentro il tema **non è ancora stato definito**: è la prima cosa da decidere» | **Oggi è falsa.** Il problema è definito e il software è costruito. Un agente che legge questo `CLAUDE.md` come istruzione crede che il progetto sia a foglio bianco. |
| `main` · `CLAUDE.md:47-51` | Tabella `13:00 pausa pranzo` / `~16:00 fine evento` / `durante debriefing intermedi` | Scadenze passate presentate come vincoli attivi. |
| `main` · `CLAUDE.md:83-88` | «**prima di implementare qualcosa, leggi `docs/aggiornamenti.md`**, dove il listener annota ciò che è stato detto dopo» | Istruzione operativa **bloccante** che punta a un log d'evento chiuso — e che sul branch `chore` non esiste nemmeno più. È il rimando più dannoso del repo. |
| `main` · `agents/subagents/orchestrator.md:35` e `.wayfinder/map.md:44` | «`presentation-builder` → PPT + 3 deliverable tema 01» | L'agente `presentation-builder` **non esiste** in `agents/subagents/` su nessuno dei due branch. L'orchestratore ha in piano una delega verso un agente inesistente. |
| `chore` · `docs/deliverable-tema-01.md:15-19` | «⚠️ **DA CONFERMARE — scegliere UNA delle due persone prima della demo** … la **commissione** vieta esplicitamente i profili generici» | TODO bloccante con una scadenza («prima della demo») che è passata. Restano due persone alternative mai riconciliate dentro un documento presentato come definitivo. Va deciso, non riscritto. |
| `chore` · `agents/README.md:125` e `agents/rules/harness.md:49` | «`./agents/hooks/test-hooks.sh` — **21 casi**, gira anche in CI» | Non è legato all'evento, ma è **numericamente sbagliato**: lo script contiene 18 invocazioni di `check` (di cui 2 condizionali). Da correggere o da rendere generico («la suite copre blocchi veri, falsi positivi, timeout e orfani»). |

### Falsi positivi dello sweep — nessuna azione

- `chore` · `README.md:73`, `CLAUDE.md:23`, `agents/rules/workflow-issue.md:100`,
  `agents/subagents/orchestrator.md:31`, `agents/hooks/guard-main.sh:3` —
  «tracciato / tracciabile» nel senso di *tracking del lavoro*, non di *traccia dell'evento*.
- `chore` · `.github/ISSUE_TEMPLATE/feature.yml:67` — «Criteri di accettazione»: uso standard.
- `chore` · `agents/subagents/orchestrator.md:50` — «criterio di "fatto"»: definition of done.
- `HEAD` · `app/backend/.../PdfBoxFormService.java:58` — «adatto alla consegna finale»:
  si riferisce alla consegna del **documento PDF** all'ufficio, non all'evento.
- `chore` · `.github/workflows/ci.yml` — **nessun riferimento all'evento**. I commenti
  del file (concurrency, AOT, `npm test` assente per la issue #7, hook non coperti da
  compilatore) sono tutti motivati tecnicamente. Non va toccato.

---

## Ordine di intervento consigliato

1. **`chore/harness-e-qualita` · `CLAUDE.md:29-31`** — una riga, il file che il
   manutentore ha nominato, ed è l'esempio canonico del problema. Cambio di
   motivazione, non di regola.
2. **`chore` · le 5 occorrenze «chi valuta» negli hook e nelle regole**
   (`agents/hooks/_lib.sh:2`, `agents/README.md:9`, `:119`,
   `agents/rules/harness.md:10`, `:40`) — sostituzione meccanica, zero rischio,
   e libera l'affermazione tecnica più forte del repo (portabilità degli hook)
   da un aggancio che la indebolisce.
3. **`chore` · `agents/hooks/session-brief.sh:3` + `agents/README.md:28-32` +
   `:159-161` + `.github/ISSUE_TEMPLATE/harness.yml:70`** — tutte le
   giustificazioni «efficienza token = criterio di gara» diventano «contesto
   pagato a ogni sessione». È una sola idea ripetuta in quattro punti:
   riformularla una volta e propagarla.
4. **`chore` · «3 plugin su 21» in 4 punti** — è l'affermazione già falsa oggi.
   Togliere il denominatore.
5. **`chore` · `docs/deliverable-tema-01.md`** — rinominare in
   `docs/contesto-utente.md`, ripulire titolo/«commissione»/«traccia», e
   **decidere la persona** (Rosa Fabbri o l'alternativa) o aprire una issue.
   Aggiornare i link in `README.md:83,87-88`.
6. **`chore` · `.gitignore:27-28`, `PULL_REQUEST_TEMPLATE.md:36`,
   `issue-done.md:69`, `workflow-issue.md:110`, `CLAUDE.md:63`,
   `harness.md:55`** — coda delle referenze minori nelle istruzioni operative.
7. **`main` · `CLAUDE.md`** — riscrittura vera, non bonifica: il file è il
   documento dell'evento. Il modo più rapido e meno rischioso è **adottare la
   versione di `chore/harness-e-qualita` già bonificata** invece di editare
   quella di `main` riga per riga.
8. **`main` · `README.md` (3-4, 78, 81-83) e `app/samples/README.md:1`** —
   intestazioni e link. Anche qui la versione `chore` è già pulita.
9. **`main` · archiviazione** — `docs/requisiti-consolidati.md`,
   `docs/aggiornamenti.md`, `docs/caso-uso.md`, `.wayfinder/**`,
   `app/presentation/**` in `docs/archivio-hackathon/`, con nota di intestazione
   «documento storico, non è una fonte di requisiti». Nessun link da `CLAUDE.md`.
10. **`main` · `agents/subagents/orchestrator.md:35`** — togliere il passo verso
    `presentation-builder`, agente che non esiste.
11. **Separata, non in questo giro** — issue dedicata per il rename del package
    `com.hagenthon` → nome neutro (`pom.xml` + 16 file Java). Invasivo, tocca
    codice compilato, merita un branch e una verifica `mvn verify` a sé.
12. **Separata** — correggere «21 casi» in `agents/README.md:125` e
    `agents/rules/harness.md:49` (il valore reale è 18), oppure sostituire il
    numero con una descrizione di cosa la suite copre.
