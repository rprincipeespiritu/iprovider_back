package com.incloud.hcp.ws.registrarcompra.service;

import com.incloud.hcp.domain.OrdenCompra;
import com.incloud.hcp.domain.OrdenCompraDetalle;
import com.incloud.hcp.domain.Proveedor;
import com.incloud.hcp.ws.registrarcompra.bean.CompraRegistroResponse;
import com.incloud.hcp.ws.registrarcompra.dto.RegistroCompraGSDto;

public interface GSCompraRegistroService {

    CompraRegistroResponse registro(RegistroCompraGSDto dto);

}
