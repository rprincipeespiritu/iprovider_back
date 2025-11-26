
package com.incloud.hcp.rest.bean;

import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class ComprobantePagoFiltrosDTO implements Serializable {

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date fechaInicio;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date fechaFin;
    private String numeroComprobantePago;
    private String email;
    private String codigoProveedor;
    private String codigoSociedad;

    public ComprobantePagoFiltrosDTO() {
    }

    public ComprobantePagoFiltrosDTO(Date fechaInicio, Date fechaFin, String numeroComprobantePago, String email, String codigoProveedor, String codigoSociedad) {
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.numeroComprobantePago = numeroComprobantePago;
        this.email = email;
        this.codigoProveedor = codigoProveedor;
        this.codigoSociedad = codigoSociedad;
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

    public String getNumeroComprobantePago() {
        return numeroComprobantePago;
    }

    public void setNumeroComprobantePago(String numeroComprobantePago) {
        this.numeroComprobantePago = numeroComprobantePago;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCodigoProveedor() {
        return codigoProveedor;
    }

    public void setCodigoProveedor(String codigoProveedor) {
        this.codigoProveedor = codigoProveedor;
    }

    public String getCodigoSociedad() {
        return codigoSociedad;
    }

    public void setCodigoSociedad(String codigoSociedad) {
        this.codigoSociedad = codigoSociedad;
    }

    @Override
    public String toString() {
        return "ComprobantePagoFiltrosDTO{" +
                "fechaInicio=" + fechaInicio +
                ", fechaFin=" + fechaFin +
                ", numeroComprobantePago='" + numeroComprobantePago + '\'' +
                ", email='" + email + '\'' +
                ", codigoProveedor='" + codigoProveedor + '\'' +
                ", codigoSociedad='" + codigoSociedad + '\'' +
                '}';
    }
}
