# Compilador JFlex + CUP - UNVM
**Universidad Nacional de Villa Mercedes (UNVM)**  
**Escuela de Ingeniería y Ciencias Ambientales - Compiladores**  
**Actualizado según reunión de cátedra (13 oct 2026)**

Este proyecto implementa un compilador modular con frontend y backend completos:
- Analizador Léxico (JFlex)
- Analizador Sintáctico (CUP)
- Construcción de AST con jerarquía separada de nodos de **Declaración** (`VarDeclNode`) y **Uso** (`VarUseNode` / `VariableRefNode`)
- Tabla de Símbolos (`SymbolTable`) con objetos `Symbol` enlazados bidireccionalmente desde el AST
- Analizador Semántico con control de **tres errores de compilación principales**:
  1. Variables redeclaradas (`[VARIABLE REDECLARADA]`)
  2. Variables no declaradas (`[VARIABLE NO DECLARADA]`)
  3. Errores de tipo (`[ERROR DE TIPO]`)
- **Generación de Código de Tres Direcciones (C3D)**:
  - Estructura basada en un `Vector<Instruccion3D>` dinámico
  - Clase `Instruccion3D` con operador y referencias a `Symbol`
  - Generador (`GeneradorC3D`) que recorre el AST en post-orden (bottom-up: hojas a raíz) utilizando variables temporales sucesivas (`T1, T2, ...`)
  - Evaluador/Intérprete secuencial de C3D (`InterpreteC3D`)
  - Visualización y exportación de Cuádruplas `(OP, ARG1, ARG2, RES)` y archivo `.c3d`
- Intérprete evaluador directo del AST (`Interpreter`)
- Generador de Seudo-Assembly (`PseudoAssemblyGenerator`, archivo `.asm`)

---

## Documentación Detallada
Consultar el informe completo con la arquitectura, decisiones de diseño tomadas en clase y ejemplos:
📄 **[INFORME_PREPROYECTO.md](INFORME_PREPROYECTO.md)**

---

## Comandos Rápidos de Uso

### 1. Compilar todo el proyecto:
```powershell
.\compilar.ps1
```
*(o en CMD: `compilar.bat`)*

---

### 2. Ejecutar programas con Código de Tres Direcciones (C3D):
```powershell
# Operaciones aritméticas compuestas con temporales sucesivos
.\ejecutar.ps1 src\prueba_c3d_aritmetica.txt

# Asignaciones y expresiones booleanas
.\ejecutar.ps1 src\prueba_c3d_bool.txt

# Programa con pipeline completo
.\ejecutar.ps1 src\programa_prueba.txt
```

---

### 3. Validar los 3 errores semánticos principales:
```powershell
# 1. Error de variable redeclarada:
.\ejecutar.ps1 src\prueba_error_redeclaracion.txt

# 2. Error de variable no declarada / inexistente:
.\ejecutar.ps1 src\prueba_error_no_declarada.txt

# 3. Errores de tipo incompatible:
.\ejecutar.ps1 src\prueba_error_tipo_asignacion.txt
.\ejecutar.ps1 src\prueba_error_tipo_operacion.txt
```

---

### 4. Filtrar visualización por etapas:
- **Ver únicamente C3D y Cuádruplas**:
  ```powershell
  .\ejecutar.ps1 src\prueba_c3d_aritmetica.txt --c3d
  ```
- **Ver únicamente el AST**:
  ```powershell
  .\ejecutar.ps1 src\prueba_c3d_aritmetica.txt --ast
  ```
- **Ver únicamente la Tabla de Símbolos y Validación Semántica**:
  ```powershell
  .\ejecutar.ps1 src\prueba_c3d_aritmetica.txt --semantic
  ```
- **Ver únicamente la Ejecución del Intérprete**:
  ```powershell
  .\ejecutar.ps1 src\prueba_c3d_aritmetica.txt --eval
  ```
- **Ver únicamente el Seudo-Assembly**:
  ```powershell
  .\ejecutar.ps1 src\prueba_c3d_aritmetica.txt --asm
  ```

---

### 5. Probar únicamente el analizador léxico (tokens):
```powershell
.\test_lexer.ps1 src\programa_prueba.txt
```
