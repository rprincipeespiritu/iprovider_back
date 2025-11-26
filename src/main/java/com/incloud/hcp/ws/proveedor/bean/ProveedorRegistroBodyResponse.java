package com.incloud.hcp.ws.proveedor.bean;

import java.io.Serializable;

public class ProveedorRegistroBodyResponse implements Serializable {

    private String id_Agenda;
    private String codigoDireccion;

    public String getId_Agenda() {
        return id_Agenda;
    }

    public void setId_Agenda(String id_Agenda) {
        this.id_Agenda = id_Agenda;
    }

    public String getCodigoDireccion() {
        return codigoDireccion;
    }

    public void setCodigoDireccion(String codigoDireccion) {
        this.codigoDireccion = codigoDireccion;
    }

    @Override
    public String toString() {
        return "ProveedorRegistroBodyResponse{" +
                "id_Agenda='" + id_Agenda + '\'' +
                ", codigoDireccion='" + codigoDireccion + '\'' +
                '}';
    }
}
