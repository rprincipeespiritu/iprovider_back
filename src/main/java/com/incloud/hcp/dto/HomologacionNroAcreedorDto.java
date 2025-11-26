package com.incloud.hcp.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class HomologacionNroAcreedorDto {

    private String numeroAcreedor;
    private String respuesta;

    public String getNumeroAcreedor() {
        return numeroAcreedor;
    }

    public void setNumeroAcreedor(String numeroAcreedor) {
        this.numeroAcreedor = numeroAcreedor;
    }

    public String getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(String respuesta) {
        this.respuesta = respuesta;
    }

    @Override
    public String toString() {
        return "RespuestaDto{" +
                "numeroAcreedor='" + numeroAcreedor + '\'' +
                ",respuesta=" + respuesta +
                '}';
    }
}
