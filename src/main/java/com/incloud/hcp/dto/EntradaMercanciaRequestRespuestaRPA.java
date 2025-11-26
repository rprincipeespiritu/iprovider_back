package com.incloud.hcp.dto;

import java.io.Serializable;
import java.util.List;

public class EntradaMercanciaRequestRespuestaRPA implements Serializable {
    List<EntradaMercanciaRespuestaRPA> entradaMercanciaRPAList;

    public List<EntradaMercanciaRespuestaRPA> getEntradaMercanciaRPAList() {
        return entradaMercanciaRPAList;
    }
    public void setEntradaMercanciaRPAList(List<EntradaMercanciaRespuestaRPA> entradaMercanciaRPAList) {
        this.entradaMercanciaRPAList = entradaMercanciaRPAList;
    }
}