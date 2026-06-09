/**
 * Compilador BRL - Trabalho Prático de Compiladores
 * Enum TokenType - Tipos de tokens da linguagem BRL
 */
public enum TokenType {
    // Palavras reservadas
    INTEIRO, CARACTERE, LOGICO, REAL,
    SE, ENTAO, SENAO, ENQUANTO, FACA,
    LEITURA, ESCRITA,
    OU, E, NAO,
    INICIO, FIM,
    DIV, MOD,
    VERDADEIRO, FALSO,

    // Identificadores e constantes
    IDENTIFICADOR,
    CONST_INTEIRO,
    CONST_REAL,
    CONST_CARACTERE,
    CONST_LOGICO,

    // Operadores aritméticos
    MAIS,           // +
    MENOS,          // -
    VEZES,          // *
    BARRA,          // /

    // Operadores relacionais
    IGUAL,          // ==
    DIFERENTE,      // <>
    MENOR,          // <
    MAIOR,          // >
    MENOR_IGUAL,    // <=
    MAIOR_IGUAL,    // >=

    // Operadores lógicos (símbolos)
    E_LOGICO,       // &&

    // Atribuição
    ATRIBUICAO,     // :=

    // Delimitadores
    PONTO_VIRGULA,  // ;
    VIRGULA,        // ,
    DOIS_PONTOS,    // :
    ABRE_PAREN,     // (
    FECHA_PAREN,    // )

    // Fim de arquivo
    EOF
}
