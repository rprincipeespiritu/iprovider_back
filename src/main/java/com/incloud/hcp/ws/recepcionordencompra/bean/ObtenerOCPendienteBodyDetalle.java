package com.incloud.hcp.ws.recepcionordencompra.bean;

import java.math.BigDecimal;

public class ObtenerOCPendienteBodyDetalle {


    private Integer iD_Amarre;
    private String iD_UnidadDoc;
    private String codigoProducto;
    private Integer kardex;
    private BigDecimal cantidad;
    private BigDecimal precio;
    private BigDecimal dcto;
    private BigDecimal dctoValor;
    private BigDecimal importe;
    private String observaciones;

    public Integer getiD_Amarre() {
        return iD_Amarre;
    }

    public void setiD_Amarre(Integer iD_Amarre) {
        this.iD_Amarre = iD_Amarre;
    }

    public String getiD_UnidadDoc() {
        return iD_UnidadDoc;
    }

    public void setiD_UnidadDoc(String iD_UnidadDoc) {
        this.iD_UnidadDoc = iD_UnidadDoc;
    }

    public String getCodigoProducto() {
        return codigoProducto;
    }

    public void setCodigoProducto(String codigoProducto) {
        this.codigoProducto = codigoProducto;
    }

    public Integer getKardex() {
        return kardex;
    }

    public void setKardex(Integer kardex) {
        this.kardex = kardex;
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
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

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    @Override
    public String toString() {
        return "ObtenerOCPendienteBodyDetalle{" +
                "iD_Amarre=" + iD_Amarre +
                ", iD_UnidadDoc=" + iD_UnidadDoc +
                ", codigoProducto='" + codigoProducto + '\'' +
                ", kardex=" + kardex +
                ", cantidad=" + cantidad +
                ", precio=" + precio +
                ", dcto=" + dcto +
                ", dctoValor=" + dctoValor +
                ", importe=" + importe +
                ", observaciones='" + observaciones + '\'' +
                '}';
    }
}
