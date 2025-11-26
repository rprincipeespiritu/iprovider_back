package com.incloud.hcp.jco.ordenCompra.service;


import com.incloud.hcp.dto.ConsultaOrdenCompra;

public interface JCOOrdenCompraPublicacionService {

    ConsultaOrdenCompra extraerOrdenCompraListRFC_old(String fechaInicio, String fechaFin, boolean enviarCorreoPublicacion) throws Exception;
    
    ConsultaOrdenCompra extraerOrdenCompraListRFC(String fechaInicio, String fechaFin, boolean enviarCorreoPublicacion) throws Exception;

    ConsultaOrdenCompra extraerOrdenCompraListRFCporOC( boolean enviarCorreoPublicacion, String numeroOc) throws Exception;

    boolean toggleOrdenCompraExtractionProcessingState();

    boolean currentOrdenCompraExtractionProcessingState();

}
