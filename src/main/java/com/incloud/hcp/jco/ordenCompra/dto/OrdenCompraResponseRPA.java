package com.incloud.hcp.jco.ordenCompra.dto;

import java.io.Serializable;
import java.util.List;


public class OrdenCompraResponseRPA implements Serializable {

    private String codigoRespuesta;
    private String Mensaje;

    public String getCodigoRespuesta() { return codigoRespuesta; }
    public void setCodigoRespuesta(String codigoRespuesta) {
        this.codigoRespuesta = codigoRespuesta;
    }

    public String getMensaje() { return Mensaje; }
    public void setMensaje(String Mensaje) {
        this.Mensaje = Mensaje;
    }
}