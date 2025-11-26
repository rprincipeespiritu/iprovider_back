package com.incloud.hcp.dto;

import com.incloud.hcp.domain.ProveedorAntecedente;
import com.incloud.hcp.domain.ProveedorDeclaracionJurada;
import com.incloud.hcp.domain.ProveedorParientePep;
import com.incloud.hcp.domain.ProveedorPep;

import java.util.List;

public class RegistroDeclaracionJuradaDto {
    private ProveedorDeclaracionJurada proveedorDeclaracionJurada;
    private List<ProveedorAntecedente> proveedorAntecedenteList;
    private List<ProveedorPep> proveedorPepList;
    private List<ProveedorParientePep> proveedorParientePepList;

    public ProveedorDeclaracionJurada getProveedorDeclaracionJurada() {
        return proveedorDeclaracionJurada;
    }

    public void setProveedorDeclaracionJurada(ProveedorDeclaracionJurada proveedorDeclaracionJurada) {
        this.proveedorDeclaracionJurada = proveedorDeclaracionJurada;
    }

    public List<ProveedorAntecedente> getProveedorAntecedenteList() {
        return proveedorAntecedenteList;
    }

    public void setProveedorAntecedenteList(List<ProveedorAntecedente> proveedorAntecedenteList) {
        this.proveedorAntecedenteList = proveedorAntecedenteList;
    }

    public List<ProveedorPep> getProveedorPepList() {
        return proveedorPepList;
    }

    public void setProveedorPepList(List<ProveedorPep> proveedorPepList) {
        this.proveedorPepList = proveedorPepList;
    }

    public List<ProveedorParientePep> getProveedorParientePepList() {
        return proveedorParientePepList;
    }

    public void setProveedorParientePepList(List<ProveedorParientePep> proveedorParientePepList) {
        this.proveedorParientePepList = proveedorParientePepList;
    }
}
