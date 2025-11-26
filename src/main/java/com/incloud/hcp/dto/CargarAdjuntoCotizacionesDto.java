package com.incloud.hcp.dto;

import java.util.List;

public class CargarAdjuntoCotizacionesDto {

    private Integer idLicitacion;
    private Integer Cotizacion;
    private CotizacionAdjuntoBase64Dto cotizacionAdjuntoBase64Dto;

    public Integer getIdLicitacion() {
        return idLicitacion;
    }

    public void setIdLicitacion(Integer idLicitacion) {
        this.idLicitacion = idLicitacion;
    }

    public Integer getCotizacion() {
        return Cotizacion;
    }

    public void setCotizacion(Integer cotizacion) {
        Cotizacion = cotizacion;
    }

    public CotizacionAdjuntoBase64Dto getCotizacionAdjuntoBase64Dto() {
        return cotizacionAdjuntoBase64Dto;
    }

    public void setCotizacionAdjuntoBase64Dto(CotizacionAdjuntoBase64Dto cotizacionAdjuntoBase64Dto) {
        this.cotizacionAdjuntoBase64Dto = cotizacionAdjuntoBase64Dto;
    }

    @Override
    public String toString() {
        return "LicitacionAdjudicadoDTO{" +
                "idLicitacion=" + idLicitacion +
                "cotizacion=" + Cotizacion +
                "cotizacionAdjuntoBase64Dto=" + cotizacionAdjuntoBase64Dto +
                '}';
    }
}
