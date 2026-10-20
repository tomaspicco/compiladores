# Universidad Nacional de Villa Mercedes (UNVM)
## Escuela de Ingeniería y Ciencias Ambientales - Compiladores
### Informe Técnico del Proyecto - Actualizado según Reunión de Cátedra (13 oct 2026)

---

## 1. Resumen de Decisiones y Requisitos Acordados (Reunión 13 Oct 2026)

Durante la sesión de revisión con la docente Bianca Guadalupe Torres Gil se acordaron las siguientes directivas de diseño e implementación:

1. **Separación de nodos para declaración y uso en el AST**:
   - Se estableció diferenciar en la jerarquía del árbol sintáctico cuándo una variable se declara (`VarDeclNode`) de cuándo se usa en una expresión (`VarUseNode` / `VariableRefNode`).
   - Ambas instancias se enlazan directamente con la misma entidad en memoria (`Symbol`), preservando su tipo, nombre y valor.

2. **Validación de tres errores de compilación principales**:
   - El analizador semántico debe verificar y reportar de forma explícita exactamente tres categorías de errores:
     1. **Variables redeclaradas**: Detección de identificadores duplicados en el mismo ámbito (`[VARIABLE REDECLARADA]`).
     2. **Variables no declaradas**: Detección de identificadores utilizados o asignados sin declaración previa (`[VARIABLE NO DECLARADA]`).
     3. **Errores de tipo**: Incompatibilidad de tipos en asignaciones, operadores aritméticos sobre booleanos o discrepancias en el valor de retorno de la función `main()` (`[ERROR DE TIPO]`).

3. **Código de Tres Direcciones (C3D)**:
   - **Vector dinámico de instrucciones**: Implementación de la clase `CodigoTresDirecciones` que almacena secuencialmente un vector dinámico de instrucciones (`Vector<Instruccion3D>`).
   - **Clase instrucción**: Definición de la clase `Instruccion3D`, donde cada instrucción contiene su código de operación (`Operador3D`) y punteros directos a los objetos `Symbol` (operando 1, operando 2 y resultado), preservando la información semántica sin degradar a texto plano.
   - **Variables temporales**: Creación de variables auxiliares temporales (`T1, T2, T3, ...`) para almacenar los resultados parciales de las operaciones intermedias, aclarando que los valores intermedios no pueden sobreescribir constantes sino variables temporales.
   - **Generador de C3D (`GeneradorC3D`)**: Recorrido del AST en post-orden (bottom-up: desde las hojas hacia la raíz), asegurando que las subexpresiones se resuelvan antes de procesar las operaciones compuestas y las asignaciones.
   - **Cuádruplas e Intérprete C3D**: Representación en forma de cuádruplas `(OP, ARG1, ARG2, RES)` y un evaluador (`InterpreteC3D`) que recorre secuencialmente la lista de instrucciones para simular la ejecución.
   - **Programas de prueba**: Creación de programas específicos para forzar y validar cada error de compilación y la generación correcta del código intermedio.

---

## 2. Arquitectura del Compilador

El compilador sigue una arquitectura de múltiples fases integradas:

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
       (AST Eval)      (Vector C3D)    (Seudo-Assembly)
                           │
                 [InterpreteC3D]
```

### Paquetes del Sistema
- `ar.edu.unvm.compiladores.lexer`: Especificación JFlex (`Lexer.flex`) y escáner generado (`Lexer.java`).
- `ar.edu.unvm.compiladores.parser`: Especificación CUP (`Parser.cup`), parser generado (`Parser.java`) y tabla de símbolos terminales (`sym.java`).
- `ar.edu.unvm.compiladores.ast`: Jerarquía del AST (`ASTNode`, `ProgramNode`, `VarDeclNode`, `VarUseNode`, `VariableRefNode`, `AssignStmtNode`, `ReturnStmtNode`, `BinaryExprNode`, `NumberLiteralNode`, `BooleanLiteralNode`, `Type`).
- `ar.edu.unvm.compiladores.symbols`: `Symbol` (almacena nombre, tipo, valor, kind: `VARIABLE`, `TEMPORAL`, `CONSTANTE`) y `SymbolTable`.
- `ar.edu.unvm.compiladores.semantic`: `SemanticAnalyzer` con control de las 3 categorías de errores.
- `ar.edu.unvm.compiladores.c3d`: Código intermedio de tres direcciones (`Operador3D`, `Instruccion3D`, `CodigoTresDirecciones`, `GeneradorC3D`, `InterpreteC3D`).
- `ar.edu.unvm.compiladores.interpreter`: Intérprete y evaluador recursivo sobre el AST (`Interpreter`).
- `ar.edu.unvm.compiladores.codegen`: Generador de código seudo-ensamblador (`PseudoAssemblyGenerator`).

---

## 3. Detalle de Implementación de las Decisiones de Cátedra

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
- **Instrucción de Tres Direcciones (`Instruccion3D`)**:
  - Contiene: `Operador3D operador`, `Symbol operando1`, `Symbol operando2` y `Symbol resultado`.
  - Método `toString()`: Genera formato `T1 = a + b`, `x = T1`, `return x`.
  - Método `toCuadrupla()`: Genera formato tupla `(ADD, a, b, T1)`.
- **Estructura Contenedora (`CodigoTresDirecciones`)**:
  - Almacena las instrucciones en un `Vector<Instruccion3D>` dinámico tal como requirió la cátedra.
  - Ofrece métodos `insertar()`, `imprimir()` y `imprimirCuadruplas()`.
- **Generador (`GeneradorC3D`)**:
  - Recorrido bottom-up (de las hojas hacia la raíz): para evaluar `z = x + y * 2 + (10 - x) / 2`:
    ```
    [01] x = 4
    [02] y = 3
    [03] T1 = y * 2
    [04] T2 = x + T1
    [05] T3 = 10 - x
    [06] T4 = T3 / 2
    [07] T5 = T2 + T4
    [08] z = T5
    [09] return z
    ```
  - Cuádruplas emitidas:
    ```
    Nro    Op       Arg1     Arg2     Res     
    ----------------------------------------
    [01]  ASSIGN   4        -        x       
    [02]  ASSIGN   3        -        y       
    [03]  MUL      y        2        T1      
    [04]  ADD      x        T1       T2      
    [05]  SUB      10       x        T3      
    [06]  DIV      T3       2        T4      
    [07]  ADD      T2       T4       T5      
    [08]  ASSIGN   T5       -        z       
    [09]  RET      z        -        -       
    ```

---

## 4. Guía de Ejecución y Programas de Prueba

### A. Compilación del Compilador
```powershell
.\compilar.ps1
```
*(o `compilar.bat` en Windows CMD)*

---

### B. Pruebas de Validación de los 3 Errores de Compilación

1. **Prueba de Variable Redeclarada**:
   ```powershell
   .\ejecutar.ps1 src\prueba_error_redeclaracion.txt
   ```
   *Salida esperada:*
   ```
   --- Errores Semanticos de Compilacion ---
   [VARIABLE REDECLARADA] (linea 4): la variable 'x' ya fue declarada previamente.
   ```

2. **Prueba de Variable Inexistente / No Declarada**:
   ```powershell
   .\ejecutar.ps1 src\prueba_error_no_declarada.txt
   ```
   *Salida esperada:*
   ```
   --- Errores Semanticos de Compilacion ---
   [VARIABLE NO DECLARADA] (linea 4): la variable 'y' no esta declarada.
   [VARIABLE NO DECLARADA] (linea 5): la variable 'z' no esta declarada.
   ```

3. **Prueba de Error de Tipo en Asignación**:
   ```powershell
   .\ejecutar.ps1 src\prueba_error_tipo_asignacion.txt
   ```
   *Salida esperada:*
   ```
   --- Errores Semanticos de Compilacion ---
   [ERROR DE TIPO] (linea 4): tipo incompatible en asignacion a 'x'. Se esperaba int pero se obtuvo bool.
   [ERROR DE TIPO] (linea 5): tipo incompatible en asignacion a 'b'. Se esperaba bool pero se obtuvo int.
   ```

4. **Prueba de Error de Tipo en Operación Aritmética**:
   ```powershell
   .\ejecutar.ps1 src\prueba_error_tipo_operacion.txt
   ```
   *Salida esperada:*
   ```
   --- Errores Semanticos de Compilacion ---
   [ERROR DE TIPO] (linea 5): el operador aritmetico '+' requiere enteros pero el lado izquierdo es bool.
   ```

---

### C. Pruebas de Código de Tres Direcciones (C3D)

1. **Aritmética Compuesta con Temporales Sucesivos**:
   ```powershell
   .\ejecutar.ps1 src\prueba_c3d_aritmetica.txt
   ```
   *Muestra AST, Tabla de Símbolos, Ejecución AST, Código C3D, Tabla de Cuádruplas, Ejecución C3D y Seudo-Assembly. Genera automáticamente `prueba_c3d_aritmetica.c3d` y `prueba_c3d_aritmetica.asm`.*

2. **Tipos Booleanos en C3D**:
   ```powershell
   .\ejecutar.ps1 src\prueba_c3d_bool.txt
   ```

3. **Filtro exclusivo para observar C3D**:
   ```powershell
   .\ejecutar.ps1 src\prueba_c3d_aritmetica.txt --c3d
   ```
