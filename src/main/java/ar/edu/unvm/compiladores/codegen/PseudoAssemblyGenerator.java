package ar.edu.unvm.compiladores.codegen;

import ar.edu.unvm.compiladores.ast.*;

import java.util.ArrayList;
import java.util.List;

public class PseudoAssemblyGenerator {
    private int registerCount = 0;
    private final List<String> instructions = new ArrayList<>();

    private String newRegister() {
        return "R" + (registerCount++);
    }

    public String generate(ProgramNode program) {
        instructions.clear();
        registerCount = 0;

        emit(".DATA");
        for (VarDeclNode decl : program.getDeclarations()) {
            String typeDirective = (decl.getType() == Type.INT) ? ".INT" : ".BOOL";
            emit(String.format("    %-10s %-6s 0", decl.getId() + ":", typeDirective));
        }
        emit("");

        emit(".CODE");
        emit(program.getName() + ":");

        for (StatementNode stmt : program.getStatements()) {
            generateStatement(stmt);
        }

        emit("    HALT");

        return String.join(System.lineSeparator(), instructions);
    }

    private void generateStatement(StatementNode stmt) {
        if (stmt instanceof AssignStmtNode) {
            AssignStmtNode assign = (AssignStmtNode) stmt;
            String resultReg = generateExpr(assign.getExpr());
            emit("    STORE       " + assign.getId() + ", " + resultReg);
        } else if (stmt instanceof ReturnStmtNode) {
            ReturnStmtNode ret = (ReturnStmtNode) stmt;
            if (ret.hasValue()) {
                String resultReg = generateExpr(ret.getExpr());
                emit("    RET         " + resultReg);
            } else {
                emit("    RET");
            }
        }
    }

    private String generateExpr(ExprNode expr) {
        if (expr instanceof NumberLiteralNode) {
            NumberLiteralNode num = (NumberLiteralNode) expr;
            String reg = newRegister();
            emit("    LOAD_IMM    " + reg + ", " + num.getValue());
            return reg;
        } else if (expr instanceof BooleanLiteralNode) {
            BooleanLiteralNode b = (BooleanLiteralNode) expr;
            String reg = newRegister();
            emit("    LOAD_IMM    " + reg + ", " + (b.isValue() ? 1 : 0));
            return reg;
        } else if (expr instanceof VariableRefNode) {
            VariableRefNode varRef = (VariableRefNode) expr;
            String reg = newRegister();
            emit("    LOAD        " + reg + ", " + varRef.getId());
            return reg;
        } else if (expr instanceof BinaryExprNode) {
            BinaryExprNode bin = (BinaryExprNode) expr;
            String leftReg = generateExpr(bin.getLeft());
            String rightReg = generateExpr(bin.getRight());
            String destReg = newRegister();

            String mnemonic;
            switch (bin.getOperator()) {
                case ADD: mnemonic = "ADD"; break;
                case SUB: mnemonic = "SUB"; break;
                case MUL: mnemonic = "MUL"; break;
                case DIV: mnemonic = "DIV"; break;
                default:  mnemonic = "OP";  break;
            }

            emit(String.format("    %-11s %s, %s, %s", mnemonic, destReg, leftReg, rightReg));
            return destReg;
        }

        String reg = newRegister();
        return reg;
    }

    private void emit(String line) {
        instructions.add(line);
    }
}
