package com.incloud.hcp.dto;

public class AdjuntoPreFacturaDto {

    private Integer idPrecatura;
    private String base64File;
    private String fileExtencion;
    private String nombreDocumento;
    private String codigoTipoDocumento;

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

    public Integer getIdPrecatura() {
        return idPrecatura;
    }

    public void setIdPrecatura(Integer idPrecatura) {
        this.idPrecatura = idPrecatura;
    }
}
