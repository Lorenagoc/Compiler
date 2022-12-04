package objetos;

import java.util.HashMap;
import java.util.Map;

public class Memoria {
    private class EntradaMemoria {
        private Identificador identificador;
        private Constante valor;

        public EntradaMemoria(Identificador identificador) {
            this.identificador = identificador;
            this.valor = new Constante(0, identificador.getTipo());
        }

        public Identificador getIdentificador() {
            return identificador;
        }

        public void setIdentificador(Identificador identificador) {
            this.identificador = identificador;
        }

        public Constante getValor() {
            return valor;
        }

        public void setValor(Constante valor) {
            this.valor = valor;
        }
    }

    Map<String, EntradaMemoria> memoria;

    public Memoria() {
        this.memoria = new HashMap<>();
    }

    public boolean possuiIdentificador(String id) {
        return memoria.containsKey(id);
    }

    public void addIdentificador(Identificador identificador) {
        memoria.put(identificador.getNome(), new EntradaMemoria(identificador));
    }

    public Tipo getTipo(String id) {
        return memoria.get(id).getIdentificador().getTipo();
    }

    public Constante getValor(String id) {
        return memoria.get(id).getValor();
    }

    public void setValor(String id, Constante valor) {
        memoria.get(id).setValor(valor);
    }
}
