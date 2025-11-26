package com.incloud.hcp.service;

import com.incloud.hcp.dto.CanalContactoInDto;
import com.incloud.hcp.dto.CanalContactoOutDto;
import com.incloud.hcp.dto.LineaComercialInDto;
import com.incloud.hcp.dto.LineaComercialOutDto;

public interface CanalContactoService {

    public CanalContactoOutDto crearCanalContacto(CanalContactoInDto bean);
}
