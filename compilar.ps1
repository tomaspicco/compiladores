Write-Host "Generando lexer..."
java -jar lib/jflex-1.9.1/lib/jflex-full-1.9.1.jar -d src/main/java/ar/edu/unvm/compiladores/lexer src/main/jflex/ar/edu/unvm/compiladores/lexer/Lexer.flex | Out-Null
if ($LASTEXITCODE -ne 0) { Write-Host "Error en lexer"; exit $LASTEXITCODE }

Write-Host "Generando parser..."
java -jar lib/java-cup-11b-2015.03.26.jar -destdir src/main/java/ar/edu/unvm/compiladores/parser -parser Parser -symbols sym src/main/cup/ar/edu/unvm/compiladores/parser/Parser.cup | Out-Null
if ($LASTEXITCODE -ne 0) { Write-Host "Error en parser"; exit $LASTEXITCODE }

Write-Host "Compilando Java..."
if (!(Test-Path out)) { New-Item -ItemType Directory -Path out | Out-Null }
$sources = Get-ChildItem -Path src/main/java -Filter "*.java" -Recurse | Select-Object -ExpandProperty FullName
javac -cp "lib/java-cup-11b-runtime.jar;src/main/java" -d out $sources
if ($LASTEXITCODE -ne 0) { Write-Host "Error en Java"; exit $LASTEXITCODE }

Write-Host "Compilacion terminada."
