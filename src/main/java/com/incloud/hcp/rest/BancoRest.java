package com.incloud.hcp.rest;

import com.incloud.hcp.domain.Banco;
import com.incloud.hcp.dto.BancoDtoMessage;
import com.incloud.hcp.dto.CanalContactoInDto;
import com.incloud.hcp.dto.NonExistedBankRequest;
import com.incloud.hcp.exception.PortalException;
import com.incloud.hcp.repository.BancoRepository;
import com.incloud.hcp.rest._framework.AppRest;
import com.incloud.hcp.service.BancoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/banco")
public class BancoRest extends AppRest {

    @Autowired
    private BancoRepository bancoRepository;

    @Autowired
    private BancoService bancoService;

    @RequestMapping(value = "",
            method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<Banco>> getListaBanco() throws PortalException {
        List listaTipo = this.bancoRepository.findAll();
        return ResponseEntity.ok().body(listaTipo);
    }


    @RequestMapping(value = "/nonExistedBank", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<BancoDtoMessage> getListaBanco(@RequestBody NonExistedBankRequest bean) throws PortalException {
        return ResponseEntity.ok().body(bancoService.sendEmail(bean));
    }

}
