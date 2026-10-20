package ar.edu.unvm.compiladores.ast;

import ar.edu.unvm.compiladores.symbols.Symbol;

/**
 * Nodo del AST que representa una sentencia de asignacion: id = expr;
 */
public class AssignStmtNode extends StatementNode {
    private final String id;
    private final ExprNode expr;
    private Symbol symbol;

    public AssignStmtNode(String id, ExprNode expr, int line, int column) {
        super(line, column);
        this.id = id;
        this.expr = expr;
    }

    public String getId() {
        return id;
    }

    public ExprNode getExpr() {
        return expr;
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
        System.out.println(prefix + (isTail ? "\\-- " : "+-- ") + "[Asignacion] " + id + symInfo + " =");
        expr.printTree(prefix + (isTail ? "    " : "|   "), true);
    }
}
