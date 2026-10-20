package ar.edu.unvm.compiladores.ast;

public class BooleanLiteralNode extends ExprNode {
    private final boolean value;

    public BooleanLiteralNode(boolean value, int line, int column) {
        super(line, column);
        this.value = value;
        setInferredType(Type.BOOL);
    }

    public boolean isValue() {
        return value;
    }

    @Override
    public void printTree(String prefix, boolean isTail) {
        System.out.println(prefix + (isTail ? "\\-- " : "+-- ") + value);
    }
}
