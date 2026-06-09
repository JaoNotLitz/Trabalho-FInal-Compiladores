; Programa: teste_completo
; Gerado pelo compilador BRL

dseg SEGMENT PUBLIC
    byte 4000h DUP(?)  ; area temporaria
    a SWORD ?
    b SWORD ?
    c SWORD ?
    x SDWORD ?
    y SDWORD ?
    nome byte 512 DUP(?)  ; caractere (512 bytes)
    sobrenome byte 512 DUP(?)  ; caractere (512 bytes)
    nome_completo byte 512 DUP(?)  ; caractere (512 bytes)
    flag1 byte ?
    flag2 byte ?
    resultado_logico byte ?
    contador SWORD ?
    limite SWORD ?
    pi SDWORD ?
    mensagem byte 512 DUP(?)  ; caractere (512 bytes)
dseg ENDS

    ; Strings literais
    str0 byte "a eh maior que b", '$'
    str1 byte "b eh maior ou igual a a", '$'
    str2 byte "Condicao composta verdadeira", '$'
    str3 byte "Joao", '$'
    str4 byte " Fernandes", '$'
    str5 byte "a menor que b", '$'
    str6 byte "a diferente de b", '$'
    str7 byte "a menor ou igual a b", '$'
    str8 byte "teste", '$'
    str9 byte "teste", '$'
    str10 byte "Strings iguais", '$'
    str11 byte "Fim do teste completo", '$'
    msg_verdadeiro byte "verdadeiro", '$'
    msg_falso byte "falso", '$'

cseg SEGMENT PUBLIC
    ASSUME CS:cseg, DS:dseg

strt:
    mov ax, dseg
    mov ds, ax

    mov ax, 10
    push ax
    pop ax
    mov DS:a, ax
    mov ax, 3
    push ax
    pop ax
    mov DS:b, ax
    mov ax, DS:a
    push ax
    mov ax, DS:b
    push ax
    pop bx          ; operando direito
    pop ax          ; operando esquerdo
    add ax, bx
    push ax
    pop ax
    mov DS:c, ax
    mov ax, DS:c
    push ax
    ; Escrita de inteiro
    pop ax
    ; Converter inteiro para ASCII e imprimir
    call write_int
    mov ax, DS:a
    push ax
    mov ax, DS:b
    push ax
    pop bx          ; operando direito
    pop ax          ; operando esquerdo
    sub ax, bx
    push ax
    pop ax
    mov DS:c, ax
    mov ax, DS:c
    push ax
    ; Escrita de inteiro
    pop ax
    ; Converter inteiro para ASCII e imprimir
    call write_int
    mov ax, DS:a
    push ax
    mov ax, DS:b
    push ax
    pop bx          ; operando direito
    pop ax          ; operando esquerdo
    imul bx
    push ax
    pop ax
    mov DS:c, ax
    mov ax, DS:c
    push ax
    ; Escrita de inteiro
    pop ax
    ; Converter inteiro para ASCII e imprimir
    call write_int
    ; carregar real 3.14
    mov ax, 1078523331
    push ax
    pop ax
    mov DS:pi, ax
    ; carregar real 2.5
    mov ax, 1075838976
    push ax
    pop ax
    mov DS:x, ax
    mov ax, DS:pi
    push ax
    mov ax, DS:x
    push ax
    pop bx          ; operando direito
    pop ax          ; operando esquerdo
    imul bx
    push ax
    pop ax
    mov DS:y, ax
    mov ax, DS:y
    push ax
    ; Escrita de inteiro
    pop ax
    ; Converter inteiro para ASCII e imprimir
    call write_int
    mov ax, DS:x
    push ax
    ; carregar real 1.0
    mov ax, 1065353216
    push ax
    pop bx          ; operando direito
    pop ax          ; operando esquerdo
    add ax, bx
    push ax
    pop ax
    mov DS:y, ax
    mov ax, DS:y
    push ax
    ; Escrita de inteiro
    pop ax
    ; Converter inteiro para ASCII e imprimir
    call write_int
    mov ax, 0FFh  ; verdadeiro
    push ax
    pop ax
    mov DS:flag1, al
    mov ax, 0     ; falso
    push ax
    pop ax
    mov DS:flag2, al
    mov al, DS:flag1
    cbw
    push ax
    mov al, DS:flag2
    cbw
    push ax
    pop bx          ; operando direito
    pop ax          ; operando esquerdo
    and ax, bx   ; E logico
    push ax
    pop ax
    mov DS:resultado_logico, al
    mov al, DS:resultado_logico
    cbw
    push ax
    ; Escrita de logico
    pop ax
    cmp ax, 0
    jne R0
    lea dx, msg_falso
    jmp R1
R0:
    lea dx, msg_verdadeiro
R1:
    mov ah, 09h
    int 21h
    mov al, DS:flag1
    cbw
    push ax
    mov al, DS:flag2
    cbw
    push ax
    pop bx          ; operando direito
    pop ax          ; operando esquerdo
    or ax, bx    ; OU logico
    push ax
    pop ax
    mov DS:resultado_logico, al
    mov al, DS:resultado_logico
    cbw
    push ax
    ; Escrita de logico
    pop ax
    cmp ax, 0
    jne R2
    lea dx, msg_falso
    jmp R3
R2:
    lea dx, msg_verdadeiro
R3:
    mov ah, 09h
    int 21h
    mov al, DS:flag2
    cbw
    push ax
    pop ax
    not ax          ; negacao logica
    push ax
    pop ax
    mov DS:resultado_logico, al
    mov al, DS:resultado_logico
    cbw
    push ax
    ; Escrita de logico
    pop ax
    cmp ax, 0
    jne R4
    lea dx, msg_falso
    jmp R5
R4:
    lea dx, msg_verdadeiro
R5:
    mov ah, 09h
    int 21h
    mov ax, 20
    push ax
    pop ax
    mov DS:a, ax
    mov ax, 15
    push ax
    pop ax
    mov DS:b, ax
    mov ax, DS:a
    push ax
    mov ax, DS:b
    push ax
    pop bx          ; operando direito
    pop ax          ; operando esquerdo
    cmp ax, bx
    jg R8
    mov ax, 0       ; falso
    jmp R9
R8:
    mov ax, 0FFh    ; verdadeiro
R9:
    push ax
    pop ax
    cmp ax, 0
    je R6
    lea dx, DS:str0
    push dx
    ; Escrita de string
    pop dx
    mov ah, 09h   ; DOS print string
    int 21h
    mov ax, DS:a
    push ax
    mov ax, DS:b
    push ax
    pop bx          ; operando direito
    pop ax          ; operando esquerdo
    sub ax, bx
    push ax
    pop ax
    mov DS:c, ax
    mov ax, DS:c
    push ax
    ; Escrita de inteiro
    pop ax
    ; Converter inteiro para ASCII e imprimir
    call write_int
    jmp R7
R6:
    lea dx, DS:str1
    push dx
    ; Escrita de string
    pop dx
    mov ah, 09h   ; DOS print string
    int 21h
    mov ax, DS:b
    push ax
    mov ax, DS:a
    push ax
    pop bx          ; operando direito
    pop ax          ; operando esquerdo
    sub ax, bx
    push ax
    pop ax
    mov DS:c, ax
    mov ax, DS:c
    push ax
    ; Escrita de inteiro
    pop ax
    ; Converter inteiro para ASCII e imprimir
    call write_int
R7:
    mov ax, 0FFh  ; verdadeiro
    push ax
    pop ax
    mov DS:flag1, al
    mov al, DS:flag1
    cbw
    push ax
    mov al, DS:flag2
    cbw
    push ax
    pop ax
    not ax          ; negacao logica
    push ax
    pop bx          ; operando direito
    pop ax          ; operando esquerdo
    and ax, bx   ; E logico
    push ax
    pop ax
    cmp ax, 0
    je R10
    lea dx, DS:str2
    push dx
    ; Escrita de string
    pop dx
    mov ah, 09h   ; DOS print string
    int 21h
R10:
    mov ax, 1
    push ax
    pop ax
    mov DS:contador, ax
    mov ax, 5
    push ax
    pop ax
    mov DS:limite, ax
R12:
    mov ax, DS:contador
    push ax
    mov ax, DS:limite
    push ax
    pop bx          ; operando direito
    pop ax          ; operando esquerdo
    cmp ax, bx
    jle R14
    mov ax, 0       ; falso
    jmp R15
R14:
    mov ax, 0FFh    ; verdadeiro
R15:
    push ax
    pop ax
    cmp ax, 0
    je R13
    mov ax, DS:contador
    push ax
    ; Escrita de inteiro
    pop ax
    ; Converter inteiro para ASCII e imprimir
    call write_int
    mov ax, DS:contador
    push ax
    mov ax, 1
    push ax
    pop bx          ; operando direito
    pop ax          ; operando esquerdo
    add ax, bx
    push ax
    pop ax
    mov DS:contador, ax
    jmp R12
R13:
    lea dx, DS:str3
    push dx
    pop si         ; endereco fonte
    lea di, DS:nome
    ; copiar string
R16:
    mov al, [si]
    mov [di], al
    cmp al, '$'
    je R17
    inc si
    inc di
    jmp R16
R17:
    lea dx, DS:str4
    push dx
    pop si         ; endereco fonte
    lea di, DS:sobrenome
    ; copiar string
R18:
    mov al, [si]
    mov [di], al
    cmp al, '$'
    je R19
    inc si
    inc di
    jmp R18
R19:
    lea dx, DS:nome
    push dx
    lea dx, DS:sobrenome
    push dx
    pop bx          ; operando direito
    pop ax          ; operando esquerdo
    add ax, bx
    push ax
    pop si         ; endereco fonte
    lea di, DS:nome_completo
    ; copiar string
R20:
    mov al, [si]
    mov [di], al
    cmp al, '$'
    je R21
    inc si
    inc di
    jmp R20
R21:
    lea dx, DS:nome_completo
    push dx
    ; Escrita de string
    pop dx
    mov ah, 09h   ; DOS print string
    int 21h
    mov ax, 17
    push ax
    pop ax
    mov DS:a, ax
    mov ax, 5
    push ax
    pop ax
    mov DS:b, ax
    mov ax, DS:a
    push ax
    mov ax, DS:b
    push ax
    pop bx          ; operando direito
    pop ax          ; operando esquerdo
    cwd
    idiv bx      ; quociente em ax
    push ax
    pop ax
    mov DS:c, ax
    mov ax, DS:c
    push ax
    ; Escrita de inteiro
    pop ax
    ; Converter inteiro para ASCII e imprimir
    call write_int
    mov ax, DS:a
    push ax
    mov ax, DS:b
    push ax
    pop bx          ; operando direito
    pop ax          ; operando esquerdo
    cwd
    idiv bx      ; resto em dx
    mov ax, dx
    push ax
    pop ax
    mov DS:c, ax
    mov ax, DS:c
    push ax
    ; Escrita de inteiro
    pop ax
    ; Converter inteiro para ASCII e imprimir
    call write_int
    mov ax, 10
    push ax
    pop ax
    mov DS:a, ax
    mov ax, 20
    push ax
    pop ax
    mov DS:b, ax
    mov ax, DS:a
    push ax
    mov ax, DS:b
    push ax
    pop bx          ; operando direito
    pop ax          ; operando esquerdo
    cmp ax, bx
    jl R24
    mov ax, 0       ; falso
    jmp R25
R24:
    mov ax, 0FFh    ; verdadeiro
R25:
    push ax
    pop ax
    cmp ax, 0
    je R22
    lea dx, DS:str5
    push dx
    ; Escrita de string
    pop dx
    mov ah, 09h   ; DOS print string
    int 21h
R22:
    mov ax, DS:a
    push ax
    mov ax, DS:b
    push ax
    pop bx          ; operando direito
    pop ax          ; operando esquerdo
    cmp ax, bx
    jne R28
    mov ax, 0       ; falso
    jmp R29
R28:
    mov ax, 0FFh    ; verdadeiro
R29:
    push ax
    pop ax
    cmp ax, 0
    je R26
    lea dx, DS:str6
    push dx
    ; Escrita de string
    pop dx
    mov ah, 09h   ; DOS print string
    int 21h
R26:
    mov ax, DS:a
    push ax
    mov ax, DS:b
    push ax
    pop bx          ; operando direito
    pop ax          ; operando esquerdo
    cmp ax, bx
    jle R32
    mov ax, 0       ; falso
    jmp R33
R32:
    mov ax, 0FFh    ; verdadeiro
R33:
    push ax
    pop ax
    cmp ax, 0
    je R30
    lea dx, DS:str7
    push dx
    ; Escrita de string
    pop dx
    mov ah, 09h   ; DOS print string
    int 21h
R30:
    lea dx, DS:str8
    push dx
    pop si         ; endereco fonte
    lea di, DS:mensagem
    ; copiar string
R34:
    mov al, [si]
    mov [di], al
    cmp al, '$'
    je R35
    inc si
    inc di
    jmp R34
R35:
    lea dx, DS:mensagem
    push dx
    lea dx, DS:str9
    push dx
    pop bx          ; operando direito
    pop ax          ; operando esquerdo
    cmp ax, bx
    je R38
    mov ax, 0       ; falso
    jmp R39
R38:
    mov ax, 0FFh    ; verdadeiro
R39:
    push ax
    pop ax
    cmp ax, 0
    je R36
    lea dx, DS:str10
    push dx
    ; Escrita de string
    pop dx
    mov ah, 09h   ; DOS print string
    int 21h
R36:
    mov ax, 5
    push ax
    pop ax
    mov DS:a, ax
    mov ax, DS:a
    push ax
    pop ax
    neg ax          ; negacao aritmetica
    push ax
    pop ax
    mov DS:b, ax
    mov ax, DS:b
    push ax
    ; Escrita de inteiro
    pop ax
    ; Converter inteiro para ASCII e imprimir
    call write_int
    mov ax, DS:a
    push ax
    pop ax
    mov DS:c, ax
    mov ax, DS:c
    push ax
    ; Escrita de inteiro
    pop ax
    ; Converter inteiro para ASCII e imprimir
    call write_int
    mov ax, 2
    push ax
    pop ax
    mov DS:a, ax
    mov ax, 3
    push ax
    pop ax
    mov DS:b, ax
    mov ax, DS:a
    push ax
    mov ax, DS:b
    push ax
    pop bx          ; operando direito
    pop ax          ; operando esquerdo
    add ax, bx
    push ax
    mov ax, DS:a
    push ax
    mov ax, DS:b
    push ax
    pop bx          ; operando direito
    pop ax          ; operando esquerdo
    sub ax, bx
    push ax
    pop bx          ; operando direito
    pop ax          ; operando esquerdo
    imul bx
    push ax
    pop ax
    mov DS:c, ax
    mov ax, DS:c
    push ax
    ; Escrita de inteiro
    pop ax
    ; Converter inteiro para ASCII e imprimir
    call write_int
    mov ax, DS:a
    push ax
    mov ax, DS:b
    push ax
    mov ax, 1
    push ax
    pop bx          ; operando direito
    pop ax          ; operando esquerdo
    add ax, bx
    push ax
    pop bx          ; operando direito
    pop ax          ; operando esquerdo
    imul bx
    push ax
    pop ax
    mov DS:c, ax
    mov ax, DS:c
    push ax
    ; Escrita de inteiro
    pop ax
    ; Converter inteiro para ASCII e imprimir
    call write_int
    ; Leitura de inteiro para a
    lea dx, DS:a
    mov ah, 0Ah   ; leitura buffered
    int 21h
    ; Converter ASCII para inteiro e armazenar
    ; Leitura de string para nome
    lea dx, DS:nome
    mov ah, 0Ah   ; leitura buffered
    int 21h
    mov ax, DS:a
    push ax
    ; Escrita de inteiro
    pop ax
    ; Converter inteiro para ASCII e imprimir
    call write_int
    lea dx, DS:nome
    push dx
    ; Escrita de string
    pop dx
    mov ah, 09h   ; DOS print string
    int 21h
    ; Leitura de inteiro para a
    lea dx, DS:a
    mov ah, 0Ah   ; leitura buffered
    int 21h
    ; Converter ASCII para inteiro e armazenar
    ; Leitura de inteiro para b
    lea dx, DS:b
    mov ah, 0Ah   ; leitura buffered
    int 21h
    ; Converter ASCII para inteiro e armazenar
    ; Leitura de inteiro para c
    lea dx, DS:c
    mov ah, 0Ah   ; leitura buffered
    int 21h
    ; Converter ASCII para inteiro e armazenar
    mov ax, DS:a
    push ax
    ; Escrita de inteiro
    pop ax
    ; Converter inteiro para ASCII e imprimir
    call write_int
    mov ax, DS:b
    push ax
    ; Escrita de inteiro
    pop ax
    ; Converter inteiro para ASCII e imprimir
    call write_int
    mov ax, DS:c
    push ax
    ; Escrita de inteiro
    pop ax
    ; Converter inteiro para ASCII e imprimir
    call write_int
    mov ax, 5
    push ax
    pop ax
    mov DS:a, ax
    mov ax, DS:a
    push ax
    pop ax
    mov DS:x, ax
    mov ax, DS:x
    push ax
    ; Escrita de inteiro
    pop ax
    ; Converter inteiro para ASCII e imprimir
    call write_int
    mov ax, DS:a
    push ax
    ; carregar real 2.5
    mov ax, 1075838976
    push ax
    pop bx          ; operando direito
    pop ax          ; operando esquerdo
    add ax, bx
    push ax
    pop ax
    mov DS:y, ax
    mov ax, DS:y
    push ax
    ; Escrita de inteiro
    pop ax
    ; Converter inteiro para ASCII e imprimir
    call write_int
    mov ax, 1
    push ax
    pop ax
    mov DS:a, ax
R40:
    mov ax, DS:a
    push ax
    mov ax, 3
    push ax
    pop bx          ; operando direito
    pop ax          ; operando esquerdo
    cmp ax, bx
    jle R42
    mov ax, 0       ; falso
    jmp R43
R42:
    mov ax, 0FFh    ; verdadeiro
R43:
    push ax
    pop ax
    cmp ax, 0
    je R41
    mov ax, 1
    push ax
    pop ax
    mov DS:b, ax
R44:
    mov ax, DS:b
    push ax
    mov ax, 3
    push ax
    pop bx          ; operando direito
    pop ax          ; operando esquerdo
    cmp ax, bx
    jle R46
    mov ax, 0       ; falso
    jmp R47
R46:
    mov ax, 0FFh    ; verdadeiro
R47:
    push ax
    pop ax
    cmp ax, 0
    je R45
    mov ax, DS:a
    push ax
    mov ax, DS:b
    push ax
    pop bx          ; operando direito
    pop ax          ; operando esquerdo
    imul bx
    push ax
    pop ax
    mov DS:c, ax
    mov ax, DS:c
    push ax
    mov ax, 4
    push ax
    pop bx          ; operando direito
    pop ax          ; operando esquerdo
    cmp ax, bx
    jg R50
    mov ax, 0       ; falso
    jmp R51
R50:
    mov ax, 0FFh    ; verdadeiro
R51:
    push ax
    pop ax
    cmp ax, 0
    je R48
    mov ax, DS:c
    push ax
    ; Escrita de inteiro
    pop ax
    ; Converter inteiro para ASCII e imprimir
    call write_int
R48:
    mov ax, DS:b
    push ax
    mov ax, 1
    push ax
    pop bx          ; operando direito
    pop ax          ; operando esquerdo
    add ax, bx
    push ax
    pop ax
    mov DS:b, ax
    jmp R44
R45:
    mov ax, DS:a
    push ax
    mov ax, 1
    push ax
    pop bx          ; operando direito
    pop ax          ; operando esquerdo
    add ax, bx
    push ax
    pop ax
    mov DS:a, ax
    jmp R40
R41:
    lea dx, DS:str11
    push dx
    ; Escrita de string
    pop dx
    mov ah, 09h   ; DOS print string
    int 21h

    ; Finalizar programa
    mov ah, 4Ch
    int 21h

cseg ENDS
END strt
