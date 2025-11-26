package com.incloud.hcp.dto.homologacion;

/**
 * Created by Administrador on 16/10/2017.
 */
public class ProveedorRespuestaDto {
    private Integer idHomologacionRespuesta;
    private String archivoId;
    private String archivoTipo;
    private String rutaAdjunto;
    private String nombreArchivo;
    private String valorRespuestaLibre;

    public String getArchivoTipo() {
        return archivoTipo;
    }

    public void setArchivoTipo(String archivoTipo) {
        this.archivoTipo = archivoTipo;
    }

    public ProveedorRespuestaDto() {
    }

    public Integer getIdHomologacionRespuesta() {
        return idHomologacionRespuesta;
    }

    public void setIdHomologacionRespuesta(Integer idHomologacionRespuesta) {
        this.idHomologacionRespuesta = idHomologacionRespuesta;
    }

    public String getArchivoId() {
        return archivoId;
    }

    public void setArchivoId(String archivoId) {
        this.archivoId = archivoId;
    }

    public String getRutaAdjunto() {
        return rutaAdjunto;
    }

    public void setRutaAdjunto(String rutaAdjunto) {
        this.rutaAdjunto = rutaAdjunto;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    public String getValorRespuestaLibre() {
        return valorRespuestaLibre;
    }

    public void setValorRespuestaLibre(String valorRespuestaLibre) {
        this.valorRespuestaLibre = valorRespuestaLibre;
    }

    @Override
    public String toString() {
        return "ProveedorRespuestaDto{" +
                "idHomologacionRespuesta=" + idHomologacionRespuesta +
                ", archivoId='" + archivoId + '\'' +
                ", archivoTipo='" + archivoId + '\'' +
                ", rutaAdjunto='" + rutaAdjunto + '\'' +
                ", nombreArchivo='" + nombreArchivo + '\'' +
                ", valorRespuestaLibre='" + valorRespuestaLibre + '\'' +
                '}';
    }
}
