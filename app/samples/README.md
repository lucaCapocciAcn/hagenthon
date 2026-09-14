# Sample PDF Forms for Hagenthon Demo

This directory contains two sample AcroForm PDF documents for testing the `pdf-form-engineer` demo.

## Files

### 1. `dichiarazione-residenza.pdf` (13 fields)

Dichiarazione di Residenza (Residence Declaration Form)
- `cognome` => Cognome
- `nome` => Nome
- `luogo_nascita` => Luogo di nascita
- `data_nascita` => Data di nascita (gg/mm/aaaa)
- `codice_fiscale` => Codice fiscale
- `titolo_occupazione` => Estremi del titolo di occupazione dell'alloggio ai sensi dell'art. 5 D.L. 47/2014
- `via_nuova` => Indirizzo di nuova dimora abituale - Via/Piazza
- `civico` => Numero civico
- `comune_nuovo` => Comune di nuova residenza
- `provincia_nuovo` => Provincia
- `dichiarazione_47` => Dichiarazione sostitutiva ai sensi dell'art. 47 DPR 445/2000 (checkbox)
- `data_dichiarazione` => Data della dichiarazione (gg/mm/aaaa)
- `firma` => Firma del dichiarante

### 2. `contributo-affitto.pdf` (11 fields)

Domanda di Contributo per Affitto (Rent Contribution Application)
- `richiedente_cognome` => Cognome del richiedente
- `richiedente_nome` => Nome del richiedente
- `richiedente_cf` => Codice fiscale del richiedente
- `isee_valore` => Valore ISEE (euro)
- `canone_mensile` => Canone mensile di locazione (euro)
- `contratto_numero` => Numero di registrazione contratto
- `contratto_data` => Data di stipula del contratto (gg/mm/aaaa)
- `immobile_via` => Via/Piazza dell'immobile locato
- `immobile_civico` => Numero civico
- `immobile_comune` => Comune dell'immobile
- `dichiaro_veridicita` => Dichiaro la veridicità di quanto sopra esposto ai sensi dell'art. 47 DPR 445/2000 (checkbox)

## Generation

Both PDFs are generated with realistic Italian bureaucratic labels (setAlternateFieldName) and can be freely edited using any PDF form viewer. The forms are created by `SampleFormGenerator.java` in `app/backend/src/test/java/com/hagenthon/uncampoallavolta/tools/`.
