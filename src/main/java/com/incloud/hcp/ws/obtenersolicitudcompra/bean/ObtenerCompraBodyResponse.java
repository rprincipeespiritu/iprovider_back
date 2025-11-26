package com.incloud.hcp.ws.obtenersolicitudcompra.bean;

import java.io.Serializable;
import java.util.List;

public class ObtenerCompraBodyResponse implements Serializable {

    private String op;
    private String serie;
    private int numero;
    private String id_agenda;
    private String agendaNombre;
    private String fecha;
    private String observaciones;
    private String centroCosto;
    private String unidadGestion;
    private String unidadProyecto;
    private List <ObtenerCompraBodyDetalle> detalle;


    public String getOp() {
        return op;
    }

    public void setOp(String op) {
        this.op = op;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public String getId_agenda() {
        return id_agenda;
    }

    public void setId_agenda(String id_agenda) {
        this.id_agenda = id_agenda;
    }

    public String getAgendaNombre() {
        return agendaNombre;
    }

    public void setAgendaNombre(String agendaNombre) {
        this.agendaNombre = agendaNombre;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getCentroCosto() {
        return centroCosto;
    }

    public void setCentroCosto(String centroCosto) {
        this.centroCosto = centroCosto;
    }

    public String getUnidadGestion() {
        return unidadGestion;
    }

    public void setUnidadGestion(String unidadGestion) {
        this.unidadGestion = unidadGestion;
    }

    public String getUnidadProyecto() {
        return unidadProyecto;
    }

    public void setUnidadProyecto(String unidadProyecto) {
        this.unidadProyecto = unidadProyecto;
    }

    public List<ObtenerCompraBodyDetalle> getDetalle() {
        return detalle;
    }

    public void setDetalle(List<ObtenerCompraBodyDetalle> detalle) {
        this.detalle = detalle;
    }

    @Override
    public String toString() {
        return "ObtenerCompraBodyResponse{" +
                "op='" + op + '\'' +
                ", serie='" + serie + '\'' +
                ", numero=" + numero +
                ", id_agenda='" + id_agenda + '\'' +
                ", agendaNombre='" + agendaNombre + '\'' +
                ", fecha='" + fecha + '\'' +
                ", observaciones='" + observaciones + '\'' +
                ", centroCosto='" + centroCosto + '\'' +
                ", unidadGestion='" + unidadGestion + '\'' +
                ", unidadProyecto='" + unidadProyecto + '\'' +
                ", detalle=" + detalle +
                '}';
    }
}
