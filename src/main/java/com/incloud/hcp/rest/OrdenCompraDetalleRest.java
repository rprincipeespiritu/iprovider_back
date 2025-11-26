package com.incloud.hcp.rest;

import com.incloud.hcp.domain.OrdenCompra;
import com.incloud.hcp.domain.OrdenCompraDetalle;
import com.incloud.hcp.service.OrdenCompraDetalleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api/OrdenCompraDetalle")
public class OrdenCompraDetalleRest {

    private OrdenCompraDetalleService ordenCompraDetalleService;

    @Autowired
    public OrdenCompraDetalleRest(OrdenCompraDetalleService ordenCompraDetalleService) {
        this.ordenCompraDetalleService = ordenCompraDetalleService;
    }

    @RequestMapping(value = "/findOrdenCompraDetalleById/{id}", method = RequestMethod.GET)
    public ResponseEntity<List<OrdenCompraDetalle>> getOrdenCompraDetalleByIdOcLiberada(@PathVariable("id") Integer idOrdenCompra){
        List<OrdenCompraDetalle> ordenCompraDetalleList = ordenCompraDetalleService.getOrdenCompraDetalleListByIdOcLiberada(idOrdenCompra);

        if(ordenCompraDetalleList.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(ordenCompraDetalleList, HttpStatus.OK);
    }

}
