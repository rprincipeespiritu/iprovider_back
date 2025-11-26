package com.incloud.hcp.domain;


import com.incloud.hcp.domain._framework.BaseDomain;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="estado_civil")
public class EstadoCivil extends BaseDomain implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name="ID_ESTADO_CIVIL")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idEstadoCivil;


    @Column(name="CODIGO")
    private String codigo;

    @Column(name="DESCRIPCION")
    private String descripcion;

    public Integer getIdEstadoCivil() {
        return idEstadoCivil;
    }

    public void setIdEstadoCivil(Integer idEstadoCivil) {
        this.idEstadoCivil = idEstadoCivil;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return "EstadoCivil{" +
                "idEstadoCivil=" + idEstadoCivil +
                ", codigo='" + codigo + '\'' +
                ", descripcion='" + descripcion + '\'' +
                '}';
    }
}
