package exceptions;

/**
 * Compilador BRL - Exceção de análise semântica
 */
public class SemanticException extends CompiladorException {

    public SemanticException(String mensagem, int linha, int coluna) {
        super(mensagem, linha, coluna);
    }

    public SemanticException(String mensagem) {
        super(mensagem);
    }
}
