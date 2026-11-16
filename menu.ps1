param(
    [string]$Archivo = "src\programa_prueba.txt"
)

while ($true) {
    Clear-Host
    Write-Host "=== Compilador UNViME ==="
    Write-Host "Archivo: $Archivo`n"
    Write-Host "1. Lexer (Tokens)"
    Write-Host "2. Parser y AST"
    Write-Host "3. Semantica y Tabla de Simbolos"
    Write-Host "4. Interprete AST"
    Write-Host "5. Codigo 3 Direcciones (C3D)"
    Write-Host "6. Assembly x86-64"
    Write-Host "7. Ejecutar todo"
    Write-Host "8. Salir`n"

    $opcion = Read-Host "Elegir opcion (1-8)"

    switch ($opcion) {
        "1" {
            Clear-Host
            java -cp "out;lib/java-cup-11b-runtime.jar" ar.edu.unvm.compiladores.Main $Archivo --lexer
            Pause
        }
        "2" {
            Clear-Host
            java -cp "out;lib/java-cup-11b-runtime.jar" ar.edu.unvm.compiladores.Main $Archivo --ast
            Pause
        }
        "3" {
            Clear-Host
            java -cp "out;lib/java-cup-11b-runtime.jar" ar.edu.unvm.compiladores.Main $Archivo --semantic
            Pause
        }
        "4" {
            Clear-Host
            java -cp "out;lib/java-cup-11b-runtime.jar" ar.edu.unvm.compiladores.Main $Archivo --eval
            Pause
        }
        "5" {
            Clear-Host
            java -cp "out;lib/java-cup-11b-runtime.jar" ar.edu.unvm.compiladores.Main $Archivo --c3d
            Pause
        }
        "6" {
            Clear-Host
            java -cp "out;lib/java-cup-11b-runtime.jar" ar.edu.unvm.compiladores.Main $Archivo --gnu
            Pause
        }
        "7" {
            Clear-Host
            java -cp "out;lib/java-cup-11b-runtime.jar" ar.edu.unvm.compiladores.Main $Archivo --all
            Pause
        }
        "8" {
            exit 0
        }
    }
}
