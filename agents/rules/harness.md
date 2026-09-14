# Regole per estendere l'harness

Riguarda `agents/` e `.claude/`: agenti, skill, regole, hook, command.

## Dove vivono le cose, e perché

`agents/` è **autoritativo**; `.claude/` ci punta con symlink
(`.claude/agents → ../agents/subagents`, `.claude/skills → ../agents/skills`,
`.claude/commands → ../agents/commands`). Un solo posto da leggere per chi
valuta il repo, e Claude Code trova comunque tutto dove si aspetta.
L'unico file reale dentro `.claude/` è `settings.json`, perché deve stare lì.

## Il vincolo che governa tutto: il costo in token

Ogni skill, regola e plugin abilitato è contesto che entra in **ogni** sessione.
Quindi:

- **Una regola si scrive solo se qualcuno la carica.** Se non è referenziata da
  `CLAUDE.md` o da un subagent, è peso morto: non scriverla, o cancellala.
- **Caricamento su richiesta, non a monte.** `CLAUDE.md` dice *quando* leggere
  una regola, non ne incolla il contenuto. Chi tocca solo il backend non deve
  pagare le regole Angular.
- **I plugin abilitati sono 3, scelti fra i 21 disponibili.** Aggiungerne uno è
  una decisione con un costo, non un default. Motivazione in `agents/README.md`.
- **File corti.** Se una regola supera ~100 righe, quasi sempre sta mescolando
  due argomenti: spezzala.

## Aggiungere un subagent

Serve solo se esiste una responsabilità che **non si sovrappone** a quelle già
presenti. Prima di crearne uno, verifica che il lavoro non sia già coperto: sei
agenti che si somigliano sono peggio di tre distinti.

Dichiara sempre `description` (quando usarlo), `tools` (il minimo necessario) e
`model` (il modello più economico che regge il compito — vedi la tabella in
`agents/README.md`).

## Aggiungere un hook

Un hook è codice che gira **sempre**, anche sulla macchina di chi valuta.
Quindi: esce `0` quando non ha niente da fare, non assume che `jq`, `mvn` o
`prettier` esistano, e ha un `timeout`. Se un hook può bloccare il lavoro per
una dipendenza mancante in locale, è scritto male.

**Provalo prima di dichiararlo verde**, passandogli su stdin il JSON che
riceverebbe davvero:

```bash
./agents/hooks/test-hooks.sh      # 21 casi: blocchi veri, falsi positivi, timeout, orfani
```

Se aggiungi un hook, **aggiungi i suoi casi lì dentro**. Un hook è codice che
nessun compilatore guarda: senza test, un bug ci resta finché non fa danno.

## Prima di consegnare

`/harness-check` — verifica symlink, hook eseguibili, regole orfane e label.
