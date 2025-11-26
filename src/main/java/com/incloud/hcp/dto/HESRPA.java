package com.incloud.hcp.dto;

import java.io.Serializable;

public class HESRPA implements Serializable {
    private String identificador;
    private String texto;

    public String getIdentificador() {
        return identificador;
    }
    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }

    public String getTexto() {
        return texto;
    }
    public void setTexto(String texto) {
        this.texto = texto;
    }

}