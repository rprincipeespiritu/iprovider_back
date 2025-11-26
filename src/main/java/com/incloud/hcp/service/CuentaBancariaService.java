package com.incloud.hcp.service;

import com.incloud.hcp.dto.CanalContactoInDto;
import com.incloud.hcp.dto.CanalContactoOutDto;
import com.incloud.hcp.dto.CuentaBancariaInDto;
import com.incloud.hcp.dto.CuentaBancariaOutDto;

import java.util.ArrayList;

public interface CuentaBancariaService {

    public CuentaBancariaOutDto crearCuentaBancaria(CuentaBancariaInDto bean);
}
