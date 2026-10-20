# Guía Completa del Proyecto y Registro de Cambios
**Universidad Nacional de Villa Mercedes (UNVM)**  
**Escuela de Ingeniería y Ciencias Ambientales - Compiladores**  
**Proyecto:** Compilador JFlex + CUP con AST, Tabla de Símbolos, Validación Semántica, C3D y Seudo-Assembly.  
**Fecha de Actualización:** Octubre 2026 (Adaptado según directivas de la reunión del 13 oct 2026).

---

## 1. Cómo Acceder al Proyecto en tu Computadora

El proyecto se encuentra ubicado físicamente en tu Escritorio:
```
C:\Users\Tomas\Desktop\JFLEX--ANALIZADOR-LEXICO--main
```

### Opciones para acceder y abrirlo:

#### Opción A: Desde la consola PowerShell (Recomendado)
1. Presiona `Win + X` y selecciona **Terminal** o **PowerShell** (o presiona `Win + R`, escribe `powershell` y presiona Enter).
2. Escribe el siguiente comando para ingresar a la carpeta del proyecto:
   ```powershell
   cd "C:\Users\Tomas\Desktop\JFLEX--ANALIZADOR-LEXICO--main"
   ```

#### Opción B: Desde el Símbolo del Sistema (CMD)
1. Presiona `Win + R`, escribe `cmd` y presiona Enter.
2. Ingresa a la carpeta ejecutando:
   ```cmd
   cd /d "C:\Users\Tomas\Desktop\JFLEX--ANALIZADOR-LEXICO--main"
   ```

#### Opción C: Desde Visual Studio Code
- Abre tu terminal y escribe:
  ```powershell
  code "C:\Users\Tomas\Desktop\JFLEX--ANALIZADOR-LEXICO--main"
  ```
- O dentro de VS Code ve a: **Archivo (File) > Abrir carpeta (Open Folder)** y selecciona la carpeta `JFLEX--ANALIZADOR-LEXICO--main` en tu Escritorio.

#### Opción D: Desde el Explorador de Archivos de Windows
- Abre el Explorador de archivos (`Win + E`), ve a tu Escritorio y haz doble clic en la carpeta `JFLEX--ANALIZADOR-LEXICO--main`.
- En la barra de direcciones superior, puedes escribir `powershell` o `cmd` y pulsar Enter para abrir la consola directamente en esa carpeta.

---

## 2. Requisitos Previos para la Ejecución

Para poder compilar y ejecutar el proyecto necesitas tener instalado Java Development Kit (JDK 8, 11, 17 o superior):
- Verifica que `java` y `javac` estén accesibles abriendo una consola y escribiendo:
  ```powershell
  java -version
  javac -version
  ```
Si ambos comandos devuelven la versión instalada, estás listo. Las librerías de JFlex (`jflex-full-1.9.1.jar`) y Java-CUP (`java-cup-11b-2015.03.26.jar` y `java-cup-11b-runtime.jar`) ya se encuentran integradas dentro de la carpeta `lib/` del proyecto.

---

## 3. Registro Detallado de Cambios Realizados

Todos los cambios implementados responden estrictamente a las definiciones acordadas con la cátedra (reunión del 13 de octubre de 2026):

### A. Separación de Nodos en el AST para Declaración y Uso
- **Problema previo:** El AST utilizaba nodos genéricos sin una clara distinción en su jerarquía sobre cuándo una variable se declara respecto de cuándo se utiliza en una expresión.
- **Cambio realizado:**
  1. [`VarDeclNode.java`](file:///c:/Users/Tomas/Desktop/JFLEX--ANALIZADOR-LEXICO--main/src/main/java/ar/edu/unvm/compiladores/ast/VarDeclNode.java): Representa exclusivamente la **declaración** de variables (`int x;`). Se le incorporó la referencia `private Symbol symbol;` con métodos `getSymbol()` y `setSymbol(Symbol)`.
  2. [`VarUseNode.java`](file:///c:/Users/Tomas/Desktop/JFLEX--ANALIZADOR-LEXICO--main/src/main/java/ar/edu/unvm/compiladores/ast/VarUseNode.java): Nueva clase que extiende a `VariableRefNode` para representar de forma explícita el **uso** de una variable dentro de una expresión aritmética o lógica.
  3. [`VariableRefNode.java`](file:///c:/Users/Tomas/Desktop/JFLEX--ANALIZADOR-LEXICO--main/src/main/java/ar/edu/unvm/compiladores/ast/VariableRefNode.java): Se ajustó el visualizador (`printTree`) para mostrar la etiqueta `[Uso] nombre -> Symbol(...)`.
  4. [`Parser.cup`](file:///c:/Users/Tomas/Desktop/JFLEX--ANALIZADOR-LEXICO--main/src/main/cup/ar/edu/unvm/compiladores/parser/Parser.cup): Se modificó la regla gramatical de variable en expresión (`ID:nombre`) para instanciar directamente un `VarUseNode`.
  5. **Vinculación bidireccional:** Tanto los nodos de declaración como los de uso quedan asociados en memoria al mismo objeto `Symbol` en la tabla de símbolos.

### B. Control Estricto de Tres Errores Semánticos de Compilación
- **Problema previo:** Los errores semánticos no estaban agrupados conforme a los 3 tipos específicos acordados en clase.
- **Cambio realizado en [`SemanticAnalyzer.java`](file:///c:/Users/Tomas/Desktop/JFLEX--ANALIZADOR-LEXICO--main/src/main/java/ar/edu/unvm/compiladores/semantic/SemanticAnalyzer.java):**
  Se formalizó la clase `CompilationError` con el enum `ErrorType`:
  1. **`VARIABLE_REDECLARADA`**: Detecta si una variable con el mismo nombre ya fue declarada previamente en el mismo ámbito.
     *Ejemplo de salida:* `[VARIABLE REDECLARADA] (linea 4): la variable 'x' ya fue declarada previamente.`
  2. **`VARIABLE_NO_DECLARADA`**: Detecta el uso de variables no declaradas, tanto en sentencias de asignación como en referencias dentro de expresiones.
     *Ejemplo de salida:* `[VARIABLE NO DECLARADA] (linea 4): la variable 'y' no esta declarada.`
  3. **`ERROR_DE_TIPO`**: Verifica que los tipos coincidan en asignaciones (ej. asignar `bool` a `int` o viceversa), que los operadores aritméticos (`+`, `-`, `*`, `/`) solo operen con enteros, y que el tipo de retorno coincida con la firma de `main`.
     *Ejemplo de salida:* `[ERROR DE TIPO] (linea 4): tipo incompatible en asignacion a 'x'. Se esperaba int pero se obtuvo bool.`

### C. Implementación Completa de Código de Tres Direcciones (C3D)
Se creó el paquete [`ar.edu.unvm.compiladores.c3d`](file:///c:/Users/Tomas/Desktop/JFLEX--ANALIZADOR-LEXICO--main/src/main/java/ar/edu/unvm/compiladores/c3d) con los siguientes componentes:
1. **[`Operador3D.java`](file:///c:/Users/Tomas/Desktop/JFLEX--ANALIZADOR-LEXICO--main/src/main/java/ar/edu/unvm/compiladores/c3d/Operador3D.java):**
   Enum con los operadores: `SUMA` (+, ADD), `RESTA` (-, SUB), `MULT` (*, MUL), `DIV` (/, DIV), `ASIGNACION` (=, ASSIGN) y `RETORNO` (return, RET).
2. **[`Instruccion3D.java`](file:///c:/Users/Tomas/Desktop/JFLEX--ANALIZADOR-LEXICO--main/src/main/java/ar/edu/unvm/compiladores/c3d/Instruccion3D.java):**
   Clase cuádrupla que almacena `operador`, `operando1`, `operando2` y `resultado` como referencias directas a objetos [`Symbol`](file:///c:/Users/Tomas/Desktop/JFLEX--ANALIZADOR-LEXICO--main/src/main/java/ar/edu/unvm/compiladores/symbols/Symbol.java), conservando la información semántica completa (tipo, valor, nombre).
   - Genera formato estándar C3D: `T1 = a + b`, `x = T1`, `return x`.
   - Genera formato cuádruplas: `(ADD, a, b, T1)`.
3. **[`CodigoTresDirecciones.java`](file:///c:/Users/Tomas/Desktop/JFLEX--ANALIZADOR-LEXICO--main/src/main/java/ar/edu/unvm/compiladores/c3d/CodigoTresDirecciones.java):**
   Clase que almacena el vector dinámico de instrucciones (`Vector<Instruccion3D>`), ofreciendo métodos para insertar instrucciones, imprimirlas numeradas y generar una tabla formateada de cuádruplas.
4. **[`GeneradorC3D.java`](file:///c:/Users/Tomas/Desktop/JFLEX--ANALIZADOR-LEXICO--main/src/main/java/ar/edu/unvm/compiladores/c3d/GeneradorC3D.java):**
   Recorre el AST en post-orden (**de abajo hacia arriba / hojas a la raíz**) para resolver primero las expresiones anidadas mediante variables temporales sucesivas (`T1, T2, T3, ...`) antes de emitir la asignación final o el retorno.
5. **[`InterpreteC3D.java`](file:///c:/Users/Tomas/Desktop/JFLEX--ANALIZADOR-LEXICO--main/src/main/java/ar/edu/unvm/compiladores/c3d/InterpreteC3D.java):**
   Intérprete que recorre secuencialmente el vector de instrucciones de tres direcciones para ejecutar los cálculos y verificar el flujo del programa.

### D. Ampliación de la Clase [`Symbol.java`](file:///c:/Users/Tomas/Desktop/JFLEX--ANALIZADOR-LEXICO--main/src/main/java/ar/edu/unvm/compiladores/symbols/Symbol.java)
- Incorporación de `Kind` (`VARIABLE`, `TEMPORAL`, `CONSTANTE`).
- Métodos de fábrica estáticos: `nuevoTemporal(nombre, tipo)` y `nuevaConstante(valor, tipo)`.
- Método `getDisplayName()` para mostrar nombres de variables o temporales (`x`, `T1`) o valores literales (`4`, `true`).

### E. Actualización del Flujo Principal en [`Main.java`](file:///c:/Users/Tomas/Desktop/JFLEX--ANALIZADOR-LEXICO--main/src/main/java/ar/edu/unvm/compiladores/Main.java)
- Ejecuta primero el análisis semántico para que el árbol sintáctico se imprima con los punteros a la tabla de símbolos ya resueltos.
- Genera el código C3D, muestra las instrucciones numeradas, la tabla de cuádruplas y el resultado de la ejecución con `InterpreteC3D`.
- Exporta automáticamente el archivo `.c3d` con el mismo nombre base que el código fuente.
- Soporta la nueva opción por línea de comandos `--c3d` (o `--3ac`).

---

## 4. Guía Paso a Paso para Compilar y Ejecutar

Abre tu terminal en la carpeta del proyecto (`C:\Users\Tomas\Desktop\JFLEX--ANALIZADOR-LEXICO--main`) y sigue estos pasos:

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

*Debe responder:*
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

### Paso 2: Ejecutar Pruebas de Código de Tres Direcciones (C3D)

#### Prueba A: Operaciones Aritméticas Compuestas y Temporales Sucesivos
Este caso de prueba resuelve: `z = x + y * 2 + (10 - x) / 2;`
- **Comando:**
  ```powershell
  .\ejecutar.ps1 src\prueba_c3d_aritmetica.txt
  ```
- **Comando para ver exclusivamente C3D y Cuádruplas:**
  ```powershell
  .\ejecutar.ps1 src\prueba_c3d_aritmetica.txt --c3d
  ```
- **Salida esperada:**
  ```
  --- Codigo de Tres Direcciones (C3D) ---
  === CODIGO DE TRES DIRECCIONES (C3D) ===
  [01] x = 4
  [02] y = 3
  [03] T1 = y * 2
  [04] T2 = x + T1
  [05] T3 = 10 - x
  [06] T4 = T3 / 2
  [07] T5 = T2 + T4
  [08] z = T5
  [09] return z

  --- Cuadruplas (C3D) ---
  === CUADRUPLAS (C3D) ===
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

  Resultado de ejecucion C3D (Return): 13
  ```
*(Además, genera automáticamente el archivo `src/prueba_c3d_aritmetica.c3d`).*

#### Prueba B: Tipos y Constantes Booleanas
- **Comando:**
  ```powershell
  .\ejecutar.ps1 src\prueba_c3d_bool.txt --c3d
  ```

---

### Paso 3: Validar los Tres Errores Semánticos de Compilación

#### Error 1: Variable Redeclarada
El archivo `src/prueba_error_redeclaracion.txt` declara la variable `x` dos veces.
- **Comando:**
  ```powershell
  .\ejecutar.ps1 src\prueba_error_redeclaracion.txt
  ```
- **Salida esperada:**
  ```
  --- Errores Semanticos de Compilacion ---
  [VARIABLE REDECLARADA] (linea 4): la variable 'x' ya fue declarada previamente.
  ```

#### Error 2: Variable No Declarada (Inexistente)
El archivo `src/prueba_error_no_declarada.txt` asigna y utiliza `y` y `z` sin haberlas declarado.
- **Comando:**
  ```powershell
  .\ejecutar.ps1 src\prueba_error_no_declarada.txt
  ```
- **Salida esperada:**
  ```
  --- Errores Semanticos de Compilacion ---
  [VARIABLE NO DECLARADA] (linea 4): la variable 'y' no esta declarada.
  [VARIABLE NO DECLARADA] (linea 5): la variable 'z' no esta declarada.
  ```

#### Error 3: Incompatibilidad de Tipos
- **Asignación de tipos incompatibles (`int = bool` y `bool = int`):**
  ```powershell
  .\ejecutar.ps1 src\prueba_error_tipo_asignacion.txt
  ```
  *Salida esperada:*
  ```
  --- Errores Semanticos de Compilacion ---
  [ERROR DE TIPO] (linea 4): tipo incompatible en asignacion a 'x'. Se esperaba int pero se obtuvo bool.
  [ERROR DE TIPO] (linea 5): tipo incompatible en asignacion a 'b'. Se esperaba bool pero se obtuvo int.
  ```

- **Operación aritmética sobre booleanos (`b + 10`):**
  ```powershell
  .\ejecutar.ps1 src\prueba_error_tipo_operacion.txt
  ```
  *Salida esperada:*
  ```
  --- Errores Semanticos de Compilacion ---
  [ERROR DE TIPO] (linea 5): el operador aritmetico '+' requiere enteros pero el lado izquierdo es bool.
  ```

---

### Paso 4: Filtrado de Visualización por Etapas

Puedes pasar banderas a `ejecutar.ps1` o `ejecutar.bat` para inspeccionar únicamente la etapa deseada:

| Flag | Etapa que se visualiza |
| :--- | :--- |
| *(sin flag)* | Todo el flujo: AST, Tabla de Símbolos, Intérprete AST, C3D, Cuádruplas, Intérprete C3D y Seudo-Assembly |
| `--c3d` | Código de Tres Direcciones, Cuádruplas y ejecución C3D |
| `--ast` | Árbol Sintáctico Abstracto con marcas `[Declaracion]`, `[Uso]` y enlaces a `Symbol` |
| `--semantic` | Validación de errores semánticos y Tabla de Símbolos |
| `--eval` | Ejecución paso a paso mediante el Intérprete del AST |
| `--asm` | Código Seudo-Assembly emitido (`.asm`) |

---

### Paso 5: Probar Individualmente el Analizador Léxico (JFlex)
Para ver la secuencia de tokens emitidos por el lexer sin pasar por el parser:
```powershell
.\test_lexer.ps1 src\programa_prueba.txt
```
