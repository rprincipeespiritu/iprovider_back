package com.incloud.hcp.ws.recepcionordencompra.service;

import com.incloud.hcp.ws.recepcionordencompra.bean.ObtenerOCPendienteResponse;

public interface GSObtenerOCPendienteService {

    ObtenerOCPendienteResponse obtenerOCPendiente(int idEmpresa, String fechaInicio, String fechaFinal, String iD_Agenda);
}
