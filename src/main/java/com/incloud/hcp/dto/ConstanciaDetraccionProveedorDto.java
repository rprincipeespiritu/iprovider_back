package com.incloud.hcp.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ConstanciaDetraccionProveedorDto {

    private Integer idCostanciaDetalle;
    private Integer nroConstancia;
    private String emailProveedor;
    private String razonSocialProveedor;
}
