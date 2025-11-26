package com.incloud.hcp.dto;

import com.incloud.hcp.domain.OrdenCompra;
import com.incloud.hcp.domain.OrdenCompraDetalle;

import java.util.List;

public class OrdenCompraFinalDto {

    private ProveedoresFinalDto proveedores;

    public ProveedoresFinalDto getProveedores() {
        return proveedores;
    }

    public void setProveedores(ProveedoresFinalDto proveedores) {
        this.proveedores = proveedores;
    }
}
