package com.incloud.hcp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConstanciaDetraccionDetallePdfDto {

    private Integer idConstanciaDetalle;
    private Integer nroConstancia;
    private String tipoDocProveedor;
    private String nroDocProvedor;
    private String razonSocialProv;
    private String codigoOperacion;
    private String nombreOperacion;
    private String codigoBienServ;
    private String nombreBienServ;
    private Double montoDeposito;
    private String periodoTributario;
    private String tipoComprobante;
    private String nroComprobante;
    private String fechaDoc;

}
