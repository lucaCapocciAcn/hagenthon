package com.hagenthon.uncampoallavolta.controller;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.hagenthon.uncampoallavolta.dto.AnswersRequest;
import com.hagenthon.uncampoallavolta.dto.UploadResponse;
import com.hagenthon.uncampoallavolta.service.FormService;

/**
 * Controller REST per le operazioni sul modulo PDF.
 *
 * ---------------------------------------------------------------------------
 * ESEMPI CURL
 *
 * 1) Carica il PDF e ottieni le domande:
 *
 *    curl -s -X POST http://localhost:8080/api/forms/upload \
 *         -F "file=@/path/to/modulo.pdf" | jq .
 *
 *    Risposta esempio:
 *    {
 *      "sessionId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
 *      "questions": [
 *        { "fieldName": "cognome_nome", "originalLabel": "Cognome e Nome",
 *          "simpleQuestion": "Come ti chiami (cognome e nome)?" },
 *        ...
 *      ]
 *    }
 *
 * 2) Invia le risposte e scarica il PDF compilato:
 *
 *    curl -s -X POST \
 *         http://localhost:8080/api/forms/3fa85f64-5717-4562-b3fc-2c963f66afa6/answers \
 *         -H "Content-Type: application/json" \
 *         -d '{"answers": {"cognome_nome": "Rossi Mario", "data_nascita": "01/01/1950", "codice_fiscale": "RSSMRA50A01H501W"}}' \
 *         --output modulo_compilato.pdf
 * ---------------------------------------------------------------------------
 */
@RestController
@RequestMapping("/api/forms")
public class FormController {

    private final FormService formService;

    public FormController(FormService formService) {
        this.formService = formService;
    }

    /**
     * Endpoint 1 — Upload del PDF.
     *
     * Riceve il file via multipart, estrae i campi AcroForm e restituisce
     * le domande in linguaggio semplice insieme all'id di sessione.
     *
     * @param file PDF da compilare caricato dall'utente
     * @return 200 OK con body {@link UploadResponse}
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UploadResponse> upload(@RequestParam("file") MultipartFile file) throws IOException {
        UploadResponse response = formService.processUpload(file);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint 2 — Invio risposte e download PDF compilato.
     *
     * Riceve le risposte in JSON, compila il PDF in memoria e lo restituisce
     * come attachment da scaricare.
     *
     * @param sessionId id della sessione ottenuto da /upload
     * @param request   corpo JSON con mappa fieldName → valore
     * @return 200 OK con body application/pdf e header Content-Disposition
     */
    @PostMapping(value = "/{sessionId}/answers", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<byte[]> submitAnswers(@PathVariable String sessionId, @RequestBody AnswersRequest request)
            throws IOException {

        byte[] compiledPdf = formService.compileAnswers(sessionId, request);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "modulo_compilato.pdf");
        headers.setContentLength(compiledPdf.length);

        return ResponseEntity.ok().headers(headers).body(compiledPdf);
    }
}
