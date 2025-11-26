package com.incloud.hcp.ws.enums;

public enum TipoTelefonoEnum {

    OFICINA(101),
    CELULAR(102);

    private final Integer valor;
    TipoTelefonoEnum(Integer valor) {
        this.valor = valor;
    }

    @Override
    public String toString() {
        return "TipoTelefonoEnum{" +
                "valor='" + valor + '\'' +
                '}';
    }
    public Integer getValor() {
        return this.valor;
    }

}
