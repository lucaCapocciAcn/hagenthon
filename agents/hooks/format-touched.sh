#!/usr/bin/env bash
# PostToolUse(Write|Edit) — formatta solo il file appena toccato, in silenzio.
# Se il tool di formattazione non è installato non fallisce: l'harness non deve
# mai bloccare il lavoro per una dipendenza locale mancante.
set -uo pipefail
. "$(dirname "$0")/_json.sh"

raw="$(cat)"
f="$(json_field '.tool_input.file_path' "$raw")"
[ -z "$f" ] || [ ! -f "$f" ] && exit 0

root="${CLAUDE_PROJECT_DIR:-.}"
case "$f" in
  *.ts|*.html|*.css|*.scss|*.json|*.md)
    fe="$root/app/frontend"
    [ -x "$fe/node_modules/.bin/prettier" ] || exit 0
    "$fe/node_modules/.bin/prettier" --write --log-level silent "$f" >/dev/null 2>&1
    ;;
  *.java)
    # Spotless è configurato sul pom: formatta il singolo file senza rebuild.
    command -v mvn >/dev/null 2>&1 || exit 0
    (cd "$root/app/backend" && timeout 60 mvn -q -o spotless:apply \
        -DspotlessFiles="$(printf '%s' "$f" | sed 's/[].[^$\\*\/]/\\&/g')" >/dev/null 2>&1)
    ;;
esac
exit 0
