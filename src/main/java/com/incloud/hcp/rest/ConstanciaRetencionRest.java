package com.incloud.hcp.rest;

import com.incloud.hcp.dto.ConstanciaDetraccionMessageDto;
import com.incloud.hcp.dto.ConstanciaRetencionDto;
import com.incloud.hcp.service.ConstanciaRetencionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/constancia-retencion")
public class ConstanciaRetencionRest {

    @Autowired
    private ConstanciaRetencionService constanciaRetencionService;

    @RequestMapping(value = "/cargar", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ConstanciaDetraccionMessageDto> cargarArchivo(@RequestParam("file") MultipartFile file){
        try{
            String result = constanciaRetencionService.saveConstanciaRetenciones(file);
            ConstanciaDetraccionMessageDto response = new ConstanciaDetraccionMessageDto(result, HttpStatus.OK.value());
            return ResponseEntity.ok(response);
        }catch (ResponseStatusException e) {
            ConstanciaDetraccionMessageDto response = new ConstanciaDetraccionMessageDto(e.getReason(), e.getStatus().value());
            return ResponseEntity.status(e.getStatus()).body(response);
        }
    };

    @RequestMapping(value = "/lista", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> obtenerListaRetenciones(
            @RequestParam(value = "nombreProveedor", required = false) String nombreProveedor,
            @RequestParam(value = "rucProveedor", required = false) String rucProveedor,
            @RequestParam(value = "fechaInicio", required = false) String fechaInicio,
            @RequestParam(value = "fechaFin", required = false) String fechaFin,
            @RequestParam(value = "nroRetencion", required = false) String nroRetencion
            ){
        try{
            List<ConstanciaRetencionDto> listaRetenciones = constanciaRetencionService.obtenerListaRetenciones(nombreProveedor, rucProveedor, fechaInicio, fechaFin, nroRetencion);
            return ResponseEntity.ok(listaRetenciones);
        }catch (IllegalArgumentException e){
            Map<String, Object> errorDeBusqueda = new HashMap<>();
            errorDeBusqueda.put("status", HttpStatus.BAD_REQUEST.value());
            errorDeBusqueda.put("message", e.getMessage());

            return ResponseEntity.badRequest().body( errorDeBusqueda);
        }


    }
}
