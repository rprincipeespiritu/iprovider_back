package com.incloud.hcp.rest;

import com.incloud.hcp.domain.AreaCompras;
import com.incloud.hcp.domain.Usuario;
import com.incloud.hcp.repository.AreaComprasRepository;
import com.incloud.hcp.rest._framework.AppRest;
import com.incloud.hcp.service.AreaComprasService;
import com.incloud.hcp.util.StrUtils;
import com.incloud.hcp.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api/areacompras")

public class AreaComprasRest extends AppRest {
    @Autowired
    AreaComprasService areaComprasService;
    AreaComprasRepository areaComprasRepository;

    @RequestMapping(value = "",
            method = RequestMethod.GET,
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> getAreaCompra() {

        return this.processList(areaComprasService.getAllAreas());
    }

}
