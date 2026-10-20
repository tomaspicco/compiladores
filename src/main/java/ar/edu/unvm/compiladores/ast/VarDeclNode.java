package ar.edu.unvm.compiladores.ast;

import ar.edu.unvm.compiladores.symbols.Symbol;

/**
 * Nodo del AST que representa la DECLARACION de una variable.
 * Se separa explicitamente de los nodos de uso de variable (VarUseNode / VariableRefNode)
 * segun lo establecido en la jerarquia del arbol sintactico.
 */
public class VarDeclNode extends ASTNode {
    private final Type type;
    private final String id;
    private Symbol symbol;

    public VarDeclNode(Type type, String id, int line, int column) {
        super(line, column);
        this.type = type;
        this.id = id;
    }

    public Type getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public Symbol getSymbol() {
        return symbol;
    }

    public void setSymbol(Symbol symbol) {
        this.symbol = symbol;
    }

    @Override
    public void printTree(String prefix, boolean isTail) {
        String symInfo = (symbol != null) ? " -> Symbol(" + symbol.getName() + ": " + symbol.getType() + ")" : "";
        System.out.println(prefix + (isTail ? "\\-- " : "+-- ") + "[Declaracion] " + type + " " + id + symInfo);
    }
}
