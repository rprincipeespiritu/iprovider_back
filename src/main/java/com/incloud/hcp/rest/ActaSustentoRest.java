package com.incloud.hcp.rest;

import com.incloud.hcp.domain.ActaSustento;
import com.incloud.hcp.domain.Licitacion;
import com.incloud.hcp.domain.LogTransaccion;
import com.incloud.hcp.dto.*;
import com.incloud.hcp.exception.PortalException;
import com.incloud.hcp.myibatis.mapper.ParametroMapper;
import com.incloud.hcp.repository.LogTransaccionRepository;
import com.incloud.hcp.rest._framework.AppRest;
import com.incloud.hcp.service.ActaSustentoService;
import com.incloud.hcp.service.CargoService;
import com.incloud.hcp.service.notificacion.ActaSustentoNotificacion;
import com.incloud.hcp.util.StrUtils;
import com.incloud.hcp.util.Utils;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


@RestController
@RequestMapping(value = "/api/actaSustento")
public class ActaSustentoRest extends AppRest {


    @Autowired
    private ActaSustentoService actaSustentoService;

    @Autowired
    private ActaSustentoNotificacion actaSustentoNotificacion;

    @Autowired
    private ParametroMapper parametroMapper;

    @Autowired
    private LogTransaccionRepository logTransaccionRepository;




    @RequestMapping(value = "/generarActa",
            method = RequestMethod.POST,
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> generarActa(@RequestBody GenerarActaDto bean) {

        try{
            Object obj = this.actaSustentoService.generarActa(bean);
            return Optional.ofNullable(obj).map(l -> new ResponseEntity<>(l, HttpStatus.OK))
                    .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));

        }catch(Exception e){
            String error = StrUtils.obtieneMensajeErrorExceptionCustom(e);
            throw new RuntimeException(error);
        }

    }


    @RequestMapping(value = "/filtroPaginado", method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<List<ActaSustento>> devuelveListaLicitacionByFiltroPaginado(@RequestBody Map<String, Object> json)  {
        List<ActaSustento> lista = null; //ActaSustento.getListaLicitacionByFiltroPaginado(json);
        try {
            lista = actaSustentoService.getListaActaByFiltroPaginado(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //return ResponseEntity.ok().body(lista);
        return Optional.of(lista)
                .map(l -> new ResponseEntity<>(l, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NO_CONTENT));
    }


    @RequestMapping(value = "/evaluar-acta/{idActaSustento}/{estado}",
            method = RequestMethod.POST,
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> findActaSustento(
            @PathVariable("idActaSustento") Integer idActaSustento,
            @PathVariable("estado") String estado,
            @RequestBody ActaSustentoRechazarDto bean) {

        try{
            Object obj = this.actaSustentoService.evaluarActa(idActaSustento,estado,bean);

            return Optional.ofNullable(obj).map(l -> new ResponseEntity<>(l, HttpStatus.OK))
                    .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));

        }catch(Exception e){
            String error = StrUtils.obtieneMensajeErrorExceptionCustom(e);
            throw new RuntimeException(error);
        }
    }


    @RequestMapping(value = "/filtroPaginadoProveedor", method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<List<ActaSustento>> actaByFiltroPaginado(
            @RequestBody Map<String, Object> json)  {
        List<ActaSustento> lista = null;
        try {
            lista = actaSustentoService.getListaActaByFiltroPaginadoProveedor("",json);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //return ResponseEntity.ok().body(lista);
        return Optional.of(lista)
                .map(l -> new ResponseEntity<>(l, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NO_CONTENT));
    }

    @RequestMapping(value = "/filtroPaginadoProveedorRechazada", method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<List<ActaSustento>> filtroPaginadoProveedorRechazada(
            @RequestBody Map<String, Object> json)  {
        List<ActaSustento> lista = null;
        try {
            lista = actaSustentoService.getListaActaByFiltroPaginadoProveedorRechazada(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //return ResponseEntity.ok().body(lista);
        return Optional.of(lista)
                .map(l -> new ResponseEntity<>(l, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NO_CONTENT));
    }


    @RequestMapping(value = "/evaluarActaSustento", method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<?> evaluarActaSustento(@RequestBody ActaSustentoEvaluacionDto bean)  {
        ActaSustento lista = new ActaSustento();
        String respuesta = "";
        try {
            lista = actaSustentoService.evaluarActaSustentoProveedor(bean);
            if(lista.getIdActaSustento() != null) {

                respuesta = actaSustentoNotificacion.enviarCorreoProveedorActaEmHes(this.parametroMapper.getMailSetting(), lista.getProveedor(), lista.getNumeroActaSustento(), bean, lista);
//                LogTransaccion logTransaccion = new LogTransaccion();
//                logTransaccion.setEnvioTrama("actaSustentoNotificacion");
//                logTransaccion.setRespuestaCodigo(respuesta);
//                logTransaccion.setTipoRegistro("evaluarActaSustento");
//                this.logTransaccionRepository.save(logTransaccion);
            }
            return new ResponseEntity<>(lista,HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
//            LogTransaccion logTransaccion = new LogTransaccion();
//            logTransaccion.setEnvioTrama("actaSustentoNotificacion");
//            logTransaccion.setRespuestaCodigo(e.getMessage());
//            logTransaccion.setTipoRegistro("evaluarActaSustento");
//            this.logTransaccionRepository.save(logTransaccion);
            String error = Utils.obtieneMensajeErrorException(e);
            throw new RuntimeException(error);
        }
    }


    @RequestMapping(value = "/anularActaSustento/{idActaSustento}", method = RequestMethod.GET, headers = "Accept=application/json")
    public ResponseEntity<?> evaluarActaSustento(@PathVariable Integer idActaSustento)  {
        ActaSustento lista = new ActaSustento();
        try {
            lista = actaSustentoService.anularActaSustentoProveedor(idActaSustento);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //return ResponseEntity.ok().body(lista);
        return Optional.of(lista)
                .map(l -> new ResponseEntity<>(l, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NO_CONTENT));
    }

    @ApiOperation(value = "Obtener Entradas de Mercancias RPA", produces = "application/json")
    @GetMapping(value = "/getObtenerEMRPA", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<EntradaMercanciaRequestRPA> getEntradaMercanciasRPA(){

        try {
            EntradaMercanciaRequestRPA entradaMercanciaRequestRPAS =  this.actaSustentoService.getEntradaMercanciaRPA();
            return new ResponseEntity<>(entradaMercanciaRequestRPAS,HttpStatus.OK);
        } catch (Exception e) {
            String error = Utils.obtieneMensajeErrorException(e);
            throw new RuntimeException(error);
        }
    }

    @ApiOperation(value = "Setear Entradas de mercancia RPA", produces = "application/json")
    @PostMapping(value = "/setEMRPA", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<EntradaMercanciaResponseRPA> setEntradaMercanciasRPA(EntradaMercanciaRequestRespuestaRPA entradaMercanciaRequestRespuestaRPA){

        try {
            EntradaMercanciaResponseRPA ordenCompraRequestRPAS =  this.actaSustentoService.setEntradaMercanciaRPA(entradaMercanciaRequestRespuestaRPA);
            return new ResponseEntity<>(ordenCompraRequestRPAS,HttpStatus.OK);
        } catch (Exception e) {
            String error = Utils.obtieneMensajeErrorException(e);
            throw new RuntimeException(error);
        }
    }

    @ApiOperation(value = "Obtener Entradas de Mercancias RPA", produces = "application/json")
    @GetMapping(value = "/getObtenerHESRPA", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<HESRequestRPA> getHESRPA(){

        try {
            HESRequestRPA hesRequestRPAS =  this.actaSustentoService.getHESRPA();
            return new ResponseEntity<>(hesRequestRPAS,HttpStatus.OK);
        } catch (Exception e) {
            String error = Utils.obtieneMensajeErrorException(e);
            throw new RuntimeException(error);
        }
    }

    @ApiOperation(value = "Setear Entradas de mercancia RPA", produces = "application/json")
    @PostMapping(value = "/setHESRPA", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<HESResponseRPA> setHESRPA(HESRequestRespuestaRPA hesRequestRespuestaRPA){

        try {
            HESResponseRPA hesRequestRPAS =  this.actaSustentoService.setHESRPA(hesRequestRespuestaRPA);
            return new ResponseEntity<>(hesRequestRPAS,HttpStatus.OK);
        } catch (Exception e) {
            String error = Utils.obtieneMensajeErrorException(e);
            throw new RuntimeException(error);
        }
    }
}
