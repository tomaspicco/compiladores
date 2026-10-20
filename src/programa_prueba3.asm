; ===================================================================
; SEUDO-ASSEMBLY GENERADO A PARTIR DEL AST
; Programa: int main()
; ===================================================================

.DATA
    a:           .INT     0
    b:           .INT     0
    c:           .INT     0
    d:           .INT     0
    e:           .INT     0

.CODE
.GLOBAL main
main:

    ; --- Asignacion: a = <expr> [L:8] ---
    LOAD_IMM    R0, 2
    LOAD_IMM    R1, 4
    ADD         R2, R0, R1
    STORE       a, R2

    ; --- Retorno con valor [L:9] ---
    LOAD        R3, a
    RET         R3

    ; --- Asignacion: b = <expr> [L:10] ---
    LOAD_IMM    R4, 5
    LOAD_IMM    R5, 6
    LOAD_IMM    R6, 3
    SUB         R7, R5, R6
    MUL         R8, R4, R7
    LOAD_IMM    R9, 1
    ADD         R10, R8, R9
    STORE       b, R10

    ; --- Retorno con valor [L:11] ---
    LOAD        R11, b
    RET         R11

    ; --- Asignacion: c = <expr> [L:12] ---
    LOAD_IMM    R12, 6
    LOAD_IMM    R13, 3
    DIV         R14, R12, R13
    LOAD_IMM    R15, 5
    MUL         R16, R14, R15
    LOAD_IMM    R17, 20
    ADD         R18, R16, R17
    STORE       c, R18

    ; --- Retorno con valor [L:13] ---
    LOAD        R19, c
    RET         R19

    ; --- Asignacion: d = <expr> [L:14] ---
    LOAD_IMM    R20, 4
    LOAD_IMM    R21, 76
    MUL         R22, R20, R21
    LOAD_IMM    R23, 31
    DIV         R24, R22, R23
    STORE       d, R24

    ; --- Retorno con valor [L:15] ---
    LOAD        R25, d
    RET         R25

    ; --- Asignacion: e = <expr> [L:16] ---
    LOAD_IMM    R26, 1
    LOAD_IMM    R27, 1
    SUB         R28, R26, R27
    LOAD_IMM    R29, 1
    SUB         R30, R28, R29
    STORE       e, R30

    ; --- Retorno con valor [L:17] ---
    LOAD        R31, e
    RET         R31
    HALT
; ======================= FIN DEL PROGRAMA ===========================
