package com.incloud.hcp.dto;

import com.incloud.hcp.domain.ActaSustentoDetalle;

import java.util.Date;
import java.util.List;

public class ActaSustentoFormularioEvaluacionDto {

    private List<ActaSustentoDetalle> actaSustentoDetalle;
    private Date fechaContabilidad;
    private Date fechaDocumento;
    private String notaEntrega;
    private String textoCabecera;
    private Integer idDocumentoAceptacion;

    public Integer getIdDocumentoAceptacion() {
        return idDocumentoAceptacion;
    }

    public void setIdDocumentoAceptacion(Integer idDocumentoAceptacion) {
        this.idDocumentoAceptacion = idDocumentoAceptacion;
    }

    public List<ActaSustentoDetalle> getActaSustentoDetalle() {
        return actaSustentoDetalle;
    }

    public void setActaSustentoDetalle(List<ActaSustentoDetalle> actaSustentoDetalle) {
        this.actaSustentoDetalle = actaSustentoDetalle;
    }

    public Date getFechaContabilidad() {
        return fechaContabilidad;
    }

    public void setFechaContabilidad(Date fechaContabilidad) {
        this.fechaContabilidad = fechaContabilidad;
    }

    public Date getFechaDocumento() {
        return fechaDocumento;
    }

    public void setFechaDocumento(Date fechaDocumento) {
        this.fechaDocumento = fechaDocumento;
    }

    public String getNotaEntrega() {
        return notaEntrega;
    }

    public void setNotaEntrega(String notaEntrega) {
        this.notaEntrega = notaEntrega;
    }

    public String getTextoCabecera() {
        return textoCabecera;
    }

    public void setTextoCabecera(String textoCabecera) {
        this.textoCabecera = textoCabecera;
    }
}
