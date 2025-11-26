package com.incloud.hcp.domain;

import com.incloud.hcp.domain._framework.BaseDomain;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name="modificacion_solped")
public class ModificacionSolped extends BaseDomain implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name="ID_MODIFICACION_SOLPED")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idModificacionSolped;

    @ManyToOne
    @JoinColumn(name="ID_LICITACION_DETALLE")
    private LicitacionDetalle licitacionDetalle;


    @Column(name="NRO_SOLPED")
    private Integer nroSolped;

    @Column(name="PRECIO_UNITARIO", precision=15, scale=2)
    private BigDecimal precioUnitario;

    @Column(name="CANTIDAD_MODIFICADA", precision=15, scale=2)
    private BigDecimal cantidadModificada;

    @Column(name="PERSONA_LICITACION")
    private String personaLicitacion;


    @Column(name="FECHA_REGISTRO")
    private Date fechaRegistro;

    @Column(name="POSICION_SOLPED")
    private String posicionSolped;

    @Column(name="ESTADO_SOLPED")
    private String estadoSolped;

    public String getEstadoSolped() {
        return estadoSolped;
    }

    public void setEstadoSolped(String estadoSolped) {
        this.estadoSolped = estadoSolped;
    }

    public Integer getIdModificacionSolped() {
        return idModificacionSolped;
    }

    public void setIdModificacionSolped(Integer idModificacionSolped) {
        this.idModificacionSolped = idModificacionSolped;
    }

    public LicitacionDetalle getLicitacionDetalle() {
        return licitacionDetalle;
    }

    public void setLicitacionDetalle(LicitacionDetalle licitacionDetalle) {
        this.licitacionDetalle = licitacionDetalle;
    }

    public Integer getNroSolped() {
        return nroSolped;
    }

    public void setNroSolped(Integer nroSolped) {
        this.nroSolped = nroSolped;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
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

    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Date fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
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
                "idModificacionSolped=" + idModificacionSolped +
                ", licitacionDetalle=" + licitacionDetalle +
                ", nroSolped=" + nroSolped +
                ", precioUnitario=" + precioUnitario +
                ", cantidadModificada=" + cantidadModificada +
                ", personaLicitacion='" + personaLicitacion + '\'' +
                ", fechaRegistro=" + fechaRegistro +
                ", posicionSolped='" + posicionSolped + '\'' +
                ", estadoSolped='" + estadoSolped + '\'' +
                '}';
    }
}
