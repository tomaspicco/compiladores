package ar.edu.unvm.compiladores.ast;

public class NumberLiteralNode extends ExprNode {
    private final int value;

    public NumberLiteralNode(int value, int line, int column) {
        super(line, column);
        this.value = value;
        setInferredType(Type.INT);
    }

    public int getValue() {
        return value;
    }

    @Override
    public void printTree(String prefix, boolean isTail) {
        System.out.println(prefix + (isTail ? "\\-- " : "+-- ") + value);
    }
}
