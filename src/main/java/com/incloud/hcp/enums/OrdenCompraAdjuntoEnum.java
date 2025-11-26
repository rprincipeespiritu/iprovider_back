package com.incloud.hcp.enums;

public enum OrdenCompraAdjuntoEnum {

    PENDIENTE("PENDIENTE"),
    APROBADA("APROBADA"),
    RECHAZADA("RECHAZADA");
    String codigo;
    OrdenCompraAdjuntoEnum(String codigo){
        this.codigo = codigo;
    }

    public String getCodigo(){
        return this.codigo;
    }
}
