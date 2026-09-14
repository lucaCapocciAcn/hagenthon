# Aggiornamenti dopo la chiusura dei requisiti

Righe aggiunte dal listener mentre si sviluppa: cose dette dopo che i requisiti
consolidati erano già stati scritti. Controllalo prima di implementare.

<!-- nessun aggiornamento finora -->
- **10:06** — la soluzione non deve dipendere da una sottoscrizione AI lato
  utente finale (niente account Claude/OpenAI richiesto a chi la usa)
  (righe 958-967) → REQ-001, ancora in discussione, non deciso
- **10:11** — chiarimento organizzatori a tutta la sala: l'obiettivo è usare
  l'AI per *creare* il software, non creare software che usa l'AI (righe
  972-975) → DEL-028; rende praticabile REQ-001 senza penalizzazioni
- **10:14** — lo scaffolding proposto va rispettato alla lettera, non come
  suggerimento: valuta prima un agente automatico, poi la giuria → D-001,
  rivede l'interpretazione di DEL-020. **Il CLAUDE.md non lo dice ancora.**
- **10:16** — idea definita: carica un PDF burocratico → il sistema lo legge →
  fa domande in linguaggio semplice una alla volta → restituisce il PDF
  compilato con le risposte (righe 1010-1028) → REQ-002, REQ-003.
  **In tensione con REQ-001** (niente dipendenza da AI lato utente).
- **10:17** — UI: un pulsante per caricare il documento, poi una chat guidata
  (righe 1045-1063) → REQ-004
- **10:17** — aperta: si risponde da tastiera o anche a voce? (riga 1062) → OQ-001
- **10:19** — stack: frontend in **Angular** (lettura incerta: la trascrizione
  dice "inoculare"/"in angola"); criterio dichiarato = usare ciò in cui si è
  più confidenti per poter correggere a mano (righe 1104-1123) → D-002,
  DA CONFERMARE
- **10:20** — consolidati obiettivo e caso d'uso in `docs/caso-uso.md`
  (persona, percorso, limiti, nodi aperti) → REQ-002..004, D-001, D-002
- **10:31** — backend **Spring**, più un harness di skill/agenti dedicati a
  strutturare il progetto Spring (righe 1130-1139) → D-003; rafforza D-002
  (Angular frontend)
- **10:36** — il repository va **pubblico**, così gli organizzatori possono
  accedervi; è un esercizio, nessun vincolo di sicurezza (righe 1176-1186)
  → DEL-029
- **10:37** — il repo si può cancellare dopo che gli organizzatori hanno
  scaricato il progetto (righe 1187-1190) → integra DEL-029
