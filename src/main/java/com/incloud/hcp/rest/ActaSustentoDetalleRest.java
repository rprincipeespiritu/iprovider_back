package com.incloud.hcp.rest;

import com.incloud.hcp.rest._framework.AppRest;
import com.incloud.hcp.service.ActaSustentoDetalleService;
import com.incloud.hcp.service.ClaseDocumentoService;
import com.incloud.hcp.util.StrUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;


@RestController
@RequestMapping(value = "/api/actaSustentoDetalle")
public class ActaSustentoDetalleRest extends AppRest {


    @Autowired
    private ActaSustentoDetalleService actaSustentoDetalleService;

    @RequestMapping(value = "/find-acta-sustento/{idActaSustento}",
            method = RequestMethod.GET,
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> findActaSustento(@PathVariable("idActaSustento") Integer idActaSustento) {

        try{
            Object obj = this.actaSustentoDetalleService.findByIdActaSustento(idActaSustento);

            return Optional.ofNullable(obj).map(l -> new ResponseEntity<>(l, HttpStatus.OK))
                    .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));

        }catch(Exception e){
            String error = StrUtils.obtieneMensajeErrorExceptionCustom(e);
            throw new RuntimeException(error);
        }
    }
}
