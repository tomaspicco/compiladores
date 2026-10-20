# Probar analizador lexico
param(
    [Parameter(Mandatory=$false, Position=0)]
    [string]$Archivo
)

if (-not $Archivo) {
    Write-Host "Uso: .\test_lexer.ps1 <ruta_archivo>" -ForegroundColor Yellow
    Write-Host "Ejemplo: .\test_lexer.ps1 src\programa_prueba.txt" -ForegroundColor Yellow
    exit 1
}

java -cp "out;lib/java-cup-11b-runtime.jar" ar.edu.unvm.compiladores.TestLexer $Archivo
