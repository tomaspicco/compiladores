package ar.edu.unvm.compiladores.symbols;

import ar.edu.unvm.compiladores.ast.Type;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class SymbolTable {
    private final Map<String, Symbol> table = new LinkedHashMap<>();

    public boolean declare(String name, Type type, int line, int column) {
        if (table.containsKey(name)) {
            return false;
        }
        table.put(name, new Symbol(name, type, line, column));
        return true;
    }

    public Symbol lookup(String name) {
        return table.get(name);
    }

    public boolean contains(String name) {
        return table.containsKey(name);
    }

    public Type getType(String name) {
        Symbol s = lookup(name);
        return (s != null) ? s.getType() : null;
    }

    public Object getValue(String name) {
        Symbol s = lookup(name);
        return (s != null) ? s.getValue() : null;
    }

    public void setValue(String name, Object value) {
        Symbol s = lookup(name);
        if (s != null) {
            s.setValue(value);
        }
    }

    public Collection<Symbol> getAll() {
        return table.values();
    }

    public void printTable() {
        System.out.printf("%-15s %-10s %-12s%n", "Variable", "Tipo", "Valor");
        System.out.println("---------------------------------------");
        for (Symbol s : table.values()) {
            if (s.isVariable()) {
                System.out.printf("%-15s %-10s %-12s%n",
                        s.getName(),
                        s.getType(),
                        String.valueOf(s.getValue()));
            }
        }
    }
}
