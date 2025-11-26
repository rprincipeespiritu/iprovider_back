package com.incloud.hcp.service;

import com.incloud.hcp.dto.CanalContactoInDto;
import com.incloud.hcp.dto.CanalContactoOutDto;
import com.incloud.hcp.dto.ProductoDto;
import com.incloud.hcp.dto.ProductoOutDto;

public interface ProductoService {

    public ProductoOutDto crearProducto(ProductoDto bean);
}
