package ar.edu.unvm.compiladores;

import ar.edu.unvm.compiladores.ast.ProgramNode;
import ar.edu.unvm.compiladores.c3d.CodigoTresDirecciones;
import ar.edu.unvm.compiladores.c3d.GeneradorC3D;
import ar.edu.unvm.compiladores.c3d.InterpreteC3D;
import ar.edu.unvm.compiladores.codegen.PseudoAssemblyGenerator;
import ar.edu.unvm.compiladores.interpreter.Interpreter;
import ar.edu.unvm.compiladores.lexer.Lexer;
import ar.edu.unvm.compiladores.parser.Parser;
import ar.edu.unvm.compiladores.semantic.SemanticAnalyzer;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;

public class Main {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Uso: java Main <archivo> [--all | --ast | --semantic | --eval | --c3d | --asm]");
            System.exit(1);
        }

        String filePath = args[0];
        boolean showAst = true;
        boolean showSemantic = true;
        boolean showEval = true;
        boolean showC3D = true;
        boolean showAsm = true;

        if (args.length > 1) {
            String mode = args[1].toLowerCase();
            if (mode.equals("--ast")) {
                showSemantic = false; showEval = false; showC3D = false; showAsm = false;
            } else if (mode.equals("--semantic")) {
                showAst = false; showEval = false; showC3D = false; showAsm = false;
            } else if (mode.equals("--eval") || mode.equals("--interpret")) {
                showAst = false; showC3D = false; showAsm = false;
            } else if (mode.equals("--c3d") || mode.equals("--3ac")) {
                showAst = false; showEval = false; showAsm = false;
            } else if (mode.equals("--asm")) {
                showAst = false; showEval = false; showC3D = false;
            }
        }

        File file = new File(filePath);
        if (!file.exists()) {
            System.err.println("Error: archivo no encontrado: " + filePath);
            System.exit(1);
        }

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
            System.err.println("Error de analisis: " + e.getMessage());
            System.exit(1);
        }

        // 1. Analisis Semantico (Validacion de los 3 errores principales y vinculacion con SymbolTable)
        SemanticAnalyzer semantic = new SemanticAnalyzer();
        boolean semanticOk = semantic.analyze(ast);

        // 2. Mostrar AST si corresponde (ahora con los enlaces a SymbolTable resueltos)
        if (showAst) {
            System.out.println("--- AST (Arbol Sintactico Abstracto) ---");
            ast.printTree("", true);
            System.out.println();
        }

        if (!semanticOk) {
            System.err.println("--- Errores Semanticos de Compilacion ---");
            for (String err : semantic.getErrors()) {
                System.err.println(err);
            }
            System.exit(2);
        }

        if (showSemantic) {
            System.out.println("--- Analisis Semantico ---");
            System.out.println("Analisis completado sin errores.");
            System.out.println();
            System.out.println("--- Tabla de Simbolos ---");
            semantic.getSymbolTable().printTable();
            System.out.println();
        }

        // 3. Ejecucion con Interprete de AST
        if (showEval) {
            System.out.println("--- Ejecucion (Interprete AST) ---");
            Interpreter interpreter = new Interpreter(semantic.getSymbolTable());
            try {
                interpreter.execute(ast);
            } catch (Exception e) {
                System.err.println("Error en ejecucion AST: " + e.getMessage());
                System.exit(3);
            }
            System.out.println();
            System.out.println("--- Variables luego de la ejecucion ---");
            semantic.getSymbolTable().printTable();
            System.out.println();
        }

        // 4. Codigo de Tres Direcciones (C3D)
        if (showC3D) {
            GeneradorC3D generadorC3D = new GeneradorC3D(semantic.getSymbolTable());
            CodigoTresDirecciones c3d = generadorC3D.generar(ast);

            System.out.println("--- Codigo de Tres Direcciones (C3D) ---");
            c3d.imprimir();
            System.out.println();

            System.out.println("--- Cuadruplas (C3D) ---");
            c3d.imprimirCuadruplas();
            System.out.println();

            // Ejecucion mediante interprete de C3D
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

            // Guardar archivo .c3d
            String c3dFilePath = filePath.replaceAll("\\.[^.]+$", "") + ".c3d";
            try (PrintWriter pw = new PrintWriter(new FileWriter(c3dFilePath))) {
                pw.println(c3d.toString());
            } catch (Exception ignored) {
            }
        }

        // 5. Seudo-Assembly
        if (showAsm) {
            System.out.println("--- Seudo-Assembly ---");
            PseudoAssemblyGenerator codegen = new PseudoAssemblyGenerator();
            String asmCode = codegen.generate(ast);
            System.out.println(asmCode);

            String asmFilePath = filePath.replaceAll("\\.[^.]+$", "") + ".asm";
            try (PrintWriter pw = new PrintWriter(new FileWriter(asmFilePath))) {
                pw.println(asmCode);
            } catch (Exception ignored) {
            }
        }
    }
}
