package com.incloud.hcp.service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.incloud.hcp.domain.LogTransaccion;
import com.incloud.hcp.domain.OrdenCompra;
import com.incloud.hcp.dto.*;
import com.incloud.hcp.jco.ordenCompra.dto.OrdenCompraRequestRPA;
import com.incloud.hcp.jco.ordenCompra.dto.OrdenCompraRequestRespuestaRPA;
import com.incloud.hcp.jco.ordenCompra.dto.OrdenCompraResponseRPA;
import com.incloud.hcp.jco.ordenCompra.dto.ordenCompraTramaJSON.ZPE_MM_GENERAR_OC;
import org.springframework.web.multipart.MultipartFile;

public interface OrdenCompraService {

    List<OrdenCompra> getAllOrdenCompra();

    OrdenCompra getOrdenCompraById(Integer idOrdenCompra);

    List<OrdenCompra> getOrdenCompraListPorFechasAndRuc( String email, FiltroOrdenCompraDto filtroOrdenCompraDto);

    OrdenCompraRespuestaDto updateOrdenCompraFechaVisualizacion(Integer idOrdenCompra);

    OrdenCompraRespuestaDto aprobarRechazarOrdenCompra(Integer idOrdenCompra, int estado, String textoRechazo);

    void extraerOrdenCompraMasivoByRangoFechas(Date fechaInicio, Date fechaFin, boolean enviarCorreoPublicacion);

    void extraerContratoMarcoMasivoByRangoFechas(Date fechaInicio, Date fechaFin, boolean enviarCorreoPublicacion);

    String getOrdenCompraPdfContent(String numeroOrdenCompra) throws Exception;

    String getContratoMarcoPdfContent(String numeroContratoMarco) throws Exception;

    List<ProveedoresFinalDto> crearOrdenCompraIprovider(OrdenCompraGenerarDto bean) throws Exception;

    List<ZPE_MM_GENERAR_OC> getOrdenCompraTramaJSON() throws Exception;

    OrdenCompraRequestRPA getOrdenComprasRPA() throws Exception;

    OrdenCompraResponseRPA setOrdenComprasRPA(OrdenCompraRequestRespuestaRPA ordenCompraRequestRespuestaRPA) throws Exception;

    OrdenCompra getNroOrdenCompraById(String nroOrdenCompra) ;

    CompletableFuture<List<OrdenCompraMasivoDto>> readExcel(MultipartFile file, String email);

    List<LogTransaccion> getLogsTrasaccion(String identificador);

}
