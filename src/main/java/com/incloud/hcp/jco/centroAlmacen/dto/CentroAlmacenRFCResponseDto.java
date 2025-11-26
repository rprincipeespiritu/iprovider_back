package com.incloud.hcp.jco.centroAlmacen.dto;



import com.incloud.hcp.domain.CentroAlmacen;
import com.incloud.hcp.domain.TempCentroAlmacen;
import com.incloud.hcp.sap.SapLog;

import java.io.Serializable;
import java.util.List;

public class CentroAlmacenRFCResponseDto implements Serializable {

    private SapLog sapLog;
    private Integer contador;
    private List<TempCentroAlmacen> listaCentroAlmacen;
    private List<CentroAlmacen> listaCentroAlmacen2;

    public SapLog getSapLog() {
        return sapLog;
    }

    public void setSapLog(SapLog sapLog) {
        this.sapLog = sapLog;
    }

    public Integer getContador() {
        return contador;
    }

    public void setContador(Integer contador) {
        this.contador = contador;
    }

    public List<TempCentroAlmacen> getListaCentroAlmacen() {
        return listaCentroAlmacen;
    }

    public void setListaCentroAlmacen(List<TempCentroAlmacen> listaCentroAlmacen) {
        this.listaCentroAlmacen = listaCentroAlmacen;
    }

    public List<CentroAlmacen> getListaCentroAlmacen2() {
        return listaCentroAlmacen2;
    }

    public void setListaCentroAlmacen2(List<CentroAlmacen> listaCentroAlmacen2) {
        this.listaCentroAlmacen2 = listaCentroAlmacen2;
    }


    @Override
    public String toString() {
        return "CentroAlmacenRFCResponseDto{" +
                "sapLog=" + sapLog +
                ", contador=" + contador +
                ", listaCentroAlmacen=" + listaCentroAlmacen +
                '}';
    }
}
