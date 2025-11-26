package com.incloud.hcp.dto.graficos;

import java.util.List;

public class JsonGraficoDuplaSalidaDto {

    private List<DataDuplaGrafico> data;
    private VizProperties vizProperties;

    public List<DataDuplaGrafico> getData() {
        return data;
    }

    public void setData(List<DataDuplaGrafico> data) {
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
        return "JsonGraficoDuplaSalidaDto{" +
                "data=" + data +
                ", vizProperties=" + vizProperties +
                '}';
    }
}


