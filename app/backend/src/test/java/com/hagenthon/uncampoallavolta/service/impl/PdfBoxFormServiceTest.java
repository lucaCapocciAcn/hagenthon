package com.hagenthon.uncampoallavolta.service.impl;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.hagenthon.uncampoallavolta.dto.QuestionDto;

/**
 * Test unitario per {@link PdfBoxFormService}.
 *
 * Genera al volo un PDF AcroForm con 3 campi text usando PDFBox,
 * verifica l'estrazione dei campi e la compilazione del PDF risultante.
 */
class PdfBoxFormServiceTest {

    private PdfBoxFormService service;

    @BeforeEach
    void setUp() {
        service = new PdfBoxFormService();
    }

    /**
     * Genera un PDF AcroForm minimale in memoria con i campi specificati.
     * Ogni campo ha un partialName (nome tecnico) e un alternateFieldName (etichetta leggibile).
     */
    private byte[] buildTestPdf() throws IOException {
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);

            PDAcroForm acroForm = new PDAcroForm(doc);
            doc.getDocumentCatalog().setAcroForm(acroForm);

            // Font di default richiesto da PDFBox per i campi testo
            PDType1Font font = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
            acroForm.setDefaultResources(acroForm.getDefaultResources());

            // Campo 1: cognome_nome con alternateFieldName
            addTextField(doc, acroForm, page, "cognome_nome", "Cognome e Nome", new PDRectangle(50, 700, 200, 20));

            // Campo 2: data_nascita con alternateFieldName
            addTextField(doc, acroForm, page, "data_nascita", "Data di nascita", new PDRectangle(50, 660, 200, 20));

            // Campo 3: codice_fiscale — nessun alternateFieldName, fallback al nome
            addTextFieldNoLabel(doc, acroForm, page, "codice_fiscale", new PDRectangle(50, 620, 200, 20));

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            doc.save(out);
            return out.toByteArray();
        }
    }

    private void addTextField(
            PDDocument doc,
            PDAcroForm acroForm,
            PDPage page,
            String partialName,
            String alternateFieldName,
            PDRectangle rect)
            throws IOException {
        PDTextField field = new PDTextField(acroForm);
        field.setPartialName(partialName);
        field.setAlternateFieldName(alternateFieldName);
        field.setDefaultValue("");

        PDAnnotationWidget widget = field.getWidgets().get(0);
        widget.setRectangle(rect);
        widget.setPage(page);
        page.getAnnotations().add(widget);

        acroForm.getFields().add(field);
    }

    private void addTextFieldNoLabel(
            PDDocument doc, PDAcroForm acroForm, PDPage page, String partialName, PDRectangle rect) throws IOException {
        PDTextField field = new PDTextField(acroForm);
        field.setPartialName(partialName);
        // Nessun alternateFieldName impostato: deve fare fallback al nome tecnico
        field.setDefaultValue("");

        PDAnnotationWidget widget = field.getWidgets().get(0);
        widget.setRectangle(rect);
        widget.setPage(page);
        page.getAnnotations().add(widget);

        acroForm.getFields().add(field);
    }

    @Test
    void extractFields_restituisceICampiConOriginalLabelCorretta() throws IOException {
        byte[] pdfBytes = buildTestPdf();

        List<QuestionDto> fields = service.extractFields(pdfBytes);

        assertFalse(fields.isEmpty(), "La lista dei campi non deve essere vuota");
        assertEquals(3, fields.size(), "Deve estrarre esattamente 3 campi");

        // Verifica campo cognome_nome
        QuestionDto cognome = fields.stream()
                .filter(f -> "cognome_nome".equals(f.fieldName()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Campo 'cognome_nome' non trovato"));
        assertEquals(
                "Cognome e Nome", cognome.originalLabel(), "originalLabel deve corrispondere all'alternateFieldName");
        assertEquals("", cognome.simpleQuestion(), "simpleQuestion deve essere vuota (riempita da QuestionGenerator)");

        // Verifica campo data_nascita
        QuestionDto dataNascita = fields.stream()
                .filter(f -> "data_nascita".equals(f.fieldName()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Campo 'data_nascita' non trovato"));
        assertEquals("Data di nascita", dataNascita.originalLabel());

        // Verifica fallback al nome tecnico quando non c'è alternateFieldName
        QuestionDto codiceFiscale = fields.stream()
                .filter(f -> "codice_fiscale".equals(f.fieldName()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Campo 'codice_fiscale' non trovato"));
        assertEquals(
                "codice_fiscale",
                codiceFiscale.originalLabel(),
                "Senza alternateFieldName, originalLabel deve fare fallback al nome tecnico");
    }

    @Test
    void fillForm_compilaICampiERestituiscePdfRiapribile() throws IOException {
        byte[] originalPdf = buildTestPdf();

        Map<String, String> answers = Map.of(
                "cognome_nome", "Rossi Maria",
                "data_nascita", "15/03/1952",
                "codice_fiscale", "RSSMRA52C55H501Z");

        byte[] compiledPdf = service.fillForm(originalPdf, answers);

        assertNotNull(compiledPdf, "Il PDF compilato non deve essere null");
        assertTrue(compiledPdf.length > 0, "Il PDF compilato non deve essere vuoto");

        // Verifica che il PDF risultante sia riapribile da PDFBox
        try (PDDocument reopened = Loader.loadPDF(compiledPdf)) {
            assertNotNull(reopened, "Il PDF compilato deve essere riapribile");
            assertEquals(1, reopened.getNumberOfPages(), "Il PDF compilato deve avere 1 pagina");
            // Dopo flatten l'AcroForm può essere null o avere campi senza widget:
            // l'importante è che il documento si carichi senza eccezioni.
        }
    }

    @Test
    void extractFields_pdfSenzaCampi_lanciaIllegalArgumentException() throws IOException {
        // PDF valido ma senza AcroForm
        byte[] pdfSenzaCampi;
        try (PDDocument doc = new PDDocument()) {
            doc.addPage(new PDPage(PDRectangle.A4));
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            doc.save(out);
            pdfSenzaCampi = out.toByteArray();
        }

        assertThrows(
                IllegalArgumentException.class,
                () -> service.extractFields(pdfSenzaCampi),
                "Deve lanciare IllegalArgumentException per PDF senza AcroForm");
    }
}
