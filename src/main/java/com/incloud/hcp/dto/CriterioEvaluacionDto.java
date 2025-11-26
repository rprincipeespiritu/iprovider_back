package com.incloud.hcp.dto;

import com.incloud.hcp.domain.Criterio;

import java.util.List;

public class CriterioEvaluacionDto {

    private String comentario;
    private List<CriterioItemsEvaluacionDto> criterio;

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public List<CriterioItemsEvaluacionDto> getCriterio() {
        return criterio;
    }

    public void setCriterio(List<CriterioItemsEvaluacionDto> criterio) {
        this.criterio = criterio;
    }
}
