package com.incloud.hcp.bean;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by USER on 05/09/2017.
 */
public class ProveedorCustom implements Serializable{

    private static final long serialVersionUID = 1L;

    private Integer idProveedor;
    private String ruc;
    private String razonSocial;
    private String direccionFiscal;
    private Integer pais;
    private Integer region;
    private String regionDescripcion;
    private Integer provincia;
    private Integer distrito;
    private String email;
    private String telefono;
    private String tipoProveedor;
    private String tipoPersona;
    private String estadoHomologacion;
    private String evaluacionHomologacion;
    private String indBlackList;
    private String indMigradoSap;
    private String indBlackListString;
    private String idHCP;
    private String codigoAcreedorSap;
    private Date fechaModificacion;
    private Integer flagActivo;

    private Integer idAreaCompra;

    public ProveedorCustom() {
    }

    public ProveedorCustom(Integer idProveedor, String idHCP) {
        this.idProveedor = idProveedor;
        this.idHCP = idHCP;
    }

    public Integer getFlagActivo() {
        return flagActivo;
    }

    public void setFlagActivo(Integer flagActivo) {
        this.flagActivo = flagActivo;
    }

    public Integer getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(Integer idProveedor) {
        this.idProveedor = idProveedor;
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getDireccionFiscal() {
        return direccionFiscal;
    }

    public void setDireccionFiscal(String direccionFiscal) {
        this.direccionFiscal = direccionFiscal;
    }

    public String getEmail() {
        return email;
    }


    public void setEmail(String email) {
        this.email = email;
    }

    public Date getFechaModificacion() {
        return fechaModificacion;
    }

    public void setFechaModificacion(Date fechaModificacion) {
        this.fechaModificacion = fechaModificacion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getTipoProveedor() {
        return tipoProveedor;
    }

    public void setTipoProveedor(String tipoProveedor) {
        this.tipoProveedor = tipoProveedor;
    }

    public String getTipoPersona() {
        return tipoPersona;
    }

    public void setTipoPersona(String tipoPersona) {
        this.tipoPersona = tipoPersona;
    }

    public String getEstadoHomologacion() {
        return estadoHomologacion;
    }

    public void setEstadoHomologacion(String estadoHomologacion) {
        this.estadoHomologacion = estadoHomologacion;
    }

    public String getEvaluacionHomologacion() {
        return evaluacionHomologacion;
    }

    public void setEvaluacionHomologacion(String evaluacionHomologacion) {
        this.evaluacionHomologacion = evaluacionHomologacion;
    }

    public Integer getPais() {
        return pais;
    }

    public void setPais(Integer pais) {
        this.pais = pais;
    }

    public Integer getRegion() {
        return region;
    }

    public void setRegion(Integer region) {
        this.region = region;
    }

    public Integer getProvincia() {
        return provincia;
    }

    public void setProvincia(Integer provincia) {
        this.provincia = provincia;
    }

    public Integer getDistrito() {
        return distrito;
    }

    public void setDistrito(Integer distrito) {
        this.distrito = distrito;
    }

    public String getIndBlackList() {
        return indBlackList;
    }

    public void setIndBlackList(String indBlackList) {
        this.indBlackList = indBlackList;
    }

    public String getIndBlackListString() {
        return indBlackListString;
    }

    public void setIndBlackListString(String indBlackListString) {
        this.indBlackListString = indBlackListString;
    }

    public String getIdHCP() {
        return idHCP;
    }

    public void setIdHCP(String idHCP) {
        this.idHCP = idHCP;
    }

    public String getIndMigradoSap() {
        return indMigradoSap;
    }

    public void setIndMigradoSap(String indMigradoSap) {
        this.indMigradoSap = indMigradoSap;
    }

    public String getCodigoAcreedorSap() {
        return codigoAcreedorSap;
    }

    public void setCodigoAcreedorSap(String codigoAcreedorSap) {
        this.codigoAcreedorSap = codigoAcreedorSap;
    }

    public String getRegionDescripcion() {
        return regionDescripcion;
    }

    public void setRegionDescripcion(String regionDescripcion) {
        this.regionDescripcion = regionDescripcion;
    }

    public Integer getIdAreaCompra() {
        return idAreaCompra;
    }

    public void setIdAreaCompra(Integer idAreaCompra) {
        this.idAreaCompra = idAreaCompra;
    }

    @Override
    public String toString() {
        return "ProveedorCustom{" +
                "idProveedor=" + idProveedor +
                ", ruc='" + ruc + '\'' +
                ", razonSocial='" + razonSocial + '\'' +
                ", direccionFiscal='" + direccionFiscal + '\'' +
                ", pais='" + pais + '\'' +
                ", region='" + region + '\'' +
                ", provincia='" + provincia + '\'' +
                ", distrito='" + distrito + '\'' +
                ", email='" + email + '\'' +
                ", telefono='" + telefono + '\'' +
                ", tipoProveedor='" + tipoProveedor + '\'' +
                ", tipoPersona='" + tipoPersona + '\'' +
                ", estadoHomologacion='" + estadoHomologacion + '\'' +
                ", evaluacionHomologacion='" + evaluacionHomologacion + '\'' +
                ", indBlackList='" + indBlackList + '\'' +
                ", indMigradoSap='" + indMigradoSap + '\'' +
                ", indBlackListString='" + indBlackListString + '\'' +
                ", idHCP='" + idHCP + '\'' +
                ", codigoAcreedorSap='" + codigoAcreedorSap + '\'' +
                ", fechaModificacion='" + fechaModificacion + '\'' +
                ", idAreaCompra='" + idAreaCompra + '\'' +
                '}';
    }
}
