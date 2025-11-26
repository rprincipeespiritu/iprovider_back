package com.incloud.hcp.ws.enums;

public enum TipoPersonaEnum {

    NATURAL(115),
    JURIDICO(116);

    private final Integer valor;
    TipoPersonaEnum(Integer valor) {
        this.valor = valor;
    }

    @Override
    public String toString() {
        return "TipoPersonaEnum{" +
                "valor='" + valor + '\'' +
                '}';
    }
    public Integer getValor() {
        return this.valor;
    }

}
