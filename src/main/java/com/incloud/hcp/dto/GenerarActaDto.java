package com.incloud.hcp.dto;

import com.incloud.hcp.domain.OrdenCompraDetalle;

import java.util.List;

public class GenerarActaDto {

    private Integer idOrdenCompra;
    private String correoProveedor;
    private List<OrdenCompraDetalle> ordenCompraDetalle;
    private List<AdjuntoActaSustentoBase64Dto> actaSustentoBase64Dto;


    public List<OrdenCompraDetalle> getOrdenCompraDetalle() {
        return ordenCompraDetalle;
    }

    public void setOrdenCompraDetalle(List<OrdenCompraDetalle> ordenCompraDetalle) {
        this.ordenCompraDetalle = ordenCompraDetalle;
    }

    public String getCorreoProveedor() {
        return correoProveedor;
    }

    public void setCorreoProveedor(String correoProveedor) {
        this.correoProveedor = correoProveedor;
    }

    public Integer getIdOrdenCompra() {
        return idOrdenCompra;
    }

    public void setIdOrdenCompra(Integer idOrdenCompra) {
        this.idOrdenCompra = idOrdenCompra;
    }

    public List<AdjuntoActaSustentoBase64Dto> getActaSustentoBase64Dto() {
        return actaSustentoBase64Dto;
    }

    public void setActaSustentoBase64Dto(List<AdjuntoActaSustentoBase64Dto> actaSustentoBase64Dto) {
        this.actaSustentoBase64Dto = actaSustentoBase64Dto;
    }
}
