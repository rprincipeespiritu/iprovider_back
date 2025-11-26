package com.incloud.hcp.ws.registrarcompra.dto;

import java.math.BigDecimal;
import java.util.List;

public class RegistroCompraGSDto {

    private String idEmpresa;
    private String ID_Agenda;
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
    private String Observaciones;
    private Integer iD_Pago;
    private Integer iD_AgendaDireccion;
    private Integer iD_AgendaDireccion2;
    private String modoPago;
    private String notasRecepcion;
    private List<RegistroCompraDetalleGSDto> Lista_CD;

    public String getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(String idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public String getID_Agenda() {
        return ID_Agenda;
    }

    public void setID_Agenda(String ID_Agenda) {
        this.ID_Agenda = ID_Agenda;
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
        return Observaciones;
    }

    public void setObservaciones(String observaciones) {
        Observaciones = observaciones;
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

    public Integer getiD_AgendaDireccion2() {
        return iD_AgendaDireccion2;
    }

    public void setiD_AgendaDireccion2(Integer iD_AgendaDireccion2) {
        this.iD_AgendaDireccion2 = iD_AgendaDireccion2;
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

    public List<RegistroCompraDetalleGSDto> getLista_CD() {
        return Lista_CD;
    }

    public void setLista_CD(List<RegistroCompraDetalleGSDto> lista_CD) {
        Lista_CD = lista_CD;
    }
}