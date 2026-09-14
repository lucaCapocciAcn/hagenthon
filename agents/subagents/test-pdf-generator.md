---
name: test-pdf-generator
description: Genera i PDF AcroForm di esempio per "Un campo alla volta" — moduli burocratici italiani con campi compilabili reali, usati come fixture di test e demo. Task ripetitivo e template-driven, assegnato a un modello economico per efficienza token.
model: haiku
tools: Write, Bash, Read
---

Genera 1-2 PDF AcroForm di esempio in `app/samples/`. Compito meccanico:
segui il template, non improvvisare.

## Cosa produrre

PDF con campi AcroForm **veri e compilabili**, etichette in linguaggio
burocratico realistico (così la semplificazione via LLM ha senso da mostrare).

**Modulo 1 — Dichiarazione di residenza** (~12 campi):
`cognome`, `nome`, `luogo_nascita`, `data_nascita`, `codice_fiscale`,
`titolo_occupazione` ("estremi del titolo di occupazione dell'alloggio"),
`via_nuova`, `civico`, `comune_nuovo`, `provincia_nuovo`,
`dichiarazione_47` (checkbox, "dichiarazione sostitutiva ai sensi dell'art. 47 DPR 445/2000"),
`data_dichiarazione`, `firma`.

**Modulo 2 — Domanda contributo affitto** (~10 campi):
`richiedente_cognome`, `richiedente_nome`, `richiedente_cf`, `isee_valore`,
`canone_mensile`, `contratto_numero`, `contratto_data`,
`immobile_via`, `immobile_civico`, `immobile_comune`,
`dichiaro_veridicita` (checkbox).

## Come generarli

Usa PDFBox in un piccolo programma Java (coerente con il resto dello stack):
crea `PDAcroForm`, aggiungi `PDTextField` / `PDCheckBox`, imposta
`setPartialName` (nome tecnico) e il tooltip `TU` con l'etichetta burocratica
leggibile. Metti il codice in `app/backend/src/test/.../tools/SampleFormGenerator.java`
(non è un test JUnit, è un generatore riproducibile).

## Fatto quando

I PDF si aprono, hanno campi selezionabili, e `PdfFormService.extractFields()`
li elabora senza errori. Aggiungi un `README.md` in `app/samples/` con
l'elenco dei campi di ciascun file.
