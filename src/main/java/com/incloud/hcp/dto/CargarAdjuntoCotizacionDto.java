package com.incloud.hcp.dto;

import com.incloud.hcp.domain.CentroAlmacen;
import com.incloud.hcp.domain.ClaseDocumento;
import com.incloud.hcp.domain.LicitacionProveedor;
import com.incloud.hcp.domain.Moneda;

import java.sql.Timestamp;
import java.util.List;

public class CargarAdjuntoCotizacionDto {

    private Integer idLicitacion;
    private Integer Cotizacion;
    private List<CotizacionAdjuntoBase64Dto> cotizacionAdjuntoBase64Dto;

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

    public List<CotizacionAdjuntoBase64Dto> getCotizacionAdjuntoBase64Dto() {
        return cotizacionAdjuntoBase64Dto;
    }

    public void setCotizacionAdjuntoBase64Dto(List<CotizacionAdjuntoBase64Dto> cotizacionAdjuntoBase64Dto) {
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
