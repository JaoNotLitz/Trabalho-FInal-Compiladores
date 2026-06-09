/**
 * Compilador BRL - Trabalho Prático de Compiladores
 * Classe Token - Representa um token da linguagem BRL
 */
public class Token {
    private String lexema;
    private TokenType tipo;
    private int linha;
    private int coluna;

    public Token(String lexema, TokenType tipo, int linha, int coluna) {
        this.lexema = lexema;
        this.tipo = tipo;
        this.linha = linha;
        this.coluna = coluna;
    }

    public String getLexema() {
        return lexema;
    }

    public TokenType getTipo() {
        return tipo;
    }

    public int getLinha() {
        return linha;
    }

    public int getColuna() {
        return coluna;
    }

    @Override
    public String toString() {
        return "Token{lexema='" + lexema + "', tipo=" + tipo + ", linha=" + linha + ", coluna=" + coluna + "}";
    }
}
