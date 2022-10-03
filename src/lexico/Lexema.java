package lexico;

public class Lexema {
    public String token;
    public Tag tipo;

    public Lexema(String token, Tag tipo) {
        this.token = token;
        this.tipo = tipo;
    }
}