package com.incloud.hcp.rest;

import com.incloud.hcp.domain.Banco;
import com.incloud.hcp.domain.EstadoCivil;
import com.incloud.hcp.domain.MtrTipoDocumento;
import com.incloud.hcp.exception.PortalException;
import com.incloud.hcp.repository.BancoRepository;
import com.incloud.hcp.repository.EstadoCivilRepository;
import com.incloud.hcp.rest._framework.AppRest;
import com.incloud.hcp.util.Utils;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/api/estadoCivil")
public class EstadoCivilRest extends AppRest {

    @Autowired
    private EstadoCivilRepository estadoCivilRepository;

    @ApiOperation(value = "Devuelve lista de registros de tipo estadoCivil", produces = "application/json")
    @GetMapping(value = "/findAll", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<EstadoCivil>> findAll() throws URISyntaxException {

        try {
            return Optional.
                    of(this.estadoCivilRepository.findAll()).map(l -> new ResponseEntity<>(l, HttpStatus.OK))
                    .orElse(new ResponseEntity<>(HttpStatus.NO_CONTENT));
        } catch (Exception e) {
            String error = Utils.obtieneMensajeErrorException(e);
            throw new RuntimeException(error);
        }
    }


    @ApiOperation(value = "Busca registro de tipo EstadoCivil en base al id enviado", produces = "application/json")
    @GetMapping(value = "/findById/{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<EstadoCivil> findById(@PathVariable Integer id) throws URISyntaxException {

        try {
            return Optional.ofNullable(this.estadoCivilRepository.findById(id).get())
                    .map(EstadoCivil -> new ResponseEntity<>(EstadoCivil, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            String error = Utils.obtieneMensajeErrorException(e);
            throw new RuntimeException(error);
        }
    }

}
