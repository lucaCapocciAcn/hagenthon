# Ciclo di vita di una issue

Il contratto fra chi apre il lavoro e l'agente che lo esegue. Vale per gli
agenti e per le persone allo stesso modo.

## Gli stati, e cosa significano davvero

| Label | Significato | Chi la mette |
| --- | --- | --- |
| `agent:ready` | Descritta abbastanza da poter partire **senza fare domande**. Un agente può prenderla adesso. | Chi apre la issue |
| `agent:in-progress` | Presa in carico, branch aperto. Nessun altro la tocca. | `/issue-take` |
| `agent:review` | Lavoro finito, build verde, PR aperta. Attende una **persona**. | `/issue-done` |
| `agent:blocked` | Serve una decisione umana o una dipendenza esterna. | Chiunque si blocchi |

Le aree (`area:backend`, `area:frontend`, `area:harness`, `area:docs`) dicono
**quali regole caricare**; le priorità (`prio:p0/p1/p2`) dicono cosa viene prima.

## Il flusso

```
agent:ready ──/issue-take──▶ agent:in-progress ──/issue-done──┐
                                     │                         │
                                     │                   ┌─────▼─────┐
                                     │                   │ build AOT │
                                     │                   │   lint    │
                                     │                   └─────┬─────┘
                                     │                         │
                                     │                ┌────────▼────────┐
                                     │                │  code-reviewer  │
                                     │                └────────┬────────┘
                                     │            APPROVATO    │    MODIFICHE
                                     │                         │    RICHIESTE
                                     │                         │        │
                                     │                         ▼        └──┐
                                     │                   PR → agent:review │
                                     │                         │           │
                                     │                      merge          │
                                     │                         ▼           │
                                     │                      chiusa         │
                                     └── serve una decisione ──▶ agent:blocked ◀┘
```

Il branch si chiama come la issue: `/issue-take` deriva il nome dal titolo
(`fix/issue-7-app-component-spec-ts-e-lo-scaffold-morto`). Chi guarda
`git branch` capisce cosa c'è dentro senza aprire GitHub.

## Il gate di review

**Nessuna PR senza review.** `/issue-done` lancia il subagent `code-reviewer`
sul diff e si ferma se il verdetto non è `APPROVATO`. Non revisioni il tuo
lavoro: chi ha scritto il codice è la persona peggio posizionata per giudicarlo,
e un agente che revisiona sé stesso approva sempre.

Prima della review devono essere verdi, con **exit code vero** (`set -o pipefail`
— `comando | tail` restituisce l'exit code di `tail`):

```bash
cd app/backend  && mvn clean verify
cd app/frontend && npx ng build --configuration production   # build AOT
cd app/frontend && npx ng lint
```

Se non sei d'accordo con un rilievo del revisore, **non ignorarlo**: rispondi
nel corpo della PR spiegando perché. Un gate che si aggira in silenzio non è un gate.

## Si parte sempre da main aggiornato

```bash
git switch main && git pull --ff-only origin main
git switch -c <tipo>/issue-<n>-<slug-dal-titolo>
```

Un branch nato da un `main` vecchio si porta dentro commit estranei e produce
conflitti che non c'entrano col lavoro. Si paga sempre, e si paga in review.

L'hook `guard-branch-base.sh` **blocca** `git switch -c` / `git checkout -b` se
non sei su `main` o se `main` è indietro rispetto a `origin/main`. Se sei
offline non blocca: non potendo verificare, non ti impedisce di lavorare.
Derivare da un altro branch è un'**eccezione da dichiarare**, non la norma.

## Il link della PR si riporta sempre

Quando apri una PR, **scrivine l'URL nella risposta**. L'hook `pr-link.sh` lo
intercetta, lo stampa e lo registra in `.claude/pr-links.log`; `session-brief.sh`
rielenca le PR aperte a ogni avvio di sessione. Tre reti perché una PR aperta e
mai più nominata è lavoro finito che nessuno chiude.

## Le tre regole che non si negoziano

**1. `agent:ready` è una promessa.** Se una issue con questa label ti costringe a
indovinare cosa costruire, la label è sbagliata: spostala su `agent:blocked`,
commenta *cosa* manca, e fermati. Indovinare e sbagliare costa più che chiedere.

**2. Una issue di implementazione copre tutto lo stack.** Backend **e** frontend,
oppure la dichiarazione esplicita del perché una metà non serve. Una issue a metà
stack produce un lavoro a metà stack e un secondo giro di lavoro che nessuno
aveva messo a budget.

**3. Niente rimandi silenziosi.** Se tagli qualcosa, restringi lo scope, o rinvii
un pezzo: **apri una issue** per il pezzo rinviato e linkala. Un rimando tracciato
è lavoro schedulato; un rimando taciuto è lavoro perso.

## Branch e PR

- Nome branch: `<feat|fix|chore|docs|test>/issue-<n>-<slug-breve>`.
- **Mai committare su `main`**: l'hook `agents/hooks/guard-main.sh` lo impedisce,
  e lo impedisce apposta — il contributo degli agenti deve essere leggibile
  in diff separati.
- Il corpo della PR dichiara **dove ha contribuito l'AI e dove è servita
  revisione umana**. Non è burocrazia: è un requisito della traccia.
- `agent:review` non si auto-approva. Il merge lo decide una persona.
