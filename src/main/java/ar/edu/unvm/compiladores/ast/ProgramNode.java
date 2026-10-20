package ar.edu.unvm.compiladores.ast;

import java.util.Collections;
import java.util.List;

public class ProgramNode extends ASTNode {
    private final Type returnType;
    private final String name;
    private final List<VarDeclNode> declarations;
    private final List<StatementNode> statements;

    public ProgramNode(Type returnType, String name, List<VarDeclNode> declarations, List<StatementNode> statements, int line, int column) {
        super(line, column);
        this.returnType = returnType;
        this.name = name;
        this.declarations = (declarations != null) ? declarations : Collections.emptyList();
        this.statements = (statements != null) ? statements : Collections.emptyList();
    }

    public Type getReturnType() {
        return returnType;
    }

    public String getName() {
        return name;
    }

    public List<VarDeclNode> getDeclarations() {
        return declarations;
    }

    public List<StatementNode> getStatements() {
        return statements;
    }

    @Override
    public void printTree(String prefix, boolean isTail) {
        System.out.println(prefix + (isTail ? "\\-- " : "+-- ") + "Program: " + returnType + " " + name + "()");
        String childPrefix = prefix + (isTail ? "    " : "|   ");

        System.out.println(childPrefix + "+-- Declaraciones:");
        for (int i = 0; i < declarations.size(); i++) {
            declarations.get(i).printTree(childPrefix + "|   ", i == declarations.size() - 1);
        }

        System.out.println(childPrefix + "\\-- Sentencias:");
        for (int i = 0; i < statements.size(); i++) {
            statements.get(i).printTree(childPrefix + "    ", i == statements.size() - 1);
        }
    }
}
