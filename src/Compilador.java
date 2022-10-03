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

import lexico.Lexema;
import lexico.Lexico;
import lexico.Tag;

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
        int erros = 0;

        try (Lexico lexico = new Lexico(arquivo)) {
            Lexema lexema;
            while (getTipo((lexema = lexico.scan()).tipo)) {
                switch (lexema.tipo) {
                    case TOKEN_INVALIDO:
                        System.out.println(PRINT_RED + "L" + lexico.getLinha() + ": Erro léxico, o token <" + lexema.token + "> é inválido." + RESET_COLOR);
                        erros++;
                        break;
                    case ERRO_EOF:
                        System.out.println(PRINT_RED + "L" + lexico.getLinha() + ": Erro léxico. Fim de arquivo inesperado." + RESET_COLOR);
                        erros++;
                        break;
                    default:
                        System.out.println("<" + lexema.token + ", " + lexema.tipo + ">");
                        break;
                }
            }
            if (erros == 0) System.out.println(PRINT_GREEN + "\nPrograma compilado com sucessso!");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Internal error: " + e.getMessage());
        }
    }

    private static boolean getTipo(Tag tipo) {
        return tipo != Tag.EOF;
    }
}