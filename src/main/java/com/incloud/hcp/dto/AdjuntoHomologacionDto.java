package com.incloud.hcp.dto;

public class AdjuntoHomologacionDto {
    private String emailProveedor;
    private Integer idHomologacion;
    private String base64File;
    private String fileExtencion;
    private String nombreDocumento;
    private String codigoTipoDocumento;

    public String getEmailProveedor() {
        return emailProveedor;
    }

    public void setEmailProveedor(String emailProveedor) {
        this.emailProveedor = emailProveedor;
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

    public String getCodigoTipoDocumento() {
        return codigoTipoDocumento;
    }

    public void setCodigoTipoDocumento(String codigoTipoDocumento) {
        this.codigoTipoDocumento = codigoTipoDocumento;
    }

    public Integer getIdHomologacion() {
        return idHomologacion;
    }

    public void setIdHomologacion(Integer idHomologacion) {
        this.idHomologacion = idHomologacion;
    }
}
