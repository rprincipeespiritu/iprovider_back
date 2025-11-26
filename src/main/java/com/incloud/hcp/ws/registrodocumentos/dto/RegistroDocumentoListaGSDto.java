package com.incloud.hcp.ws.registrodocumentos.dto;

import java.math.BigDecimal;

public class RegistroDocumentoListaGSDto {

    private String tablaOrigen;
    private Integer iD_Amarre_GL;
    private BigDecimal precio;
    private BigDecimal dcto;
    private BigDecimal dctoValor;
    private BigDecimal importe;

    public String getTablaOrigen() {
        return tablaOrigen;
    }

    public void setTablaOrigen(String tablaOrigen) {
        this.tablaOrigen = tablaOrigen;
    }

    public Integer getiD_Amarre_GL() {
        return iD_Amarre_GL;
    }

    public void setiD_Amarre_GL(Integer iD_Amarre_GL) {
        this.iD_Amarre_GL = iD_Amarre_GL;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public BigDecimal getDcto() {
        return dcto;
    }

    public void setDcto(BigDecimal dcto) {
        this.dcto = dcto;
    }

    public BigDecimal getDctoValor() {
        return dctoValor;
    }

    public void setDctoValor(BigDecimal dctoValor) {
        this.dctoValor = dctoValor;
    }

    public BigDecimal getImporte() {
        return importe;
    }

    public void setImporte(BigDecimal importe) {
        this.importe = importe;
    }

    @Override
    public String toString() {
        return "RegistroDocumentoListaGSDto{" +
                "tablaOrigen='" + tablaOrigen + '\'' +
                ", iD_Amarre_GL=" + iD_Amarre_GL +
                ", precio=" + precio +
                ", dcto=" + dcto +
                ", dctoValor=" + dctoValor +
                ", importe=" + importe +
                '}';
    }
}
