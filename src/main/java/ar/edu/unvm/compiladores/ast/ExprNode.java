package ar.edu.unvm.compiladores.ast;

public abstract class ExprNode extends ASTNode {
    private Type inferredType;

    public ExprNode(int line, int column) {
        super(line, column);
    }

    public Type getInferredType() {
        return inferredType;
    }

    public void setInferredType(Type inferredType) {
        this.inferredType = inferredType;
    }
}
