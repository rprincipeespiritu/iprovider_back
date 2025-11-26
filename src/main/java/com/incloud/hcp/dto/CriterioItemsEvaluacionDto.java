package com.incloud.hcp.dto;

import java.math.BigDecimal;

public class CriterioItemsEvaluacionDto {

    private String comentario;
    private Integer idActaEvaluacionProveedor;
    private Integer idCriterio;
    private String nombre;
    private String pesos;
    private String ponderacion;
    private String subcriterio;
    private String tipoCriterio;
    private BigDecimal total;

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public Integer getIdActaEvaluacionProveedor() {
        return idActaEvaluacionProveedor;
    }

    public void setIdActaEvaluacionProveedor(Integer idActaEvaluacionProveedor) {
        this.idActaEvaluacionProveedor = idActaEvaluacionProveedor;
    }

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

    public String getPesos() {
        return pesos;
    }

    public void setPesos(String pesos) {
        this.pesos = pesos;
    }

    public String getPonderacion() {
        return ponderacion;
    }

    public void setPonderacion(String ponderacion) {
        this.ponderacion = ponderacion;
    }

    public String getSubcriterio() {
        return subcriterio;
    }

    public void setSubcriterio(String subcriterio) {
        this.subcriterio = subcriterio;
    }

    public String getTipoCriterio() {
        return tipoCriterio;
    }

    public void setTipoCriterio(String tipoCriterio) {
        this.tipoCriterio = tipoCriterio;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}
