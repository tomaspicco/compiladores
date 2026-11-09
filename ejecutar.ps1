# Ejecutar Main
param(
    [Parameter(Mandatory=$false, Position=0)]
    [string]$Archivo,
    [Parameter(ValueFromRemainingArguments=$true)]
    [string[]]$Opciones
)

if (-not $Archivo) {
    Write-Host "Uso: .\ejecutar.ps1 <ruta_archivo> [opciones]" -ForegroundColor Yellow
    Write-Host "Ejemplo: .\ejecutar.ps1 src\programa_prueba.txt" -ForegroundColor Yellow
    Write-Host "Opciones: --all, --ast, --semantic, --eval, --c3d, --asm, --gnu, --pseudo"
    exit 1
}

java -cp "out;lib/java-cup-11b-runtime.jar" ar.edu.unvm.compiladores.Main $Archivo $Opciones
