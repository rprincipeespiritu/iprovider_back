package com.incloud.hcp.rest;

import com.incloud.hcp.domain.OrdenCompraAdjunto;
import com.incloud.hcp.service.OrdeCompraAdjuntoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/OrdenCompraAdjunto")
public class OrdenCompraAdjuntoRest {

    @Autowired
    OrdeCompraAdjuntoService ordeCompraAdjuntoService;

    @PostMapping("/create")
    public ResponseEntity<?> createAdjunto(@RequestBody List<OrdenCompraAdjunto> ordenCompraAdjuntoList){
        try{
            return ResponseEntity.ok(ordeCompraAdjuntoService.createAdjuntoOc(ordenCompraAdjuntoList));
        }catch (Exception e){
            return ResponseEntity.badRequest().build();
        }

    }

    @PostMapping("/update")
    public ResponseEntity<?> updateAdjunto(@RequestBody List<OrdenCompraAdjunto> ordenCompraAdjuntoList){
        try{
            return ResponseEntity.ok(ordeCompraAdjuntoService.updateAdjuntoOc(ordenCompraAdjuntoList));
        }catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/updateStatus")
    public ResponseEntity<?> updateStatus(@RequestBody List<OrdenCompraAdjunto> ordenCompraAdjuntoList){
        try{
            return ResponseEntity.ok(ordeCompraAdjuntoService.updateStatus(ordenCompraAdjuntoList));
        }catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/getAdjuntoByOc")
    public ResponseEntity<?> getAdjuntoByOc(@RequestParam String numeroOc){
        try {
            List<OrdenCompraAdjunto> ordenCompraAdjunto = ordeCompraAdjuntoService.getAdjuntosByOc(numeroOc);

            return new ResponseEntity<>(ordenCompraAdjunto,HttpStatus.OK);

        }catch (Exception e){
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
        }

    }

}


