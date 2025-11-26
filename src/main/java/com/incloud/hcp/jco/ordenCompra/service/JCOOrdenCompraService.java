package com.incloud.hcp.jco.ordenCompra.service;

import com.incloud.hcp.domain.*;
import com.incloud.hcp.jco.ordenCompra.dto.OrdenCompraResponseDto;

import java.util.List;

public interface JCOOrdenCompraService {

    OrdenCompraResponseDto grabarOrdenCompra(
            String claseDocumento,
            Proveedor proveedor,
            Usuario usuario,
            List<CcomparativoAdjudicado> ccomparativoAdjudicadoList) throws Exception;

    OrdenCompraResponseDto grabarOrdenCompraSAP(
            Proveedor proveedor,
            OrdenCompra ordenCompra,  List<OrdenCompraDetalle> ordenCompraDetalles,Integer idLicitacion) throws Exception;

}
