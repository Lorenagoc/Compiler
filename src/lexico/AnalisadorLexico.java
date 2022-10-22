package lexico;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PushbackInputStream;

public class AnalisadorLexico implements AutoCloseable {

    private int linha;
    private TabelaSimbolos ts;
    private PushbackInputStream arquivo;

    public AnalisadorLexico(String nomeArquivo) throws LexicalException {
        try {
            arquivo = new PushbackInputStream(new FileInputStream(nomeArquivo));
        } catch (Exception e) {
            throw new LexicalException("Arquivo não encontrado");
        }

        ts = new TabelaSimbolos();
        linha = 1;
    }

    public int getLinha() {
        return linha;
    }

    public Lexema scan() throws IOException {
        int estado = 1;
        Lexema lexema = new Lexema("", Tag.EOF);

        while (estado != 12 && estado != 13) {
            int ch = arquivo.read();

            switch (estado) {
                case 1:
                    if (ch == ' ' || ch == '\t' || ch == '\r') break;
                    else if (ch == '\n') linha++;
                    else if (ch == '{') estado = 2;
                    else if (Character.isLetter(ch) && ch != 255) {
                        readCh(lexema, (char) ch);
                        estado = 3;
                    } else if (Character.isDigit(ch)) {
                        readCh(lexema, (char) ch);
                        estado = 4;
                    } else if (ch == '=' || ch == '>') {
                        readCh(lexema, (char) ch);
                        estado = 6;
                    } else if (ch == '<') {
                        readCh(lexema, (char) ch);
                        estado = 7;
                    } else if (ch == '/') {
                        readCh(lexema, (char) ch);
                        estado = 8;
                    } else if (ch == ',' || ch == ';' || ch == '+' || ch == '-' ||
                            ch == '*' || ch == '(' || ch == ')') {
                        readCh(lexema, (char) ch);
                        estado = 12;
                    } else if (ch == -1 || ch == 255) {
                        lexema.tipo = Tag.EOF;
                        estado = 13;
                    } else if (ch == '&') {
                        readCh(lexema, (char) ch);
                        estado = 14;
                    } else if (ch == '|') {
                        readCh(lexema, (char) ch);
                        estado = 15;
                    } else {
                        readCh(lexema, (char) ch);
                        lexema.tipo = Tag.TOKEN_INVALIDO;
                        estado = 13;
                    }
                    break;

                case 2:
                    if (ch == '}') {
                        lexema.tipo = Tag.LITERAL;
                        estado = 13;
                    } else {
                        readCh(lexema, (char) ch);
                        estado = 2;
                    }
                    break;

                case 3:
                    if (Character.isLetter(ch) || Character.isDigit(ch)) {
                        readCh(lexema, (char) ch);
                        estado = 3;
                    } else {
                        estado = 12;
                        arquivo.unread(ch);
                    }
                    break;

                case 4:
                    if (Character.isDigit(ch)) {
                        readCh(lexema, (char) ch);
                        estado = 4;
                    } else if (ch == '.') {
                        readCh(lexema, (char) ch);
                        estado = 5;
                    } else {
                        lexema.tipo = Tag.INTEGER_CONST;
                        arquivo.unread(ch);
                        estado = 13;
                    }
                    break;

                case 5:
                    if (Character.isDigit(ch)) {
                        readCh(lexema, (char) ch);
                        estado = 5;
                    } else {
                        lexema.tipo = Tag.FLOAT_CONST;
                        arquivo.unread(ch);
                        estado = 13;
                    }
                    break;

                case 6:
                    if (ch == '=') readCh(lexema, (char) ch);
                    else arquivo.unread(ch);
                    estado = 12;
                    break;

                case 7:
                    if (ch == '>' || ch == '=') readCh(lexema, (char) ch);
                    else arquivo.unread(ch);
                    estado = 12;
                    break;

                case 8:
                    if (ch == '/') {
                        ignoraComentario(lexema);
                        estado = 9;
                    } else if (ch == '*') {
                        ignoraComentario(lexema);
                        estado = 10;
                    } else {
                        arquivo.unread(ch);
                        estado = 12;
                    }
                    break;

                case 9:
                    if (ch == '\n') {
                        arquivo.unread(ch);
                        estado = 1;
                    } else estado = 9;
                    break;

                case 10:
                    if (ch == '*') estado = 11;
                    else estado = 10;
                    break;

                case 11:
                    if (ch == '/') estado = 1;
                    else estado = 10;
                    break;

                case 14:
                    if (ch == '&') readCh(lexema, (char) ch);
                    else arquivo.unread(ch);
                    estado = 12;
                    break;

                case 15:
                    if (ch == '|') readCh(lexema, (char) ch);
                    else arquivo.unread(ch);
                    estado = 12;
                    break;

                default:
                    break;

            }
        }

        if (estado == 12) {
            if (ts.contem(lexema.token)) {
                lexema.tipo = ts.busca(lexema.token);
            } else {
                lexema.tipo = Tag.IDENTIFIER;
            }
        }

        return lexema;
    }

    private void readCh(Lexema lexema, char ch) {
        lexema.token = lexema.token + ch;
    }

    private void ignoraComentario(Lexema lexema) {
        lexema.token = lexema.token.substring(0, lexema.token.length() - 1);
    }

    @Override
    public void close() throws Exception {
        arquivo.close();
    }
}