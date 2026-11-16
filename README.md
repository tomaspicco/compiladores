# Compilador JFlex + CUP - UNViME
**Universidad Nacional de Villa Mercedes (UNViME)**  
**Escuela de Ingeniería y Ciencias Ambientales - Compiladores**  

Este proyecto implementa un compilador modular con frontend y backend completos:
- **Paso 1 / Punto 3: Analizador Léxico (JFlex)**: Reconocimiento de tokens, palabras reservadas (`int`, `bool`, `void`, `main`, `return`, `if`, `else`, `while`, `true`, `false`), identificadores, literales y operadores (aritméticos `+`, `-`, `*`, `/` y relacionales `<`, `>`, `<=`, `>=`, `==`, `!=`).
- **Paso 2 / Puntos 4 y 5: Analizador Sintáctico (CUP) y AST**: Gramática extendida con tipos, asignaciones, expresiones aritméticas y de comparación, retornos y estructuras de control (`if`, `if-else`, `while`). Construcción del Árbol Sintáctico con distinción entre nodos de declaración (`VarDeclNode`), uso (`VarUseNode`), condicionales (`IfStmtNode`) y bucles (`WhileStmtNode`).
- **Paso 3: Análisis Semántico y Tabla de Símbolos (`SymbolTable`)**: Control estricto de las 3 categorías de errores: variables no declaradas, redeclaraciones y discrepancias de tipos (incluyendo la validación obligatoria de condición booleana en `if` y `while`).
- **Paso 4 / Punto 6: Intérprete del AST (`Interpreter`)**: Evaluación paso a paso directamente sobre el árbol sintáctico (soporta asignaciones, expresiones, bifurcaciones `if/else` y bucles `while`).
- **Paso 5: Código de Tres Direcciones (C3D)**: Vector dinámico de cuádruplas `(OP, ARG1, ARG2, RES)` generado en post-orden con temporales `T1..Tn`, etiquetas (`LABEL`), saltos condicionales (`IFFALSE`) e incondicionales (`GOTO`), evaluado por `InterpreteC3D`.
- **Paso 6 / Punto 7: Generador de Assembly GNU x86-64 (`GnuAssemblyGenerator`) y GCC**: Traducción a ensamblador real de 64 bits (sintaxis AT&T) instrucción por instrucción a partir de C3D, marco de pila relativo a `%rbp`, registros `%r10` y `%r11`, comparaciones con `cmpq` y `setcc`, saltos locales `.L`, y retorno en `%rax` (`.s`).

---

## Ejecución Paso a Paso (Recomendado para Defensa / Cátedra)

Puedes ejecutar el menú interactivo para seleccionar cualquier etapa con una tecla:
```powershell
.\menu.ps1
```
*(o en CMD: `menu.bat`)*

O ejecutar directamente los scripts individuales para cada etapa pasando cualquier archivo de prueba:
```powershell
# Paso 1: Ver tokens del Lexer (JFlex)
.\paso1_lexer.ps1 src\programa_prueba_control.txt

# Paso 2: Ver el Árbol Sintáctico (Parser CUP -> AST)
.\paso2_parser_ast.ps1 src\programa_prueba_control.txt

# Paso 3: Ver el Análisis Semántico y la Tabla de Símbolos
.\paso3_semantica.ps1 src\programa_prueba_control.txt

# Paso 4: Ver la Ejecución del Intérprete sobre el AST
.\paso4_interprete.ps1 src\programa_prueba_control.txt

# Paso 5: Ver el Código de Tres Direcciones (C3D) y Cuádruplas
.\paso5_c3d.ps1 src\programa_prueba_control.txt

# Paso 6: Ver el Assembly GNU x86-64 generado instrucción por instrucción
.\paso6_assembly.ps1 src\programa_prueba_control.txt
```

---

## Comandos Generales

### 1. Compilar todo el proyecto:
```powershell
.\compilar.ps1
```
*(o en CMD: `compilar.bat`)*

---

### 2. Ejecutar el Pipeline Completo:
```powershell
.\ejecutar.ps1 src\programa_prueba_control.txt
```

---

### 3. Validar Detección de Errores Semánticos:
```powershell
# Variable redeclarada
.\paso3_semantica.ps1 src\prueba_error_redeclaracion.txt

# Variable no declarada
.\paso3_semantica.ps1 src\prueba_error_no_declarada.txt

# Incompatibilidad de tipos en asignaciones y operaciones
.\paso3_semantica.ps1 src\prueba_error_tipo_asignacion.txt
.\paso3_semantica.ps1 src\prueba_error_tipo_operacion.txt

# Incompatibilidad de tipos en condición de if / while (no booleana)
.\paso3_semantica.ps1 src\prueba_error_condicion_tipo.txt
```

---

## Compilación y Ejecución Nativa con GCC

El código generado en el **Paso 6** es código ensamblador real GNU x86-64 en sintaxis AT&T (`.s`), diseñado para ser ensamblado y enlazado directamente por **GCC**.

### Requisito: Instalación de GCC en Windows
Si no tienes GCC instalado, puedes instalar el paquete oficial de MinGW-w64 (WinLibs) mediante `winget`:
```powershell
winget install --id BrechtSanders.WinLibs.POSIX.UCRT
```
*(Reinicia la terminal tras la instalación para actualizar el PATH)*.

Verifica la instalación ejecutando:
```powershell
gcc --version
```

### Comandos para Ensamblar y Ejecutar con GCC:

1. **Generar el archivo ensamblador `.s` con el compilador:**
   ```powershell
   .\paso6_assembly.ps1 src\programa_prueba_control.txt
   ```
   *(Esto genera el archivo `src\programa_prueba_control.s`)*.

2. **Ensamblar y linkear con GCC para producir el binario nativo `.exe`:**
   ```powershell
   gcc src\programa_prueba_control.s -o programa.exe
   ```

3. **Ejecutar el programa y consultar el valor de retorno en `%rax`:**
   ```powershell
   .\programa.exe
   $LASTEXITCODE
   ```
   *(En Linux / bash: `./programa; echo $?`)*.

---

## Documentación
- 📄 **[INFORME_PREPROYECTO.md](INFORME_PREPROYECTO.md)**: Informe técnico formal de arquitectura, gramática y especificación.
- 📄 **[GUIA_Y_CAMBIOS_PROYECTO.md](GUIA_Y_CAMBIOS_PROYECTO.md)**: Registro histórico de modificaciones, decisiones de diseño y pruebas.
