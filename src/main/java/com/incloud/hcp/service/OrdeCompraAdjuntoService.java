package com.incloud.hcp.service;


import com.incloud.hcp.domain.OrdenCompraAdjunto;

import java.util.List;

public interface OrdeCompraAdjuntoService {

    List<OrdenCompraAdjunto> getAdjuntosByOc(String numeroOc);

    String createAdjuntoOc(List<OrdenCompraAdjunto> ordenCompraAdjuntoList);

    String updateAdjuntoOc(List<OrdenCompraAdjunto> ordenCompraAdjuntoList);

    String updateStatus(List<OrdenCompraAdjunto> ordenCompraAdjuntoList);
}
