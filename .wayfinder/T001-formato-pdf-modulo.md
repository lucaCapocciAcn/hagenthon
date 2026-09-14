# T001 — Formato PDF: il modulo del Ministero è AcroForm o scansionato?
label: wayfinder:research
status: open
parent: MAP-001
blocks: T005

## Question

Il modulo ufficiale per il cambio di residenza (Ministero dell'Interno, scaricabile
da servizi.interno.gov.it) ha campi AcroForm leggibili programmaticamente con
librerie Java (Apache PDFBox / iText), oppure è un PDF scansionato / immagine?

La risposta determina l'intera architettura di parsing:
- **AcroForm** → `PDFBox.getAcroForm().getFields()`, 2 ore di lavoro, fattibile
- **Scansionato** → OCR + coordinate → fuori scope; si prepara un PDF AcroForm sostitutivo per la demo e si dichiara il limite in presentazione

Stessa verifica per il modulo contributo affitto (se disponibile come PDF digitale).

## Resolution

**FORMATO PDF: SCANSIONATO — zero campi AcroForm**

Sia la versione DAIT originale (immagine JBIG2, 4 pagine) sia le versioni redistribuite dai Comuni: **nessun campo AcroForm**. Non compilabile programmaticamente con la pipeline AcroForm → PDFBox.

**URL ufficiale DAIT:** `https://dait.interno.gov.it/documenti/circ-009-servdemo-27-04-2012-modulo-residenza.pdf`

**Libreria consigliata:** Apache PDFBox 3.x (Apache 2.0, nessun vincolo AGPL come iText 7 community)

**Alternativa per la demo con AcroForm reale:**
`https://www.inail.it/content/dam/inail-hub-site/documenti/2015/10/mod_101_RA_09_2018_reader.pdf` (INAIL mod_101_RA) — campi interattivi veri, documento burocratico italiano autentico.

**Decisione architetturale conseguente (da confermare in T005):**

**Approccio raccomandato: Template JSON hardcoded + overlay PDF**
1. I ~20 campi chiave del modulo residenza sono definiti come template JSON nel BE (nome, cognome, CF, data nascita, vecchio indirizzo, nuovo indirizzo, motivazione, ecc.)
2. Ollama genera la domanda semplice per ciascun campo al caricamento
3. PDFBox stampa i valori raccolti nelle coordinate note del PDF scansionato (overlay) OPPURE genera un PDF nuovo pulito
4. Alternativa più semplice: usare INAIL per la pipeline tecnica + modulo residenza come "storia di Anna" nella presentazione

**Snippet PDFBox — overlay testo su PDF:**
```java
PDPageContentStream cs = new PDPageContentStream(doc, page,
    PDPageContentStream.AppendMode.APPEND, true);
cs.beginText();
cs.setFont(PDType1Font.HELVETICA, 11);
cs.newLineAtOffset(x, y); // coordinate del campo
cs.showText(valoreUtente);
cs.endText();
cs.close();
```

Status: CHIUSO ✅
