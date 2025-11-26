package com.incloud.hcp.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class ConstanciaDetraccionDto {

    private Integer idCostanciaDetalle;
    private String razonSocial;
    private String nroDocProv;

    private String fechaDoc;
    private String tipoComprobante;
    private String nroComprobante;

    private Integer nroConstancia;

    private Double montoDeposito;
}
