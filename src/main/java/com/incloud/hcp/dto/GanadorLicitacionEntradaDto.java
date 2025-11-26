package com.incloud.hcp.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GanadorLicitacionEntradaDto implements Serializable {

    private Integer idLicitacion;
    private List<GanadorLicitacionEvaluacionEntradaDto> listaEvaluacionTecnica;

    public Integer getIdLicitacion() {
        return idLicitacion;
    }

    public void setIdLicitacion(Integer idLicitacion) {
        this.idLicitacion = idLicitacion;
    }

    public List<GanadorLicitacionEvaluacionEntradaDto> getListaEvaluacionTecnica() {
        return listaEvaluacionTecnica;
    }

    public void setListaEvaluacionTecnica(List<GanadorLicitacionEvaluacionEntradaDto> listaEvaluacionTecnica) {
        this.listaEvaluacionTecnica = listaEvaluacionTecnica;
    }

    @Override
    public String toString() {
        return "GanadorLicitacionEntradaDto{" +
                "idLicitacion=" + idLicitacion +
                ", listaEvaluacionTecnica=" + listaEvaluacionTecnica +
                '}';
    }


}
