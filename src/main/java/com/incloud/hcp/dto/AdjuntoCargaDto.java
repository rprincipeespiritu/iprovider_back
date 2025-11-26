package com.incloud.hcp.dto;

public class AdjuntoCargaDto {
    private Integer idProveedor;
    private String base64File;
    private String fileExtencion;
    private String nombreDocumento;
    private String codigoDocumento;

    public Integer getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(Integer idProveedor) {
        this.idProveedor = idProveedor;
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

    public void setCodigoDocumento(String codigoDocumento) {
        this.codigoDocumento = codigoDocumento;
    }
}
