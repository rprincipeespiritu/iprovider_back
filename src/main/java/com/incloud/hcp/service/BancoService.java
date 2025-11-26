package com.incloud.hcp.service;

import com.incloud.hcp.domain.Banco;
import com.incloud.hcp.domain.Ubigeo;
import com.incloud.hcp.dto.BancoDtoMessage;
import com.incloud.hcp.dto.NonExistedBankRequest;

import java.util.List;

public interface BancoService {
    List<Banco> getListAll();

    Banco getBancoByCodigo(String codigo);

    BancoDtoMessage sendEmail(NonExistedBankRequest nonExistedBankRequest);
}
