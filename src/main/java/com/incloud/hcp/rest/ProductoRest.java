package com.incloud.hcp.rest;

import com.incloud.hcp.dto.CanalContactoInDto;
import com.incloud.hcp.dto.CanalContactoOutDto;
import com.incloud.hcp.dto.ProductoDto;
import com.incloud.hcp.dto.ProductoOutDto;
import com.incloud.hcp.rest._framework.AppRest;
import com.incloud.hcp.service.ProductoService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Created by Administrador on 08/06/2021.
 */
@RestController
@RequestMapping(value = "/api/producto")
public class ProductoRest extends AppRest {

    @Autowired
    private ProductoService productoService;

    @ApiOperation(value = "Servicio Producto - Proveedor", produces = "application/json")
    @PostMapping(value = "/_crearProducto", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<ProductoOutDto> crearProducto(
            @RequestBody ProductoDto bean){
        HttpHeaders headers = new HttpHeaders();


        ProductoOutDto lista = productoService.crearProducto(bean);
        return Optional.ofNullable(lista).map(l -> new ResponseEntity<>(l, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));


    }

}
