package com.incloud.hcp.ws.enums;

public enum TipoCategoriaEnum {
    MATERIALES(0),
    SERVICIOS(2);



    private final Integer valor;
    TipoCategoriaEnum(Integer valor) {
        this.valor = valor;
    }

    @Override
    public String toString() {
        return "TipoCategoriaEnum{" +
                "valor='" + valor + '\'' +
                '}';
    }
    public Integer getValor() {
        return this.valor;
    }
}
