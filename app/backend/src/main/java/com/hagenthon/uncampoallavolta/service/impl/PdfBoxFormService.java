package com.hagenthon.uncampoallavolta.service.impl;

import com.hagenthon.uncampoallavolta.dto.QuestionDto;
import com.hagenthon.uncampoallavolta.service.PdfFormService;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDCheckBox;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Implementazione reale di {@link PdfFormService} basata su Apache PDFBox 3.x.
 *
 * <p>Estrae i campi AcroForm da un PDF e li restituisce come lista di {@link QuestionDto};
 * compila i campi con le risposte fornite e restituisce il PDF compilato (con flatten).
 */
@Service
public class PdfBoxFormService implements PdfFormService {

    /**
     * Estrae tutti i campi AcroForm dal PDF (inclusi annidati via {@code getFieldTree()}).
     * Popola {@code fieldName} con il nome tecnico e {@code originalLabel} con
     * {@code getAlternateFieldName()} (tooltip TU), con fallback al nome tecnico.
     * {@code simpleQuestion} è lasciata vuota: la riempirà {@code QuestionGenerator}.
     */
    @Override
    public List<QuestionDto> extractFields(byte[] pdfBytes) throws IOException {
        try (PDDocument doc = Loader.loadPDF(pdfBytes)) {
            PDAcroForm form = doc.getDocumentCatalog().getAcroForm();
            if (form == null) {
                throw new IllegalArgumentException("PDF senza campi compilabili");
            }

            List<QuestionDto> fields = new ArrayList<>();
            for (PDField field : form.getFieldTree()) {
                String name = field.getFullyQualifiedName();
                String label = field.getAlternateFieldName();
                if (label == null || label.isBlank()) {
                    label = name;
                }
                fields.add(new QuestionDto(name, label, ""));
            }
            return fields;
        }
    }

    /**
     * Compila i campi AcroForm del PDF con i valori forniti e restituisce i byte
     * del PDF risultante. Esegue {@code flatten()} per rendere il documento non
     * più modificabile (adatto alla consegna finale).
     *
     * <p>Campi non trovati nella mappa {@code answers} vengono lasciati vuoti.
     */
    @Override
    public byte[] fillForm(byte[] pdfBytes, Map<String, String> answers) throws IOException {
        try (PDDocument doc = Loader.loadPDF(pdfBytes)) {
            PDAcroForm form = doc.getDocumentCatalog().getAcroForm();
            if (form == null) {
                throw new IllegalArgumentException("PDF senza campi compilabili");
            }

            for (Map.Entry<String, String> entry : answers.entrySet()) {
                PDField field = form.getField(entry.getKey());
                if (field == null) continue;

                if (field instanceof PDCheckBox checkbox) {
                    // PDCheckBox richiede l'export value ("Yes"/"On") o "Off".
                    // Mappiamo risposte affermative comuni al valore di spunta reale.
                    String val = entry.getValue().trim().toLowerCase();
                    boolean checked = val.equals("si") || val.equals("sì")
                            || val.equals("yes") || val.equals("true") || val.equals("1");
                    checkbox.setValue(checked ? checkbox.getOnValue() : "Off");
                } else {
                    field.setValue(entry.getValue());
                }
            }

            form.flatten();

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            doc.save(out);
            return out.toByteArray();
        }
    }
}
