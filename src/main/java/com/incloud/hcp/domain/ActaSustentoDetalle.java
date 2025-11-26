package com.incloud.hcp.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.incloud.hcp.domain._framework.BaseDomain;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="acta_sustento_detalle")
public class ActaSustentoDetalle   extends BaseDomain implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ID_ACTA_SUSTENTO_DETALLE")
    private Integer idActaSustentoDetalle;


    @ManyToOne
    @JoinColumn(name="ACTA_SUSTENTO_ID")
    private ActaSustento actaSustento;

    @ManyToOne
    @JoinColumn(name="ORDEN_COMPRA_DETALLE_ID")
    private OrdenCompraDetalle ordenCompraDetalle;

    @Column(name="ESTADO")
    private String estado;

    private Float precioTotalDecimal;


    //=====================================================================
    //CAMPO PARA LA GENERACION DE EM/HES
    private Integer idDocumentoDetalle;

    public Integer getIdDocumentoDetalle() {
        return idDocumentoDetalle;
    }

    public void setIdDocumentoDetalle(Integer idDocumentoDetalle) {
        this.idDocumentoDetalle = idDocumentoDetalle;
    }
    //====================================================================

    public Integer getIdActaSustentoDetalle() {
        return idActaSustentoDetalle;
    }

    public void setIdActaSustentoDetalle(Integer idActaSustentoDetalle) {
        this.idActaSustentoDetalle = idActaSustentoDetalle;
    }

    public ActaSustento getActaSustento() {
        return actaSustento;
    }

    public void setActaSustento(ActaSustento actaSustento) {
        this.actaSustento = actaSustento;
    }

    public OrdenCompraDetalle getOrdenCompraDetalle() {
        return ordenCompraDetalle;
    }

    public void setOrdenCompraDetalle(OrdenCompraDetalle ordenCompraDetalle) {
        this.ordenCompraDetalle = ordenCompraDetalle;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }


    @Override
    public String toString() {
        return "ActaSustentoDetalle{" +
                "idActaSustentoDetalle='" + idActaSustentoDetalle + '\'' +
                ", actaSustento=" + actaSustento +
                ", ordenCompraDetalle=" + ordenCompraDetalle +
                ", estado='" + estado + '\'' +
                '}';
    }
}
