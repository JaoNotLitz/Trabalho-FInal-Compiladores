; Programa: programa2
; Gerado pelo compilador BRL

dseg SEGMENT PUBLIC
    byte 4000h DUP(?)  ; area temporaria
    nome byte 512 DUP(?)  ; caractere (512 bytes)
    idade SWORD ?
    salario SDWORD ?
    ativo byte ?
dseg ENDS

    ; Strings literais
    str0 byte "Maior de idade", '$'
    str1 byte "Menor de idade", '$'
    msg_verdadeiro byte "verdadeiro", '$'
    msg_falso byte "falso", '$'

cseg SEGMENT PUBLIC
    ASSUME CS:cseg, DS:dseg

strt:
    mov ax, dseg
    mov ds, ax

    ; Leitura de string para nome
    lea dx, DS:nome
    mov ah, 0Ah   ; leitura buffered
    int 21h
    ; Leitura de inteiro para idade
    lea dx, DS:idade
    mov ah, 0Ah   ; leitura buffered
    int 21h
    ; Converter ASCII para inteiro e armazenar
    mov ax, 1500
    push ax
    pop ax
    mov DS:salario, ax
    mov ax, 0FFh  ; verdadeiro
    push ax
    pop ax
    mov DS:ativo, al
    mov ax, DS:idade
    push ax
    mov ax, 18
    push ax
    pop bx          ; operando direito
    pop ax          ; operando esquerdo
    cmp ax, bx
    jge R2
    mov ax, 0       ; falso
    jmp R3
R2:
    mov ax, 0FFh    ; verdadeiro
R3:
    push ax
    pop ax
    cmp ax, 0
    je R0
    lea dx, DS:str0
    push dx
    ; Escrita de string
    pop dx
    mov ah, 09h   ; DOS print string
    int 21h
    lea dx, DS:nome
    push dx
    ; Escrita de string
    pop dx
    mov ah, 09h   ; DOS print string
    int 21h
    jmp R1
R0:
    lea dx, DS:str1
    push dx
    ; Escrita de string
    pop dx
    mov ah, 09h   ; DOS print string
    int 21h
R1:
R4:
    mov al, DS:ativo
    cbw
    push ax
    pop ax
    cmp ax, 0
    je R5
    mov ax, DS:salario
    push ax
    mov ax, 100
    push ax
    pop bx          ; operando direito
    pop ax          ; operando esquerdo
    add ax, bx
    push ax
    pop ax
    mov DS:salario, ax
    mov ax, DS:salario
    push ax
    mov ax, 5000
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
    mov ax, 0     ; falso
    push ax
    pop ax
    mov DS:ativo, al
R6:
    jmp R4
R5:
    mov ax, DS:salario
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
