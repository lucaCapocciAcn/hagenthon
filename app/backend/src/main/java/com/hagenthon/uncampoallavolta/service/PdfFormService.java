package com.hagenthon.uncampoallavolta.service;

import com.hagenthon.uncampoallavolta.dto.QuestionDto;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Interfaccia per le operazioni sul PDF AcroForm.
 *
 * <p>Implementazione reale delegata all'agente {@code pdf-form-engineer}.
 * Al momento viene usato uno stub in {@code impl/StubPdfFormService}.
 */
public interface PdfFormService {

    /**
     * Estrae i campi AcroForm dal PDF e li restituisce come lista.
     * Ogni elemento contiene il nome tecnico del campo e l'etichetta originale;
     * {@code simpleQuestion} sarà poi riempita da {@link QuestionGenerator}.
     *
     * @param pdfBytes byte del PDF originale caricato dall'utente
     * @return lista di {@link QuestionDto} con {@code fieldName} e
     *         {@code originalLabel} valorizzati; {@code simpleQuestion} vuota
     * @throws IOException se il PDF non è leggibile
     */
    List<QuestionDto> extractFields(byte[] pdfBytes) throws IOException;

    /**
     * Compila i campi AcroForm del PDF con le risposte fornite dall'utente
     * e restituisce il PDF compilato come array di byte.
     *
     * @param pdfBytes byte del PDF originale
     * @param answers  mappa {@code fieldName → valore} da scrivere nel PDF
     * @return PDF compilato come array di byte (pronto per il download)
     * @throws IOException se il PDF non è scrivibile
     */
    byte[] fillForm(byte[] pdfBytes, Map<String, String> answers) throws IOException;
}
