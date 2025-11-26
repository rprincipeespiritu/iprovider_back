package com.incloud.hcp.service;

import com.incloud.hcp.domain.DocumentoAceptacion;
import com.incloud.hcp.domain.Prefactura;
import com.incloud.hcp.dto.*;
import com.incloud.hcp.jco.prefactura.dto.PrefacturaRFCResponseDto;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.util.Date;
import java.util.List;


public interface PrefacturaService {

    List<Prefactura> getPrefacturaList(PrefacturaEntradaDto bean) throws Exception;
    List<Prefactura> getPrefacturaListPorFechasAndRuc(Date fechaInicio, Date fechaFin, String ruc);

    List<Prefactura> getPrefacturaListByFechasEmisionAndFechaEntradaAndRuc(Date fechaEmisionInicio, Date fechaEmisionFin,
                                                                           Date fechaEntradaInicio, Date fechaEntradaFin,
                                                                           String ruc,
                                                                           String referencia);

    List<Prefactura> getPrefacturaList(Date fechaEmisionInicio, Date fechaEmisionFin,
                                       Date fechaEntradaInicio, Date fechaEntradaFin,
                                       String ruc,
                                       String referencia,
                                       String comprador,
                                       String centro,
                                       Integer idEstado);

    PrefacturaReferenciaDTO ingresarNuevaPrefactura(PrefacturaDto prefacturaDto);

    List<DocumentoAceptacion> obtenerDocumentoAceptacionListByIdPrefactura(Integer idPrefactura);

    String descartarPrefactura(Integer idPrefactura);

    PrefacturaRFCResponseDto rechazarPrefactura(Integer idPrefactura, String textoRechazo);

    String enviarCorreoAnulacionPrefactura(Integer idPrefactura, String textoAnulacion);

    PrefacturaRFCResponseDto registrarPrefacturaEnSap(Integer idPrefactura, Date fechaContabilizacion, Date fechaBase, String indicadorImpuesto) throws Exception;

    String updatePathAdjuntoPrefactura(Integer idPrefactura, String cmisFileUrl, String type);

    String getFileEcmPath(Integer idPrefactura, String fileType);

    String getPrefacturaPdfContent(Prefactura prefactura) throws Exception;

    PrefacturaAnuladaRespuestaDto actualizarPrefacturasAnuladasPorRangoFechas(String fechaInicio, String fechaFin, boolean actualizacionManual);

    PrefacturaAnuladaRespuestaDto actualizarMasivoPrefacturasAnuladasPorRangoFechas(Date fechaInicio, Date fechaFin);

    List<PrefacturaActualizarDto> actualizarPrefacturasRegistradasEnSap (List<Integer> idPrefacturaActualizarList);

    SXSSFWorkbook descargarListaPrefacturaExcelSXLSX(Date fechaEmisionInicio, Date fechaEmisionFin,
                                                     Date fechaEntradaInicio, Date fechaEntradaFin,
                                                     String ruc,
                                                     String referencia,
                                                     String comprador,
                                                     String centro,
                                                     Integer idEstado);

    SXSSFWorkbook descargarListaPrefacturaExcelSXLSXconFiltros(PrefacturaExcelRequestDto prefacturaExcelRequestDto);
}
