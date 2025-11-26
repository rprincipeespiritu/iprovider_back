package com.incloud.hcp.dto;

import com.incloud.hcp.domain.*;

import java.util.Date;
import java.util.List;

public class ConsultaOrdenCompra {

    private List<OrdenCompra> ordenCompraSapList;
    private  List<OrdenCompraTextoCabecera> ordenCompraTextoCabeceraSapList ;
    private List<OrdenCompraDetalle> ordenCompraDetalleSapList;
    private List<OrdenCompraDetalleTexto> ordenCompraDetalleTextoPosicionSapList;
    private List<OrdenCompraDetalleTextoRegistroInfo> ordenCompraDetalleTextoRegistroInfoSapList;
    private List<OrdenCompraDetalleTextoMaterialAmpliado> ordenCompraDetalleTextoMaterialAmpliadoSapList;

    public List<OrdenCompra> getOrdenCompraSapList() {
        return ordenCompraSapList;
    }

    public void setOrdenCompraSapList(List<OrdenCompra> ordenCompraSapList) {
        this.ordenCompraSapList = ordenCompraSapList;
    }

    public List<OrdenCompraTextoCabecera> getOrdenCompraTextoCabeceraSapList() {
        return ordenCompraTextoCabeceraSapList;
    }

    public void setOrdenCompraTextoCabeceraSapList(List<OrdenCompraTextoCabecera> ordenCompraTextoCabeceraSapList) {
        this.ordenCompraTextoCabeceraSapList = ordenCompraTextoCabeceraSapList;
    }

    public List<OrdenCompraDetalle> getOrdenCompraDetalleSapList() {
        return ordenCompraDetalleSapList;
    }

    public void setOrdenCompraDetalleSapList(List<OrdenCompraDetalle> ordenCompraDetalleSapList) {
        this.ordenCompraDetalleSapList = ordenCompraDetalleSapList;
    }

    public List<OrdenCompraDetalleTexto> getOrdenCompraDetalleTextoPosicionSapList() {
        return ordenCompraDetalleTextoPosicionSapList;
    }

    public void setOrdenCompraDetalleTextoPosicionSapList(List<OrdenCompraDetalleTexto> ordenCompraDetalleTextoPosicionSapList) {
        this.ordenCompraDetalleTextoPosicionSapList = ordenCompraDetalleTextoPosicionSapList;
    }

    public List<OrdenCompraDetalleTextoRegistroInfo> getOrdenCompraDetalleTextoRegistroInfoSapList() {
        return ordenCompraDetalleTextoRegistroInfoSapList;
    }

    public void setOrdenCompraDetalleTextoRegistroInfoSapList(List<OrdenCompraDetalleTextoRegistroInfo> ordenCompraDetalleTextoRegistroInfoSapList) {
        this.ordenCompraDetalleTextoRegistroInfoSapList = ordenCompraDetalleTextoRegistroInfoSapList;
    }

    public List<OrdenCompraDetalleTextoMaterialAmpliado> getOrdenCompraDetalleTextoMaterialAmpliadoSapList() {
        return ordenCompraDetalleTextoMaterialAmpliadoSapList;
    }

    public void setOrdenCompraDetalleTextoMaterialAmpliadoSapList(List<OrdenCompraDetalleTextoMaterialAmpliado> ordenCompraDetalleTextoMaterialAmpliadoSapList) {
        this.ordenCompraDetalleTextoMaterialAmpliadoSapList = ordenCompraDetalleTextoMaterialAmpliadoSapList;
    }

    @Override
    public String toString() {
        return "ConsultaOC{" +
                "ordenCompraSapList=" + ordenCompraSapList +
                ", ordenCompraTextoCabeceraSapList='" + ordenCompraTextoCabeceraSapList + '\'' +
                ", ordenCompraDetalleSapList='" + ordenCompraDetalleSapList + '\'' +
                ", ordenCompraDetalleTextoPosicionSapList='" + ordenCompraDetalleTextoPosicionSapList + '\'' +
                ", ordenCompraDetalleTextoRegistroInfoSapList='" + ordenCompraDetalleTextoRegistroInfoSapList + '\'' +
                ", ordenCompraDetalleTextoMaterialAmpliadoSapList='" + ordenCompraDetalleTextoMaterialAmpliadoSapList + '\'' +
                '}';
    }
}
