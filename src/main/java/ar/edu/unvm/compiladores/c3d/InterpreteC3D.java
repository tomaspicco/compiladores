package ar.edu.unvm.compiladores.c3d;

import ar.edu.unvm.compiladores.symbols.Symbol;
import java.util.Vector;

/**
 * Interprete / Evaluador del Codigo de Tres Direcciones.
 * Recorre secuencialmente el vector de instrucciones (desde arriba hacia abajo)
 * evaluando los valores de los simbolos (constantes, temporales y variables).
 */
public class InterpreteC3D {
    private Object returnValue = null;
    private boolean returned = false;

    public Object getReturnValue() {
        return returnValue;
    }

    public boolean hasReturned() {
        return returned;
    }

    public void ejecutar(CodigoTresDirecciones c3d) {
        returned = false;
        returnValue = null;
        Vector<Instruccion3D> instrs = c3d.getInstrucciones();

        for (int i = 0; i < instrs.size(); i++) {
            if (returned) {
                break;
            }
            Instruccion3D inst = instrs.get(i);
            ejecutarInstruccion(inst);
        }
    }

    private void ejecutarInstruccion(Instruccion3D inst) {
        Operador3D op = inst.getOperador();
        Symbol op1 = inst.getOperando1();
        Symbol op2 = inst.getOperando2();
        Symbol res = inst.getResultado();

        switch (op) {
            case SUMA: {
                int v1 = toInt(op1.getValue());
                int v2 = toInt(op2.getValue());
                res.setValue(v1 + v2);
                break;
            }
            case RESTA: {
                int v1 = toInt(op1.getValue());
                int v2 = toInt(op2.getValue());
                res.setValue(v1 - v2);
                break;
            }
            case MULT: {
                int v1 = toInt(op1.getValue());
                int v2 = toInt(op2.getValue());
                res.setValue(v1 * v2);
                break;
            }
            case DIV: {
                int v1 = toInt(op1.getValue());
                int v2 = toInt(op2.getValue());
                if (v2 == 0) {
                    throw new ArithmeticException("Division por cero durante la ejecucion de C3D.");
                }
                res.setValue(v1 / v2);
                break;
            }
            case ASIGNACION: {
                res.setValue(op1.getValue());
                break;
            }
            case RETORNO: {
                this.returnValue = (op1 != null) ? op1.getValue() : null;
                this.returned = true;
                break;
            }
        }
    }

    private int toInt(Object obj) {
        if (obj instanceof Number) {
            return ((Number) obj).intValue();
        }
        if (obj instanceof Boolean) {
            return ((Boolean) obj) ? 1 : 0;
        }
        throw new IllegalArgumentException("No se puede convertir a entero: " + obj);
    }
}
