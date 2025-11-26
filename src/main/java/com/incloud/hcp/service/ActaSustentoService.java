package com.incloud.hcp.service;

import com.incloud.hcp.domain.ActaSustento;
import com.incloud.hcp.dto.*;
import com.incloud.hcp.jco.ordenCompra.dto.OrdenCompraRequestRPA;
import com.incloud.hcp.jco.ordenCompra.dto.OrdenCompraRequestRespuestaRPA;
import com.incloud.hcp.jco.ordenCompra.dto.OrdenCompraResponseRPA;

import java.util.List;
import java.util.Map;

public interface ActaSustentoService {
    Object generarActa(GenerarActaDto bean) throws  Exception;

    List<ActaSustento> getListaActaByFiltroPaginado(Map<String, Object> json) throws  Exception;

    Object evaluarActa(Integer idActaSustento, String estado, ActaSustentoRechazarDto bean) throws  Exception;

    List<ActaSustento> getListaActaByFiltroPaginadoProveedor(String email ,Map<String, Object> json);

    List<ActaSustento> getListaActaByFiltroPaginadoProveedorRechazada(Map<String, Object> json);

    ActaSustento evaluarActaSustentoProveedor(ActaSustentoEvaluacionDto bean) throws  Exception;

    ActaSustento anularActaSustentoProveedor(Integer idActaSustento) throws  Exception;

    EntradaMercanciaRequestRPA getEntradaMercanciaRPA() throws Exception;

    EntradaMercanciaResponseRPA setEntradaMercanciaRPA(EntradaMercanciaRequestRespuestaRPA entradaMercanciaRequestRespuestaRPA) throws Exception;

    HESRequestRPA getHESRPA() throws Exception;

    HESResponseRPA setHESRPA(HESRequestRespuestaRPA hesRequestRespuestaRPA) throws Exception;
}
