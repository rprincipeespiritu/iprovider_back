package com.incloud.hcp.rest;

import com.incloud.hcp.domain.DocumentoAceptacion;
import com.incloud.hcp.dto.ComprobantePagoEntradaDto;
import com.incloud.hcp.dto.DocumentoAceptacionEntradaDto;
import com.incloud.hcp.jco.comprobantePago.dto.ComprobantePagoDto;
import com.incloud.hcp.service.ComprobantePagoService;
import com.incloud.hcp.service.extractor.ComprobantePagoGsService;
import com.incloud.hcp.util.Utils;
import com.incloud.hcp.ws.docspendientes.dto.ComprobantePagoGsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(value = "/api/ComprobantePago")
public class ComprobantePagoRest {

    private ComprobantePagoService comprobantePagoService;

    @Autowired
    public ComprobantePagoRest(ComprobantePagoService comprobantePagoService) {
        this.comprobantePagoService = comprobantePagoService;
    }
    @Autowired
    ComprobantePagoGsService comprobantePagoGsService;

    @GetMapping(value = "/getComprobantePagoList")
    public ResponseEntity<List<ComprobantePagoDto>> getComprobantePagoList(
            @RequestParam(value = "fechaInicio", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fechaInicio,
            @RequestParam(value = "fechaFin", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fechaFin,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "numeroComprobante", required = false) String numeroComprobante,
            @RequestParam(value = "codigoSociedad", required = false) String codigoSociedad){
        try{
            List<ComprobantePagoDto> comprobantePagoDtoList = comprobantePagoService.getComprobantePagoListPorFechasAndRuc(fechaInicio, fechaFin, email, numeroComprobante, codigoSociedad);

            if(comprobantePagoDtoList.isEmpty()){
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(comprobantePagoDtoList, HttpStatus.OK);
        } catch (Exception e) {
            String error = Utils.obtieneMensajeErrorException(e);
            throw new RuntimeException(error);
        }
    }

    @PostMapping(value = "/getPostComprobantePagoList")
    public ResponseEntity<List<ComprobantePagoDto>> getPostComprobantePagoList(@RequestBody ComprobantePagoEntradaDto bean) {
        try {
            List<ComprobantePagoDto> listaRetorno = this.comprobantePagoService.getComprobantePagoList(bean);

            if (listaRetorno.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(listaRetorno, HttpStatus.OK);
        } catch (Exception e) {
            String error = Utils.obtieneMensajeErrorException(e);
            throw new RuntimeException(error);
        }
    }

    @PostMapping(value = "/setComprobanteFactoring")
    public ResponseEntity<List<ComprobantePagoDto>> setComprobanteFactoring(@RequestBody ComprobantePagoEntradaDto bean) {
        try {
            ComprobantePagoDto listaRetorno = this.comprobantePagoService.setComprobanteFactoring(bean);

            if (listaRetorno == null) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity(listaRetorno, HttpStatus.OK);
        } catch (Exception e) {
            String error = Utils.obtieneMensajeErrorException(e);
            throw new RuntimeException(error);
        }
    }

   /*@GetMapping(value = "/getComprobantePagoList")
   public ResponseEntity<List<ComprobantePagoGsDto>> getComprobantePagoList(
           @RequestParam(value = "fechaInicio", required = false)  String fechaInicio,
           @RequestParam(value = "fechaFin", required = false) String fechaFin,
           @RequestParam(value = "email", required = false) String email){
       try{
           List<ComprobantePagoGsDto> comprobantePagoDtoList = comprobantePagoGsService.getComprobantePagoList(fechaInicio,fechaFin,email);

           if(comprobantePagoDtoList.isEmpty()){
               return new ResponseEntity<>(HttpStatus.NO_CONTENT);
           }
           return new ResponseEntity<>(comprobantePagoDtoList, HttpStatus.OK);
       } catch (Exception e) {
           String error = Utils.obtieneMensajeErrorException(e);
           throw new RuntimeException(error);
       }
   }*/
}
