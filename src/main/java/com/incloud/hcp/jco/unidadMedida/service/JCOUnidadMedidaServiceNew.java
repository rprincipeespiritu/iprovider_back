package com.incloud.hcp.jco.unidadMedida.service;


import com.incloud.hcp.jco.unidadMedida.dto.UnidadMedidaRFCResponseDto;

public interface JCOUnidadMedidaServiceNew {

    UnidadMedidaRFCResponseDto actualizarUnidadMedida_old() throws Exception;
    UnidadMedidaRFCResponseDto actualizarUnidadMedida() throws Exception;

}
