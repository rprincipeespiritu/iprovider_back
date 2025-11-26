package com.incloud.hcp.ws.ingresomercaderias.bean;

import java.io.Serializable;
import java.util.List;

public class GuiaBodyResponse implements Serializable {

    private Integer opGuiaCompra;
    private String codigoProveedor;
    private Integer codigoAlmacen;
    private String fecha;
    private String serie;
    private Integer numero;
    private Integer codigoDireccion;
    private String observacion;
    private List <GuiaBodyDetalle> detalle;

    public Integer getOpGuiaCompra() {
        return opGuiaCompra;
    }

    public void setOpGuiaCompra(Integer opGuiaCompra) {
        this.opGuiaCompra = opGuiaCompra;
    }

    public String getCodigoProveedor() {
        return codigoProveedor;
    }

    public void setCodigoProveedor(String codigoProveedor) {
        this.codigoProveedor = codigoProveedor;
    }

    public Integer getCodigoAlmacen() {
        return codigoAlmacen;
    }

    public void setCodigoAlmacen(Integer codigoAlmacen) {
        this.codigoAlmacen = codigoAlmacen;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
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

    public Integer getCodigoDireccion() {
        return codigoDireccion;
    }

    public void setCodigoDireccion(Integer codigoDireccion) {
        this.codigoDireccion = codigoDireccion;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public List<GuiaBodyDetalle> getDetalle() {
        return detalle;
    }

    public void setDetalle(List<GuiaBodyDetalle> detalle) {
        this.detalle = detalle;
    }

    @Override
    public String toString() {
        return "GuiaBodyResponse{" +
                "opGuiaCompra=" + opGuiaCompra +
                ", codigoProveedor='" + codigoProveedor + '\'' +
                ", codigoAlmacen=" + codigoAlmacen +
                ", fecha='" + fecha + '\'' +
                ", serie='" + serie + '\'' +
                ", numero=" + numero +
                ", codigoDireccion=" + codigoDireccion +
                ", observacion='" + observacion + '\'' +
                ", detalle=" + detalle +
                '}';
    }
}


