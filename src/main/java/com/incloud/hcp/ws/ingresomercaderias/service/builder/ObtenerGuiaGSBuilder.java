package com.incloud.hcp.ws.ingresomercaderias.service.builder;

import com.google.gson.Gson;
import com.incloud.hcp.ws.ingresomercaderias.dto.ObtenerGuiaGSDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ObtenerGuiaGSBuilder {
    private int idEmpresa;
    private String fechaInicio;
    private String fechaFinal;
    private String id_Agenda;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public static ObtenerGuiaGSBuilder newBuilder(int idEmpresa, String fechaInicio, String fechaFinal, String id_Agenda) {
        return new ObtenerGuiaGSBuilder(idEmpresa, fechaInicio, fechaFinal, id_Agenda);
    }

    private ObtenerGuiaGSBuilder(int idEmpresa, String fechaInicio, String fechaFinal, String id_Agenda) {
        this.idEmpresa = idEmpresa;
        this.fechaInicio = fechaInicio;
        this.fechaFinal = fechaFinal;
        this.id_Agenda= id_Agenda;
    }

    public String build() {
        ObtenerGuiaGSDto dto = new ObtenerGuiaGSDto();
        Gson gson = new Gson();

        dto.setIdEmpresa(1);
        dto.setFechaInicial(fechaInicio);
        dto.setFechaFinal(fechaFinal);
        dto.setId_Agenda(id_Agenda);

        return gson.toJson(dto);
    }

}


