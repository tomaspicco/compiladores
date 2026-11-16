@echo off
set ARCHIVO=src\programa_prueba.txt
if not "%~1"=="" set ARCHIVO=%~1

:MENU
cls
echo === Compilador UNViME ===
echo Archivo: %ARCHIVO%
echo.
echo 1. Lexer (Tokens)
echo 2. Parser y AST
echo 3. Semantica y Tabla de Simbolos
echo 4. Interprete AST
echo 5. Codigo 3 Direcciones (C3D)
echo 6. Assembly x86-64
echo 7. Ejecutar todo
echo 8. Salir
echo.
set /p OPCION="Elegir opcion (1-8): "

if "%OPCION%"=="1" (
    cls
    java -cp "out;lib\java-cup-11b-runtime.jar" ar.edu.unvm.compiladores.Main %ARCHIVO% --lexer
    pause
    goto MENU
)
if "%OPCION%"=="2" (
    cls
    java -cp "out;lib\java-cup-11b-runtime.jar" ar.edu.unvm.compiladores.Main %ARCHIVO% --ast
    pause
    goto MENU
)
if "%OPCION%"=="3" (
    cls
    java -cp "out;lib\java-cup-11b-runtime.jar" ar.edu.unvm.compiladores.Main %ARCHIVO% --semantic
    pause
    goto MENU
)
if "%OPCION%"=="4" (
    cls
    java -cp "out;lib\java-cup-11b-runtime.jar" ar.edu.unvm.compiladores.Main %ARCHIVO% --eval
    pause
    goto MENU
)
if "%OPCION%"=="5" (
    cls
    java -cp "out;lib\java-cup-11b-runtime.jar" ar.edu.unvm.compiladores.Main %ARCHIVO% --c3d
    pause
    goto MENU
)
if "%OPCION%"=="6" (
    cls
    java -cp "out;lib\java-cup-11b-runtime.jar" ar.edu.unvm.compiladores.Main %ARCHIVO% --gnu
    pause
    goto MENU
)
if "%OPCION%"=="7" (
    cls
    java -cp "out;lib\java-cup-11b-runtime.jar" ar.edu.unvm.compiladores.Main %ARCHIVO% --all
    pause
    goto MENU
)
if "%OPCION%"=="8" exit /b 0

goto MENU
