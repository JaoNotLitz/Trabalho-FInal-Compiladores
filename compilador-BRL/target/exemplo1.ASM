; Programa: programa1
; Gerado pelo compilador BRL

dseg SEGMENT PUBLIC
    byte 4000h DUP(?)  ; area temporaria
    n SWORD ?
    resultado SWORD ?
    i SWORD ?
    continuar byte ?
dseg ENDS

    msg_verdadeiro byte "verdadeiro", '$'
    msg_falso byte "falso", '$'

cseg SEGMENT PUBLIC
    ASSUME CS:cseg, DS:dseg

strt:
    mov ax, dseg
    mov ds, ax

    ; Leitura de inteiro para n
    lea dx, DS:n
    mov ah, 0Ah   ; leitura buffered
    int 21h
    ; Converter ASCII para inteiro e armazenar
    mov ax, 1
    push ax
    pop ax
    mov DS:resultado, ax
    mov ax, 1
    push ax
    pop ax
    mov DS:i, ax
    mov ax, 0FFh  ; verdadeiro
    push ax
    pop ax
    mov DS:continuar, al
R0:
    mov al, DS:continuar
    cbw
    push ax
    pop ax
    cmp ax, 0
    je R1
    mov ax, DS:resultado
    push ax
    mov ax, DS:i
    push ax
    pop bx          ; operando direito
    pop ax          ; operando esquerdo
    imul bx
    push ax
    pop ax
    mov DS:resultado, ax
    mov ax, DS:i
    push ax
    mov ax, 1
    push ax
    pop bx          ; operando direito
    pop ax          ; operando esquerdo
    add ax, bx
    push ax
    pop ax
    mov DS:i, ax
    mov ax, DS:i
    push ax
    mov ax, DS:n
    push ax
    pop bx          ; operando direito
    pop ax          ; operando esquerdo
    cmp ax, bx
    jg R4
    mov ax, 0       ; falso
    jmp R5
R4:
    mov ax, 0FFh    ; verdadeiro
R5:
    push ax
    pop ax
    cmp ax, 0
    je R2
    mov ax, 0     ; falso
    push ax
    pop ax
    mov DS:continuar, al
R2:
    jmp R0
R1:
    mov ax, DS:resultado
    push ax
    ; Escrita de inteiro
    pop ax
    ; Converter inteiro para ASCII e imprimir
    call write_int

    ; Finalizar programa
    mov ah, 4Ch
    int 21h

cseg ENDS
END strt
