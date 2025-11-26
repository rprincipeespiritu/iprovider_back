package com.incloud.hcp.rest;

import com.incloud.hcp.domain.Moneda;
import com.incloud.hcp.dto.FacturaSapDto;
import com.incloud.hcp.exception.PortalException;
import com.incloud.hcp.repository.MonedaRepository;
import com.incloud.hcp.rest._framework.AppRest;
import com.incloud.hcp.service.MonedaService;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import okhttp3.*;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(value = "/api/moneda")
public class MonedaRest extends AppRest {
private final MonedaService monedaService;
    @Autowired
    private MonedaRepository monedaRepository;

    public MonedaRest(MonedaService monedaService) {
        this.monedaService = monedaService;
    }
    @RequestMapping(value = "",
            method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<Moneda>> getListaMoneda() throws PortalException {
        List listaTipo = this.monedaRepository.findAll();
        return ResponseEntity.ok().body(listaTipo);
    }

}