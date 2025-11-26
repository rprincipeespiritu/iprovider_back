package com.incloud.hcp.ws.docspendientes.service;

import com.incloud.hcp.ws.docspendientes.bean.ObtenerDocsPendientesResponse;

public interface GSObtenerDocsPendienteService {

    public ObtenerDocsPendientesResponse obtenerDocsPendientes(int idEmpresa, String iD_Agenda, String fechaInicio, String fechaFin);
}
