package com.incloud.hcp.rest;

import com.incloud.hcp.service.extractor.SolicitudPedidoGs;
import com.incloud.hcp.util.Utils;
import com.incloud.hcp.ws.obtenersolicitudcompra.dto.SolicitudCompraResponseDto;
import com.incloud.hcp.ws.obtenersolicitudcompra.service.GSObtenerSolicitudCompraService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URISyntaxException;
import java.util.Optional;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Este Rest llama al controlador del paquete ws que contiene los servicios de las interfaces del grupo Topitop.
 */


@RestController
@RequestMapping("/api/wsSilvestre")
public class WsRest {

 private final Logger log = LoggerFactory.getLogger(this.getClass());

 @Autowired
 GSObtenerSolicitudCompraService gsObtenerSolicitudCompraService;

 @Autowired
 SolicitudPedidoGs solicitudPedidoGs;





 @GetMapping(value = "/getSolped/{solped}", produces = APPLICATION_JSON_VALUE)
 public ResponseEntity<SolicitudCompraResponseDto>  getPeticionOfertaGs (@PathVariable Integer solped) throws URISyntaxException {
  log.debug("getPeticionOfertaGs" );
  try {
   return Optional.ofNullable(this.solicitudPedidoGs.solicitudCompraByCodigo(solped,false))
           .map(l -> new ResponseEntity<>(l, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  } catch (Exception e) {
   String error = Utils.obtieneMensajeErrorException(e);
   throw new RuntimeException(error);
  }

 }

}
