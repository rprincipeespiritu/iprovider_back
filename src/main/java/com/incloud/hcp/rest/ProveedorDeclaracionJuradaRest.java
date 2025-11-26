package com.incloud.hcp.rest;

import com.incloud.hcp.domain.Banco;
import com.incloud.hcp.domain.ProveedorDeclaracionJurada;
import com.incloud.hcp.dto.RegistroDeclaracionJuradaDto;
import com.incloud.hcp.dto.RegistroDeclaracionJuradaPJDto;
import com.incloud.hcp.exception.PortalException;
import com.incloud.hcp.repository.BancoRepository;
import com.incloud.hcp.rest._framework.AppRest;
import com.incloud.hcp.service.ProveedorDeclaracionJuradaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api/proveedorDeclaracionJurada")
public class ProveedorDeclaracionJuradaRest extends AppRest {

    @Autowired
    private ProveedorDeclaracionJuradaService proveedorDeclaracionJuradaService;

    @RequestMapping(value = "/create",
            method = RequestMethod.POST, produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> create(@RequestBody RegistroDeclaracionJuradaDto bean) throws Exception {

       ProveedorDeclaracionJurada declaracionJurada =  proveedorDeclaracionJuradaService.create(bean);

        return Optional.of(declaracionJurada)
                .map(l -> new ResponseEntity<>(l, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NO_CONTENT));
    }


    @RequestMapping(value = "/personaJuridica/create",
            method = RequestMethod.POST, produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> personaJuridicaCreate(@RequestBody RegistroDeclaracionJuradaPJDto bean) throws Exception {

        ProveedorDeclaracionJurada declaracionJurada =  proveedorDeclaracionJuradaService.personaJuridicaCreate(bean);

        return Optional.of(declaracionJurada)
                .map(l -> new ResponseEntity<>(l, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NO_CONTENT));
    }

    @RequestMapping(value = "/consultar-pj/{idProveedor}",
            method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> consultarPJ(@PathVariable("idProveedor") Integer idProveedor) throws Exception {
        RegistroDeclaracionJuradaPJDto registroDeclaracionJuradaPJDto = null;
        try {
            registroDeclaracionJuradaPJDto = proveedorDeclaracionJuradaService.consultarpj(idProveedor);
        }catch (Exception ex){
            ex.printStackTrace();
            throw new PortalException(ex.getMessage());
        }

        return Optional.of(registroDeclaracionJuradaPJDto)
                .map(l -> new ResponseEntity<>(l, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NO_CONTENT));
    }

    @RequestMapping(value = "/consultar-pn/{idProveedor}",
            method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> consultarPN(@PathVariable("idProveedor") Integer idProveedor) throws Exception {
        RegistroDeclaracionJuradaDto registroDeclaracionJuradaDto = null;
        try {
            registroDeclaracionJuradaDto = proveedorDeclaracionJuradaService.consultarpn(idProveedor);
        }catch (Exception ex){
            throw new PortalException(ex.getMessage());
        }

        return Optional.of(registroDeclaracionJuradaDto)
                .map(l -> new ResponseEntity<>(l, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NO_CONTENT));
    }

}
