---
name: pdf-acroform-toolkit
description: Pattern Apache PDFBox 3.x per leggere ed estrarre i campi AcroForm da un PDF e per riscrivere un PDF compilato. Usala quando lavori con la manipolazione di moduli PDF in Java (estrazione campi, compilazione, flatten).
---

# PDFBox AcroForm — pattern essenziali

Dipendenza Maven: `org.apache.pdfbox:pdfbox:3.0.x`.

## Estrarre i campi

```java
try (PDDocument doc = Loader.loadPDF(bytes)) {
    PDAcroForm form = doc.getDocumentCatalog().getAcroForm();
    if (form == null) throw new IllegalArgumentException("PDF senza campi compilabili");
    for (PDField f : form.getFieldTree()) {           // getFieldTree = include annidati
        String name  = f.getFullyQualifiedName();      // nome tecnico → chiave
        String label = f.getAlternateFieldName();      // tooltip "TU" = etichetta leggibile
        if (label == null || label.isBlank()) label = name;
    }
}
```

## Compilare e restituire i byte

```java
try (PDDocument doc = Loader.loadPDF(originalBytes)) {
    PDAcroForm form = doc.getDocumentCatalog().getAcroForm();
    for (var e : answers.entrySet()) {
        PDField field = form.getField(e.getKey());
        if (field != null) field.setValue(e.getValue()); // text: valore diretto
    }
    if (flatten) form.flatten();                          // non più modificabile
    var out = new ByteArrayOutputStream();
    doc.save(out);
    return out.toByteArray();
}
```

## Gotcha

- `getFields()` restituisce solo le radici; usa `getFieldTree()` per i campi annidati.
- Checkbox/radio: `setValue()` vuole il valore di esportazione (es. `"Yes"`), non testo libero.
- Font mancanti dopo flatten: imposta `form.setNeedAppearances(false)` e assicura un font
  di default prima del flatten.
- Se `getAcroForm()` è null il PDF è scansionato o statico — non gestibile con questa skill.
- Lavora sempre su `byte[]`, non su path: il PDF arriva come upload multipart.
