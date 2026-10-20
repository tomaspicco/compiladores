.DATA
    x:         .INT   0
    y:         .INT   0

.CODE
main:
    LOAD_IMM    R0, 1
    STORE       x, R0
    LOAD        R1, x
    LOAD_IMM    R2, 3
    LOAD_IMM    R3, 2
    MUL         R4, R2, R3
    LOAD        R5, y
    MUL         R6, R4, R5
    ADD         R7, R1, R6
    STORE       x, R7
    LOAD        R8, x
    RET         R8
    LOAD_IMM    R9, 4
    STORE       y, R9
    LOAD_IMM    R10, 2
    STORE       x, R10
    LOAD        R11, y
    LOAD_IMM    R12, 2
    MUL         R13, R11, R12
    RET         R13
    HALT
