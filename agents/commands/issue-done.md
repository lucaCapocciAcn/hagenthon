---
description: Chiudi una issue — build AOT, lint, review obbligatoria di un subagent, poi commit e PR
argument-hint: <numero-issue>
allowed-tools: Bash(gh:*), Bash(git:*), Bash(mvn:*), Bash(npm:*), Bash(npx:*), Bash(tail:*), Bash(head:*), Agent
---

Chiudi il lavoro sulla issue **#$1**. **L'ordine dei passi non è negoziabile:
la PR è l'ultimo, non il primo.**

## Stato

!`git rev-parse --abbrev-ref HEAD && echo "--- modifiche ---" && git status --short && echo "--- diff vs main ---" && git diff main...HEAD --stat | tail -3`

---

### Passo 1 — Verde, per davvero

Esegui le verifiche delle **aree toccate dal diff** qui sopra:

```bash
cd app/backend  && mvn clean verify                          # test + Spotless + Error Prone
cd app/frontend && npx ng build --configuration production   # build AOT di produzione
cd app/frontend && npx ng lint                               # ESLint + regole a11y
```

⚠️ **Leggi l'exit code vero.** `comando | tail` restituisce l'exit code di
`tail`, non del comando: usa `set -o pipefail` oppure `${PIPESTATUS[0]}`.
Dichiarare verde qualcosa che è rosso è il peggior errore possibile qui.

Nota: in Angular l'AOT non è un flag, è il comportamento del builder
`application` — `ng build --configuration production` **è** il build AOT, ed è
lì che emergono gli errori di tipo nei template che `ng serve` non mostra.

Se qualcosa è rosso: **non proseguire**. Aggiusta, oppure metti la issue
`agent:blocked` spiegando cosa si è rotto.

### Passo 2 — Review obbligatoria (subagent `code-reviewer`)

**Prima di aprire la PR**, lancia il subagent **`code-reviewer`** sul diff.
Non revisionare il tuo stesso lavoro: serve un occhio che non ha scritto il codice.

Passagli: il numero della issue (**#$1**), il branch corrente, e l'esito reale
delle verifiche del passo 1.

Il verdetto è l'ultima riga del suo report:

| Verdetto | Cosa fai |
| --- | --- |
| `VERDETTO: APPROVATO` | Vai al passo 3 |
| `VERDETTO: MODIFICHE RICHIESTE` | **Niente PR.** Correggi i rilievi bloccanti, poi rifai passo 1 e passo 2 da capo |
| `VERDETTO: BLOCCATO` | **Niente PR.** `agent:blocked`, commenta sulla issue e fermati |

Se non sei d'accordo con un rilievo, **non ignorarlo**: rispondi nel corpo della
PR spiegando perché, così resta agli atti. Un gate che si aggira in silenzio non
è un gate.

### Passo 3 — Commit

Conventional commit che referenzia la issue:
`<tipo>(<scope>): <cosa cambia>`, nel corpo `Refs #$1`.
Chiudi con: `Co-Authored-By: Claude Opus 5 <noreply@anthropic.com>`

### Passo 4 — PR

`gh pr create`, corpo secondo `.github/PULL_REQUEST_TEMPLATE.md`, con `Closes #$1`.
Nel corpo riporta:
- l'esito **reale** dei comandi del passo 1 (incolla le righe che contano);
- la **sintesi del verdetto** del `code-reviewer` e cosa hai cambiato dopo;
- **dove ha contribuito l'AI e dove è servita revisione umana** (lo chiede la traccia).

### Passo 5 — Riporta il link della PR all'utente

**Obbligatorio.** Scrivi l'URL della PR nella tua risposta, come link cliccabile.
Un link annegato nell'output di `gh pr create` è un link perso: l'hook
`pr-link.sh` lo estrae e te lo mette davanti apposta, e lo registra in
`.claude/pr-links.log`. Se non lo riporti, il lavoro diventa invisibile.

### Passo 6 — Stato

`gh issue edit $1 --remove-label agent:in-progress --add-label agent:review`

**Non fare merge.** `agent:review` significa che decide una persona.
