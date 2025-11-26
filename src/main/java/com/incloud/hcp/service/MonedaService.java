package com.incloud.hcp.service;

import com.incloud.hcp.domain.Moneda;
import com.incloud.hcp.dto.FacturaSapDto;
import java.util.List;

public interface MonedaService {
    List<Moneda> getListAll();
    //List<FacturaSapDto> getEstadoCuentaProveedor(String email);
}
