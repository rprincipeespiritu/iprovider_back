package com.incloud.hcp.jco.ordenCompra.service;


import com.incloud.hcp.domain.OrdenCompra;
import com.incloud.hcp.domain.OrdenCompraDetalle;
import com.incloud.hcp.jco.ordenCompra.dto.OrdenCompraPdfDto;

import java.util.List;

public interface JCOOrdenCompraPdfService {

    OrdenCompraPdfDto extraerOrdenCompraPdfDtoRFC(String numeroOrdenCompra) throws Exception;

    String obtenerBase64OC(String numeroOrden) throws Exception;
    
    String obtenerBase64OcRFC(String numeroOrden) throws Exception;

    String obtenerBase64OCAdjuntoEmail(OrdenCompra ordenCompra, List<OrdenCompraDetalle> ordenCompraDetalles) throws Exception;

}
