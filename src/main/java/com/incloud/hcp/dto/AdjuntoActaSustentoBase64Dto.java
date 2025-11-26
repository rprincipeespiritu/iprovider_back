package com.incloud.hcp.dto;

public class AdjuntoActaSustentoBase64Dto {
    private String idActaSustento;
    private String base64File;
    private String fileExtencion;
    private String nombreDocumento;
    private String codigoDocumento;
    private String mtrTipoDocumento;


    public String getIdActaSustento() {
        return idActaSustento;
    }

    public void setIdActaSustento(String idActaSustento) {
        this.idActaSustento = idActaSustento;
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

    public String getMtrTipoDocumento() {
        return mtrTipoDocumento;
    }

    public void setMtrTipoDocumento(String mtrTipoDocumento) {
        this.mtrTipoDocumento = mtrTipoDocumento;
    }

    @Override
    public String toString() {
        return "AdjuntoActaSustentoBase64Dto{" +
                "idActaSustento='" + idActaSustento + '\'' +
                ", base64File='" + base64File + '\'' +
                ", fileExtencion='" + fileExtencion + '\'' +
                ", nombreDocumento='" + nombreDocumento + '\'' +
                ", codigoDocumento='" + codigoDocumento + '\'' +
                ", mtrTipoDocumento='" + mtrTipoDocumento + '\'' +
                '}';
    }
}
