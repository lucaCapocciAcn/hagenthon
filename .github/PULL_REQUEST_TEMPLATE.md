<!-- Tieni la PR corta e leggibile. Se una sezione non si applica, scrivi perché. -->

## Cosa cambia

<!-- 2-4 righe. Cosa fa ora il sistema che prima non faceva. -->

## Issue collegata

Closes #

## Come l'ho verificato

<!-- Comando eseguito e esito reale, non "dovrebbe funzionare". Es:
     `cd app/backend && mvn -B verify` → BUILD SUCCESS, 4 test, 0 failure -->

| Comando | Esito |
| --- | --- |
| `cd app/backend && mvn clean verify` |  |
| `cd app/frontend && npx ng build --configuration production` |  |
| `cd app/frontend && npx ng lint` |  |

<!-- ⚠️ Leggi l'exit code VERO: `comando | tail` restituisce quello di `tail`,
     non del comando. Usa `set -o pipefail` o ${PIPESTATUS[0]}. -->

## Verdetto del `code-reviewer`

<!-- Obbligatorio: /issue-done non apre la PR senza `VERDETTO: APPROVATO`.
     Se hai respinto un rilievo, spiega qui perché — deve restare agli atti. -->

- **Verdetto:**
- **Rilievi sollevati e come li ho chiusi:**
- **Rilievi su cui non sono d'accordo, e perché:**

## Contributo AI vs revisione umana

<!-- Dichiarare cosa è stato generato e cosa verificato da una persona dice
     a chi rivede dove concentrare l'attenzione. -->

- **Generato dall'AI:**
- **Scritto / corretto a mano:**
- **Revisione umana fatta su:**

## Checklist

- [ ] Backend verde: `mvn clean verify` (test + Spotless + Error Prone)
- [ ] Frontend verde: build **AOT** di produzione + `ng lint`
- [ ] Il `code-reviewer` ha dato `VERDETTO: APPROVATO`
- [ ] Ho letto le regole dell'area che sto toccando (`CLAUDE.md`, `agents/README.md`)
- [ ] Nessun file fuori dallo scope dichiarato nella issue
- [ ] La issue collegata è passata a `agent:review`
