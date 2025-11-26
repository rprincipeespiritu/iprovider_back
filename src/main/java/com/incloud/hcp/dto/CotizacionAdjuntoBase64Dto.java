package com.incloud.hcp.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CotizacionAdjuntoBase64Dto {
    String archivoId;
    String archivoNombre;
    String archivoTipo;
    String base64File;
    String descripcion;
    String idCotizacion;
    String mtrTipoDocumento;

    public String getArchivoId() {
        return archivoId;
    }

    public void setArchivoId(String archivoId) {
        this.archivoId = archivoId;
    }

    public String getArchivoNombre() {
        return archivoNombre;
    }

    public void setArchivoNombre(String archivoNombre) {
        this.archivoNombre = archivoNombre;
    }

    public String getArchivoTipo() {
        return archivoTipo;
    }

    public void setArchivoTipo(String archivoTipo) {
        this.archivoTipo = archivoTipo;
    }

    public String getBase64File() {
        return base64File;
    }

    public void setBase64File(String base64File) {
        this.base64File = base64File;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getIdCotizacion() {
        return idCotizacion;
    }

    public void setIdCotizacion(String idCotizacion) {
        this.idCotizacion = idCotizacion;
    }

    public String getMtrTipoDocumento() {
        return mtrTipoDocumento;
    }

    public void setMtrTipoDocumento(String mtrTipoDocumento) {
        this.mtrTipoDocumento = mtrTipoDocumento;
    }
}
