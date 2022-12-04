package sintatico;

import lexico.AnalisadorLexico;
import lexico.Lexema;
import lexico.Tag;
import objetos.Constante;
import objetos.Identificador;
import objetos.Tipo;
import semantico.AnalisadorSemantico;

import java.io.IOException;

public class AnalisadorSintatico {
    public static final String PRINT_RED = "\u001B[31m";
    public static final String PRINT_GREEN = "\u001B[32m";
    public static final String RESET_COLOR = "\u001B[0m";

    private AnalisadorLexico lexico;
    private Lexema tokenAtual;
    private AnalisadorSemantico semantico;

    public AnalisadorSintatico(AnalisadorLexico lexico) throws IOException {
        this.lexico = lexico;
        this.tokenAtual = lexico.scan();
        this.semantico = new AnalisadorSemantico(lexico);
    }

    public void executar() {
        procProgram();
        consome(Tag.EOF);
    }

    private void avance() throws IOException {
        tokenAtual = lexico.scan();
    }

    private Lexema consome(Tag tipo) {
        System.out.println(lexico.getLinha() + " - Expected (" + tokenAtual.token + "), found (" + tokenAtual.tipo + ")");
        if(tipo == Tag.EOF) System.out.println(PRINT_GREEN + "Compilação finalizada com sucesso" + RESET_COLOR);
        if (tipo == tokenAtual.tipo) {
            try {
                Lexema tokenAnterior = tokenAtual;
                avance();
                return tokenAnterior;
            } catch (IOException e) {
                System.out.printf("Erro na leitura do token\n");
            }
        } else {
            mostraErro();
        }
        return null;
    }

    private void mostraErro() {
        System.out.printf(PRINT_RED + "L" + lexico.getLinha() + ": " + RESET_COLOR);
        switch (tokenAtual.tipo) {
            case TOKEN_INVALIDO:
                System.out.printf(PRINT_RED+"Erro sintático, o lexema [" + tokenAtual.token + "] é inválido." + RESET_COLOR + "\n");
                break;
            case ERRO_EOF:
            case EOF:
                System.out.printf("Fim de aquivo inesperado.\n");
                break;
            default:
                System.out.printf(PRINT_RED+"Erro sintático, lexema [" + tokenAtual.token + "] não esperado." + RESET_COLOR + "\n");
                break;
        }
        System.exit(1);
    }

    // program ::= start [decl-list] stmt-list exit
    // program ::= start [decl-list] stmt { stmt } exit
    private void procProgram() {
        consome(Tag.START);

        if (tokenAtual.tipo == Tag.INT || tokenAtual.tipo == Tag.FLOAT || tokenAtual.tipo == Tag.STRING) {
            procDeclList();
        }

        do {
            procStmt();
        } while (tokenAtual.tipo != Tag.EXIT);
        consome(Tag.EXIT);
    }

    // decl-list ::= decl {decl}
    private void procDeclList() {
        do {
            Tipo tipo = procType();
            Lexema lexema = consome(Tag.IDENTIFIER);
            semantico.procDecl(new Identificador(lexema.token, tipo));
            while (tokenAtual.tipo == Tag.COMMA) {
                consome(Tag.COMMA);
                lexema = consome(Tag.IDENTIFIER);
                semantico.procDecl(new Identificador(lexema.token, tipo));
            }
            consome(Tag.DOT_COMMA);

        } while (tokenAtual.tipo == Tag.INT || tokenAtual.tipo == Tag.FLOAT || tokenAtual.tipo == Tag.STRING);
    }

    // type ::= int | float | string
    private Tipo procType() {
        if (tokenAtual.tipo == Tag.INT) {
            consome(Tag.INT);
            return Tipo.INT;
        } else if (tokenAtual.tipo == Tag.FLOAT) {
            consome(Tag.FLOAT);
            return Tipo.FLOAT;
        } else {
            consome(Tag.STRING);
            return Tipo.STRING;
        }
    }

    // stmt-list ::= stmt {stmt}
    private void procStmtList() {
        procStmt();
        while (tokenAtual.tipo == Tag.IF ||
                tokenAtual.tipo == Tag.DO ||
                tokenAtual.tipo == Tag.SCAN ||
                tokenAtual.tipo == Tag.PRINT ||
                tokenAtual.tipo == Tag.IDENTIFIER) {
            procStmt();
        }
    }

    // stmt ::= assign-stmt ";" | if-stmt | while-stmt | read-stmt ";" | write-stmt ";"
    private void procStmt() {
        if (tokenAtual.tipo == Tag.IF) {
            procIfStmt();
        } else if (tokenAtual.tipo == Tag.DO) {
            procWhileStmt();
        } else if (tokenAtual.tipo == Tag.SCAN) {
            procReadStmt();
            consome(Tag.DOT_COMMA);
        } else if (tokenAtual.tipo == Tag.PRINT) {
            procWriteStmt();
            consome(Tag.DOT_COMMA);
        } else {
            procAssignStmt();
            consome(Tag.DOT_COMMA);
        }
    }

    // assign-stmt ::= identifier "=" simple_expr
    private void procAssignStmt() {
        consome(Tag.IDENTIFIER);
        consome(Tag.ASSIGN);
        procSimpleExpr();
    }

    // if-stmt ::= if condition then stmt-list end | if condition then stmt-list else stmt-list end
    private void procIfStmt() {
        consome(Tag.IF);
        procCondition();
        consome(Tag.THEN);
        procStmtList();
        if (tokenAtual.tipo == Tag.ELSE) {
            consome(Tag.ELSE);
            procStmtList();
        }
        consome(Tag.END);
    }

    // condition ::= expression
    private void procCondition() {
        procExpression();
    }

    // while-stmt ::= do stmt-list stmt-sufix
    private void procWhileStmt() {
        consome(Tag.DO);
        procStmtList();
        procStmtSufix();
    }

    // stmt-sufix ::= while condition end
    private void procStmtSufix() {
        consome(Tag.WHILE);
        procCondition();
        consome(Tag.END);
    }

    // read-stmt ::= scan "(" identifier ")"
    private void procReadStmt() {
        consome(Tag.SCAN);
        consome(Tag.OPEN_PAR);
        procIdentifier();
        consome(Tag.CLOSE_PAR);
    }

    // write-stmt ::= print "(" writable ")"
    private void procWriteStmt() {
        consome(Tag.PRINT);
        consome(Tag.OPEN_PAR);
        procWritable();
        consome(Tag.CLOSE_PAR);

    }

    // writable ::= simple-expr | literal
    private void procWritable() {
        if (tokenAtual.tipo == Tag.IDENTIFIER) {
            procSimpleExpr();
        } else {
            procLiteral();
        }
    }

    // expression ::= simple-expr | simple-expr relop simple-expr
    // expression ::= simple-expr { relop simple-expr }
    private Constante procExpression() {
        Constante expression = procSimpleExpr();
        while (tokenAtual.tipo == Tag.EQUAL || tokenAtual.tipo == Tag.GREATER ||
                tokenAtual.tipo == Tag.GREATER_OR_EQUAL || tokenAtual.tipo == Tag.LESS ||
                tokenAtual.tipo == Tag.LESS_OR_EQUAL || tokenAtual.tipo == Tag.NOT_EQUAL) {
            procRelOp();
            Constante operacao = procSimpleExpr();
            expression = semantico.procExpressionRelOp(expression, operacao);
        }
        return expression;
    }

    // simple-expr ::= term | simple-expr addop term
    // simple-expr ::= term { addop term }
    private Constante procSimpleExpr() {
        Constante term = procTerm();

        while (tokenAtual.tipo == Tag.PLUS || tokenAtual.tipo == Tag.MINUS ||
                tokenAtual.tipo == Tag.OR) {
            Tag operacao = tokenAtual.tipo;
            procAddOp();
            Constante addTerm = procTerm();
            term = semantico.procTermSimpleExpr(term, addTerm, operacao);
        }
        return term;
    }

    // term ::= factor-a | term mulop factor-a
    // term ::= factor-a { mulop factor-a }
    private Constante procTerm() {
        Constante factor = procFactorA();

        while (tokenAtual.tipo == Tag.MULT || tokenAtual.tipo == Tag.DIV ||
                tokenAtual.tipo == Tag.AND) {
            Tag operacao = tokenAtual.tipo;
            procMulOp();
            Constante mulFactor = procFactorA();
            factor = semantico.procFactorMulOp(factor, mulFactor, operacao);
        }
        return factor;
    }

    // factor-a ::= factor | "!" factor | "-" factor
    private Constante procFactorA() {
        Tag extraToken = null;
        if (tokenAtual.tipo == Tag.NOT || tokenAtual.tipo == Tag.MINUS) {
            extraToken = tokenAtual.tipo;
            consome(tokenAtual.tipo);
        }
        Constante factorConst = procFactor();
        if (extraToken == Tag.NOT) {
            return semantico.procFactorNot(factorConst);
        }
        if (extraToken == Tag.MINUS) {
            return semantico.procFactorMinus(factorConst);
        }
        return factorConst;
    }



    // factor ::= identifier | constant | "(" expression ")"
    private Constante procFactor() {
        if (tokenAtual.tipo == Tag.IDENTIFIER) {
            Lexema lexema = consome(Tag.IDENTIFIER);
            return semantico.procFactorIdentifier(lexema.token);
        } else if (tokenAtual.tipo == Tag.OPEN_PAR) {
            consome(Tag.OPEN_PAR);
            Constante valorExpressao = procExpression();
            consome(Tag.CLOSE_PAR);
            return valorExpressao;
        }
        return procConstant();
    }

    // relop ::= "==" | ">" | ">=" | "<" | "<=" | "<>"
    private void procRelOp() {
        if (tokenAtual.tipo == Tag.EQUAL) {
            consome(Tag.EQUAL);
        } else if (tokenAtual.tipo == Tag.GREATER) {
            consome(Tag.GREATER);
        } else if (tokenAtual.tipo == Tag.GREATER_OR_EQUAL) {
            consome(Tag.GREATER_OR_EQUAL);
        } else if (tokenAtual.tipo == Tag.LESS) {
            consome(Tag.LESS);
        } else if (tokenAtual.tipo == Tag.LESS_OR_EQUAL) {
            consome(Tag.LESS_OR_EQUAL);
        } else {
            consome(Tag.NOT_EQUAL);
        }
    }

    // addop ::= "+" | "-" | "||"
    private void procAddOp() {
        if (tokenAtual.tipo == Tag.PLUS) {
            consome(Tag.PLUS);
        } else if (tokenAtual.tipo == Tag.MINUS) {
            consome(Tag.MINUS);
        } else {
            consome(Tag.OR);
        }
    }

    // mulop ::= "*" | "/" | "&&"
    private void procMulOp() {
        if (tokenAtual.tipo == Tag.MULT) {
            consome(Tag.MULT);
        } else if (tokenAtual.tipo == Tag.DIV) {
            consome(Tag.DIV);
        } else {
            consome(Tag.AND);
        }
    }

    // constant ::= integer_const | float_const | literal
    private Constante procConstant() {
        if (tokenAtual.tipo == Tag.INTEGER_CONST) {
            return procIntegerConst();
        } else if (tokenAtual.tipo == Tag.FLOAT_CONST) {
            return procFloatConst();
        } else {
            return procLiteral();
        }
    }

    // integer_const ::= digit+
    private Constante procIntegerConst() {
        return new Constante(Integer.parseInt(consome(Tag.INTEGER_CONST).token), Tipo.INT);
    }

    // float_const ::= digit+ “.” digit+
    private Constante procFloatConst() {
        return new Constante(Float.parseFloat(consome(Tag.FLOAT_CONST).token), Tipo.FLOAT);
    }

    // literal ::= " { " {caractere} " } "
    private Constante procLiteral() {
        return new Constante(consome(Tag.LITERAL).token, Tipo.STRING);
    }

    // identifier ::= (letter | _ ) (letter | digit )*
    private void procIdentifier() { consome(Tag.IDENTIFIER); }
}
