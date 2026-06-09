package exceptions;

/**
 * Compilador BRL - Exceção de análise sintática
 */
public class SyntaxException extends CompiladorException {

    public SyntaxException(String mensagem, int linha, int coluna) {
        super(mensagem, linha, coluna);
    }

    public SyntaxException(String mensagem) {
        super(mensagem);
    }
}
