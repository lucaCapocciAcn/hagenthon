# T004 — Prototipo UX: come appare la schermata di Anna
label: wayfinder:prototype
status: open
parent: MAP-001
blocks: T005

## Question

Anna usa lo schermo: caratteri grandi, layout semplice, nessuna sovraccarico
cognitivo. Come appare la schermata principale?

Da validare:
- Font size minimo: 20px body, 28px+ domanda principale
- Un campo per schermata (full-viewport focus)
- Riquadro "testo originale del modulo" sempre visibile ma secondario (grigio chiaro, font più piccolo)
- Pulsante "Avanti" grande, colore contrastato (#0056a6 Accenture o simile)
- Indicatore progresso: "Campo 3 di 12" in cima
- Nessun menu, nessuna navigazione laterale

Mockup ASCII da validare:

```
┌─────────────────────────────────────────────────────┐
│  Campo 3 di 12                            [■■□□□□□□] │
│                                                      │
│                                                      │
│   ┌──────────────────────────────────────────────┐   │
│   │                                              │   │
│   │  Come si chiama la via dove                  │   │
│   │  vai ad abitare?                             │   │
│   │                                              │   │
│   │  ___________________________________         │   │
│   │                                              │   │
│   └──────────────────────────────────────────────┘   │
│                                                      │
│   ┌──────────────────────────────────────────────┐   │
│   │ Testo del modulo                             │   │
│   │ "Indirizzo di nuova residenza"               │   │
│   └──────────────────────────────────────────────┘   │
│                                                      │
│              [ Avanti → ]                            │
│                                                      │
└─────────────────────────────────────────────────────┘
```

Decisione: questo layout va bene, oppure c'è da cambiare qualcosa prima di
scaffoldare il componente Angular?

## Resolution

_(HITL — prototipo da approvare dal team)_
