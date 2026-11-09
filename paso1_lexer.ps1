param([string]$Archivo = "src\programa_prueba.txt")
java -cp "out;lib/java-cup-11b-runtime.jar" ar.edu.unvm.compiladores.Main $Archivo --lexer
