package com.incloud.hcp.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.incloud.hcp.domain._framework.BaseDomain;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name="acta_sustento")
public class ActaSustento extends BaseDomain implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name="ID_ACTA_SUSTENTO")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idActaSustento;

    @ManyToOne
    @JoinColumn(name="ORDEN_COMPRA_ID")
    private OrdenCompra ordenCompra;


    @Column(name="NUMERO_ACTA_SUSTENTO", nullable=false, length=10)
    private String numeroActaSustento;

    @ManyToOne
    @JoinColumn(name="PROVEEDOR_ID")
    private Proveedor proveedor;

    @Column(name="FECHA_REGISTRO")
    private Date fechaRegistro;


    @Column(name="ESTADO")
    private Integer estado;



    @Column(name="ANIO_ACTA_SUSTENTO")
    private String anioActaSustento;

    @Column(name="MOTIVO")
    private String motivo;

    @Column(name="CODIGO_SAP_EM")
    private String codigoSapEm;

    @Column(name="CODIGO_SAP_HES")
    private String codigoSapHes;

    @Column(name="USUARIO_ID")
    private Integer usuarioId;


    public Integer getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Integer usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getAnioActaSustento() {
        return anioActaSustento;
    }

    public void setAnioActaSustento(String anioActaSustento) {
        this.anioActaSustento = anioActaSustento;
    }

    public Integer getIdActaSustento() {
        return idActaSustento;
    }

    public void setIdActaSustento(Integer idActaSustento) {
        this.idActaSustento = idActaSustento;
    }

    public OrdenCompra getOrdenCompra() {
        return ordenCompra;
    }

    public void setOrdenCompra(OrdenCompra ordenCompra) {
        this.ordenCompra = ordenCompra;
    }

    public String getNumeroActaSustento() {
        return numeroActaSustento;
    }

    public void setNumeroActaSustento(String numeroActaSustento) {
        this.numeroActaSustento = numeroActaSustento;
    }

    public Proveedor getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Date fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    public String getCodigoSapEm() {
        return codigoSapEm;
    }

    public void setCodigoSapEm(String codigoSapEm) {
        this.codigoSapEm = codigoSapEm;
    }

    public String getCodigoSapHes() {
        return codigoSapHes;
    }

    public void setCodigoSapHes(String codigoSapHes) {
        this.codigoSapHes = codigoSapHes;
    }


    @Override
    public String toString() {
        return "ActaSustento{" +
                "idActaSustento=" + idActaSustento +
                ", ordenCompra=" + ordenCompra +
                ", numeroActaSustento='" + numeroActaSustento + '\'' +
                ", proveedor=" + proveedor +
                ", fechaRegistro=" + fechaRegistro +
                ", estado=" + estado +
                ", anioActaSustento='" + anioActaSustento + '\'' +
                ", motivo='" + motivo + '\'' +
                ", codigoSapEm='" + codigoSapEm + '\'' +
                ", codigoSapHes='" + codigoSapHes + '\'' +
                '}';
    }
}
