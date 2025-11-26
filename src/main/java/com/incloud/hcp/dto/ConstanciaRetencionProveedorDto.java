package com.incloud.hcp.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ConstanciaRetencionProveedorDto {

    private Integer idConstanciaRetencion;
    private String nroRetencion;
    private String emailProveedor;
    private String razonSocialProveedor;

    @Override
    public String toString() {
        return "ConstanciaRetencionProveedorDto{" +
                "idConstanciaRetencion=" + idConstanciaRetencion +
                ", nroRetencion=" + nroRetencion +
                ", emailProveedor='" + emailProveedor + '\'' +
                ", razonSocialProveedor='" + razonSocialProveedor + '\'' +
                '}';
    }
}
