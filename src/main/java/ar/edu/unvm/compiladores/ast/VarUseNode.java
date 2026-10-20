package ar.edu.unvm.compiladores.ast;

/**
 * Nodo del AST para el USO de una variable en una expresion.
 * Extiende VariableRefNode para mantener compatibilidad total y cumplir directamente
 * con el acuerdo de separar la jerarquia entre nodos de declaracion (VarDeclNode)
 * y nodos de uso (VarUseNode).
 */
public class VarUseNode extends VariableRefNode {
    public VarUseNode(String id, int line, int column) {
        super(id, line, column);
    }
}
