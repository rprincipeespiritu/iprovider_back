package com.incloud.hcp.dto;

import java.io.Serializable;
import java.util.List;

public class HESRequestRespuestaRPA implements Serializable {
    List<HESRespuestaRPA> hesRespuestaRPAList;

    public List<HESRespuestaRPA> getHESRPAList() {
        return hesRespuestaRPAList;
    }
    public void setHESRPAList(List<HESRespuestaRPA> hesRespuestaRPAList) {
        this.hesRespuestaRPAList = hesRespuestaRPAList;
    }
}