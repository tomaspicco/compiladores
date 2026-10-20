.DATA
    a:         .BOOL  0
    b:         .BOOL  0

.CODE
main:
    LOAD_IMM    R0, 1
    STORE       a, R0
    LOAD_IMM    R1, 0
    STORE       b, R1
    LOAD        R2, b
    STORE       a, R2
    LOAD        R3, a
    RET         R3
    HALT
