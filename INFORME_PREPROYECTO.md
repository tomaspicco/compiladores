# Universidad Nacional de Villa Mercedes (UNVM)
## Escuela de Ingeniería y Ciencias Ambientales - Compiladores
### Informe Técnico del Proyecto - Compilador Modular JFlex + CUP

---

## 1. Resumen de Decisiones y Requisitos de Cátedra

El compilador ha sido diseñado e implementado siguiendo las directivas formales de la cátedra de Compiladores:

1. **Separación de nodos para declaración y uso en el AST**:
   - Se diferencia en la jerarquía del árbol sintáctico cuándo una variable se declara (`VarDeclNode`) de cuándo se usa en una expresión (`VarUseNode` / `VariableRefNode`).
   - Ambas instancias se enlazan directamente con la misma entidad en memoria (`Symbol`), preservando su tipo, nombre y valor.

2. **Validación de tres errores de compilación principales**:
   - El analizador semántico verifica y reporta de forma explícita exactamente tres categorías de errores:
     1. **Variables redeclaradas**: Detección de identificadores duplicados en el mismo ámbito (`[VARIABLE REDECLARADA]`).
     2. **Variables no declaradas**: Detección de identificadores utilizados o asignados sin declaración previa (`[VARIABLE NO DECLARADA]`).
     3. **Errores de tipo**: Incompatibilidad de tipos en asignaciones, operadores aritméticos sobre booleanos o discrepancias en el valor de retorno de la función `main()` (`[ERROR DE TIPO]`).

3. **Código de Tres Direcciones (C3D)**:
   - **Vector dinámico de instrucciones**: Clase `CodigoTresDirecciones` que almacena un vector dinámico de instrucciones (`Vector<Instruccion3D>`).
   - **Clase instrucción**: Clase `Instruccion3D` donde cada instrucción contiene su código de operación (`Operador3D`) y punteros directos a los objetos `Symbol` (operando 1, operando 2 y resultado), preservando la información semántica.
   - **Variables temporales**: Creación de variables auxiliares temporales (`T1, T2, T3, ...`) para almacenar los resultados parciales de las operaciones intermedias.
   - **Generador de C3D (`GeneradorC3D`)**: Recorrido del AST en post-orden (bottom-up: desde las hojas hacia la raíz), asegurando que las subexpresiones se resuelvan antes de procesar las operaciones compuestas y las asignaciones.
   - **Cuádruplas e Intérprete C3D**: Representación en forma de cuádruplas `(OP, ARG1, ARG2, RES)` y evaluador (`InterpreteC3D`) que recorre secuencialmente la lista de instrucciones para simular la ejecución.

4. **Generación de Assembly GNU x86-64 (64 bits) a partir de C3D**:
   - Traducción instrucción por instrucción del código de tres direcciones a lenguaje ensamblador GNU x86-64 (sintaxis AT&T para `as` / `gcc`).
   - Asignación de marco de pila (stack frame) con `enter $(8 * N), $0` para variables locales y temporales.
   - Manipulación de expresiones mediante los registros temporales `%r10` y `%r11`.
   - Convención de llamadas x86-64 System V / GNU: colocación del valor de retorno en `%rax` y epílogo con `leave` y `ret`.

---

## 2. Arquitectura del Compilador

El compilador sigue una arquitectura modular en pipeline:

```
                  Código Fuente (.txt)
                           │
                           ▼
                      [Lexer.flex]  (JFlex)
                           │ Tokens
                           ▼
                      [Parser.cup]  (CUP)
                           │
                           ▼
            Árbol Sintáctico Abstracto (AST)
            ├── VarDeclNode  [Declaración] ──┐
            ├── AssignStmtNode               ├──> Enlazados a Symbol
            └── VarUseNode   [Uso] ──────────┘
                           │
                           ▼
                   [SemanticAnalyzer]
            (Verifica: Redeclaraciones, No Declaradas, Tipos)
                           │
           ┌───────────────┼───────────────┐
           ▼               ▼               ▼
     [Interpreter]   [GeneradorC3D]  [PseudoAssemblyGenerator]
       (AST Eval)      (Vector C3D)    (Seudo-Assembly AST)
                           │
                   ┌───────┴───────┐
                   ▼               ▼
            [InterpreteC3D]  [GnuAssemblyGenerator]
            (Simulacion C3D) (Assembly GNU x86-64)
```

### Paquetes del Sistema
- `ar.edu.unvm.compiladores.lexer`: Especificación JFlex (`Lexer.flex`) y escáner generado (`Lexer.java`).
- `ar.edu.unvm.compiladores.parser`: Especificación CUP (`Parser.cup`), parser generado (`Parser.java`) y tabla de símbolos terminales (`sym.java`).
- `ar.edu.unvm.compiladores.ast`: Jerarquía del AST (`ASTNode`, `ProgramNode`, `VarDeclNode`, `VarUseNode`, `VariableRefNode`, `AssignStmtNode`, `ReturnStmtNode`, `BinaryExprNode`, `NumberLiteralNode`, `BooleanLiteralNode`, `Type`).
- `ar.edu.unvm.compiladores.symbols`: `Symbol` (almacena nombre, tipo, valor, kind: `VARIABLE`, `TEMPORAL`, `CONSTANTE`) y `SymbolTable`.
- `ar.edu.unvm.compiladores.semantic`: `SemanticAnalyzer` con control de las 3 categorías de errores.
- `ar.edu.unvm.compiladores.c3d`: Código intermedio de tres direcciones (`Operador3D`, `Instruccion3D`, `CodigoTresDirecciones`, `GeneradorC3D`, `InterpreteC3D`).
- `ar.edu.unvm.compiladores.interpreter`: Intérprete y evaluador recursivo sobre el AST (`Interpreter`).
- `ar.edu.unvm.compiladores.codegen`:
  - `PseudoAssemblyGenerator`: Generador de código seudo-ensamblador basado en registros desde el AST.
  - `GnuAssemblyGenerator`: Generador de código ensamblador real GNU x86-64 a partir de C3D instrucción por instrucción.

---

## 3. Detalle de Implementación

### A. Jerarquía Separada: `VarDeclNode` vs `VarUseNode`
- **`VarDeclNode`**: Representa la sentencia de declaración (`int x;`). Almacena el `Type`, el identificador `id` y mantiene una referencia `Symbol symbol` asignada durante el análisis semántico.
- **`VarUseNode`** (subclase de `VariableRefNode`): Representa la utilización de la variable dentro de una expresión aritmética o lógica (`x + 1`). Almacena el identificador `id` y la referencia al mismo objeto `Symbol symbol` en la tabla de símbolos.
- En la visualización del AST:
  ```
  +-- [Declaracion] int x -> Symbol(x: int)
  ...
  +-- [Asignacion] x -> Symbol(x: int) =
  |   \-- (+)
  |       +-- [Uso] x -> Symbol(x: int)
  |       \-- 10
  ```

### B. Los Tres Errores Semánticos de Compilación
El `SemanticAnalyzer` clasifica y formatea los errores en 3 categorías:
1. **`[VARIABLE REDECLARADA]`**:
   ```
   [VARIABLE REDECLARADA] (linea 4): la variable 'x' ya fue declarada previamente.
   ```
2. **`[VARIABLE NO DECLARADA]`**:
   ```
   [VARIABLE NO DECLARADA] (linea 4): la variable 'y' no esta declarada.
   [VARIABLE NO DECLARADA] (linea 5): la variable 'z' no esta declarada.
   ```
3. **`[ERROR DE TIPO]`**:
   ```
   [ERROR DE TIPO] (linea 4): tipo incompatible en asignacion a 'x'. Se esperaba int pero se obtuvo bool.
   [ERROR DE TIPO] (linea 5): el operador aritmetico '+' requiere enteros pero el lado izquierdo es bool.
   ```

### C. Código de Tres Direcciones (C3D)
- Recorrido bottom-up del AST mediante `GeneradorC3D`.
- Generación de temporales sucesivos `T1, T2, ...`.
- Vector de instrucciones `Instruccion3D` con operador y operandos `Symbol`.
- Representación en cuádruplas `(OP, ARG1, ARG2, RES)` y ejecución simulada con `InterpreteC3D`.

### D. Generación de Assembly GNU x86-64 (Sintaxis AT&T)
`GnuAssemblyGenerator` implementa la traducción directa desde la lista de C3D:
1. **Distribución del Stack Frame**:
   - Cada variable y temporal tiene un slot de 8 bytes en `-8 * i (%rbp)`.
   - Prólogo: `enter $(8 * N), $0` donde `N` es la suma de variables y temporales.
2. **Uso de Registros**:
   - Carga de operandos en `%r10` y `%r11` desde inmediatos (`$val`) o pila (`-offset(%rbp)`).
   - Operaciones:
     - Suma: `add %r11, %r10`
     - Resta: `sub %r11, %r10`
     - Multiplicación: `imul %r11, %r10`
     - División: `mov %r10, %rax; cqo; idivq %r11; mov %rax, %r10`
   - Almacenamiento: `mov %r10, -offset(%rbp)`.
3. **Retorno y Epílogo**:
   - Carga del valor en `%rax`.
   - Epílogo: `leave` y `ret`.

---

## 4. Pruebas y Validación del Sistema

### A. Ejecución Integral del Pipeline
```powershell
.\ejecutar.ps1 src\programa_prueba.txt
```
Genera los archivos `src/programa_prueba.c3d`, `src/programa_prueba.asm` y `src/programa_prueba.s`.

### B. Pruebas de Errores Semánticos
```powershell
.\ejecutar.ps1 src\prueba_error_redeclaracion.txt
.\ejecutar.ps1 src\prueba_error_no_declarada.txt
.\ejecutar.ps1 src\prueba_error_tipo_asignacion.txt
.\ejecutar.ps1 src\prueba_error_tipo_operacion.txt
```

### C. Visualización de Assembly GNU x86-64
```powershell
.\ejecutar.ps1 src\programa_prueba.txt --gnu
```
