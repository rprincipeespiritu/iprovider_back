package com.incloud.hcp.dto;

import com.incloud.hcp.domain.*;

import java.util.List;

public class RegistroDeclaracionJuradaPJDto {
    private ProveedorDeclaracionJurada proveedorDeclaracionJurada;
    private List<ProveedorAntecedente> proveedorAntecedenteSociedadList;
    private ProveedorRepresentanteLegal proveedorRepresentanteLegal;
    private List<ProveedorAntecedenteRepresentanteLegal> proveedorAntecedenteRepresentanteList;

    private List<ProveedorPep> proveedorPepRepresentanteList;
    private List<ProveedorParientePep> proveedorParientePepRepresentanteList;

    private List<AccionistasAsociadosDto> proveedorAccionistasAsociadosList;

    public List<AccionistasAsociadosDto> getProveedorAccionistasAsociadosList() {
        return proveedorAccionistasAsociadosList;
    }

    public void setProveedorAccionistasAsociadosList(List<AccionistasAsociadosDto> proveedorAccionistasAsociadosList) {
        this.proveedorAccionistasAsociadosList = proveedorAccionistasAsociadosList;
    }

    public ProveedorDeclaracionJurada getProveedorDeclaracionJurada() {
        return proveedorDeclaracionJurada;
    }

    public void setProveedorDeclaracionJurada(ProveedorDeclaracionJurada proveedorDeclaracionJurada) {
        this.proveedorDeclaracionJurada = proveedorDeclaracionJurada;
    }

    public List<ProveedorAntecedente> getProveedorAntecedenteSociedadList() {
        return proveedorAntecedenteSociedadList;
    }

    public void setProveedorAntecedenteSociedadList(List<ProveedorAntecedente> proveedorAntecedenteSociedadList) {
        this.proveedorAntecedenteSociedadList = proveedorAntecedenteSociedadList;
    }

    public ProveedorRepresentanteLegal getProveedorRepresentanteLegal() {
        return proveedorRepresentanteLegal;
    }

    public void setProveedorRepresentanteLegal(ProveedorRepresentanteLegal proveedorRepresentanteLegal) {
        this.proveedorRepresentanteLegal = proveedorRepresentanteLegal;
    }

    public List<ProveedorAntecedenteRepresentanteLegal> getProveedorAntecedenteRepresentanteList() {
        return proveedorAntecedenteRepresentanteList;
    }

    public void setProveedorAntecedenteRepresentanteList(List<ProveedorAntecedenteRepresentanteLegal> proveedorAntecedenteRepresentanteList) {
        this.proveedorAntecedenteRepresentanteList = proveedorAntecedenteRepresentanteList;
    }


    public List<ProveedorPep> getProveedorPepRepresentanteList() {
        return proveedorPepRepresentanteList;
    }

    public void setProveedorPepRepresentanteList(List<ProveedorPep> proveedorPepRepresentanteList) {
        this.proveedorPepRepresentanteList = proveedorPepRepresentanteList;
    }

    public List<ProveedorParientePep> getProveedorParientePepRepresentanteList() {
        return proveedorParientePepRepresentanteList;
    }

    public void setProveedorParientePepRepresentanteList(List<ProveedorParientePep> proveedorParientePepRepresentanteList) {
        this.proveedorParientePepRepresentanteList = proveedorParientePepRepresentanteList;
    }
}
