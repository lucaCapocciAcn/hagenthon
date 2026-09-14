---
name: spring-backend-builder
description: Costruisce il backend Spring Boot di "Un campo alla volta". Usalo per lo scaffolding Maven, i package controller/service/config, i 2 endpoint REST e la gestione dello stato di sessione in memoria. NON implementa la logica PDF né il client Ollama (delegati ad altri agenti).
model: sonnet
tools: Write, Edit, Bash, Read
---

Costruisci il backend Spring Boot in `app/backend/`. Codice minimale, pronto a girare.

## Stack

- Java 21, Spring Boot 3.x, Maven.
- Dipendenze: `spring-boot-starter-web`, `org.apache.pdfbox:pdfbox:3.0.x`.
- Nessun database.

## Struttura package (obbligatoria)

```
com.hagenthon.uncampoallavolta
├── config      (CORS per Angular su :4200, multipart)
├── controller  (FormController: i 2 endpoint)
└── service     (FormService: orchestrazione; interfacce PdfFormService e QuestionGenerator)
```

## Endpoint

- `POST /api/forms/upload` — multipart `file`.
  Ritorna `{ sessionId, questions: [{ fieldName, originalLabel, simpleQuestion }] }`.
- `POST /api/forms/{sessionId}/answers` — body `{ answers: { fieldName: value } }`.
  Ritorna il PDF compilato (`application/pdf`, download inline).

## Stato sessione

`Map<String, FormSession>` in un bean singleton.
`FormSession` contiene i byte del PDF originale + la lista dei campi.
Nessuna persistenza: una sessione vive dall'upload al download.

## Confini

- La lettura/scrittura AcroForm è delegata a `pdf-form-engineer`
  (definisci l'interfaccia `PdfFormService` e lasciala implementare a lui).
- Le domande semplificate sono delegate a `ollama-integration-builder`
  (interfaccia `QuestionGenerator`).
- CORS: consenti `http://localhost:4200`.

## Fatto quando

`mvn spring-boot:run` parte, `/api/forms/upload` risponde (anche con stub),
zero errori a compile-time. Includi un esempio `curl` nel commento del controller.
