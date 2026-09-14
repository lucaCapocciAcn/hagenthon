#!/usr/bin/env bash
# PostToolUse(Write|Edit) — formatta solo il file appena toccato, in silenzio.
# Se il formattatore non è installato non fallisce: l'harness non deve mai
# bloccare il lavoro per una dipendenza locale mancante.
set -uo pipefail
. "$(dirname "$0")/_lib.sh"

raw="$(cat)"
f="$(json_field '.tool_input.file_path' "$raw")"
[ -z "$f" ] || [ ! -f "$f" ] && exit 0

root="${CLAUDE_PROJECT_DIR:-.}"
fe="$root/app/frontend"
case "$f" in
  # SOLO i sorgenti del frontend: `.prettierrc` vive in app/frontend/, quindi
  # fuori di lì prettier userebbe i default (80 colonne) e riscriverebbe la
  # documentazione del repo con la config sbagliata.
  "$fe"/*.ts|"$fe"/*.html|"$fe"/*.css|"$fe"/*.scss|"$fe"/*.json)
    [ -x "$fe/node_modules/.bin/prettier" ] || exit 0
    run_timeout 30 "$fe/node_modules/.bin/prettier" --write --log-level silent "$f" >/dev/null 2>&1
    ;;
  "$root"/app/backend/*.java)
    command -v mvn >/dev/null 2>&1 || exit 0
    # -DspotlessFiles limita al SOLO file toccato: senza, spotless:apply
    # riformatta l'intero modulo e infila nel diff file che la issue non
    # nominava, violando la checklist della PR.
    esc="$(printf '%s' "$f" | sed 's/[].[^$\*\/]/\\&/g')"
    ( cd "$root/app/backend" && run_timeout 60 mvn -q -o spotless:apply -DspotlessFiles="$esc" >/dev/null 2>&1 )
    ;;
esac
exit 0
