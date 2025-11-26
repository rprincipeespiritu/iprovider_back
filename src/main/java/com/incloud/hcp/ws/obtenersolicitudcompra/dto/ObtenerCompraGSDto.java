package com.incloud.hcp.ws.obtenersolicitudcompra.dto;

public class ObtenerCompraGSDto {

    private int idEmpresa;
    private String fechaInicial;
    private String fechaFinal;

    public int getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(int idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public String getFechaInicial() {
        return fechaInicial;
    }

    public void setFechaInicial(String fechaInicial) {
        this.fechaInicial = fechaInicial;
    }

    public String getFechaFinal() {
        return fechaFinal;
    }

    public void setFechaFinal(String fechaFinal) {
        this.fechaFinal = fechaFinal;
    }

    @Override
    public String toString() {
        return "ObtenerCompraGSDto{" +
                "idEmpresa=" + idEmpresa +
                ", fechaInicial='" + fechaInicial + '\'' +
                ", fechaFinal='" + fechaFinal + '\'' +
                '}';
    }
}
