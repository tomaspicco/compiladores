# Guía Completa del Proyecto y Registro de Cambios
**Universidad Nacional de Villa Mercedes (UNVM)**  
**Escuela de Ingeniería y Ciencias Ambientales - Compiladores**  
**Proyecto:** Compilador JFlex + CUP con AST, Tabla de Símbolos, Validación Semántica, C3D, Seudo-Assembly y Assembly GNU x86-64.

---

## 1. Cómo Acceder al Proyecto en tu Computadora

El proyecto se encuentra ubicado físicamente en tu entorno:
```
C:\Users\maxi\Desktop\DECOMPILER\compiladores
```

### Opciones para acceder y abrirlo:

#### Opción A: Desde la consola PowerShell (Recomendado)
1. Presiona `Win + X` y selecciona **Terminal** o **PowerShell**.
2. Escribe el comando para ingresar a la carpeta del proyecto:
   ```powershell
   cd "C:\Users\maxi\Desktop\DECOMPILER\compiladores"
   ```

#### Opción B: Desde el Símbolo del Sistema (CMD)
1. Presiona `Win + R`, escribe `cmd` y presiona Enter.
2. Ingresa a la carpeta ejecutando:
   ```cmd
   cd /d "C:\Users\maxi\Desktop\DECOMPILER\compiladores"
   ```

#### Opción C: Desde Visual Studio Code
- Abre tu terminal y escribe:
   ```powershell
   code "C:\Users\maxi\Desktop\DECOMPILER\compiladores"
   ```

---

## 2. Requisitos Previos para la Ejecución

Para poder compilar y ejecutar el proyecto necesitas tener instalado Java Development Kit (JDK 8, 11, 17 o superior):
- Verifica que `java` y `javac` estén accesibles abriendo una consola y escribiendo:
  ```powershell
  java -version
  javac -version
  ```
Las librerías de JFlex (`jflex-full-1.9.1.jar`) y Java-CUP (`java-cup-11b-2015.03.26.jar` y `java-cup-11b-runtime.jar`) se encuentran integradas dentro de la carpeta `lib/` del proyecto.

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
3. **`ERROR_DE_TIPO`**: Verifica que los tipos coincidan en asignaciones, que los operadores aritméticos (`+`, `-`, `*`, `/`) solo operen con enteros, y que el tipo de retorno coincida con la firma de `main`.
   *Ejemplo:* `[ERROR DE TIPO] (linea 4): tipo incompatible en asignacion a 'x'. Se esperaba int pero se obtuvo bool.`

### C. Implementación Completa de Código de Tres Direcciones (C3D)
Ubicado en el paquete `ar.edu.unvm.compiladores.c3d`:
1. **`Operador3D.java`**: Enum con operadores: `SUMA` (+, ADD), `RESTA` (-, SUB), `MULT` (*, MUL), `DIV` (/, DIV), `ASIGNACION` (=, ASSIGN) y `RETORNO` (return, RET).
2. **`Instruccion3D.java`**: Cuádrupla que almacena `operador`, `operando1`, `operando2` y `resultado` como referencias directas a objetos `Symbol`, conservando la información semántica completa.
3. **`CodigoTresDirecciones.java`**: Administra el vector dinámico de instrucciones (`Vector<Instruccion3D>`), ofreciendo métodos para listar instrucciones y generar la tabla de cuádruplas.
4. **`GeneradorC3D.java`**: Recorre el AST en post-orden (**bottom-up: hojas a raíz**) resolviendo expresiones anidadas mediante variables temporales sucesivas (`T1, T2, T3, ...`).
5. **`InterpreteC3D.java`**: Intérprete que recorre secuencialmente el vector de C3D para simular la ejecución.

### D. Ampliación de la Clase `Symbol.java`
- Clasificación por `Kind` (`VARIABLE`, `TEMPORAL`, `CONSTANTE`).
- Métodos estáticos de fábrica: `nuevoTemporal(nombre, tipo)` y `nuevaConstante(valor, tipo)`.
- Método `getDisplayName()` para visualización legible.

### E. Generación de Assembly GNU x86-64 a partir de C3D (64 bits)
Implementado en `GnuAssemblyGenerator.java` (`ar.edu.unvm.compiladores.codegen`) conforme a la directiva de cátedra y la guía técnica de arquitectura x86-64:
1. **Traducción Instrucción por Instrucción**: Recorre secuencialmente la lista de instrucciones de tres direcciones generada en la fase previa.
2. **Mapeo de Pila (Stack Frame)**:
   - Asigna a cada variable local y temporal una posición fija de 8 bytes (quadword) en el marco de pila: `-8(%rbp)`, `-16(%rbp)`, `-24(%rbp)`, etc.
   - Calcula el total `N` de variables y temporales para configurar el prólogo: `enter $(8 * N), $0`.
3. **Uso de Registros de Trabajo**:
   - Utiliza exclusivamente los registros temporales `%r10` y `%r11` para cargar operandos desde la pila o inmediatos (`$val`), realizar la operación y guardar el resultado nuevamente en la pila.
   - En la división (`idivq`), prepara el dividendo extendido en `%rax` mediante `cqo`.
4. **Convención de Llamadas x86-64 GNU**:
   - En sentencias de retorno (`return`), coloca el valor en `%rax` y ejecuta `leave` y `ret`.
   - Incluye un epílogo por defecto (`mov $0, %rax; leave; ret`) para salidas seguras.
   - Exporta el código generado en formato AT&T directamente al archivo fuente ensamblador `.s`.

### F. Actualización del Flujo Principal en `Main.java`
- Ejecuta el análisis semántico y AST con anotaciones resueltas.
- Genera y muestra C3D, cuádruplas y ejecución con `InterpreteC3D`.
- Genera y exporta el Seudo-Assembly (`.asm`) y el Assembly GNU x86-64 (`.s`).

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

#### Prueba Principal: Expresiones Compuestas, C3D y Ensamblador GNU
- **Comando:**
  ```powershell
  .\ejecutar.ps1 src\programa_prueba.txt
  ```
- **Comando para ver exclusivamente el Assembly GNU x86-64:**
  ```powershell
  .\ejecutar.ps1 src\programa_prueba.txt --gnu
  ```
- **Salida de Assembly GNU x86-64:**
  ```gas
  .globl main
  main:
      enter $(8 * 6), $0
      # Marco de pila (stack frame):
      #   x          -> -8(%rbp)
      #   y          -> -16(%rbp)
      #   T1         -> -24(%rbp)
      #   T2         -> -32(%rbp)
      #   T3         -> -40(%rbp)
      #   T4         -> -48(%rbp)

      # x = 1
      mov $1, %r10
      mov %r10, -8(%rbp)

      # T1 = 3 * 2
      mov $3, %r10
      mov $2, %r11
      imul %r11, %r10
      mov %r10, -24(%rbp)

      # T2 = T1 * y
      mov -24(%rbp), %r10
      mov -16(%rbp), %r11
      imul %r11, %r10
      mov %r10, -32(%rbp)

      # T3 = x + T2
      mov -8(%rbp), %r10
      mov -32(%rbp), %r11
      add %r11, %r10
      mov %r10, -40(%rbp)

      # x = T3
      mov -40(%rbp), %r10
      mov %r10, -8(%rbp)

      # return x
      mov -8(%rbp), %rax
      leave
      ret
  ```
*(Genera automáticamente `src/programa_prueba.c3d`, `src/programa_prueba.asm` y `src/programa_prueba.s`).*

---

### Paso 3: Validar los Tres Errores Semánticos de Compilación

#### Error 1: Variable Redeclarada
```powershell
.\ejecutar.ps1 src\prueba_error_redeclaracion.txt
```
*Salida esperada:*
```
[VARIABLE REDECLARADA] (linea 4): la variable 'x' ya fue declarada previamente.
```

#### Error 2: Variable No Declarada (Inexistente)
```powershell
.\ejecutar.ps1 src\prueba_error_no_declarada.txt
```
*Salida esperada:*
```
[VARIABLE NO DECLARADA] (linea 4): la variable 'y' no esta declarada.
[VARIABLE NO DECLARADA] (linea 5): la variable 'z' no esta declarada.
```

#### Error 3: Incompatibilidad de Tipos
```powershell
.\ejecutar.ps1 src\prueba_error_tipo_asignacion.txt
.\ejecutar.ps1 src\prueba_error_tipo_operacion.txt
```
*Salida esperada:*
```
[ERROR DE TIPO] (linea 4): tipo incompatible en asignacion a 'x'. Se esperaba int pero se obtuvo bool.
[ERROR DE TIPO] (linea 5): tipo incompatible en asignacion a 'b'. Se esperaba bool pero se obtuvo int.
```

---

### Paso 4: Ejecución Paso a Paso y Menú Interactivo

Para facilitar la comprensión y la defensa académica ante los docentes, se crearon scripts específicos para cada fase del compilador:

#### A. Menú Interactivo Completo
Puedes lanzar el menú de consola para ejecutar cualquier paso de forma interactiva:
```powershell
.\menu.ps1
```
*(o en CMD: `menu.bat`)*

#### B. Scripts Directos Paso a Paso

| Script PowerShell | Script CMD | Fase que ejecuta y muestra |
| :--- | :--- | :--- |
| `.\paso1_lexer.ps1` | `paso1_lexer.bat` | **Paso 1 / Punto 3:** Analizador Léxico (JFlex). Muestra la tabla de tokens, lexemas, líneas y columnas. |
| `.\paso2_parser_ast.ps1` | `paso2_parser_ast.bat` | **Paso 2 / Puntos 4 y 5:** Parser CUP y Árbol Sintáctico (AST). Muestra la jerarquía con `VarDeclNode` y `VarUseNode`. |
| `.\paso3_semantica.ps1` | `paso3_semantica.bat` | **Paso 3:** Análisis Semántico. Valida los 3 errores e imprime la Tabla de Símbolos (`SymbolTable`). |
| `.\paso4_interprete.ps1` | `paso4_interprete.bat` | **Paso 4 / Punto 6:** Intérprete/Evaluador del AST. Ejecuta las operaciones y muestra el valor final de memoria. |
| `.\paso5_c3d.ps1` | `paso5_c3d.bat` | **Paso 5:** Código de Tres Direcciones. Muestra la lista C3D, la tabla de cuádruplas y la ejecución con `InterpreteC3D`. |
| `.\paso6_assembly.ps1` | `paso6_assembly.bat` | **Paso 6 / Punto 7:** Generador Assembly GNU x86-64. Muestra el código ensamblador generado instrucción por instrucción. |

*(Nota: En todos los scripts se puede indicar un archivo como parámetro, por ejemplo: `.\paso3_semantica.ps1 src\prueba_error_tipo_asignacion.txt`).*

