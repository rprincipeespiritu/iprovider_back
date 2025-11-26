package com.incloud.hcp.jco.ordenCompra.dto;

import java.io.Serializable;
import java.util.List;


public class OrdenCompraRequestRespuestaRPA implements Serializable {
    List<OrdenCompraRespuestaRPA> ordenCompraRPAList;

    public List<OrdenCompraRespuestaRPA> getOrdenCompraRPAList() {
        return ordenCompraRPAList;
    }
    public void setOrdenCompraRPAList(List<OrdenCompraRespuestaRPA> ordenCompraRPAList) {
        this.ordenCompraRPAList = ordenCompraRPAList;
    }
}