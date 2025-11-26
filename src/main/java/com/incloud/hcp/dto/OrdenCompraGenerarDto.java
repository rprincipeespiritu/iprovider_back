package com.incloud.hcp.dto;

import com.incloud.hcp.domain.*;
import io.swagger.models.auth.In;

import java.io.Serializable;
import java.util.List;

public class OrdenCompraGenerarDto  {

    private OrdenCompra ordenCompra;
    private Integer idLicitacion;
    private List<ProveedoresDto> proveedores;
    private List<OrdenCompraDetalle> ordenCompraDetalleList;
    private String emailComprador;
    private List<Sociedades> sociedades;


    public static  class  Sociedades{
       private String sociedad;

        public String getSociedad() {
            return sociedad;
        }

        public void setSociedad(String sociedad) {
            this.sociedad = sociedad;
        }
    }

    public List<Sociedades> getSociedades() {
        return sociedades;
    }

    public void setSociedades(List<Sociedades> sociedades) {
        this.sociedades = sociedades;
    }

    public OrdenCompra getOrdenCompra() {
        return ordenCompra;
    }

    public void setOrdenCompra(OrdenCompra ordenCompra) {
        this.ordenCompra = ordenCompra;
    }

    public List<OrdenCompraDetalle> getOrdenCompraDetalleList() {
        return ordenCompraDetalleList;
    }

    public void setOrdenCompraDetalleList(List<OrdenCompraDetalle> ordenCompraDetalleList) {
        this.ordenCompraDetalleList = ordenCompraDetalleList;
    }

    public String getEmailComprador() {
        return emailComprador;
    }

    public void setEmailComprador(String emailComprador) {
        this.emailComprador = emailComprador;
    }

    public List<ProveedoresDto> getProveedores() {
        return proveedores;
    }

    public void setProveedores(List<ProveedoresDto> proveedores) {
        this.proveedores = proveedores;
    }

    public Integer getIdLicitacion() {
        return idLicitacion;
    }

    public void setIdLicitacion(Integer idLicitacion) {
        this.idLicitacion = idLicitacion;
    }
}
