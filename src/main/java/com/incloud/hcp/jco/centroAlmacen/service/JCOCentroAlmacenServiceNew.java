package com.incloud.hcp.jco.centroAlmacen.service;

import com.incloud.hcp.jco.centroAlmacen.dto.CentroAlmacenRFCResponseDto;

public interface JCOCentroAlmacenServiceNew {

    CentroAlmacenRFCResponseDto getListaCentroAlmacen(String centro) throws Exception;
    CentroAlmacenRFCResponseDto getListaCentroAlmacen_old(String centro) throws Exception;

}
