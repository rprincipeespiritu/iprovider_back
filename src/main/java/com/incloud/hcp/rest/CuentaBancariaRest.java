package com.incloud.hcp.rest;

import com.incloud.hcp.dto.CanalContactoInDto;
import com.incloud.hcp.dto.CanalContactoOutDto;
import com.incloud.hcp.dto.CuentaBancariaInDto;
import com.incloud.hcp.dto.CuentaBancariaOutDto;
import com.incloud.hcp.rest._framework.AppRest;
import com.incloud.hcp.service.CanalContactoService;
import com.incloud.hcp.service.CuentaBancariaService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Optional;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/cuenta-bancaria")
public class CuentaBancariaRest extends AppRest {

    @Autowired
    private CuentaBancariaService cuentaBancariaService;

    @ApiOperation(value = "Servicio Cuenta Bancaria - Proveedor", produces = "application/json")
    @PostMapping(value = "/_crearCuentaBancaria", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<CuentaBancariaOutDto> crearCanalContacto(
            @RequestBody CuentaBancariaInDto bean){
        HttpHeaders headers = new HttpHeaders();


        CuentaBancariaOutDto lista = cuentaBancariaService.crearCuentaBancaria(bean);
        return Optional.ofNullable(lista).map(l -> new ResponseEntity<>(l, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));


    }
}
