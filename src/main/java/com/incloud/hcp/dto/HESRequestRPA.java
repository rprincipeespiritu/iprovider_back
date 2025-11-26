package com.incloud.hcp.dto;

import java.io.Serializable;
import java.util.List;

public class HESRequestRPA implements Serializable {
    List<HESRPA> hesRPAList;

    public List<HESRPA> getHESRPAList() {
        return hesRPAList;
    }

    public void setHESRPAList(List<HESRPA> hesRPAList) {
        this.hesRPAList = hesRPAList;
    }
}