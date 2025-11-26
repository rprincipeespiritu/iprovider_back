package com.incloud.hcp.ws.recepcionordencompra.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class ObtenerOCPendienteBodyResponse implements Serializable {

    private Integer op;
    private String iD_Agenda;
    private String noRegistro;
    private String fechaOrden;
    private String fechaEntrega;
    private String fechaVigencia;
    private Integer iD_Moneda;
    private BigDecimal neto;
    private BigDecimal dcto;
    private BigDecimal subTotal;
    private BigDecimal impuestos;
    private BigDecimal total;
    private String observaciones;
    private Integer prioridad;
    private Integer iD_Pago;
    private Integer iD_AgendaDireccion;
    private String modoPago;
    private String notasRecepcion;
    private String estadoOC;
    private List <ObtenerOCPendienteBodyDetalle> detalle;

    public Integer getOp() {
        return op;
    }

    public void setOp(Integer op) {
        this.op = op;
    }

    public String getiD_Agenda() {
        return iD_Agenda;
    }

    public void setiD_Agenda(String iD_Agenda) {
        this.iD_Agenda = iD_Agenda;
    }

    public String getNoRegistro() {
        return noRegistro;
    }

    public void setNoRegistro(String noRegistro) {
        this.noRegistro = noRegistro;
    }

    public String getFechaOrden() {
        return fechaOrden;
    }

    public void setFechaOrden(String fechaOrden) {
        this.fechaOrden = fechaOrden;
    }

    public String getFechaEntrega() {
        return fechaEntrega;
    }

    public void setFechaEntrega(String fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    public String getFechaVigencia() {
        return fechaVigencia;
    }

    public void setFechaVigencia(String fechaVigencia) {
        this.fechaVigencia = fechaVigencia;
    }

    public Integer getiD_Moneda() {
        return iD_Moneda;
    }

    public void setiD_Moneda(Integer iD_Moneda) {
        this.iD_Moneda = iD_Moneda;
    }

    public BigDecimal getNeto() {
        return neto;
    }

    public void setNeto(BigDecimal neto) {
        this.neto = neto;
    }

    public BigDecimal getDcto() {
        return dcto;
    }

    public void setDcto(BigDecimal dcto) {
        this.dcto = dcto;
    }

    public BigDecimal getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(BigDecimal subTotal) {
        this.subTotal = subTotal;
    }

    public BigDecimal getImpuestos() {
        return impuestos;
    }

    public void setImpuestos(BigDecimal impuestos) {
        this.impuestos = impuestos;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Integer getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(Integer prioridad) {
        this.prioridad = prioridad;
    }

    public Integer getiD_Pago() {
        return iD_Pago;
    }

    public void setiD_Pago(Integer iD_Pago) {
        this.iD_Pago = iD_Pago;
    }

    public Integer getiD_AgendaDireccion() {
        return iD_AgendaDireccion;
    }

    public void setiD_AgendaDireccion(Integer iD_AgendaDireccion) {
        this.iD_AgendaDireccion = iD_AgendaDireccion;
    }

    public String getModoPago() {
        return modoPago;
    }

    public void setModoPago(String modoPago) {
        this.modoPago = modoPago;
    }

    public String getNotasRecepcion() {
        return notasRecepcion;
    }

    public void setNotasRecepcion(String notasRecepcion) {
        this.notasRecepcion = notasRecepcion;
    }

    public String getEstadoOC() {
        return estadoOC;
    }

    public void setEstadoOC(String estadoOC) {
        this.estadoOC = estadoOC;
    }

    public List<ObtenerOCPendienteBodyDetalle> getDetalle() {
        return detalle;
    }

    public void setDetalle(List<ObtenerOCPendienteBodyDetalle> detalle) {
        this.detalle = detalle;
    }

    @Override
    public String toString() {
        return "ObtenerOCPendienteBodyResponse{" +
                "op=" + op +
                ", iD_Agenda='" + iD_Agenda + '\'' +
                ", noRegistro='" + noRegistro + '\'' +
                ", fechaOrden='" + fechaOrden + '\'' +
                ", fechaEntrega='" + fechaEntrega + '\'' +
                ", fechaVigencia='" + fechaVigencia + '\'' +
                ", iD_Moneda=" + iD_Moneda +
                ", neto=" + neto +
                ", dcto=" + dcto +
                ", subTotal=" + subTotal +
                ", impuestos=" + impuestos +
                ", total=" + total +
                ", observaciones='" + observaciones + '\'' +
                ", prioridad=" + prioridad +
                ", iD_Pago=" + iD_Pago +
                ", iD_AgendaDireccion=" + iD_AgendaDireccion +
                ", modoPago='" + modoPago + '\'' +
                ", notasRecepcion='" + notasRecepcion + '\'' +
                ", estadoOC='" + estadoOC + '\'' +
                ", detalle=" + detalle +
                '}';
    }
}
