package com.incloud.hcp.jco.proveedor.service;

import com.incloud.hcp.domain.Proveedor;
import com.incloud.hcp.jco.proveedor.dto.ProveedorHomologacionRFCResponseDto;
import com.incloud.hcp.jco.proveedor.dto.ProveedorRFCResponseDto;
import com.incloud.hcp.jco.proveedor.dto.ProveedorResponseRFC;

import java.util.List;

public interface JCOProveedorService {

    ProveedorRFCResponseDto grabarProveedor_old(Integer idProveedor,String usuarioSap) throws Exception;
    ProveedorRFCResponseDto grabarProveedor(Integer idProveedor,String usuarioSap) throws Exception;
    ProveedorRFCResponseDto actualizarProveedor_old(Integer idProveedor,String usuarioSap) throws Exception;
    ProveedorRFCResponseDto actualizarProveedor(Integer idProveedor,String usuarioSap) throws Exception;
    ProveedorRFCResponseDto actualizarHomologacion(String codigoAcrededor, String fechaIni, String fechaFin) throws Exception;

    void notificacionHomologacion() throws Exception;

    ProveedorHomologacionRFCResponseDto obtenerFechaHomologacion(String usuarioSap) throws Exception;

    //ProveedorRFCResponseDto actualizarHomologacion(String codigoAcrededor) throws Exception;

    ProveedorResponseRFC grabarListaProveedorSAP(List<Proveedor> listaProveedorPotencial,String usuarioSap) throws Exception;

    ProveedorRFCResponseDto grabarUnicoProveedorSAP(Proveedor proveedorPotencial,String usuarioSap) throws Exception;
    String homologarProveedor(Integer idProveedor,String usuarioSap) throws Exception;
}


