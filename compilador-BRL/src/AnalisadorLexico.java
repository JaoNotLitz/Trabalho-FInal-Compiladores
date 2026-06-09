import exceptions.LexicalException;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Compilador BRL - Analisador Léxico
 * Realiza a análise léxica do programa fonte na linguagem BRL.
 * Caracteres permitidos: letra, digito, espaco, sublinhado, ponto,
 * virgula, ponto-e-virgula, dois-pontos, parenteses, colchetes, chaves,
 * sinal de mais, sinal de menos, aspas, apostrofo, barra, barra invertida,
 * barra em pe (pipe), exclamacao, interrogacao, maior, menor e igual,
 * alem da quebra de linha (0Dh e 0Ah).
 */
public class AnalisadorLexico {

    private static final Map<String, TokenType> PALAVRAS_RESERVADAS = new HashMap<>();

    static {
        PALAVRAS_RESERVADAS.put("inteiro", TokenType.INTEIRO);
        PALAVRAS_RESERVADAS.put("caractere", TokenType.CARACTERE);
        PALAVRAS_RESERVADAS.put("logico", TokenType.LOGICO);
        PALAVRAS_RESERVADAS.put("real", TokenType.REAL);
        PALAVRAS_RESERVADAS.put("se", TokenType.SE);
        PALAVRAS_RESERVADAS.put("entao", TokenType.ENTAO);
        PALAVRAS_RESERVADAS.put("senao", TokenType.SENAO);
        PALAVRAS_RESERVADAS.put("enquanto", TokenType.ENQUANTO);
        PALAVRAS_RESERVADAS.put("faca", TokenType.FACA);
        PALAVRAS_RESERVADAS.put("leitura", TokenType.LEITURA);
        PALAVRAS_RESERVADAS.put("escrita", TokenType.ESCRITA);
        PALAVRAS_RESERVADAS.put("ou", TokenType.OU);
        PALAVRAS_RESERVADAS.put("e", TokenType.E);
        PALAVRAS_RESERVADAS.put("nao", TokenType.NAO);
        PALAVRAS_RESERVADAS.put("inicio", TokenType.INICIO);
        PALAVRAS_RESERVADAS.put("fim", TokenType.FIM);
        PALAVRAS_RESERVADAS.put("div", TokenType.DIV);
        PALAVRAS_RESERVADAS.put("mod", TokenType.MOD);
        PALAVRAS_RESERVADAS.put("verdadeiro", TokenType.VERDADEIRO);
        PALAVRAS_RESERVADAS.put("falso", TokenType.FALSO);
    }

    private String fonte;
    private int pos;
    private int linha;
    private int coluna;
    private char caracterAtual;
    private boolean fimArquivo;

    public AnalisadorLexico() {
        this.pos = 0;
        this.linha = 1;
        this.coluna = 0;
        this.fimArquivo = false;
    }

    /**
     * Executa a análise léxica do arquivo fonte.
     */
    public List<Token> analisar(String caminhoArquivo) throws LexicalException, IOException {
        // Lê o arquivo inteiro para uma string
        fonte = lerArquivo(caminhoArquivo);
        pos = 0;
        linha = 1;
        coluna = 0;
        fimArquivo = false;

        if (fonte.isEmpty()) {
            fimArquivo = true;
        } else {
            caracterAtual = fonte.charAt(0);
            coluna = 1;
        }

        List<Token> tokens = new ArrayList<>();

        while (!fimArquivo) {
            Token token = proximoToken();
            if (token != null) {
                tokens.add(token);
            }
        }

        // Adiciona token EOF
        tokens.add(new Token("EOF", TokenType.EOF, linha, coluna));
        return tokens;
    }

    private String lerArquivo(String caminho) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(caminho))) {
            int c;
            while ((c = reader.read()) != -1) {
                sb.append((char) c);
            }
        }
        return sb.toString();
    }

    private void avancar() {
        pos++;
        if (pos >= fonte.length()) {
            fimArquivo = true;
            caracterAtual = '\0';
        } else {
            if (caracterAtual == '\n') {
                linha++;
                coluna = 1;
            } else {
                coluna++;
            }
            caracterAtual = fonte.charAt(pos);
        }
    }

    private char espiar() {
        int proxPos = pos + 1;
        if (proxPos >= fonte.length()) {
            return '\0';
        }
        return fonte.charAt(proxPos);
    }

    /**
     * Verifica se o caractere é permitido na linguagem BRL.
     */
    private boolean caracterePermitido(char c) {
        if (Character.isLetterOrDigit(c)) return true;
        if (c == ' ' || c == '\t' || c == '\r' || c == '\n') return true;
        String permitidos = "_.;:,()[]{}+-\"'/\\|!?<>=*&";
        return permitidos.indexOf(c) != -1;
    }

    /**
     * Obtém o próximo token do arquivo fonte.
     */
    private Token proximoToken() throws LexicalException {
        // Pula espaços em branco
        while (!fimArquivo && (caracterAtual == ' ' || caracterAtual == '\t' ||
                caracterAtual == '\r' || caracterAtual == '\n')) {
            avancar();
        }

        if (fimArquivo) return null;

        // Verifica caractere inválido
        if (!caracterePermitido(caracterAtual)) {
            throw new LexicalException("Caractere invalido: '" + caracterAtual + "'", linha, coluna);
        }

        int linhaInicio = linha;
        int colunaInicio = coluna;

        // Comentários /* */
        if (caracterAtual == '/' && espiar() == '*') {
            pularComentario();
            return null;
        }

        // Identificadores e palavras reservadas
        if (Character.isLetter(caracterAtual) || caracterAtual == '_') {
            return lerIdentificadorOuPalavraReservada(linhaInicio, colunaInicio);
        }

        // Números (inteiro ou real)
        if (Character.isDigit(caracterAtual)) {
            return lerNumero(linhaInicio, colunaInicio);
        }

        // Strings (caractere entre aspas)
        if (caracterAtual == '"') {
            return lerString(linhaInicio, colunaInicio);
        }

        // Operadores e delimitadores
        return lerOperadorOuDelimitador(linhaInicio, colunaInicio);
    }

    /**
     * Pula comentário delimitado por /* e * /
     */
    private void pularComentario() throws LexicalException {
        int linhaInicio = linha;
        int colunaInicio = coluna;
        avancar(); // consome '/'
        avancar(); // consome '*'

        while (!fimArquivo) {
            if (caracterAtual == '*' && espiar() == '/') {
                avancar(); // consome '*'
                avancar(); // consome '/'
                return;
            }
            avancar();
        }

        throw new LexicalException("Comentario nao fechado (esperado '*/')", linhaInicio, colunaInicio);
    }

    /**
     * Lê um identificador ou palavra reservada.
     * Identificadores: letra ou sublinhado, seguido de letras, digitos ou sublinhado.
     * Máximo 512 caracteres.
     */
    private Token lerIdentificadorOuPalavraReservada(int linhaInicio, int colunaInicio) throws LexicalException {
        StringBuilder lexema = new StringBuilder();
        lexema.append(caracterAtual);
        avancar();

        while (!fimArquivo && (Character.isLetterOrDigit(caracterAtual) || caracterAtual == '_')) {
            lexema.append(caracterAtual);
            avancar();
        }

        String lex = lexema.toString();

        if (lex.length() > 512) {
            throw new LexicalException("Identificador excede 512 caracteres", linhaInicio, colunaInicio);
        }

        // Verifica se é palavra reservada (case-sensitive)
        if (PALAVRAS_RESERVADAS.containsKey(lex)) {
            TokenType tipo = PALAVRAS_RESERVADAS.get(lex);
            // verdadeiro e falso são constantes lógicas
            if (tipo == TokenType.VERDADEIRO || tipo == TokenType.FALSO) {
                return new Token(lex, TokenType.CONST_LOGICO, linhaInicio, colunaInicio);
            }
            return new Token(lex, tipo, linhaInicio, colunaInicio);
        }

        return new Token(lex, TokenType.IDENTIFICADOR, linhaInicio, colunaInicio);
    }

    /**
     * Lê um número (inteiro ou real).
     * Inteiro: sequência de dígitos, opcionalmente precedida de + ou -
     * Real: dígitos com ponto decimal (ex: 3.14)
     */
    private Token lerNumero(int linhaInicio, int colunaInicio) throws LexicalException {
        StringBuilder lexema = new StringBuilder();
        boolean temPonto = false;

        lexema.append(caracterAtual);
        avancar();

        while (!fimArquivo && (Character.isDigit(caracterAtual) || caracterAtual == '.')) {
            if (caracterAtual == '.') {
                if (temPonto) {
                    throw new LexicalException("Numero real mal formado (dois pontos decimais)", linhaInicio, colunaInicio);
                }
                temPonto = true;
            }
            lexema.append(caracterAtual);
            avancar();
        }

        String lex = lexema.toString();

        // Verifica se termina com ponto (mal formado)
        if (lex.endsWith(".")) {
            throw new LexicalException("Numero real mal formado (termina com ponto)", linhaInicio, colunaInicio);
        }

        if (temPonto) {
            return new Token(lex, TokenType.CONST_REAL, linhaInicio, colunaInicio);
        } else {
            return new Token(lex, TokenType.CONST_INTEIRO, linhaInicio, colunaInicio);
        }
    }

    /**
     * Lê uma string delimitada por aspas.
     * Não pode conter quebra de linha.
     * Máximo 255 caracteres úteis.
     */
    private Token lerString(int linhaInicio, int colunaInicio) throws LexicalException {
        StringBuilder lexema = new StringBuilder();
        lexema.append(caracterAtual); // aspas de abertura
        avancar();

        while (!fimArquivo && caracterAtual != '"') {
            if (caracterAtual == '\n' || caracterAtual == '\r') {
                throw new LexicalException("String nao pode conter quebra de linha", linhaInicio, colunaInicio);
            }
            lexema.append(caracterAtual);
            avancar();
        }

        if (fimArquivo) {
            throw new LexicalException("String nao fechada (esperado '\"')", linhaInicio, colunaInicio);
        }

        lexema.append(caracterAtual); // aspas de fechamento
        avancar();

        // Verifica tamanho (sem as aspas)
        String conteudo = lexema.substring(1, lexema.length() - 1);
        if (conteudo.length() > 255) {
            throw new LexicalException("String excede 255 caracteres", linhaInicio, colunaInicio);
        }

        return new Token(lexema.toString(), TokenType.CONST_CARACTERE, linhaInicio, colunaInicio);
    }

    /**
     * Lê operadores e delimitadores.
     */
    private Token lerOperadorOuDelimitador(int linhaInicio, int colunaInicio) throws LexicalException {
        char c = caracterAtual;

        switch (c) {
            case '+':
                avancar();
                // Verifica se é sinal de número: +dígito
                // Não tratamos aqui — será tratado na análise sintática
                return new Token("+", TokenType.MAIS, linhaInicio, colunaInicio);

            case '-':
                avancar();
                return new Token("-", TokenType.MENOS, linhaInicio, colunaInicio);

            case '*':
                avancar();
                return new Token("*", TokenType.VEZES, linhaInicio, colunaInicio);

            case '/':
                avancar();
                return new Token("/", TokenType.BARRA, linhaInicio, colunaInicio);

            case '=':
                avancar();
                if (!fimArquivo && caracterAtual == '=') {
                    avancar();
                    return new Token("==", TokenType.IGUAL, linhaInicio, colunaInicio);
                }
                throw new LexicalException("Caractere '=' inesperado (use ':=' para atribuicao ou '==' para comparacao)", linhaInicio, colunaInicio);

            case '<':
                avancar();
                if (!fimArquivo && caracterAtual == '=') {
                    avancar();
                    return new Token("<=", TokenType.MENOR_IGUAL, linhaInicio, colunaInicio);
                }
                if (!fimArquivo && caracterAtual == '>') {
                    avancar();
                    return new Token("<>", TokenType.DIFERENTE, linhaInicio, colunaInicio);
                }
                return new Token("<", TokenType.MENOR, linhaInicio, colunaInicio);

            case '>':
                avancar();
                if (!fimArquivo && caracterAtual == '=') {
                    avancar();
                    return new Token(">=", TokenType.MAIOR_IGUAL, linhaInicio, colunaInicio);
                }
                return new Token(">", TokenType.MAIOR, linhaInicio, colunaInicio);

            case '&':
                avancar();
                if (!fimArquivo && caracterAtual == '&') {
                    avancar();
                    return new Token("&&", TokenType.E_LOGICO, linhaInicio, colunaInicio);
                }
                throw new LexicalException("Caractere '&' inesperado (use '&&' para E logico)", linhaInicio, colunaInicio);

            case ':':
                avancar();
                if (!fimArquivo && caracterAtual == '=') {
                    avancar();
                    return new Token(":=", TokenType.ATRIBUICAO, linhaInicio, colunaInicio);
                }
                return new Token(":", TokenType.DOIS_PONTOS, linhaInicio, colunaInicio);

            case ';':
                avancar();
                return new Token(";", TokenType.PONTO_VIRGULA, linhaInicio, colunaInicio);

            case ',':
                avancar();
                return new Token(",", TokenType.VIRGULA, linhaInicio, colunaInicio);

            case '(':
                avancar();
                return new Token("(", TokenType.ABRE_PAREN, linhaInicio, colunaInicio);

            case ')':
                avancar();
                return new Token(")", TokenType.FECHA_PAREN, linhaInicio, colunaInicio);

            default:
                throw new LexicalException("Caractere invalido: '" + c + "'", linhaInicio, colunaInicio);
        }
    }
}
