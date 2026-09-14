#!/usr/bin/env bash
# PostToolUse(Bash) — quando viene aperta una PR, ne rende impossibile la perdita:
# la stampa in sessione e la registra su file. Un link di PR annegato in mezzo
# all'output di un comando è un link perso.
set -uo pipefail

raw="$(cat)"
printf '%s' "$raw" | grep -q 'gh pr create' || exit 0

url="$(printf '%s' "$raw" \
  | grep -oE 'https://github\.com/[^"[:space:]\\]+/pull/[0-9]+' \
  | head -1)"
[ -z "$url" ] && exit 0

root="${CLAUDE_PROJECT_DIR:-.}"
log="$root/.claude/pr-links.log"
mkdir -p "$(dirname "$log")" 2>/dev/null
if ! grep -qF "$url" "$log" 2>/dev/null; then
  printf '%s  %s\n' "$(date '+%Y-%m-%d %H:%M')" "$url" >> "$log"
fi

cat <<MSG
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
PR APERTA → $url
Riportala all'utente nella tua prossima risposta, come link.
Registrata anche in .claude/pr-links.log
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
MSG
exit 0
