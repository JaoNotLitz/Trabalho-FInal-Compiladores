import java.io.*;

/**
 * Compilador BRL - Gerador de Código Assembly (MASM 80x86)
 * Gera código Assembly compatível com o montador MASM.
 * Utiliza modelo de pilha para avaliação de expressões.
 */
public class GeradorCodigo {

    private StringBuilder segmentoDados;
    private StringBuilder segmentoCodigo;
    private StringBuilder segmentoStrings; // para strings literais
    private int contadorRotulo;
    private int contadorString;
    private TabelaSimbolos tabela;

    public GeradorCodigo(TabelaSimbolos tabela) {
        this.segmentoDados = new StringBuilder();
        this.segmentoCodigo = new StringBuilder();
        this.segmentoStrings = new StringBuilder();
        this.contadorRotulo = 0;
        this.contadorString = 0;
        this.tabela = tabela;
    }

    /**
     * Gera um novo rótulo único.
     */
    public String novoRotulo() {
        return "R" + (contadorRotulo++);
    }

    private String novaStringLiteral() {
        return "str" + (contadorString++);
    }

    // =================== GERAÇÃO DE CABEÇALHO E ESTRUTURA ===================

    /**
     * Gera o cabeçalho do programa assembly.
     */
    public void gerarCabecalho(String nomeProg) {
        segmentoDados.append("; Programa: ").append(nomeProg).append("\n");
        segmentoDados.append("; Gerado pelo compilador BRL\n\n");
        segmentoDados.append("dseg SEGMENT PUBLIC\n");
        segmentoDados.append("    byte 4000h DUP(?)  ; area temporaria\n");
    }

    /**
     * Gera declaração de variável no segmento de dados.
     */
    public void gerarDeclaracaoVariavel(String nome, TabelaSimbolos.TipoVariavel tipo) {
        switch (tipo) {
            case INTEIRO:
                segmentoDados.append("    ").append(nome).append(" SWORD ?\n");
                break;
            case CARACTERE:
                segmentoDados.append("    ").append(nome).append(" byte 512 DUP(?)  ; caractere (512 bytes)\n");
                break;
            case LOGICO:
                segmentoDados.append("    ").append(nome).append(" byte ?\n");
                break;
            case REAL:
                segmentoDados.append("    ").append(nome).append(" SDWORD ?\n");
                break;
        }
    }

    /**
     * Gera início do segmento de código.
     */
    public void gerarInicioCodigoSegmento() {
        segmentoDados.append("dseg ENDS\n\n");
        segmentoCodigo.append("cseg SEGMENT PUBLIC\n");
        segmentoCodigo.append("    ASSUME CS:cseg, DS:dseg\n\n");
        segmentoCodigo.append("strt:\n");
        segmentoCodigo.append("    mov ax, dseg\n");
        segmentoCodigo.append("    mov ds, ax\n\n");
    }

    /**
     * Gera finalização do assembly.
     */
    public void gerarFinalizacao() {
        segmentoCodigo.append("\n    ; Finalizar programa\n");
        segmentoCodigo.append("    mov ah, 4Ch\n");
        segmentoCodigo.append("    int 21h\n\n");
        segmentoCodigo.append("cseg ENDS\n");
        segmentoCodigo.append("END strt\n");
    }

    // =================== GERAÇÃO DE EXPRESSÕES ===================

    /**
     * Carrega variável no registrador (topo da pilha).
     */
    public void gerarCarregarVariavel(String nome, TabelaSimbolos.TipoVariavel tipo) {
        switch (tipo) {
            case INTEIRO:
            case REAL:
                segmentoCodigo.append("    mov ax, DS:").append(nome).append("\n");
                segmentoCodigo.append("    push ax\n");
                break;
            case LOGICO:
                segmentoCodigo.append("    mov al, DS:").append(nome).append("\n");
                segmentoCodigo.append("    cbw\n");
                segmentoCodigo.append("    push ax\n");
                break;
            case CARACTERE:
                segmentoCodigo.append("    lea dx, DS:").append(nome).append("\n");
                segmentoCodigo.append("    push dx\n");
                break;
        }
    }

    /**
     * Carrega constante inteira no topo da pilha.
     */
    public void gerarCarregarConstanteInteira(String valor) {
        segmentoCodigo.append("    mov ax, ").append(valor).append("\n");
        segmentoCodigo.append("    push ax\n");
    }

    /**
     * Carrega constante real no topo da pilha.
     */
    public void gerarCarregarConstanteReal(String valor) {
        // Para simplificar, representamos real como inteiro escalado
        // Em MASM completo, usaria FPU (fld, fstp, etc.)
        segmentoCodigo.append("    ; carregar real ").append(valor).append("\n");
        segmentoCodigo.append("    mov ax, ").append(converterRealParaInteiro(valor)).append("\n");
        segmentoCodigo.append("    push ax\n");
    }

    /**
     * Carrega constante caractere (string) no topo da pilha.
     */
    public void gerarCarregarConstanteCaractere(String valor) {
        String nomeStr = novaStringLiteral();
        // Remove aspas e adiciona terminador '$'
        String conteudo = valor.substring(1, valor.length() - 1);
        segmentoStrings.append("    ").append(nomeStr).append(" byte \"")
                .append(conteudo).append("\", '$'\n");
        segmentoCodigo.append("    lea dx, DS:").append(nomeStr).append("\n");
        segmentoCodigo.append("    push dx\n");
    }

    /**
     * Carrega constante lógica no topo da pilha.
     */
    public void gerarCarregarConstanteLogica(String valor) {
        if (valor.equals("verdadeiro")) {
            segmentoCodigo.append("    mov ax, 0FFh  ; verdadeiro\n");
        } else {
            segmentoCodigo.append("    mov ax, 0     ; falso\n");
        }
        segmentoCodigo.append("    push ax\n");
    }

    // =================== OPERAÇÕES ===================

    /**
     * Gera código para operação aritmética.
     */
    public void gerarOperacaoAritmetica(String op) {
        segmentoCodigo.append("    pop bx          ; operando direito\n");
        segmentoCodigo.append("    pop ax          ; operando esquerdo\n");

        switch (op) {
            case "+":
                segmentoCodigo.append("    add ax, bx\n");
                break;
            case "-":
                segmentoCodigo.append("    sub ax, bx\n");
                break;
            case "*":
                segmentoCodigo.append("    imul bx\n");
                break;
            case "/":
                segmentoCodigo.append("    cwd\n");
                segmentoCodigo.append("    idiv bx\n");
                break;
            case "div":
                segmentoCodigo.append("    cwd\n");
                segmentoCodigo.append("    idiv bx      ; quociente em ax\n");
                break;
            case "mod":
                segmentoCodigo.append("    cwd\n");
                segmentoCodigo.append("    idiv bx      ; resto em dx\n");
                segmentoCodigo.append("    mov ax, dx\n");
                break;
            case "&&":
                segmentoCodigo.append("    and ax, bx   ; E logico\n");
                break;
            case "ou":
                segmentoCodigo.append("    or ax, bx    ; OU logico\n");
                break;
        }

        segmentoCodigo.append("    push ax\n");
    }

    /**
     * Gera código para operação relacional.
     */
    public void gerarOperacaoRelacional(String op) {
        String rotuloVerd = novoRotulo();
        String rotuloFim = novoRotulo();

        segmentoCodigo.append("    pop bx          ; operando direito\n");
        segmentoCodigo.append("    pop ax          ; operando esquerdo\n");
        segmentoCodigo.append("    cmp ax, bx\n");

        switch (op) {
            case "==":
                segmentoCodigo.append("    je ").append(rotuloVerd).append("\n");
                break;
            case "<>":
                segmentoCodigo.append("    jne ").append(rotuloVerd).append("\n");
                break;
            case "<":
                segmentoCodigo.append("    jl ").append(rotuloVerd).append("\n");
                break;
            case ">":
                segmentoCodigo.append("    jg ").append(rotuloVerd).append("\n");
                break;
            case "<=":
                segmentoCodigo.append("    jle ").append(rotuloVerd).append("\n");
                break;
            case ">=":
                segmentoCodigo.append("    jge ").append(rotuloVerd).append("\n");
                break;
        }

        // Falso
        segmentoCodigo.append("    mov ax, 0       ; falso\n");
        segmentoCodigo.append("    jmp ").append(rotuloFim).append("\n");
        // Verdadeiro
        segmentoCodigo.append(rotuloVerd).append(":\n");
        segmentoCodigo.append("    mov ax, 0FFh    ; verdadeiro\n");
        segmentoCodigo.append(rotuloFim).append(":\n");
        segmentoCodigo.append("    push ax\n");
    }

    /**
     * Gera negação lógica (nao).
     */
    public void gerarNegacaoLogica() {
        segmentoCodigo.append("    pop ax\n");
        segmentoCodigo.append("    not ax          ; negacao logica\n");
        segmentoCodigo.append("    push ax\n");
    }

    /**
     * Gera negação aritmética (- unário).
     */
    public void gerarNegacaoAritmetica() {
        segmentoCodigo.append("    pop ax\n");
        segmentoCodigo.append("    neg ax          ; negacao aritmetica\n");
        segmentoCodigo.append("    push ax\n");
    }

    // =================== ATRIBUIÇÃO ===================

    /**
     * Gera código de atribuição (pop do topo e armazena na variável).
     */
    public void gerarAtribuicao(String nome, TabelaSimbolos.TipoVariavel tipo) {
        switch (tipo) {
            case INTEIRO:
            case REAL:
                segmentoCodigo.append("    pop ax\n");
                segmentoCodigo.append("    mov DS:").append(nome).append(", ax\n");
                break;
            case LOGICO:
                segmentoCodigo.append("    pop ax\n");
                segmentoCodigo.append("    mov DS:").append(nome).append(", al\n");
                break;
            case CARACTERE:
                // Copia string do endereço no topo da pilha para a variável
                segmentoCodigo.append("    pop si         ; endereco fonte\n");
                segmentoCodigo.append("    lea di, DS:").append(nome).append("\n");
                segmentoCodigo.append("    ; copiar string\n");
                String rotuloCopia = novoRotulo();
                String rotuloFimCopia = novoRotulo();
                segmentoCodigo.append(rotuloCopia).append(":\n");
                segmentoCodigo.append("    mov al, [si]\n");
                segmentoCodigo.append("    mov [di], al\n");
                segmentoCodigo.append("    cmp al, '$'\n");
                segmentoCodigo.append("    je ").append(rotuloFimCopia).append("\n");
                segmentoCodigo.append("    inc si\n");
                segmentoCodigo.append("    inc di\n");
                segmentoCodigo.append("    jmp ").append(rotuloCopia).append("\n");
                segmentoCodigo.append(rotuloFimCopia).append(":\n");
                break;
        }
    }

    // =================== CONTROLE DE FLUXO ===================

    /**
     * Gera salto condicional para falso (se topo da pilha == 0).
     */
    public void gerarSaltoCondicionalFalso(String rotulo) {
        segmentoCodigo.append("    pop ax\n");
        segmentoCodigo.append("    cmp ax, 0\n");
        segmentoCodigo.append("    je ").append(rotulo).append("\n");
    }

    /**
     * Gera salto incondicional.
     */
    public void gerarSaltoIncondicional(String rotulo) {
        segmentoCodigo.append("    jmp ").append(rotulo).append("\n");
    }

    /**
     * Gera rótulo no código.
     */
    public void gerarRotulo(String rotulo) {
        segmentoCodigo.append(rotulo).append(":\n");
    }

    // =================== LEITURA E ESCRITA ===================

    /**
     * Gera código para leitura de variável (leitura do teclado).
     */
    public void gerarLeitura(String nome, TabelaSimbolos.TipoVariavel tipo) {
        switch (tipo) {
            case INTEIRO:
            case REAL:
                // Leitura de número inteiro via DOS
                segmentoCodigo.append("    ; Leitura de inteiro para ").append(nome).append("\n");
                segmentoCodigo.append("    lea dx, DS:").append(nome).append("\n");
                segmentoCodigo.append("    mov ah, 0Ah   ; leitura buffered\n");
                segmentoCodigo.append("    int 21h\n");
                segmentoCodigo.append("    ; Converter ASCII para inteiro e armazenar\n");
                break;
            case CARACTERE:
                // Leitura de string via DOS
                segmentoCodigo.append("    ; Leitura de string para ").append(nome).append("\n");
                segmentoCodigo.append("    lea dx, DS:").append(nome).append("\n");
                segmentoCodigo.append("    mov ah, 0Ah   ; leitura buffered\n");
                segmentoCodigo.append("    int 21h\n");
                break;
            case LOGICO:
                segmentoCodigo.append("    ; Leitura de logico para ").append(nome).append("\n");
                segmentoCodigo.append("    mov ah, 01h   ; leitura de caractere\n");
                segmentoCodigo.append("    int 21h\n");
                segmentoCodigo.append("    mov DS:").append(nome).append(", al\n");
                break;
        }
    }

    /**
     * Gera código para escrita de expressão (saída para tela).
     */
    public void gerarEscrita(TabelaSimbolos.TipoVariavel tipo) {
        switch (tipo) {
            case INTEIRO:
            case REAL:
                segmentoCodigo.append("    ; Escrita de inteiro\n");
                segmentoCodigo.append("    pop ax\n");
                segmentoCodigo.append("    ; Converter inteiro para ASCII e imprimir\n");
                segmentoCodigo.append("    call write_int\n");
                break;
            case CARACTERE:
                segmentoCodigo.append("    ; Escrita de string\n");
                segmentoCodigo.append("    pop dx\n");
                segmentoCodigo.append("    mov ah, 09h   ; DOS print string\n");
                segmentoCodigo.append("    int 21h\n");
                break;
            case LOGICO:
                segmentoCodigo.append("    ; Escrita de logico\n");
                segmentoCodigo.append("    pop ax\n");
                segmentoCodigo.append("    cmp ax, 0\n");
                String rotuloVerd = novoRotulo();
                String rotuloFim = novoRotulo();
                segmentoCodigo.append("    jne ").append(rotuloVerd).append("\n");
                segmentoCodigo.append("    lea dx, msg_falso\n");
                segmentoCodigo.append("    jmp ").append(rotuloFim).append("\n");
                segmentoCodigo.append(rotuloVerd).append(":\n");
                segmentoCodigo.append("    lea dx, msg_verdadeiro\n");
                segmentoCodigo.append(rotuloFim).append(":\n");
                segmentoCodigo.append("    mov ah, 09h\n");
                segmentoCodigo.append("    int 21h\n");
                break;
        }
    }

    // =================== ESCRITA DO ARQUIVO ===================

    /**
     * Escreve o código assembly completo no arquivo de saída.
     */
    public void escreverArquivo(String caminhoSaida) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(caminhoSaida))) {
            // Segmento de dados
            writer.print(segmentoDados.toString());

            // Strings literais
            if (segmentoStrings.length() > 0) {
                writer.println("    ; Strings literais");
                writer.print(segmentoStrings.toString());
            }

            // Mensagens para saída de lógicos
            writer.println("    msg_verdadeiro byte \"verdadeiro\", '$'");
            writer.println("    msg_falso byte \"falso\", '$'");
            writer.println();

            // Segmento de código
            writer.print(segmentoCodigo.toString());
        }
    }

    // =================== AUXILIARES ===================

    /**
     * Converte valor real string para representação inteira simplificada.
     */
    private String converterRealParaInteiro(String valor) {
        try {
            float f = Float.parseFloat(valor);
            int bits = Float.floatToIntBits(f);
            return String.valueOf(bits);
        } catch (NumberFormatException e) {
            return "0";
        }
    }
}
