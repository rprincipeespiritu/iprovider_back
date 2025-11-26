package com.incloud.hcp.ws.registrarcompra.dto;

import java.math.BigDecimal;

public class RegistroCompraDetalleGSDto {

    private Integer op_SolicitudCompra;
    private Integer id_Amarre;
    private Integer id_Amarre_SC;
    private String codigoProducto;
    private Integer kardex;
    private BigDecimal cantidad;
    private BigDecimal precio;
    private BigDecimal importe;
    private String observaciones;

    public Integer getOp_SolicitudCompra() {
        return op_SolicitudCompra;
    }

    public void setOp_SolicitudCompra(Integer op_SolicitudCompra) {
        this.op_SolicitudCompra = op_SolicitudCompra;
    }

    public Integer getId_Amarre() {
        return id_Amarre;
    }

    public void setId_Amarre(Integer id_Amarre) {
        this.id_Amarre = id_Amarre;
    }

    public Integer getId_Amarre_SC() {
        return id_Amarre_SC;
    }

    public void setId_Amarre_SC(Integer id_Amarre_SC) {
        this.id_Amarre_SC = id_Amarre_SC;
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
        return "RegistroCompraDetalleGSDto{" +
                "op_SolicitudCompra=" + op_SolicitudCompra +
                ", id_Amarre=" + id_Amarre +
                ", id_Amarre_SC=" + id_Amarre_SC +
                ", codigoProducto='" + codigoProducto + '\'' +
                ", kardex=" + kardex +
                ", cantidad=" + cantidad +
                ", precio=" + precio +
                ", importe=" + importe +
                ", observaciones='" + observaciones + '\'' +
                '}';
    }
}
