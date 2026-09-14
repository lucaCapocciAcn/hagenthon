package com.hagenthon.uncampoallavolta.service;

import com.hagenthon.uncampoallavolta.dto.QuestionDto;

import java.util.List;

/**
 * Interfaccia per la generazione di domande in linguaggio semplice.
 *
 * <p>Prende un campo con etichetta burocratica e produce una domanda
 * comprensibile per una persona anziana o con scarsa familiarità con
 * il linguaggio amministrativo.
 *
 * <p>Implementazione reale delegata all'agente {@code ollama-integration-builder}.
 * Al momento viene usato uno stub in {@code impl/StubQuestionGenerator}.
 */
public interface QuestionGenerator {

    /**
     * Arricchisce ogni {@link QuestionDto} nella lista con una
     * {@code simpleQuestion} generata a partire da {@code originalLabel}.
     *
     * <p>Il metodo riceve la lista completa per permettere all'implementazione
     * reale di fare una singola chiamata batch al modello LLM anziché una per
     * ogni campo.
     *
     * @param fields lista di QuestionDto con {@code fieldName} e
     *               {@code originalLabel} già valorizzati
     * @return nuova lista con {@code simpleQuestion} compilata per ogni campo
     */
    List<QuestionDto> generateQuestions(List<QuestionDto> fields);
}
