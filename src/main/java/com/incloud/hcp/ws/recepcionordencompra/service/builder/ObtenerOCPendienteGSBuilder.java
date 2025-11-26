package com.incloud.hcp.ws.recepcionordencompra.service.builder;

import com.google.gson.Gson;
import com.incloud.hcp.ws.recepcionordencompra.dto.ObtenerOCPendienteGSDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ObtenerOCPendienteGSBuilder {
    private int idEmpresa;
    private String fechaInicio;
    private String fechaFinal;
    private String iD_Agenda;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public static ObtenerOCPendienteGSBuilder newBuilder(int idEmpresa, String fechaInicio, String fechaFinal, String iD_Agenda) {
        return new ObtenerOCPendienteGSBuilder(idEmpresa, fechaInicio, fechaFinal, iD_Agenda);
    }

    private ObtenerOCPendienteGSBuilder(int idEmpresa, String fechaInicio, String fechaFinal, String iD_Agenda) {
        this.idEmpresa = idEmpresa;
        this.fechaInicio = fechaInicio;
        this.fechaFinal = fechaFinal;
        this.iD_Agenda= iD_Agenda;
    }

    public String build() {
        ObtenerOCPendienteGSDto dto = new ObtenerOCPendienteGSDto();
        Gson gson = new Gson();

        dto.setIdEmpresa(1);
        dto.setFechaInicial(fechaInicio);
        dto.setFechaFinal(fechaFinal);
        dto.setiD_Agenda(iD_Agenda);

        return gson.toJson(dto);
    }

}
