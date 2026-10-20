package ar.edu.unvm.compiladores.ast;

public enum Type {
    INT,
    BOOL,
    VOID;

    @Override
    public String toString() {
        return name().toLowerCase();
    }

    public static Type fromString(String str) {
        if (str == null) return VOID;
        switch (str.toLowerCase()) {
            case "int": return INT;
            case "bool": return BOOL;
            case "void": return VOID;
            default: throw new IllegalArgumentException("Tipo desconocido: " + str);
        }
    }
}
