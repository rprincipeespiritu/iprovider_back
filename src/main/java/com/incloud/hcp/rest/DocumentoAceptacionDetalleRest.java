package com.incloud.hcp.rest;

import com.incloud.hcp.domain.DocumentoAceptacionDetalle;
import com.incloud.hcp.service.DocumentoAceptacionDetalleService;
import com.incloud.hcp.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api/DocumentoAceptacionDetalle")
public class DocumentoAceptacionDetalleRest {

    private DocumentoAceptacionDetalleService documentoAceptacionDetalleService;

    @Autowired
    public DocumentoAceptacionDetalleRest(DocumentoAceptacionDetalleService documentoAceptacionDetalleService) {
        this.documentoAceptacionDetalleService = documentoAceptacionDetalleService;
    }

    @GetMapping(value = "/findDocumentoAceptacionDetalleById/{idDocumentoAceptacion}")
    public ResponseEntity<List<DocumentoAceptacionDetalle>> getDocumentoAceptacionDetalleNoAnuladasListById(@PathVariable("idDocumentoAceptacion") Integer idDocumentoAceptacion){
        try {
            List<DocumentoAceptacionDetalle> documentoAceptacionDetalleList = documentoAceptacionDetalleService.getDocumentoAceptacionDetalleNoAnuladasListById(idDocumentoAceptacion);

            if(documentoAceptacionDetalleList.isEmpty()){
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            documentoAceptacionDetalleList.sort(Comparator.comparingInt(detalle -> 
                Integer.parseInt(detalle.getPosicionOrdenCompra())
            ));

            return new ResponseEntity<>(documentoAceptacionDetalleList, HttpStatus.OK);
        }catch (Exception e) {
                String error = Utils.obtieneMensajeErrorException(e);
                throw new RuntimeException(error);
        }
    }

    @GetMapping(value = "/findDocumentoAceptacionDetalleConAnuladasById/{idDocumentoAceptacion}")
    public ResponseEntity<List<DocumentoAceptacionDetalle>> getDocumentoAceptacionDetalleConAnuladasListById(@PathVariable("idDocumentoAceptacion") Integer idDocumentoAceptacion){
        try {
            List<DocumentoAceptacionDetalle> documentoAceptacionDetalleList = documentoAceptacionDetalleService.getDocumentoAceptacionDetalleConAnuladasListById(idDocumentoAceptacion);

            if(documentoAceptacionDetalleList.isEmpty()){
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(documentoAceptacionDetalleList, HttpStatus.OK);
        }catch (Exception e) {
            String error = Utils.obtieneMensajeErrorException(e);
            throw new RuntimeException(error);
        }
    }

    @RequestMapping(value = "/actaSustento/{idDocumentoAceptacion}", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> getByActaSustento(@PathVariable("idDocumentoAceptacion") Integer idDocumentoAceptacion) throws Exception {

        Object response =this.documentoAceptacionDetalleService.getByIdDocumentoAceptacion(idDocumentoAceptacion);
        return Optional.of(response)
                .map(l -> new ResponseEntity<>(l, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NO_CONTENT));
    }
}
