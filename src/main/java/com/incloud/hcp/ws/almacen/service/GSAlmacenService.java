package com.incloud.hcp.ws.almacen.service;

import com.incloud.hcp.domain.CentroAlmacen;
import com.incloud.hcp.ws.almacen.bean.ObtenerAlmacenResponse;

public interface GSAlmacenService {

    ObtenerAlmacenResponse obtenerAlmacen (int idEmpresa, String fechaInicio, String fechaFinal);
}
