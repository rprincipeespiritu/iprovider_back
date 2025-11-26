package com.incloud.hcp.dto;

public class ResponseCrearDocumentoDto {

    private String status;
    private String mensaje;
    private Integer IDDocumento;


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public Integer getIDDocumento() {
        return IDDocumento;
    }

    public void setIDDocumento(Integer IDDocumento) {
        this.IDDocumento = IDDocumento;
    }
}
