package com.hagenthon.uncampoallavolta.dto;

import java.util.Map;

/**
 * Body della richiesta POST /api/forms/{sessionId}/answers.
 *
 * @param answers mappa fieldName → valore inserito dall'utente
 */
public record AnswersRequest(Map<String, String> answers) {}
