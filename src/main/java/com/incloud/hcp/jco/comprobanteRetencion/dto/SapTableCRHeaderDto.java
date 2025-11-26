package com.incloud.hcp.jco.comprobanteRetencion.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class SapTableCRHeaderDto implements Serializable {
    private String numeroDocumentoErp;
    private String sociedad;
    private Integer ejercicio;
    private String claseDocumento;
    private String serieCorrelativoDocumento;
    private String proveedorRuc;
    private String proveedorRazonSocial;
    private String proveedorEmail;
    private Date fechaEmision;
    private Date fechaContabilizacion;
    private String estado;
    private String monedaDocumento;
    private String monedaLocal;
    private BigDecimal importeBaseCalculoRetencion;
    private BigDecimal importeBaseMonedaLocal;
    private BigDecimal importeRetencion;
    private BigDecimal importeRetencionMonedaLocal;



    public String getNumeroDocumentoErp() {
        return numeroDocumentoErp;
    }

    public void setNumeroDocumentoErp(String numeroDocumentoErp) {
        this.numeroDocumentoErp = numeroDocumentoErp;
    }

    public String getSociedad() {
        return sociedad;
    }

    public void setSociedad(String sociedad) {
        this.sociedad = sociedad;
    }

    public Integer getEjercicio() {
        return ejercicio;
    }

    public void setEjercicio(Integer ejercicio) {
        this.ejercicio = ejercicio;
    }

    public String getProveedorRuc() {
        return proveedorRuc;
    }

    public void setProveedorRuc(String proveedorRuc) {
        this.proveedorRuc = proveedorRuc;
    }

    public String getProveedorRazonSocial() {
        return proveedorRazonSocial;
    }

    public void setProveedorRazonSocial(String proveedorRazonSocial) {
        this.proveedorRazonSocial = proveedorRazonSocial;
    }

    public Date getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(Date fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getClaseDocumento() {
        return claseDocumento;
    }

    public void setClaseDocumento(String claseDocumento) {
        this.claseDocumento = claseDocumento;
    }

    public String getSerieCorrelativoDocumento() {
        return serieCorrelativoDocumento;
    }

    public void setSerieCorrelativoDocumento(String serieCorrelativoDocumento) {
        this.serieCorrelativoDocumento = serieCorrelativoDocumento;
    }

    public String getProveedorEmail() {
        return proveedorEmail;
    }

    public void setProveedorEmail(String proveedorEmail) {
        this.proveedorEmail = proveedorEmail;
    }

    public Date getFechaContabilizacion() {
        return fechaContabilizacion;
    }

    public void setFechaContabilizacion(Date fechaContabilizacion) {
        this.fechaContabilizacion = fechaContabilizacion;
    }

    public String getMonedaDocumento() {
        return monedaDocumento;
    }

    public void setMonedaDocumento(String monedaDocumento) {
        this.monedaDocumento = monedaDocumento;
    }

    public String getMonedaLocal() {
        return monedaLocal;
    }

    public void setMonedaLocal(String monedaLocal) {
        this.monedaLocal = monedaLocal;
    }

    public BigDecimal getImporteBaseCalculoRetencion() {
        return importeBaseCalculoRetencion;
    }

    public void setImporteBaseCalculoRetencion(BigDecimal importeBaseCalculoRetencion) {
        this.importeBaseCalculoRetencion = importeBaseCalculoRetencion;
    }

    public BigDecimal getImporteBaseMonedaLocal() {
        return importeBaseMonedaLocal;
    }

    public void setImporteBaseMonedaLocal(BigDecimal importeBaseMonedaLocal) {
        this.importeBaseMonedaLocal = importeBaseMonedaLocal;
    }

    public BigDecimal getImporteRetencion() {
        return importeRetencion;
    }

    public void setImporteRetencion(BigDecimal importeRetencion) {
        this.importeRetencion = importeRetencion;
    }

    public BigDecimal getImporteRetencionMonedaLocal() {
        return importeRetencionMonedaLocal;
    }

    public void setImporteRetencionMonedaLocal(BigDecimal importeRetencionMonedaLocal) {
        this.importeRetencionMonedaLocal = importeRetencionMonedaLocal;
    }

}
