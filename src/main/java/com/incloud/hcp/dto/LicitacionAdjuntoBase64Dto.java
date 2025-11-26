package com.incloud.hcp.dto;

import com.incloud.hcp.domain.MtrTipoDocumento;

public class LicitacionAdjuntoBase64Dto {
    private Integer idLicitacion;
    private String base64File;
    private String fileExtencion;
    private String nombreDocumento;
    private String codigoDocumento;
    private Integer id;
    private String archivoId;
    private String archivoNombre;
    private String archivoTipo;
    private String descripcion;
    private String rutaAdjunto;
    private MtrTipoDocumento tipoDocumento;

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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getRutaAdjunto() {
        return rutaAdjunto;
    }

    public void setRutaAdjunto(String rutaAdjunto) {
        this.rutaAdjunto = rutaAdjunto;
    }

    public Integer getIdLicitacion() {
        return idLicitacion;
    }

    public void setIdLicitacion(Integer idLicitacion) {
        this.idLicitacion = idLicitacion;
    }

    public String getBase64File() {
        return base64File;
    }

    public void setBase64File(String base64File) {
        this.base64File = base64File;
    }

    public String getFileExtencion() {
        return fileExtencion;
    }

    public void setFileExtencion(String fileExtencion) {
        this.fileExtencion = fileExtencion;
    }

    public String getNombreDocumento() {
        return nombreDocumento;
    }

    public void setNombreDocumento(String nombreDocumento) {
        this.nombreDocumento = nombreDocumento;
    }

    public String getCodigoDocumento() {
        return codigoDocumento;
    }

    public MtrTipoDocumento getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(MtrTipoDocumento tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public void setCodigoDocumento(String codigoDocumento) {
        this.codigoDocumento = codigoDocumento;
    }
}
