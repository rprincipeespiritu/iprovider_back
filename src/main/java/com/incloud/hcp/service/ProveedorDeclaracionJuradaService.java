package com.incloud.hcp.service;

import com.incloud.hcp.domain.Banco;
import com.incloud.hcp.domain.ProveedorDeclaracionJurada;
import com.incloud.hcp.dto.RegistroDeclaracionJuradaDto;
import com.incloud.hcp.dto.RegistroDeclaracionJuradaPJDto;

import java.util.List;
import java.util.Optional;

public interface ProveedorDeclaracionJuradaService {

    ProveedorDeclaracionJurada create(RegistroDeclaracionJuradaDto bean) throws Exception;

    ProveedorDeclaracionJurada personaJuridicaCreate(RegistroDeclaracionJuradaPJDto bean) throws Exception;

    RegistroDeclaracionJuradaPJDto consultarpj(Integer idProveedor) throws Exception;

    RegistroDeclaracionJuradaDto consultarpn(Integer idProveedor) throws Exception;
}
