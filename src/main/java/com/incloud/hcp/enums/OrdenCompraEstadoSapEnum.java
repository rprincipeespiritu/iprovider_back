package com.incloud.hcp.enums;

public enum OrdenCompraEstadoSapEnum {
    LIBERADA("1"),//L
    BLOQUEADA("B"),
    ANULADA("A"),
    NOLIBERADA("S");//L
    private final String codigo;

    OrdenCompraEstadoSapEnum(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }
}
