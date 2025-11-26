package com.incloud.hcp.ws.producto.service;

import com.incloud.hcp.ws.producto.bean.ObtenerProductoResponse;

public interface GSObtenerProductoService {

    ObtenerProductoResponse obtenerproducto(String fechaInicial, String fechaFinal, int idEmpresa,int itemCategoria);

}
