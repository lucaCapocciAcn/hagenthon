#!/usr/bin/env bash
# SessionStart — briefing minimo: branch, issue prendibili, PR aperte.
# Volutamente cortissimo: "efficienza dei token" è un criterio di valutazione,
# quindi l'harness non versa contesto inutile a ogni avvio.
set -uo pipefail
cd "${CLAUDE_PROJECT_DIR:-.}" 2>/dev/null || exit 0

branch="$(git rev-parse --abbrev-ref HEAD 2>/dev/null || echo '?')"
behind="$(git rev-list --count HEAD..origin/main 2>/dev/null || echo 0)"
if [ "$branch" = "main" ] && [ "${behind:-0}" -gt 0 ]; then
  echo "Branch: main — INDIETRO di $behind commit su origin/main. Fai 'git pull --ff-only' prima di ramificare."
else
  echo "Branch: $branch"
fi

command -v gh >/dev/null 2>&1 || exit 0

# Le PR aperte per prime: sono la cosa che si perde più facilmente fra una
# sessione e l'altra.
prs="$(gh pr list --state open --json number,title,url \
        --jq '.[] | "  #\(.number) \(.title)\n      \(.url)"' 2>/dev/null | head -8)"
[ -n "$prs" ] && { echo "PR APERTE — da chiudere o revisionare:"; echo "$prs"; }

ready="$(gh issue list --label 'agent:ready' --state open \
          --json number,title --jq '.[] | "  #\(.number) \(.title)"' 2>/dev/null | head -5)"
[ -n "$ready" ] && { echo "Issue prendibili (agent:ready) — usa /issue-take <n>:"; echo "$ready"; }

wip="$(gh issue list --label 'agent:in-progress' --state open \
        --json number,title --jq '.[] | "  #\(.number) \(.title)"' 2>/dev/null | head -3)"
[ -n "$wip" ] && { echo "In lavorazione:"; echo "$wip"; }
exit 0
