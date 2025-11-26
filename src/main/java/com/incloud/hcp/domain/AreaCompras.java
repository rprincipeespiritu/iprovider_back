package com.incloud.hcp.domain;

import com.incloud.hcp.domain._framework.BaseDomain;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="area_compras")
public class AreaCompras extends BaseDomain implements Serializable {
    @Id
    @Column(name="id_area_compra", unique=true, nullable=false)
    @GeneratedValue(generator = "id_area_compra_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "id_area_compra_seq", sequenceName = "id_area_compra_seq", allocationSize = 1)
    private Integer idAreaCompra;

    @Column(name="CODIGO")
    private String codigo;

    @Column(name="DESCRIPCION")
    private String descripcion;

    public Integer getIdAreaCompra() {
        return idAreaCompra;
    }

    public void setIdAreaCompra(Integer idAreaCompra) {
        idAreaCompra = idAreaCompra;
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
        return "AreaCompras{" +
                "idAreaCompra=" + idAreaCompra +
                ", codigo=" + codigo +
                ", descripcion='" + descripcion + '\'' +

                '}';
    }

}
