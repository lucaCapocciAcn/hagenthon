package com.hagenthon.uncampoallavolta.tools;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDCheckBox;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;

/**
 * Generatore di PDF AcroForm di prova per la demo di Hagenthon.
 * Crea due moduli burocratici realistici:
 * 1. Dichiarazione di residenza (~12 campi)
 * 2. Domanda contributo affitto (~10 campi)
 */
public class SampleFormGenerator {

    private static final String SAMPLES_DIR = "/Users/luca.capocci/hagenthon/app/samples";
    private static final float MARGIN_LEFT = 50;
    private static final float MARGIN_RIGHT = 50;
    private static final float FIELD_WIDTH = 300;
    private static final float FIELD_HEIGHT = 22;
    private static final float LINE_SPACING = 35;
    private static final float LABEL_X = MARGIN_LEFT;
    private static final float FIELD_X = LABEL_X + 250;

    public static void main(String[] args) throws IOException {
        // Assicura che la directory samples esista
        Path samplesPath = Paths.get(SAMPLES_DIR);
        Files.createDirectories(samplesPath);

        // Genera i due PDF
        generateDichiarazioneResidenza();
        generateContributoAffitto();

        System.out.println("\nPDF generati con successo in: " + SAMPLES_DIR);

        // Verifica che i campi siano estratti correttamente
        System.out.println("\n=== VERIFICA ESTRAZIONE CAMPI ===\n");
        verifyPdfFields(SAMPLES_DIR + "/dichiarazione-residenza.pdf", "DICHIARAZIONE DI RESIDENZA");
        verifyPdfFields(SAMPLES_DIR + "/contributo-affitto.pdf", "DOMANDA DI CONTRIBUTO PER AFFITTO");
    }

    /**
     * Verifica che i campi di un PDF possano essere estratti correttamente
     */
    private static void verifyPdfFields(String filePath, String title) throws IOException {
        System.out.println(title);
        System.out.println("  File: " + filePath);

        try (PDDocument doc = Loader.loadPDF(Files.readAllBytes(Paths.get(filePath)))) {
            PDAcroForm form = doc.getDocumentCatalog().getAcroForm();
            if (form == null) {
                System.out.println("  ERRORE: PDF senza campi compilabili");
                return;
            }

            int count = 0;
            for (PDField field : form.getFieldTree()) {
                count++;
            }
            System.out.println("  Campi estratti: " + count);
            System.out.println();

            for (PDField field : form.getFieldTree()) {
                String name = field.getFullyQualifiedName();
                String label = field.getAlternateFieldName();
                if (label == null || label.isBlank()) {
                    label = name;
                }
                System.out.println("    - " + name + " => " + label);
            }
        }

        System.out.println();
    }

    /**
     * Genera il PDF "Dichiarazione di residenza" con 12 campi
     */
    private static void generateDichiarazioneResidenza() throws IOException {
        List<FieldDefinition> fields = new ArrayList<>();
        fields.add(new FieldDefinition("cognome", "Cognome", "text"));
        fields.add(new FieldDefinition("nome", "Nome", "text"));
        fields.add(new FieldDefinition("luogo_nascita", "Luogo di nascita", "text"));
        fields.add(new FieldDefinition("data_nascita", "Data di nascita (gg/mm/aaaa)", "text"));
        fields.add(new FieldDefinition("codice_fiscale", "Codice fiscale", "text"));
        fields.add(new FieldDefinition(
                "titolo_occupazione",
                "Estremi del titolo di occupazione dell'alloggio ai sensi dell'art. 5 D.L. 47/2014",
                "text"));
        fields.add(new FieldDefinition("via_nuova", "Indirizzo di nuova dimora abituale - Via/Piazza", "text"));
        fields.add(new FieldDefinition("civico", "Numero civico", "text"));
        fields.add(new FieldDefinition("comune_nuovo", "Comune di nuova residenza", "text"));
        fields.add(new FieldDefinition("provincia_nuovo", "Provincia", "text"));
        fields.add(new FieldDefinition(
                "dichiarazione_47", "Dichiarazione sostitutiva ai sensi dell'art. 47 DPR 445/2000", "checkbox"));
        fields.add(new FieldDefinition("data_dichiarazione", "Data della dichiarazione (gg/mm/aaaa)", "text"));
        fields.add(new FieldDefinition("firma", "Firma del dichiarante", "text"));

        String pdfPath = SAMPLES_DIR + "/dichiarazione-residenza.pdf";
        createFormPdf(
                pdfPath,
                "DICHIARAZIONE DI RESIDENZA",
                "Ai sensi dell'art. 4, comma 3 del decreto legislativo 6 settembre 1989, n. 322",
                fields);
        System.out.println("Created: " + pdfPath + " (" + fields.size() + " fields)");
    }

    /**
     * Genera il PDF "Domanda di contributo affitto" con ~10 campi
     */
    private static void generateContributoAffitto() throws IOException {
        List<FieldDefinition> fields = new ArrayList<>();
        fields.add(new FieldDefinition("richiedente_cognome", "Cognome del richiedente", "text"));
        fields.add(new FieldDefinition("richiedente_nome", "Nome del richiedente", "text"));
        fields.add(new FieldDefinition("richiedente_cf", "Codice fiscale del richiedente", "text"));
        fields.add(new FieldDefinition("isee_valore", "Valore ISEE (euro)", "text"));
        fields.add(new FieldDefinition("canone_mensile", "Canone mensile di locazione (euro)", "text"));
        fields.add(new FieldDefinition("contratto_numero", "Numero di registrazione contratto", "text"));
        fields.add(new FieldDefinition("contratto_data", "Data di stipula del contratto (gg/mm/aaaa)", "text"));
        fields.add(new FieldDefinition("immobile_via", "Via/Piazza dell'immobile locato", "text"));
        fields.add(new FieldDefinition("immobile_civico", "Numero civico", "text"));
        fields.add(new FieldDefinition("immobile_comune", "Comune dell'immobile", "text"));
        fields.add(new FieldDefinition(
                "dichiaro_veridicita",
                "Dichiaro la veridicità di quanto sopra esposto ai sensi dell'art. 47 DPR 445/2000",
                "checkbox"));

        String pdfPath = SAMPLES_DIR + "/contributo-affitto.pdf";
        createFormPdf(
                pdfPath,
                "DOMANDA DI CONTRIBUTO PER AFFITTO",
                "Procedura per l'accesso al contributo straordinario per il pagamento dei canoni di locazione",
                fields);
        System.out.println("Created: " + pdfPath + " (" + fields.size() + " fields)");
    }

    /**
     * Crea un PDF AcroForm con i campi e le etichette forniti
     */
    private static void createFormPdf(String filePath, String title, String subtitle, List<FieldDefinition> fields)
            throws IOException {
        PDDocument doc = new PDDocument();

        // Setup form e font
        PDAcroForm acroForm = new PDAcroForm(doc);
        doc.getDocumentCatalog().setAcroForm(acroForm);

        PDFont helv = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
        PDFont helvBold = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);

        // Configura le risorse default
        PDResources dr = new PDResources();
        dr.put(COSName.getPDFName("Helv"), helv);
        acroForm.setDefaultResources(dr);
        acroForm.setDefaultAppearance("/Helv 12 Tf 0 g");

        // Aggiungi pagine e campi
        float currentY = 750;
        PDPage currentPage = new PDPage(PDRectangle.A4);
        doc.addPage(currentPage);

        // Titolo
        try (PDPageContentStream contentStream =
                new PDPageContentStream(doc, currentPage, PDPageContentStream.AppendMode.APPEND, true, true)) {
            contentStream.setFont(helvBold, 16);
            contentStream.beginText();
            contentStream.newLineAtOffset(MARGIN_LEFT, currentY);
            contentStream.showText(title);
            contentStream.endText();

            currentY -= 25;
            contentStream.setFont(helv, 10);
            contentStream.beginText();
            contentStream.newLineAtOffset(MARGIN_LEFT, currentY);
            contentStream.showText(subtitle);
            contentStream.endText();

            currentY -= 30;
        }

        // Aggiungi campi
        for (FieldDefinition fieldDef : fields) {
            // Controlla se serve una nuova pagina
            if (currentY < 80) {
                currentPage = new PDPage(PDRectangle.A4);
                doc.addPage(currentPage);
                currentY = 750;
            }

            // Disegna l'etichetta
            try (PDPageContentStream contentStream =
                    new PDPageContentStream(doc, currentPage, PDPageContentStream.AppendMode.APPEND, true, true)) {
                contentStream.setFont(helv, 11);
                contentStream.beginText();
                contentStream.newLineAtOffset(LABEL_X, currentY);
                // Trunca l'etichetta se troppo lunga
                String label = fieldDef.label;
                if (label.length() > 70) {
                    label = label.substring(0, 67) + "...";
                }
                contentStream.showText(label);
                contentStream.endText();
            }

            // Aggiungi il campo form
            if ("checkbox".equals(fieldDef.type)) {
                PDCheckBox checkBox = new PDCheckBox(acroForm);
                checkBox.setPartialName(fieldDef.partialName);
                checkBox.setAlternateFieldName(fieldDef.label);

                PDAnnotationWidget widget = checkBox.getWidgets().get(0);
                widget.setRectangle(new PDRectangle(FIELD_X, currentY - 8, 20, FIELD_HEIGHT));
                widget.setPage(currentPage);
                widget.setPrinted(true);

                currentPage.getAnnotations().add(widget);
                acroForm.getFields().add(checkBox);
            } else {
                PDTextField textField = new PDTextField(acroForm);
                textField.setPartialName(fieldDef.partialName);
                textField.setAlternateFieldName(fieldDef.label);

                PDAnnotationWidget widget = textField.getWidgets().get(0);
                widget.setRectangle(new PDRectangle(FIELD_X, currentY - 8, FIELD_WIDTH, FIELD_HEIGHT));
                widget.setPage(currentPage);
                widget.setPrinted(true);

                currentPage.getAnnotations().add(widget);
                acroForm.getFields().add(textField);
            }

            currentY -= LINE_SPACING;
        }

        // Salva il documento
        doc.save(filePath);
        doc.close();
    }

    /**
     * Definizione di un campo form
     */
    static class FieldDefinition {
        String partialName; // Nome tecnico (es. "cognome")
        String label; // Etichetta burocratica leggibile
        String type; // "text" o "checkbox"

        FieldDefinition(String partialName, String label, String type) {
            this.partialName = partialName;
            this.label = label;
            this.type = type;
        }
    }
}
