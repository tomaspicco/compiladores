package ar.edu.unvm.compiladores.c3d;

/**
 * Operadores para Codigo de Tres Direcciones (C3D).
 */
public enum Operador3D {
    SUMA("+", "ADD"),
    RESTA("-", "SUB"),
    MULT("*", "MUL"),
    DIV("/", "DIV"),
    ASIGNACION("=", "ASSIGN"),
    RETORNO("return", "RET");

    private final String signo;
    private final String mnemonico;

    Operador3D(String signo, String mnemonico) {
        this.signo = signo;
        this.mnemonico = mnemonico;
    }

    public String getSigno() {
        return signo;
    }

    public String getMnemonico() {
        return mnemonico;
    }

    @Override
    public String toString() {
        return signo;
    }
}
