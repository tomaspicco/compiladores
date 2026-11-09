@echo off
set ARCHIVO=%~1
if "%ARCHIVO%"=="" set ARCHIVO=src\programa_prueba.txt
java -cp "out;lib\java-cup-11b-runtime.jar" ar.edu.unvm.compiladores.Main %ARCHIVO% --c3d
