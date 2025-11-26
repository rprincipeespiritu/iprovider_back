package com.incloud.hcp.dto;

import com.incloud.hcp.domain.ProveedorAccionistasAsociados;
import com.incloud.hcp.domain.ProveedorAccionistasParientePep;
import com.incloud.hcp.domain.ProveedorAccionistasPep;

import java.util.List;

public class AccionistasAsociadosDto {

    private ProveedorAccionistasAsociados proveedorAccionistasAsociado;
    private List<ProveedorAccionistasPep> proveedorAccionistasPepList;
    private List<ProveedorAccionistasParientePep> proveedorAccionistasParientePepList;


    public ProveedorAccionistasAsociados getProveedorAccionistasAsociado() {
        return proveedorAccionistasAsociado;
    }

    public void setProveedorAccionistasAsociado(ProveedorAccionistasAsociados proveedorAccionistasAsociado) {
        this.proveedorAccionistasAsociado = proveedorAccionistasAsociado;
    }

    public List<ProveedorAccionistasPep> getProveedorAccionistasPepList() {
        return proveedorAccionistasPepList;
    }

    public void setProveedorAccionistasPepList(List<ProveedorAccionistasPep> proveedorAccionistasPepList) {
        this.proveedorAccionistasPepList = proveedorAccionistasPepList;
    }

    public List<ProveedorAccionistasParientePep> getProveedorAccionistasParientePepList() {
        return proveedorAccionistasParientePepList;
    }

    public void setProveedorAccionistasParientePepList(List<ProveedorAccionistasParientePep> proveedorAccionistasParientePepList) {
        this.proveedorAccionistasParientePepList = proveedorAccionistasParientePepList;
    }
}
