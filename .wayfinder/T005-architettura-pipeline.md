# T005 — Architettura pipeline: PDF → campi → domande semplici → PDF compilato
label: wayfinder:grilling
status: open
parent: MAP-001
blocked-by: T001, T004

## Question

Come si costruisce la pipeline backend?

```
[PDF caricato]
     ↓
[BE: estrai campi]  ← dipende da T001 (AcroForm vs altro)
     ↓
[BE: genera domande semplici via Ollama]
     ↓
[FE: mostra una domanda alla volta] ← dipende da T004 (UX)
     ↓
[FE: raccoglie risposta]
     ↓
[BE: mappa risposta → campo PDF]
     ↓
[BE: compila PDF con risposte]
     ↓
[FE: download PDF compilato]
```

Decisioni da prendere:

1. **Session state**: le risposte vivono in memoria BE (Map<sessionId, List<FieldAnswer>>)?
   Oppure il FE le mantiene e le manda tutte insieme alla fine?

2. **Generazione domande**: le domande si generano tutte upfront (una sola chiamata
   Ollama all'inizio) o on-demand campo per campo (più lenta ma serve meno memoria)?
   → upfront è preferibile per la demo (meno latenza per domanda)

3. **Endpoint REST**:
   - `POST /api/pdf/upload` → restituisce sessionId + lista domande
   - `POST /api/pdf/{sessionId}/complete` → riceve risposte, restituisce PDF compilato

4. **Libreria PDF BE**: Apache PDFBox (open source, AcroForm support) o iText (license)?
   → PDFBox per semplicità e licenza Apache

5. **Chiamata Ollama**: `POST http://localhost:11434/api/generate` con prompt strutturato.
   Serve client HTTP (RestTemplate / WebClient) — nessuna lib esterna.

6. **Struttura payload Ollama**:
   ```json
   {
     "model": "llama3.1:8b",
     "prompt": "Sei un assistente che aiuta anziani a compilare moduli. Il campo si chiama '{fieldLabel}'. Scrivi UNA sola domanda in italiano semplice, max 15 parole, come se parlassi con una persona di 70 anni. Solo la domanda, nessun'altra parola.",
     "stream": false
   }
   ```

## Resolution

**SEMPLIFICATA (decisione utente: app = veicolo dimostrativo, tenersi semplici).**

Pipeline generica AcroForm, PDF di test generati da noi:
- `POST /api/forms/upload` (multipart PDF) → estrae campi AcroForm via PDFBox, per ogni campo chiama Ollama per la domanda semplice, ritorna `{ sessionId, questions: [{fieldName, originalLabel, simpleQuestion}] }`
- `POST /api/forms/{sessionId}/answers` → riceve le risposte, PDFBox le scrive nei campi AcroForm, ritorna il PDF compilato (download)
- State in memoria: `Map<sessionId, FormSession>` (nessun DB)
- Domande generate **upfront** all'upload (una passata Ollama), non on-demand → demo più fluida
- Ollama: `POST localhost:11434/api/chat`, `qwen2.5:7b`, stream false, temp 0.3, num_predict 60 (da T002)
- Client Java: `RestClient`/`WebClient` nativo Spring, nessuna lib esterna

Status: CHIUSO ✅ (implementazione delegata ai builder agent di T003)
