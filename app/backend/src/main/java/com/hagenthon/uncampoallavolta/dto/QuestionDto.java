package com.hagenthon.uncampoallavolta.dto;

/**
 * Rappresenta un singolo campo del modulo con la domanda semplificata
 * generata da QuestionGenerator.
 *
 * @param fieldName      nome tecnico del campo AcroForm nel PDF
 * @param originalLabel  etichetta originale (burocratica) del campo
 * @param simpleQuestion domanda in linguaggio semplice, comprensibile
 */
public record QuestionDto(
        String fieldName,
        String originalLabel,
        String simpleQuestion
) {}
