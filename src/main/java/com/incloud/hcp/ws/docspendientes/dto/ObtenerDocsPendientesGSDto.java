package com.incloud.hcp.ws.docspendientes.dto;

public class ObtenerDocsPendientesGSDto {

    private int idEmpresa;
    private String iD_Agenda;
    private String fechaInicio;
    private String fechaFin;


    public int getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(int idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public String getFechaInicial() {
        return fechaInicio;
    }

    public void setFechaInicial(String fechaInicial) {
        this.fechaInicio = fechaInicial;
    }

    public String getFechaFinal() {
        return fechaFin;
    }

    public void setFechaFinal(String fechaFinal) {
        this.fechaFin = fechaFinal;
    }

    public String getiD_Agenda() {
        return iD_Agenda;
    }

    public void setiD_Agenda(String iD_Agenda) {
        this.iD_Agenda = iD_Agenda;
    }

    @Override
    public String toString() {
        return "ObtenerDocsPendientesGSDto{" +
                "idEmpresa=" + idEmpresa +
                ", iD_Agenda='" + iD_Agenda + '\'' +
                ", fechaInicio='" + fechaInicio + '\'' +
                ", fechaFin='" + fechaFin + '\'' +
                '}';
    }
}
