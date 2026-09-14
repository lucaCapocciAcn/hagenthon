#!/usr/bin/env bash
# Test degli hook. Gira in CI e a mano: `./agents/hooks/test-hooks.sh`
#
# Perché esiste: gli hook sono ~200 righe di shell che nessun compilatore
# guarda. Il bug `timeout` (GNU, assente su macOS) è sopravvissuto per ore
# proprio perché niente lo eseguiva. Un hook non provato è un hook rotto
# che non lo sa ancora.
set -uo pipefail
H="$(cd "$(dirname "$0")" && pwd)"
ROOT="$(cd "$H/../.." && pwd)"
export CLAUDE_PROJECT_DIR="$ROOT"
PASS=0; FAIL=0

check() { # check <descrizione> <exit-atteso> <json> <hook>
  printf '  %-62s' "$1"
  out="$(printf '%s' "$3" | "$H/$4" 2>&1)"; rc=$?
  if [ "$rc" = "$2" ]; then echo "ok"; PASS=$((PASS+1))
  else echo "FALLITO (exit $rc, atteso $2)"; echo "$out" | head -3 | sed 's/^/        /'; FAIL=$((FAIL+1)); fi
}

# Repo fittizio su main: serve per provare i guard senza toccare il repo vero.
TMP="$(mktemp -d)"; trap 'rm -rf "$TMP"' EXIT
git init -q -b main "$TMP" 2>/dev/null
git -C "$TMP" commit -q --allow-empty -m init 2>/dev/null

echo "guard-main (su un repo il cui HEAD è main)"
export CLAUDE_PROJECT_DIR="$TMP"
check "git commit → blocca"                      2 '{"tool_input":{"command":"git commit -m x"}}'            guard-main.sh
check "git push → blocca"                        2 '{"tool_input":{"command":"git push"}}'                    guard-main.sh
check "cd app && git commit → blocca"            2 '{"tool_input":{"command":"cd app/backend && git commit -m x"}}' guard-main.sh
check "git  commit (doppio spazio) → blocca"     2 '{"tool_input":{"command":"git  commit -m x"}}'            guard-main.sh
check "git -c user.name=x commit → blocca"       2 '{"tool_input":{"command":"git -c user.name=x commit -m y"}}' guard-main.sh
check "git log → passa"                          0 '{"tool_input":{"command":"git log --oneline"}}'           guard-main.sh
# I tre casi che il match su sottostringa sbagliava:
check "echo \"un git commit\" → passa"           0 '{"tool_input":{"command":"echo \"come si fa un git commit\""}}' guard-main.sh
check "grep -rn \"git push\" → passa"            0 '{"tool_input":{"command":"grep -rn \"git push\" agents/"}}' guard-main.sh
check "autotest del README → passa"              0 '{"tool_input":{"command":"echo '"'"'{\"tool_input\":{\"command\":\"git commit -m x\"}}'"'"' | ./agents/hooks/guard-main.sh"}}' guard-main.sh

echo "guard-branch-base (su un branch che non è main)"
export CLAUDE_PROJECT_DIR="$ROOT"
if [ "$(git -C "$ROOT" rev-parse --abbrev-ref HEAD)" != "main" ]; then
  check "git switch -c → blocca (base non è main)"  2 '{"tool_input":{"command":"git switch -c feat/x"}}'      guard-branch-base.sh
  check "git checkout -b → blocca"                  2 '{"tool_input":{"command":"git checkout -b feat/y"}}'    guard-branch-base.sh
else
  echo "  (saltato: il repo è su main)"
fi
check "git switch main → passa (non crea)"        0 '{"tool_input":{"command":"git switch main"}}'             guard-branch-base.sh
check "ls -la → passa"                            0 '{"tool_input":{"command":"ls -la"}}'                      guard-branch-base.sh
check "echo \"git switch -c x\" → passa"          0 '{"tool_input":{"command":"echo \"per creare: git switch -c feat/x\""}}' guard-branch-base.sh

echo "pr-link"
check "gh pr create con URL → intercetta"         0 '{"tool_input":{"command":"gh pr create --fill"},"tool_response":{"stdout":"https://github.com/o/r/pull/42"}}' pr-link.sh
check "comando qualsiasi → silenzio"              0 '{"tool_input":{"command":"git status"},"tool_response":{"stdout":"ok"}}' pr-link.sh

echo "format-touched (deve restare in scope e non esplodere)"
check "file fuori da app/frontend → non tocca"    0 "{\"tool_input\":{\"file_path\":\"$ROOT/CLAUDE.md\"}}"      format-touched.sh
check "file inesistente → esce pulito"            0 '{"tool_input":{"file_path":"/non/esiste.ts"}}'            format-touched.sh

echo "libreria"
. "$H/_lib.sh"
printf '  %-62s' "run_timeout: comando veloce"
run_timeout 5 sleep 1 && { echo "ok"; PASS=$((PASS+1)); } || { echo "FALLITO"; FAIL=$((FAIL+1)); }
printf '  %-62s' "run_timeout: comando lento viene ucciso"
run_timeout 1 sleep 8; [ $? -ne 0 ] && { echo "ok"; PASS=$((PASS+1)); } || { echo "FALLITO"; FAIL=$((FAIL+1)); }
printf '  %-62s' "run_timeout: nessun processo sleep orfano"
before=$(pgrep -f '^sleep ' 2>/dev/null | wc -l | tr -d ' ')
run_timeout 1 sleep 9 >/dev/null 2>&1; sleep 1
after=$(pgrep -f '^sleep ' 2>/dev/null | wc -l | tr -d ' ')
[ "$after" -le "$before" ] && { echo "ok"; PASS=$((PASS+1)); } || { echo "FALLITO ($before → $after)"; FAIL=$((FAIL+1)); }

echo
echo "$PASS passati, $FAIL falliti"
[ "$FAIL" -eq 0 ]
