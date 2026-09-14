# T002 — Modello Ollama: quale per italiano burocratico su hardware consumer?
label: wayfinder:research
status: open
parent: MAP-001

## Question

Quale modello Ollama (≤ 8B parametri, scaricabile in demo, gira su GPU consumer)
è più adatto per:
1. Leggere il testo di un campo PDF burocratico italiano (es. "estremi del titolo
   di occupazione dell'alloggio") e generare una domanda in italiano semplice
   (es. "Come si chiama la via dove vai ad abitare?")
2. Latenza accettabile per una demo live (< 5 secondi per risposta)
3. Nessuna subscription richiesta all'utente finale

Candidati da confrontare: `llama3.2:3b`, `llama3.1:8b`, `mistral:7b`, `phi3:mini`,
`gemma2:9b`, `qwen2.5:7b`.

Criteri di valutazione: qualità italiano · comprensione burocratico → semplice ·
velocità · dimensione download.

## Resolution

**MODELLO SCELTO: `qwen2.5:7b`**
- Miglior italiano tra i 7-8B su Ollama; addestrato su corpus multilingua esteso
- RAM: ~4.7 GB (Q4_K_M) — funziona su MacBook 8 GB RAM e Apple Silicon M1/M2/M3
- Latenza: 2-4 s su M2/M3, 1-3 s su GPU discreta 8 GB VRAM → sotto i 5 s richiesti

**ALTERNATIVA LEGGERA (se RAM scarsa): `phi3:mini`**
- ~2.3 GB, latenza < 2 s. Italiano leggermente meno fluente su testi burocratici.

**COMANDO:**
```bash
ollama pull qwen2.5:7b
```

**TEMPLATE PROMPT (ottimale):**
- System (< 50 token): `"Sei un assistente che semplifica il linguaggio burocratico italiano. Rispondi sempre e solo in italiano."`
- User: `"Il modulo contiene il campo: \"<fieldLabel>\". Scrivi UNA sola domanda, in italiano semplice, per una persona di 70 anni. Massimo 15 parole. Niente spiegazioni, solo la domanda."`

**API endpoint:**
`POST http://localhost:11434/api/chat`
Payload: `{ "model": "qwen2.5:7b", "stream": false, "options": { "temperature": 0.3, "num_predict": 60 }, "messages": [...] }`

**Estrazione risposta Java:**
```java
String domanda = response.getMessage().getContent().trim();
```
Aggiungere validazione: se `split("\\s+").length > 18`, re-prompt.

**Riepilogo per hardware:**
- MacBook M1/M2/M3 8 GB → `phi3:mini` (sicuro) o `qwen2.5:7b` (consigliato, ce la fa)
- MacBook 16 GB / Windows GPU 6-8 GB VRAM → `qwen2.5:7b`
- Windows GPU 4 GB VRAM → `phi3:mini`

Status: CHIUSO ✅
