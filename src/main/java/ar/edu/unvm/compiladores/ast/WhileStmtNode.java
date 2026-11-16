package ar.edu.unvm.compiladores.ast;

import java.util.Collections;
import java.util.List;

/**
 * Nodo del AST que representa un bucle:
 * while (condicion) { sentencias }
 */
public class WhileStmtNode extends StatementNode {
    private final ExprNode condition;
    private final List<StatementNode> body;

    public WhileStmtNode(ExprNode condition, List<StatementNode> body, int line, int column) {
        super(line, column);
        this.condition = condition;
        this.body = (body != null) ? body : Collections.emptyList();
    }

    public ExprNode getCondition() {
        return condition;
    }

    public List<StatementNode> getBody() {
        return body;
    }

    @Override
    public void printTree(String prefix, boolean isTail) {
        System.out.println(prefix + (isTail ? "\\-- " : "+-- ") + "[While]");
        String childPrefix = prefix + (isTail ? "    " : "|   ");

        System.out.println(childPrefix + "+-- Condicion:");
        condition.printTree(childPrefix + "|   ", true);

        System.out.println(childPrefix + "\\-- Cuerpo:");
        for (int i = 0; i < body.size(); i++) {
            body.get(i).printTree(childPrefix + "    ", i == body.size() - 1);
        }
    }
}
