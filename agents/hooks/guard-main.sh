#!/usr/bin/env bash
# PreToolUse(Bash) — impedisce commit e push diretti sul branch di default.
# Ogni lavoro passa da un branch e da una PR: è ciò che rende tracciabile
# il contributo degli agenti.
set -uo pipefail
. "$(dirname "$0")/_json.sh"

raw="$(cat)"
cmd="$(json_field '.tool_input.command' "$raw")"
[ -z "$cmd" ] && exit 0

case "$cmd" in
  *"git commit"*|*"git push"*) ;;
  *) exit 0 ;;
esac

branch="$(git -C "${CLAUDE_PROJECT_DIR:-.}" rev-parse --abbrev-ref HEAD 2>/dev/null)"
[ "$branch" != "main" ] && exit 0

cat >&2 <<MSG
BLOCCATO: sei su 'main'. Su questo repo main è protetto dall'harness.

Apri un branch prima di committare:
  git switch -c <tipo>/<slug>      # feat|fix|chore|docs|test
  # tipo/slug coerente con la issue che stai chiudendo, es. feat/issue-12-upload

Poi committa e apri la PR con:  /issue-done
Regola completa: agents/rules/workflow-issue.md
MSG
exit 2
