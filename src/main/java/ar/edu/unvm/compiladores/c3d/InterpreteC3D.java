package ar.edu.unvm.compiladores.c3d;

import ar.edu.unvm.compiladores.symbols.Symbol;
import java.util.HashMap;
import java.util.Map;
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

        // 1. Mapear las posiciones de las etiquetas (LABEL)
        Map<String, Integer> labelMap = new HashMap<>();
        for (int i = 0; i < instrs.size(); i++) {
            Instruccion3D inst = instrs.get(i);
            if (inst.getOperador() == Operador3D.LABEL && inst.getOperando1() != null) {
                labelMap.put(inst.getOperando1().getName(), i);
            }
        }

        // 2. Ejecucion con saltos y control de flujo
        int pc = 0;
        int maxSteps = 100000;
        int steps = 0;

        while (pc < instrs.size() && !returned) {
            if (++steps > maxSteps) {
                throw new RuntimeException("Bucle infinito o limite de pasos excedido en la ejecucion de C3D.");
            }
            Instruccion3D inst = instrs.get(pc);
            Operador3D op = inst.getOperador();

            if (op == Operador3D.LABEL) {
                pc++;
            } else if (op == Operador3D.GOTO) {
                String target = inst.getOperando1().getName();
                if (!labelMap.containsKey(target)) {
                    throw new RuntimeException("Etiqueta no encontrada en C3D: " + target);
                }
                pc = labelMap.get(target);
            } else if (op == Operador3D.IF_FALSE) {
                boolean cond = toBoolean(inst.getOperando1().getValue());
                if (!cond) {
                    String target = inst.getOperando2().getName();
                    if (!labelMap.containsKey(target)) {
                        throw new RuntimeException("Etiqueta no encontrada en C3D: " + target);
                    }
                    pc = labelMap.get(target);
                } else {
                    pc++;
                }
            } else {
                ejecutarInstruccion(inst);
                pc++;
            }
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
            case MENOR: {
                int v1 = toInt(op1.getValue());
                int v2 = toInt(op2.getValue());
                res.setValue(v1 < v2);
                break;
            }
            case MAYOR: {
                int v1 = toInt(op1.getValue());
                int v2 = toInt(op2.getValue());
                res.setValue(v1 > v2);
                break;
            }
            case MENOR_IGUAL: {
                int v1 = toInt(op1.getValue());
                int v2 = toInt(op2.getValue());
                res.setValue(v1 <= v2);
                break;
            }
            case MAYOR_IGUAL: {
                int v1 = toInt(op1.getValue());
                int v2 = toInt(op2.getValue());
                res.setValue(v1 >= v2);
                break;
            }
            case IGUAL_IGUAL: {
                Object v1 = op1.getValue();
                Object v2 = op2.getValue();
                res.setValue(v1 != null && v1.equals(v2));
                break;
            }
            case DISTINTO: {
                Object v1 = op1.getValue();
                Object v2 = op2.getValue();
                res.setValue(v1 == null ? v2 != null : !v1.equals(v2));
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

    private boolean toBoolean(Object obj) {
        if (obj instanceof Boolean) {
            return (Boolean) obj;
        }
        if (obj instanceof Number) {
            return ((Number) obj).intValue() != 0;
        }
        if (obj != null) {
            return Boolean.parseBoolean(obj.toString());
        }
        return false;
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
