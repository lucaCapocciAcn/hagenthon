#!/usr/bin/env bash
# PreToolUse(Bash) — un branch nuovo nasce SOLO da main allineato a origin/main.
# Motivo: un branch che parte da main vecchio produce una PR con dentro merge
# spuri e conflitti che non c'entrano col lavoro. Si paga sempre, e si paga dopo.
set -uo pipefail
. "$(dirname "$0")/_lib.sh"

raw="$(cat)"
cmd="$(json_field '.tool_input.command' "$raw")"
[ -z "$cmd" ] && exit 0

# Solo creazione di branch. Uno switch normale non ci riguarda.
case "$cmd" in
  *"git switch -c"*|*"git switch --create"*|*"git checkout -b"*) ;;
  *) exit 0 ;;
esac

repo="${CLAUDE_PROJECT_DIR:-.}"
cd "$repo" 2>/dev/null || exit 0
git rev-parse --git-dir >/dev/null 2>&1 || exit 0

base="$(git rev-parse --abbrev-ref HEAD 2>/dev/null)"

# Allinea la conoscenza di origin/main. Se la rete non c'è non blocchiamo:
# non possiamo verificare, e un hook non deve impedire di lavorare offline.
if ! run_timeout 12 git fetch --quiet origin main 2>/dev/null; then
  echo "guard-branch-base: fetch non riuscito (offline?), controllo saltato." >&2
  exit 0
fi
git rev-parse --verify --quiet origin/main >/dev/null 2>&1 || exit 0

if [ "$base" != "main" ]; then
  cat >&2 <<MSG
BLOCCATO: stai creando un branch partendo da '$base', non da 'main'.

Il lavoro nuovo parte sempre da main aggiornato, altrimenti la PR si porta
dentro commit che non c'entrano.

  git switch main && git pull --ff-only origin main
  # poi rilancia il comando di creazione del branch

Se devi davvero derivare da '$base' (lavoro dipendente da una PR non ancora
in main), dichiaralo: è un'eccezione, non la norma.
Regola: agents/rules/workflow-issue.md
MSG
  exit 2
fi

behind="$(git rev-list --count HEAD..origin/main 2>/dev/null || echo 0)"
if [ "${behind:-0}" -gt 0 ]; then
  cat >&2 <<MSG
BLOCCATO: 'main' è indietro di $behind commit rispetto a origin/main.

Allinea prima di ramificare:

  git pull --ff-only origin main
  # poi rilancia il comando di creazione del branch

Regola: agents/rules/workflow-issue.md
MSG
  exit 2
fi
exit 0
