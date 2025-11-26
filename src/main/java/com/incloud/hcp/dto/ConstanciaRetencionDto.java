package com.incloud.hcp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConstanciaRetencionDto {

    private Integer idConstanciaRetencion;
    private String nombreEmisor;
    private String rucEmisor;
    private String nombreProveedor;
    private String rucProveedor;
    private String nroRetencion;
    private String nroFactura;

    private BigDecimal totalFactura;

    private BigDecimal neto;
    private BigDecimal retencion;
    private String moneda;
    private String fechaDoc;


}
