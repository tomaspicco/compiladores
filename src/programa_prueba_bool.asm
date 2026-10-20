; ===================================================================
; SEUDO-ASSEMBLY GENERADO A PARTIR DEL AST
; Programa: bool main()
; ===================================================================

.DATA
    flag:        .BOOL    0
    aux:         .BOOL    0
    contador:    .INT     0

.CODE
.GLOBAL main
main:

    ; --- Asignacion: flag = <expr> [L:5] ---
    LOAD_IMM    R0, 1    ; true
    STORE       flag, R0

    ; --- Asignacion: aux = <expr> [L:6] ---
    LOAD_IMM    R1, 0    ; false
    STORE       aux, R1

    ; --- Asignacion: contador = <expr> [L:7] ---
    LOAD_IMM    R2, 10
    STORE       contador, R2

    ; --- Retorno con valor [L:8] ---
    LOAD        R3, flag
    RET         R3
    HALT
; ======================= FIN DEL PROGRAMA ===========================
