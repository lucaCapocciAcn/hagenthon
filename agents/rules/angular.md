# Regole Angular

Da leggere prima di toccare `app/frontend/`. Vale per **Angular 21**, la versione
di questo repo (`app/frontend/package.json` è la fonte di verità: se diverge da
qui, ha ragione il package.json e questa regola va corretta).

Base: il file ufficiale Angular per LLM,
<https://angular.dev/assets/context/best-practices.md>, **adattato alla v21** —
tre sue regole valgono solo da v22 e qui sarebbero sbagliate (vedi ultima sezione).

## TypeScript

- Type checking strict. Niente `any`: se il tipo è incerto usa `unknown`.
- Lascia inferire il tipo quando è ovvio; annota quando chiarisce.

## Componenti

- **Standalone sempre.** Non scrivere `standalone: true`: è il default da v20,
  metterlo è rumore.
- **`changeDetection: ChangeDetectionStrategy.OnPush` va messo esplicitamente.**
  In v21 non è ancora il default (lo diventa in v22).
- `input()` / `output()` come funzioni, **non** i decoratori `@Input`/`@Output`.
  `model()` per il two-way binding `[(prop)]`.
- Niente `@HostBinding` / `@HostListener`: usa l'oggetto `host` nel decoratore.
- Componenti piccoli, una responsabilità sola. Template inline per i componenti
  brevi; se usi file esterni, path relativi al file TS.
- Non importare `CommonModule`: importa solo ciò che il template usa davvero
  (`AsyncPipe`, `DatePipe`, …).

## Stato

- **Signals** per lo stato locale; `computed()` per lo stato derivato;
  `linkedSignal()` quando deriva da più sorgenti che devono restare allineate.
- Su un signal usa `set()` o `update()`. **Mai `mutate()`.**
- Trasformazioni pure e prevedibili.

## Template

- **Control flow nativo** `@if` / `@for` / `@switch`. Mai `*ngIf` / `*ngFor` /
  `*ngSwitch`.
- Niente `ngClass` e `ngStyle`: usa i binding `[class.x]` e `[style.x]`.
- Logica fuori dal template. Niente globali come `new Date()` dentro il template.
- `NgOptimizedImage` per le immagini statiche (non funziona con base64 inline).

## Form

- **Reactive forms.** Le Signal Forms (`@angular/forms/signals`) diventano
  stabili in v22: qui non ci sono ancora, non usarle.
- Mai template-driven.

## Servizi

- `@Injectable({ providedIn: 'root' })` per i singleton.
  (Il decoratore `@Service` arriva in v22: qui non esiste.)
- **`inject()`**, non la constructor injection.
- `provideHttpClient()` in `app.config.ts` per HttpClient.

## Accessibilità — non è opzionale su questo progetto

L'app serve persone a bassa alfabetizzazione digitale: l'accessibilità **è** il
prodotto, non una rifinitura.

- Deve passare **tutti i check AXE** e rispettare i **minimi WCAG AA**: gestione
  del focus, contrasto colore, attributi ARIA.
- Le regole di dettaglio (dimensione font, un solo campo per schermata, lingua
  semplice) stanno nella skill **`accessible-ui-guidelines`**: caricala prima di
  scrivere UI, non duplicarla qui.
- Lint: il preset `templateAccessibility` di angular-eslint è attivo. Se una sua
  regola ti dà fastidio, **non disabilitarla**: chiedi.

## Cosa cambia quando passeremo a v22

Tre regole qui sopra si invertono, e vanno cambiate **insieme** all'upgrade:
`OnPush` diventa default (va tolto), le Signal Forms diventano preferite alle
Reactive, e `@Service` sostituisce `@Injectable({providedIn:'root'})`.
L'upgrade a v22 è bloccato da Node: richiede `^22.22.3`, qui c'è `22.12.0`.
