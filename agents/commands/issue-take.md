---
description: Prendi in carico una issue — passa a agent:in-progress, crea il branch, mostra il contesto
argument-hint: <numero-issue>
allowed-tools: Bash(gh issue:*), Bash(git switch:*), Bash(git fetch:*), Bash(git rev-parse:*)
---

Prendi in carico la issue **#$1**.

## Contesto della issue

!`gh issue view $1 --json number,title,body,labels --jq '"#\(.number) \(.title)\n\nLabel: \(.labels|map(.name)|join(", "))\n\n\(.body)"'`

## Stato attuale del repo

!`git rev-parse --abbrev-ref HEAD`

## Cosa fare adesso

1. **Verifica che sia prendibile.** Deve avere `agent:ready`. Se ha già
   `agent:in-progress` qualcuno ci sta lavorando: fermati e dillo.
   Se ha `agent:blocked`, serve una decisione umana: fermati e dillo.
2. **Sposta lo stato:**
   `gh issue edit $1 --remove-label agent:ready --add-label agent:in-progress`
3. **Apri il branch.** Il nome deriva dal tipo di lavoro e dal numero:
   `git switch -c <feat|fix|chore|docs|test>/issue-$1-<slug-breve>`
   (l'hook `guard-main.sh` blocca i commit su `main`, quindi questo passo non è opzionale)
4. **Leggi le regole dell'area toccata** — solo quelle, non tutte:
   label `area:backend` → `agents/rules/spring-boot.md`
   label `area:frontend` → `agents/rules/angular.md` + `agents/rules/accessibilita.md`
   label `area:harness` → `agents/rules/harness.md`
5. **Lavora in TDD** se la issue tocca logica: test rosso, implementazione, test verde.
6. Quando hai finito, chiudi con `/issue-done $1`.

Se la issue è ambigua o troppo grande per un solo branch, **non tirare a indovinare**:
mettila `agent:blocked`, commenta cosa manca, e dillo all'utente.
