package com.incloud.hcp.jco.solped.service;

import com.incloud.hcp.jco.solped.dto.SolicitudPedidoRFCResponseDto;

import java.math.BigDecimal;

public interface JCOSolicitudPedidoService {


    SolicitudPedidoRFCResponseDto getSolpedResponseByCodigo(String numeroSolicitud) throws Exception;
    String modificarSolped(Integer codigoSolicitud, String solicitudPedido,BigDecimal cantidad, BigDecimal precioOc) throws Exception;
}
