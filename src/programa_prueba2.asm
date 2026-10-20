.DATA
    x:         .INT   0
    y:         .INT   0

.CODE
main:
    LOAD_IMM    R0, 1
    STORE       x, R0
    LOAD_IMM    R1, 1
    STORE       y, R1
    LOAD        R2, x
    LOAD_IMM    R3, 3
    LOAD_IMM    R4, 2
    MUL         R5, R3, R4
    LOAD        R6, y
    MUL         R7, R5, R6
    ADD         R8, R2, R7
    STORE       x, R8
    HALT
