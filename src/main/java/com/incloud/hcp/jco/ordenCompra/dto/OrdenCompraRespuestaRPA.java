package com.incloud.hcp.jco.ordenCompra.dto;

import java.io.Serializable;


public class OrdenCompraRespuestaRPA implements Serializable {
    private String identificador;
    private String numeroOC;

    public String getIdentificador() {
        return identificador;
    }
    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }

    public String getNumeroOC() {
        return numeroOC;
    }
    public void setNumeroOC(String numeroOC) {
        this.numeroOC = numeroOC;
    }

}