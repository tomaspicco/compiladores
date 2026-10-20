package ar.edu.unvm.compiladores.semantic;

import ar.edu.unvm.compiladores.ast.*;
import ar.edu.unvm.compiladores.symbols.Symbol;
import ar.edu.unvm.compiladores.symbols.SymbolTable;

import java.util.ArrayList;
import java.util.List;

/**
 * Analizador Semantico del Compilador.
 *
 * Segun lo acordado en la reunion del 13 oct 2026, el analisis semantico
 * verifica especificamente tres tipos principales de errores:
 * 1. Variables redeclaradas (redeclaracion en el mismo ambito).
 * 2. Variables no declaradas (uso o asignacion de variable sin declaracion previa).
 * 3. Errores de tipo (incompatibilidad de tipos en asignaciones, operadores o retornos).
 *
 * Ademas, enlaza los nodos de declaracion y uso del AST directamente con la
 * entrada correspondiente en la Tabla de Simbolos.
 */
public class SemanticAnalyzer {

    public enum ErrorType {
        VARIABLE_REDECLARADA("VARIABLE REDECLARADA"),
        VARIABLE_NO_DECLARADA("VARIABLE NO DECLARADA"),
        ERROR_DE_TIPO("ERROR DE TIPO");

        private final String label;

        ErrorType(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    public static class CompilationError {
        private final ErrorType type;
        private final int line;
        private final String message;

        public CompilationError(ErrorType type, int line, String message) {
            this.type = type;
            this.line = line;
            this.message = message;
        }

        public ErrorType getType() {
            return type;
        }

        public int getLine() {
            return line;
        }

        public String getMessage() {
            return message;
        }

        @Override
        public String toString() {
            return String.format("[%s] (linea %d): %s", type.getLabel(), line, message);
        }
    }

    private final SymbolTable symbolTable = new SymbolTable();
    private final List<CompilationError> errors = new ArrayList<>();

    public SymbolTable getSymbolTable() {
        return symbolTable;
    }

    public List<String> getErrors() {
        List<String> list = new ArrayList<>();
        for (CompilationError err : errors) {
            list.add(err.toString());
        }
        return list;
    }

    public List<CompilationError> getCompilationErrors() {
        return errors;
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public boolean analyze(ProgramNode program) {
        errors.clear();

        // 1. Recorrido de declaraciones: verificar variables redeclaradas y enlazar con Symbol
        for (VarDeclNode decl : program.getDeclarations()) {
            if (symbolTable.contains(decl.getId())) {
                errors.add(new CompilationError(
                        ErrorType.VARIABLE_REDECLARADA,
                        decl.getLine(),
                        "la variable '" + decl.getId() + "' ya fue declarada previamente."
                ));
            } else {
                symbolTable.declare(decl.getId(), decl.getType(), decl.getLine(), decl.getColumn());
                Symbol sym = symbolTable.lookup(decl.getId());
                decl.setSymbol(sym);
            }
        }

        // 2. Recorrido de sentencias: verificar variables no declaradas y compatibilidad de tipos
        for (StatementNode stmt : program.getStatements()) {
            analyzeStatement(stmt, program.getReturnType());
        }

        return !hasErrors();
    }

    private void analyzeStatement(StatementNode stmt, Type programReturnType) {
        if (stmt instanceof AssignStmtNode) {
            AssignStmtNode assign = (AssignStmtNode) stmt;
            Symbol sym = symbolTable.lookup(assign.getId());

            if (sym == null) {
                errors.add(new CompilationError(
                        ErrorType.VARIABLE_NO_DECLARADA,
                        assign.getLine(),
                        "la variable '" + assign.getId() + "' no esta declarada."
                ));
            } else {
                assign.setSymbol(sym);
            }

            analyzeExpr(assign.getExpr());

            if (sym != null && assign.getExpr().getInferredType() != null) {
                if (sym.getType() != assign.getExpr().getInferredType()) {
                    errors.add(new CompilationError(
                            ErrorType.ERROR_DE_TIPO,
                            assign.getLine(),
                            "tipo incompatible en asignacion a '" + assign.getId()
                                    + "'. Se esperaba " + sym.getType()
                                    + " pero se obtuvo " + assign.getExpr().getInferredType() + "."
                    ));
                }
            }
        } else if (stmt instanceof ReturnStmtNode) {
            ReturnStmtNode ret = (ReturnStmtNode) stmt;
            if (ret.hasValue()) {
                if (programReturnType == Type.VOID) {
                    errors.add(new CompilationError(
                            ErrorType.ERROR_DE_TIPO,
                            ret.getLine(),
                            "la funcion main es de tipo VOID y no debe retornar un valor."
                    ));
                }
                analyzeExpr(ret.getExpr());
                if (programReturnType != Type.VOID && ret.getExpr().getInferredType() != null) {
                    if (ret.getExpr().getInferredType() != programReturnType) {
                        errors.add(new CompilationError(
                                ErrorType.ERROR_DE_TIPO,
                                ret.getLine(),
                                "tipo de retorno incompatible. Se esperaba "
                                        + programReturnType + " pero se obtuvo "
                                        + ret.getExpr().getInferredType() + "."
                        ));
                    }
                }
            } else {
                if (programReturnType != Type.VOID) {
                    errors.add(new CompilationError(
                            ErrorType.ERROR_DE_TIPO,
                            ret.getLine(),
                            "la funcion main debe retornar un valor de tipo " + programReturnType + "."
                    ));
                }
            }
        }
    }

    private void analyzeExpr(ExprNode expr) {
        if (expr instanceof NumberLiteralNode) {
            expr.setInferredType(Type.INT);
        } else if (expr instanceof BooleanLiteralNode) {
            expr.setInferredType(Type.BOOL);
        } else if (expr instanceof VariableRefNode) {
            VariableRefNode varRef = (VariableRefNode) expr;
            Symbol sym = symbolTable.lookup(varRef.getId());
            if (sym == null) {
                errors.add(new CompilationError(
                        ErrorType.VARIABLE_NO_DECLARADA,
                        varRef.getLine(),
                        "la variable '" + varRef.getId() + "' no esta declarada."
                ));
            } else {
                varRef.setSymbol(sym);
                varRef.setInferredType(sym.getType());
            }
        } else if (expr instanceof BinaryExprNode) {
            BinaryExprNode bin = (BinaryExprNode) expr;
            analyzeExpr(bin.getLeft());
            analyzeExpr(bin.getRight());

            Type leftType = bin.getLeft().getInferredType();
            Type rightType = bin.getRight().getInferredType();

            if (leftType != null && leftType != Type.INT) {
                errors.add(new CompilationError(
                        ErrorType.ERROR_DE_TIPO,
                        bin.getLine(),
                        "el operador aritmetico '" + bin.getOperator()
                                + "' requiere enteros pero el lado izquierdo es " + leftType + "."
                ));
            }
            if (rightType != null && rightType != Type.INT) {
                errors.add(new CompilationError(
                        ErrorType.ERROR_DE_TIPO,
                        bin.getLine(),
                        "el operador aritmetico '" + bin.getOperator()
                                + "' requiere enteros pero el lado derecho es " + rightType + "."
                ));
            }

            bin.setInferredType(Type.INT);
        }
    }
}
