# Documento Guía: Fases de un Compilador
**Universidad Nacional de Villa Mercedes (UNViME)**  
**Escuela de Ingeniería y Ciencias Ambientales — Cátedra de Compiladores**  

> [!NOTE]
> **Documento Guía Oficial del Proyecto:** Este `README.md` es la guía principal de estudio, comprensión teórica y ejecución práctica del compilador. Resume conceptualmente cada etapa del pipeline, documenta las dependencias, describe los programas de prueba y detalla todos los comandos de ejecución.

---

## 1. Concepto Central: El Pipeline del Compilador
Un compilador es una **tubería (*pipeline*)**: la salida de una etapa es la entrada de la siguiente.

```text
Código Fuente (.txt) 
       │
       ▼
[1. Lexer JFlex] ──────► Tira de Tokens (+, -, *, /, <, >, ==, if, while, etc.)
       │
       ▼
[2. Parser CUP]  ──────► Árbol Sintáctico Abstracto (AST)
       │
       ▼
[3. Semántica]   ──────► AST Verificado + Tabla de Símbolos
       │
       ├─────────────────────────────────┐
       ▼                                 ▼
[4. Intérprete AST] (Simulación)   [5. Generador C3D] ──► Cuádruplas / 3 Direcciones
                                         │
                                         ▼
                                   [6. Assembly GNU x86-64] (.s)
                                         │
                                         ▼
                                   [7. GCC Linker] ──► Ejecutable Nativo (.exe)
```

---

## 2. Requisitos Previos y Dependencias

Para compilar y ejecutar este proyecto no necesitás instalar herramientas complejas ni configurar Maven, ya que todas las dependencias requeridas se encuentran empaquetadas dentro de la carpeta `lib/`:

### A. Java Development Kit (JDK)
* **Versión requerida:** JDK 8, 11, 17 o superior.
* Verificá que `java` y `javac` estén instalados en tu sistema:
  ```powershell
  java -version
  javac -version
  ```

### B. Librerías incluidas en `lib/` (No requiere descarga externa)
* `lib/jflex-1.9.1/lib/jflex-full-1.9.1.jar`: Generador de analizadores léxicos JFlex.
* `lib/java-cup-11b-2015.03.26.jar`: Generador de parsers LALR CUP.
* `lib/java-cup-11b-runtime.jar`: Runtime de Java-CUP necesario para ejecutar el parser en memoria.

### C. GCC (MinGW-w64) — Necesario para el Paso 6 (Assembly a .exe)
Para compilar el código assembly generado (`.s`) a un ejecutable nativo de Windows (`.exe`):
* Si no tenés GCC instalado, podés instalarlo con un solo comando mediante `winget`:
  ```powershell
  winget install --id BrechtSanders.WinLibs.POSIX.UCRT
  ```
* Verificá la instalación abriendo una nueva terminal:
  ```powershell
  gcc --version
  ```

### D. Política de Ejecución de Scripts en PowerShell (Windows)
Si al ejecutar los scripts `.ps1` Windows muestra un mensaje de restricción de políticas (`Execution_Policies`), habilitá la ejecución para la sesión actual ejecutando:
```powershell
Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass
```

---

## 3. Estructura del Proyecto

```text
compiladores/
├── lib/                             # Librerías JAR (JFlex y CUP)
│   ├── java-cup-11b-2015.03.26.jar
│   ├── java-cup-11b-runtime.jar
│   └── jflex-1.9.1/lib/jflex-full-1.9.1.jar
├── out/                             # Clases Java compiladas (.class)
├── src/
│   ├── main/
│   │   ├── jflex/                  # Especificación léxica (Lexer.flex)
│   │   ├── cup/                    # Especificación gramatical (Parser.cup)
│   │   └── java/                   # Código fuente Java del compilador
│   │       └── ar/edu/unvm/compiladores/
│   │           ├── ast/            # Nodos del Árbol Sintáctico Abstracto
│   │           ├── symbols/        # Tabla de Símbolos y clases Symbol
│   │           ├── semantic/       # Analizador Semántico (3 errores)
│   │           ├── interpreter/    # Intérprete y Evaluador del AST
│   │           ├── c3d/            # Generador y Evaluador de C3D (Cuádruplas)
│   │           ├── codegen/        # Generador de Assembly GNU x86-64
│   │           └── Main.java       # Punto de entrada principal
│   └── *.txt                       # Programas de prueba (válidos y con errores)
├── compilar.ps1 / compilar.bat     # Recompila JFlex, CUP y Java
├── menu.ps1 / menu.bat             # Menú interactivo de consola
├── ejecutar.ps1 / ejecutar.bat     # Ejecutor directo del pipeline
├── paso1_lexer.ps1 ... paso6.ps1   # Scripts directos para cada etapa
└── README.md                       # Documento guía oficial
```

---

## 4. ¿CÓMO EJECUTAR?

### A. Ejecutar el Menú Interactivo (Recomendado):
El menú interactivo te permite probar cualquier etapa del compilador con una sola tecla:
```powershell
.\menu.ps1
```
*(o en CMD: `menu.bat`)*

### B. ¿Cómo pasarle un archivo de prueba específico al menú?
Podés pasar la ruta de cualquier archivo `.txt` como argumento:
```powershell
.\menu.ps1 src\programa_prueba_control.txt
```
*(o también: `.\menu.ps1 src\prueba_c3d_aritmetica.txt`)*

### C. Recompilar el Compilador:
Si modificás el archivo léxico (`Lexer.flex`), la gramática (`Parser.cup`) o cualquier clase Java, regenerá y recompilá el proyecto con:
```powershell
.\compilar.ps1
```
*(o en CMD: `compilar.bat`)*

### D. Ejecución por Línea de Comandos (`ejecutar.ps1`):
Podés ejecutar directamente cualquier etapa pasando opciones por parámetro:
```powershell
# Ejecutar todo el pipeline completo:
.\ejecutar.ps1 src\programa_prueba_control.txt

# Opciones individuales:
.\ejecutar.ps1 src\programa_prueba_control.txt --lexer     # Ver solo tokens
.\ejecutar.ps1 src\programa_prueba_control.txt --ast       # Ver solo el AST
.\ejecutar.ps1 src\programa_prueba_control.txt --semantic  # Ver análisis semántico
.\ejecutar.ps1 src\programa_prueba_control.txt --eval      # Ejecutar en intérprete
.\ejecutar.ps1 src\programa_prueba_control.txt --c3d       # Ver cuádruplas C3D
.\ejecutar.ps1 src\programa_prueba_control.txt --gnu       # Generar Assembly .s
```

---

## 5. Fases del Compilador (Teoría y Práctica Paso a Paso)

---

### Fase 1: Lexer (Tokens) — Análisis Léxico

* **Qué hace:** Lee el archivo fuente como puro texto (carácter por carácter) y lo corta en "palabras clave" o "fichas" llamadas **Tokens**.
* **Ejemplo:** Si ve `int x = 1;`, no sabe qué significa, pero reconoce:
  * `INT`, `ID(x)`, `ASIGNACION(=)`, `NRO(1)`, `PUNTOYCOMA(;)`.
* **Tokens y Palabras Soportadas:**
  * **Tipos y función:** `int`, `bool`, `void`, `main`, `return`.
  * **Estructuras de control:** `if`, `else`, `while`.
  * **Booleanos:** `true`, `false`.
  * **Operadores aritméticos:** `+`, `-`, `*`, `/`.
  * **Operadores de comparación:** `<`, `>`, `<=`, `>=`, `==`, `!=`.
  * **Delimitadores:** `(`, `)`, `{`, `}`, `;`.
* **Herramienta:** JFlex (`Lexer.java` generado desde `Lexer.flex`). Usa expresiones regulares para reconocer patrones.

> **En criollo:** Agarra todo el archivo que tenés, va leyendo línea por línea e identifica cada palabra clave, número o identificador junto a su valor y posición.

**Comando directo:**
```powershell
.\paso1_lexer.ps1 src\programa_prueba_control.txt
```

---

### Fase 2: Parser y AST — Análisis Sintáctico

* **Qué hace:** Toma la tira de tokens del Lexer y se fija si respetan las reglas gramaticales del lenguaje (que tenga una estructura `main()`, que no falten llaves `{ }`, paréntesis `( )` o puntos y comas `;`).
* **Sentencia vs. Expresión (Concepto clave de cátedra):**
  * **Sentencia:** Instrucción que le ordena a la computadora realizar una acción ejecutable (ej: asignación `x = 5;`, retorno `return x;`, condicional `if / else`, bucle `while`).
  * **Expresión:** Combinación de valores, variables y operadores que el compilador evalúa para producir un **único valor** (ej: `5`, `y`, `5 + 3`, `x < 10`).
* **Qué es el AST (Árbol Sintáctico Abstracto):** Si la sintaxis es correcta, construye un árbol en memoria donde cada nodo es una acción:
  * `VarDeclNode`: Declaración de variable (`int x;`).
  * `VarUseNode`: Uso de variable dentro de una expresión (`x + 1`).
  * `AssignStmtNode`: Asignación (`x = expr;`).
  * `IfStmtNode`: Condicional con rama `then` y rama opcional `else`.
  * `WhileStmtNode`: Bucle con condición y lista de sentencias de cuerpo.
  * `BinaryExprNode`: Expresiones binarias aritméticas y relacionales.
* **Por qué un árbol:** Porque el árbol resuelve naturalmente la **precedencia matemática**: en `1 + 3 * 2`, la multiplicación queda como nodo hijo de la suma.
* **Herramienta:** CUP (`Parser.java` generado desde `Parser.cup`).

> **En criollo:** Agarra los tokens y verifica que el código esté bien escrito según las reglas. Si todo es correcto, devuelve el árbol sintáctico (AST) ordenado con la jerarquía de operaciones.

**Comando directo:**
```powershell
.\paso2_parser_ast.ps1 src\programa_prueba_control.txt
```

---

### Fase 3: Semántica y Tabla de Símbolos — Análisis Semántico

* **Qué hace:** Revisa el **significado y la coherencia lógica** sobre el AST. El parser solo revisa la forma/estructura; la semántica revisa que lo escrito tenga sentido lógico.
* **Qué verifica (las 3 categorías de errores obligatorias):**
  1. **Que no uses variables sin declarar (`[VARIABLE NO DECLARADA]`):**
     * Usar una variable en una expresión o asignación sin haberla declarado previamente.
  2. **Que no declares dos veces la misma variable (`[VARIABLE REDECLARADA]`):**
     * Declarar dos veces el mismo identificador en el mismo ámbito.
  3. **Que los tipos coincidan (`[ERROR DE TIPO]`):**
     * Asignar un booleano a un entero o viceversa.
     * Operar aritméticamente con booleanos (ej. `true + 5`).
     * Usar una condición que no sea de tipo booleano en estructuras de control (ej: `if (x + 5)` lanza error de tipo porque la condición del `if` o `while` debe ser estrictamente `bool`).
* **Qué es la Tabla de Símbolos:** Es un mapa/diccionario en memoria (`SymbolTable.java`) que anota el nombre de cada variable, su tipo (`int`, `bool`) y su valor en memoria.

> **En criollo:** Se asegura de que no hagas cosas sin sentido, como sumar peras con manzanas o usar variables que no existen.

**Comandos para validar detección de errores semánticos:**
```powershell
# Variable redeclarada
.\paso3_semantica.ps1 src\prueba_error_redeclaracion.txt

# Variable no declarada
.\paso3_semantica.ps1 src\prueba_error_no_declarada.txt

# Discrepancia de tipos en asignación
.\paso3_semantica.ps1 src\prueba_error_tipo_asignacion.txt

# Discrepancia de tipos en operaciones aritméticas
.\paso3_semantica.ps1 src\prueba_error_tipo_operacion.txt

# Condición no booleana en if / while
.\paso3_semantica.ps1 src\prueba_error_condicion_tipo.txt
```

---

### Fase 4: Intérprete AST — Evaluación en Memoria

* **Qué hace:** Recorre el árbol sintáctico recién validado y lo ejecuta paso a paso directamente en Java sin generar código de máquina todavía.
* **Para qué sirve:** Para validar de forma inmediata que el programa calcula bien los valores, ejecuta las ramas `if / else`, itera los bucles `while` y saber con exactitud qué valor devuelve el `return` en la práctica.

> **En criollo:** Es como un simulador que corre tu código directamente sobre el árbol para ver si da el resultado esperado.

**Comando directo:**
```powershell
.\paso4_interprete.ps1 src\programa_prueba_control.txt
```

---

### Fase 5: Código de 3 Direcciones (C3D) — Código Intermedio

* **Qué hace:** Desarma las expresiones complejas del árbol en una lista de instrucciones lineales y simples donde cada operación tiene **a lo sumo tres partes**: `resultado = op1 operador op2`.
* **Temporales (`T1, T2, ...`):** Como una CPU no puede calcular `x + 3 * 2 * y` de un solo saque, el generador (`GeneradorC3D.java`) va guardando resultados parciales en variables temporales:
  ```text
  T1 = 3 * 2
  T2 = T1 * y
  T3 = x + T2
  x = T3
  ```
* **Etiquetas y Saltos (`LABEL`, `IFFALSE`, `GOTO`):** Para desarmar bucles `while` y condicionales `if/else`, genera puntos de salto:
  ```text
  L1:
  T1 = i < 5
  ifFalse T1 goto L2
  ...
  goto L1
  L2:
  ```
* **Cuádruplas:** Cada instrucción se estructura formalmente como `(OP, ARG1, ARG2, RES)` y es evaluada por `InterpreteC3D.java`.

> **En criollo:** Simplifica las operaciones difíciles en instrucciones atómicas paso a paso, listas para ser traducidas a lenguaje ensamblador.

**Comando directo:**
```powershell
.\paso5_c3d.ps1 src\programa_prueba_control.txt
```

---

### Fase 6: Assembly x86-64 y GCC — Generación de Código Final

* **Qué hace:** Recorre la lista de C3D instrucción por instrucción y traduce cada una a código de máquina assembly GNU real de 64 bits con sintaxis AT&T (`GnuAssemblyGenerator.java`), guardándolo en un archivo `.s`.
* **Conceptos clave que pide el profe:**
  * **Stack Frame (Pila):** Cada variable (`x`, `y`) y cada temporal (`T1`, `T2`) tiene un espacio reservado de 8 bytes (quadword) en la memoria: `-8(%rbp)`, `-16(%rbp)`, `-24(%rbp)`, etc.
  * **Prólogo y Epílogo:**
    * **Prólogo:** `pushq %rbp; movq %rsp, %rbp; subq $N, %rsp` (configura la base de la pila y reserva memoria alineada a 16 bytes).
    * **Epílogo:** Carga el resultado a devolver en `%rax`, restaura la pila (`movq %rbp, %rsp; popq %rbp`) y sale con `ret`.
  * **Registros de trabajo (`%r10`, `%r11`):** x86-64 no permite hacer operaciones aritméticas directas de memoria a memoria (ej: no podés sumar dos direcciones de pila directamente). Hay que cargar primero a los registros temporales `%r10` y `%r11`, operar ahí, y guardar el resultado en la pila.
  * **Comparaciones y Saltos:** Compara con `cmpq %r11, %r10`, genera el booleano con instrucciones condicionales (`setl`, `setge`, `sete`, etc.), lo extiende con `movzbq %al, %r10`, y salta con `je .L` o `jmp .L`.

---

### Compilación y Ejecución Nativa con GCC

**GCC** (GNU Compiler Collection) es el compilador externo que toma el archivo `.s` generado por nuestro compilador y genera el binario ejecutable (`.exe`).

#### Probar el ejecutable de punta a punta:
```powershell
# 1. Generar el archivo assembly GNU x86-64 (.s) con el compilador:
.\paso6_assembly.ps1 src\programa_prueba_control.txt

# 2. Ensamblar y linkear con GCC para crear el ejecutable nativo (.exe):
gcc src\programa_prueba_control.s -o programa_control.exe

# 3. Ejecutar el programa nativo de 64 bits:
.\programa_control.exe

# 4. Consultar el valor retornado en %rax:
$LASTEXITCODE
```
*(Devuelve `10`, correspondiente a la sumatoria calculada en el bucle `while`)*.

---

## 6. Programas de Prueba Disponibles en `src/`

| Archivo de Prueba | Propósito y Contenido |
| :--- | :--- |
| `src\programa_prueba_control.txt` | **Prueba completa de control de flujo:** bucle `while`, condicional `if-else`, operadores relacionales y retorno. |
| `src\prueba_c3d_aritmetica.txt` | **Prueba aritmética compleja:** operaciones combinadas, paréntesis y precedencia matemática (`4 + 3 * 2 + (10 - 4) / 2 = 13`). |
| `src\prueba_c3d_bool.txt` | **Prueba booleana:** asignaciones de variables tipo `bool` (`true`, `false`). |
| `src\prueba_error_redeclaracion.txt` | **Error semántico:** redeclaración de una misma variable en el ámbito. |
| `src\prueba_error_no_declarada.txt` | **Error semántico:** uso de una variable sin previa declaración. |
| `src\prueba_error_tipo_asignacion.txt` | **Error semántico:** asignación incompatible (`int` a variable `bool`). |
| `src\prueba_error_tipo_operacion.txt` | **Error semántico:** suma aritmética entre un entero y un booleano. |
| `src\prueba_error_condicion_tipo.txt` | **Error semántico:** condición entera en `if (x + 5)` en lugar de tipo `bool`. |

---

## 7. Resumen de Scripts del Proyecto

| Script PowerShell | Script CMD | Etapa / Qué muestra |
| :--- | :--- | :--- |
| `.\compilar.ps1` | `compilar.bat` | Recompila el lexer JFlex, el parser CUP y las clases Java. |
| `.\menu.ps1` | `menu.bat` | **Menú interactivo completo** para seleccionar cualquier fase. |
| `.\ejecutar.ps1 <arch>` | `ejecutar.bat` | Ejecuta el pipeline completo de punta a punta. |
| `.\paso1_lexer.ps1` | `paso1_lexer.bat` | **Paso 1:** Muestra los tokens y lexemas reconocidos por JFlex. |
| `.\paso2_parser_ast.ps1` | `paso2_parser_ast.bat` | **Paso 2:** Muestra el Árbol Sintáctico Abstracto (AST) generado por CUP. |
| `.\paso3_semantica.ps1` | `paso3_semantica.bat` | **Paso 3:** Muestra la validación de errores semánticos y la Tabla de Símbolos. |
| `.\paso4_interprete.ps1` | `paso4_interprete.bat` | **Paso 4:** Ejecuta el programa en memoria sobre el AST e imprime el retorno. |
| `.\paso5_c3d.ps1` | `paso5_c3d.bat` | **Paso 5:** Muestra el Código de Tres Direcciones, cuádruplas y evaluación. |
| `.\paso6_assembly.ps1` | `paso6_assembly.bat` | **Paso 6:** Genera el código ensamblador GNU x86-64 (`.s`) para GCC. |

---

## 8. Documentación Formal
- 📄 **[INFORME_PREPROYECTO.md](INFORME_PREPROYECTO.md)**: Informe técnico formal de arquitectura, gramática y especificación para la cátedra.
