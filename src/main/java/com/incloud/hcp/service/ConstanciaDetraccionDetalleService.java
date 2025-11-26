package com.incloud.hcp.service;

import com.incloud.hcp.dto.ConstanciaDetraccionProveedorDto;

import java.util.List;

public interface ConstanciaDetraccionDetalleService {

    public List<ConstanciaDetraccionProveedorDto> obtenerProveedores();

    String enviarCorreoConstanciaDetraccion();

    String generarPdfConstanciaDetraccion(Integer idDetalleDetraccion);
}
