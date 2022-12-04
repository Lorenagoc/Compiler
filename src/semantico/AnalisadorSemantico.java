package semantico;

import lexico.AnalisadorLexico;
import lexico.Tag;
import objetos.Constante;
import objetos.Identificador;
import objetos.Memoria;
import objetos.Tipo;

public class AnalisadorSemantico {
    public static final String PRINT_RED = "\u001B[31m";
    public static final String RESET_COLOR = "\u001B[0m";

    private AnalisadorLexico lexico;
    private Memoria memoria;

    public AnalisadorSemantico(AnalisadorLexico lexico) {
        this.lexico = lexico;
        this.memoria = new Memoria();
    }

    public void showErro(String erro) {
        System.out.printf(PRINT_RED + "%02d: %s\n" + RESET_COLOR, lexico.getLinha(), erro);
        System.exit(1);
    }

    public void procDecl(Identificador identificador) {
        if (!memoria.possuiIdentificador(identificador.getNome())) {
            memoria.addIdentificador(identificador);
        } else {
            showErro("Identificador '" + identificador.getNome() +  "' ja foi declarado.");
        }
    }

    public Constante procFactorIdentifier(String id) {
        if (memoria.possuiIdentificador(id)) {
            return memoria.getValor(id);
        }
        showErro("Identificador '" + id +  "' nao foi declarado.");
        return null;
    }

    public Constante procFactorNot(Constante fator) {
        if (fator.getTipo() == Tipo.BOOL) {
            return new Constante(!((boolean) fator.getValor()), fator.getTipo());
        }
        showErro("Not so pode ser usado em expressoes booleanas.");
        return null;
    }

    public Constante procFactorMinus(Constante factor) {
        if (factor.getTipo() == Tipo.INT) {
            return new Constante(-((int) factor.getValor()), factor.getTipo());
        }
        if (factor.getTipo() == Tipo.FLOAT) {
            return new Constante(-((float) factor.getValor()), factor.getTipo());
        }
        showErro("Menos so pode ser usado com numeros do tipo inteiro ou float.");
        return null;
    }

    public Constante procFactorMulOp(Constante factor, Constante mulOpFactor, Tag op) {
        if (factor.getTipo() == mulOpFactor.getTipo()) {
            if (factor.getTipo() == Tipo.BOOL && op != Tag.AND) {
                showErro("BOOL nao e compativel com esse operador.");
            }
            if (factor.getTipo() != Tipo.BOOL && op == Tag.AND) {
                showErro("Operador AND e compativel apenas com tipo BOOL.");
            }
            if (!isNumber(factor)) {
                showErro("Operacao " + op + " esta disponivel apenas para tipos numericos.");
            }
            return new Constante(factor.getValor(), factor.getTipo());
        }
        showErro("Nao e possivel executar a operacao " + op + " com '" + factor.getTipo() +
                "' e '" + mulOpFactor.getTipo() + "' - tipos sao diferentes.");
        return null;
    }

    public Constante procTermSimpleExpr(Constante term, Constante addTerm, Tag op) {
        if (term.getTipo() == addTerm.getTipo()) {
            if (term.getTipo() == Tipo.STRING && op != Tag.PLUS) {
                showErro("STRING nao e compativel com operador " + op + ".");
            }
            if (term.getTipo() == Tipo.BOOL && op != Tag.OR) {
                showErro("BOOL nao e compativel com operador " + op + ".");
            }
            if (term.getTipo() != Tipo.BOOL && op == Tag.OR) {
                showErro("Operador OR operator e compativel apenas com tipo BOOL.");
            }
            if (!isNumber(term) && op == Tag.MINUS) {
                showErro("Operador MINUS e compativel apenas com tipos numericos.");
            }
            return new Constante(term.getValor(), term.getTipo());
        }
        showErro("Nao e possivel executar a operacao com '" + term.getTipo() +
                "' e '" + addTerm.getTipo() + "' - tipos sao diferentes.");
        return null;
    }

    public Constante procExpressionRelOp(Constante expressao, Constante operacao) {
        if (expressao.getTipo() == operacao.getTipo()) {
            return new Constante(expressao.getValor(), expressao.getTipo());
        }
        showErro("Nao e possivel executar a operacao com '" + expressao.getTipo() +
                "' e '" + operacao.getTipo() + "' - tipos sao diferentes.");
        return null;
    }
    
    private static boolean isNumber(Constante valor) {
        return valor.getTipo() == Tipo.FLOAT || valor.getTipo() == Tipo.INT;
    }
}
