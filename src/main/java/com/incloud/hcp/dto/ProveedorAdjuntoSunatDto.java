package com.incloud.hcp.dto;

import com.incloud.hcp.domain.MtrTipoDocumento;

public class ProveedorAdjuntoSunatDto {

    private Integer id;
    private String archivoId;
    private String archivoNombre;
    private String archivoTipo;
    private String rutaAdjunto;
    private MtrTipoDocumento mtrTipoDocumento;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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

    public String getRutaAdjunto() {
        return rutaAdjunto;
    }

    public void setRutaAdjunto(String rutaAdjunto) {
        this.rutaAdjunto = rutaAdjunto;
    }

    public MtrTipoDocumento getMtrTipoDocumento() {
        return mtrTipoDocumento;
    }

    public void setMtrTipoDocumento(MtrTipoDocumento mtrTipoDocumento) {
        this.mtrTipoDocumento = mtrTipoDocumento;
    }

    @Override
    public String toString() {
        return "ProveedorAdjuntoSunatDto{" +
                "id=" + id +
                ", archivoId='" + archivoId + '\'' +
                ", archivoNombre='" + archivoNombre + '\'' +
                ", archivoTipo='" + archivoTipo + '\'' +
                ", rutaAdjunto='" + rutaAdjunto + '\'' +
                ", mtrTipoDocumento=" + mtrTipoDocumento +
                '}';
    }
}
