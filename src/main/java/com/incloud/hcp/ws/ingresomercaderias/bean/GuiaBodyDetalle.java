package com.incloud.hcp.ws.ingresomercaderias.bean;

public class GuiaBodyDetalle {

    private Integer id_Amarre;
    private Integer id_AmarreOC;
    private String codigoProducto;
    private Integer kardex;
    private Integer cantidad;
    private String unidad;
    private String observacion;

    public Integer getId_Amarre() {
        return id_Amarre;
    }

    public void setId_Amarre(Integer id_Amarre) {
        this.id_Amarre = id_Amarre;
    }

    public Integer getId_AmarreOC() {
        return id_AmarreOC;
    }

    public void setId_AmarreOC(Integer id_AmarreOC) {
        this.id_AmarreOC = id_AmarreOC;
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

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public String getUnidad() {
        return unidad;
    }

    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    @Override
    public String toString() {
        return "GuiaBodyDetalle{" +
                "id_Amarre=" + id_Amarre +
                ", id_AmarreOC=" + id_AmarreOC +
                ", codigoProducto='" + codigoProducto + '\'' +
                ", kardex=" + kardex +
                ", cantidad=" + cantidad +
                ", unidad='" + unidad + '\'' +
                ", observacion='" + observacion + '\'' +
                '}';
    }
}


