# Deliverable 3 — Autonomia & Limiti

Hackathon Accenture · 14 settembre 2026 · Tema 01 — Accessibilità Digitale

---

## Cosa guadagna Anna

Il guadagno è misurabile in modo netto.

**Prima:** Anna apre il modulo, non capisce i campi, si blocca. La pratica non parte. Per completarla deve dipendere da qualcuno — un figlio, un patronato, un CAF. Questo ha un costo in tempo, in disponibilità degli altri, in dignità.

**Dopo:** Anna carica il PDF, risponde alle domande una alla volta, scarica il modulo compilato. La pratica è fatta. Da sola.

Il guadagno non è una migliore esperienza visiva del modulo, né una spiegazione più chiara di cosa sia il cambio di residenza. Il guadagno è concreto: **una pratica che prima non partiva ora arriva in fondo**.

L'autonomia acquistata non dipende dall'aiuto di nessuno. Non richiede un appuntamento, non richiede di aspettare che qualcuno sia disponibile, non richiede di spiegare la propria situazione a uno sportellista.

---

## Cosa il sistema non fa

Dichiarare i limiti non è una debolezza: è la condizione per usare il sistema in modo consapevole.

**Il sistema non verifica la correttezza dei dati inseriti.**
Se Anna scrive un indirizzo sbagliato, o un codice fiscale errato, il sistema non lo rileva. Inserisce nei campi del PDF quello che Anna ha scritto. La verifica dell'accuratezza delle informazioni è responsabilità di Anna.

**Il sistema non dà consulenza sulla pratica.**
Non spiega cosa succede dopo aver presentato il modulo, non indica i tempi di risposta del Comune, non chiarisce se Anna abbia diritto a presentare quella specifica dichiarazione. Non è un patronato digitale.

**Il sistema non interpreta la situazione personale di Anna.**
Se Anna è in una situazione particolare — ad esempio, un cambio di residenza che coinvolge più persone, o una situazione di affitto complessa — il sistema non lo sa e non lo gestisce. Risponde ai campi del modulo, non alla situazione.

**La responsabilità di quanto dichiarato resta di Anna.**
Il modulo compilato è un documento ufficiale che Anna firmerà e presenterà a un ente pubblico. Il sistema aiuta la compilazione, non assume nessuna responsabilità sulle dichiarazioni.

---

## Il vincolo "semplificare senza tradire"

La traccia pone un vincolo esplicito: semplificare il linguaggio non deve alterare il significato dell'informazione richiesta.

Il sistema rispetta questo vincolo in modo strutturale: **il testo originale del campo è sempre visibile**, in ogni schermata, sopra la domanda semplificata.

```
┌─────────────────────────────────────────────────────┐
│  Campo del modulo:                                  │
│  Luogo di iscrizione anagrafica                     │
│─────────────────────────────────────────────────────│
│                                                     │
│  In quale città sei iscritta all'anagrafe?          │
│                                                     │
│  [ ___________________________________ ]            │
│                                                     │
│                              [ Avanti → ]           │
└─────────────────────────────────────────────────────┘
```

Anna vede entrambe le versioni. La domanda semplificata la aiuta a capire cosa scrivere. Il campo originale rimane visibile perché è quello che apparirà nel documento ufficiale — e Anna ha il diritto di saperlo.

Se la domanda semplificata non le fosse sufficiente, o se volesse confrontare le due formulazioni per essere sicura, può farlo in ogni momento. Non si nasconde niente.

---

## Perché questa scelta è coerente con il tema

La traccia del tema 01 è esplicita su un punto: il focus non è l'audit tecnico del codice né la conformità formale alle linee guida, **è la persona**. Il sistema è costruito intorno alla barriera concreta di Anna — il linguaggio burocratico — e la rimuove senza toglierle il controllo su ciò che sta dichiarando.

Non è un intermediario che compila al posto suo. È uno strumento che la mette in condizione di compilare lei stessa, consapevolmente, fino in fondo.
