package com.incloud.hcp.ws.tipoCambio.bean;

import java.io.Serializable;
import java.math.BigDecimal;

public class ObtenerTipoCambioBodyResponse implements Serializable {

    private String moneda;
    private BigDecimal compra;
    private BigDecimal venta;

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public BigDecimal getCompra() {
        return compra;
    }

    public void setCompra(BigDecimal compra) {
        this.compra = compra;
    }

    public BigDecimal getVenta() {
        return venta;
    }

    public void setVenta(BigDecimal venta) {
        this.venta = venta;
    }

    @Override
    public String toString() {
        return "ObtenerAlmacenBodyResponse{" +
                "moneda='" + moneda + '\'' +
                ", compra=" + compra +
                ", venta=" + venta +
                '}';
    }
}