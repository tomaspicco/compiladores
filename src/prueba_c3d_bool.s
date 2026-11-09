.globl main
main:
    # Prologo: armado del marco de pila (stack frame)
    pushq %rbp
    movq %rsp, %rbp
    subq $16, %rsp

    # Distribucion de variables y temporales en la pila:
    #   a          -> -8(%rbp)
    #   b          -> -16(%rbp)

    # a = true
    movq $1, %r10
    movq %r10, -8(%rbp)

    # b = false
    movq $0, %r10
    movq %r10, -16(%rbp)

    # a = b
    movq -16(%rbp), %r10
    movq %r10, -8(%rbp)

    # return a
    movq -8(%rbp), %rax
    movq %rbp, %rsp
    popq %rbp
    ret

    # Fin de funcion por defecto
    movq $0, %rax
    movq %rbp, %rsp
    popq %rbp
    ret
