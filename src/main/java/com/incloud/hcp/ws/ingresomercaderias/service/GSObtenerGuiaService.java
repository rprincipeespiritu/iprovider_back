package com.incloud.hcp.ws.ingresomercaderias.service;

import com.incloud.hcp.ws.ingresomercaderias.bean.ObtenerGuiaResponse;

public interface GSObtenerGuiaService {

    ObtenerGuiaResponse obtenerGuia(int idEmpresa, String fechaInicio, String fechaFinal,String id_Agenda);


}


