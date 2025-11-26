package com.incloud.hcp.service;

import com.incloud.hcp.dto.ComprobantePagoEntradaDto;
import com.incloud.hcp.jco.comprobantePago.dto.ComprobantePagoDto;

import java.util.Date;
import java.util.List;

public interface ComprobantePagoService {

    List<ComprobantePagoDto> getComprobantePagoListPorFechasAndRuc(Date fechaInicio, Date fechaFin, String ruc, String numeroComprobantePago, String codigoSociedad) throws Exception;

    List<ComprobantePagoDto> getComprobantePagoList(ComprobantePagoEntradaDto bean) throws Exception;

    ComprobantePagoDto setComprobanteFactoring(ComprobantePagoEntradaDto bean) throws Exception;

}
