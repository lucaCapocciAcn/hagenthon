#!/usr/bin/env python3
"""Genera la presentazione di vendita di "Un campo alla volta".

Identità visiva: tema ufficiale "Accenture 2020", estratto da un deck
Accenture (`brand/tema-accenture-2020.xml`). Colori e loghi non sono
ricostruiti a mano: vengono da lì.

Disciplina cromatica — è ciò che tiene insieme il deck:
  · il viola #A100FF è SOLO un accento (filetti, occhielli, il segno ">",
    al massimo un riquadro pieno per slide). Mai viola su viola.
  · il testo è nero. Il secondario è un grigio caldo: #96968C sui fondi neri,
    #55554E sui fondi chiari — il primo su bianco starebbe sotto 2:1 di
    contrasto, e questo è un deck sull'accessibilità.
  · i fondi sono bianco, grigio caldo chiaro #E6E6DC, o nero.
  · ritmo scuro, chiaro, chiaro, ..., scuro: la chiusura richiama l'apertura.

Uso:  python genera-deck.py
Serve: python-pptx, Pillow
"""
from pptx import Presentation
from pptx.util import Inches, Pt, Emu
from pptx.dml.color import RGBColor
from pptx.enum.text import PP_ALIGN, MSO_ANCHOR
from pptx.enum.shapes import MSO_SHAPE
from PIL import Image
import os
import shutil
import subprocess
import zipfile

HERE  = os.path.dirname(os.path.abspath(__file__))
SHOTS = os.path.join(HERE, "schermate")
BRAND = os.path.join(HERE, "brand")
BUILD = os.path.join(HERE, ".build")            # intermedi, non versionati
OUT   = os.path.join(HERE, "un-campo-alla-volta.pptx")

# ── Tema "Accenture 2020" ────────────────────────────────────────────────
VIOLA      = RGBColor(0xA1, 0x00, 0xFF)   # accent1 — Accenture Purple
VIOLA_MED  = RGBColor(0x75, 0x00, 0xC0)   # accent2
VIOLA_SCUR = RGBColor(0x46, 0x00, 0x73)   # accent3
VIOLA_CHIA = RGBColor(0xBE, 0x82, 0xFF)   # accent5
VIOLA_PALE = RGBColor(0xDC, 0xAF, 0xFF)   # accent6
NERO       = RGBColor(0x00, 0x00, 0x00)   # dk1
BIANCO     = RGBColor(0xFF, 0xFF, 0xFF)   # lt1
GRIGIO     = RGBColor(0x96, 0x96, 0x8C)   # dk2 — grigio caldo: OK su nero, troppo
                                          # chiaro su bianco (~2:1)
GRIGIO_TXT = RGBColor(0x55, 0x55, 0x4E)   # stessa famiglia calda, scurito a ~6:1:
                                          # è il testo secondario sui fondi chiari
GRIGIO_CH  = RGBColor(0xE6, 0xE6, 0xDC)   # lt2 — grigio caldo chiaro

# Graphik è il font di brand. Se non è installato PowerPoint sostituisce:
# il deck resta corretto su una macchina Accenture.
FONT = "Graphik"

LOGO_CHIARO = os.path.join(BRAND, "accenture-technology-chiaro.png")  # per fondi scuri
LOGO_SCURO  = os.path.join(BRAND, "accenture-technology-scuro.png")   # per fondi chiari
SEGNO       = os.path.join(BRAND, "accenture-greater-than.png")

W, H = Inches(13.333), Inches(7.5)


def prepara_ritagli():
    """Deriva i ritagli usati nelle slide dalle schermate reali in schermate/."""
    os.makedirs(BUILD, exist_ok=True)
    pdf = os.path.join(SHOTS, "04-modulo-compilato.pdf")
    png = os.path.join(BUILD, "modulo-compilato.png")
    if not os.path.exists(png):
        subprocess.run(["qlmanage", "-t", "-s", "1600", "-o", BUILD, pdf],
                       stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL, check=False)
        prodotto = os.path.join(BUILD, "04-modulo-compilato.pdf.png")
        if os.path.exists(prodotto):
            os.replace(prodotto, png)
    if os.path.exists(png):
        im = Image.open(png)
        w, h = im.size
        im.crop((int(w * .05), int(h * .06), int(w * .80), int(h * .38))) \
          .save(os.path.join(BUILD, "pdf-compilato-crop.png"))

    up = Image.open(os.path.join(SHOTS, "01-upload.png"))
    w, h = up.size
    up.crop((0, int(h * .22), w, int(h * .80))).save(os.path.join(BUILD, "upload-crop.png"))


prs = Presentation()
prs.slide_width, prs.slide_height = W, H
BLANK = prs.slide_layouts[6]
prepara_ritagli()


# ── primitive ────────────────────────────────────────────────────────────
def slide(bg=BIANCO, scura=False):
    s = prs.slides.add_slide(BLANK)
    r = s.shapes.add_shape(MSO_SHAPE.RECTANGLE, 0, 0, W, H)
    r.fill.solid(); r.fill.fore_color.rgb = bg
    r.line.fill.background(); r.shadow.inherit = False
    s._scura = scura
    return s


def text(s, x, y, w, h, runs, align=PP_ALIGN.LEFT, anchor=MSO_ANCHOR.TOP):
    tb = s.shapes.add_textbox(x, y, w, h)
    tf = tb.text_frame
    tf.word_wrap = True
    tf.vertical_anchor = anchor
    tf.margin_left = tf.margin_right = tf.margin_top = tf.margin_bottom = 0
    for i, item in enumerate(runs):
        p = tf.paragraphs[0] if i == 0 else tf.add_paragraph()
        p.alignment = align
        p.line_spacing = item.get("ls", 1.0)
        p.space_after = Pt(item.get("sa", 0))
        r = p.add_run()
        r.text = item["t"]
        f = r.font
        f.name = FONT
        f.size = Pt(item["sz"])
        f.bold = item.get("b", False)
        f.italic = item.get("i", False)
        f.color.rgb = item.get("c", NERO)
    return tb


def pic(s, path, x, y, max_w, max_h, cornice=True):
    iw, ih = Image.open(path).size
    k = min(max_w / iw, max_h / ih)
    w, h = int(iw * k), int(ih * k)
    px, py = x + int((max_w - w) / 2), y + int((max_h - h) / 2)
    if cornice:
        bd = s.shapes.add_shape(MSO_SHAPE.RECTANGLE, px - Emu(9525), py - Emu(9525),
                                w + Emu(19050), h + Emu(19050))
        bd.fill.solid(); bd.fill.fore_color.rgb = BIANCO
        bd.line.color.rgb = GRIGIO_CH; bd.line.width = Pt(1)
        bd.shadow.inherit = False
    s.shapes.add_picture(path, px, py, w, h)


def logo(s, scura=False):
    """Wordmark Accenture Technology, misura e posizione costanti in tutto il deck."""
    path = LOGO_CHIARO if scura else LOGO_SCURO
    iw, ih = Image.open(path).size
    w = Inches(1.75)
    h = int(w * ih / iw)
    s.shapes.add_picture(path, W - w - Inches(0.72), H - h - Inches(0.5), w, h)


def segno(s, x, y, altezza):
    """Il segno ">" di Accenture, usato come accento grafico."""
    iw, ih = Image.open(SEGNO).size
    h = altezza
    w = int(h * iw / ih)
    s.shapes.add_picture(SEGNO, x, y, w, h)
    return w


def box(s, x, y, w, h, fill, linea=None):
    shp = s.shapes.add_shape(MSO_SHAPE.RECTANGLE, x, y, w, h)
    shp.fill.solid(); shp.fill.fore_color.rgb = fill
    if linea:
        shp.line.color.rgb = linea; shp.line.width = Pt(1)
    else:
        shp.line.fill.background()
    shp.shadow.inherit = False
    return shp


def filetto(s, x, y, w, colore=VIOLA, spessore=3):
    r = s.shapes.add_shape(MSO_SHAPE.RECTANGLE, x, y, w, Pt(spessore))
    r.fill.solid(); r.fill.fore_color.rgb = colore
    r.line.fill.background(); r.shadow.inherit = False


def occhiello(s, x, y, testo, colore=VIOLA):
    text(s, x, y, Inches(9), Inches(0.28),
         [{"t": testo.upper(), "sz": 11.5, "b": True, "c": colore}])


def note(s, txt):
    s.notes_slide.notes_text_frame.text = txt


MX = Inches(0.95)          # margine sinistro costante
MW = Inches(11.45)         # larghezza utile


# ── 1 · Titolo ───────────────────────────────────────────────────────────
s = slide(NERO, scura=True)
segno(s, MX, Inches(1.55), Inches(0.78))
text(s, MX, Inches(2.7), Inches(11.0), Inches(1.5),
     [{"t": "Un campo alla volta", "sz": 62, "b": True, "c": BIANCO}])
text(s, MX, Inches(4.05), Inches(9.4), Inches(1.1),
     [{"t": "Chi non capisce il linguaggio burocratico\ncompila il modulo da solo, fino in fondo.",
       "sz": 23, "c": GRIGIO_CH, "ls": 1.3}])
filetto(s, MX, Inches(5.6), Inches(1.5), VIOLA, 3)
text(s, MX, Inches(5.95), Inches(11.0), Inches(0.5),
     [{"t": "Carica il PDF   ·   Rispondi a una domanda alla volta   ·   Scarica il modulo compilato",
       "sz": 14.5, "b": True, "c": VIOLA_CHIA}])
logo(s, scura=True)
note(s, "0:00-0:15 — Titolo. Una frase sola: questo strumento non spiega il "
        "documento, lo porta a termine. Tre passi, nessuna registrazione.")

# ── 2 · Il problema ──────────────────────────────────────────────────────
s = slide(BIANCO)
occhiello(s, MX, Inches(0.82), "Il problema")
text(s, MX, Inches(1.2), MW, Inches(1.0),
     [{"t": "Il modulo non è difficile.\nÈ scritto in una lingua che non è la tua.",
       "sz": 33, "b": True, "ls": 1.15}])

box(s, MX, Inches(2.75), MW, Inches(1.45), GRIGIO_CH)
box(s, MX, Inches(2.75), Pt(4), Inches(1.45), VIOLA)
text(s, MX + Inches(0.42), Inches(3.02), Inches(10.5), Inches(0.6),
     [{"t": "«Estremi del titolo di occupazione dell'alloggio ai sensi dell'art. 5 D.L. 47/2014»",
       "sz": 20, "i": True, "ls": 1.2}])
text(s, MX + Inches(0.42), Inches(3.68), Inches(10.5), Inches(0.35),
     [{"t": "Campo reale di una dichiarazione di residenza comunale", "sz": 11.5, "c": GRIGIO_TXT}])

for i, (t1, t2) in enumerate([
    ("Si ferma prima di iniziare",
     "Non sa quale informazione le stia chiedendo, né come scriverla."),
    ("Chiede aiuto a qualcuno",
     "Un figlio, un patronato, un CAF. Ogni volta perde autonomia."),
    ("Il modulo resta bianco",
     "E con lui la pratica: la residenza, il contributo, il diritto."),
]):
    x = MX + Inches(i * 3.93)
    filetto(s, x, Inches(4.75), Inches(3.4), VIOLA, 2)
    text(s, x, Inches(5.0), Inches(3.4), Inches(0.4),
         [{"t": t1, "sz": 17.5, "b": True}])
    text(s, x, Inches(5.52), Inches(3.4), Inches(1.0),
         [{"t": t2, "sz": 13.5, "c": GRIGIO_TXT, "ls": 1.3}])
logo(s)
note(s, "0:15-0:45 — Il problema non è la persona, è il linguaggio. Leggere ad alta "
        "voce l'etichetta: nessuno in sala sa cosa chiede, ed è un campo vero di un "
        "modulo comunale. L'esito è sempre lo stesso: o chiede aiuto, o rinuncia.")

# ── 3 · La soluzione ─────────────────────────────────────────────────────
s = slide(BIANCO)
occhiello(s, MX, Inches(0.82), "La soluzione")
text(s, MX, Inches(1.2), Inches(6.8), Inches(1.5),
     [{"t": "Una domanda alla volta,\nin italiano semplice.", "sz": 33, "b": True, "ls": 1.15}])
for i, (n, t1, t2) in enumerate([
    ("1", "Carica il PDF", "Un solo pulsante. Nessuna registrazione, nessuna configurazione."),
    ("2", "Rispondi", "Una domanda per schermata, in linguaggio comune."),
    ("3", "Scarica il modulo", "Il PDF compilato, pronto da stampare o inviare."),
]):
    y = Inches(2.85 + i * 1.3)
    box(s, MX, y, Inches(0.58), Inches(0.58), VIOLA)
    text(s, MX, y + Inches(0.115), Inches(0.58), Inches(0.4),
         [{"t": n, "sz": 19, "b": True, "c": BIANCO}], align=PP_ALIGN.CENTER)
    text(s, MX + Inches(0.85), y, Inches(5.4), Inches(0.4),
         [{"t": t1, "sz": 19, "b": True}])
    text(s, MX + Inches(0.85), y + Inches(0.46), Inches(5.4), Inches(0.6),
         [{"t": t2, "sz": 13, "c": GRIGIO_TXT, "ls": 1.25}])
pic(s, f"{BUILD}/upload-crop.png", Inches(7.75), Inches(2.2), Inches(4.7), Inches(3.5))
logo(s)
note(s, "0:45-1:15 — Tre passi. L'interfaccia è un pulsante: niente account, niente "
        "setup. Poi una domanda per schermata, testo grande, un solo campo. "
        "Alla fine il documento pronto.")

# ── 4 · Prima / dopo ─────────────────────────────────────────────────────
s = slide(BIANCO)
occhiello(s, MX, Inches(0.8), "Il cuore del prodotto")
text(s, MX, Inches(1.15), MW, Inches(0.7),
     [{"t": "Semplificare senza tradire il significato", "sz": 33, "b": True}])

box(s, MX, Inches(2.1), Inches(5.2), Inches(1.7), GRIGIO_CH)
text(s, MX + Inches(0.38), Inches(2.35), Inches(4.5), Inches(0.3),
     [{"t": "TESTO DEL MODULO", "sz": 10.5, "b": True, "c": GRIGIO_TXT}])
text(s, MX + Inches(0.38), Inches(2.75), Inches(4.5), Inches(0.9),
     [{"t": "Indirizzo di nuova dimora abituale — Via/Piazza", "sz": 16.5, "ls": 1.2}])

segno(s, Inches(6.55), Inches(2.78), Inches(0.34))

box(s, Inches(7.3), Inches(2.1), Inches(5.1), Inches(1.7), VIOLA)
text(s, Inches(7.68), Inches(2.35), Inches(4.4), Inches(0.3),
     [{"t": "LA DOMANDA CHE LEGGE LA PERSONA", "sz": 10.5, "b": True, "c": VIOLA_PALE}])
text(s, Inches(7.68), Inches(2.75), Inches(4.4), Inches(0.9),
     [{"t": "Qual è l'indirizzo della tua nuova casa?", "sz": 18.5, "b": True,
       "c": BIANCO, "ls": 1.2}])

text(s, MX, Inches(4.25), Inches(5.2), Inches(2.0),
     [{"t": "Il testo originale resta sempre a schermo.", "sz": 16.5, "b": True, "sa": 8},
      {"t": "La domanda semplice non sostituisce il modulo: lo affianca. Su un "
            "documento con valore legale, una semplificazione che altera il senso "
            "produce una dichiarazione sbagliata — e la responsabilità resta di "
            "chi firma.", "sz": 13, "c": GRIGIO_TXT, "ls": 1.35}])
pic(s, f"{SHOTS}/03-domanda-indirizzo.png", Inches(6.95), Inches(4.1), Inches(5.45), Inches(2.6))
logo(s)
note(s, "1:15-1:50 — La slide che conta. A sinistra il campo com'è scritto nel modulo, "
        "a destra quello che legge la persona. Il testo originale non sparisce mai: "
        "resta sotto, sempre visibile. Non riscriviamo il documento, facciamo da ponte.")

# ── 5 · Il risultato ─────────────────────────────────────────────────────
s = slide(BIANCO)
occhiello(s, MX, Inches(0.82), "Il risultato")
text(s, MX, Inches(1.2), Inches(5.9), Inches(1.4),
     [{"t": "Non una spiegazione.\nIl documento finito.", "sz": 33, "b": True, "ls": 1.15}])
text(s, MX, Inches(2.85), Inches(5.3), Inches(1.2),
     [{"t": "Le risposte finiscono nei campi giusti del PDF originale, che viene "
            "restituito compilato e pronto.", "sz": 14.5, "c": GRIGIO_TXT, "ls": 1.35}])
box(s, MX, Inches(4.15), Inches(5.3), Inches(1.15), GRIGIO_CH)
box(s, MX, Inches(4.15), Pt(4), Inches(1.15), VIOLA)
text(s, MX + Inches(0.38), Inches(4.42), Inches(4.6), Inches(0.7),
     [{"t": "Prima: il modulo resta bianco.\nDopo: il modulo è compilato.",
       "sz": 15.5, "b": True, "ls": 1.3}])
text(s, MX, Inches(5.6), Inches(5.3), Inches(0.8),
     [{"t": "Il guadagno è misurabile senza interpretazioni: o la pratica è completa, "
            "o non lo è.", "sz": 12.5, "i": True, "c": GRIGIO_TXT, "ls": 1.3}])
pic(s, f"{BUILD}/pdf-compilato-crop.png", Inches(6.8), Inches(1.45), Inches(5.6), Inches(4.6))
logo(s)
note(s, "1:50-2:15 — Il valore non è nella chat, è qui. L'utente non riceve un riassunto "
        "né una guida: riceve il modulo compilato. Prima bianco, dopo completo. "
        "È l'unica metrica che conta e non ha bisogno di interpretazione.")

# ── 6 · Lo stack ─────────────────────────────────────────────────────────
s = slide(GRIGIO_CH)
occhiello(s, MX, Inches(0.8), "Come è fatto")
text(s, MX, Inches(1.15), MW, Inches(0.7),
     [{"t": "Tre pezzi, nessun servizio esterno", "sz": 33, "b": True}])
for i, (k, titolo, desc, col) in enumerate([
    ("FRONTEND", "Angular 21",
     "Due schermate: carica e rispondi. Testo grande, un campo per volta, "
     "navigabile da tastiera.", VIOLA),
    ("BACKEND", "Spring Boot · Java 21",
     "Legge i campi del PDF, orchestra le domande, riscrive il modulo compilato "
     "con Apache PDFBox.", VIOLA_MED),
    ("MOTORE LINGUISTICO", "Ollama · qwen2.5 locale",
     "Trasforma l'etichetta burocratica in una domanda semplice. Gira sulla "
     "macchina, non nel cloud.", VIOLA_SCUR),
]):
    x = MX + Inches(i * 3.93)
    box(s, x, Inches(2.15), Inches(3.4), Inches(2.5), BIANCO)
    filetto(s, x, Inches(2.15), Inches(3.4), col, 4)
    text(s, x + Inches(0.32), Inches(2.5), Inches(2.75), Inches(0.3),
         [{"t": k, "sz": 10, "b": True, "c": col}])
    text(s, x + Inches(0.32), Inches(2.85), Inches(2.75), Inches(0.5),
         [{"t": titolo, "sz": 17, "b": True}])
    text(s, x + Inches(0.32), Inches(3.5), Inches(2.75), Inches(1.0),
         [{"t": desc, "sz": 12.5, "c": GRIGIO_TXT, "ls": 1.3}])

box(s, MX, Inches(5.15), MW, Inches(1.3), BIANCO)
box(s, MX, Inches(5.15), Pt(4), Inches(1.3), VIOLA)
text(s, MX + Inches(0.42), Inches(5.4), Inches(10.5), Inches(0.3),
     [{"t": "L'INTEGRAZIONE, IN UNA RIGA", "sz": 10, "b": True, "c": VIOLA}])
text(s, MX + Inches(0.42), Inches(5.76), Inches(10.5), Inches(0.5),
     [{"t": "PDF caricato   >   campi estratti   >   ogni etichetta diventa una domanda   >   "
            "risposte riscritte nel PDF   >   download", "sz": 14, "b": True, "ls": 1.2}])
logo(s)
note(s, "2:15-2:40 — Stack volutamente ordinario: Angular davanti, Spring Boot dietro, "
        "PDFBox per il PDF. L'unico pezzo interessante è il motore linguistico, ed è "
        "locale. Flusso lineare: nessuna coda, nessun database, nessuno stato oltre "
        "la sessione.")

# ── 7 · Chiusura ─────────────────────────────────────────────────────────
s = slide(NERO, scura=True)
occhiello(s, MX, Inches(0.85), "Perché conta", VIOLA_CHIA)
text(s, MX, Inches(1.25), MW, Inches(0.8),
     [{"t": "I dati non escono mai dalla macchina", "sz": 33, "b": True, "c": BIANCO}])
for i, (t1, t2) in enumerate([
    ("Nessun account, per nessuno",
     "Né per chi usa lo strumento, né per chi lo installa. Nessun abbonamento AI."),
    ("Il modello gira in locale",
     "Nome, indirizzo, codice fiscale non lasciano il computer. "
     "Non è una promessa contrattuale: è architettura."),
    ("Funziona su qualsiasi modulo compilabile",
     "Non è legato a un modulo specifico: legge i campi di qualunque PDF con moduli."),
    ("Se il modello non risponde, si continua lo stesso",
     "Lo strumento mostra l'etichetta originale e la persona arriva comunque in fondo."),
]):
    y = Inches(2.45 + i * 1.0)
    box(s, MX, y + Inches(0.1), Pt(9), Pt(9), VIOLA)
    text(s, MX + Inches(0.42), y, Inches(10.9), Inches(0.35),
         [{"t": t1, "sz": 17, "b": True, "c": BIANCO}])
    text(s, MX + Inches(0.42), y + Inches(0.38), Inches(10.6), Inches(0.45),
         [{"t": t2, "sz": 12.5, "c": GRIGIO_CH, "ls": 1.25}])
filetto(s, MX, Inches(6.5), MW, VIOLA, 2)
text(s, MX, Inches(6.73), Inches(9.0), Inches(0.5),
     [{"t": "Dal modulo bianco alla pratica conclusa.", "sz": 16, "b": True, "c": VIOLA_CHIA}])
logo(s, scura=True)
note(s, "2:40-3:00 — Chiusura sul punto che vende: privacy per costruzione. I dati "
        "anagrafici non lasciano il computer perché il modello è locale, non perché "
        "qualcuno lo promette in un contratto. Nessun account, nessun abbonamento, e "
        "se il modello cade la persona arriva in fondo comunque.")

prs.save(OUT)


# ── tema ufficiale nel file ──────────────────────────────────────────────
def applica_tema(pptx_path, theme_xml):
    """Sostituisce il tema del file con quello ufficiale Accenture 2020.

    Non è estetica: fa sì che il selettore colori di PowerPoint mostri la
    palette Accenture, così chi ritocca le slide non pesca colori a caso.
    """
    if not os.path.exists(theme_xml):
        print("  tema ufficiale non trovato: salto"); return
    tmp = pptx_path + ".tmp"
    with zipfile.ZipFile(pptx_path) as zin, \
         zipfile.ZipFile(tmp, "w", zipfile.ZIP_DEFLATED) as zout:
        for item in zin.infolist():
            data = zin.read(item.filename)
            if item.filename == "ppt/theme/theme1.xml":
                with open(theme_xml, "rb") as f:
                    data = f.read()
            zout.writestr(item, data)
    shutil.move(tmp, pptx_path)
    print("  tema 'Accenture 2020' applicato")


applica_tema(OUT, os.path.join(BRAND, "tema-accenture-2020.xml"))
print(f"OK  {OUT}")
print(f"slide: {len(prs.slides._sldIdLst)}")
