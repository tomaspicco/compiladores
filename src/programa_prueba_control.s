.globl main
main:
    # Prologo: armado del marco de pila (stack frame)
    pushq %rbp
    movq %rsp, %rbp
    subq $64, %rsp

    # Distribucion de variables y temporales en la pila:
    #   i          -> -8(%rbp)
    #   suma       -> -16(%rbp)
    #   mayor      -> -24(%rbp)
    #   T1         -> -32(%rbp)
    #   T2         -> -40(%rbp)
    #   T3         -> -48(%rbp)
    #   T4         -> -56(%rbp)

    # i = 0
    movq $0, %r10
    movq %r10, -8(%rbp)

    # suma = 0
    movq $0, %r10
    movq %r10, -16(%rbp)

    # L1:
.L1:

    # T1 = i < 5
    movq -8(%rbp), %r10
    movq $5, %r11
    cmpq %r11, %r10
    setl %al
    movzbq %al, %r10
    movq %r10, -32(%rbp)

    # ifFalse T1 goto L2
    movq -32(%rbp), %r10
    cmpq $0, %r10
    je .L2

    # T2 = suma + i
    movq -16(%rbp), %r10
    movq -8(%rbp), %r11
    addq %r11, %r10
    movq %r10, -40(%rbp)

    # suma = T2
    movq -40(%rbp), %r10
    movq %r10, -16(%rbp)

    # T3 = i + 1
    movq -8(%rbp), %r10
    movq $1, %r11
    addq %r11, %r10
    movq %r10, -48(%rbp)

    # i = T3
    movq -48(%rbp), %r10
    movq %r10, -8(%rbp)

    # goto L1
    jmp .L1

    # L2:
.L2:

    # T4 = suma >= 10
    movq -16(%rbp), %r10
    movq $10, %r11
    cmpq %r11, %r10
    setge %al
    movzbq %al, %r10
    movq %r10, -56(%rbp)

    # ifFalse T4 goto L3
    movq -56(%rbp), %r10
    cmpq $0, %r10
    je .L3

    # mayor = true
    movq $1, %r10
    movq %r10, -24(%rbp)

    # goto L4
    jmp .L4

    # L3:
.L3:

    # mayor = false
    movq $0, %r10
    movq %r10, -24(%rbp)

    # L4:
.L4:

    # return suma
    movq -16(%rbp), %rax
    movq %rbp, %rsp
    popq %rbp
    ret

    # Fin de funcion por defecto
    movq $0, %rax
    movq %rbp, %rsp
    popq %rbp
    ret
