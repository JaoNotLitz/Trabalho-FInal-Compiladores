; Programa: programa3
; Gerado pelo compilador BRL

dseg SEGMENT PUBLIC
    byte 4000h DUP(?)  ; area temporaria
    msg1 byte 512 DUP(?)  ; caractere (512 bytes)
    msg2 byte 512 DUP(?)  ; caractere (512 bytes)
    resultado_concat byte 512 DUP(?)  ; caractere (512 bytes)
    x SWORD ?
    y SWORD ?
    cond byte ?
dseg ENDS

    ; Strings literais
    str0 byte "Ola ", '$'
    str1 byte "Mundo", '$'
    str2 byte "x eh menor que y", '$'
    msg_verdadeiro byte "verdadeiro", '$'
    msg_falso byte "falso", '$'

cseg SEGMENT PUBLIC
    ASSUME CS:cseg, DS:dseg

strt:
    mov ax, dseg
    mov ds, ax

    lea dx, DS:str0
    push dx
    pop si         ; endereco fonte
    lea di, DS:msg1
    ; copiar string
R0:
    mov al, [si]
    mov [di], al
    cmp al, '$'
    je R1
    inc si
    inc di
    jmp R0
R1:
    lea dx, DS:str1
    push dx
    pop si         ; endereco fonte
    lea di, DS:msg2
    ; copiar string
R2:
    mov al, [si]
    mov [di], al
    cmp al, '$'
    je R3
    inc si
    inc di
    jmp R2
R3:
    lea dx, DS:msg1
    push dx
    lea dx, DS:msg2
    push dx
    pop bx          ; operando direito
    pop ax          ; operando esquerdo
    add ax, bx
    push ax
    pop si         ; endereco fonte
    lea di, DS:resultado_concat
    ; copiar string
R4:
    mov al, [si]
    mov [di], al
    cmp al, '$'
    je R5
    inc si
    inc di
    jmp R4
R5:
    lea dx, DS:resultado_concat
    push dx
    ; Escrita de string
    pop dx
    mov ah, 09h   ; DOS print string
    int 21h
    mov ax, 10
    push ax
    pop ax
    mov DS:x, ax
    mov ax, 20
    push ax
    pop ax
    mov DS:y, ax
    mov ax, DS:x
    push ax
    mov ax, DS:y
    push ax
    pop bx          ; operando direito
    pop ax          ; operando esquerdo
    cmp ax, bx
    jl R6
    mov ax, 0       ; falso
    jmp R7
R6:
    mov ax, 0FFh    ; verdadeiro
R7:
    push ax
    mov ax, 0     ; falso
    push ax
    pop ax
    not ax          ; negacao logica
    push ax
    pop bx          ; operando direito
    pop ax          ; operando esquerdo
    and ax, bx   ; E logico
    push ax
    pop ax
    mov DS:cond, al
    mov al, DS:cond
    cbw
    push ax
    pop ax
    cmp ax, 0
    je R8
    lea dx, DS:str2
    push dx
    ; Escrita de string
    pop dx
    mov ah, 09h   ; DOS print string
    int 21h
R8:
    mov ax, DS:x
    push ax
    mov ax, 3
    push ax
    pop bx          ; operando direito
    pop ax          ; operando esquerdo
    cwd
    idiv bx      ; quociente em ax
    push ax
    ; Escrita de inteiro
    pop ax
    ; Converter inteiro para ASCII e imprimir
    call write_int
    mov ax, DS:y
    push ax
    mov ax, 7
    push ax
    pop bx          ; operando direito
    pop ax          ; operando esquerdo
    cwd
    idiv bx      ; resto em dx
    mov ax, dx
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
