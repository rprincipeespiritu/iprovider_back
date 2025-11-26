package com.incloud.hcp.ws.tipoCambio.dto;

public class TipoCambioGSDto {

    private int idEmpresa;
    private String fecha;

    public int getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(int idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    @Override
    public String toString() {
        return "AlmacenGSDto{" +
                "idEmpresa=" + idEmpresa +
                ", fecha='" + fecha + '\'' +
                '}';
    }
}
