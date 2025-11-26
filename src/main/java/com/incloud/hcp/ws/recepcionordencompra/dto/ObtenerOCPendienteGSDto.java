package com.incloud.hcp.ws.recepcionordencompra.dto;

public class ObtenerOCPendienteGSDto {

    private int idEmpresa;
    private String fechaInicial;
    private String fechaFinal;
    private String iD_Agenda;

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

    public String getiD_Agenda() {
        return iD_Agenda;
    }

    public void setiD_Agenda(String iD_Agenda) {
        this.iD_Agenda = iD_Agenda;
    }

    @Override
    public String
    toString() {
        return "ObtenerOCPendienteGSDto{" +
                "idEmpresa=" + idEmpresa +
                ", fechaInicial='" + fechaInicial + '\'' +
                ", fechaFinal='" + fechaFinal + '\'' +
                ", iD_Agenda='" + iD_Agenda + '\'' +
                '}';
    }
}
