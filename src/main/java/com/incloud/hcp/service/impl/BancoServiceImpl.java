package com.incloud.hcp.service.impl;

import com.incloud.hcp.domain.Banco;
import com.incloud.hcp.domain.Parametro;
import com.incloud.hcp.dto.BancoDtoMessage;
import com.incloud.hcp.dto.NonExistedBankRequest;
import com.incloud.hcp.myibatis.mapper.BancoMapper;
import com.incloud.hcp.myibatis.mapper.ParametroMapper;
import com.incloud.hcp.repository.BancoRepository;
import com.incloud.hcp.repository.ParametroRepository;
import com.incloud.hcp.service.BancoService;
import com.incloud.hcp.service.notificacion.EnvioBancoNonExistNotification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BancoServiceImpl implements BancoService {

    private BancoRepository bancoRepository;
    private BancoMapper bancoMapper;

    @Autowired
    private EnvioBancoNonExistNotification envioBancoNonExistNotification;

    @Autowired
    private ParametroMapper parametroMapper;

    @Autowired
    private ParametroRepository parametroRepository;

    @Autowired
    public void setBancoRepository(BancoRepository bancoRepository) {
        this.bancoRepository = bancoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Banco> getListAll() {
        return this.bancoRepository.findAll();
    }

    @Override
    public Banco getBancoByCodigo(String codigo) {
        return bancoMapper.getBancoByCodigo(codigo);    }

    @Override
    public BancoDtoMessage sendEmail(NonExistedBankRequest nonExistedBankRequest) {

        Parametro parametro= parametroRepository.getParametroByModuloAndTipoAndCodigo("CORREO", "CORREO CONTABILIDAD", "CB");

        String mensaje = envioBancoNonExistNotification
                .enviarBankNonExist(this.parametroMapper.getMailSetting(), nonExistedBankRequest, parametro.getValor());
        BancoDtoMessage bancoDtoMessage = new BancoDtoMessage();
        bancoDtoMessage.setMensaje(mensaje);

        return bancoDtoMessage;
    }

}
