package ar.edu.unvm.compiladores.c3d;

import ar.edu.unvm.compiladores.ast.*;
import ar.edu.unvm.compiladores.symbols.Symbol;
import ar.edu.unvm.compiladores.symbols.SymbolTable;

/**
 * Generador de Codigo de Tres Direcciones (C3D).
 *
 * Realiza un recorrido post-orden (bottom-up: desde las hojas hacia la raiz)
 * sobre el arbol sintactico abstracto (AST) verificado.
 *
 * Resuelve las operaciones intermedias generando variables temporales sucesivas (T1, T2, ...),
 * y enlaza cada instruccion con los objetos Symbol que contienen la informacion
 * semantica (nombre, tipo y valor).
 */
public class GeneradorC3D {
    private int tempCounter = 1;
    private int labelCounter = 1;
    private final SymbolTable symbolTable;

    public GeneradorC3D(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }

    /**
     * Genera una nueva variable temporal con tipo asociado.
     */
    public Symbol nuevoTemporal(Type type) {
        return Symbol.nuevoTemporal("T" + (tempCounter++), type);
    }

    /**
     * Genera una nueva etiqueta de salto (L1, L2, ...).
     */
    public Symbol nuevaEtiqueta() {
        return Symbol.nuevaEtiqueta("L" + (labelCounter++));
    }

    /**
     * Recorre el AST y genera el vector de instrucciones de tres direcciones.
     */
    public CodigoTresDirecciones generar(ProgramNode program) {
        tempCounter = 1;
        labelCounter = 1;
        CodigoTresDirecciones c3d = new CodigoTresDirecciones();

        for (StatementNode stmt : program.getStatements()) {
            generarSentencia(stmt, c3d);
        }

        return c3d;
    }

    /**
     * Procesa una sentencia (Asignacion o Retorno).
     */
    private void generarSentencia(StatementNode stmt, CodigoTresDirecciones c3d) {
        if (stmt instanceof AssignStmtNode) {
            AssignStmtNode assign = (AssignStmtNode) stmt;

            // Primero se resuelven las expresiones (hojas hacia la raiz)
            Symbol exprSymbol = generarExpr(assign.getExpr(), c3d);

            // Obtener el simbolo de la variable de destino
            Symbol destSymbol = assign.getSymbol();
            if (destSymbol == null && symbolTable != null) {
                destSymbol = symbolTable.lookup(assign.getId());
            }

            // Emitir la instruccion de asignacion (dest = exprSymbol)
            c3d.insertar(new Instruccion3D(Operador3D.ASIGNACION, exprSymbol, null, destSymbol));

        } else if (stmt instanceof ReturnStmtNode) {
            ReturnStmtNode ret = (ReturnStmtNode) stmt;
            if (ret.hasValue()) {
                Symbol retSymbol = generarExpr(ret.getExpr(), c3d);
                c3d.insertar(new Instruccion3D(Operador3D.RETORNO, retSymbol, null, null));
            } else {
                c3d.insertar(new Instruccion3D(Operador3D.RETORNO, null, null, null));
            }
        } else if (stmt instanceof IfStmtNode) {
            IfStmtNode ifStmt = (IfStmtNode) stmt;
            Symbol condSym = generarExpr(ifStmt.getCondition(), c3d);
            Symbol lElse = ifStmt.hasElse() ? nuevaEtiqueta() : null;
            Symbol lEnd = nuevaEtiqueta();

            Symbol lFalse = ifStmt.hasElse() ? lElse : lEnd;
            c3d.insertar(new Instruccion3D(Operador3D.IF_FALSE, condSym, lFalse, null));

            for (StatementNode s : ifStmt.getThenBranch()) {
                generarSentencia(s, c3d);
            }

            if (ifStmt.hasElse()) {
                c3d.insertar(new Instruccion3D(Operador3D.GOTO, lEnd, null, null));
                c3d.insertar(new Instruccion3D(Operador3D.LABEL, lElse, null, null));
                for (StatementNode s : ifStmt.getElseBranch()) {
                    generarSentencia(s, c3d);
                }
            }

            c3d.insertar(new Instruccion3D(Operador3D.LABEL, lEnd, null, null));

        } else if (stmt instanceof WhileStmtNode) {
            WhileStmtNode whileStmt = (WhileStmtNode) stmt;
            Symbol lStart = nuevaEtiqueta();
            Symbol lEnd = nuevaEtiqueta();

            c3d.insertar(new Instruccion3D(Operador3D.LABEL, lStart, null, null));

            Symbol condSym = generarExpr(whileStmt.getCondition(), c3d);
            c3d.insertar(new Instruccion3D(Operador3D.IF_FALSE, condSym, lEnd, null));

            for (StatementNode s : whileStmt.getBody()) {
                generarSentencia(s, c3d);
            }

            c3d.insertar(new Instruccion3D(Operador3D.GOTO, lStart, null, null));
            c3d.insertar(new Instruccion3D(Operador3D.LABEL, lEnd, null, null));
        }
    }

    /**
     * Resuelve expresiones de abajo hacia arriba (bottom-up), emitiendo
     * instrucciones con temporales para operaciones intermedias y retornando
     * el Symbol que contiene el resultado parcial.
     */
    private Symbol generarExpr(ExprNode expr, CodigoTresDirecciones c3d) {
        if (expr instanceof NumberLiteralNode) {
            NumberLiteralNode num = (NumberLiteralNode) expr;
            return Symbol.nuevaConstante(num.getValue(), Type.INT);

        } else if (expr instanceof BooleanLiteralNode) {
            BooleanLiteralNode b = (BooleanLiteralNode) expr;
            return Symbol.nuevaConstante(b.isValue(), Type.BOOL);

        } else if (expr instanceof VariableRefNode) {
            VariableRefNode varRef = (VariableRefNode) expr;
            if (varRef.getSymbol() != null) {
                return varRef.getSymbol();
            }
            if (symbolTable != null) {
                return symbolTable.lookup(varRef.getId());
            }
            return new Symbol(varRef.getId(), expr.getInferredType(), varRef.getLine(), varRef.getColumn());

        } else if (expr instanceof BinaryExprNode) {
            BinaryExprNode bin = (BinaryExprNode) expr;

            // Recorrido de las hojas hacia la raiz: primero resolver operandos izquierdo y derecho
            Symbol leftSymbol = generarExpr(bin.getLeft(), c3d);
            Symbol rightSymbol = generarExpr(bin.getRight(), c3d);

            Type resType = (bin.getInferredType() != null) ? bin.getInferredType() : Type.INT;
            Symbol tempSymbol = nuevoTemporal(resType);

            Operador3D op;
            switch (bin.getOperator()) {
                case ADD: op = Operador3D.SUMA; break;
                case SUB: op = Operador3D.RESTA; break;
                case MUL: op = Operador3D.MULT; break;
                case DIV: op = Operador3D.DIV; break;
                case LT: op = Operador3D.MENOR; break;
                case GT: op = Operador3D.MAYOR; break;
                case LE: op = Operador3D.MENOR_IGUAL; break;
                case GE: op = Operador3D.MAYOR_IGUAL; break;
                case EQ: op = Operador3D.IGUAL_IGUAL; break;
                case NEQ: op = Operador3D.DISTINTO; break;
                default:
                    throw new IllegalArgumentException("Operador desconocido en C3D: " + bin.getOperator());
            }

            // Cada operacion maneja como maximo dos operandos y un resultado
            c3d.insertar(new Instruccion3D(op, leftSymbol, rightSymbol, tempSymbol));
            return tempSymbol;
        }

        throw new UnsupportedOperationException("Expresion no soportada en C3D: " + expr.getClass().getSimpleName());
    }
}
