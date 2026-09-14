# Utility condivise dagli hook. Nessuna dipendenza obbligatoria:
# gli hook girano anche sulla macchina di chi valuta.

# Estrae un campo dal JSON. jq se c'è, altrimenti fallback POSIX.
json_field() {
  local path="$1" raw="$2"
  if command -v jq >/dev/null 2>&1; then
    printf '%s' "$raw" | jq -r "$path // empty" 2>/dev/null
  else
    local key="${path##*.}"
    printf '%s' "$raw" | sed -n "s/.*\"$key\"[[:space:]]*:[[:space:]]*\"\([^\"]*\)\".*/\1/p" | head -1
  fi
}

# `timeout` è GNU e su macOS NON esiste: qui una versione portabile.
run_timeout() {
  local secs="$1"; shift
  "$@" & local pid=$!
  ( sleep "$secs"; kill -9 "$pid" 2>/dev/null ) & local watcher=$!
  wait "$pid" 2>/dev/null; local rc=$?
  kill -9 "$watcher" 2>/dev/null; wait "$watcher" 2>/dev/null
  return $rc
}
