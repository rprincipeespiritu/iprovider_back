package com.incloud.hcp.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.incloud.hcp.domain._framework.BaseDomain;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="adjunto_acta_sustento")
public class AdjuntoActaSustento extends BaseDomain implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ID_ADJUNTO_ACTA_SUSTENTO")
    private Integer idAdjuntoActaSustento;

    @Column(name="ARCHIVO")
    private String archivo;

    @Column(name="ARCHIVO_NOMBRE")
    private String archivoNombre;

    @Column(name="ARCHIVO_TIPO")
    private String archivoTipo;

    @ManyToOne
    @JoinColumn(name="ACTA_SUSTENTO_ID")
    private ActaSustento actaSustento;

    @ManyToOne
    @JoinColumn(name="ID_TIPO_DOCUMENTO")
    private MtrTipoDocumento tipoDocumento;


    public Integer getIdAdjuntoActaSustento() {
        return idAdjuntoActaSustento;
    }

    public void setIdAdjuntoActaSustento(Integer idAdjuntoActaSustento) {
        this.idAdjuntoActaSustento = idAdjuntoActaSustento;
    }

    public String getArchivo() {
        return archivo;
    }

    public void setArchivo(String archivo) {
        this.archivo = archivo;
    }

    public String getArchivoNombre() {
        return archivoNombre;
    }

    public void setArchivoNombre(String archivoNombre) {
        this.archivoNombre = archivoNombre;
    }

    public String getArchivoTipo() {
        return archivoTipo;
    }

    public void setArchivoTipo(String archivoTipo) {
        this.archivoTipo = archivoTipo;
    }

    public ActaSustento getActaSustento() {
        return actaSustento;
    }

    public void setActaSustento(ActaSustento actaSustento) {
        this.actaSustento = actaSustento;
    }

    public MtrTipoDocumento getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(MtrTipoDocumento tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    @Override
    public String toString() {
        return "AdjuntoActaSustento{" +
                "idAdjuntoActaSustento='" + idAdjuntoActaSustento + '\'' +
                ", archivo='" + archivo + '\'' +
                ", archivoNombre='" + archivoNombre + '\'' +
                ", archivoTipo='" + archivoTipo + '\'' +
                ", actaSustento=" + actaSustento +
                ", tipoDocumento=" + tipoDocumento +
                '}';
    }
}
