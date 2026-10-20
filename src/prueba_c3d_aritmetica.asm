.DATA
    x:         .INT   0
    y:         .INT   0
    z:         .INT   0

.CODE
main:
    LOAD_IMM    R0, 4
    STORE       x, R0
    LOAD_IMM    R1, 3
    STORE       y, R1
    LOAD        R2, x
    LOAD        R3, y
    LOAD_IMM    R4, 2
    MUL         R5, R3, R4
    ADD         R6, R2, R5
    LOAD_IMM    R7, 10
    LOAD        R8, x
    SUB         R9, R7, R8
    LOAD_IMM    R10, 2
    DIV         R11, R9, R10
    ADD         R12, R6, R11
    STORE       z, R12
    LOAD        R13, z
    RET         R13
    HALT
