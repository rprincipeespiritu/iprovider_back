package com.incloud.hcp.domain;

import com.incloud.hcp.domain._framework.BaseDomain;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;


/**
 * The persistent class for the centro_almacen database table.
 *
 */
@Entity
@Table(name="centro_almacen")
public class CentroAlmacen extends BaseDomain implements Serializable {
    private static final long serialVersionUID = 1L;

    /*@Id
    @Column(name="id_centro_almacen", unique=true, nullable=false)
    @GeneratedValue(generator = "centro_almacen_id_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "centro_almacen_id_seq", sequenceName = "centro_almacen_id_seq", allocationSize = 1)*/

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "centro_almacen_seq")
    @SequenceGenerator(name = "centro_almacen_seq", sequenceName = "centro_almacen_id_seq", allocationSize = 1)
    @Column(name = "id_centro_almacen")
    private Integer idCentroAlmacen;

    @Column(name="idErp")
    private Integer idErp;


    @Column(name="codigo_sap", nullable=false, length=4)
    private String codigoSap;

    @Column(nullable=false, length=100)
    private String denominacion;

    @Column(name="id_padre")
    private Integer idPadre;

    @Column(nullable=false)
    private Integer nivel;

    @Size(max = 200)
    @Column(name = "poblacion", length = 200)
    private String poblacion;

    @Size(max = 200)
    @Column(name = "distrito", length = 200)
    private String distrito;

    @Size(max = 200)
    @Column(name = "direccion", length = 200)
    private String direccion;

    @Column(name = "pais", length = 200)
    private String pais;

    @Column(name = "departamento", length = 200)
    private String departamento;

    @Column(name = "descripcion", length = 200)
    private String descripcion;

    @Transient
    private String codigoSapPadre;

    public CentroAlmacen() {
    }

    public Integer getIdCentroAlmacen() {
        return this.idCentroAlmacen;
    }

    public void setIdCentroAlmacen(Integer idCentroAlmacen) {
        this.idCentroAlmacen = idCentroAlmacen;
    }

    public String getCodigoSap() {
        return this.codigoSap;
    }

    public void setCodigoSap(String codigoSap) {
        this.codigoSap = codigoSap;
    }

    public String getDenominacion() {
        return this.denominacion;
    }

    public void setDenominacion(String denominacion) {
        this.denominacion = denominacion;
    }

    public Integer getIdErp() {
        return idErp;
    }

    public void setIdErp(Integer idErp) {
        this.idErp = idErp;
    }

    public Integer getIdPadre() {
        return this.idPadre;
    }

    public void setIdPadre(Integer idPadre) {
        this.idPadre = idPadre;
    }

    public Integer getNivel() {
        return this.nivel;
    }

    public void setNivel(Integer nivel) {
        this.nivel = nivel;
    }

    // -- [direccion] ------------------------

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public CentroAlmacen direccion(String direccion) {
        setDireccion(direccion);
        return this;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getPoblacion() {
        return poblacion;
    }

    public void setPoblacion(String poblacion) {
        this.poblacion = poblacion;
    }

    public String getDistrito() {
        return distrito;
    }

    public void setDistrito(String distrito) {
        this.distrito = distrito;
    }

    public String getCodigoSapPadre() {
        return codigoSapPadre;
    }

    public void setCodigoSapPadre(String codigoSapPadre) {
        this.codigoSapPadre = codigoSapPadre;
    }

    @Override
    public String toString() {
        return "CentroAlmacen{" +
                "idCentroAlmacen=" + idCentroAlmacen +
                ", codigoSap='" + codigoSap + '\'' +
                ", denominacion='" + denominacion + '\'' +
                ", idPadre=" + idPadre +
                ", nivel=" + nivel +
                ", poblacion='" + poblacion + '\'' +
                ", distrito='" + distrito + '\'' +
                ", direccion='" + direccion + '\'' +
                ", codigoSapPadre='" + codigoSapPadre + '\'' +
                '}';
    }
}