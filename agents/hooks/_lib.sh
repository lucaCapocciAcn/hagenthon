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

# Vero se il comando INVOCA davvero `git <sub>`, non se lo nomina soltanto.
# Un match su sottostringa produce falsi positivi grotteschi: bloccherebbe
# `echo "come si fa un git commit"` e persino l'autotest di questi hook.
# Gestisce comandi composti (&& ; || |), spazi multipli e opzioni globali
# (`git -c k=v commit`, `git -C dir push`).
git_invokes() {
  local sub="$1" cmd="$2"
  printf '%s\n' "$cmd" | awk '
    { gsub(/&&|\|\||[;|]/, "\n"); print }
  ' | awk '
    { sub(/^[ \t]+/, ""); sub(/[ \t]+$/, ""); gsub(/[ \t]+/, " ")
      while ($0 ~ /^git (-c [^ ]+|-C [^ ]+|--git-dir=[^ ]+|--work-tree=[^ ]+) /)
        sub(/^git (-c [^ ]+|-C [^ ]+|--git-dir=[^ ]+|--work-tree=[^ ]+) /, "git ")
      print }
  ' | grep -qE "^git ${sub}( |$)"
}

# `timeout` è GNU e su macOS NON esiste: qui una versione portabile.
# `pkill -P` è necessario perché uccidere la subshell non uccide il suo
# `sleep` figlio, che resterebbe orfano a ogni invocazione.
run_timeout() {
  local secs="$1"; shift
  "$@" & local pid=$!
  ( sleep "$secs"; kill -9 "$pid" 2>/dev/null ) & local watcher=$!
  disown "$watcher" 2>/dev/null || true   # evita il messaggio "Terminated" della shell
  wait "$pid" 2>/dev/null; local rc=$?
  pkill -P "$watcher" 2>/dev/null
  kill -9 "$watcher" 2>/dev/null
  wait "$watcher" 2>/dev/null
  return $rc
}
