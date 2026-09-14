---
description: Chiudi il lavoro su una issue — verifica, commit, PR, passa a agent:review
argument-hint: <numero-issue>
allowed-tools: Bash(gh:*), Bash(git:*), Bash(mvn:*), Bash(npm:*), Bash(npx:*)
---

Chiudi il lavoro sulla issue **#$1** e aprine la PR.

## Stato

!`git status --short && echo "--- branch ---" && git rev-parse --abbrev-ref HEAD`

## Cosa fare, in quest'ordine

1. **Verde prima della PR.** Esegui solo le verifiche dell'area toccata:
   - backend modificato → `cd app/backend && mvn -q verify`
   - frontend modificato → `cd app/frontend && npx ng build && npx ng lint`
   Se qualcosa è rosso **non aprire la PR**: aggiusta, oppure metti la issue
   `agent:blocked` spiegando cosa si è rotto.
2. **Commit** con messaggio conventional commit che referenzia la issue:
   `<tipo>(<scope>): <cosa cambia>` e nel corpo `Refs #$1`.
   Chiudi il messaggio con: `Co-Authored-By: Claude Opus 5 <noreply@anthropic.com>`
3. **Apri la PR** con `gh pr create`. Il corpo segue
   `.github/PULL_REQUEST_TEMPLATE.md` e deve contenere `Closes #$1`.
4. **Sposta lo stato:**
   `gh issue edit $1 --remove-label agent:in-progress --add-label agent:review`
5. **Riporta all'utente** il link della PR e cosa resta da verificare a mano.

Non fare merge da solo: `agent:review` significa che decide una persona.
