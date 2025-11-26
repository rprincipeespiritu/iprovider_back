package com.incloud.hcp.ws.obtenersolicitudcompra.bean;

import java.math.BigDecimal;

public class ObtenerCompraResponseDto {

    private Integer op;
    private String serie;
    private int numero;
    private String id_agenda;
    private String agendaNombre;
    private String fecha;
    private String observaciones;
    private String centroCosto;
    private String unidadGestion;
    private String unidadProyecto;
    private Integer id_Amarre;
    private String codigoProducto;
    private Integer kardex;
    private String nombreProducto;
    private BigDecimal cantidad;
    private String centroCostoDetalle;
    private String unidadGestionDetalle;
    private String unidadProyectoDetalle;
    private String unidadMedida;

    public Integer getOp() {
        return op;
    }

    public void setOp(Integer op) {
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

    public Integer getId_Amarre() {
        return id_Amarre;
    }

    public void setId_Amarre(Integer id_Amarre) {
        this.id_Amarre = id_Amarre;
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

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
    }

    public String getCentroCostoDetalle() {
        return centroCostoDetalle;
    }

    public void setCentroCostoDetalle(String centroCostoDetalle) {
        this.centroCostoDetalle = centroCostoDetalle;
    }

    public String getUnidadGestionDetalle() {
        return unidadGestionDetalle;
    }

    public void setUnidadGestionDetalle(String unidadGestionDetalle) {
        this.unidadGestionDetalle = unidadGestionDetalle;
    }

    public String getUnidadProyectoDetalle() {
        return unidadProyectoDetalle;
    }

    public void setUnidadProyectoDetalle(String unidadProyectoDetalle) {
        this.unidadProyectoDetalle = unidadProyectoDetalle;
    }

    public String getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    @Override
    public String toString() {
        return "ObtenerCompraResponseDto{" +
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
                ", id_Amarre=" + id_Amarre +
                ", codigoProducto='" + codigoProducto + '\'' +
                ", kardex=" + kardex +
                ", nombreProducto='" + nombreProducto + '\'' +
                ", cantidad=" + cantidad +
                ", centroCostoDetalle='" + centroCostoDetalle + '\'' +
                ", unidadGestionDetalle='" + unidadGestionDetalle + '\'' +
                ", unidadProyectoDetalle='" + unidadProyectoDetalle + '\'' +
                ", unidadMedida='" + unidadMedida + '\'' +
                '}';
    }
}
