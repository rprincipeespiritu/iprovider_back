package com.incloud.hcp.jco.banco.service;

import com.incloud.hcp.domain.Banco;

import java.util.List;

public interface JCOBancoService {

    void extraerBancosRFC(String fechaInicio, String fechaFin) throws Exception;
    void extraerBancosRFC_old(String fechaInicio, String fechaFin) throws Exception;

}
