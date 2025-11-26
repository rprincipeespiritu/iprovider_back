package com.incloud.hcp.service._framework.reportes.service;

import com.incloud.hcp.service._framework.reportes.bean.ReporteParams;
import com.incloud.hcp.service._framework.reportes.enums.TipoReporteJasperEnum;
import org.springframework.core.io.ByteArrayResource;

import java.util.List;
import java.util.Map;

public interface ReporteEjecucionService {

    ReporteParams inicializaReporte(
            String jrxmlReporte,
            TipoReporteJasperEnum tipoReporteJasperEnum,
            Map parameterMap,
            List<?> listaData) throws Exception;

    ByteArrayResource executeReporte(ReporteParams reportParams) throws Exception;
    ByteArrayResource executeReporteVirtualizado(ReporteParams reportParams) throws Exception;
}
