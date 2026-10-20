package ar.edu.unvm.compiladores.ast;

public abstract class StatementNode extends ASTNode {
    public StatementNode(int line, int column) {
        super(line, column);
    }
}
