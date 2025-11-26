package com.incloud.hcp.ws.enums;

public enum CodEmpresaEnum {
    SILVESTRE(1),NEOAGRUM(2);
    private final Integer valor;

   CodEmpresaEnum(Integer valor){
    this.valor=valor;

    }

    public Integer getValor() {
        return valor;
    }

    @Override
    public String toString() {
        return "CodEmpresa{" +
                "valor=" + valor +
                '}';
    }
}
