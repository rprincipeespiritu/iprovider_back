package com.incloud.hcp.ws.obtenersolicitudcompra.bean;

import java.math.BigDecimal;

public class ObtenerCompraBodyDetalle {

    private Integer id_Amarre;
    private String codigoProducto;
    private Integer kardex;
    private String nombreProducto;
    private BigDecimal cantidad;
    private String centroCostoDetalle;
    private String unidadGestionDetalle;
    private String unidadProyectoDetalle;
    private String unidadMedida;

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
        return "ObtenerCompraBodyDetalle{" +
                "id_Amarre=" + id_Amarre +
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
