package ar.edu.unvm.compiladores.codegen;

import ar.edu.unvm.compiladores.ast.ProgramNode;
import ar.edu.unvm.compiladores.ast.VarDeclNode;
import ar.edu.unvm.compiladores.c3d.CodigoTresDirecciones;
import ar.edu.unvm.compiladores.c3d.Instruccion3D;
import ar.edu.unvm.compiladores.symbols.Symbol;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Generador de Assembly GNU x86-64 (Sintaxis AT&T para GNU Assembler / GCC / Emulador).
 *
 * Implementado siguiendo las directivas de la clase grabada (20 oct 2026) y la guía técnica:
 *
 * 1. Marco de Pila (Stack Frame) y Prólogo:
 *    - Cada variable local y temporal se ubica relativa al puntero base %rbp con
 *      desplazamientos de 8 en 8 bytes: -8(%rbp), -16(%rbp), -24(%rbp)... (-4 no existe en 64 bits).
 *    - Prólogo canónico trazado en clase para emulador y GCC:
 *        pushq %rbp
 *        movq  %rsp, %rbp
 *        subq  $N, %rsp     (reserva N bytes alineados a 16 bytes)
 *
 * 2. Restricción de Memoria en Ensamblador:
 *    - Como el procesador no permite operaciones aritméticas directas entre dos posiciones de memoria,
 *      se mueven primero los datos a registros temporales (%r10 y %r11).
 *
 * 3. Sufijo 'q' para 64 bits (Quadword):
 *    - Se utiliza explícitamente la notación de tamaño de 64 bits:
 *      movq, addq, subq, imulq, idivq.
 *
 * 4. Convención de Retorno y Epílogo:
 *    - El valor de retorno se sitúa en %rax.
 *    - Epílogo:
 *        movq  %rbp, %rsp
 *        popq  %rbp
 *        ret
 */
public class GnuAssemblyGenerator {

    private final List<String> instructions = new ArrayList<>();
    private final Map<String, Integer> stackMap = new LinkedHashMap<>();
    private final Map<String, String> displayNameMap = new LinkedHashMap<>();

    public String generate(ProgramNode program, CodigoTresDirecciones c3d) {
        instructions.clear();
        stackMap.clear();
        displayNameMap.clear();

        // 1. Reservar slots en la pila para las variables locales declaradas
        for (VarDeclNode decl : program.getDeclarations()) {
            allocateSlot("V_" + decl.getId(), decl.getId());
        }

        // 2. Reservar slots en la pila para los temporales de C3D (T1, T2, ...)
        for (Instruccion3D inst : c3d.getInstrucciones()) {
            registerSymbolSlot(inst.getResultado());
            registerSymbolSlot(inst.getOperando1());
            registerSymbolSlot(inst.getOperando2());
        }

        // Cálculo de memoria total en bytes (de 8 en 8 bytes por slot)
        int totalQuadwords = stackMap.size();
        int stackBytes = totalQuadwords * 8;
        // Alineación estándar a 16 bytes para ABI x86-64 y emuladores
        if (stackBytes % 16 != 0) {
            stackBytes += 8;
        }

        String funcName = program.getName();

        // Directivas de función para GNU Assembler y linker
        emit(".globl " + funcName);
        emit(funcName + ":");

        // Prólogo: configuracion del marco de pila
        emit("    # Prologo: armado del marco de pila (stack frame)");
        emit("    pushq %rbp");
        emit("    movq %rsp, %rbp");
        if (stackBytes > 0) {
            emit("    subq $" + stackBytes + ", %rsp");
        }
        emit("");

        // Tabla documental del stack frame
        emit("    # Distribucion de variables y temporales en la pila:");
        for (Map.Entry<String, Integer> entry : stackMap.entrySet()) {
            String name = displayNameMap.getOrDefault(entry.getKey(), entry.getKey());
            int offset = -8 * entry.getValue();
            emit(String.format("    #   %-10s -> %d(%%rbp)", name, offset));
        }
        emit("");

        // 3. Traducción instrucción por instrucción de C3D a Assembly
        for (Instruccion3D inst : c3d.getInstrucciones()) {
            generateInstruction(inst);
        }

        // Epílogo por defecto
        emit("    # Fin de funcion por defecto");
        emit("    movq $0, %rax");
        emit("    movq %rbp, %rsp");
        emit("    popq %rbp");
        emit("    ret");

        return String.join(System.lineSeparator(), instructions);
    }

    private void registerSymbolSlot(Symbol sym) {
        if (sym == null || sym.isConstant() || sym.isLabel()) {
            return;
        }
        String key = getSymbolKey(sym);
        allocateSlot(key, sym.getName());
    }

    private void allocateSlot(String key, String displayName) {
        if (!stackMap.containsKey(key)) {
            stackMap.put(key, stackMap.size() + 1);
            displayNameMap.put(key, displayName);
        }
    }

    private String getStackLocation(Symbol sym) {
        String key = getSymbolKey(sym);
        Integer slot = stackMap.get(key);
        if (slot == null) {
            slot = stackMap.size() + 1;
            stackMap.put(key, slot);
            displayNameMap.put(key, sym.getName());
        }
        return "-" + (slot * 8) + "(%rbp)";
    }

    private String getSymbolKey(Symbol sym) {
        if (sym.isTemporal()) {
            return "T_" + sym.getName();
        }
        return "V_" + sym.getName();
    }

    private void generateInstruction(Instruccion3D inst) {
        emit("    # " + inst.toString());

        switch (inst.getOperador()) {
            case ASIGNACION: {
                loadOperand(inst.getOperando1(), "%r10");
                storeResult(inst.getResultado(), "%r10");
                break;
            }

            case SUMA: {
                loadOperand(inst.getOperando1(), "%r10");
                loadOperand(inst.getOperando2(), "%r11");
                emit("    addq %r11, %r10");
                storeResult(inst.getResultado(), "%r10");
                break;
            }

            case RESTA: {
                loadOperand(inst.getOperando1(), "%r10");
                loadOperand(inst.getOperando2(), "%r11");
                emit("    subq %r11, %r10");
                storeResult(inst.getResultado(), "%r10");
                break;
            }

            case MULT: {
                loadOperand(inst.getOperando1(), "%r10");
                loadOperand(inst.getOperando2(), "%r11");
                emit("    imulq %r11, %r10");
                storeResult(inst.getResultado(), "%r10");
                break;
            }

            case DIV: {
                loadOperand(inst.getOperando1(), "%r10");
                loadOperand(inst.getOperando2(), "%r11");
                emit("    movq %r10, %rax");
                emit("    cqo");
                emit("    idivq %r11");
                emit("    movq %rax, %r10");
                storeResult(inst.getResultado(), "%r10");
                break;
            }

            case MENOR:
            case MAYOR:
            case MENOR_IGUAL:
            case MAYOR_IGUAL:
            case IGUAL_IGUAL:
            case DISTINTO: {
                loadOperand(inst.getOperando1(), "%r10");
                loadOperand(inst.getOperando2(), "%r11");
                emit("    cmpq %r11, %r10");

                String setInst;
                switch (inst.getOperador()) {
                    case MENOR: setInst = "setl"; break;
                    case MAYOR: setInst = "setg"; break;
                    case MENOR_IGUAL: setInst = "setle"; break;
                    case MAYOR_IGUAL: setInst = "setge"; break;
                    case IGUAL_IGUAL: setInst = "sete"; break;
                    case DISTINTO: setInst = "setne"; break;
                    default: setInst = "sete"; break;
                }
                emit("    " + setInst + " %al");
                emit("    movzbq %al, %r10");
                storeResult(inst.getResultado(), "%r10");
                break;
            }

            case LABEL: {
                String labelName = inst.getOperando1().getName();
                emit("." + labelName + ":");
                break;
            }

            case IF_FALSE: {
                loadOperand(inst.getOperando1(), "%r10");
                emit("    cmpq $0, %r10");
                String labelName = inst.getOperando2().getName();
                emit("    je ." + labelName);
                break;
            }

            case GOTO: {
                String labelName = inst.getOperando1().getName();
                emit("    jmp ." + labelName);
                break;
            }

            case RETORNO: {
                if (inst.getOperando1() != null) {
                    loadOperand(inst.getOperando1(), "%rax");
                } else {
                    emit("    movq $0, %rax");
                }
                emit("    movq %rbp, %rsp");
                emit("    popq %rbp");
                emit("    ret");
                break;
            }
        }
        emit("");
    }

    private void loadOperand(Symbol sym, String targetReg) {
        if (sym == null) {
            emit("    movq $0, " + targetReg);
            return;
        }

        if (sym.isConstant()) {
            int val = toIntValue(sym.getValue());
            emit("    movq $" + val + ", " + targetReg);
        } else {
            String loc = getStackLocation(sym);
            emit("    movq " + loc + ", " + targetReg);
        }
    }

    private void storeResult(Symbol sym, String sourceReg) {
        if (sym == null) {
            return;
        }
        String loc = getStackLocation(sym);
        emit("    movq " + sourceReg + ", " + loc);
    }

    private int toIntValue(Object val) {
        if (val instanceof Boolean) {
            return ((Boolean) val) ? 1 : 0;
        }
        if (val instanceof Number) {
            return ((Number) val).intValue();
        }
        if (val != null) {
            String str = val.toString().trim();
            if ("true".equalsIgnoreCase(str)) return 1;
            if ("false".equalsIgnoreCase(str)) return 0;
            try {
                return Integer.parseInt(str);
            } catch (NumberFormatException ignored) {}
        }
        return 0;
    }

    private void emit(String line) {
        instructions.add(line);
    }
}
