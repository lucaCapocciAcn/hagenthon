package com.hagenthon.uncampoallavolta.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.hagenthon.uncampoallavolta.dto.AnswersRequest;
import com.hagenthon.uncampoallavolta.dto.QuestionDto;
import com.hagenthon.uncampoallavolta.dto.UploadResponse;
import com.hagenthon.uncampoallavolta.model.FormSession;

/**
 * Servizio di orchestrazione principale.
 *
 * Coordina PdfFormService e QuestionGenerator e gestisce lo stato
 * di sessione in memoria. Nessuna persistenza: i dati vivono finché
 * il server è in esecuzione.
 */
@Service
public class FormService {

    private final PdfFormService pdfFormService;
    private final QuestionGenerator questionGenerator;

    /** Stato di sessione in memoria: sessionId → FormSession */
    private final Map<String, FormSession> sessions = new ConcurrentHashMap<>();

    public FormService(PdfFormService pdfFormService, QuestionGenerator questionGenerator) {
        this.pdfFormService = pdfFormService;
        this.questionGenerator = questionGenerator;
    }

    /**
     * Elabora il PDF caricato: estrae i campi, genera le domande semplificate,
     * salva la sessione in memoria e restituisce la risposta per il frontend.
     *
     * @param file PDF caricato dall'utente
     * @return UploadResponse con sessionId e lista di domande
     * @throws IOException se il PDF non è leggibile
     */
    public UploadResponse processUpload(MultipartFile file) throws IOException {
        byte[] pdfBytes = file.getBytes();

        // 1. Estrai i campi dal PDF (logica reale in PdfFormService)
        List<QuestionDto> rawFields = pdfFormService.extractFields(pdfBytes);

        // 2. Genera le domande in linguaggio semplice (logica reale in QuestionGenerator)
        List<QuestionDto> questions = questionGenerator.generateQuestions(rawFields);

        // 3. Crea e memorizza la sessione
        String sessionId = UUID.randomUUID().toString();
        sessions.put(sessionId, new FormSession(pdfBytes, questions));

        return new UploadResponse(sessionId, questions);
    }

    /**
     * Recupera la sessione, compila il PDF con le risposte e restituisce i byte
     * del documento compilato.
     *
     * @param sessionId id della sessione creata al momento dell'upload
     * @param request   corpo JSON con la mappa fieldName → valore
     * @return byte del PDF compilato
     * @throws IOException              se la scrittura del PDF fallisce
     * @throws IllegalArgumentException se la sessione non esiste
     */
    public byte[] compileAnswers(String sessionId, AnswersRequest request) throws IOException {
        FormSession session = sessions.get(sessionId);
        if (session == null) {
            throw new IllegalArgumentException("Sessione non trovata: " + sessionId);
        }

        byte[] compiledPdf = pdfFormService.fillForm(session.getOriginalPdfBytes(), request.answers());

        // Rimuoviamo la sessione dopo l'uso — il PDF è stato prodotto
        sessions.remove(sessionId);

        return compiledPdf;
    }
}
