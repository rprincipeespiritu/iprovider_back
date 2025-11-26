package com.incloud.hcp.rest;

import com.incloud.hcp.bean.UserSession;
import com.incloud.hcp.domain.CriteriosBlacklist;
import com.incloud.hcp.rest._framework.AppRest;
import com.incloud.hcp.service.CriterioBlackListService;
import com.incloud.hcp.service.CriterioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping(value = "/api/criterio")
public class CriterioRest extends AppRest {


    @Autowired
    private CriterioService criterioService;

    @RequestMapping(value = "/{tipoCriterio}", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> getCriterioByTipoCriterio(@PathVariable("tipoCriterio") String tipoCriterio) throws Exception {

        Object response =this.criterioService.getCriterioByTipoCriterio(tipoCriterio);
        return Optional.of(response)
                .map(l -> new ResponseEntity<>(l, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NO_CONTENT));
    }

}
