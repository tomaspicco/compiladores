import os
import sys
from reportlab.lib.pagesizes import A4, landscape
from reportlab.lib import colors
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.platypus import (
    SimpleDocTemplate, Paragraph, Spacer, Table, TableStyle, PageBreak, KeepTogether, Preformatted
)
from reportlab.pdfgen import canvas

PDF_PATH = r"C:\Users\maxi\Desktop\DECOMPILER\compiladores\Presentacion_Proyecto_Compiladores_UNVM.pdf"

class NumberedCanvas(canvas.Canvas):
    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self._saved_page_states = []

    def showPage(self):
        self._saved_page_states.append(dict(self.__dict__))
        self._startPage()

    def save(self):
        num_pages = len(self._saved_page_states)
        for state in self._saved_page_states:
            self.__dict__.update(state)
            self.draw_page_decorations(num_pages)
            super().showPage()
        super().save()

    def draw_page_decorations(self, total_pages):
        width, height = self._pagesize
        if self._pageNumber == 1:
            # Portada: no dibujamos header ni footer estándar
            return

        # Header superior
        self.saveState()
        self.setStrokeColor(colors.HexColor("#CBD5E1"))
        self.setLineWidth(0.8)
        self.line(40, height - 38, width - 40, height - 38)

        self.setFont("Helvetica-Bold", 8)
        self.setFillColor(colors.HexColor("#1E3A8A"))
        self.drawString(40, height - 30, "UNViME | ESCUELA DE INGENIERÍA Y CIENCIAS AMBIENTALES - CÁTEDRA DE COMPILADORES")

        self.setFont("Helvetica", 8)
        self.setFillColor(colors.HexColor("#64748B"))
        self.drawRightString(width - 40, height - 30, "PROYECTO COMPILADOR: JFLEX + CUP + C3D + GNU x86-64")

        # Footer inferior
        self.setStrokeColor(colors.HexColor("#CBD5E1"))
        self.setLineWidth(0.8)
        self.line(40, 38, width - 40, 38)

        self.setFont("Helvetica", 8)
        self.setFillColor(colors.HexColor("#64748B"))
        self.drawString(40, 25, "Documento Técnico y Presentación Ejecutiva del Proyecto")

        page_str = f"Diapositiva {self._pageNumber} de {total_pages}"
        self.drawRightString(width - 40, 25, page_str)
        self.restoreState()


def build_pdf():
    # Tamaño A4 apaisado: 841.89 x 595.27 puntos
    doc = SimpleDocTemplate(
        PDF_PATH,
        pagesize=landscape(A4),
        leftMargin=40,
        rightMargin=40,
        topMargin=50,
        bottomMargin=45
    )

    styles = getSampleStyleSheet()

    # Definición de estilos
    title_cover = ParagraphStyle(
        'CoverTitle',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=28,
        leading=34,
        textColor=colors.HexColor("#0F172A"),
        alignment=0
    )

    subtitle_cover = ParagraphStyle(
        'CoverSubtitle',
        parent=styles['Normal'],
        fontName='Helvetica',
        fontSize=14,
        leading=18,
        textColor=colors.HexColor("#2563EB"),
        alignment=0
    )

    slide_title = ParagraphStyle(
        'SlideTitle',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=20,
        leading=24,
        textColor=colors.HexColor("#0F172A"),
        spaceAfter=10
    )

    slide_subtitle = ParagraphStyle(
        'SlideSubtitle',
        parent=styles['Normal'],
        fontName='Helvetica-Bold',
        fontSize=11,
        leading=14,
        textColor=colors.HexColor("#2563EB"),
        spaceAfter=8
    )

    body_text = ParagraphStyle(
        'Body',
        parent=styles['Normal'],
        fontName='Helvetica',
        fontSize=10,
        leading=14,
        textColor=colors.HexColor("#334155")
    )

    bullet_text = ParagraphStyle(
        'Bullet',
        parent=styles['Normal'],
        fontName='Helvetica',
        fontSize=9.5,
        leading=13.5,
        textColor=colors.HexColor("#1E293B"),
        leftIndent=12
    )

    code_style = ParagraphStyle(
        'CodeText',
        parent=styles['Normal'],
        fontName='Courier',
        fontSize=8.5,
        leading=11,
        textColor=colors.HexColor("#F8FAFC")
    )

    story = []

    # =========================================================================
    # DIAPOSITIVA 1: PORTADA
    # =========================================================================
    story.append(Spacer(1, 40))
    story.append(Paragraph("UNIVERSIDAD NACIONAL DE VILLA MERCEDES", slide_subtitle))
    story.append(Paragraph("Compilador Modular con AST, Tabla de Símbolos, C3D y Ensamblador GNU x86-64", title_cover))
    story.append(Spacer(1, 10))
    story.append(Paragraph("Presentación Técnica de Arquitectura, Especificación y Guía de Uso Integral", subtitle_cover))
    story.append(Spacer(1, 25))

    info_data = [
        [
            Paragraph("<b>Cátedra:</b> Compiladores<br/><b>Carrera:</b> Ingeniería en Sistemas de Información<br/><b>Institución:</b> Escuela de Ingeniería y Ciencias Ambientales (UNViME)", body_text),
            Paragraph("<b>Componentes Clave:</b><br/>• Frontend: JFlex 1.9.1 + Java-CUP 11b<br/>• Verificación Semántica Estricta (3 categorías)<br/>• Generación de Código Intermedio (C3D Cuádruplas)<br/>• Backend: Assembly GNU x86-64 (Sintaxis AT&T)", body_text)
        ]
    ]
    t_info = Table(info_data, colWidths=[360, 400])
    t_info.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,-1), colors.HexColor("#F1F5F9")),
        ('BOX', (0,0), (-1,-1), 1, colors.HexColor("#CBD5E1")),
        ('INNERGRID', (0,0), (-1,-1), 1, colors.HexColor("#E2E8F0")),
        ('TOPPADDING', (0,0), (-1,-1), 12),
        ('BOTTOMPADDING', (0,0), (-1,-1), 12),
        ('LEFTPADDING', (0,0), (-1,-1), 16),
        ('RIGHTPADDING', (0,0), (-1,-1), 16),
    ]))
    story.append(t_info)

    story.append(Spacer(1, 40))
    story.append(Paragraph("<b>Estado del Proyecto:</b> 100% Funcional, limpio, modularizado y verificado contra todos los casos de prueba.", body_text))
    story.append(PageBreak())

    # =========================================================================
    # DIAPOSITIVA 2: PROPÓSITO Y REQUISITOS OBLIGATORIOS
    # =========================================================================
    story.append(Paragraph("1. Propósito y Requisitos Obligatorios del Proyecto", slide_title))
    story.append(Paragraph("Cumplimiento exhaustivo de los 7 puntos de la consigna y la directiva de cátedra sobre assembly x86-64:", slide_subtitle))

    req_data = [
        [
            Paragraph("<b>Requisito Original de Consigna</b>", ParagraphStyle('TH', parent=body_text, fontName='Helvetica-Bold', textColor=colors.white)),
            Paragraph("<b>Implementación y Solución Técnica en el Compilador</b>", ParagraphStyle('TH', parent=body_text, fontName='Helvetica-Bold', textColor=colors.white))
        ],
        [
            Paragraph("<b>1. Gramática Extendida:</b> Tipos <code>int</code>, <code>bool</code>, constantes lógicas, declaraciones, asignaciones y sentencias <code>return</code> en función <code>main</code>.", bullet_text),
            Paragraph("Sintaxis formal en CUP: función <code>main</code> con retorno <code>void|int|bool</code>, declaraciones previas múltiples y cuerpo secuencial de sentencias.", bullet_text)
        ],
        [
            Paragraph("<b>2. Expresiones Regulares:</b> Definición de palabras reservadas, identificadores, números y operadores.", bullet_text),
            Paragraph("Especificación en JFlex con macros formales: <code>Letra</code>, <code>Digito</code>, <code>Id</code>, <code>Nro</code> y manejo exacto de números de línea/columna.", bullet_text)
        ],
        [
            Paragraph("<b>3. Analizador Léxico (JFlex):</b> Escaneo y generación de tokens.", bullet_text),
            Paragraph("Clase <code>Lexer.java</code> generada con JFlex 1.9.1. Incluye script <code>test_lexer.ps1</code> para depuración directa de tokens.", bullet_text)
        ],
        [
            Paragraph("<b>4. Parser (JFlex + CUP):</b> Análisis sintáctico y precedencia.", bullet_text),
            Paragraph("Gramática LALR(1) en <code>Parser.cup</code> con precedencia declarada: <code>*</code> y <code>/</code> con mayor prioridad que <code>+</code> y <code>-</code>. Cero conflictos.", bullet_text)
        ],
        [
            Paragraph("<b>5. AST (Árbol Sintáctico Abstracto):</b> Estructuración del árbol.", bullet_text),
            Paragraph("Jerarquía orientada a objetos en <code>ast/</code>. Distinción explícita acordada en clase: <code>VarDeclNode</code> (declaración) vs <code>VarUseNode</code> (uso).", bullet_text)
        ],
        [
            Paragraph("<b>6. Intérprete AST y Tabla de Símbolos:</b> Evaluación y verificación.", bullet_text),
            Paragraph("Clase <code>Interpreter.java</code> para ejecución directa del AST. Tabla <code>SymbolTable</code> con enlace bidireccional y control estricto de 3 errores semánticos.", bullet_text)
        ],
        [
            Paragraph("<b>7. C3D y Ensamblador GNU x86-64:</b> Requisito del profesor.", bullet_text),
            Paragraph("Generador C3D con cuádruplas y evaluador. Nuevo <code>GnuAssemblyGenerator.java</code> que traduce C3D a Assembly GNU x86-64 instrucción por instrucción.", bullet_text)
        ]
    ]

    t_req = Table(req_data, colWidths=[360, 400])
    t_req.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,0), colors.HexColor("#1E3A8A")),
        ('ALIGN', (0,0), (-1,-1), 'LEFT'),
        ('VALIGN', (0,0), (-1,-1), 'TOP'),
        ('GRID', (0,0), (-1,-1), 0.5, colors.HexColor("#CBD5E1")),
        ('ROWBACKGROUNDS', (0,1), (-1,-1), [colors.white, colors.HexColor("#F8FAFC")]),
        ('TOPPADDING', (0,0), (-1,-1), 4),
        ('BOTTOMPADDING', (0,0), (-1,-1), 4),
    ]))
    story.append(t_req)
    story.append(PageBreak())

    # =========================================================================
    # DIAPOSITIVA 3: ARQUITECTURA GENERAL DEL COMPILADOR
    # =========================================================================
    story.append(Paragraph("2. Arquitectura del Compilador (Pipeline de Fases)", slide_title))
    story.append(Paragraph("Flujo modular e integrado de datos desde el código fuente hasta el código de máquina ensamblador:", slide_subtitle))

    arch_diagram = """
    +------------------------------------------------------------------------------------------------------+
    | 1. FRONTEND LÉXICO Y SINTÁCTICO                                                                      |
    |    Código Fuente (.txt) ---> [Lexer (JFlex)] ---> Tokens ---> [Parser (CUP)]                         |
    +------------------------------------------------------------------------------------------------------+
                                                                    |
                                                                    v
    +------------------------------------------------------------------------------------------------------+
    | 2. REPRESENTACIÓN INTERMEDIA (AST) Y SEMÁNTICA                                                       |
    |    Construcción de Árbol AST:                                                                        |
    |      * ProgramNode (Firma de main)                                                                   |
    |      * VarDeclNode (Declaraciones) <==== Enlace Bidireccional ====> SymbolTable (Tabla de Símbolos)  |
    |      * VarUseNode / Assign / Retorno                                                                 |
    |                                                                                                      |
    |    [SemanticAnalyzer]: Detección de Redeclaraciones, No Declaradas e Incompatibilidad de Tipos       |
    +------------------------------------------------------------------------------------------------------+
                                           |
                                           v
    +------------------------------------------------------------------------------------------------------+
    | 3. CÓDIGO DE TRES DIRECCIONES (C3D)                                                                  |
    |    [GeneradorC3D]: Recorrido Post-Orden (Bottom-Up) resolviendo hojas a raíz con temporales T1, T2...   |
    |    Estructuras: Vector<Instruccion3D>  |  Cuádruplas (OP, ARG1, ARG2, RES)  |  [InterpreteC3D]       |
    +------------------------------------------------------------------------------------------------------+
                                           |
                                           v
    +------------------------------------------------------------------------------------------------------+
    | 4. BACKEND DE GENERACIÓN DE CÓDIGO (ASSEMBLY)                                                        |
    |    A) [PseudoAssemblyGenerator]: Genera seudo-assembly simbólico basado en registros (.asm)          |
    |    B) [GnuAssemblyGenerator]:   Genera Assembly GNU x86-64 real instrucción por instrucción (.s)     |
    |       - Marco de pila: enter $(8 * N), $0                                                            |
    |       - Registros temporales de cálculo: %r10 y %r11                                                 |
    |       - Convención Linux/GNU en %rax, leave y ret                                                    |
    +------------------------------------------------------------------------------------------------------+
    """

    t_arch = Table([[Preformatted(arch_diagram.strip(), code_style)]], colWidths=[760])
    t_arch.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,-1), colors.HexColor("#0F172A")),
        ('LEFTPADDING', (0,0), (-1,-1), 14),
        ('RIGHTPADDING', (0,0), (-1,-1), 14),
        ('TOPPADDING', (0,0), (-1,-1), 10),
        ('BOTTOMPADDING', (0,0), (-1,-1), 10),
        ('BOX', (0,0), (-1,-1), 1, colors.HexColor("#334155"))
    ]))
    story.append(t_arch)
    story.append(PageBreak())

    # =========================================================================
    # DIAPOSITIVA 4: ANÁLISIS SEMÁNTICO Y TABLA DE SÍMBOLOS
    # =========================================================================
    story.append(Paragraph("3. Análisis Semántico y Verificación de Errores", slide_title))
    story.append(Paragraph("Verificación obligatoria de las tres categorías de errores y vinculación con la Tabla de Símbolos:", slide_subtitle))

    sem_data = [
        [
            Paragraph("<b>Categoría de Error</b>", ParagraphStyle('TH', parent=body_text, fontName='Helvetica-Bold', textColor=colors.white)),
            Paragraph("<b>Condición de Detección</b>", ParagraphStyle('TH', parent=body_text, fontName='Helvetica-Bold', textColor=colors.white)),
            Paragraph("<b>Mensaje Oficial Emitido</b>", ParagraphStyle('TH', parent=body_text, fontName='Helvetica-Bold', textColor=colors.white))
        ],
        [
            Paragraph("<b>1. VARIABLE REDECLARADA</b>", bullet_text),
            Paragraph("Se intenta declarar una variable que ya existe previamente en el ámbito actual (mismo identificador en declaraciones).", bullet_text),
            Paragraph("<code>[VARIABLE REDECLARADA] (linea X): la variable 'x' ya fue declarada previamente.</code>", bullet_text)
        ],
        [
            Paragraph("<b>2. VARIABLE NO DECLARADA</b>", bullet_text),
            Paragraph("Se utiliza una variable en una expresión (<code>VarUseNode</code>) o en el lado izquierdo de una asignación sin haber sido declarada.", bullet_text),
            Paragraph("<code>[VARIABLE NO DECLARADA] (linea X): la variable 'y' no esta declarada.</code>", bullet_text)
        ],
        [
            Paragraph("<b>3. ERROR DE TIPO</b>", bullet_text),
            Paragraph("A) Asignación entre tipos incompatibles (ej. asignar <code>bool</code> a <code>int</code>).<br/>B) Operador aritmético (<code>+,-,*,/</code>) aplicado a booleanos.<br/>C) Retorno de función incompatible con la firma.", bullet_text),
            Paragraph("<code>[ERROR DE TIPO] (linea X): tipo incompatible en asignacion a 'x'. Se esperaba int pero se obtuvo bool.</code><br/><code>[ERROR DE TIPO] (linea X): el operador aritmetico '+' requiere enteros pero el lado izquierdo es bool.</code>", bullet_text)
        ]
    ]

    t_sem = Table(sem_data, colWidths=[200, 260, 300])
    t_sem.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,0), colors.HexColor("#1E3A8A")),
        ('ALIGN', (0,0), (-1,-1), 'LEFT'),
        ('VALIGN', (0,0), (-1,-1), 'TOP'),
        ('GRID', (0,0), (-1,-1), 0.5, colors.HexColor("#CBD5E1")),
        ('ROWBACKGROUNDS', (0,1), (-1,-1), [colors.white, colors.HexColor("#F8FAFC")]),
        ('TOPPADDING', (0,0), (-1,-1), 8),
        ('BOTTOMPADDING', (0,0), (-1,-1), 8),
    ]))
    story.append(t_sem)
    story.append(Spacer(1, 14))

    sym_explanation = """
<b>Diseño de la Tabla de Símbolos (SymbolTable):</b>
• Mantiene objetos <code>Symbol</code> que almacenan: identificador, tipo (<code>INT</code>, <code>BOOL</code>, <code>VOID</code>), valor en tiempo de ejecución, línea/columna y categoría (<code>VARIABLE</code>, <code>TEMPORAL</code>, <code>CONSTANTE</code>).
• <b>Enlace Bidireccional AST &lt;-&gt; Symbol:</b> Tanto los nodos de declaración (<code>VarDeclNode</code>) como los de uso (<code>VarUseNode</code>) guardan una referencia directa al mismo objeto <code>Symbol</code> en memoria, asegurando consistencia absoluta durante la interpretación y generación de código.
    """
    story.append(Paragraph(sym_explanation.strip(), body_text))
    story.append(PageBreak())

    # =========================================================================
    # DIAPOSITIVA 5: CÓDIGO DE TRES DIRECCIONES (C3D)
    # =========================================================================
    story.append(Paragraph("4. Código de Tres Direcciones (C3D) y Cuádruplas", slide_title))
    story.append(Paragraph("Generación intermedia mediante recorrido Post-Orden (Bottom-Up: de hojas hacia la raíz):", slide_subtitle))

    c3d_col1 = """
<b>Principios de Diseño de C3D:</b>
1. <b>Estructura Dinámica:</b> Implementado en <code>CodigoTresDirecciones.java</code> como un vector dinámico <code>Vector&lt;Instruccion3D&gt;</code>.
2. <b>Instrucciones Cuádruplas:</b> Cada <code>Instruccion3D</code> contiene el operador y referencias directas a objetos <code>Symbol</code> para operando 1, operando 2 y resultado.
3. <b>Variables Temporales:</b> Generación de temporales sucesivos (<code>T1, T2, T3...</code>) para almacenar resultados intermedios de expresiones complejas.
4. <b>Intérprete C3D (InterpreteC3D):</b> Evalúa secuencialmente el vector de instrucciones para verificar el valor de cómputo y el flujo de ejecución antes del paso a assembly.

<b>Formato Estándar y Cuádruplas:</b>
• Expresión: <code>x = x + 3 * 2 * y;</code>
• C3D Resultante:
  <code>[01] T1 = 3 * 2</code>
  <code>[02] T2 = T1 * y</code>
  <code>[03] T3 = x + T2</code>
  <code>[04] x = T3</code>
    """

    c3d_sample_code = """
=== TABLA DE CUÁDRUPLAS (C3D) ===
Nro   Op       Arg1    Arg2    Res
-----------------------------------
[01]  ASSIGN   1       -       x
[02]  MUL      3       2       T1
[03]  MUL      T1      y       T2
[04]  ADD      x       T2      T3
[05]  ASSIGN   T3      -       x
[06]  RET      x       -       -
[07]  ASSIGN   4       -       y
[08]  ASSIGN   2       -       x
[09]  MUL      y       2       T4
[10]  RET      T4      -       -

Resultado de Ejecución C3D (Return): 1
    """

    t_c3d = Table([
        [
            Paragraph(c3d_col1.strip(), body_text),
            Table([[Preformatted(c3d_sample_code.strip(), code_style)]], colWidths=[370], style=[
                ('BACKGROUND', (0,0), (-1,-1), colors.HexColor("#0F172A")),
                ('TOPPADDING', (0,0), (-1,-1), 8),
                ('BOTTOMPADDING', (0,0), (-1,-1), 8),
                ('LEFTPADDING', (0,0), (-1,-1), 10),
                ('RIGHTPADDING', (0,0), (-1,-1), 10),
                ('BOX', (0,0), (-1,-1), 1, colors.HexColor("#334155"))
            ])
        ]
    ], colWidths=[380, 380])
    t_c3d.setStyle(TableStyle([
        ('VALIGN', (0,0), (-1,-1), 'TOP')
    ]))
    story.append(t_c3d)
    story.append(PageBreak())

    # =========================================================================
    # DIAPOSITIVA 6: GENERACIÓN DE ASSEMBLY GNU x86-64 (LA NUEVA ETAPA)
    # =========================================================================
    story.append(Paragraph("5. Generación de Assembly GNU x86-64 (Nueva Etapa)", slide_title))
    story.append(Paragraph("Traducción instrucción por instrucción desde el C3D siguiendo la guía técnica de arquitectura x86-64:", slide_subtitle))

    asm_explanation = """
<b>Especificaciones Técnicas del Backend GNU x86-64 (<code>GnuAssemblyGenerator.java</code>):</b>
• <b>Sintaxis:</b> Ensamblador GNU (GAS / AT&T), compatible directamente con <code>gcc</code> y simuladores x86-64. En AT&T el operando destino se ubica a la derecha (<code>mov src, dest</code>).
• <b>Gestión de Marco de Pila (Stack Frame):</b>
  - Cada variable local y cada temporal creado en C3D tiene asignado un espacio fijo de 8 bytes (quadword) indexado respecto al registro base <code>%rbp</code>: <code>-8(%rbp)</code>, <code>-16(%rbp)</code>, <code>-24(%rbp)</code>, etc.
  - El prólogo de la función utiliza la instrucción formal: <code>enter $(8 * N), $0</code>, donde N es la cantidad total de variables y temporales.
• <b>Esquema sin Asignación de Registros:</b> Siguiendo las directivas de la guía, cada operando se carga desde la pila hacia los registros temporales <code>%r10</code> y <code>%r11</code>, se ejecuta la instrucción aritmética, y el resultado se guarda inmediatamente de vuelta en la pila.
• <b>Convención de Retorno:</b> En sentencias <code>return</code>, el valor se carga en <code>%rax</code> y se ejecutan las instrucciones de epílogo <code>leave</code> y <code>ret</code>.
    """
    story.append(Paragraph(asm_explanation.strip(), body_text))
    story.append(Spacer(1, 10))

    asm_sample = """
.globl main
main:
    enter $(8 * 6), $0          # Reserva espacio para 2 variables (x,y) y 4 temporales (T1..T4)
    # Marco de pila (stack frame):
    #   x -> -8(%rbp) | y -> -16(%rbp) | T1 -> -24(%rbp) | T2 -> -32(%rbp) | T3 -> -40(%rbp) | T4 -> -48(%rbp)

    # [01] x = 1 (Asignación)          # [02] T1 = 3 * 2 (Multiplicación)
    mov $1, %r10                       mov $3, %r10
    mov %r10, -8(%rbp)                 mov $2, %r11
                                       imul %r11, %r10
                                       mov %r10, -24(%rbp)

    # [03] T2 = T1 * y                 # [04] T3 = x + T2 (Suma con temporal)
    mov -24(%rbp), %r10                mov -8(%rbp), %r10
    mov -16(%rbp), %r11                mov -32(%rbp), %r11
    imul %r11, %r10                    add %r11, %r10
    mov %r10, -32(%rbp)                mov %r10, -40(%rbp)

    # [05] x = T3                      # [06] return x (Retorno de valor en %rax)
    mov -40(%rbp), %r10                mov -8(%rbp), %rax
    mov %r10, -8(%rbp)                 leave
                                       ret
    """

    t_asm = Table([[Preformatted(asm_sample.strip(), code_style)]], colWidths=[760])
    t_asm.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,-1), colors.HexColor("#0F172A")),
        ('LEFTPADDING', (0,0), (-1,-1), 14),
        ('RIGHTPADDING', (0,0), (-1,-1), 14),
        ('TOPPADDING', (0,0), (-1,-1), 8),
        ('BOTTOMPADDING', (0,0), (-1,-1), 8),
        ('BOX', (0,0), (-1,-1), 1, colors.HexColor("#334155"))
    ]))
    story.append(t_asm)
    story.append(PageBreak())

    # =========================================================================
    # DIAPOSITIVA 7: COMPARATIVA: SEUDO-ASSEMBLY VS GNU x86-64
    # =========================================================================
    story.append(Paragraph("6. Comparativa: Seudo-Assembly (AST) vs GNU x86-64 (C3D)", slide_title))
    story.append(Paragraph("El proyecto incluye ambos generadores para satisfacer tanto el requerimiento 7 original como la ampliación solicitada:", slide_subtitle))

    cmp_data = [
        [
            Paragraph("<b>Característica</b>", ParagraphStyle('TH', parent=body_text, fontName='Helvetica-Bold', textColor=colors.white)),
            Paragraph("<b>Seudo-Assembly (PseudoAssemblyGenerator)</b>", ParagraphStyle('TH', parent=body_text, fontName='Helvetica-Bold', textColor=colors.white)),
            Paragraph("<b>Assembly GNU x86-64 (GnuAssemblyGenerator)</b>", ParagraphStyle('TH', parent=body_text, fontName='Helvetica-Bold', textColor=colors.white))
        ],
        [
            Paragraph("<b>Origen de Traducción</b>", bullet_text),
            Paragraph("Recorre directamente el <b>Árbol Sintáctico (AST)</b>.", bullet_text),
            Paragraph("Recorre la lista de <b>Código de Tres Direcciones (C3D)</b> instrucción por instrucción.", bullet_text)
        ],
        [
            Paragraph("<b>Modelo de Datos</b>", bullet_text),
            Paragraph("Sección <code>.DATA</code> estática con directivas <code>.INT</code> y <code>.BOOL</code>.", bullet_text),
            Paragraph("<b>Marco de Pila (Stack Frame) dinámico</b> indexado con <code>-offset(%rbp)</code>.", bullet_text)
        ],
        [
            Paragraph("<b>Modelo de Registros</b>", bullet_text),
            Paragraph("Registros simbólicos infinitos: <code>R0, R1, R2...</code>", bullet_text),
            Paragraph("Registros reales x86-64 GNU: <code>%r10</code>, <code>%r11</code> para cálculo y <code>%rax</code> para retornos.", bullet_text)
        ],
        [
            Paragraph("<b>Operaciones Aritméticas</b>", bullet_text),
            Paragraph("Instrucciones de 3 operandos: <code>ADD R3, R1, R2</code>", bullet_text),
            Paragraph("Instrucciones x86-64 reales: <code>add %r11, %r10</code>, <code>imul</code>, <code>idivq</code>.", bullet_text)
        ],
        [
            Paragraph("<b>Compatibilidad Real</b>", bullet_text),
            Paragraph("Educativo / Simulación conceptual.", bullet_text),
            Paragraph("<b>100% Compatible con Ensamblador GNU (GAS / GCC)</b> de 64 bits.", bullet_text)
        ],
        [
            Paragraph("<b>Archivo Exportado</b>", bullet_text),
            Paragraph("Archivo con extensión <code>.asm</code>.", bullet_text),
            Paragraph("Archivo con extensión <code>.s</code> estándar de ensamblador GNU.", bullet_text)
        ]
    ]

    t_cmp = Table(cmp_data, colWidths=[160, 300, 300])
    t_cmp.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,0), colors.HexColor("#1E3A8A")),
        ('ALIGN', (0,0), (-1,-1), 'LEFT'),
        ('VALIGN', (0,0), (-1,-1), 'TOP'),
        ('GRID', (0,0), (-1,-1), 0.5, colors.HexColor("#CBD5E1")),
        ('ROWBACKGROUNDS', (0,1), (-1,-1), [colors.white, colors.HexColor("#F8FAFC")]),
        ('TOPPADDING', (0,0), (-1,-1), 6),
        ('BOTTOMPADDING', (0,0), (-1,-1), 6),
    ]))
    story.append(t_cmp)
    story.append(PageBreak())

    # =========================================================================
    # DIAPOSITIVA 8: GUÍA DE COMPILACIÓN, EJECUCIÓN Y PRUEBAS
    # =========================================================================
    story.append(Paragraph("7. Guía Práctica de Compilación, Ejecución y Pruebas", slide_title))
    story.append(Paragraph("Scripts automatizados y opciones por línea de comandos para verificar todas las etapas:", slide_subtitle))

    run_text = """
<b>1. Compilar todo el proyecto:</b> <code>powershell: .\\compilar.ps1</code>  |  <code>cmd: compilar.bat</code><br/>
<b>2. Menú Interactivo (Recomendado para defensa):</b> <code>.\\menu.ps1</code>  |  <code>menu.bat</code><br/>
<b>3. Scripts Individuales Paso a Paso:</b>
    """
    story.append(Paragraph(run_text.strip(), body_text))
    story.append(Spacer(1, 6))

    flags_data = [
        [
            Paragraph("<b>Script Paso a Paso</b>", ParagraphStyle('TH', parent=body_text, fontName='Helvetica-Bold', textColor=colors.white)),
            Paragraph("<b>Etapa Específica que Ejecuta y Muestra</b>", ParagraphStyle('TH', parent=body_text, fontName='Helvetica-Bold', textColor=colors.white))
        ],
        [
            Paragraph("<code>.\\paso1_lexer.ps1</code> (o <code>--lexer</code>)", bullet_text),
            Paragraph("<b>Paso 1 / Punto 3:</b> Analizador Léxico (JFlex). Imprime la tabla de tokens, lexemas, líneas y columnas.", bullet_text)
        ],
        [
            Paragraph("<code>.\\paso2_parser_ast.ps1</code> (o <code>--ast</code>)", bullet_text),
            Paragraph("<b>Paso 2 / Puntos 4 y 5:</b> Parser CUP. Construye y visualiza el Árbol Sintáctico Abstracto (AST).", bullet_text)
        ],
        [
            Paragraph("<code>.\\paso3_semantica.ps1</code> (o <code>--semantic</code>)", bullet_text),
            Paragraph("<b>Paso 3:</b> Análisis Semántico. Chequea los 3 errores y muestra la Tabla de Símbolos.", bullet_text)
        ],
        [
            Paragraph("<code>.\\paso4_interprete.ps1</code> (o <code>--eval</code>)", bullet_text),
            Paragraph("<b>Paso 4 / Punto 6:</b> Intérprete del AST. Ejecuta las sentencias y muestra el estado final de memoria.", bullet_text)
        ],
        [
            Paragraph("<code>.\\paso5_c3d.ps1</code> (o <code>--c3d</code>)", bullet_text),
            Paragraph("<b>Paso 5:</b> Código de Tres Direcciones. Muestra cuádruplas y ejecuta la simulación con InterpreteC3D.", bullet_text)
        ],
        [
            Paragraph("<code>.\\paso6_assembly.ps1</code> (o <code>--gnu</code>)", bullet_text),
            Paragraph("<b>Paso 6 / Punto 7:</b> Assembly GNU x86-64. Muestra el código ensamblador generado instrucción por instrucción.", bullet_text)
        ]
    ]

    t_flags = Table(flags_data, colWidths=[360, 400])
    t_flags.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,0), colors.HexColor("#1E3A8A")),
        ('ALIGN', (0,0), (-1,-1), 'LEFT'),
        ('VALIGN', (0,0), (-1,-1), 'MIDDLE'),
        ('GRID', (0,0), (-1,-1), 0.5, colors.HexColor("#CBD5E1")),
        ('ROWBACKGROUNDS', (0,1), (-1,-1), [colors.white, colors.HexColor("#F8FAFC")]),
        ('TOPPADDING', (0,0), (-1,-1), 4),
        ('BOTTOMPADDING', (0,0), (-1,-1), 4),
    ]))
    story.append(t_flags)
    story.append(PageBreak())

    # =========================================================================
    # DIAPOSITIVA 9: CASOS DE PRUEBA Y VALIDACIONES INCLUIDAS
    # =========================================================================
    story.append(Paragraph("8. Suite de Pruebas Incluida en el Proyecto", slide_title))
    story.append(Paragraph("Archivos de prueba listos para validar cada capacidad del compilador:", slide_subtitle))

    tests_data = [
        [
            Paragraph("<b>Archivo de Prueba</b>", ParagraphStyle('TH', parent=body_text, fontName='Helvetica-Bold', textColor=colors.white)),
            Paragraph("<b>Objetivo y Qué se Valida</b>", ParagraphStyle('TH', parent=body_text, fontName='Helvetica-Bold', textColor=colors.white)),
            Paragraph("<b>Resultado Esperado</b>", ParagraphStyle('TH', parent=body_text, fontName='Helvetica-Bold', textColor=colors.white))
        ],
        [
            Paragraph("<code>src\\programa_prueba.txt</code>", bullet_text),
            Paragraph("Programa completo con múltiples variables, precedencia compuesta y retornos sucesivos.", bullet_text),
            Paragraph("Compilación limpia, C3D de 10 instrucciones, Seudo-Assembly y GNU x86-64 con retorno 1.", bullet_text)
        ],
        [
            Paragraph("<code>src\\prueba_c3d_aritmetica.txt</code>", bullet_text),
            Paragraph("Expresión aritmética anidada con división y resta: <code>z = x + y * 2 + (10 - x) / 2;</code>", bullet_text),
            Paragraph("Genera 5 temporales <code>T1..T5</code>. Cuádruplas exactas y retorno 13 en C3D y x86-64.", bullet_text)
        ],
        [
            Paragraph("<code>src\\prueba_c3d_bool.txt</code>", bullet_text),
            Paragraph("Variables y constantes lógicas: <code>bool a; a = true; b = false; return a;</code>", bullet_text),
            Paragraph("Asignación de booleanos en C3D y representación como 1 y 0 en x86-64.", bullet_text)
        ],
        [
            Paragraph("<code>src\\prueba_error_redeclaracion.txt</code>", bullet_text),
            Paragraph("Declaración doble de la variable <code>x</code> en el mismo bloque.", bullet_text),
            Paragraph("Reporta de inmediato: <code>[VARIABLE REDECLARADA] (linea 4)</code> y aborta limpiamente.", bullet_text)
        ],
        [
            Paragraph("<code>src\\prueba_error_no_declarada.txt</code>", bullet_text),
            Paragraph("Uso de variables <code>y</code> y <code>z</code> en asignación y suma sin previa declaración.", bullet_text),
            Paragraph("Reporta de inmediato: <code>[VARIABLE NO DECLARADA]</code> para cada identificador.", bullet_text)
        ],
        [
            Paragraph("<code>src\\prueba_error_tipo_asignacion.txt</code>", bullet_text),
            Paragraph("Asignación incompatible: <code>x = true; b = 42;</code>", bullet_text),
            Paragraph("Reporta de inmediato: <code>[ERROR DE TIPO]</code> indicando tipos esperados y obtenidos.", bullet_text)
        ],
        [
            Paragraph("<code>src\\prueba_error_tipo_operacion.txt</code>", bullet_text),
            Paragraph("Operación aritmética <code>b + 10</code> sobre variable lógica.", bullet_text),
            Paragraph("Reporta de inmediato: <code>[ERROR DE TIPO]</code> señalando que '+' requiere enteros.", bullet_text)
        ]
    ]

    t_tests = Table(tests_data, colWidths=[200, 300, 260])
    t_tests.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,0), colors.HexColor("#1E3A8A")),
        ('ALIGN', (0,0), (-1,-1), 'LEFT'),
        ('VALIGN', (0,0), (-1,-1), 'TOP'),
        ('GRID', (0,0), (-1,-1), 0.5, colors.HexColor("#CBD5E1")),
        ('ROWBACKGROUNDS', (0,1), (-1,-1), [colors.white, colors.HexColor("#F8FAFC")]),
        ('TOPPADDING', (0,0), (-1,-1), 5),
        ('BOTTOMPADDING', (0,0), (-1,-1), 5),
    ]))
    story.append(t_tests)
    story.append(PageBreak())

    # =========================================================================
    # DIAPOSITIVA 10: CONCLUSIÓN Y RESUMEN DEL PROYECTO
    # =========================================================================
    story.append(Paragraph("9. Resumen de Entregables y Estado Final", slide_title))
    story.append(Paragraph("Proyecto concluido con estándar profesional universitario y total fidelidad a las especificaciones:", slide_subtitle))

    summary_items = [
        [
            Paragraph("<b>Estructura Limpia del Repositorio</b>", ParagraphStyle('TH', parent=body_text, fontName='Helvetica-Bold', textColor=colors.white)),
            Paragraph("<b>Aportes de Ingeniería del Compilador</b>", ParagraphStyle('TH', parent=body_text, fontName='Helvetica-Bold', textColor=colors.white))
        ],
        [
            Paragraph("""
• <b>Sin residuos ni artefactos innecesarios:</b> Carpetas residuales depuradas por completo.<br/>
• <b>Librerías integradas:</b> JFlex 1.9.1 y Java-CUP 11b listos para compilar en <code>lib/</code>.<br/>
• <b>Documentación técnica completa:</b><br/>
  - <code>README.md</code>: Vista rápida y comandos.<br/>
  - <code>INFORME_PREPROYECTO.md</code>: Informe técnico de cátedra.<br/>
  - <code>GUIA_Y_CAMBIOS_PROYECTO.md</code>: Registro detallado y pasos de ejecución.<br/>
  - <code>Presentacion_Proyecto_Compiladores_UNVM.pdf</code>: Este documento imprimible y presentable.
            """, bullet_text),
            Paragraph("""
• <b>Modularidad Estricta:</b> Separación clara entre <code>lexer</code>, <code>parser</code>, <code>ast</code>, <code>symbols</code>, <code>semantic</code>, <code>c3d</code>, <code>interpreter</code> y <code>codegen</code>.<br/>
• <b>Dos Generadores de Código:</b><br/>
  1. <code>PseudoAssemblyGenerator</code>: Responde al punto 7 original.<br/>
  2. <code>GnuAssemblyGenerator</code>: Responde a la clase del docente, traduciendo C3D a Assembly GNU x86-64 real.<br/>
• <b>Extensibilidad:</b> Preparado para incorporar estructuras de control de flujo (<code>if/else</code>, bucles) y llamadas a funciones futuras.<br/>
• <b>Listo para Defensa Oral y Demostración en Vivo.</b>
            """, bullet_text)
        ]
    ]

    t_sum = Table(summary_items, colWidths=[380, 380])
    t_sum.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,0), colors.HexColor("#1E3A8A")),
        ('ALIGN', (0,0), (-1,-1), 'LEFT'),
        ('VALIGN', (0,0), (-1,-1), 'TOP'),
        ('GRID', (0,0), (-1,-1), 0.5, colors.HexColor("#CBD5E1")),
        ('ROWBACKGROUNDS', (0,1), (-1,-1), [colors.white]),
        ('TOPPADDING', (0,0), (-1,-1), 10),
        ('BOTTOMPADDING', (0,0), (-1,-1), 10),
        ('LEFTPADDING', (0,0), (-1,-1), 12),
        ('RIGHTPADDING', (0,0), (-1,-1), 12),
    ]))
    story.append(t_sum)

    story.append(Spacer(1, 35))
    story.append(Paragraph("<font size='12' color='#1E3A8A'><b>Universidad Nacional de Villa Mercedes (UNViME) - Cátedra de Compiladores</b></font>", body_text))
    story.append(Paragraph("Proyecto finalizado y verificado con éxito.", body_text))

    doc.build(story, canvasmaker=NumberedCanvas)
    print("PDF generado exitosamente en:", PDF_PATH)

if __name__ == "__main__":
    build_pdf()
