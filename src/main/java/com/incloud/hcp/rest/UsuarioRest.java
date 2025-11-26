package com.incloud.hcp.rest;

import com.incloud.hcp.domain.Usuario;
import com.incloud.hcp.rest._framework.AppRest;
import com.incloud.hcp.service.UsuarioService;
import com.incloud.hcp.util.StrUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by MARCELO on 22/09/2017.
 */
@RestController
@RequestMapping(value = "/api/usuarios")
public class UsuarioRest extends AppRest {

    @Autowired
    private UsuarioService usuarioService;

    @RequestMapping(value = "", method = RequestMethod.GET, produces =
            {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<java.util.List<Usuario>> gelAllUsuario(){
        return Optional.ofNullable(usuarioService.getAllUsuario())
                .map(l -> new ResponseEntity<>(l, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @RequestMapping(value = "", method = RequestMethod.POST, produces =
            {MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Map> save(@RequestBody Usuario usuario){
        return usuarioService.save(usuario);
    }

    @RequestMapping(value = "", method = RequestMethod.PUT, produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Map> edit(@RequestBody Usuario user){
        return usuarioService.update(user);
    }

    @RequestMapping(value = "", method = RequestMethod.DELETE, produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Map> delete(@RequestBody Usuario user){
        return usuarioService.delete(user);
    }

    @RequestMapping(value = "/areacompra", method = RequestMethod.GET, produces =
            {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})

    public ResponseEntity<List<Usuario>> getAreaCompra(
            @RequestParam(value = "emailUsuario", required = false) String emailUsuario
        ) {

        try {
            List<Usuario> usuComprador = this.usuarioService.findByEmailUsuarioComprador(emailUsuario);

            return new ResponseEntity<>(usuComprador,HttpStatus.OK);

        } catch (Exception e) {
            String error = StrUtils.obtieneMensajeErrorExceptionCustom(e);
            throw new RuntimeException(error);

        }
    }
}
