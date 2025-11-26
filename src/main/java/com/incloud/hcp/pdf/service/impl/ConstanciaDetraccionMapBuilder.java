package com.incloud.hcp.pdf.service.impl;

import com.incloud.hcp.dto.ConstanciaDetraccionDetallePdfDto;



import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ConstanciaDetraccionMapBuilder {
    private ConstanciaDetraccionDetallePdfDto constanciaDetraccionDetallePdfDto;

    private static final String DASH = "-";

    private static final String razonSocial = "IT-TEAM PERU S.A.C.";
    private static  final Integer nroConstancia = 233811931;

    public static ConstanciaDetraccionMapBuilder newConstanciaDetraccionMapBuilder(ConstanciaDetraccionDetallePdfDto constanciaDetraccionDetallePdfDto){
        return  new ConstanciaDetraccionMapBuilder(constanciaDetraccionDetallePdfDto);
    }
    public ConstanciaDetraccionMapBuilder(ConstanciaDetraccionDetallePdfDto constanciaDetraccionDetallePdfDto) {
        this.constanciaDetraccionDetallePdfDto = constanciaDetraccionDetallePdfDto;
    }

    Map<String,Object> buildParams(){
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("razonSocial", Optional.ofNullable(constanciaDetraccionDetallePdfDto.getRazonSocialProv()).orElse(DASH) );
        parameters.put("nroConstancia",Optional.ofNullable(constanciaDetraccionDetallePdfDto.getNroConstancia()).orElse(0));
        parameters.put("fechaDoc", Optional.ofNullable(constanciaDetraccionDetallePdfDto.getFechaDoc()).orElse(DASH) );
        parameters.put("tipoDocProveedor", Optional.ofNullable(constanciaDetraccionDetallePdfDto.getTipoDocProveedor()).orElse(DASH) );
        parameters.put("nroDocProveedor", Optional.ofNullable(constanciaDetraccionDetallePdfDto.getNroDocProvedor()).orElse(DASH) );
        parameters.put("codigoOperacion", Optional.ofNullable(constanciaDetraccionDetallePdfDto.getCodigoOperacion()).orElse(DASH) );
        parameters.put("nombreOperacion", Optional.ofNullable(constanciaDetraccionDetallePdfDto.getNombreOperacion()).orElse(DASH) );
        parameters.put("codigoBienServ", Optional.ofNullable(constanciaDetraccionDetallePdfDto.getCodigoBienServ()).orElse(DASH) );
        parameters.put("nombreBienServ", Optional.ofNullable(constanciaDetraccionDetallePdfDto.getNombreBienServ()).orElse(DASH) );
        parameters.put("montoDeposito", Optional.ofNullable(constanciaDetraccionDetallePdfDto.getMontoDeposito()).orElse(Double.parseDouble(String.valueOf(0))));
        parameters.put("periodoTributario", Optional.ofNullable(constanciaDetraccionDetallePdfDto.getPeriodoTributario()).orElse(DASH) );
        parameters.put("tipoComprobante", Optional.ofNullable(constanciaDetraccionDetallePdfDto.getTipoComprobante()).orElse(DASH) );
        parameters.put("nroComprobante", Optional.ofNullable(constanciaDetraccionDetallePdfDto.getNroComprobante()).orElse(DASH) );
        parameters.put("logo", Optional.ofNullable("logoCopeinca/JRC-Logo.jpg").orElse(DASH) );
        return  parameters;
    }

}
