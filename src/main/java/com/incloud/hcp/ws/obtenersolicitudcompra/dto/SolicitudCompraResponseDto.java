package com.incloud.hcp.ws.obtenersolicitudcompra.dto;

import com.incloud.hcp.bean.ProveedorCustom;
import com.incloud.hcp.bean.SolicitudPedido;

import java.util.List;

public class SolicitudCompraResponseDto {

    private List<SolicitudPedido> listaSolped;
    private List<ProveedorCustom> listaProveedorSeleccionado;

    public List<SolicitudPedido> getListaSolped() {
        return listaSolped;
    }

    public void setListaSolped(List<SolicitudPedido> listaSolped) {
        this.listaSolped = listaSolped;
    }

    public List<ProveedorCustom> getListaProveedorSeleccionado() {
        return listaProveedorSeleccionado;
    }

    public void setListaProveedorSeleccionado(List<ProveedorCustom> listaProveedorSeleccionado) {
        this.listaProveedorSeleccionado = listaProveedorSeleccionado;
    }

    @Override
    public String toString() {
        return "SolicitudCompraResponseDto{" +
                "listaSolped=" + listaSolped +
                ", listaProveedorSeleccionado=" + listaProveedorSeleccionado +
                '}';
    }
}
