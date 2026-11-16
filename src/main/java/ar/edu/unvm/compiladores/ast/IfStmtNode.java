package ar.edu.unvm.compiladores.ast;

import java.util.Collections;
import java.util.List;

/**
 * Nodo del AST que representa una sentencia condicional:
 * if (condicion) { sentencias } [ else { sentencias } ]
 */
public class IfStmtNode extends StatementNode {
    private final ExprNode condition;
    private final List<StatementNode> thenBranch;
    private final List<StatementNode> elseBranch;

    public IfStmtNode(ExprNode condition, List<StatementNode> thenBranch, List<StatementNode> elseBranch, int line, int column) {
        super(line, column);
        this.condition = condition;
        this.thenBranch = (thenBranch != null) ? thenBranch : Collections.emptyList();
        this.elseBranch = elseBranch;
    }

    public ExprNode getCondition() {
        return condition;
    }

    public List<StatementNode> getThenBranch() {
        return thenBranch;
    }

    public List<StatementNode> getElseBranch() {
        return elseBranch;
    }

    public boolean hasElse() {
        return elseBranch != null && !elseBranch.isEmpty();
    }

    @Override
    public void printTree(String prefix, boolean isTail) {
        System.out.println(prefix + (isTail ? "\\-- " : "+-- ") + "[If]");
        String childPrefix = prefix + (isTail ? "    " : "|   ");

        System.out.println(childPrefix + "+-- Condicion:");
        condition.printTree(childPrefix + "|   ", true);

        System.out.println(childPrefix + (hasElse() ? "+-- " : "\\-- ") + "Then:");
        for (int i = 0; i < thenBranch.size(); i++) {
            thenBranch.get(i).printTree(childPrefix + (hasElse() ? "|   " : "    "), i == thenBranch.size() - 1);
        }

        if (hasElse()) {
            System.out.println(childPrefix + "\\-- Else:");
            for (int i = 0; i < elseBranch.size(); i++) {
                elseBranch.get(i).printTree(childPrefix + "    ", i == elseBranch.size() - 1);
            }
        }
    }
}
