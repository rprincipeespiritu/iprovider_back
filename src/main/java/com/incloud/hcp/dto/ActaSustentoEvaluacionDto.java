package com.incloud.hcp.dto;

public class ActaSustentoEvaluacionDto {

    private Integer idActaSustento;
    private CriterioEvaluacionDto criterioM;
    private CriterioEvaluacionDto criterioS;

    private ActaSustentoFormularioEvaluacionDto formularioEm;
    private ActaSustentoFormularioEvaluacionDto formularioHes;


    public Integer getIdActaSustento() {
        return idActaSustento;
    }

    public void setIdActaSustento(Integer idActaSustento) {
        this.idActaSustento = idActaSustento;
    }

    public CriterioEvaluacionDto getCriterioM() {
        return criterioM;
    }

    public void setCriterioM(CriterioEvaluacionDto criterioM) {
        this.criterioM = criterioM;
    }

    public CriterioEvaluacionDto getCriterioS() {
        return criterioS;
    }

    public void setCriterioS(CriterioEvaluacionDto criterioS) {
        this.criterioS = criterioS;
    }

    public ActaSustentoFormularioEvaluacionDto getFormularioEm() {
        return formularioEm;
    }

    public void setFormularioEm(ActaSustentoFormularioEvaluacionDto formularioEm) {
        this.formularioEm = formularioEm;
    }

    public ActaSustentoFormularioEvaluacionDto getFormularioHes() {
        return formularioHes;
    }

    public void setFormularioHes(ActaSustentoFormularioEvaluacionDto formularioHes) {
        this.formularioHes = formularioHes;
    }
}
