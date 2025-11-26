package com.incloud.hcp.service;

import com.incloud.hcp.domain.AreaCompras;
import com.incloud.hcp.domain.Banco;
import com.incloud.hcp.domain.Usuario;

import java.util.List;

public interface AreaComprasService {

    List<AreaCompras> getListAll();

    List<AreaCompras> getAllAreas();
}
