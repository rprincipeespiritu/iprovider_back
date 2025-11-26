package com.incloud.hcp.jco.ordenCompra.dto;

import com.incloud.hcp.sap.SapLog;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


public class OrdenCompraRequestRPA implements Serializable {
    List<OrdenCompraRPA> ordenCompraRPAList;

    public List<OrdenCompraRPA> getOrdenCompraRPAList() {
        return ordenCompraRPAList;
    }
    public void setOrdenCompraRPAList(List<OrdenCompraRPA> ordenCompraRPAList) {
        this.ordenCompraRPAList = ordenCompraRPAList;
    }
}