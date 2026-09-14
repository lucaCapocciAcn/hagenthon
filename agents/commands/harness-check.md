---
description: Verifica che l'harness sia integro — hook eseguibili, symlink, label, regole referenziate
allowed-tools: Bash(*)
---

Verifica l'integrità dell'harness di questo repo e riporta in una tabella
**cosa è verde e cosa è rotto**. Non aggiustare nulla senza chiedermelo.

!`cd "$CLAUDE_PROJECT_DIR" && echo "--- symlink .claude ---" && ls -l .claude && echo "--- hook (devono essere eseguibili) ---" && ls -l agents/hooks && echo "--- regole ---" && ls agents/rules && echo "--- command ---" && ls agents/commands && echo "--- subagent ---" && ls agents/subagents && echo "--- label ---" && gh label list --limit 40 --json name --jq '.[].name' | tr '\n' ' '`

Controlla in particolare:

1. **Regole orfane.** Ogni file in `agents/rules/` è referenziato da `CLAUDE.md`
   o da un subagent? Una regola che nessuno carica è peso morto e costa
   manutenzione senza dare niente: segnalala.
2. **Hook vivi.** Ogni hook dichiarato in `.claude/settings.json` esiste ed è
   eseguibile? Provali a mano passando un JSON di esempio su stdin.
3. **Subagent coerenti.** Ogni file in `agents/subagents/` punta a skill e regole
   che esistono davvero sul filesystem?
4. **Label complete.** Esistono tutte e quattro le label di stato
   (`agent:ready`, `agent:in-progress`, `agent:review`, `agent:blocked`)?
