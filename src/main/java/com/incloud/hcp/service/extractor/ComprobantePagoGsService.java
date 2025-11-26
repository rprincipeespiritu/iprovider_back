package com.incloud.hcp.service.extractor;

import com.incloud.hcp.ws.docspendientes.dto.ComprobantePagoGsDto;

import java.util.Date;
import java.util.List;

public interface ComprobantePagoGsService {

    public List<ComprobantePagoGsDto> getComprobantePagoList( String fechaInicio, String fechaFin, String email);

}
