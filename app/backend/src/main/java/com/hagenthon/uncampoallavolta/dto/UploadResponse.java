package com.hagenthon.uncampoallavolta.dto;

import java.util.List;

/**
 * Risposta all'endpoint POST /api/forms/upload.
 *
 * @param sessionId identificatore univoco della sessione in memoria
 * @param questions lista dei campi con domande semplificate
 */
public record UploadResponse(String sessionId, List<QuestionDto> questions) {}
