package com.incloud.hcp.dto.graficos;

import java.util.ArrayList;
import java.util.List;

public class DataDuplaGrafico {

    private String indicador;
    private Integer id;
    private List<DuplaGrafico> data = new ArrayList();

    public String getIndicador() {
        return indicador;
    }

    public void setIndicador(String indicador) {
        this.indicador = indicador;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<DuplaGrafico> getData() {
        return data;
    }

    public void setData(List<DuplaGrafico> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "DataDuplaGrafico{" +
                "indicador='" + indicador + '\'' +
                ", id=" + id +
                ", data=" + data +
                '}';
    }
}
