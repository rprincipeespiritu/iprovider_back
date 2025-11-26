package com.incloud.hcp.ws.producto.bean;

import java.io.Serializable;
import java.math.BigDecimal;

public class ObtenerProductoBodyResponse implements Serializable {

    private Integer kardex;
    private String codigo;
    private String nombre;
    private String unidadInventario;
    private String unidadPresentacion;
    private BigDecimal factorConversion;
    private String tipoCategoria;

    public Integer getKardex() {
        return kardex;
    }

    public void setKardex(Integer kardex) {
        this.kardex = kardex;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUnidadInventario() {
        return unidadInventario;
    }

    public void setUnidadInventario(String unidadInventario) {
        this.unidadInventario = unidadInventario;
    }

    public String getUnidadPresentacion() {
        return unidadPresentacion;
    }

    public void setUnidadPresentacion(String unidadPresentacion) {
        this.unidadPresentacion = unidadPresentacion;
    }

    public BigDecimal getFactorConversion() {
        return factorConversion;
    }

    public void setFactorConversion(BigDecimal factorConversion) {
        this.factorConversion = factorConversion;
    }

    public String getTipoCategoria() {
        return tipoCategoria;
    }

    public void setTipoCategoria(String tipoCategoria) {
        this.tipoCategoria = tipoCategoria;
    }

    @Override
    public String toString() {
        return "ObtenerProductoBodyResponse{" +
                "Kardex=" + kardex +
                ", codigo='" + codigo + '\'' +
                ", nombre='" + nombre + '\'' +
                ", unidadInventario='" + unidadInventario + '\'' +
                ", unidadPresentacion='" + unidadPresentacion + '\'' +
                ", factorConversion=" + factorConversion +
                ", tipoCategoria='" + tipoCategoria + '\'' +
                '}';
    }
}
