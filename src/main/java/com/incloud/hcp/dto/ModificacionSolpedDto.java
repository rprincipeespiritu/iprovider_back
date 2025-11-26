package com.incloud.hcp.dto;

import com.incloud.hcp.domain.*;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class ModificacionSolpedDto {

    private Integer idLicitacionDetalle;

    private Integer nroSolped;

    private BigDecimal precioSolped;

    private BigDecimal precioUnitario;

    private BigDecimal cantidadModificada;

    private String personaLicitacion;

    private String posicionSolped;


    public Integer getIdLicitacionDetalle() {
        return idLicitacionDetalle;
    }

    public void setIdLicitacionDetalle(Integer idLicitacionDetalle) {
        this.idLicitacionDetalle = idLicitacionDetalle;
    }

    public Integer getNroSolped() {
        return nroSolped;
    }

    public void setNroSolped(Integer nroSolped) {
        this.nroSolped = nroSolped;
    }

    public BigDecimal getPrecioSolped() {
        return precioSolped;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public void setPrecioSolped(BigDecimal precioSolped) {
        this.precioSolped = precioSolped;
    }

    public BigDecimal getCantidadModificada() {
        return cantidadModificada;
    }

    public void setCantidadModificada(BigDecimal cantidadModificada) {
        this.cantidadModificada = cantidadModificada;
    }

    public String getPersonaLicitacion() {
        return personaLicitacion;
    }

    public void setPersonaLicitacion(String personaLicitacion) {
        this.personaLicitacion = personaLicitacion;
    }



    public String getPosicionSolped() {
        return posicionSolped;
    }

    public void setPosicionSolped(String posicionSolped) {
        this.posicionSolped = posicionSolped;
    }

    @Override
    public String toString() {
        return "ModificacionSolped{" +
                ", nroSolped=" + nroSolped +
                ", precioSolped=" + precioSolped +
                ", cantidadModificada=" + cantidadModificada +
                ", idLicitacionDetalle=" + idLicitacionDetalle +
                ", precioUnitario=" + idLicitacionDetalle +
                ", personaLicitacion='" + personaLicitacion + '\'' +
                ", posicionSolped=" + posicionSolped +
                '}';
    }
}
