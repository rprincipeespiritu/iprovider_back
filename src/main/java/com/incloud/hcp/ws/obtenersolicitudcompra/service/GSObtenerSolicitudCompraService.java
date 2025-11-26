package com.incloud.hcp.ws.obtenersolicitudcompra.service;

import com.incloud.hcp.ws.obtenersolicitudcompra.bean.ObtenerCompraResponse;
import com.incloud.hcp.ws.obtenersolicitudcompra.dto.SolicitudCompraResponseDto;

public interface GSObtenerSolicitudCompraService {

    public ObtenerCompraResponse obtenerSolicitudCompra(int idEmpresa, String fechaInicio, String fechaFinal);


}
