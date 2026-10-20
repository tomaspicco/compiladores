@echo off
if "%~1"=="" (
    echo Uso: test_lexer.bat ^<ruta_archivo_prueba^>
    echo Ejemplo: test_lexer.bat src\programa_prueba.txt
    exit /b 1
)

java -cp "out;lib\java-cup-11b-runtime.jar" ar.edu.unvm.compiladores.TestLexer %*
