package com.incloud.hcp.dto;


import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class DocumentoAceptacionEntradaDto {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private Date fechaInicio;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private Date fechaFin;

    private String sociedad;
    private String email;
    private String proveedorRuc;
    private String nroDocumento;
    private String nroOrdenCompra;
    private String nroHojaServicio;

    public String getSociedad() {
        return sociedad;
    }

    public void setSociedad(String sociedad) {
        this.sociedad = sociedad;
    }

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNroDocumento() {
        return nroDocumento;
    }

    public void setNroDocumento(String nroDocumento) {
        this.nroDocumento = nroDocumento;
    }

    public String getNroOrdenCompra() {
        return nroOrdenCompra;
    }

    public void setNroOrdenCompra(String nroOrdenCompra) {
        this.nroOrdenCompra = nroOrdenCompra;
    }

    public String getNroHojaServicio() {
        return nroHojaServicio;
    }

    public void setNroHojaServicio(String nroHojaServicio) {
        this.nroHojaServicio = nroHojaServicio;
    }

    public String getProveedorRuc() {
        return proveedorRuc;
    }

    public void setProveedorRuc(String proveedorRuc) {
        this.proveedorRuc = proveedorRuc;
    }
}
