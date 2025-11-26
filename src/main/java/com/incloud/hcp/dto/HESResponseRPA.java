package com.incloud.hcp.dto;

import java.io.Serializable;

public class HESResponseRPA implements Serializable {

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