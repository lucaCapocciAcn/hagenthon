# Estrae un campo dal JSON su stdin. Usa jq se c'è, altrimenti fallback POSIX.
# Gli hook girano anche sulla macchina di chi valuta: non possono assumere jq.
json_field() {
  local path="$1" raw="$2"
  if command -v jq >/dev/null 2>&1; then
    printf '%s' "$raw" | jq -r "$path // empty" 2>/dev/null
  else
    local key="${path##*.}"
    printf '%s' "$raw" | sed -n "s/.*\"$key\"[[:space:]]*:[[:space:]]*\"\([^\"]*\)\".*/\1/p" | head -1
  fi
}
