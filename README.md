# Guía de Estudio y Documento Guía: Fases de un Compilador
**Universidad Nacional de Villa Mercedes (UNViME)**  
**Escuela de Ingeniería y Ciencias Ambientales — Cátedra de Compiladores**  

> [!NOTE]
> **Documento Guía Oficial del Proyecto:** Este `README.md` es la guía principal de estudio, comprensión teórica y ejecución práctica del compilador. Resume conceptualmente cada etapa del pipeline y documenta todas las funcionalidades implementadas.

---

## Concepto Central: El Pipeline del Compilador
Un compilador es una **tubería (*pipeline*)**: la salida de una etapa es la entrada de la siguiente.

```text
Código Fuente (.txt) 
       │
       ▼
[1. Lexer JFlex] ──────► Tira de Tokens
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
                                   [7. GCC Linker] ──► Ejecutable (.exe)
```

---

## ¿CÓMO EJECUTAR?

### Ejecutar el Menú Interactivo:
```powershell
.\menu.ps1
```
*(o en CMD: `menu.bat`)*

### ¿CÓMO EJECUTAR UN TEST DE SRC?
Podés pasarle la ruta de cualquier archivo de prueba como argumento al menú o a los scripts:
```powershell
.\menu.ps1 src\programa_prueba_control.txt
```
*(o también: `.\menu.ps1 src\prueba_c3d_aritmetica.txt`)*

### Compilar el Compilador:
Si modificás el léxico (`Lexer.flex`), la gramática (`Parser.cup`) o clases Java, recompilá todo con:
```powershell
.\compilar.ps1
```

---

## 1. Lexer (Tokens) — Análisis Léxico

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

**Comando para probar esta fase:**
```powershell
.\paso1_lexer.ps1 src\programa_prueba_control.txt
```

---

## 2. Parser y AST — Análisis Sintáctico

* **Qué hace:** Toma la tira de tokens del Lexer y se fija si respetan las reglas gramaticales del lenguaje (que tenga una estructura `main()`, que no falten llaves `{ }`, paréntesis `( )` o puntos y comas `;`).
* **Sentencia vs. Expresión (Concepto clave de clase):**
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

**Comando para probar esta fase:**
```powershell
.\paso2_parser_ast.ps1 src\programa_prueba_control.txt
```

---

## 3. Semántica y Tabla de Símbolos — Análisis Semántico

* **Qué hace:** Revisa el **significado y la coherencia lógica** sobre el AST. El parser solo revisa la forma/estructura; la semántica revisa que lo escrito tenga sentido lógico.
* **Qué verifica (las 3 categorías de errores obligatorias):**
  1. **Que no uses variables sin declarar (`[VARIABLE NO DECLARADA]`):**
     * *Ejemplo:* Usar `y` en una cuenta sin haber hecho `int y;` antes.
  2. **Que no declares dos veces la misma variable (`[VARIABLE REDECLARADA]`):**
     * *Ejemplo:* Poner dos veces `int x;` en el mismo programa.
  3. **Que los tipos coincidan (`[ERROR DE TIPO]`):**
     * Asignar un booleano a un entero o viceversa.
     * Operar aritméticamente con booleanos (ej. `true + 5`).
     * Usar una condición que no sea de tipo booleano en estructuras de control (ej: `if (x + 5)` lanza error de tipo porque la condición del `if` o `while` debe ser obligatoriamente `bool`).
* **Qué es la Tabla de Símbolos:** Es un mapa/diccionario en memoria (`SymbolTable.java`) que anota el nombre de cada variable, su tipo (`int`, `bool`) y su valor en memoria.

> **En criollo:** Se asegura de que no hagas cosas sin sentido, como sumar peras con manzanas o usar variables que no existen.

**Comandos para validar errores semánticos:**
```powershell
# Variable redeclarada
.\paso3_semantica.ps1 src\prueba_error_redeclaracion.txt

# Variable no declarada
.\paso3_semantica.ps1 src\prueba_error_no_declarada.txt

# Discrepancia de tipos
.\paso3_semantica.ps1 src\prueba_error_tipo_asignacion.txt

# Condición no booleana en if / while
.\paso3_semantica.ps1 src\prueba_error_condicion_tipo.txt
```

---

## 4. Intérprete AST — Evaluación en Memoria

* **Qué hace:** Recorre el árbol sintáctico recién validado y lo ejecuta paso a paso directamente en Java sin generar código de máquina todavía.
* **Para qué sirve:** Para validar de forma inmediata que el programa calcula bien los valores, ejecuta las ramas `if / else`, itera los bucles `while` y saber con exactitud qué valor devuelve el `return` en la práctica.

> **En criollo:** Es como un simulador que corre tu código directamente sobre el árbol para ver si da el resultado esperado.

**Comando para probar esta fase:**
```powershell
.\paso4_interprete.ps1 src\programa_prueba_control.txt
```

---

## 5. Código de 3 Direcciones (C3D) — Código Intermedio

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

**Comando para probar esta fase:**
```powershell
.\paso5_c3d.ps1 src\programa_prueba_control.txt
```

---

## 6. Assembly x86-64 y GCC — Generación de Código Final

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

#### 1. Instalar GCC en Windows (si no lo tenés):
```powershell
winget install --id BrechtSanders.WinLibs.POSIX.UCRT
```
*(Reiniciá la terminal tras instalar para actualizar el PATH. Verificá con `gcc --version`)*.

#### 2. Probar el ejecutable de punta a punta:
```powershell
# Paso A: Generar el archivo assembly GNU x86-64 (.s)
.\paso6_assembly.ps1 src\programa_prueba_control.txt

# Paso B: Ensamblar y linkear con GCC para crear el ejecutable .exe
gcc src\programa_prueba_control.s -o programa_control.exe

# Paso C: Ejecutar el programa nativo de 64 bits
.\programa_control.exe

# Paso D: Consultar el valor retornado en %rax
$LASTEXITCODE
```
*(Devuelve `10`, correspondiente a la sumatoria calculada en el bucle `while`)*.

---

## Resumen de Scripts del Proyecto

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

## Documentación Formal
- 📄 **[INFORME_PREPROYECTO.md](INFORME_PREPROYECTO.md)**: Informe técnico formal de arquitectura, gramática y especificación.
- 📄 **[GUIA_Y_CAMBIOS_PROYECTO.md](GUIA_Y_CAMBIOS_PROYECTO.md)**: Registro histórico detallado de decisiones de diseño y modificaciones.
