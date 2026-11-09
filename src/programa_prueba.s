.globl main
main:
    # Prologo: armado del marco de pila (stack frame)
    pushq %rbp
    movq %rsp, %rbp
    subq $48, %rsp

    # Distribucion de variables y temporales en la pila:
    #   x          -> -8(%rbp)
    #   y          -> -16(%rbp)
    #   T1         -> -24(%rbp)
    #   T2         -> -32(%rbp)
    #   T3         -> -40(%rbp)
    #   T4         -> -48(%rbp)

    # x = 1
    movq $1, %r10
    movq %r10, -8(%rbp)

    # T1 = 3 * 2
    movq $3, %r10
    movq $2, %r11
    imulq %r11, %r10
    movq %r10, -24(%rbp)

    # T2 = T1 * y
    movq -24(%rbp), %r10
    movq -16(%rbp), %r11
    imulq %r11, %r10
    movq %r10, -32(%rbp)

    # T3 = x + T2
    movq -8(%rbp), %r10
    movq -32(%rbp), %r11
    addq %r11, %r10
    movq %r10, -40(%rbp)

    # x = T3
    movq -40(%rbp), %r10
    movq %r10, -8(%rbp)

    # return x
    movq -8(%rbp), %rax
    movq %rbp, %rsp
    popq %rbp
    ret

    # y = 4
    movq $4, %r10
    movq %r10, -16(%rbp)

    # x = 2
    movq $2, %r10
    movq %r10, -8(%rbp)

    # T4 = y * 2
    movq -16(%rbp), %r10
    movq $2, %r11
    imulq %r11, %r10
    movq %r10, -48(%rbp)

    # return T4
    movq -48(%rbp), %rax
    movq %rbp, %rsp
    popq %rbp
    ret

    # Fin de funcion por defecto
    movq $0, %rax
    movq %rbp, %rsp
    popq %rbp
    ret
