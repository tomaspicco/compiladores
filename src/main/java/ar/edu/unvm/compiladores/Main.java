package ar.edu.unvm.compiladores;

import ar.edu.unvm.compiladores.ast.ProgramNode;
import ar.edu.unvm.compiladores.c3d.CodigoTresDirecciones;
import ar.edu.unvm.compiladores.c3d.GeneradorC3D;
import ar.edu.unvm.compiladores.c3d.InterpreteC3D;
import ar.edu.unvm.compiladores.codegen.GnuAssemblyGenerator;
import ar.edu.unvm.compiladores.codegen.PseudoAssemblyGenerator;
import ar.edu.unvm.compiladores.interpreter.Interpreter;
import ar.edu.unvm.compiladores.lexer.Lexer;
import ar.edu.unvm.compiladores.parser.Parser;
import ar.edu.unvm.compiladores.parser.sym;
import ar.edu.unvm.compiladores.semantic.SemanticAnalyzer;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        if (args.length < 1) {
            imprimirAyuda();
            System.exit(1);
        }

        String filePath = args[0];
        String mode = (args.length > 1) ? args[1].toLowerCase().trim() : "--all";

        File file = new File(filePath);
        if (!file.exists()) {
            System.err.println("Error: archivo no encontrado: " + filePath);
            System.exit(1);
        }

        // 1. Lexer
        if (mode.equals("--lexer") || mode.equals("--tokens") || mode.equals("--paso1") || mode.equals("--punto3")) {
            ejecutarLexer(file);
            return;
        }

        // 2. Parser CUP -> AST
        ProgramNode ast = null;
        try {
            Lexer lexer = new Lexer(new FileReader(file));
            Parser parser = new Parser(lexer);
            parser.parse();
            ast = parser.getAST();

            if (ast == null) {
                System.err.println("Error: no se pudo generar el AST.");
                System.exit(1);
            }
        } catch (Exception e) {
            System.err.println("Error de analisis sintactico: " + e.getMessage());
            System.exit(1);
        }

        if (mode.equals("--ast") || mode.equals("--parser") || mode.equals("--paso2") || mode.equals("--punto4") || mode.equals("--punto5")) {
            System.out.println("--- AST (Arbol Sintactico Abstracto) ---");
            ast.printTree("", true);
            return;
        }

        // 3. Analisis Semantico
        SemanticAnalyzer semantic = new SemanticAnalyzer();
        boolean semanticOk = semantic.analyze(ast);

        if (mode.equals("--semantic") || mode.equals("--semantica") || mode.equals("--paso3")) {
            System.out.println("--- Analisis Semantico ---");
            if (!semanticOk) {
                for (String err : semantic.getErrors()) {
                    System.err.println(err);
                }
                System.exit(2);
            }
            System.out.println("Analisis completado sin errores.");
            System.out.println();
            System.out.println("--- Tabla de Simbolos ---");
            semantic.getSymbolTable().printTable();
            return;
        }

        if (!semanticOk) {
            System.err.println("--- Errores Semanticos ---");
            for (String err : semantic.getErrors()) {
                System.err.println(err);
            }
            System.exit(2);
        }

        // 4. Interprete AST
        if (mode.equals("--eval") || mode.equals("--interpret") || mode.equals("--interprete") || mode.equals("--paso4") || mode.equals("--punto6")) {
            System.out.println("--- Ejecucion (Interprete AST) ---");
            Interpreter interpreter = new Interpreter(semantic.getSymbolTable());
            try {
                interpreter.execute(ast);
            } catch (Exception e) {
                System.err.println("Error en ejecucion: " + e.getMessage());
                System.exit(3);
            }
            System.out.println();
            System.out.println("--- Variables luego de la ejecucion ---");
            semantic.getSymbolTable().printTable();
            return;
        }

        // 5. Codigo de Tres Direcciones (C3D)
        GeneradorC3D generadorC3D = new GeneradorC3D(semantic.getSymbolTable());
        CodigoTresDirecciones c3d = generadorC3D.generar(ast);

        if (mode.equals("--c3d") || mode.equals("--3ac") || mode.equals("--paso5")) {
            System.out.println("--- Codigo de Tres Direcciones (C3D) ---");
            c3d.imprimir();
            System.out.println();
            System.out.println("--- Cuadruplas (C3D) ---");
            c3d.imprimirCuadruplas();
            System.out.println();

            InterpreteC3D interpreteC3D = new InterpreteC3D();
            try {
                interpreteC3D.ejecutar(c3d);
                if (interpreteC3D.hasReturned()) {
                    System.out.println("Resultado de ejecucion C3D (Return): " + interpreteC3D.getReturnValue());
                }
            } catch (Exception e) {
                System.err.println("Error al interpretar C3D: " + e.getMessage());
            }

            guardarArchivo(filePath, ".c3d", c3d.toString());
            return;
        }

        // 6. Assembly GNU x86-64
        if (mode.equals("--gnu") || mode.equals("--assembly") || mode.equals("--asm") || mode.equals("--paso6") || mode.equals("--punto7")) {
            System.out.println("--- Assembly GNU x86-64 ---");
            GnuAssemblyGenerator gnuCodegen = new GnuAssemblyGenerator();
            String gnuAsm = gnuCodegen.generate(ast, c3d);
            System.out.println(gnuAsm);

            guardarArchivo(filePath, ".s", gnuAsm);
            return;
        }

        // 7. Seudo-Assembly
        if (mode.equals("--pseudo") || mode.equals("--paso7")) {
            System.out.println("--- Seudo-Assembly (AST) ---");
            PseudoAssemblyGenerator pseudoCodegen = new PseudoAssemblyGenerator();
            String pseudoAsm = pseudoCodegen.generate(ast);
            System.out.println(pseudoAsm);

            guardarArchivo(filePath, ".asm", pseudoAsm);
            return;
        }

        // Modo completo
        System.out.println("--- AST (Arbol Sintactico Abstracto) ---");
        ast.printTree("", true);
        System.out.println();

        System.out.println("--- Analisis Semantico ---");
        System.out.println("Analisis completado sin errores.");
        System.out.println();
        System.out.println("--- Tabla de Simbolos ---");
        semantic.getSymbolTable().printTable();
        System.out.println();

        System.out.println("--- Ejecucion (Interprete AST) ---");
        Interpreter interpreter = new Interpreter(semantic.getSymbolTable());
        try {
            interpreter.execute(ast);
        } catch (Exception e) {
            System.err.println("Error en ejecucion: " + e.getMessage());
        }
        System.out.println();

        System.out.println("--- Codigo de Tres Direcciones (C3D) ---");
        c3d.imprimir();
        System.out.println();
        System.out.println("--- Cuadruplas (C3D) ---");
        c3d.imprimirCuadruplas();
        System.out.println();

        InterpreteC3D interpreteC3D = new InterpreteC3D();
        try {
            interpreteC3D.ejecutar(c3d);
            if (interpreteC3D.hasReturned()) {
                System.out.println("Resultado de ejecucion C3D (Return): " + interpreteC3D.getReturnValue());
            }
        } catch (Exception e) {
            System.err.println("Error al interpretar C3D: " + e.getMessage());
        }
        System.out.println();

        System.out.println("--- Assembly GNU x86-64 ---");
        GnuAssemblyGenerator gnuCodegen = new GnuAssemblyGenerator();
        String gnuAsm = gnuCodegen.generate(ast, c3d);
        System.out.println(gnuAsm);

        guardarArchivo(filePath, ".c3d", c3d.toString());
        guardarArchivo(filePath, ".s", gnuAsm);
    }

    private static void ejecutarLexer(File file) {
        System.out.println("--- Tokens (Lexer JFlex) ---");
        System.out.printf("%-15s %-15s (linea, columna)%n", "TOKEN", "VALOR");
        System.out.println("----------------------------------------------");

        Map<Integer, String> nombresTokens = new HashMap<>();
        for (Field f : sym.class.getFields()) {
            try {
                nombresTokens.put(f.getInt(null), f.getName());
            } catch (Exception ignored) {}
        }

        try {
            Lexer lexer = new Lexer(new FileReader(file));
            java_cup.runtime.Symbol tok;
            while ((tok = lexer.next_token()).sym != sym.EOF) {
                String nombre = nombresTokens.getOrDefault(tok.sym, "?" + tok.sym);
                String valor = (tok.value != null) ? tok.value.toString() : "";
                System.out.printf("%-15s %-15s (%d, %d)%n", nombre, valor, tok.left, tok.right);
            }
        } catch (Exception e) {
            System.err.println("Error lexico: " + e.getMessage());
            System.exit(1);
        }
    }

    private static void guardarArchivo(String basePath, String extension, String contenido) {
        String outPath = basePath.replaceAll("\\.[^.]+$", "") + extension;
        try (PrintWriter pw = new PrintWriter(new FileWriter(outPath))) {
            pw.println(contenido);
        } catch (Exception ignored) {}
    }

    private static void imprimirAyuda() {
        System.out.println("Uso: java Main <archivo> [opcion]");
        System.out.println("Opciones: --lexer, --ast, --semantic, --eval, --c3d, --gnu, --all");
    }
}
