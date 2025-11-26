package com.incloud.hcp.ws.registrodocumentos.dto;

import java.math.BigDecimal;
import java.util.List;

public class RegistroDocumentoGSDto {

    private Integer idEmpresa;
    private Integer op_Guia;
    private Integer iD_Documento;
    private String serie;
    private Integer numero;
    private String fechaAplicacion;
    private String fechaPago;
    private String fecha;
    private Integer iD_Moneda;
    private BigDecimal neto;
    private BigDecimal dcto;
    private BigDecimal subTotal;
    private BigDecimal impuestos;
    private BigDecimal total;
    private String Observaciones;
    private List<RegistroDocumentoListaGSDto> ListaF;

    public Integer getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(Integer idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public Integer getOp_Guia() {
        return op_Guia;
    }

    public void setOp_Guia(Integer op_Guia) {
        this.op_Guia = op_Guia;
    }

    public Integer getiD_Documento() {
        return iD_Documento;
    }

    public void setiD_Documento(Integer iD_Documento) {
        this.iD_Documento = iD_Documento;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public String getFechaAplicacion() {
        return fechaAplicacion;
    }

    public void setFechaAplicacion(String fechaAplicacion) {
        this.fechaAplicacion = fechaAplicacion;
    }

    public String getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(String fechaPago) {
        this.fechaPago = fechaPago;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
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

    public List<RegistroDocumentoListaGSDto> getListaF() {
        return ListaF;
    }

    public void setListaF(List<RegistroDocumentoListaGSDto> listaF) {
        ListaF = listaF;
    }

    @Override
    public String toString() {
        return "RegistroCompraGSDto{" +
                "idEmpresa=" + idEmpresa +
                ", op_Guia=" + op_Guia +
                ", iD_Documento=" + iD_Documento +
                ", serie='" + serie + '\'' +
                ", numero=" + numero +
                ", fechaAplicacion='" + fechaAplicacion + '\'' +
                ", fechaPago='" + fechaPago + '\'' +
                ", fecha='" + fecha + '\'' +
                ", iD_Moneda=" + iD_Moneda +
                ", neto=" + neto +
                ", dcto=" + dcto +
                ", subTotal=" + subTotal +
                ", impuestos=" + impuestos +
                ", total=" + total +
                ", Observaciones='" + Observaciones + '\'' +
                ", ListaF=" + ListaF +
                '}';
    }
}