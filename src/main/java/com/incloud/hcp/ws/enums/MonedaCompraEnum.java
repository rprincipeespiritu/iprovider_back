package com.incloud.hcp.ws.enums;

public enum MonedaCompraEnum {

    DOLARES(0),
    SOLES(1);



    private final Integer valor;
    MonedaCompraEnum(Integer valor) {
        this.valor = valor;
    }

    @Override
    public String toString() {
        return "MonedaCompraEnum{" +
                "valor='" + valor + '\'' +
                '}';
    }
    public Integer getValor() {
        return this.valor;
    }

}
