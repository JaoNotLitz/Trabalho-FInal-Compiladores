; Programa: teste_algoritmos
; Gerado pelo compilador BRL

dseg SEGMENT PUBLIC
    byte 4000h DUP(?)  ; area temporaria
    fib_a SWORD ?
    fib_b SWORD ?
    fib_temp SWORD ?
    fib_n SWORD ?
    fib_i SWORD ?
    soma SWORD ?
    num SWORD ?
    max_num SWORD ?
    n SWORD ?
    divisor SWORD ?
    eh_primo SWORD ?
    primo byte ?
    celsius SDWORD ?
    fahrenheit SDWORD ?
    msg byte 512 DUP(?)  ; caractere (512 bytes)
    continua byte ?
dseg ENDS

    ; Strings literais
    str0 byte "=== Fibonacci ===", '$'
    str1 byte "=== Soma de pares ===", '$'
    str2 byte "=== Maior de tres numeros ===", '$'
    str3 byte "=== Conversao de temperatura ===", '$'
    str4 byte "=== Contagem regressiva ===", '$'
    str5 byte "Lancamento!", '$'
    str6 byte "=== Operacoes mistas ===", '$'
    str7 byte "Numero eh par", '$'
    str8 byte "Numero eh impar", '$'
    str9 byte "Primo e continua", '$'
    str10 byte "Nao primo ou nao continua", '$'
    str11 byte "=== Strings ===", '$'
    str12 byte "Compilador", '$'
    str13 byte " BRL", '$'
    str14 byte " funcionando", '$'
    str15 byte "=== FIM DOS TESTES ===", '$'
    msg_verdadeiro byte "verdadeiro", '$'
    msg_falso byte "falso", '$'

cseg SEGMENT PUBLIC
    ASSUME CS:cseg, DS:dseg

strt:
    mov ax, dseg
    mov ds, ax

    lea dx, DS:str0
    push dx
    ; Escrita de string
    pop dx
    mov ah, 09h   ; DOS print string
    int 21h
    mov ax, 10
    push ax
    pop ax
    mov DS:fib_n, ax
    mov ax, 0
    push ax
    pop ax
    mov DS:fib_a, ax
    mov ax, 1
    push ax
    pop ax
    mov DS:fib_b, ax
    mov ax, 0
    push ax
    pop ax
    mov DS:fib_i, ax
    mov ax, DS:fib_a
    push ax
    ; Escrita de inteiro
    pop ax
    ; Converter inteiro para ASCII e imprimir
    call write_int
    mov ax, DS:fib_b
    push ax
    ; Escrita de inteiro
    pop ax
    ; Converter inteiro para ASCII e imprimir
    call write_int
R0:
    mov ax, DS:fib_i
    push ax
    mov ax, DS:fib_n
    push ax
    pop bx          ; operando direito
    pop ax          ; operando esquerdo
    cmp ax, bx
    jl R2
    mov ax, 0       ; falso
    jmp R3
R2:
    mov ax, 0FFh    ; verdadeiro
R3:
    push ax
    pop ax
    cmp ax, 0
    je R1
    mov ax, DS:fib_a
    push ax
    mov ax, DS:fib_b
    push ax
    pop bx          ; operando direito
    pop ax          ; operando esquerdo
    add ax, bx
    push ax
    pop ax
    mov DS:fib_temp, ax
    mov ax, DS:fib_b
    push ax
    pop ax
    mov DS:fib_a, ax
    mov ax, DS:fib_temp
    push ax
    pop ax
    mov DS:fib_b, ax
    mov ax, DS:fib_temp
    push ax
    ; Escrita de inteiro
    pop ax
    ; Converter inteiro para ASCII e imprimir
    call write_int
    mov ax, DS:fib_i
    push ax
    mov ax, 1
    push ax
    pop bx          ; operando direito
    pop ax          ; operando esquerdo
    add ax, bx
    push ax
    pop ax
    mov DS:fib_i, ax
    jmp R0
R1:
    lea dx, DS:str1
    push dx
    ; Escrita de string
    pop dx
    mov ah, 09h   ; DOS print string
    int 21h
    mov ax, 0
    push ax
    pop ax
    mov DS:soma, ax
    mov ax, 2
    push ax
    pop ax
    mov DS:num, ax
    mov ax, 20
    push ax
    pop ax
    mov DS:max_num, ax
R4:
    mov ax, DS:num
    push ax
    mov ax, DS:max_num
    push ax
    pop bx          ; operando direito
    pop ax          ; operando esquerdo
    cmp ax, bx
    jle R6
    mov ax, 0       ; falso
    jmp R7
R6:
    mov ax, 0FFh    ; verdadeiro
R7:
    push ax
    pop ax
    cmp ax, 0
    je R5
    mov ax, DS:soma
    push ax
    mov ax, DS:num
    push ax
    pop bx          ; operando direito
    pop ax          ; operando esquerdo
    add ax, bx
    push ax
    pop ax
    mov DS:soma, ax
    mov ax, DS:num
    push ax
    mov ax, 2
    push ax
    pop bx          ; operando direito
    pop ax          ; operando esquerdo
    add ax, bx
    push ax
    pop ax
    mov DS:num, ax
    jmp R4
R5:
    mov ax, DS:soma
    push ax
    ; Escrita de inteiro
    pop ax
    ; Converter inteiro para ASCII e imprimir
    call write_int
    lea dx, DS:str2
    push dx
    ; Escrita de string
    pop dx
    mov ah, 09h   ; DOS print string
    int 21h
    mov ax, 15
    push ax
    pop ax
    mov DS:fib_a, ax
    mov ax, 42
    push ax
    pop ax
    mov DS:fib_b, ax
    mov ax, 7
    push ax
    pop ax
    mov DS:fib_temp, ax
    mov ax, DS:fib_a
    push ax
    mov ax, DS:fib_b
    push ax
    pop bx          ; operando direito
    pop ax          ; operando esquerdo
    cmp ax, bx
    jge R10
    mov ax, 0       ; falso
    jmp R11
R10:
    mov ax, 0FFh    ; verdadeiro
R11:
    push ax
    pop ax
    cmp ax, 0
    je R8
    mov ax, DS:fib_a
    push ax
    mov ax, DS:fib_temp
    push ax
    pop bx          ; operando direito
    pop ax          ; operando esquerdo
    cmp ax, bx
    jge R14
    mov ax, 0       ; falso
    jmp R15
R14:
    mov ax, 0FFh    ; verdadeiro
R15:
    push ax
    pop ax
    cmp ax, 0
    je R12
    mov ax, DS:fib_a
    push ax
    ; Escrita de inteiro
    pop ax
    ; Converter inteiro para ASCII e imprimir
    call write_int
    jmp R13
R12:
    mov ax, DS:fib_temp
    push ax
    ; Escrita de inteiro
    pop ax
    ; Converter inteiro para ASCII e imprimir
    call write_int
R13:
    jmp R9
R8:
    mov ax, DS:fib_b
    push ax
    mov ax, DS:fib_temp
    push ax
    pop bx          ; operando direito
    pop ax          ; operando esquerdo
    cmp ax, bx
    jge R18
    mov ax, 0       ; falso
    jmp R19
R18:
    mov ax, 0FFh    ; verdadeiro
R19:
    push ax
    pop ax
    cmp ax, 0
    je R16
    mov ax, DS:fib_b
    push ax
    ; Escrita de inteiro
    pop ax
    ; Converter inteiro para ASCII e imprimir
    call write_int
    jmp R17
R16:
    mov ax, DS:fib_temp
    push ax
    ; Escrita de inteiro
    pop ax
    ; Converter inteiro para ASCII e imprimir
    call write_int
R17:
R9:
    lea dx, DS:str3
    push dx
    ; Escrita de string
    pop dx
    mov ah, 09h   ; DOS print string
    int 21h
    ; carregar real 0.0
    mov ax, 0
    push ax
    pop ax
    mov DS:celsius, ax
R20:
    mov ax, DS:celsius
    push ax
    ; carregar real 100.0
    mov ax, 1120403456
    push ax
    pop bx          ; operando direito
    pop ax          ; operando esquerdo
    cmp ax, bx
    jle R22
    mov ax, 0       ; falso
    jmp R23
R22:
    mov ax, 0FFh    ; verdadeiro
R23:
    push ax
    pop ax
    cmp ax, 0
    je R21
    mov ax, DS:celsius
    push ax
    ; carregar real 1.8
    mov ax, 1072064102
    push ax
    pop bx          ; operando direito
    pop ax          ; operando esquerdo
    imul bx
    push ax
    ; carregar real 32.0
    mov ax, 1107296256
    push ax
    pop bx          ; operando direito
    pop ax          ; operando esquerdo
    add ax, bx
    push ax
    pop ax
    mov DS:fahrenheit, ax
    mov ax, DS:celsius
    push ax
    ; Escrita de inteiro
    pop ax
    ; Converter inteiro para ASCII e imprimir
    call write_int
    mov ax, DS:fahrenheit
    push ax
    ; Escrita de inteiro
    pop ax
    ; Converter inteiro para ASCII e imprimir
    call write_int
    mov ax, DS:celsius
    push ax
    ; carregar real 25.0
    mov ax, 1103626240
    push ax
    pop bx          ; operando direito
    pop ax          ; operando esquerdo
    add ax, bx
    push ax
    pop ax
    mov DS:celsius, ax
    jmp R20
R21:
    lea dx, DS:str4
    push dx
    ; Escrita de string
    pop dx
    mov ah, 09h   ; DOS print string
    int 21h
    mov ax, 10
    push ax
    pop ax
    mov DS:num, ax
R24:
    mov ax, DS:num
    push ax
    mov ax, 0
    push ax
    pop bx          ; operando direito
    pop ax          ; operando esquerdo
    cmp ax, bx
    jge R26
    mov ax, 0       ; falso
    jmp R27
R26:
    mov ax, 0FFh    ; verdadeiro
R27:
    push ax
    pop ax
    cmp ax, 0
    je R25
    mov ax, DS:num
    push ax
    ; Escrita de inteiro
    pop ax
    ; Converter inteiro para ASCII e imprimir
    call write_int
    mov ax, DS:num
    push ax
    mov ax, 0
    push ax
    pop bx          ; operando direito
    pop ax          ; operando esquerdo
    cmp ax, bx
    je R30
    mov ax, 0       ; falso
    jmp R31
R30:
    mov ax, 0FFh    ; verdadeiro
R31:
    push ax
    pop ax
    cmp ax, 0
    je R28
    lea dx, DS:str5
    push dx
    ; Escrita de string
    pop dx
    mov ah, 09h   ; DOS print string
    int 21h
R28:
    mov ax, DS:num
    push ax
    mov ax, 1
    push ax
    pop bx          ; operando direito
    pop ax          ; operando esquerdo
    sub ax, bx
    push ax
    pop ax
    mov DS:num, ax
    jmp R24
R25:
    lea dx, DS:str6
    push dx
    ; Escrita de string
    pop dx
    mov ah, 09h   ; DOS print string
    int 21h
    mov ax, 100
    push ax
    pop ax
    mov DS:n, ax
    mov ax, 0FFh  ; verdadeiro
    push ax
    pop ax
    mov DS:primo, al
    mov ax, 0FFh  ; verdadeiro
    push ax
    pop ax
    mov DS:continua, al
    mov ax, DS:n
    push ax
    mov ax, 2
    push ax
    pop bx          ; operando direito
    pop ax          ; operando esquerdo
    cwd
    idiv bx      ; resto em dx
    mov ax, dx
    push ax
    mov ax, 0
    push ax
    pop bx          ; operando direito
    pop ax          ; operando esquerdo
    cmp ax, bx
    je R34
    mov ax, 0       ; falso
    jmp R35
R34:
    mov ax, 0FFh    ; verdadeiro
R35:
    push ax
    pop ax
    cmp ax, 0
    je R32
    lea dx, DS:str7
    push dx
    ; Escrita de string
    pop dx
    mov ah, 09h   ; DOS print string
    int 21h
    mov ax, 0     ; falso
    push ax
    pop ax
    mov DS:primo, al
    jmp R33
R32:
    lea dx, DS:str8
    push dx
    ; Escrita de string
    pop dx
    mov ah, 09h   ; DOS print string
    int 21h
R33:
    mov al, DS:primo
    cbw
    push ax
    mov al, DS:continua
    cbw
    push ax
    pop bx          ; operando direito
    pop ax          ; operando esquerdo
    and ax, bx   ; E logico
    push ax
    pop ax
    cmp ax, 0
    je R36
    lea dx, DS:str9
    push dx
    ; Escrita de string
    pop dx
    mov ah, 09h   ; DOS print string
    int 21h
    jmp R37
R36:
    lea dx, DS:str10
    push dx
    ; Escrita de string
    pop dx
    mov ah, 09h   ; DOS print string
    int 21h
R37:
    lea dx, DS:str11
    push dx
    ; Escrita de string
    pop dx
    mov ah, 09h   ; DOS print string
    int 21h
    lea dx, DS:str12
    push dx
    lea dx, DS:str13
    push dx
    pop bx          ; operando direito
    pop ax          ; operando esquerdo
    add ax, bx
    push ax
    lea dx, DS:str14
    push dx
    pop bx          ; operando direito
    pop ax          ; operando esquerdo
    add ax, bx
    push ax
    pop si         ; endereco fonte
    lea di, DS:msg
    ; copiar string
R38:
    mov al, [si]
    mov [di], al
    cmp al, '$'
    je R39
    inc si
    inc di
    jmp R38
R39:
    lea dx, DS:msg
    push dx
    ; Escrita de string
    pop dx
    mov ah, 09h   ; DOS print string
    int 21h
    lea dx, DS:str15
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
