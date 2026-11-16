# Guía Completa del Proyecto y Registro de Cambios
**Universidad Nacional de Villa Mercedes (UNViME)**  
**Escuela de Ingeniería y Ciencias Ambientales - Compiladores**  
**Proyecto:** Compilador JFlex + CUP con AST, Tabla de Símbolos, Validación Semántica, C3D, Seudo-Assembly, Assembly GNU x86-64 y GCC.

---

## 1. Cómo Acceder al Proyecto en tu Computadora

El proyecto se encuentra ubicado físicamente en tu entorno:
```
C:\Users\Tomas\Desktop\compiladores
```

### Opciones para acceder y abrirlo:

#### Opción A: Desde la consola PowerShell (Recomendado)
1. Presiona `Win + X` y selecciona **Terminal** o **PowerShell**.
2. Escribe el comando para ingresar a la carpeta del proyecto:
   ```powershell
   cd "C:\Users\Tomas\Desktop\compiladores"
   ```

#### Opción B: Desde el Símbolo del Sistema (CMD)
1. Presiona `Win + R`, escribe `cmd` y presiona Enter.
2. Ingresa a la carpeta ejecutando:
   ```cmd
   cd /d "C:\Users\Tomas\Desktop\compiladores"
   ```

#### Opción C: Desde Visual Studio Code
- Abre tu terminal y escribe:
   ```powershell
   code "C:\Users\Tomas\Desktop\compiladores"
   ```

---

## 2. Requisitos Previos para la Ejecución

1. **Java Development Kit (JDK 8, 11, 17 o superior):**
   - Verifica que `java` y `javac` estén accesibles abriendo una consola y escribiendo:
     ```powershell
     java -version
     javac -version
     ```
   - Las librerías de JFlex (`jflex-full-1.9.1.jar`) y Java-CUP (`java-cup-11b-2015.03.26.jar` y `java-cup-11b-runtime.jar`) se encuentran integradas dentro de la carpeta `lib/` del proyecto.

2. **GCC (GNU Compiler Collection - MinGW-w64):**
   - Necesario para compilar a binario nativo (`.exe`) el código ensamblador GNU x86-64 (`.s`) generado en el Paso 6.
   - Instalación rápida con `winget`:
     ```powershell
     winget install --id BrechtSanders.WinLibs.POSIX.UCRT
     ```
   - Verificar instalación:
     ```powershell
     gcc --version
     ```

---

## 3. Registro Detallado de Cambios Realizados

Todos los componentes implementados responden estrictamente a las definiciones acordadas con la cátedra y las especificaciones técnicas del curso:

### A. Separación de Nodos en el AST para Declaración y Uso
1. `VarDeclNode.java`: Representa exclusivamente la **declaración** de variables (`int x;`). Posee referencia `private Symbol symbol;` con métodos `getSymbol()` y `setSymbol(Symbol)`.
2. `VarUseNode.java`: Subclase de `VariableRefNode` para representar de forma explícita el **uso** de una variable dentro de una expresión aritmética o lógica.
3. `VariableRefNode.java`: Visualizador (`printTree`) ajustado para mostrar la etiqueta `[Uso] nombre -> Symbol(...)`.
4. `Parser.cup`: La regla gramatical de variable en expresión (`ID:nombre`) instancia directamente un `VarUseNode`.
5. **Vinculación bidireccional:** Tanto los nodos de declaración como los de uso quedan asociados en memoria al mismo objeto `Symbol` en la tabla de símbolos.

### B. Control Estricto de Tres Errores Semánticos de Compilación
Formalizado en `SemanticAnalyzer.java`:
1. **`VARIABLE_REDECLARADA`**: Detecta si una variable con el mismo nombre ya fue declarada previamente en el mismo ámbito.
   *Ejemplo:* `[VARIABLE REDECLARADA] (linea 4): la variable 'x' ya fue declarada previamente.`
2. **`VARIABLE_NO_DECLARADA`**: Detecta el uso de variables no declaradas, tanto en sentencias de asignación como en referencias dentro de expresiones.
   *Ejemplo:* `[VARIABLE NO DECLARADA] (linea 4): la variable 'y' no esta declarada.`
3. **`ERROR_DE_TIPO`**: Verifica que los tipos coincidan en asignaciones, que los operadores aritméticos (`+`, `-`, `*`, `/`) y relacionales solo operen sobre tipos compatibles, que la condición de `if` y `while` sea estrictamente `bool`, y que el tipo de retorno coincida con la firma de `main`.
   *Ejemplos:*
   - `[ERROR DE TIPO] (linea 4): tipo incompatible en asignacion a 'x'. Se esperaba int pero se obtuvo bool.`
   - `[ERROR DE TIPO] (linea 5): la condicion de la sentencia 'if' debe ser de tipo BOOL pero se obtuvo int.`

### C. Implementación Completa de Código de Tres Direcciones (C3D)
Ubicado en el paquete `ar.edu.unvm.compiladores.c3d`:
1. **`Operador3D.java`**: Enum con operadores aritméticos (`SUMA`, `RESTA`, `MULT`, `DIV`), de comparación (`MENOR`, `MAYOR`, `MENOR_IGUAL`, `MAYOR_IGUAL`, `IGUAL_IGUAL`, `DISTINTO`), de asignación (`ASIGNACION`), retorno (`RETORNO`) y de control de flujo (`LABEL`, `IF_FALSE`, `GOTO`).
2. **`Instruccion3D.java`**: Cuádrupla que almacena `operador`, `operando1`, `operando2` y `resultado` como referencias directas a objetos `Symbol`, conservando la información semántica completa.
3. **`CodigoTresDirecciones.java`**: Administra el vector dinámico de instrucciones (`Vector<Instruccion3D>`), ofreciendo métodos para listar instrucciones y generar la tabla de cuádruplas.
4. **`GeneradorC3D.java`**: Recorre el AST en post-orden (**bottom-up: hojas a raíz**) resolviendo expresiones anidadas mediante variables temporales sucesivas (`T1, T2, T3, ...`), y genera etiquetas y saltos para bifurcaciones y ciclos.
5. **`InterpreteC3D.java`**: Intérprete que recorre el vector de C3D mediante tabla de etiquetas y contador de programa para simular la ejecución con saltos y bucles.

### D. Ampliación de la Clase `Symbol.java`
- Clasificación por `Kind` (`VARIABLE`, `TEMPORAL`, `CONSTANTE`, `ETIQUETA`).
- Métodos estáticos de fábrica: `nuevoTemporal(nombre, tipo)`, `nuevaConstante(valor, tipo)` y `nuevaEtiqueta(nombre)`.
- Método `getDisplayName()` para visualización legible y `isLabel()` para no reservar slots en la pila.

### E. Incorporación de Estructuras de Control (`if`, `if-else`, `while`) y Comparaciones
1. **Léxico ([Lexer.flex](src/main/jflex/ar/edu/unvm/compiladores/lexer/Lexer.flex)):**
   - Palabras reservadas agregadas: `"if"`, `"else"`, `"while"`.
   - Operadores de comparación agregados: `"=="`, `"!="`, `"<="`, `">="`, `"<"`, `">"`.
2. **Sintáctico y Gramática ([Parser.cup](src/main/cup/ar/edu/unvm/compiladores/parser/Parser.cup)):**
   - Se formalizó la diferencia entre:
     - **Sentencia:** Acción ejecutable (`asignacion`, `retorno`, `if`, `while`).
     - **Expresión:** Evaluación que produce un valor único (literales, variables, operaciones aritméticas y relacionales).
   - Reglas gramaticales explícitas con llaves obligatorias `{ ... }` para evitar cualquier ambigüedad sintáctica (*dangling else*):
     ```cup
     sentencia ::= asignacion | retorno
                 | IF PAR_IZQ expr PAR_DER LLAVE_IZQ sentencias LLAVE_DER
                 | IF PAR_IZQ expr PAR_DER LLAVE_IZQ sentencias LLAVE_DER ELSE LLAVE_IZQ sentencias LLAVE_DER
                 | WHILE PAR_IZQ expr PAR_DER LLAVE_IZQ sentencias LLAVE_DER
                 ;
     ```
3. **Nuevos Nodos del AST:**
   - `IfStmtNode.java`: Condición, bloque `then` y bloque opcional `else`.
   - `WhileStmtNode.java`: Condición y bloque de cuerpo `body`.
   - Ambos extienden de `StatementNode` y cuentan con soporte visual en `printTree()`.
4. **Intérprete del AST ([Interpreter.java](src/main/java/ar/edu/unvm/compiladores/interpreter/Interpreter.java)):**
   - Ejecuta directamente bucles y condicionales evaluando la verdad de la condición y despachando las sentencias hijas.

### F. Generación de Assembly GNU x86-64 y GCC (64 bits)
Implementado en `GnuAssemblyGenerator.java` (`ar.edu.unvm.compiladores.codegen`) conforme a la directiva de cátedra y la guía técnica de arquitectura x86-64:
1. **Traducción Instrucción por Instrucción**: Recorre secuencialmente la lista de instrucciones de tres direcciones generada en la fase previa.
2. **Mapeo de Pila (Stack Frame)**:
   - Asigna a cada variable local y temporal una posición fija de 8 bytes (quadword) en el marco de pila: `-8(%rbp)`, `-16(%rbp)`, `-24(%rbp)`, etc.
   - Alineación canónica a 16 bytes y prólogo: `pushq %rbp; movq %rsp, %rbp; subq $N, %rsp`.
3. **Comparaciones y Saltos en Assembly**:
   - `cmpq %r11, %r10` para comparar operandos en registros.
   - Generación de booleano en `%al` con `setl`, `setg`, `setle`, `setge`, `sete`, `setne`.
   - Extensión con `movzbq %al, %r10` y almacenamiento en pila.
   - Saltos locales: `.L1:`, `jmp .L1` (salto incondicional), y `cmpq $0, %r10; je .L2` (salto condicional si falso).
4. **Convención de Llamadas x86-64 GNU**:
   - Retorno en `%rax`.
   - Epílogo: `movq %rbp, %rsp; popq %rbp; ret`.
   - Exporta el código generado en formato AT&T directamente al archivo fuente ensamblador `.s`.

---

## 4. Guía Paso a Paso para Compilar y Ejecutar

Abre tu terminal en la carpeta del proyecto y sigue estos pasos:

### Paso 1: Compilar el Compilador
Ejecuta el script de compilación (regenera el analizador léxico con JFlex, el parser con CUP y compila todas las clases Java en la carpeta `out/`):

- **En PowerShell:**
  ```powershell
  .\compilar.ps1
  ```
- **En CMD (Símbolo del sistema):**
  ```cmd
  compilar.bat
  ```

*Salida exitosa:*
```
Generando lexer...
Generando parser...
------- CUP v0.11a beta 20060608 Parser Generation Summary -------
  0 errors and 0 warnings
...
Compilando Java...
Compilacion terminada.
```

---

### Paso 2: Ejecutar Pruebas con Pipeline Completo y Assembly GNU x86-64

#### Prueba de Control de Flujo (`while` + `if/else`):
- **Comando:**
  ```powershell
  .\ejecutar.ps1 src\programa_prueba_control.txt
  ```
- **Ver exclusivamente el Assembly GNU x86-64 generado:**
  ```powershell
  .\ejecutar.ps1 src\programa_prueba_control.txt --gnu
  ```

---

### Paso 3: Probar el Ejecutable Nativo con GCC

Toma el archivo ensamblador generado en el paso anterior y compílalo directamente con GCC:

```powershell
# 1. Ensamblar y linkear con GCC
gcc src\programa_prueba_control.s -o programa_control.exe

# 2. Ejecutar el programa nativo de 64 bits
.\programa_control.exe

# 3. Verificar el valor de retorno retornado en %rax
$LASTEXITCODE
```
*Salida esperada:*
```
10
```

---

### Paso 4: Validar Detección de Errores Semánticos

#### Error 1: Variable Redeclarada
```powershell
.\ejecutar.ps1 src\prueba_error_redeclaracion.txt
```
*Salida:* `[VARIABLE REDECLARADA] (linea 4): la variable 'x' ya fue declarada previamente.`

#### Error 2: Variable No Declarada
```powershell
.\ejecutar.ps1 src\prueba_error_no_declarada.txt
```
*Salida:* `[VARIABLE NO DECLARADA] (linea 4): la variable 'y' no esta declarada.`

#### Error 3: Incompatibilidad de Tipos en Condición de Control
```powershell
.\ejecutar.ps1 src\prueba_error_condicion_tipo.txt
```
*Salida:* `[ERROR DE TIPO] (linea 5): la condicion de la sentencia 'if' debe ser de tipo BOOL pero se obtuvo int.`

---

### Paso 5: Menú Interactivo y Scripts Paso a Paso

#### A. Menú Interactivo Completo
```powershell
.\menu.ps1
```
*(o en CMD: `menu.bat`)*

#### B. Scripts Directos Paso a Paso

| Script PowerShell | Script CMD | Fase que ejecuta y muestra |
| :--- | :--- | :--- |
| `.\paso1_lexer.ps1` | `paso1_lexer.bat` | **Paso 1 / Punto 3:** Analizador Léxico (JFlex). Muestra tokens, lexemas, líneas y columnas. |
| `.\paso2_parser_ast.ps1` | `paso2_parser_ast.bat` | **Paso 2 / Puntos 4 y 5:** Parser CUP y Árbol Sintáctico (AST). Muestra `VarDeclNode`, `VarUseNode`, `IfStmtNode`, `WhileStmtNode`. |
| `.\paso3_semantica.ps1` | `paso3_semantica.bat` | **Paso 3:** Análisis Semántico. Valida los 3 errores e imprime la Tabla de Símbolos (`SymbolTable`). |
| `.\paso4_interprete.ps1` | `paso4_interprete.bat` | **Paso 4 / Punto 6:** Intérprete del AST. Ejecuta paso a paso y muestra la memoria final. |
| `.\paso5_c3d.ps1` | `paso5_c3d.bat` | **Paso 5:** Código de Tres Direcciones. Muestra la lista C3D, etiquetas, saltos y ejecución con `InterpreteC3D`. |
| `.\paso6_assembly.ps1` | `paso6_assembly.bat` | **Paso 6 / Punto 7:** Generador Assembly GNU x86-64. Muestra el código ensamblador generado instrucción por instrucción. |

*(Nota: En todos los scripts se puede indicar un archivo como parámetro, por ejemplo: `.\paso3_semantica.ps1 src\prueba_error_condicion_tipo.txt`).*
