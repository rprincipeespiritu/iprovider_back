package com.incloud.hcp.service.extractor;

import com.incloud.hcp.ws.obtenersolicitudcompra.dto.SolicitudCompraResponseDto;

public interface SolicitudPedidoGs {

    public SolicitudCompraResponseDto solicitudCompraByCodigo(Integer Codigo, boolean obtainFullPositionList);
}
