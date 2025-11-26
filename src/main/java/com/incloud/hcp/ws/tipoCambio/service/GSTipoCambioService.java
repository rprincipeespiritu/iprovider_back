package com.incloud.hcp.ws.tipoCambio.service;

import com.incloud.hcp.ws.tipoCambio.bean.TipoCambioResponse;

public interface GSTipoCambioService {

    TipoCambioResponse obtenerTipoCambio(int idEmpresa, String fecha);
}
