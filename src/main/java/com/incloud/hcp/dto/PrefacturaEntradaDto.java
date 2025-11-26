package com.incloud.hcp.dto;


import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class PrefacturaEntradaDto {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private Date fechaInicio;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private Date fechaFin;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private Date fechaEntradaInicio;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private Date fechaEntradaFin;

    private String nroFactura;
    private String proveedorRuc;
    private Integer idEstadoPrefactura;

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Date getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getNroFactura() {
        return nroFactura;
    }

    public void setNroFactura(String nroFactura) {
        this.nroFactura = nroFactura;
    }

    public String getProveedorRuc() {
        return proveedorRuc;
    }

    public void setProveedorRuc(String proveedorRuc) {
        this.proveedorRuc = proveedorRuc;
    }

    public Integer getIdEstadoPrefactura() {
        return idEstadoPrefactura;
    }

    public void setIdEstadoPrefactura(Integer idEstadoPrefactura) {
        this.idEstadoPrefactura = idEstadoPrefactura;
    }

    public Date getFechaEntradaInicio() {
        return fechaEntradaInicio;
    }

    public void setFechaEntradaInicio(Date fechaEntradaInicio) {
        this.fechaEntradaInicio = fechaEntradaInicio;
    }

    public Date getFechaEntradaFin() {
        return fechaEntradaFin;
    }

    public void setFechaEntradaFin(Date fechaEntradaFin) {
        this.fechaEntradaFin = fechaEntradaFin;
    }
}
