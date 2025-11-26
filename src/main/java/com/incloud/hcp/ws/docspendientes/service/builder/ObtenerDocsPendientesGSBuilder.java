package com.incloud.hcp.ws.docspendientes.service.builder;

import com.google.gson.Gson;
import com.incloud.hcp.ws.docspendientes.dto.ObtenerDocsPendientesGSDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ObtenerDocsPendientesGSBuilder {
    private int idEmpresa;
    private String iD_Agenda;
    private String fechaInicio;
    private String fechaFin;


    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public static ObtenerDocsPendientesGSBuilder newBuilder(int idEmpresa,  String iD_Agenda,String fechaInicio, String fechaFin) {
        return new ObtenerDocsPendientesGSBuilder(idEmpresa, iD_Agenda, fechaInicio, fechaFin );
    }

    private ObtenerDocsPendientesGSBuilder(int idEmpresa, String iD_Agenda, String fechaInicio, String fechaFinal) {
        this.idEmpresa = idEmpresa;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFinal;
        this.iD_Agenda= iD_Agenda;
    }

    public String build() {
        ObtenerDocsPendientesGSDto dto = new ObtenerDocsPendientesGSDto();
        Gson gson = new Gson();

        dto.setIdEmpresa(1);
        dto.setiD_Agenda(iD_Agenda);
        dto.setFechaInicial(fechaInicio);
        dto.setFechaFinal(fechaFin);


        return gson.toJson(dto);
    }

}
