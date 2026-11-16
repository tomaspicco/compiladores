# Universidad Nacional de Villa Mercedes (UNViME)
## Escuela de Ingeniería y Ciencias Ambientales - Compiladores
### Informe Técnico del Proyecto - Compilador Modular JFlex + CUP

---

## 1. Resumen de Decisiones y Requisitos de Cátedra

El compilador ha sido diseñado e implementado siguiendo las directivas formales de la cátedra de Compiladores:

1. **Separación de nodos para declaración y uso en el AST**:
   - Se diferencia en la jerarquía del árbol sintáctico cuándo una variable se declara (`VarDeclNode`) de cuándo se usa en una expresión (`VarUseNode` / `VariableRefNode`).
   - Ambas instancias se enlazan directamente con la misma entidad en memoria (`Symbol`), preservando su tipo, nombre y valor.

2. **Estructuras de Control y Operadores de Comparación**:
   - Soporte para bifurcaciones condicionales `if (condicion) { ... }` y `if (condicion) { ... } else { ... }`.
   - Soporte para estructuras de repetición `while (condicion) { ... }`.
   - Definición formal en la gramática de:
     - **Sentencia**: Instrucción que ordena una acción a la computadora (asignaciones, `return`, `if`, `while`).
     - **Expresión**: Combinación de valores, variables y operadores que evalúa a un único valor.
   - Operadores relacionales y de igualdad: `<`, `>`, `<=`, `>=`, `==`, `!=`.

3. **Validación de tres errores de compilación principales**:
   - El analizador semántico verifica y reporta de forma explícita exactamente tres categorías de errores:
     1. **Variables redeclaradas**: Detección de identificadores duplicados en el mismo ámbito (`[VARIABLE REDECLARADA]`).
     2. **Variables no declaradas**: Detección de identificadores utilizados o asignados sin declaración previa (`[VARIABLE NO DECLARADA]`).
     3. **Errores de tipo**: Incompatibilidad de tipos en asignaciones, operadores aritméticos/relacionales, discrepancias en el valor de retorno de `main()`, y validación obligatoria de que la condición de `if` y `while` sea de tipo `bool` (`[ERROR DE TIPO]`).

4. **Código de Tres Direcciones (C3D)**:
   - **Vector dinámico de instrucciones**: Clase `CodigoTresDirecciones` que almacena un vector dinámico de instrucciones (`Vector<Instruccion3D>`).
   - **Clase instrucción**: Clase `Instruccion3D` donde cada instrucción contiene su código de operación (`Operador3D`) y punteros directos a los objetos `Symbol` (operando 1, operando 2 y resultado), preservando la información semántica.
   - **Variables temporales y etiquetas**: Creación de variables auxiliares temporales (`T1, T2, ...`) y etiquetas de control de flujo (`L1, L2, ...`).
   - **Instrucciones de salto**: `LABEL` (declaración de punto de salto), `IFFALSE` (salto condicional si la evaluación booleana es falsa) y `GOTO` (salto incondicional).
   - **Generador de C3D (`GeneradorC3D`)**: Recorrido del AST en post-orden (bottom-up: desde las hojas hacia la raíz) para expresiones, y estructuración secuencial con etiquetas para condicionales y bucles.
   - **Cuádruplas e Intérprete C3D**: Representación en forma de cuádruplas `(OP, ARG1, ARG2, RES)` y evaluador (`InterpreteC3D`) capaz de simular la ejecución siguiendo los saltos y bucles.

5. **Generación de Assembly GNU x86-64 (64 bits) y Compatibilidad GCC**:
   - Traducción instrucción por instrucción del código de tres direcciones a lenguaje ensamblador GNU x86-64 (sintaxis AT&T para `as` / `gcc`).
   - Marco de pila (stack frame) relativo al puntero base `%rbp` con desplazamientos de 8 bytes (`-8(%rbp)`, `-16(%rbp)`, ...), con prólogo `pushq %rbp; movq %rsp, %rbp; subq $N, %rsp`.
   - Comparaciones mediante `cmpq` de 64 bits, generación de valor booleano en `%al` vía `setl`, `setg`, `setle`, `setge`, `sete`, `setne`, y extensión limpia con `movzbq`.
   - Saltos locales de control de flujo (`jmp .L`, `je .L`).
   - Manipulación de expresiones mediante los registros temporales `%r10` y `%r11`.
   - Convención de llamadas x86-64 System V / GNU: retorno en `%rax` y epílogo con `movq %rbp, %rsp; popq %rbp; ret`.

---

## 2. Arquitectura del Compilador

El compilador sigue una arquitectura modular en pipeline:

```
                  Código Fuente (.txt)
                           │
                           ▼
                      [Lexer.flex]  (JFlex)
                           │ Tokens (+, -, *, /, <, >, <=, >=, ==, !=, if, else, while)
                           ▼
                      [Parser.cup]  (CUP)
                           │
                           ▼
            Árbol Sintáctico Abstracto (AST)
            ├── VarDeclNode    [Declaración] ──┐
            ├── AssignStmtNode                 ├──> Enlazados a Symbol
            ├── VarUseNode     [Uso] ──────────┘
            ├── IfStmtNode     [Condicional (then / else)]
            └── WhileStmtNode  [Bucle while]
                           │
                           ▼
                   [SemanticAnalyzer]
            (Verifica: Redeclaraciones, No Declaradas, Tipos y Condición Bool)
                           │
           ┌───────────────┼───────────────┐
           ▼               ▼               ▼
     [Interpreter]   [GeneradorC3D]  [PseudoAssemblyGenerator]
       (AST Eval)      (Vector C3D)    (Seudo-Assembly AST)
                           │
                   ┌───────┴───────┐
                   ▼               ▼
            [InterpreteC3D]  [GnuAssemblyGenerator]
            (Simulacion C3D) (Assembly GNU x86-64 .s)
                                   │
                                   ▼
                             [GCC / MinGW]
                            (Binario .exe)
```

### Paquetes del Sistema
- `ar.edu.unvm.compiladores.lexer`: Especificación JFlex (`Lexer.flex`) y escáner generado (`Lexer.java`).
- `ar.edu.unvm.compiladores.parser`: Especificación CUP (`Parser.cup`), parser generado (`Parser.java`) y tabla de símbolos terminales (`sym.java`).
- `ar.edu.unvm.compiladores.ast`: Jerarquía del AST (`ASTNode`, `ProgramNode`, `VarDeclNode`, `VarUseNode`, `VariableRefNode`, `AssignStmtNode`, `ReturnStmtNode`, `IfStmtNode`, `WhileStmtNode`, `BinaryExprNode`, `NumberLiteralNode`, `BooleanLiteralNode`, `Type`).
- `ar.edu.unvm.compiladores.symbols`: `Symbol` (almacena nombre, tipo, valor, kind: `VARIABLE`, `TEMPORAL`, `CONSTANTE`, `ETIQUETA`) y `SymbolTable`.
- `ar.edu.unvm.compiladores.semantic`: `SemanticAnalyzer` con control de las 3 categorías de errores.
- `ar.edu.unvm.compiladores.c3d`: Código intermedio de tres direcciones (`Operador3D`, `Instruccion3D`, `CodigoTresDirecciones`, `GeneradorC3D`, `InterpreteC3D`).
- `ar.edu.unvm.compiladores.interpreter`: Intérprete y evaluador recursivo sobre el AST (`Interpreter`).
- `ar.edu.unvm.compiladores.codegen`:
  - `PseudoAssemblyGenerator`: Generador de código seudo-ensamblador basado en registros desde el AST.
  - `GnuAssemblyGenerator`: Generador de código ensamblador real GNU x86-64 a partir de C3D instrucción por instrucción.

---

## 3. Detalle de Implementación

### A. Jerarquía Separada: `VarDeclNode`, `VarUseNode`, `IfStmtNode` y `WhileStmtNode`
- **`VarDeclNode`**: Sentencia de declaración (`int x;`). Almacena tipo, identificador y enlace `Symbol`.
- **`VarUseNode`**: Referencia a variable en expresiones (`x + 1`, `x < 10`). Enlazado al mismo objeto `Symbol`.
- **`IfStmtNode`**: Condicional con expresión condicional, lista de sentencias `thenBranch` y lista opcional `elseBranch`.
- **`WhileStmtNode`**: Bucle con expresión de condición y lista de sentencias en `body`.

### B. Los Tres Errores Semánticos de Compilación
1. **`[VARIABLE REDECLARADA]`**:
   ```
   [VARIABLE REDECLARADA] (linea 4): la variable 'x' ya fue declarada previamente.
   ```
2. **`[VARIABLE NO DECLARADA]`**:
   ```
   [VARIABLE NO DECLARADA] (linea 4): la variable 'y' no esta declarada.
   ```
3. **`[ERROR DE TIPO]`**:
   - Incompatibilidad en asignaciones:
     ```
     [ERROR DE TIPO] (linea 4): tipo incompatible en asignacion a 'x'. Se esperaba int pero se obtuvo bool.
     ```
   - Operadores aritméticos o relacionales sobre tipos incompatibles:
     ```
     [ERROR DE TIPO] (linea 5): el operador relacional '<' requiere enteros pero el lado izquierdo es bool.
     ```
   - Condición no booleana en estructuras de control:
     ```
     [ERROR DE TIPO] (linea 5): la condicion de la sentencia 'if' debe ser de tipo BOOL pero se obtuvo int.
     ```

### C. Código de Tres Direcciones (C3D) y Cuádruplas
- Generación de temporales `T1, T2, ...` para subexpresiones.
- Generación de etiquetas `L1, L2, ...` para control de flujo.
- Estructura de cuádruplas `(OP, ARG1, ARG2, RES)`:
  - `(LT    , i     , 5     , T1    )`
  - `(IFFALSE, T1   , L2    , -     )`
  - `(GOTO  , L1    , -     , -     )`
  - `(LABEL , L2    , -     , -     )`
- Evaluación en `InterpreteC3D` con contador de programa y resolución de bifurcaciones.

### D. Generación de Assembly GNU x86-64 y GCC
- Prólogo canónico:
  ```gas
  .globl main
  main:
      pushq %rbp
      movq %rsp, %rbp
      subq $N, %rsp
  ```
- Comparaciones y saltos:
  ```gas
      # T1 = i < 5
      movq -8(%rbp), %r10
      movq $5, %r11
      cmpq %r11, %r10
      setl %al
      movzbq %al, %r10
      movq %r10, -32(%rbp)

      # ifFalse T1 goto L2
      movq -32(%rbp), %r10
      cmpq $0, %r10
      je .L2
  ```
- Epílogo estándar:
  ```gas
      movq %rbp, %rsp
      popq %rbp
      ret
  ```

---

## 4. Pruebas y Validación del Sistema

### A. Ejecución de Prueba Completa (Control de Flujo)
```powershell
.\ejecutar.ps1 src\programa_prueba_control.txt
```
Genera los archivos `src/programa_prueba_control.c3d` y `src/programa_prueba_control.s`.

### B. Pruebas de Errores Semánticos
```powershell
.\ejecutar.ps1 src\prueba_error_redeclaracion.txt
.\ejecutar.ps1 src\prueba_error_no_declarada.txt
.\ejecutar.ps1 src\prueba_error_tipo_asignacion.txt
.\ejecutar.ps1 src\prueba_error_condicion_tipo.txt
```

### C. Compilación y Ejecución con GCC
```powershell
# 1. Generar Assembly GNU x86-64
.\paso6_assembly.ps1 src\programa_prueba_control.txt

# 2. Ensamblar con GCC
gcc src\programa_prueba_control.s -o programa_control.exe

# 3. Ejecutar y consultar retorno
.\programa_control.exe
$LASTEXITCODE
```
