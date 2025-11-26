package com.incloud.hcp.ws.enums;

public enum TipoDocumentoEnum {

    DNI(1),
    RUC(6);

    private final Integer valor;
    TipoDocumentoEnum(Integer valor) {
        this.valor = valor;
    }

    @Override
    public String toString() {
        return "TipoDocumentoEnum{" +
                "valor='" + valor + '\'' +
                '}';
    }
    public Integer getValor() {
        return this.valor;
    }

}
