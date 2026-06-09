package exceptions;

/**
 * Compilador BRL - Exceção de análise léxica
 */
public class LexicalException extends CompiladorException {

    public LexicalException(String mensagem, int linha, int coluna) {
        super(mensagem, linha, coluna);
    }

    public LexicalException(String mensagem) {
        super(mensagem);
    }
}
