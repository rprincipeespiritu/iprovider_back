package com.incloud.hcp.service;

import com.incloud.hcp.dto.estadistico.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

public interface ReporteEstadisticoService {

    ReporteEstadisticoAdjudicacionSalidaDto reporteAdjudicacion(String ruc) throws Exception;

    ReporteEstadisticoParticipacionSalidaDto reporteParticipacion(String ruc) throws Exception;

    ReporteStatusPeticionOfertaSalidaDto reporteStatusPeticionOferta(
            ReporteStatusPeticionOfertaEntradaDto bean) throws Exception;

    ReporteStatusPeticionOfertaSalidaDto reporteStatusPeticionOfertaTodosPaginado(
            ReporteStatusPeticionOfertaEntradaPaginadoDto bean) throws Exception;

    ReporteStatusPeticionOfertaSalidaDto reporteStatusPeticionOfertaAdjuPaginado(
            ReporteStatusPeticionOfertaEntradaPaginadoDto bean) throws Exception;

    ReporteStatusPeticionOfertaSalidaDto reporteStatusPeticionOfertaEnProcPaginado(
            ReporteStatusPeticionOfertaEntradaPaginadoDto bean) throws Exception;
    // inicio mizalo
    ReporteEstadisticoAdjudicacionSalidaDto reporteAdjudicacionAcreedor(String acreedor) throws Exception;
    ReporteEstadisticoAdjudicacionGrupoCompraSalidaDto reporteAdjudicacionGrupoCompra(String grupoCompra) throws Exception;
    SXSSFWorkbook reporteAdjudicacionExcelGrupoCompra(String ruc, String acreedor) throws Exception;
    // fin mizalo
}
