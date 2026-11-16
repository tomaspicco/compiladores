package ar.edu.unvm.compiladores.lexer;

import java_cup.runtime.Symbol;
import ar.edu.unvm.compiladores.parser.sym;

%%

%class Lexer
%public
%unicode
%line
%column
%cup

/* Al llegar al final de archivo, notificar a CUP con EOF */
%eofval{
    return symbol(sym.EOF);
%eofval}

%{
    /* Métodos auxiliares para construir el Symbol que espera CUP.
       Se suma 1 para que las líneas y columnas sean 1-indexadas (humanamente legibles). */
    private Symbol symbol(int tipo) {
        return new Symbol(tipo, yyline + 1, yycolumn + 1);
    }

    private Symbol symbol(int tipo, Object valor) {
        return new Symbol(tipo, yyline + 1, yycolumn + 1, valor);
    }
%}

/* ==== Conjuntos auxiliares (Punto 2 del TP) ==== */
Letra             = [a-zA-Z]
Digito            = [0-9]
Id                = {Letra}({Letra}|{Digito})*
Nro               = {Digito}+
EspacioBlanco     = [ \t\r\n]+
ComentarioLinea   = "//" [^\r\n]*
ComentarioBloque  = "/*" [^*]* ~"*/" | "/*" "*"+ "/"

%%

/* ==== Palabras reservadas ==== */
"void"          { return symbol(sym.VOID); }
"int"           { return symbol(sym.INT); }
"bool"          { return symbol(sym.BOOL); }
"main"          { return symbol(sym.MAIN); }
"return"        { return symbol(sym.RETURN); }
"if"            { return symbol(sym.IF); }
"else"          { return symbol(sym.ELSE); }
"while"         { return symbol(sym.WHILE); }
"true"          { return symbol(sym.TRUE); }
"false"         { return symbol(sym.FALSE); }

/* ==== Constantes numericas e identificadores ==== */
{Nro}           { return symbol(sym.NRO, Integer.valueOf(yytext())); }
{Id}            { return symbol(sym.ID, yytext()); }

/* ==== Operadores ==== */
"+"             { return symbol(sym.MAS); }
"-"             { return symbol(sym.MENOS); }
"*"             { return symbol(sym.POR); }
"/"             { return symbol(sym.DIV); }
"=="            { return symbol(sym.IGUAL_IGUAL); }
"!="            { return symbol(sym.DISTINTO); }
"<="            { return symbol(sym.MENOR_IGUAL); }
">="            { return symbol(sym.MAYOR_IGUAL); }
"<"             { return symbol(sym.MENOR); }
">"             { return symbol(sym.MAYOR); }
"="             { return symbol(sym.ASIGNACION); }

/* ==== Delimitadores ==== */
";"             { return symbol(sym.PUNTOYCOMA); }
"("             { return symbol(sym.PAR_IZQ); }
")"             { return symbol(sym.PAR_DER); }
"{"             { return symbol(sym.LLAVE_IZQ); }
"}"             { return symbol(sym.LLAVE_DER); }

/* ==== Espacios en blanco y comentarios: se ignoran ==== */
{EspacioBlanco}     { /* ignorar */ }
{ComentarioLinea}   { /* ignorar */ }
{ComentarioBloque}  { /* ignorar */ }

/* ==== Cualquier otro caracter: error lexico ==== */
.               {
                    System.err.println("Error lexico: caracter no reconocido '" + yytext()
                            + "' en linea " + (yyline + 1) + ", columna " + (yycolumn + 1));
                    throw new RuntimeException("Error lexico: caracter no reconocido '" + yytext()
                            + "' en linea " + (yyline + 1) + ", columna " + (yycolumn + 1));
                }
