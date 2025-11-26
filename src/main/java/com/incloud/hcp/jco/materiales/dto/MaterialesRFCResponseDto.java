package com.incloud.hcp.jco.materiales.dto;

import com.incloud.hcp.domain.BienServicio;
import com.incloud.hcp.domain.TempBienServicio;
import com.incloud.hcp.sap.SapLog;

import java.io.Serializable;
import java.util.List;

public class MaterialesRFCResponseDto implements Serializable {

    private SapLog sapLog;
    private Integer contador;
    //private List<TempBienServicio> listaBienServicio;
    private List<BienServicio> listaBienServicio;


    public SapLog getSapLog() {
        return sapLog;
    }

    public void setSapLog(SapLog sapLog) {
        this.sapLog = sapLog;
    }

    public List<BienServicio> getListaBienServicio() {
        return listaBienServicio;
    }

    public void setListaBienServicio(List<BienServicio> listaBienServicio) {
        this.listaBienServicio = listaBienServicio;
    }

    public Integer getContador() {
        return contador;
    }

    public void setContador(Integer contador) {
        this.contador = contador;
    }

    @Override
    public String toString() {
        return "MaterialesRFCResponseDto{" +
                "sapLog=" + sapLog +
                ", contador=" + contador +
                ", listaBienServicio=" + listaBienServicio +
                '}';
    }
}
