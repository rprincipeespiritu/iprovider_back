package com.incloud.hcp.dto.graficos;

import java.util.List;

public class JsonGraficoSalidaDto {

    private List<DataGrafico> data;
    private VizProperties vizProperties;

    public List<DataGrafico> getData() {
        return data;
    }

    public void setData(List<DataGrafico> data) {
        this.data = data;
    }

    public VizProperties getVizProperties() {
        return vizProperties;
    }

    public void setVizProperties(VizProperties vizProperties) {
        this.vizProperties = vizProperties;
    }

    @Override
    public String toString() {
        return "JsonGraficoSalidaDto{" +
                "data=" + data +
                ", vizProperties=" + vizProperties +
                '}';
    }
}


