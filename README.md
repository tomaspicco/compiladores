# Compilador JFlex + CUP - UNVM
**Universidad Nacional de Villa Mercedes (UNVM)**  
**Escuela de Ingeniería y Ciencias Ambientales - Compiladores**  

Este proyecto implementa un compilador modular con frontend y backend completos:
- **Paso 1 / Punto 3: Analizador Léxico (JFlex)**: Reconocimiento de tokens, palabras reservadas, identificadores y literales enteros y booleanos.
- **Paso 2 / Puntos 4 y 5: Analizador Sintáctico (CUP) y AST**: Gramática extendida con tipos (`int`, `bool`, `void`), asignaciones, expresiones aritméticas y retornos. Construcción del Árbol Sintáctico con distinción entre `VarDeclNode` y `VarUseNode`.
- **Paso 3: Análisis Semántico y Tabla de Símbolos (`SymbolTable`)**: Control de variables no declaradas, redeclaraciones y discrepancias de tipos.
- **Paso 4 / Punto 6: Intérprete del AST (`Interpreter`)**: Evaluación paso a paso directamente sobre el árbol sintáctico.
- **Paso 5: Código de Tres Direcciones (C3D)**: Vector dinámico de cuádruplas `(OP, ARG1, ARG2, RES)` generado en post-orden con temporales `T1..Tn` y evaluador `InterpreteC3D`.
- **Paso 6 / Punto 7: Generador de Assembly GNU x86-64 (`GnuAssemblyGenerator`)**: Traducción a ensamblador real de 64 bits (sintaxis AT&T) instrucción por instrucción a partir de C3D, marco de pila con `enter $(8 * N), $0`, registros temporales `%r10` y `%r11`, y retorno en `%rax` (`.s`).

---

## Ejecución Paso a Paso (Recomendado para Defensa / Cátedra)

Puedes ejecutar el menú interactivo para seleccionar cualquier etapa con una tecla:
```powershell
.\menu.ps1
```
*(o en CMD: `menu.bat`)*

O ejecutar directamente los scripts individuales para cada etapa:
```powershell
# Paso 1: Ver tokens del Lexer (JFlex)
.\paso1_lexer.ps1

# Paso 2: Ver el Árbol Sintáctico (Parser CUP -> AST)
.\paso2_parser_ast.ps1

# Paso 3: Ver el Análisis Semántico y la Tabla de Símbolos
.\paso3_semantica.ps1

# Paso 4: Ver la Ejecución del Intérprete sobre el AST
.\paso4_interprete.ps1

# Paso 5: Ver el Código de Tres Direcciones (C3D) y Cuádruplas
.\paso5_c3d.ps1

# Paso 6: Ver el Assembly GNU x86-64 generado instrucción por instrucción
.\paso6_assembly.ps1
```

*(En todos los scripts podés pasar otro archivo de prueba como parámetro, por ejemplo: `.\paso3_semantica.ps1 src\prueba_error_redeclaracion.txt`)*

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
.\ejecutar.ps1 src\programa_prueba.txt
```

---

### 3. Validar Errores Semánticos:
```powershell
# Variable redeclarada
.\paso3_semantica.ps1 src\prueba_error_redeclaracion.txt

# Variable no declarada
.\paso3_semantica.ps1 src\prueba_error_no_declarada.txt

# Incompatibilidad de tipos
.\paso3_semantica.ps1 src\prueba_error_tipo_asignacion.txt
.\paso3_semantica.ps1 src\prueba_error_tipo_operacion.txt
```

---

## Documentación y Presentación
- 📄 **[Presentacion_Proyecto_Compiladores_UNVM.pdf](Presentacion_Proyecto_Compiladores_UNVM.pdf)**: Presentación de 10 diapositivas para defender el proyecto.
- 📄 **[INFORME_PREPROYECTO.md](INFORME_PREPROYECTO.md)**: Informe técnico formal de arquitectura y especificación.
- 📄 **[GUIA_Y_CAMBIOS_PROYECTO.md](GUIA_Y_CAMBIOS_PROYECTO.md)**: Registro histórico de modificaciones y decisiones de diseño.
