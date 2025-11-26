package com.incloud.hcp.ws.registrodocumentos.service;

import com.incloud.hcp.domain.OrdenCompra;
import com.incloud.hcp.domain.Prefactura;
import com.incloud.hcp.ws.registrodocumentos.bean.DocumentoRegistroResponse;
import com.incloud.hcp.ws.registrodocumentos.dto.RegistroDocumentoGSDto;

public interface GSDocumentoRegistroService {

    DocumentoRegistroResponse registro(RegistroDocumentoGSDto dto);


}
