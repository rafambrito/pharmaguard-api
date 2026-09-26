#!/bin/sh
set -e

MODEL="${OLLAMA_MODEL:-llama3.2:1b}"

# O servidor precisa estar no ar para que "ollama list/pull" funcionem.
/bin/ollama serve &
SERVER_PID=$!

until /bin/ollama list >/dev/null 2>&1; do
  echo "[ollama-init] aguardando o servidor subir..."
  sleep 2
done

# Lock em volume persistente evita downloads simultaneos em recriacoes/replicas.
LOCK_DIR="/root/.ollama/.pull-${MODEL}.lock"
if /bin/ollama list | awk '{print $1}' | grep -Fxq "$MODEL"; then
  echo "[ollama-init] modelo $MODEL ja presente no volume, pull ignorado"
elif mkdir "$LOCK_DIR" 2>/dev/null; then
  trap 'rmdir "$LOCK_DIR" 2>/dev/null || true' EXIT INT TERM
  echo "[ollama-init] baixando modelo $MODEL..."
  until /bin/ollama pull "$MODEL"; do
    echo "[ollama-init] falha no pull de $MODEL, nova tentativa em 10s..."
    sleep 10
  done
  rmdir "$LOCK_DIR" 2>/dev/null || true
  trap - EXIT INT TERM
  echo "[ollama-init] modelo $MODEL pronto"
else
  echo "[ollama-init] download de $MODEL ja em andamento em outro processo, aguardando..."
  while [ -d "$LOCK_DIR" ]; do sleep 5; done
fi

wait "$SERVER_PID"
