package com.hagenthon.uncampoallavolta.model;

import java.util.List;

import com.hagenthon.uncampoallavolta.dto.QuestionDto;

/**
 * Stato di sessione tenuto in memoria (nessuna persistenza).
 * Contiene i byte del PDF originale e la lista dei campi rilevati.
 */
public class FormSession {

    private final byte[] originalPdfBytes;
    private final List<QuestionDto> fields;

    public FormSession(byte[] originalPdfBytes, List<QuestionDto> fields) {
        this.originalPdfBytes = originalPdfBytes;
        this.fields = fields;
    }

    public byte[] getOriginalPdfBytes() {
        return originalPdfBytes;
    }

    public List<QuestionDto> getFields() {
        return fields;
    }
}
