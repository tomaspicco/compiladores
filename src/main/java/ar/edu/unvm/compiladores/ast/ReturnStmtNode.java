package ar.edu.unvm.compiladores.ast;

public class ReturnStmtNode extends StatementNode {
    private final ExprNode expr;

    public ReturnStmtNode(ExprNode expr, int line, int column) {
        super(line, column);
        this.expr = expr;
    }

    public ExprNode getExpr() {
        return expr;
    }

    public boolean hasValue() {
        return expr != null;
    }

    @Override
    public void printTree(String prefix, boolean isTail) {
        if (expr != null) {
            System.out.println(prefix + (isTail ? "\\-- " : "+-- ") + "return");
            expr.printTree(prefix + (isTail ? "    " : "|   "), true);
        } else {
            System.out.println(prefix + (isTail ? "\\-- " : "+-- ") + "return;");
        }
    }
}
