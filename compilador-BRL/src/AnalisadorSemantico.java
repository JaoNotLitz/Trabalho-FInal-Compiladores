import exceptions.SemanticException;

/**
 * Compilador BRL - Analisador Semântico
 * Realiza verificações de tipos e uso correto de variáveis.
 */
public class AnalisadorSemantico {

    private TabelaSimbolos tabela;

    public AnalisadorSemantico(TabelaSimbolos tabela) {
        this.tabela = tabela;
    }

    /**
     * Declara uma variável na tabela de símbolos.
     */
    public void declararVariavel(String nome, TabelaSimbolos.TipoVariavel tipo,
                                  int linha, int coluna) throws SemanticException {
        if (tabela.estaDeclarado(nome)) {
            throw new SemanticException("Identificador '" + nome + "' ja declarado", linha, coluna);
        }
        tabela.adicionar(nome, tipo);
    }

    /**
     * Verifica se uma variável foi declarada.
     */
    public void verificarDeclarada(String nome, int linha, int coluna) throws SemanticException {
        if (!tabela.estaDeclarado(nome)) {
            throw new SemanticException("Identificador '" + nome + "' nao declarado", linha, coluna);
        }
    }

    /**
     * Verifica compatibilidade de tipos em atribuição.
     * inteiro := inteiro ✓
     * real := real ✓
     * real := inteiro ✓ (promoção)
     * caractere := caractere ✓
     * logico := logico ✓
     */
    public void verificarAtribuicao(String nomeVar, TabelaSimbolos.TipoVariavel tipoExp,
                                     int linha, int coluna) throws SemanticException {
        TabelaSimbolos.Simbolo simbolo = tabela.buscar(nomeVar);
        TabelaSimbolos.TipoVariavel tipoVar = simbolo.getTipo();

        if (tipoVar == tipoExp) return; // tipos iguais, ok

        // Promoção: inteiro pode ser atribuído a real
        if (tipoVar == TabelaSimbolos.TipoVariavel.REAL &&
            tipoExp == TabelaSimbolos.TipoVariavel.INTEIRO) {
            return;
        }

        throw new SemanticException("Tipos incompativeis na atribuicao: variavel '" + nomeVar +
                "' eh do tipo " + tipoVar + " mas expressao eh do tipo " + tipoExp, linha, coluna);
    }

    /**
     * Verifica se a expressão de condição é do tipo lógico.
     */
    public void verificarCondicao(TabelaSimbolos.TipoVariavel tipo, int linha, int coluna)
            throws SemanticException {
        if (tipo != TabelaSimbolos.TipoVariavel.LOGICO) {
            throw new SemanticException("Expressao condicional deve ser do tipo logico, encontrado: " + tipo,
                    linha, coluna);
        }
    }

    /**
     * Verifica operação relacional (==, <>, <, >, <=, >=).
     * Comparação aritmética: inteiro/real com inteiro/real
     * Comparação de igualdade entre caracteres: caractere == caractere
     */
    public void verificarOperacaoRelacional(TabelaSimbolos.TipoVariavel tipoEsq, String op,
                                             TabelaSimbolos.TipoVariavel tipoDir,
                                             int linha, int coluna) throws SemanticException {
        // Comparação entre caracteres (apenas == permitido)
        if (tipoEsq == TabelaSimbolos.TipoVariavel.CARACTERE ||
            tipoDir == TabelaSimbolos.TipoVariavel.CARACTERE) {
            if (!op.equals("==")) {
                throw new SemanticException("Apenas operador '==' permitido para caracteres", linha, coluna);
            }
            if (tipoEsq != TabelaSimbolos.TipoVariavel.CARACTERE ||
                tipoDir != TabelaSimbolos.TipoVariavel.CARACTERE) {
                throw new SemanticException("Comparacao entre caractere e outro tipo nao permitida", linha, coluna);
            }
            return;
        }

        // Comparação entre tipos numéricos (inteiro/real)
        if (ehNumerico(tipoEsq) && ehNumerico(tipoDir)) {
            return;
        }

        // Comparação entre lógicos (apenas == e <>)
        if (tipoEsq == TabelaSimbolos.TipoVariavel.LOGICO &&
            tipoDir == TabelaSimbolos.TipoVariavel.LOGICO) {
            if (op.equals("==") || op.equals("<>")) {
                return;
            }
            throw new SemanticException("Operador '" + op + "' nao permitido para tipos logicos", linha, coluna);
        }

        throw new SemanticException("Tipos incompativeis em operacao relacional: " +
                tipoEsq + " " + op + " " + tipoDir, linha, coluna);
    }

    /**
     * Verifica operação aditiva (+, -, ou).
     * + entre numéricos: resultado numérico
     * + entre caracteres: concatenação → caractere
     * - entre numéricos: resultado numérico
     * ou entre lógicos: resultado lógico
     */
    public TabelaSimbolos.TipoVariavel verificarOperacaoAditiva(
            TabelaSimbolos.TipoVariavel tipoEsq, String op,
            TabelaSimbolos.TipoVariavel tipoDir,
            int linha, int coluna) throws SemanticException {

        if (op.equals("ou")) {
            if (tipoEsq == TabelaSimbolos.TipoVariavel.LOGICO &&
                tipoDir == TabelaSimbolos.TipoVariavel.LOGICO) {
                return TabelaSimbolos.TipoVariavel.LOGICO;
            }
            throw new SemanticException("Operador 'ou' exige operandos logicos", linha, coluna);
        }

        if (op.equals("+")) {
            // Concatenação de caracteres
            if (tipoEsq == TabelaSimbolos.TipoVariavel.CARACTERE &&
                tipoDir == TabelaSimbolos.TipoVariavel.CARACTERE) {
                return TabelaSimbolos.TipoVariavel.CARACTERE;
            }
            // Adição numérica
            if (ehNumerico(tipoEsq) && ehNumerico(tipoDir)) {
                return promoverTipo(tipoEsq, tipoDir);
            }
            throw new SemanticException("Tipos incompativeis com operador '+'", linha, coluna);
        }

        if (op.equals("-")) {
            if (ehNumerico(tipoEsq) && ehNumerico(tipoDir)) {
                return promoverTipo(tipoEsq, tipoDir);
            }
            throw new SemanticException("Operador '-' exige operandos numericos", linha, coluna);
        }

        throw new SemanticException("Operador aditivo desconhecido: '" + op + "'", linha, coluna);
    }

    /**
     * Verifica operação multiplicativa (*, /, div, mod, &&).
     * *, / entre numéricos: resultado numérico (/ com real se algum for real)
     * div, mod entre inteiros: resultado inteiro
     * && entre lógicos: resultado lógico
     */
    public TabelaSimbolos.TipoVariavel verificarOperacaoMultiplicativa(
            TabelaSimbolos.TipoVariavel tipoEsq, String op,
            TabelaSimbolos.TipoVariavel tipoDir,
            int linha, int coluna) throws SemanticException {

        if (op.equals("&&")) {
            if (tipoEsq == TabelaSimbolos.TipoVariavel.LOGICO &&
                tipoDir == TabelaSimbolos.TipoVariavel.LOGICO) {
                return TabelaSimbolos.TipoVariavel.LOGICO;
            }
            throw new SemanticException("Operador '&&' exige operandos logicos", linha, coluna);
        }

        if (op.equals("*")) {
            if (ehNumerico(tipoEsq) && ehNumerico(tipoDir)) {
                return promoverTipo(tipoEsq, tipoDir);
            }
            throw new SemanticException("Operador '*' exige operandos numericos", linha, coluna);
        }

        if (op.equals("/")) {
            if (ehNumerico(tipoEsq) && ehNumerico(tipoDir)) {
                // Divisão real sempre resulta em real
                return TabelaSimbolos.TipoVariavel.REAL;
            }
            throw new SemanticException("Operador '/' exige operandos numericos", linha, coluna);
        }

        if (op.equals("div") || op.equals("mod")) {
            if (tipoEsq == TabelaSimbolos.TipoVariavel.INTEIRO &&
                tipoDir == TabelaSimbolos.TipoVariavel.INTEIRO) {
                return TabelaSimbolos.TipoVariavel.INTEIRO;
            }
            throw new SemanticException("Operador '" + op + "' exige operandos inteiros", linha, coluna);
        }

        throw new SemanticException("Operador multiplicativo desconhecido: '" + op + "'", linha, coluna);
    }

    /**
     * Verifica negação lógica (nao).
     */
    public void verificarNegacaoLogica(TabelaSimbolos.TipoVariavel tipo, int linha, int coluna)
            throws SemanticException {
        if (tipo != TabelaSimbolos.TipoVariavel.LOGICO) {
            throw new SemanticException("Operador 'nao' exige operando logico, encontrado: " + tipo,
                    linha, coluna);
        }
    }

    /**
     * Verifica operador unário (+ ou -).
     */
    public void verificarOperadorUnario(TabelaSimbolos.TipoVariavel tipo, String op,
                                         int linha, int coluna) throws SemanticException {
        if (!ehNumerico(tipo)) {
            throw new SemanticException("Operador unario '" + op + "' exige operando numerico", linha, coluna);
        }
    }

    // =================== AUXILIARES ===================

    private boolean ehNumerico(TabelaSimbolos.TipoVariavel tipo) {
        return tipo == TabelaSimbolos.TipoVariavel.INTEIRO ||
               tipo == TabelaSimbolos.TipoVariavel.REAL;
    }

    /**
     * Promoção de tipo: se algum for real, resultado é real.
     */
    private TabelaSimbolos.TipoVariavel promoverTipo(TabelaSimbolos.TipoVariavel t1,
                                                      TabelaSimbolos.TipoVariavel t2) {
        if (t1 == TabelaSimbolos.TipoVariavel.REAL || t2 == TabelaSimbolos.TipoVariavel.REAL) {
            return TabelaSimbolos.TipoVariavel.REAL;
        }
        return TabelaSimbolos.TipoVariavel.INTEIRO;
    }
}
