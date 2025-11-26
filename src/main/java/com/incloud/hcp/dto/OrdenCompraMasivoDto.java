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
public class OrdenCompraMasivoDto {
    String identificadorOc;
    String acreedorCodigoSap;
    String codigoComprador;
    String moneda;
    String codigoItem;
    BigDecimal  cantidad;
    String centro;
    BigDecimal costoUnitario;
    String nroSolped;
    String posicionSolped;
    String areaSolicitante;
    String grupoCompras;
    String claseDocCompras;
    String organizacionCompras;
    String Sociedad;
    String indicadorImpuesto;
    String condicionEntrega;
    String unidadMedida;
    String fechaEntrega;

}
