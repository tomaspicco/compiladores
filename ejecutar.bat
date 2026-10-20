@echo off
if "%~1"=="" (
    echo Uso: ejecutar.bat ^<ruta_archivo_prueba^> [opciones]
    echo Ejemplo: ejecutar.bat src\programa_prueba.txt
    echo Opciones: --all, --ast, --semantic, --eval, --c3d, --asm
    exit /b 1
)

java -cp "out;lib\java-cup-11b-runtime.jar" ar.edu.unvm.compiladores.Main %*
