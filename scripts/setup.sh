#!/usr/bin/env bash
# =============================================================================
#  Hagenthon — Un campo alla volta
#  setup.sh: verifica prerequisiti e installa tutte le dipendenze
#  Eseguire UNA VOLTA prima del primo avvio (o dopo un clone).
#  Non richiede nulla oltre a Java, Node e Ollama già installati.
# =============================================================================

set -euo pipefail

# --- colori ------------------------------------------------------------------
RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[1;33m'
CYAN='\033[0;36m'; BOLD='\033[1m'; RESET='\033[0m'

ok()   { echo -e "${GREEN}✔${RESET}  $*"; }
warn() { echo -e "${YELLOW}⚠${RESET}  $*"; }
fail() { echo -e "${RED}✘${RESET}  $*" >&2; }
step() { echo -e "\n${CYAN}${BOLD}▶ $*${RESET}"; }

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT="$SCRIPT_DIR/.."

# --- 1. Java 21+ -------------------------------------------------------------
step "Verifica Java 21+"
if ! command -v java &>/dev/null; then
  fail "Java non trovato. Installa JDK 21 (es. brew install --cask temurin@21)"
  exit 1
fi
JAVA_VER=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d. -f1)
if [[ "$JAVA_VER" -lt 21 ]]; then
  fail "Java $JAVA_VER trovato, richiesto 21+. Installa JDK 21."
  exit 1
fi
ok "Java $JAVA_VER"

# --- 2. Maven (wrapper opzionale, altrimenti mvn di sistema) -----------------
step "Verifica Maven"
if [[ -f "$ROOT/app/backend/mvnw" ]]; then
  chmod +x "$ROOT/app/backend/mvnw"
  MVN="$ROOT/app/backend/mvnw"
  ok "Maven wrapper trovato"
elif command -v mvn &>/dev/null; then
  MVN="mvn"
  ok "mvn di sistema: $(mvn -version 2>&1 | head -1)"
else
  fail "Maven non trovato. Installa con: brew install maven"
  exit 1
fi

# --- 3. Node 18+ e npm -------------------------------------------------------
step "Verifica Node 18+"
if ! command -v node &>/dev/null; then
  fail "Node.js non trovato. Installa con: brew install node"
  exit 1
fi
NODE_VER=$(node -e "process.stdout.write(process.versions.node.split('.')[0])")
if [[ "$NODE_VER" -lt 18 ]]; then
  fail "Node $NODE_VER trovato, richiesto 18+."
  exit 1
fi
ok "Node $NODE_VER · npm $(npm -v)"

# --- 4. Ollama ---------------------------------------------------------------
step "Verifica Ollama"
if ! command -v ollama &>/dev/null; then
  fail "Ollama non trovato. Installa da https://ollama.com o: brew install ollama"
  exit 1
fi
ok "Ollama $(ollama -v 2>/dev/null | head -1)"

# --- 5. npm install (frontend) -----------------------------------------------
step "Installa dipendenze frontend (npm ci)"
cd "$ROOT/app/frontend"
npm ci --prefer-offline 2>&1 | tail -3
ok "node_modules aggiornato"
cd "$ROOT"

# --- 6. Maven dependency:go-offline (backend) --------------------------------
step "Pre-scarica dipendenze Maven (backend)"
"$MVN" -f "$ROOT/app/backend/pom.xml" dependency:go-offline --quiet \
  -Dmaven.test.skip=true 2>&1 | tail -3
ok "Dipendenze Maven in cache locale"

# --- 7. Pull modello Ollama --------------------------------------------------
step "Pull modello Ollama (qwen2.5:7b)"
MODEL="qwen2.5:7b"

# Avvia Ollama temporaneamente se non è già in ascolto
_OLLAMA_STARTED=false
if ! curl -sf http://localhost:11434 &>/dev/null; then
  warn "Ollama serve non attivo — lo avvio in background per il pull..."
  ollama serve &>/tmp/ollama-setup.log &
  _OLLAMA_PID=$!
  _OLLAMA_STARTED=true
  # Attendi che sia pronto (max 15 s)
  for i in $(seq 1 15); do
    curl -sf http://localhost:11434 &>/dev/null && break
    sleep 1
  done
fi

if ollama list 2>/dev/null | grep -q "^${MODEL}"; then
  ok "Modello $MODEL già presente"
else
  echo "   Download $MODEL (circa 4 GB, potrebbe richiedere qualche minuto)..."
  ollama pull "$MODEL"
  ok "Modello $MODEL scaricato"
fi

# Ferma Ollama se lo avevamo avviato noi
if $_OLLAMA_STARTED; then
  kill "$_OLLAMA_PID" 2>/dev/null || true
fi

# --- Fine --------------------------------------------------------------------
echo -e "\n${GREEN}${BOLD}Setup completato.${RESET}"
echo -e "Avvia l'ambiente di sviluppo con: ${BOLD}./scripts/dev.sh${RESET}"
