package com.incloud.hcp.dto;

import com.incloud.hcp.domain.OrdenCompra;

import java.util.List;

public class ProveedoresFinalDto {
    private String nombreProveedor;
    private String ruc;


    private List<OrdenCompra> ordenesList;



    public String getNombreProveedor() {
        return nombreProveedor;
    }

    public void setNombreProveedor(String nombreProveedor) {
        this.nombreProveedor = nombreProveedor;
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public List<OrdenCompra> getOrdenesList() {
        return ordenesList;
    }

    public void setOrdenesList(List<OrdenCompra> ordenesList) {
        this.ordenesList = ordenesList;
    }
}
