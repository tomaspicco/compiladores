@echo off
echo Generando lexer...
java -jar lib\jflex-1.9.1\lib\jflex-full-1.9.1.jar -d src\main\java\ar\edu\unvm\compiladores\lexer src\main\jflex\ar\edu\unvm\compiladores\lexer\Lexer.flex > nul
if %ERRORLEVEL% NEQ 0 (
    echo Error al generar lexer.
    exit /b %ERRORLEVEL%
)

echo Generando parser...
java -jar lib\java-cup-11b-2015.03.26.jar -destdir src\main\java\ar\edu\unvm\compiladores\parser -parser Parser -symbols sym src\main\cup\ar\edu\unvm\compiladores\parser\Parser.cup > nul
if %ERRORLEVEL% NEQ 0 (
    echo Error al generar parser.
    exit /b %ERRORLEVEL%
)

echo Compilando Java...
if not exist out mkdir out
dir /s /b src\main\java\*.java > sources.tmp
javac -cp "lib\java-cup-11b-runtime.jar;src\main\java" -d out @sources.tmp
set JAVAC_STATUS=%ERRORLEVEL%
del sources.tmp

if %JAVAC_STATUS% NEQ 0 (
    echo Error al compilar Java.
    exit /b %JAVAC_STATUS%
)

echo Compilacion terminada.
