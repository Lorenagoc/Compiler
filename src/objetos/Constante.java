package objetos;

public class Constante {
    private Object valor;
    private Tipo tipo;

    public Constante(Object valor, Tipo tipo) {
        this.valor = valor;
        this.tipo = tipo;
    }

    public Object getValor() {
        return valor;
    }

    public void setValor(Object valor) {
        this.valor = valor;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
    }
}
