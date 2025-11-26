package com.incloud.hcp.ws.obtenersolicitudcompra.service.builder;

import com.google.gson.Gson;

import com.incloud.hcp.ws.obtenersolicitudcompra.dto.ObtenerCompraGSDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ObtenerCompraGSBuilder {
    private int idEmpresa;
    private String fechaInicio;
    private String fechaFinal;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public static ObtenerCompraGSBuilder newBuilder(int idEmpresa, String fechaInicio, String fechaFinal) {
        return new ObtenerCompraGSBuilder(idEmpresa, fechaInicio, fechaFinal);
    }

    private ObtenerCompraGSBuilder(int idEmpresa, String fechaInicio, String fechaFinal) {
        this.idEmpresa = idEmpresa;
        this.fechaInicio = fechaInicio;
        this.fechaFinal = fechaFinal;
    }

    public String build() {
        ObtenerCompraGSDto dto = new ObtenerCompraGSDto();
        Gson gson = new Gson();

        dto.setIdEmpresa(1);
        dto.setFechaInicial(fechaInicio);
        dto.setFechaFinal(fechaFinal);

        return gson.toJson(dto);
    }

}
