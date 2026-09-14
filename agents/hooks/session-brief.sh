#!/usr/bin/env bash
# SessionStart — briefing minimo di 5 righe: branch corrente e issue prendibili.
# Volutamente cortissimo: "efficienza dei token" è un criterio di valutazione,
# quindi l'harness non versa contesto inutile a ogni avvio.
set -uo pipefail
cd "${CLAUDE_PROJECT_DIR:-.}" 2>/dev/null || exit 0

branch="$(git rev-parse --abbrev-ref HEAD 2>/dev/null || echo '?')"
echo "Branch: $branch"

command -v gh >/dev/null 2>&1 || exit 0
ready="$(gh issue list --label 'agent:ready' --state open \
          --json number,title --jq '.[] | "  #\(.number) \(.title)"' 2>/dev/null | head -5)"
if [ -n "$ready" ]; then
  echo "Issue prendibili (agent:ready) — usa /issue-take <n>:"
  echo "$ready"
fi

wip="$(gh issue list --label 'agent:in-progress' --state open \
        --json number,title --jq '.[] | "  #\(.number) \(.title)"' 2>/dev/null | head -3)"
[ -n "$wip" ] && { echo "In lavorazione:"; echo "$wip"; }
exit 0
