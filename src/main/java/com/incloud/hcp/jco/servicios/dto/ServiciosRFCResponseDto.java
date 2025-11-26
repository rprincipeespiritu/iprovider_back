package com.incloud.hcp.jco.servicios.dto;

import com.incloud.hcp.domain.BienServicio;
import com.incloud.hcp.domain.TempBienServicio;
import com.incloud.hcp.sap.SapLog;

import java.io.Serializable;
import java.util.List;

public class ServiciosRFCResponseDto implements Serializable {

    private SapLog sapLog;
    private Integer contador;
    private List<TempBienServicio> listaBienServicio;
    private List<BienServicio> listaBienServicio2;


    public SapLog getSapLog() {
        return sapLog;
    }

    public void setSapLog(SapLog sapLog) {
        this.sapLog = sapLog;
    }

    public List<TempBienServicio> getListaBienServicio() {
        return listaBienServicio;
    }

    public void setListaBienServicio(List<TempBienServicio> listaBienServicio) {
        this.listaBienServicio = listaBienServicio;
    }

    public List<BienServicio> getListaBienServicio2() {
        return listaBienServicio2;
    }

    public void setListaBienServicio2(List<BienServicio> listaBienServicio2) {
        this.listaBienServicio2 = listaBienServicio2;
    }

    public Integer getContador() {
        return contador;
    }

    public void setContador(Integer contador) {
        this.contador = contador;
    }

    @Override
    public String toString() {
        return "ServiciosRFCResponseDto{" +
                "sapLog=" + sapLog +
                ", contador=" + contador +
                ", listaBienServicio=" + listaBienServicio +
                '}';
    }
}
