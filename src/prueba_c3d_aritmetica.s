.globl main
main:
    # Prologo: armado del marco de pila (stack frame)
    pushq %rbp
    movq %rsp, %rbp
    subq $64, %rsp

    # Distribucion de variables y temporales en la pila:
    #   x          -> -8(%rbp)
    #   y          -> -16(%rbp)
    #   z          -> -24(%rbp)
    #   T1         -> -32(%rbp)
    #   T2         -> -40(%rbp)
    #   T3         -> -48(%rbp)
    #   T4         -> -56(%rbp)
    #   T5         -> -64(%rbp)

    # x = 4
    movq $4, %r10
    movq %r10, -8(%rbp)

    # y = 3
    movq $3, %r10
    movq %r10, -16(%rbp)

    # T1 = y * 2
    movq -16(%rbp), %r10
    movq $2, %r11
    imulq %r11, %r10
    movq %r10, -32(%rbp)

    # T2 = x + T1
    movq -8(%rbp), %r10
    movq -32(%rbp), %r11
    addq %r11, %r10
    movq %r10, -40(%rbp)

    # T3 = 10 - x
    movq $10, %r10
    movq -8(%rbp), %r11
    subq %r11, %r10
    movq %r10, -48(%rbp)

    # T4 = T3 / 2
    movq -48(%rbp), %r10
    movq $2, %r11
    movq %r10, %rax
    cqo
    idivq %r11
    movq %rax, %r10
    movq %r10, -56(%rbp)

    # T5 = T2 + T4
    movq -40(%rbp), %r10
    movq -56(%rbp), %r11
    addq %r11, %r10
    movq %r10, -64(%rbp)

    # z = T5
    movq -64(%rbp), %r10
    movq %r10, -24(%rbp)

    # return z
    movq -24(%rbp), %rax
    movq %rbp, %rsp
    popq %rbp
    ret

    # Fin de funcion por defecto
    movq $0, %rax
    movq %rbp, %rsp
    popq %rbp
    ret
