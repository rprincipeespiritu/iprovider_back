package com.incloud.hcp.dto;

import java.math.BigDecimal;

public class HomologacionNewDto {
    private Integer idHomologacion;
    private String estado;
    private String indAdjunto;
    private BigDecimal peso;
    private String pregunta;
    private Integer usuarioCreacion;
    private Integer idLineaComercial;

    public Integer getIdHomologacion() {
        return idHomologacion;
    }

    public void setIdHomologacion(Integer idHomologacion) {
        this.idHomologacion = idHomologacion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getIndAdjunto() {
        return indAdjunto;
    }

    public void setIndAdjunto(String indAdjunto) {
        this.indAdjunto = indAdjunto;
    }

    public BigDecimal getPeso() {
        return peso;
    }

    public void setPeso(BigDecimal peso) {
        this.peso = peso;
    }

    public String getPregunta() {
        return pregunta;
    }

    public void setPregunta(String pregunta) {
        this.pregunta = pregunta;
    }

    public Integer getUsuarioCreacion() {
        return usuarioCreacion;
    }

    public void setUsuarioCreacion(Integer usuarioCreacion) {
        this.usuarioCreacion = usuarioCreacion;
    }

    public Integer getIdLineaComercial() {
        return idLineaComercial;
    }

    public void setIdLineaComercial(Integer idLineaComercial) {
        this.idLineaComercial = idLineaComercial;
    }

}
