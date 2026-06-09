/**
 * Compilador BRL - Programa Principal
 * Compilador para linguagem imperativa simplificada BRL.
 * Gera código Assembly (MASM 80x86) a partir de programas fonte .LC
 *
 * Trabalho Prático - Compiladores
 * Ciência da Computação - Dom Helder / Escola Superior
 * Prof. Dr. Marcos W. Rodrigues
 *
 * Integrantes:
 *   - João Gabriel Ribeiro Holanda
 *   - João Victor Fernandes Lima
 *   - Luan Tadeu Lima Rezende Dias
 *
 * Uso: java BRL <arquivo_fonte.LC> <arquivo_assembly.ASM>
 */

import exceptions.LexicalException;
import exceptions.SyntaxException;
import exceptions.SemanticException;
import java.io.File;
import java.io.IOException;
import java.util.List;
public class BRL {

    public static void main(String[] args) {
        // Verifica argumentos de linha de comando
        if (args.length != 2) {
            System.err.println("Uso: BRL <arquivo_fonte.LC> <arquivo_assembly.ASM>");
            System.exit(1);
        }

        String arquivoFonte = args[0];
        String arquivoSaida = args[1];

        // Verifica se o arquivo fonte existe
        File fonte = new File(arquivoFonte);
        if (!fonte.exists()) {
            System.err.println("Erro: Arquivo fonte '" + arquivoFonte + "' nao encontrado.");
            System.exit(1);
        }

        // Verifica extensão .LC
        if (!arquivoFonte.toUpperCase().endsWith(".LC")) {
            System.err.println("Erro: Arquivo fonte deve ter extensao .LC");
            System.exit(1);
        }

        // Verifica extensão .ASM
        if (!arquivoSaida.toUpperCase().endsWith(".ASM")) {
            System.err.println("Erro: Arquivo de saida deve ter extensao .ASM");
            System.exit(1);
        }

        try {
            compilar(arquivoFonte, arquivoSaida);
            System.out.println("Compilacao concluida com sucesso!");
            System.out.println("Arquivo gerado: " + arquivoSaida);
        } catch (LexicalException e) {
            System.err.println("Erro lexico: " + e.getMessage());
            System.exit(1);
        } catch (SyntaxException e) {
            System.err.println("Erro sintatico: " + e.getMessage());
            System.exit(1);
        } catch (SemanticException e) {
            System.err.println("Erro semantico: " + e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Erro de I/O: " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Executa todas as fases da compilação.
     */
    private static void compilar(String arquivoFonte, String arquivoSaida)
            throws LexicalException, SyntaxException, SemanticException, IOException {

        // ===== FASE 1: Análise Léxica =====
        AnalisadorLexico lexico = new AnalisadorLexico();
        List<Token> tokens = lexico.analisar(arquivoFonte);

        // ===== FASE 2 e 3: Análise Sintática + Semântica + Geração de Código =====
        TabelaSimbolos tabela = new TabelaSimbolos();
        GeradorCodigo gerador = new GeradorCodigo(tabela);
        AnalisadorSintatico sintatico = new AnalisadorSintatico(tabela, gerador);

        sintatico.analisar(tokens);

        // ===== FASE 4: Geração do arquivo Assembly =====
        gerador.escreverArquivo(arquivoSaida);
    }
}
