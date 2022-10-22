/*
 * Trabalho pratico de compiladores
 * Estudante: Lorena Gomes de Oliveira Cabral - 20183002361
 * Discente: Kecia Marques
 *
 *
 * Programa baseado no interpretador para uma linguagem ficticia que foi desenvolvido
 * na disciplina de Linguagem de Programacao, ministrada pelo professor Andrei Rimsa, no Cefet - MG.
 *
 * Reference: RIMSA, Andrei. Interpretador para Tiny. GitHub, 10 jun. 2020.
 * Disponível em: https://github.com/rimsa/tiny. Acesso em: 25 set. 2022.
 * */

import lexico.AnalisadorLexico;
import sintatico.AnalisadorSintatico;

public class Compilador {

    public static final String RESET_COLOR = "\u001B[0m";
    public static final String PRINT_RED = "\u001B[31m";
    public static final String PRINT_GREEN = "\u001B[32m";

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Falta argumentos, por favor, digite o nome do arquivo.");
            return;
        }

        String arquivo = "src/examples/"+args[0]+".txt";

        try (AnalisadorLexico lexico = new AnalisadorLexico(arquivo)) {
            AnalisadorSintatico sintatico = new AnalisadorSintatico(lexico);
            sintatico.executar();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Internal error: " + e.getMessage());
        }
    }
}