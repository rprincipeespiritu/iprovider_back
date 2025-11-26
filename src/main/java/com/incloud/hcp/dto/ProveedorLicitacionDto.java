package com.incloud.hcp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProveedorLicitacionDto {
    private Integer idProveedor;
    private String acreedorCodigoSap;
    private String contacto;
    private String direccionFiscal;
    private String email;
    private BigDecimal evaluacionDesempeno;
    private BigDecimal evaluacionHomologacion;
    private Date fechaCreacion;
    private Date fechaModificacion;
    private String indBlackList;
    private String indBloqueadoSap;
    private String indHomologado;
    private String indProveedorComunidad;
    private String indSujetoRetencion;
    private String indDetraccion;
    private Boolean indEmiteRecibo;
    private String razonSocial;
    private String ruc;
    private String telefono;
    private String bancoExtranjero;
    private String tipoPersona;
}
