package com.incloud.hcp.dto;


import java.io.Serializable;
import java.util.List;

public class EntradaMercanciaRequestRPA implements Serializable {
    List<EntradaMercanciaRPA> entradaMercanciaRPAList;

    public List<EntradaMercanciaRPA> getEntradaMercanciaRPAList() {
        return entradaMercanciaRPAList;
    }

    public void setEntradaMercanciaRPAList(List<EntradaMercanciaRPA> entradaMercanciaRPAList) {
        this.entradaMercanciaRPAList = entradaMercanciaRPAList;
    }
}
