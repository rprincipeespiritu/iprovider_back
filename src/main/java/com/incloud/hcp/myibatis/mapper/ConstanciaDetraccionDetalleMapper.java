package com.incloud.hcp.myibatis.mapper;


import com.incloud.hcp.dto.ConstanciaDetraccionDetallePdfDto;
import com.incloud.hcp.dto.ConstanciaDetraccionProveedorDto;

import java.util.List;

public interface ConstanciaDetraccionDetalleMapper {

    List<ConstanciaDetraccionProveedorDto> obtenerListaProveedores();

    ConstanciaDetraccionDetallePdfDto obtenerPdfConstancia(Integer idDetalleConstancia);
}
