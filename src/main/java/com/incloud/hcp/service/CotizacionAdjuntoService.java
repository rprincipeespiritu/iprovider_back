package com.incloud.hcp.service;

import com.incloud.hcp.domain.Cotizacion;
import com.incloud.hcp.domain.CotizacionAdjunto;
import com.incloud.hcp.domain.Licitacion;

import java.util.List;

/**
 * Created by USER on 06/11/2017.
 */
public interface CotizacionAdjuntoService {

    public void deleteCotizacionAdjuntoByCotizacionArchivoId(Cotizacion cotizacion, String archivoId);
    String eliminarAdjunto ( String archivoId);
}
