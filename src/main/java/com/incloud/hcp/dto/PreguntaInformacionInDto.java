package com.incloud.hcp.dto;

public class PreguntaInformacionInDto {

    private Integer idPreguntaInformacion;
    private String respuesta;
    private Integer preguntaInformacion;
    private Integer proveedor;

    public PreguntaInformacionInDto(){
    }

    public Integer getIdPreguntaInformacion() {
        return idPreguntaInformacion;
    }

    public void setIdPreguntaInformacion(Integer idPreguntaInformacion) {
        this.idPreguntaInformacion = idPreguntaInformacion;
    }

    public String getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(String respuesta) {
        this.respuesta = respuesta;
    }

    public Integer getPreguntaInformacion() {
        return preguntaInformacion;
    }

    public void setPreguntaInformacion(Integer preguntaInformacion) {
        this.preguntaInformacion = preguntaInformacion;
    }

    public Integer getProveedor() {
        return proveedor;
    }

    public void setProveedor(Integer proveedor) {
        this.proveedor = proveedor;
    }

    @Override
    public String toString() {
        return "ProductoDto{" +
                "respuesta='" + respuesta + '\'' +
                ", preguntaInformacion='" + preguntaInformacion + '\'' +
                ", proveedor='" + proveedor + '\'' +
                '}';
    }
}
