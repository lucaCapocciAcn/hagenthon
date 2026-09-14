package com.hagenthon.uncampoallavolta.service.impl;

import com.hagenthon.uncampoallavolta.dto.QuestionDto;
import com.hagenthon.uncampoallavolta.service.QuestionGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Implementazione reale di {@link QuestionGenerator} che chiama Ollama
 * (modello qwen2.5:7b) per trasformare etichette burocratiche in domande
 * in italiano semplice.
 *
 * <p>Prompt validato dalla skill {@code ollama-italian-prompting}.
 *
 * <p>Robustezza: timeout 30 s; se l'output è invalido → re-prompt; se
 * fallisce ancora o Ollama è irraggiungibile → fallback sull'originalLabel,
 * il flusso non viene mai interrotto.
 */
@Service
public class OllamaQuestionGenerator implements QuestionGenerator {

    private static final Logger log = LoggerFactory.getLogger(OllamaQuestionGenerator.class);

    /** System prompt — skill ollama-italian-prompting (validato). */
    private static final String SYSTEM_MSG =
            "Sei un assistente che semplifica il linguaggio burocratico italiano. " +
            "Rispondi sempre e solo in italiano.";

    /** User prompt — skill ollama-italian-prompting (validato). */
    private static final String USER_TEMPLATE =
            "Il modulo contiene il campo: \"%s\".\n" +
            "Scrivi UNA sola domanda, in italiano semplice, per una persona di 70 anni " +
            "che non usa spesso il computer.\n" +
            "Massimo 15 parole. Niente spiegazioni, solo la domanda.";

    private static final int MAX_WORDS = 18;

    private final RestClient restClient;
    private final String model;

    public OllamaQuestionGenerator(
            @Value("${ollama.base-url:http://localhost:11434}") String baseUrl,
            @Value("${ollama.model:qwen2.5:7b}") String model) {

        this.model = model;

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(30_000);  // 30 s — messaggio chiaro nei log se Ollama non risponde
        factory.setReadTimeout(30_000);

        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(factory)
                .build();
    }

    // -------------------------------------------------------------------------
    // Interfaccia pubblica
    // -------------------------------------------------------------------------

    @Override
    public List<QuestionDto> generateQuestions(List<QuestionDto> fields) {
        List<QuestionDto> result = new ArrayList<>(fields.size());
        for (QuestionDto field : fields) {
            String question = generateForLabel(field.originalLabel());
            result.add(new QuestionDto(field.fieldName(), field.originalLabel(), question));
        }
        return result;
    }

    // -------------------------------------------------------------------------
    // Logica interna
    // -------------------------------------------------------------------------

    /**
     * Genera la domanda per una singola etichetta. Ritorna sempre una stringa
     * non nulla: in caso di errore o output invalido dopo il re-prompt, ritorna
     * l'originalLabel così com'è.
     */
    private String generateForLabel(String originalLabel) {
        try {
            String first = callOllama(originalLabel);
            if (isValid(first)) {
                return sanitize(first);
            }
            log.warn("QuestionGenerator: output non valido (tentativo 1) per '{}' — re-prompt", originalLabel);

            String second = callOllama(originalLabel);
            if (isValid(second)) {
                return sanitize(second);
            }
            log.warn("QuestionGenerator: output non valido (tentativo 2) per '{}' — uso fallback", originalLabel);

        } catch (Exception e) {
            // Ollama irraggiungibile, timeout o risposta malformata
            log.error("QuestionGenerator: impossibile contattare Ollama per '{}': {} — uso fallback",
                    originalLabel, e.getMessage());
        }

        // Fallback: mai bloccare il flusso
        return originalLabel;
    }

    /**
     * Esegue la chiamata HTTP a Ollama e ritorna il testo grezzo del campo
     * {@code message.content} della risposta.
     */
    @SuppressWarnings("unchecked")
    private String callOllama(String originalLabel) {
        String userContent = String.format(USER_TEMPLATE, originalLabel);

        Map<String, Object> payload = Map.of(
                "model", model,
                "stream", false,
                "options", Map.of("temperature", 0.3, "num_predict", 60),
                "messages", List.of(
                        Map.of("role", "system", "content", SYSTEM_MSG),
                        Map.of("role", "user", "content", userContent)
                )
        );

        Map<String, Object> response = restClient.post()
                .uri("/api/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .body(payload)
                .retrieve()
                .body(Map.class);

        if (response == null) return "";

        Object messageObj = response.get("message");
        if (!(messageObj instanceof Map<?, ?> message)) return "";

        Object contentObj = message.get("content");
        if (contentObj == null) return "";

        return contentObj.toString().trim();
    }

    /**
     * Ritorna {@code true} se l'output è non vuoto e ≤ {@value MAX_WORDS} parole.
     */
    private boolean isValid(String content) {
        if (content == null || content.isBlank()) return false;
        return content.split("\\s+").length <= MAX_WORDS;
    }

    /**
     * Rimuove eventuali virgolette (doppie o singole) attorno alla domanda.
     */
    private String sanitize(String content) {
        return content.trim()
                .replaceAll("^\"|\"$", "")
                .replaceAll("^'|'$", "")
                .trim();
    }
}
