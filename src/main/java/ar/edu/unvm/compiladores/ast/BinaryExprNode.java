package ar.edu.unvm.compiladores.ast;

public class BinaryExprNode extends ExprNode {
    public enum Operator {
        ADD("+"),
        SUB("-"),
        MUL("*"),
        DIV("/"),
        LT("<"),
        GT(">"),
        LE("<="),
        GE(">="),
        EQ("=="),
        NEQ("!=");

        private final String symbol;

        Operator(String symbol) {
            this.symbol = symbol;
        }

        public String getSymbol() {
            return symbol;
        }

        public boolean isComparison() {
            return this == LT || this == GT || this == LE || this == GE || this == EQ || this == NEQ;
        }

        @Override
        public String toString() {
            return symbol;
        }
    }

    private final Operator operator;
    private final ExprNode left;
    private final ExprNode right;

    public BinaryExprNode(Operator operator, ExprNode left, ExprNode right, int line, int column) {
        super(line, column);
        this.operator = operator;
        this.left = left;
        this.right = right;
    }

    public Operator getOperator() {
        return operator;
    }

    public ExprNode getLeft() {
        return left;
    }

    public ExprNode getRight() {
        return right;
    }

    @Override
    public void printTree(String prefix, boolean isTail) {
        System.out.println(prefix + (isTail ? "\\-- " : "+-- ") + "(" + operator + ")");
        String childPrefix = prefix + (isTail ? "    " : "|   ");
        left.printTree(childPrefix, false);
        right.printTree(childPrefix, true);
    }
}
