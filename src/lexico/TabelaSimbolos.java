package lexico;

import java.util.HashMap;

public class TabelaSimbolos {

    private HashMap<String, Tag> ts;

    public TabelaSimbolos() {

        ts = new HashMap<>();

        // Palavras reservadas
        ts.put("start", Tag.START);
        ts.put("exit", Tag.EXIT);
        ts.put("int", Tag.INT);
        ts.put("float", Tag.FLOAT);
        ts.put("string", Tag.STRING);
        ts.put("if", Tag.IF);
        ts.put("then", Tag.THEN);
        ts.put("end", Tag.END);
        ts.put("else", Tag.ELSE);
        ts.put("do", Tag.DO);
        ts.put("while", Tag.WHILE);
        ts.put("scan", Tag.SCAN);
        ts.put("print", Tag.PRINT);

        // Operadores
        ts.put("==", Tag.EQUAL);
        ts.put("<>", Tag.NOT_EQUAL);
        ts.put(">", Tag.GREATER);
        ts.put(">=", Tag.GREATER_OR_EQUAL);
        ts.put("<", Tag.LESS);
        ts.put("<=", Tag.LESS_OR_EQUAL);
        ts.put("||", Tag.OR);
        ts.put("&&", Tag.AND);
        ts.put("!", Tag.NOT);
        ts.put("-", Tag.MINUS);
        ts.put("=", Tag.ASSIGN);
        ts.put("*", Tag.MULT);
        ts.put("/", Tag.DIV);

        // Pontuacao
        ts.put(";", Tag.DOT_COMMA);
        ts.put(",", Tag.COMMA);
        ts.put("(", Tag.OPEN_PAR);
        ts.put(")", Tag.CLOSE_PAR);

    }

    public boolean contem(String token) {
        return ts.containsKey(token);
    }

    public Tag busca(String token) {
        if (contem(token)) {
            return ts.get(token);
        } else {
            return Tag.TOKEN_INVALIDO;
        }
    }
}