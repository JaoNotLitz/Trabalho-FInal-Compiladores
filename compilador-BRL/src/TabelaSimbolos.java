import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Compilador BRL - Tabela de Símbolos
 * Armazena informações sobre identificadores declarados no programa fonte.
 */
public class TabelaSimbolos {

    public enum TipoVariavel {
        INTEIRO,    // 4 bytes (32 bits), -2^31 a 2^31-1
        CARACTERE,  // arranjo até 255 chars + '$', 512 bytes
        LOGICO,     // 1 byte (0h = false, FFh = true)
        REAL        // 4 bytes (32 bits), ponto flutuante
    }

    public static class Simbolo {
        private String nome;
        private TipoVariavel tipo;
        private int endereco;       // offset na memória para geração de código
        private boolean inicializado;

        public Simbolo(String nome, TipoVariavel tipo, int endereco) {
            this.nome = nome;
            this.tipo = tipo;
            this.endereco = endereco;
            this.inicializado = false;
        }

        public String getNome() { return nome; }
        public TipoVariavel getTipo() { return tipo; }
        public int getEndereco() { return endereco; }
        public boolean isInicializado() { return inicializado; }
        public void setInicializado(boolean inicializado) { this.inicializado = inicializado; }

        public int getTamanhoBytes() {
            switch (tipo) {
                case INTEIRO: return 4;
                case CARACTERE: return 512;
                case LOGICO: return 1;
                case REAL: return 4;
                default: return 0;
            }
        }

        @Override
        public String toString() {
            return "Simbolo{nome='" + nome + "', tipo=" + tipo +
                   ", endereco=" + endereco + ", inicializado=" + inicializado + "}";
        }
    }

    private Map<String, Simbolo> simbolos;
    private int proximoEndereco;

    public TabelaSimbolos() {
        this.simbolos = new LinkedHashMap<>();
        this.proximoEndereco = 0;
    }

    /**
     * Adiciona um símbolo à tabela.
     * Retorna true se adicionado com sucesso, false se já existe.
     */
    public boolean adicionar(String nome, TipoVariavel tipo) {
        if (simbolos.containsKey(nome)) {
            return false; // já declarado
        }
        Simbolo s = new Simbolo(nome, tipo, proximoEndereco);
        proximoEndereco += s.getTamanhoBytes();
        simbolos.put(nome, s);
        return true;
    }

    /**
     * Busca um símbolo na tabela.
     */
    public Simbolo buscar(String nome) {
        return simbolos.get(nome);
    }

    /**
     * Verifica se um símbolo está declarado.
     */
    public boolean estaDeclarado(String nome) {
        return simbolos.containsKey(nome);
    }

    /**
     * Retorna todos os símbolos.
     */
    public Map<String, Simbolo> getTodosSimbolos() {
        return simbolos;
    }

    /**
     * Retorna o total de bytes usados pelas variáveis.
     */
    public int getTotalBytes() {
        return proximoEndereco;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("=== Tabela de Simbolos ===\n");
        for (Simbolo s : simbolos.values()) {
            sb.append("  ").append(s).append("\n");
        }
        sb.append("Total: ").append(proximoEndereco).append(" bytes\n");
        return sb.toString();
    }
}
