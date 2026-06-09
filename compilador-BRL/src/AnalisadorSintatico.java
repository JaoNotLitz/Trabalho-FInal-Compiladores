import exceptions.SyntaxException;
import exceptions.SemanticException;
import java.util.ArrayList;
import java.util.List;

/**
 * Compilador BRL - Analisador Sintático (Parser)
 * Implementa um parser descendente recursivo para a gramática da linguagem BRL.
 *
 * Gramática:
 * PROGRAMA -> 'inicio' IDENTIFICADOR ';' DECLARACOES INSTRUCOES 'fim'
 * DECLARACOES -> (DECLARACAO ';')*
 * DECLARACAO -> ID (',' ID)* ':' TIPO
 * TIPO -> 'inteiro' | 'caractere' | 'logico' | 'real'
 * INSTRUCOES -> (INSTRUCAO (';' INSTRUCAO)*)?
 * INSTRUCAO -> ATRIBUICAO | SE | ENQUANTO | LEITURA | ESCRITA
 * ATRIBUICAO -> ID ':=' EXP
 * SE -> 'se' EXP 'entao' 'inicio' INSTRUCOES 'fim' ('senao' 'inicio' INSTRUCOES 'fim')?
 * ENQUANTO -> 'enquanto' EXP 'faca' 'inicio' INSTRUCOES 'fim'
 * LEITURA -> 'leitura' '(' ID (',' ID)* ')'
 * ESCRITA -> 'escrita' '(' EXP ')'
 * EXP -> EXP_SIMPLES (RELOP EXP_SIMPLES)?
 * EXP_SIMPLES -> TERMO (('+' | '-' | 'ou') TERMO)*
 * TERMO -> FATOR (('*' | '/' | 'div' | 'mod' | '&&') FATOR)*
 * FATOR -> ID | CONST | '(' EXP ')' | 'nao' FATOR | ('+' | '-') FATOR
 */
public class AnalisadorSintatico {

    private List<Token> tokens;
    private int pos;
    private Token tokenAtual;
    private TabelaSimbolos tabela;
    private AnalisadorSemantico semantico;
    private GeradorCodigo gerador;

    public AnalisadorSintatico(TabelaSimbolos tabela, GeradorCodigo gerador) {
        this.tabela = tabela;
        this.semantico = new AnalisadorSemantico(tabela);
        this.gerador = gerador;
    }

    /**
     * Inicia a análise sintática.
     */
    public void analisar(List<Token> tokens) throws SyntaxException, SemanticException {
        this.tokens = tokens;
        this.pos = 0;
        this.tokenAtual = tokens.get(0);

        programa();

        if (tokenAtual.getTipo() != TokenType.EOF) {
            throw new SyntaxException("Tokens inesperados apos 'fim'",
                    tokenAtual.getLinha(), tokenAtual.getColuna());
        }
    }

    // =================== AUXILIARES ===================

    private void consumir(TokenType esperado) throws SyntaxException {
        if (tokenAtual.getTipo() == esperado) {
            avancar();
        } else {
            throw new SyntaxException("Esperado '" + esperado + "', encontrado '" +
                    tokenAtual.getLexema() + "'", tokenAtual.getLinha(), tokenAtual.getColuna());
        }
    }

    private void avancar() {
        pos++;
        if (pos < tokens.size()) {
            tokenAtual = tokens.get(pos);
        }
    }

    private boolean verificar(TokenType tipo) {
        return tokenAtual.getTipo() == tipo;
    }

    private boolean verificarTipo() {
        return verificar(TokenType.INTEIRO) || verificar(TokenType.CARACTERE) ||
               verificar(TokenType.LOGICO) || verificar(TokenType.REAL);
    }

    // =================== REGRAS GRAMATICAIS ===================

    /**
     * PROGRAMA -> 'inicio' IDENTIFICADOR ';' DECLARACOES INSTRUCOES 'fim'
     */
    private void programa() throws SyntaxException, SemanticException {
        consumir(TokenType.INICIO);

        if (!verificar(TokenType.IDENTIFICADOR)) {
            throw new SyntaxException("Identificador esperado apos 'inicio'",
                    tokenAtual.getLinha(), tokenAtual.getColuna());
        }
        String nomeProg = tokenAtual.getLexema();
        avancar();

        consumir(TokenType.PONTO_VIRGULA);

        // Gerar cabeçalho do assembly
        gerador.gerarCabecalho(nomeProg);

        // Declarações
        declaracoes();

        // Gerar início do segmento de código
        gerador.gerarInicioCodigoSegmento();

        // Instruções
        instrucoes();

        consumir(TokenType.FIM);

        // Gerar finalização do assembly
        gerador.gerarFinalizacao();
    }

    /**
     * DECLARACOES -> (DECLARACAO ';')*
     * Declarações: ID [, ID]* : TIPO ;
     * Distingue de instrução: se após o ID vem ':=' é instrução, não declaração.
     */
    private void declaracoes() throws SyntaxException, SemanticException {
        while (verificar(TokenType.IDENTIFICADOR) && ehDeclaracao()) {
            declaracao();
            consumir(TokenType.PONTO_VIRGULA);
        }
    }

    /**
     * Lookahead para distinguir declaração de instrução.
     * Se o token atual é IDENTIFICADOR e é seguido de ',' ou ':', é declaração.
     * Se é seguido de ':=', é instrução (atribuição).
     */
    private boolean ehDeclaracao() {
        int lookPos = pos + 1;
        // Percorre possíveis IDs separados por vírgula
        while (lookPos < tokens.size()) {
            Token look = tokens.get(lookPos);
            if (look.getTipo() == TokenType.DOIS_PONTOS) {
                return true; // encontrou ':' -> é declaração
            } else if (look.getTipo() == TokenType.ATRIBUICAO) {
                return false; // encontrou ':=' -> é instrução
            } else if (look.getTipo() == TokenType.VIRGULA) {
                lookPos++; // pula ','
                lookPos++; // pula próximo ID
            } else {
                return false; // outro token -> não é declaração
            }
        }
        return false;
    }

    /**
     * DECLARACAO -> ID (',' ID)* ':' TIPO
     */
    private void declaracao() throws SyntaxException, SemanticException {
        List<String> ids = new ArrayList<>();

        if (!verificar(TokenType.IDENTIFICADOR)) {
            throw new SyntaxException("Identificador esperado na declaracao",
                    tokenAtual.getLinha(), tokenAtual.getColuna());
        }

        ids.add(tokenAtual.getLexema());
        int linhaDecl = tokenAtual.getLinha();
        int colunaDecl = tokenAtual.getColuna();
        avancar();

        while (verificar(TokenType.VIRGULA)) {
            avancar(); // consome ','
            if (!verificar(TokenType.IDENTIFICADOR)) {
                throw new SyntaxException("Identificador esperado apos ','",
                        tokenAtual.getLinha(), tokenAtual.getColuna());
            }
            ids.add(tokenAtual.getLexema());
            avancar();
        }

        consumir(TokenType.DOIS_PONTOS);

        if (!verificarTipo()) {
            throw new SyntaxException("Tipo esperado (inteiro, caractere, logico, real)",
                    tokenAtual.getLinha(), tokenAtual.getColuna());
        }

        TabelaSimbolos.TipoVariavel tipo = obterTipoVariavel(tokenAtual);
        avancar(); // consome o tipo

        // Análise semântica: registra variáveis na tabela
        for (String id : ids) {
            semantico.declararVariavel(id, tipo, linhaDecl, colunaDecl);
        }

        // Gerar declaração no assembly
        for (String id : ids) {
            gerador.gerarDeclaracaoVariavel(id, tipo);
        }
    }

    private TabelaSimbolos.TipoVariavel obterTipoVariavel(Token token) throws SyntaxException {
        switch (token.getTipo()) {
            case INTEIRO: return TabelaSimbolos.TipoVariavel.INTEIRO;
            case CARACTERE: return TabelaSimbolos.TipoVariavel.CARACTERE;
            case LOGICO: return TabelaSimbolos.TipoVariavel.LOGICO;
            case REAL: return TabelaSimbolos.TipoVariavel.REAL;
            default:
                throw new SyntaxException("Tipo invalido: " + token.getLexema(),
                        token.getLinha(), token.getColuna());
        }
    }

    /**
     * INSTRUCOES -> (INSTRUCAO (';' INSTRUCAO)*)?
     */
    private void instrucoes() throws SyntaxException, SemanticException {
        if (ehInicioInstrucao()) {
            instrucao();
            while (verificar(TokenType.PONTO_VIRGULA)) {
                avancar(); // consome ';'
                if (ehInicioInstrucao()) {
                    instrucao();
                }
            }
        }
    }

    private boolean ehInicioInstrucao() {
        return verificar(TokenType.IDENTIFICADOR) || verificar(TokenType.SE) ||
               verificar(TokenType.ENQUANTO) || verificar(TokenType.LEITURA) ||
               verificar(TokenType.ESCRITA);
    }

    /**
     * INSTRUCAO -> ATRIBUICAO | SE | ENQUANTO | LEITURA | ESCRITA
     */
    private void instrucao() throws SyntaxException, SemanticException {
        switch (tokenAtual.getTipo()) {
            case IDENTIFICADOR:
                atribuicao();
                break;
            case SE:
                se();
                break;
            case ENQUANTO:
                enquanto();
                break;
            case LEITURA:
                leitura();
                break;
            case ESCRITA:
                escrita();
                break;
            default:
                throw new SyntaxException("Instrucao esperada, encontrado '" +
                        tokenAtual.getLexema() + "'", tokenAtual.getLinha(), tokenAtual.getColuna());
        }
    }

    /**
     * ATRIBUICAO -> ID ':=' EXP
     */
    private void atribuicao() throws SyntaxException, SemanticException {
        String nomeVar = tokenAtual.getLexema();
        int linhaVar = tokenAtual.getLinha();
        int colunaVar = tokenAtual.getColuna();

        semantico.verificarDeclarada(nomeVar, linhaVar, colunaVar);
        avancar(); // consome ID

        consumir(TokenType.ATRIBUICAO);

        // Avalia a expressão e coloca resultado no topo da pilha/registrador
        TabelaSimbolos.TipoVariavel tipoExp = expressao();

        // Verifica compatibilidade de tipos
        semantico.verificarAtribuicao(nomeVar, tipoExp, linhaVar, colunaVar);

        // Gerar código de atribuição
        gerador.gerarAtribuicao(nomeVar, tabela.buscar(nomeVar).getTipo());

        tabela.buscar(nomeVar).setInicializado(true);
    }

    /**
     * SE -> 'se' EXP 'entao' 'inicio' INSTRUCOES 'fim' ('senao' 'inicio' INSTRUCOES 'fim')?
     */
    private void se() throws SyntaxException, SemanticException {
        consumir(TokenType.SE);

        String rotuloSenao = gerador.novoRotulo();
        String rotuloFim = gerador.novoRotulo();

        TabelaSimbolos.TipoVariavel tipoCond = expressao();
        semantico.verificarCondicao(tipoCond, tokenAtual.getLinha(), tokenAtual.getColuna());

        // Gerar salto condicional
        gerador.gerarSaltoCondicionalFalso(rotuloSenao);

        consumir(TokenType.ENTAO);
        consumir(TokenType.INICIO);
        instrucoes();
        consumir(TokenType.FIM);

        if (verificar(TokenType.SENAO)) {
            gerador.gerarSaltoIncondicional(rotuloFim);
            gerador.gerarRotulo(rotuloSenao);

            avancar(); // consome 'senao'
            consumir(TokenType.INICIO);
            instrucoes();
            consumir(TokenType.FIM);

            gerador.gerarRotulo(rotuloFim);
        } else {
            gerador.gerarRotulo(rotuloSenao);
        }
    }

    /**
     * ENQUANTO -> 'enquanto' EXP 'faca' 'inicio' INSTRUCOES 'fim'
     */
    private void enquanto() throws SyntaxException, SemanticException {
        consumir(TokenType.ENQUANTO);

        String rotuloInicio = gerador.novoRotulo();
        String rotuloFim = gerador.novoRotulo();

        gerador.gerarRotulo(rotuloInicio);

        TabelaSimbolos.TipoVariavel tipoCond = expressao();
        semantico.verificarCondicao(tipoCond, tokenAtual.getLinha(), tokenAtual.getColuna());

        gerador.gerarSaltoCondicionalFalso(rotuloFim);

        consumir(TokenType.FACA);
        consumir(TokenType.INICIO);
        instrucoes();
        consumir(TokenType.FIM);

        gerador.gerarSaltoIncondicional(rotuloInicio);
        gerador.gerarRotulo(rotuloFim);
    }

    /**
     * LEITURA -> 'leitura' '(' ID (',' ID)* ')'
     */
    private void leitura() throws SyntaxException, SemanticException {
        consumir(TokenType.LEITURA);
        consumir(TokenType.ABRE_PAREN);

        if (!verificar(TokenType.IDENTIFICADOR)) {
            throw new SyntaxException("Identificador esperado em leitura",
                    tokenAtual.getLinha(), tokenAtual.getColuna());
        }

        String nomeVar = tokenAtual.getLexema();
        semantico.verificarDeclarada(nomeVar, tokenAtual.getLinha(), tokenAtual.getColuna());
        gerador.gerarLeitura(nomeVar, tabela.buscar(nomeVar).getTipo());
        tabela.buscar(nomeVar).setInicializado(true);
        avancar();

        while (verificar(TokenType.VIRGULA)) {
            avancar(); // consome ','
            if (!verificar(TokenType.IDENTIFICADOR)) {
                throw new SyntaxException("Identificador esperado apos ','",
                        tokenAtual.getLinha(), tokenAtual.getColuna());
            }
            nomeVar = tokenAtual.getLexema();
            semantico.verificarDeclarada(nomeVar, tokenAtual.getLinha(), tokenAtual.getColuna());
            gerador.gerarLeitura(nomeVar, tabela.buscar(nomeVar).getTipo());
            tabela.buscar(nomeVar).setInicializado(true);
            avancar();
        }

        consumir(TokenType.FECHA_PAREN);
    }

    /**
     * ESCRITA -> 'escrita' '(' EXP ')'
     */
    private void escrita() throws SyntaxException, SemanticException {
        consumir(TokenType.ESCRITA);
        consumir(TokenType.ABRE_PAREN);

        TabelaSimbolos.TipoVariavel tipoExp = expressao();
        gerador.gerarEscrita(tipoExp);

        consumir(TokenType.FECHA_PAREN);
    }

    // =================== EXPRESSÕES ===================

    /**
     * EXP -> EXP_SIMPLES (RELOP EXP_SIMPLES)?
     * RELOP: == | <> | < | > | <= | >=
     */
    private TabelaSimbolos.TipoVariavel expressao() throws SyntaxException, SemanticException {
        TabelaSimbolos.TipoVariavel tipoEsq = expressaoSimples();

        if (ehOperadorRelacional()) {
            String op = tokenAtual.getLexema();
            int linhaOp = tokenAtual.getLinha();
            int colunaOp = tokenAtual.getColuna();
            avancar(); // consome operador

            TabelaSimbolos.TipoVariavel tipoDir = expressaoSimples();
            semantico.verificarOperacaoRelacional(tipoEsq, op, tipoDir, linhaOp, colunaOp);
            gerador.gerarOperacaoRelacional(op);
            return TabelaSimbolos.TipoVariavel.LOGICO;
        }

        return tipoEsq;
    }

    private boolean ehOperadorRelacional() {
        return verificar(TokenType.IGUAL) || verificar(TokenType.DIFERENTE) ||
               verificar(TokenType.MENOR) || verificar(TokenType.MAIOR) ||
               verificar(TokenType.MENOR_IGUAL) || verificar(TokenType.MAIOR_IGUAL);
    }

    /**
     * EXP_SIMPLES -> TERMO (('+' | '-' | 'ou') TERMO)*
     */
    private TabelaSimbolos.TipoVariavel expressaoSimples() throws SyntaxException, SemanticException {
        TabelaSimbolos.TipoVariavel tipo = termo();

        while (verificar(TokenType.MAIS) || verificar(TokenType.MENOS) || verificar(TokenType.OU)) {
            String op = tokenAtual.getLexema();
            int linhaOp = tokenAtual.getLinha();
            int colunaOp = tokenAtual.getColuna();
            avancar(); // consome operador

            TabelaSimbolos.TipoVariavel tipoDir = termo();
            tipo = semantico.verificarOperacaoAditiva(tipo, op, tipoDir, linhaOp, colunaOp);
            gerador.gerarOperacaoAritmetica(op);
        }

        return tipo;
    }

    /**
     * TERMO -> FATOR (('*' | '/' | 'div' | 'mod' | '&&') FATOR)*
     */
    private TabelaSimbolos.TipoVariavel termo() throws SyntaxException, SemanticException {
        TabelaSimbolos.TipoVariavel tipo = fator();

        while (verificar(TokenType.VEZES) || verificar(TokenType.BARRA) ||
               verificar(TokenType.DIV) || verificar(TokenType.MOD) ||
               verificar(TokenType.E_LOGICO)) {
            String op = tokenAtual.getLexema();
            int linhaOp = tokenAtual.getLinha();
            int colunaOp = tokenAtual.getColuna();
            avancar(); // consome operador

            TabelaSimbolos.TipoVariavel tipoDir = fator();
            tipo = semantico.verificarOperacaoMultiplicativa(tipo, op, tipoDir, linhaOp, colunaOp);
            gerador.gerarOperacaoAritmetica(op);
        }

        return tipo;
    }

    /**
     * FATOR -> ID | CONST | '(' EXP ')' | 'nao' FATOR | ('+' | '-') FATOR
     */
    private TabelaSimbolos.TipoVariavel fator() throws SyntaxException, SemanticException {
        switch (tokenAtual.getTipo()) {
            case IDENTIFICADOR: {
                String nome = tokenAtual.getLexema();
                semantico.verificarDeclarada(nome, tokenAtual.getLinha(), tokenAtual.getColuna());
                TabelaSimbolos.Simbolo simbolo = tabela.buscar(nome);
                gerador.gerarCarregarVariavel(nome, simbolo.getTipo());
                avancar();
                return simbolo.getTipo();
            }

            case CONST_INTEIRO: {
                String valor = tokenAtual.getLexema();
                gerador.gerarCarregarConstanteInteira(valor);
                avancar();
                return TabelaSimbolos.TipoVariavel.INTEIRO;
            }

            case CONST_REAL: {
                String valor = tokenAtual.getLexema();
                gerador.gerarCarregarConstanteReal(valor);
                avancar();
                return TabelaSimbolos.TipoVariavel.REAL;
            }

            case CONST_CARACTERE: {
                String valor = tokenAtual.getLexema();
                gerador.gerarCarregarConstanteCaractere(valor);
                avancar();
                return TabelaSimbolos.TipoVariavel.CARACTERE;
            }

            case CONST_LOGICO:
            case VERDADEIRO:
            case FALSO: {
                String valor = tokenAtual.getLexema();
                gerador.gerarCarregarConstanteLogica(valor);
                avancar();
                return TabelaSimbolos.TipoVariavel.LOGICO;
            }

            case ABRE_PAREN: {
                avancar(); // consome '('
                TabelaSimbolos.TipoVariavel tipo = expressao();
                consumir(TokenType.FECHA_PAREN);
                return tipo;
            }

            case NAO: {
                int linhaNao = tokenAtual.getLinha();
                int colunaNao = tokenAtual.getColuna();
                avancar(); // consome 'nao'
                TabelaSimbolos.TipoVariavel tipo = fator();
                semantico.verificarNegacaoLogica(tipo, linhaNao, colunaNao);
                gerador.gerarNegacaoLogica();
                return TabelaSimbolos.TipoVariavel.LOGICO;
            }

            case MAIS:
            case MENOS: {
                String op = tokenAtual.getLexema();
                int linhaOp = tokenAtual.getLinha();
                int colunaOp = tokenAtual.getColuna();
                avancar(); // consome '+' ou '-'
                TabelaSimbolos.TipoVariavel tipo = fator();
                semantico.verificarOperadorUnario(tipo, op, linhaOp, colunaOp);
                if (op.equals("-")) {
                    gerador.gerarNegacaoAritmetica();
                }
                return tipo;
            }

            default:
                throw new SyntaxException("Fator esperado, encontrado '" +
                        tokenAtual.getLexema() + "'", tokenAtual.getLinha(), tokenAtual.getColuna());
        }
    }
}
