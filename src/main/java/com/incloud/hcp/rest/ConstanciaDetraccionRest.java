package com.incloud.hcp.rest;


import com.incloud.hcp.dto.ConstanciaDetraccionDto;
import com.incloud.hcp.dto.ConstanciaDetraccionMessageDto;
import com.incloud.hcp.pdf.PdfGeneratorFactory;
import com.incloud.hcp.service.ConstanciaDetraccionService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping(value = "api/costancia-detraccion")
public class ConstanciaDetraccionRest {

    @Autowired
    private ConstanciaDetraccionService constanciaDetraccionService;

    @RequestMapping(value = "/cargar", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ConstanciaDetraccionMessageDto> cargarArchivo(@RequestParam("file") MultipartFile file){
        try{
             String result = constanciaDetraccionService.saveConstanciaDetraccion(file);
            ConstanciaDetraccionMessageDto response = new ConstanciaDetraccionMessageDto(result, HttpStatus.OK.value());
             return ResponseEntity.ok(response);
        }catch (ResponseStatusException e) {
            ConstanciaDetraccionMessageDto response = new ConstanciaDetraccionMessageDto(e.getReason(), e.getStatus().value());
            return ResponseEntity.status(e.getStatus()).body(response);
        }
    };

    @RequestMapping(value = "/lista", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getListaDetracciones( @RequestParam(value = "razonSocial", required = false) String razonSocial,
                                                                               @RequestParam(value = "fechaInicio", required = false) String fechaInicio,
                                                                               @RequestParam(value = "fechaFin", required = false) String fechaFin,
                                                                               @RequestParam(value = "nroComprobante", required = false) String nroComprobante,
                                                                               @RequestParam( value ="ruc", required = false) String ruc){

       try {
           List<ConstanciaDetraccionDto> detracciones = constanciaDetraccionService.getListaDetracciones(razonSocial, fechaInicio, fechaFin, nroComprobante, ruc);
           return ResponseEntity.ok(detracciones);
       }catch (IllegalArgumentException e ){
           Map<String, Object> errorDeBusqueda = new HashMap<>();
           errorDeBusqueda.put("status", HttpStatus.BAD_REQUEST.value());
           errorDeBusqueda.put("message", e.getMessage());

           return ResponseEntity.badRequest().body( errorDeBusqueda);

       }
    };

   /* @GetMapping("/pdf")
    public ResponseEntity<byte[]> getReportPdf(){
        byte[] reporte = PdfGeneratorFactory.getJasperGenerator().generateConstanciaDetraccion();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_PDF);
        httpHeaders.setContentDispositionFormData("report", "report.pdf");

        return new ResponseEntity<>(reporte, httpHeaders, HttpStatus.OK);
    }*/

}
