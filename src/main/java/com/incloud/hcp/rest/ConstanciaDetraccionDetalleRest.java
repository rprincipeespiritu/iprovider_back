package com.incloud.hcp.rest;


import com.incloud.hcp.domain.ConstanciaDetraccionDetalle;
import com.incloud.hcp.repository.ConstanciaDetraccionDetalleRepository;
import com.incloud.hcp.service.ConstanciaDetraccionDetalleService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@RestController
@RequestMapping("api/constancia-detraccion-detalle")
public class ConstanciaDetraccionDetalleRest {

    @Autowired
    ConstanciaDetraccionDetalleService constanciaDetraccionDetalleService;

    @Autowired
    ConstanciaDetraccionDetalleRepository constanciaDetraccionDetalleRepository;

    @GetMapping("/{id}")
    public ResponseEntity<String> generarPdf(@PathVariable("id") Integer idDetalleConstancia){

        if (idDetalleConstancia == null){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        try{
            Optional<ConstanciaDetraccionDetalle> constanciaDetraccionDetalle  = constanciaDetraccionDetalleRepository.findById(idDetalleConstancia);

            if(constanciaDetraccionDetalle.isPresent()){
                String report = constanciaDetraccionDetalleService.generarPdfConstanciaDetraccion(idDetalleConstancia);

                return new ResponseEntity<>(report, HttpStatus.OK);
            }else {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
        }catch (Exception e){

            throw  new RuntimeException("Error al enviar el pdf " + e.getMessage());
        }



    }


}
