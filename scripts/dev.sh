#!/usr/bin/env bash
# =============================================================================
#  Hagenthon — Un campo alla volta
#  dev.sh: avvia in parallelo Ollama, backend (Spring :8080), frontend (Angular :4200)
#  Esegui setup.sh almeno una volta prima di questo script.
#  Ctrl+C ferma tutti i processi.
# =============================================================================

set -euo pipefail

# --- colori ------------------------------------------------------------------
RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[1;33m'
CYAN='\033[0;36m'; BOLD='\033[1m'; RESET='\033[0m'

ok()   { echo -e "${GREEN}✔${RESET}  $*"; }
warn() { echo -e "${YELLOW}⚠${RESET}  $*"; }
info() { echo -e "${CYAN}▶${RESET}  $*"; }

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT="$SCRIPT_DIR/.."

LOG_DIR="$ROOT/.dev-logs"
mkdir -p "$LOG_DIR"

# Maven wrapper o mvn di sistema
if [[ -f "$ROOT/app/backend/mvnw" ]]; then
  MVN="$ROOT/app/backend/mvnw"
else
  MVN="mvn"
fi

# PID dei processi figlio
PIDS=()

# --- pulizia al Ctrl+C -------------------------------------------------------
cleanup() {
  echo -e "\n${YELLOW}Interruzione — fermo tutti i servizi...${RESET}"
  for pid in "${PIDS[@]}"; do
    kill "$pid" 2>/dev/null || true
  done
  # Ferma anche i processi npm/mvn figli rimasti
  pkill -P $$ 2>/dev/null || true
  echo -e "${GREEN}Fatto.${RESET}"
}
trap cleanup SIGINT SIGTERM EXIT

# --- 1. Ollama ---------------------------------------------------------------
info "Ollama"
if curl -sf http://localhost:11434 &>/dev/null; then
  ok "Ollama già in ascolto su :11434"
else
  warn "Ollama non attivo — lo avvio..."
  ollama serve >"$LOG_DIR/ollama.log" 2>&1 &
  PIDS+=($!)
  # Attendi pronto (max 10 s)
  for i in $(seq 1 10); do
    curl -sf http://localhost:11434 &>/dev/null && break
    sleep 1
  done
  ok "Ollama avviato (log: .dev-logs/ollama.log)"
fi

# --- 2. Backend Spring Boot --------------------------------------------------
info "Backend  →  http://localhost:8080"
"$MVN" -f "$ROOT/app/backend/pom.xml" spring-boot:run \
  -Dmaven.test.skip=true \
  >"$LOG_DIR/backend.log" 2>&1 &
PIDS+=($!)
ok "Backend avviato (log: .dev-logs/backend.log)"

# Attendi che Spring sia pronto (max 60 s)
info "Attendo che il backend risponda..."
for i in $(seq 1 60); do
  if curl -sf http://localhost:8080/actuator/health &>/dev/null \
     || curl -sf http://localhost:8080/ &>/dev/null; then
    ok "Backend pronto"
    break
  fi
  sleep 1
  if [[ $i -eq 60 ]]; then
    warn "Backend non risponde dopo 60 s — controlla .dev-logs/backend.log"
  fi
done

# --- 3. Frontend Angular -----------------------------------------------------
info "Frontend →  http://localhost:4200"
cd "$ROOT/app/frontend"
npx ng serve --open=false >"$LOG_DIR/frontend.log" 2>&1 &
PIDS+=($!)
cd "$ROOT"
ok "Frontend avviato (log: .dev-logs/frontend.log)"

# --- Riepilogo ---------------------------------------------------------------
echo ""
echo -e "${BOLD}═══════════════════════════════════════${RESET}"
echo -e "  ${GREEN}${BOLD}Ambiente di sviluppo attivo${RESET}"
echo -e "  Frontend  →  ${CYAN}http://localhost:4200${RESET}"
echo -e "  Backend   →  ${CYAN}http://localhost:8080${RESET}"
echo -e "  Ollama    →  ${CYAN}http://localhost:11434${RESET}"
echo -e "  Log       →  ${CYAN}.dev-logs/${RESET}"
echo -e "${BOLD}═══════════════════════════════════════${RESET}"
echo -e "  ${YELLOW}Ctrl+C per fermare tutto${RESET}"
echo ""

# Tieni in vita lo script (i figli girano in background)
wait
