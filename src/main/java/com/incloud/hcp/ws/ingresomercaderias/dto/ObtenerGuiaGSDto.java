package com.incloud.hcp.ws.ingresomercaderias.dto;

public class ObtenerGuiaGSDto {

    private int idEmpresa;
    private String fechaInicial;
    private String fechaFinal;
    private String id_Agenda;

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

    public String getId_Agenda() {
        return id_Agenda;
    }

    public void setId_Agenda(String id_Agenda) {
        this.id_Agenda = id_Agenda;
    }

    @Override
    public String toString() {
        return "ObtenerGuiaGSDto{" +
                "idEmpresa=" + idEmpresa +
                ", fechaInicial='" + fechaInicial + '\'' +
                ", fechaFinal='" + fechaFinal + '\'' +
                ", id_Agenda='" + id_Agenda + '\'' +
                '}';
    }
}


