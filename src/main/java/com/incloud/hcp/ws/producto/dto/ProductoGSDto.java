package com.incloud.hcp.ws.producto.dto;

public class ProductoGSDto {

    private int idEmpresa;
    private String fechaInicial;
    private String fechaFinal;
    private int itemCategoria;

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

    public int getItemCategoria() {
        return itemCategoria;
    }

    public void setItemCategoria(int itemCategoria) {
        this.itemCategoria = itemCategoria;
    }

    @Override
    public String toString() {
        return "ProductoGSDto{" +
                "idEmpresa=" + idEmpresa +
                ", fechaInicial='" + fechaInicial + '\'' +
                ", fechaFinal='" + fechaFinal + '\'' +
                ", itemCategoria=" + itemCategoria +
                '}';
    }
}
