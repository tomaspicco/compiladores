package ar.edu.unvm.compiladores.interpreter;

import ar.edu.unvm.compiladores.ast.*;
import ar.edu.unvm.compiladores.symbols.Symbol;
import ar.edu.unvm.compiladores.symbols.SymbolTable;

public class Interpreter {
    private final SymbolTable symbolTable;
    private Object returnValue = null;
    private boolean returned = false;

    public Interpreter(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }

    public Object getReturnValue() {
        return returnValue;
    }

    public boolean hasReturned() {
        return returned;
    }

    public void execute(ProgramNode program) {
        for (StatementNode stmt : program.getStatements()) {
            if (returned) {
                break;
            }
            executeStatement(stmt);
        }

        if (returned) {
            System.out.println("Return: " + returnValue);
        }
    }

    private void executeStatement(StatementNode stmt) {
        if (stmt instanceof AssignStmtNode) {
            AssignStmtNode assign = (AssignStmtNode) stmt;
            Object val = evaluateExpr(assign.getExpr());

            Symbol sym = assign.getSymbol();
            if (sym == null) {
                sym = symbolTable.lookup(assign.getId());
            }

            if (sym != null) {
                sym.setValue(val);
                System.out.println(assign.getId() + " = " + val);
            }
        } else if (stmt instanceof ReturnStmtNode) {
            ReturnStmtNode ret = (ReturnStmtNode) stmt;
            if (ret.hasValue()) {
                this.returnValue = evaluateExpr(ret.getExpr());
            } else {
                this.returnValue = null;
            }
            this.returned = true;
        }
    }

    public Object evaluateExpr(ExprNode expr) {
        if (expr instanceof NumberLiteralNode) {
            return ((NumberLiteralNode) expr).getValue();
        } else if (expr instanceof BooleanLiteralNode) {
            return ((BooleanLiteralNode) expr).isValue();
        } else if (expr instanceof VariableRefNode) {
            VariableRefNode varRef = (VariableRefNode) expr;
            Symbol sym = varRef.getSymbol();
            if (sym == null) {
                sym = symbolTable.lookup(varRef.getId());
            }
            if (sym != null) {
                return sym.getValue();
            } else {
                throw new RuntimeException("Variable no declarada: " + varRef.getId());
            }
        } else if (expr instanceof BinaryExprNode) {
            BinaryExprNode bin = (BinaryExprNode) expr;
            Object leftObj = evaluateExpr(bin.getLeft());
            Object rightObj = evaluateExpr(bin.getRight());

            if (!(leftObj instanceof Integer) || !(rightObj instanceof Integer)) {
                throw new RuntimeException("Operacion sobre tipos no enteros.");
            }

            int left = (Integer) leftObj;
            int right = (Integer) rightObj;

            switch (bin.getOperator()) {
                case ADD:
                    return left + right;
                case SUB:
                    return left - right;
                case MUL:
                    return left * right;
                case DIV:
                    if (right == 0) {
                        throw new ArithmeticException("Division por cero en linea " + bin.getLine());
                    }
                    return left / right;
                default:
                    throw new UnsupportedOperationException("Operador desconocido: " + bin.getOperator());
            }
        }

        throw new IllegalArgumentException("Nodo desconocido.");
    }
}
