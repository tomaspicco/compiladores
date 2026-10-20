package ar.edu.unvm.compiladores;

import ar.edu.unvm.compiladores.lexer.Lexer;
import ar.edu.unvm.compiladores.parser.sym;
import java_cup.runtime.Symbol;

import java.io.FileReader;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class TestLexer {
    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("Uso: java TestLexer <archivo>");
            System.exit(1);
        }

        Map<Integer, String> nombres = new HashMap<>();
        for (Field f : sym.class.getFields()) {
            nombres.put(f.getInt(null), f.getName());
        }

        Lexer lexer = new Lexer(new FileReader(args[0]));
        Symbol token;

        while ((token = lexer.next_token()).sym != sym.EOF) {
            String nombre = nombres.getOrDefault(token.sym, "?");
            String valor = (token.value != null) ? token.value.toString() : "";
            System.out.printf("%-15s %s%n", nombre, valor);
        }

        System.out.println("FIN TOKENS");
    }
}