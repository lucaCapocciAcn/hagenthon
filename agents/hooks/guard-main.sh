#!/usr/bin/env bash
# PreToolUse(Bash) — impedisce commit e push diretti sul branch di default.
# Ogni lavoro passa da un branch e da una PR: è ciò che rende tracciabile
# il contributo degli agenti.
set -uo pipefail
. "$(dirname "$0")/_lib.sh"

raw="$(cat)"
cmd="$(json_field '.tool_input.command' "$raw")"
[ -z "$cmd" ] && exit 0

# Match ancorato, non su sottostringa: `echo "git commit"` non è un commit.
git_invokes commit "$cmd" || git_invokes push "$cmd" || exit 0

branch="$(git -C "${CLAUDE_PROJECT_DIR:-.}" rev-parse --abbrev-ref HEAD 2>/dev/null)"
[ "$branch" != "main" ] && exit 0

cat >&2 <<MSG
BLOCCATO: sei su 'main'. L'harness impedisce i commit diretti sul branch di default.

Apri un branch prima di committare:
  git switch -c <tipo>/<slug>      # feat|fix|chore|docs|test
  # tipo/slug coerente con la issue che stai chiudendo, es. feat/issue-12-upload

Poi committa e apri la PR con:  /issue-done
Regola completa: agents/rules/workflow-issue.md
MSG
exit 2
