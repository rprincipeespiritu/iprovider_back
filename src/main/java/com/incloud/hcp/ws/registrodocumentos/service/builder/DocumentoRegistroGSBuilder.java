package com.incloud.hcp.ws.registrodocumentos.service.builder;

import com.google.gson.Gson;
import com.incloud.hcp.domain.OrdenCompra;
import com.incloud.hcp.domain.Prefactura;
import com.incloud.hcp.ws.enums.MonedaCompraEnum;
import com.incloud.hcp.ws.registrodocumentos.dto.RegistroDocumentoGSDto;
import com.incloud.hcp.ws.registrodocumentos.dto.RegistroDocumentoListaGSDto;
import com.incloud.hcp.ws.util.GSConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DocumentoRegistroGSBuilder {

    RegistroDocumentoGSDto dto;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public static DocumentoRegistroGSBuilder newBuilder(RegistroDocumentoGSDto dto) {
        return new DocumentoRegistroGSBuilder(dto);

    }

    private DocumentoRegistroGSBuilder(RegistroDocumentoGSDto dto) {
        this.dto = dto;
   }

    public String build() {
        Prefactura f = new Prefactura();

        Gson gson = new Gson();
/*
        dto.setIdEmpresa(1);
        dto.setOp_Guia();
        dto.setiD_Documento();
        dto.setSerie();
        dto.setNumero();
       // dto.setFecha(f.getFechaEmision());
        dto.getFechaAplicacion();
        dto.setFechaPago();
        dto.setiD_Moneda(f.getCodigoMondeda().equals("PEN") ? MonedaCompraEnum.SOLES.getValor() : MonedaCompraEnum.DOLARES.getValor());
        dto.setNeto();
        dto.setDcto();
        dto.setSubTotal(f.getSubTotal());
        dto.setImpuestos(f.getIgv());
        dto.setTotal(f.getTotal());
        dto.setObservaciones(f.getObservaciones());
        dto.setListaF();

*/


        return gson.toJson(dto);
    }

}


