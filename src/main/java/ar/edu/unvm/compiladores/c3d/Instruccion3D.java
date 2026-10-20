package ar.edu.unvm.compiladores.c3d;

import ar.edu.unvm.compiladores.symbols.Symbol;

/**
 * Representa una instruccion de codigo de tres direcciones (C3D).
 * Segun lo acordado en la reunion (00:14:05), cada instruccion contiene un
 * operador y punteros/referencias directas a los objetos Symbol correspondientes
 * (operandos y resultado), preservando toda su informacion semantica (nombre, tipo, valor).
 */
public class Instruccion3D {
    private final Operador3D operador;
    private final Symbol operando1;
    private final Symbol operando2;
    private final Symbol resultado;

    public Instruccion3D(Operador3D operador, Symbol operando1, Symbol operando2, Symbol resultado) {
        this.operador = operador;
        this.operando1 = operando1;
        this.operando2 = operando2;
        this.resultado = resultado;
    }

    public Operador3D getOperador() {
        return operador;
    }

    public Symbol getOperando1() {
        return operando1;
    }

    public Symbol getOperando2() {
        return operando2;
    }

    public Symbol getResultado() {
        return resultado;
    }

    /**
     * Representacion estandar de la instruccion de tres direcciones:
     * Ejemplos:
     *   T1 = 3 * 2
     *   T2 = T1 + y
     *   x = T2
     *   return x
     */
    @Override
    public String toString() {
        switch (operador) {
            case SUMA:
            case RESTA:
            case MULT:
            case DIV:
                return String.format("%s = %s %s %s",
                        (resultado != null ? resultado.getDisplayName() : "?"),
                        (operando1 != null ? operando1.getDisplayName() : "?"),
                        operador.getSigno(),
                        (operando2 != null ? operando2.getDisplayName() : "?"));
            case ASIGNACION:
                return String.format("%s = %s",
                        (resultado != null ? resultado.getDisplayName() : "?"),
                        (operando1 != null ? operando1.getDisplayName() : "?"));
            case RETORNO:
                if (operando1 != null) {
                    return "return " + operando1.getDisplayName();
                } else {
                    return "return";
                }
            default:
                return operador + " " + operando1 + " " + operando2 + " " + resultado;
        }
    }

    /**
     * Representacion clasica en forma de cuadrupla (OP, ARG1, ARG2, RES).
     */
    public String toCuadrupla() {
        String op = operador.getMnemonico();
        String arg1 = (operando1 != null) ? operando1.getDisplayName() : "-";
        String arg2 = (operando2 != null) ? operando2.getDisplayName() : "-";
        String res = (resultado != null) ? resultado.getDisplayName() : "-";
        return String.format("(%-6s, %-6s, %-6s, %-6s)", op, arg1, arg2, res);
    }
}
