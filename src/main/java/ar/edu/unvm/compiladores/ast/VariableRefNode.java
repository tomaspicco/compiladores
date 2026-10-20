package ar.edu.unvm.compiladores.ast;

import ar.edu.unvm.compiladores.symbols.Symbol;

/**
 * Nodo del AST que representa el USO / REFERENCIA de una variable dentro de una expresion.
 * Se diferencia en la jerarquia del arbol del nodo de declaracion (VarDeclNode).
 */
public class VariableRefNode extends ExprNode {
    private final String id;
    private Symbol symbol;

    public VariableRefNode(String id, int line, int column) {
        super(line, column);
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public Symbol getSymbol() {
        return symbol;
    }

    public void setSymbol(Symbol symbol) {
        this.symbol = symbol;
        if (symbol != null) {
            setInferredType(symbol.getType());
        }
    }

    @Override
    public void printTree(String prefix, boolean isTail) {
        String symInfo = (symbol != null) ? " -> Symbol(" + symbol.getName() + ": " + symbol.getType() + ")" : "";
        System.out.println(prefix + (isTail ? "\\-- " : "+-- ") + "[Uso] " + id + symInfo);
    }
}
