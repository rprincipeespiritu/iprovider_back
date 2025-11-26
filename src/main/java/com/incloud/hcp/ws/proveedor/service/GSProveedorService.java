package com.incloud.hcp.ws.proveedor.service;

import com.incloud.hcp.domain.Proveedor;
import com.incloud.hcp.ws.proveedor.bean.ProveedorRegistroResponse;

public interface GSProveedorService {

    ProveedorRegistroResponse registro(Proveedor p);

}
