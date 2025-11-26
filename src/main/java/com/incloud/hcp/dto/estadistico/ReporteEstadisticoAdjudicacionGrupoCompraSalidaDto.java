package com.incloud.hcp.dto.estadistico;

import com.incloud.hcp.dto.graficos.JsonGraficoSalidaDto;

import java.util.List;

public class ReporteEstadisticoAdjudicacionGrupoCompraSalidaDto {

    private List<ReporteEstadisticoAdjudicacionDto> dataResumen;
    private JsonGraficoSalidaDto dataDetalle;

    public List<ReporteEstadisticoAdjudicacionDto> getDataResumen() {
        return dataResumen;
    }

    public void setDataResumen(List<ReporteEstadisticoAdjudicacionDto> dataResumen) {
        this.dataResumen = dataResumen;
    }

    public JsonGraficoSalidaDto getDataDetalle() {
        return dataDetalle;
    }

    public void setDataDetalle(JsonGraficoSalidaDto dataDetalle) {
        this.dataDetalle = dataDetalle;
    }


    @Override
    public String toString() {
        return "ReporteEstadisticoAdjudicacionGrupoCompraSalidaDto{" +
                "dataResumen=" + dataResumen +
                ", dataDetalle=" + dataDetalle +
                '}';
    }
}
