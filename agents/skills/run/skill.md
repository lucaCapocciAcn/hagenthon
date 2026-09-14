---
name: run
description: Avvia l'app locale — Ollama + backend Spring Boot (:8080) + frontend Angular (:4200). Usa scripts/dev.sh come entrypoint. Controlla anche che il setup iniziale sia stato fatto.
---

# Avvia "Un campo alla volta" in locale

## Prima di avviare

Controlla che `app/frontend/node_modules/` esista. Se non c'è, esegui prima:

```bash
./scripts/setup.sh
```

> Se setup.sh non è mai stato eseguito (node_modules assente, modello Ollama mancante),
> avvialo e attendi il completamento prima di procedere.

## Avvio

```bash
chmod +x ./scripts/dev.sh ./scripts/setup.sh
./scripts/dev.sh
```

Lo script avvia in parallelo:
1. **Ollama** su `:11434` (se non già in ascolto)
2. **Backend** Spring Boot su `:8080` (Maven, log in `.dev-logs/backend.log`)
3. **Frontend** Angular su `:4200` (log in `.dev-logs/frontend.log`)

## Verifica che sia tutto su

Dopo l'avvio, controlla i tre endpoint:

```bash
curl -sf http://localhost:11434 && echo "Ollama OK"
curl -sf http://localhost:8080/  && echo "Backend OK"
curl -sf http://localhost:4200/  && echo "Frontend OK"
```

Se uno dei tre non risponde, mostra le ultime righe del log corrispondente:

```bash
tail -30 .dev-logs/backend.log
tail -30 .dev-logs/frontend.log
tail -20 .dev-logs/ollama.log
```

## Porte già in uso?

```bash
lsof -ti :8080 | xargs kill -9 2>/dev/null; \
lsof -ti :4200 | xargs kill -9 2>/dev/null
```

Poi rilancia `./scripts/dev.sh`.

## Stop

`Ctrl+C` nello stesso terminale ferma tutti i processi figli.
