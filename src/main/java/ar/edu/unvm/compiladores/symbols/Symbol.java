package ar.edu.unvm.compiladores.symbols;

import ar.edu.unvm.compiladores.ast.Type;

/**
 * Representa una entrada de simbolo en el compilador.
 * Puede representar:
 * - Una variable declarada en el programa (VARIABLE)
 * - Una variable temporal generada para C3D (TEMPORAL: T1, T2, ...)
 * - Un valor constante o literal (CONSTANTE: 10, true, false)
 *
 * Cada simbolo almacena su nombre/identificador, su tipo de dato,
 * su valor actual, su estado de inicializacion y su categoria.
 */
public class Symbol {
    public enum Kind {
        VARIABLE,
        TEMPORAL,
        CONSTANTE,
        ETIQUETA
    }

    private final String name;
    private final Type type;
    private Object value;
    private boolean initialized;
    private final int line;
    private final int column;
    private final Kind kind;

    /**
     * Constructor para variables declaradas por el usuario.
     */
    public Symbol(String name, Type type, int line, int column) {
        this.name = name;
        this.type = type;
        this.line = line;
        this.column = column;
        this.initialized = false;
        this.kind = Kind.VARIABLE;

        if (type == Type.INT) {
            this.value = 0;
        } else if (type == Type.BOOL) {
            this.value = false;
        } else {
            this.value = null;
        }
    }

    /**
     * Constructor completo.
     */
    public Symbol(String name, Type type, Object value, boolean initialized, int line, int column, Kind kind) {
        this.name = name;
        this.type = type;
        this.value = value;
        this.initialized = initialized;
        this.line = line;
        this.column = column;
        this.kind = kind;
    }

    /**
     * Fabrica para crear variables temporales de Codigo de Tres Direcciones (T1, T2, etc.).
     */
    public static Symbol nuevoTemporal(String name, Type type) {
        Object defaultVal = (type == Type.BOOL) ? false : 0;
        return new Symbol(name, type, defaultVal, false, 0, 0, Kind.TEMPORAL);
    }

    /**
     * Fabrica para crear operandos constantes / literales en C3D.
     */
    public static Symbol nuevaConstante(Object value, Type type) {
        return new Symbol(String.valueOf(value), type, value, true, 0, 0, Kind.CONSTANTE);
    }

    /**
     * Fabrica para crear etiquetas de saltos en C3D (L1, L2, etc.).
     */
    public static Symbol nuevaEtiqueta(String name) {
        return new Symbol(name, Type.VOID, null, true, 0, 0, Kind.ETIQUETA);
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
        this.initialized = true;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public Kind getKind() {
        return kind;
    }

    public boolean isVariable() {
        return kind == Kind.VARIABLE;
    }

    public boolean isTemporal() {
        return kind == Kind.TEMPORAL;
    }

    public boolean isConstant() {
        return kind == Kind.CONSTANTE;
    }

    public boolean isLabel() {
        return kind == Kind.ETIQUETA;
    }

    /**
     * Devuelve el nombre para presentacion o emision de codigo intermedio:
     * Si es constante, muestra su valor (ej: 4, true).
     * Si es variable o temporal, muestra su nombre (ej: x, T1).
     */
    public String getDisplayName() {
        if (kind == Kind.CONSTANTE) {
            return String.valueOf(value);
        }
        return name;
    }

    @Override
    public String toString() {
        if (kind == Kind.CONSTANTE) {
            return String.valueOf(value);
        }
        return String.format("Symbol{name='%s', tipo=%s, valor=%s, kind=%s}", name, type, value, kind);
    }
}
