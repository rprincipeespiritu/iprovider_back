package com.incloud.hcp.jco.solped.dto;

import com.incloud.hcp.bean.SolicitudPedido;
import com.incloud.hcp.sap.SapLog;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class SolicitudPedidoRFCResponseDto implements Serializable {

    private SapLog sapLog;
    private List<SolicitudPedido> listaSolped;

    private Integer idCentro;
    private Integer idAlmacen;
    private Integer idClaseDocumento;
    private Date    fechaEntrega;
    private Integer idMoneda;
    private BigDecimal precioSolped;
    private String estadoSolped;

    public SapLog getSapLog() {
        return sapLog;
    }

    public void setSapLog(SapLog sapLog) {
        this.sapLog = sapLog;
    }

    public Integer getIdMoneda() {
        return idMoneda;
    }

    public void setIdMoneda(Integer idMoneda) {
        this.idMoneda = idMoneda;
    }

    public List<SolicitudPedido> getListaSolped() {
        return listaSolped;
    }

    public void setListaSolped(List<SolicitudPedido> listaSolped) {
        this.listaSolped = listaSolped;
    }

    public Integer getIdCentro() {
        return idCentro;
    }

    public void setIdCentro(Integer idCentro) {
        this.idCentro = idCentro;
    }

    public Integer getIdAlmacen() {
        return idAlmacen;
    }

    public void setIdAlmacen(Integer idAlmacen) {
        this.idAlmacen = idAlmacen;
    }

    public Integer getIdClaseDocumento() {
        return idClaseDocumento;
    }

    public void setIdClaseDocumento(Integer idClaseDocumento) {
        this.idClaseDocumento = idClaseDocumento;
    }

    public Date getFechaEntrega() {
        return fechaEntrega;
    }

    public void setFechaEntrega(Date fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    public BigDecimal getPrecioSolped() {
        return precioSolped;
    }

    public void setPrecioSolped(BigDecimal precioSolped) {
        this.precioSolped = precioSolped;
    }

    public String getEstadoSolped() {
        return estadoSolped;
    }

    public void setEstadoSolped(String estadoSolped) {
        this.estadoSolped = estadoSolped;
    }

    @Override
    public String toString() {
        return "SolicitudPedidoRFCResponseDto{" +
                "sapLog=" + sapLog +
                ", listaSolped=" + listaSolped +
                ", idCentro=" + idCentro +
                ", idAlmacen=" + idAlmacen +
                ", idClaseDocumento=" + idClaseDocumento +
                ", fechaEntrega=" + fechaEntrega +
                ", precioSolped=" + precioSolped +
                ", estadoSolped=" + estadoSolped +
                '}';
    }
}
