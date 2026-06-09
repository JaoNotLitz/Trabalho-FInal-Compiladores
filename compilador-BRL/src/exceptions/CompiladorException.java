package exceptions;

/**
 * Compilador BRL - Exceção base do compilador
 */
public class CompiladorException extends Exception {
    private int linha;
    private int coluna;

    public CompiladorException(String mensagem, int linha, int coluna) {
        super(mensagem);
        this.linha = linha;
        this.coluna = coluna;
    }

    public CompiladorException(String mensagem) {
        super(mensagem);
        this.linha = -1;
        this.coluna = -1;
    }

    public int getLinha() {
        return linha;
    }

    public int getColuna() {
        return coluna;
    }

    @Override
    public String getMessage() {
        if (linha >= 0) {
            return super.getMessage() + " [linha: " + linha + ", coluna: " + coluna + "]";
        }
        return super.getMessage();
    }
}
