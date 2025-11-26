package com.incloud.hcp.domain;


import com.incloud.hcp.domain._framework.BaseDomain;
import org.apache.poi.hpsf.Decimal;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name="acta_sustento_evaluacion_proveedor")
public class ActaSustentoEvaluacionProveedor extends BaseDomain implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ID_ACTA_EVALUACION_PROVEEDOR")
    private Integer idActaEvaluacionProveedor;


    @ManyToOne
    @JoinColumn(name="ID_ACTA_SUSTENTO")
    private ActaSustento actaSustento;

    @Column(name="TOTAL_INDIVIDUAL")
    private BigDecimal totalIndividual;

    @Column(name="COMENTARIO")
    private String comentario;

    @ManyToOne
    @JoinColumn(name="ID_CRITERIO")
    private Criterio criterio;

    public Integer getIdActaEvaluacionProveedor() {
        return idActaEvaluacionProveedor;
    }

    public void setIdActaEvaluacionProveedor(Integer idActaEvaluacionProveedor) {
        this.idActaEvaluacionProveedor = idActaEvaluacionProveedor;
    }

    public ActaSustento getActaSustento() {
        return actaSustento;
    }

    public void setActaSustento(ActaSustento actaSustento) {
        this.actaSustento = actaSustento;
    }

    public BigDecimal getTotalIndividual() {
        return totalIndividual;
    }

    public void setTotalIndividual(BigDecimal totalIndividual) {
        this.totalIndividual = totalIndividual;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public Criterio getCriterio() {
        return criterio;
    }

    public void setCriterio(Criterio criterio) {
        this.criterio = criterio;
    }

    @Override
    public String toString() {
        return "ActaSustentoEvaluacionProveedor{" +
                "idActaEvaluacionProveedor=" + idActaEvaluacionProveedor +
                ", actaSustento=" + actaSustento +
                ", totalIndividual='" + totalIndividual + '\'' +
                ", comentario='" + comentario + '\'' +
                ", criterio=" + criterio +
                '}';
    }
}
