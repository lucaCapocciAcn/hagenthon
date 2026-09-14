# Regole Spring Boot

Da leggere prima di toccare `app/backend/`. Java 21, Maven, Spring Boot 3.5.x.

Nota onesta: **per Spring non esiste un file di best practice ufficiale per LLM**
(a differenza di Angular). Queste regole sono scritte a mano per questo progetto.

## Struttura dei package

`com.hagenthon.uncampoallavolta` con: `controller` · `service` (+ `service.impl`)
· `dto` · `model` · `config`. Non aggiungere layer: l'app ha 2 endpoint, un
repository pattern qui sarebbe cerimonia senza guadagno.

## Dipendenze e costruzione

- **Constructor injection**, sempre. Niente `@Autowired` sui campi: rende la
  classe non istanziabile nei test e nasconde le dipendenze.
- Un solo costruttore → l'annotazione non serve, Spring lo capisce da solo.
- Campi `private final`.

## Interfaccia e implementazione

Questo repo separa `service/Xxx` (interfaccia) da `service/impl/XxxImpl`
**solo dove serve davvero**: `QuestionGenerator` ha un'implementazione Ollama e
un fallback, quindi l'interfaccia guadagna qualcosa. Non creare interfacce con
una sola implementazione e nessuna prospettiva di averne due.

## Controller

- Sottili: validano l'input, delegano al service, mappano la risposta. **Zero
  logica di dominio.**
- DTO dedicati in entrata e in uscita — mai esporre le entità interne.
- `record` per i DTO: immutabili e concisi.
- Errori via `@RestControllerAdvice` centralizzato, non try/catch sparsi nei
  controller. Il messaggio che arriva al frontend deve essere **leggibile da una
  persona anziana**, non uno stack trace.

## Configurazione

- Valori in `application.properties`, mai hardcoded. Già così per Ollama
  (`ollama.model`, `ollama.base-url`).
- `@ConfigurationProperties` con un record quando i valori correlati sono più di
  due; `@Value` solo per il caso singolo.
- Niente segreti nel repo.

## Stato di sessione

Lo stato vive in una `ConcurrentHashMap` in un singleton — **è una scelta presa**,
non una mancanza: niente DB, niente persistenza oltre la sessione. Non
introdurre JPA, Redis o un database senza che sia stato deciso esplicitamente.
Se la mappa cresce senza limite è un problema reale: aprine una issue, non
risolverlo di nascosto cambiando architettura.

## Resilienza — la regola che conta di più qui

**Il flusso non si blocca mai.** Se Ollama non risponde, il sistema ripiega
sull'etichetta originale del campo e la persona può comunque finire il modulo.
Qualsiasi integrazione esterna che aggiungi segue la stessa regola: timeout
esplicito, fallback definito, nessuna eccezione che arriva all'utente.

## PDF

I pattern PDFBox 3.x (lettura AcroForm, compilazione, flatten) stanno nella skill
**`pdf-acroform-toolkit`**. Caricala invece di riscoprirli: PDFBox 3 ha cambiato
API rispetto alla 2 e gli esempi che trovi online sono quasi tutti per la 2.

## Test

- JUnit 5 + AssertJ. `@SpringBootTest` **solo** quando serve davvero il contesto:
  per la logica pura basta un unit test, che parte in millisecondi invece che in
  secondi.
- `@MockBean` e `@SpyBean` sono deprecati e spariscono in Spring Boot 4: usa
  `@MockitoBean` / `@MockitoSpyBean`.
- I test PDF girano sui file in `app/samples/`: sono fixture, non toccarli per
  far passare un test.
- Un test che non fallirebbe mai non è un test. Prima di scriverlo, chiediti
  quale bug lo farebbe diventare rosso.

## Qualità

- `mvn spotless:apply` per formattare, `mvn verify` prima di aprire una PR
  (Spotless in modalità check è agganciato a `verify`: build rossa se il formato
  è sbagliato).
- Non disabilitare una regola per far passare la build: aggiusta il codice o
  chiedi.
