package com.incloud.hcp.dto.graficos;

public class DuplaGrafico {

    private String label;
    private String valor;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    @Override
    public String toString() {
        return "DuplaGrafico{" +
                "label='" + label + '\'' +
                ", valor='" + valor + '\'' +
                '}';
    }
}
