package com.incloud.hcp.ws.enums;

public enum CodPagoEnum {

    CREDITO(1),CONTADO(0);
    private final Integer valor;

    CodPagoEnum(Integer valor){
        this.valor=valor;

    }

    public Integer getValor() {
        return valor;
    }

    @Override
    public String toString() {
        return "CodPago{" +
                "valor=" + valor +
                '}';
    }
}
