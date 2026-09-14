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
agent:ready ──/issue-take──▶ agent:in-progress ──/issue-done──▶ agent:review ──merge──▶ chiusa
                                     │
                                     └── manca una decisione ──▶ agent:blocked
```

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
