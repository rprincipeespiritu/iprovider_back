package com.incloud.hcp.domain;

import com.incloud.hcp.domain._framework.BaseDomain;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name="CRITERIOS")
public class Criterio extends BaseDomain implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name="ID_CRITERIO")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idCriterio;

    @Column(name="NOMBRE", nullable=false, length=200)
    private String nombre;

    @Column(name="SUBCRITERIO")
    private String subcriterio;

    @Column(name="PESOS")
    private Integer pesos;

    @Column(name="PONDERACION")
    private String ponderacion;

    @Column(name="TIPO_CRITERIO")
    private String tipoCriterio;

    public Integer getIdCriterio() {
        return idCriterio;
    }

    public void setIdCriterio(Integer idCriterio) {
        this.idCriterio = idCriterio;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getSubcriterio() {
        return subcriterio;
    }

    public void setSubcriterio(String subcriterio) {
        this.subcriterio = subcriterio;
    }

    public Integer getPesos() {
        return pesos;
    }

    public void setPesos(Integer pesos) {
        this.pesos = pesos;
    }

    public String getPonderacion() {
        return ponderacion;
    }

    public void setPonderacion(String ponderacion) {
        this.ponderacion = ponderacion;
    }

    public String getTipoCriterio() {
        return tipoCriterio;
    }

    public void setTipoCriterio(String tipoCriterio) {
        this.tipoCriterio = tipoCriterio;
    }

    @Override
    public String toString() {
        return "Criterio{" +
                "idCriterio=" + idCriterio +
                ", nombre='" + nombre + '\'' +
                ", subcriterio='" + subcriterio + '\'' +
                ", pesos=" + pesos +
                ", ponderacion='" + ponderacion + '\'' +
                ", tipoCriterio='" + tipoCriterio + '\'' +
                '}';
    }
}
