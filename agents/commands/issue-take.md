---
description: Prendi in carico una issue — stato a agent:in-progress, branch derivato dal titolo, contesto
argument-hint: <numero-issue>
allowed-tools: Bash(gh issue:*), Bash(git switch:*), Bash(git fetch:*), Bash(git rev-parse:*), Bash(git status:*)
---

Prendi in carico la issue **#$1**.

## La issue

!`gh issue view $1 --json number,title,body,labels --jq '"#\(.number) \(.title)\n\nLabel: \(.labels|map(.name)|join(", "))\n\n\(.body)"'`

## Branch da usare (derivato dal titolo, già calcolato)

!`gh issue view $1 --json title,labels --jq '[(.labels|map(.name)|join(" ")), .title] | @tsv' | awk -F'	' '{lab=$1; t=tolower($2); if (lab ~ /area:harness/) p="chore"; else if (lab ~ /area:docs/) p="docs"; else if (t ~ /bug|rott|fallisc|errore|ross|mort|guast|corregg/) p="fix"; else p="feat"; gsub(/[àáâã]/,"a",t); gsub(/[èéêë]/,"e",t); gsub(/[ìíîï]/,"i",t); gsub(/[òóôõ]/,"o",t); gsub(/[ùúûü]/,"u",t); gsub(/[^a-z0-9]+/,"-",t); gsub(/^-+|-+$/,"",t); if (length(t)>42) { t=substr(t,1,42); sub(/-[^-]*$/,"",t) } print p "/issue-'"$1"'-" t}'`

## Stato del repo

!`git rev-parse --abbrev-ref HEAD && git status --short | head -5`

## Cosa fare adesso

1. **Verifica che sia prendibile.** Deve avere `agent:ready`.
   Se ha `agent:in-progress`, qualcuno ci sta già lavorando: **fermati e dillo**.
   Se ha `agent:blocked`, serve una decisione umana: **fermati e dillo**.
   Se il working tree qui sopra non è pulito, fermati: non si parte su lavoro altrui.
2. **Sposta lo stato:**
   `gh issue edit $1 --remove-label agent:ready --add-label agent:in-progress`
3. **Parti da `main` aggiornato.** Non è una formalità: un branch nato da main
   vecchio produce una PR piena di commit che non c'entrano.
   ```bash
   git switch main && git pull --ff-only origin main
   ```
   L'hook `guard-branch-base.sh` **blocca** la creazione del branch se non sei
   su `main` o se `main` è indietro rispetto a `origin/main`.
4. **Crea il branch con esattamente il nome calcolato qui sopra:**
   `git switch -c <nome-calcolato>`
   Il nome deriva dal titolo della issue apposta: chi guarda `git branch` deve
   capire cosa c'è dentro senza aprire GitHub. Non inventarne un altro.
   (L'hook `guard-main.sh` blocca i commit su `main`: questo passo non è opzionale.)
5. **Leggi solo le regole dell'area toccata**, non tutte — il contesto è un costo:
   `area:backend` → `agents/rules/spring-boot.md`
   `area:frontend` → `agents/rules/angular.md` + skill `accessible-ui-guidelines`
   `area:harness` → `agents/rules/harness.md`
6. **TDD** se la issue tocca logica: test rosso, implementazione, test verde.
7. A lavoro finito: **`/issue-done $1`**.

Se la issue è ambigua o troppo grande per un solo branch, **non tirare a
indovinare**: rimettila `agent:blocked`, commenta cosa manca, e dillo all'utente.
