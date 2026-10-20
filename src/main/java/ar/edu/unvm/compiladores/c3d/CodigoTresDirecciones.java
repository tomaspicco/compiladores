package ar.edu.unvm.compiladores.c3d;

import java.util.Vector;

/**
 * Estructura contenedora del Codigo de Tres Direcciones (C3D).
 * Implementada con un Vector dinamico de instrucciones de tres direcciones,
 * tal como fue solicitado en la clase (00:12:28, 00:17:11).
 */
public class CodigoTresDirecciones {
    private final Vector<Instruccion3D> instrucciones = new Vector<>();

    /**
     * Inserta secuencialmente una instruccion al vector de instrucciones.
     */
    public void insertar(Instruccion3D inst) {
        instrucciones.add(inst);
    }

    public Vector<Instruccion3D> getInstrucciones() {
        return instrucciones;
    }

    public int size() {
        return instrucciones.size();
    }

    public Instruccion3D get(int index) {
        return instrucciones.get(index);
    }

    /**
     * Imprime las instrucciones secuenciales en formato estandar de 3 direcciones.
     */
    public void imprimir() {
        System.out.println("=== CODIGO DE TRES DIRECCIONES (C3D) ===");
        if (instrucciones.isEmpty()) {
            System.out.println("   (No hay instrucciones)");
            return;
        }
        for (int i = 0; i < instrucciones.size(); i++) {
            System.out.printf("[%02d] %s%n", (i + 1), instrucciones.get(i).toString());
        }
    }

    /**
     * Imprime las instrucciones en formato tabla de cuadruplas (Op, Arg1, Arg2, Res).
     */
    public void imprimirCuadruplas() {
        System.out.println("=== CUADRUPLAS (C3D) ===");
        System.out.printf("%-6s %-8s %-8s %-8s %-8s%n", "Nro", "Op", "Arg1", "Arg2", "Res");
        System.out.println("----------------------------------------");
        for (int i = 0; i < instrucciones.size(); i++) {
            Instruccion3D inst = instrucciones.get(i);
            String arg1 = (inst.getOperando1() != null) ? inst.getOperando1().getDisplayName() : "-";
            String arg2 = (inst.getOperando2() != null) ? inst.getOperando2().getDisplayName() : "-";
            String res = (inst.getResultado() != null) ? inst.getResultado().getDisplayName() : "-";
            System.out.printf("[%02d]  %-8s %-8s %-8s %-8s%n",
                    (i + 1), inst.getOperador().getMnemonico(), arg1, arg2, res);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < instrucciones.size(); i++) {
            sb.append(String.format("[%02d] %s%n", (i + 1), instrucciones.get(i).toString()));
        }
        return sb.toString();
    }
}
