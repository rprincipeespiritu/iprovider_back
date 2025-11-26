package com.incloud.hcp.dto;

import com.incloud.hcp.domain.Licitacion;

public class EliminarCotizacionAdjuntoDto {
    String archivoId;
    Integer licitacion;
    Integer idProveedor;

    public String getArchivoId() {
        return archivoId;
    }

    public void setArchivoId(String archivoId) {
        this.archivoId = archivoId;
    }

    public Integer getLicitacion() {
        return licitacion;
    }

    public void setLicitacion(Integer licitacion) {
        this.licitacion = licitacion;
    }

    public Integer getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(Integer idProveedor) {
        this.idProveedor = idProveedor;
    }

    @Override
    public String toString() {
        return "Adjunto{" +
                "archivoId=" + archivoId +
                ", licitacion='" + licitacion + '\'' +
                ", idProveedor='" + idProveedor + '\'' +
                '}';
    }
}
