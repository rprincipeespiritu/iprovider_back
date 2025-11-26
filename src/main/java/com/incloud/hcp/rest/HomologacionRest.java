package com.incloud.hcp.rest;

import com.incloud.hcp.bean.UserSession;
import com.incloud.hcp.bean.UserSessionFront;
import com.incloud.hcp.domain.*;
import com.incloud.hcp.dto.*;
import com.incloud.hcp.dto.homologacion.LineaComercialHomologacionBodyDto;
import com.incloud.hcp.enums.EstadoProveedorEnum;
import com.incloud.hcp.enums.TipoHomologacionEnum;
import com.incloud.hcp.exception.PortalException;
import com.incloud.hcp.facade.HomologacionFacade;
import com.incloud.hcp.myibatis.mapper.HomologacionMapper;
import com.incloud.hcp.myibatis.mapper.ParametroMapper;
import com.incloud.hcp.myibatis.mapper.ProveedorMapper;
import com.incloud.hcp.repository.EstadoProveedorRepository;
import com.incloud.hcp.repository.LogTransaccionRepository;
import com.incloud.hcp.repository.ProveedorLineaComercialRepository;
import com.incloud.hcp.repository.ProveedorRepository;
import com.incloud.hcp.rest._framework.AppRest;
import com.incloud.hcp.service.HomologacionService;
import com.incloud.hcp.service.ProveedorService;
import com.incloud.hcp.service.notificacion.HomologacionAprobadaNotificacion;
import com.incloud.hcp.service.notificacion.HomologacionAprobadaNotificacionHomExt;
import com.incloud.hcp.util.DateUtils;
import com.incloud.hcp.util.Utils;
import com.incloud.hcp.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by Administrador on 28/09/2017.
 */
@RestController
@RequestMapping(value = "/api/homologacion")
public class HomologacionRest extends AppRest {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private HomologacionService homologacionService;
    @Autowired
    private ProveedorService proveedorService;

    @Autowired
    private ProveedorMapper proveedorMapperMybatis;
    @Autowired
    private HomologacionMapper homologacionMapper;

    @Autowired
    private ProveedorRepository proveedorRepository;
    @Autowired
    private HomologacionAprobadaNotificacion homologacionAprobadaNotificacion;
    private HomologacionFacade homologacionFacade;
    private Validator<HomologacionDto> validator;
    @Autowired
    private HomologacionAprobadaNotificacionHomExt homologacionAprobadaNotificacionHomExt;
    @Autowired
    private ProveedorLineaComercialRepository proveedorLineaComercialRepository;
    @Autowired
    private ParametroMapper parametroMapper;
    @Autowired
    private LogTransaccionRepository logTransaccionRepository;
    @Autowired
    public void setHomologacionFacade(HomologacionFacade homologacionFacade) {
        this.homologacionFacade = homologacionFacade;
    }

    @Autowired
    @Qualifier(value ="homologacionValidatorImpl")
    public void setValidator(Validator<HomologacionDto> validator) {
        this.validator = validator;
    }

    public Validator<HomologacionDto> getValidator() {
        return validator;
    }

    @RequestMapping(value = "/proveedor",
            method = RequestMethod.POST, produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> guardarHomologacion(HttpServletRequest request,
                                                 @RequestBody LineaComercialHomologacionBodyDto value) throws PortalException {
                                                 //@RequestBody List<LineaComercialHomologacionDto> list) throws PortalException {

        if (!Optional.ofNullable(value.getUserFront().getUserName()).isPresent()) {
            throw new PortalException("El usuario no esta registrado como proveedor");
        }

        Proveedor p = this.proveedorService.getProveedorDtoByEmail(value.getUserFront().getUserName());
        return this.processObject(homologacionService.guardarHomologacion(p, value.getList()));
    }

    @RequestMapping(value = "/proveedor",
            method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> getListHomologacionByIdProveedor(HttpServletRequest request) throws PortalException {
        UserSession userSession = this.getUserSession();
        if (!Optional.ofNullable(userSession.getRuc()).isPresent()) {
            throw new PortalException("El usuario no esta registrado como proveedor");
        }
        Proveedor p = this.proveedorService.getProveedorByRuc(userSession.getRuc());
        if (p !=null)
            return this.processObject(homologacionService.getListHomologacionByIdProveedor(p.getIdProveedor()));
        else
            return this.processObject(null);
    }

    @RequestMapping(value = "/proveedor/responder",
            method = RequestMethod.POST, produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Map> getListHomologacionByIdProveedorResponder(HttpServletRequest request, @RequestBody UserSessionFront userFront) throws PortalException {

        if (!Optional.ofNullable(userFront.getUserName()).isPresent()) {
            throw new PortalException("El usuario no esta registrado como proveedor");
        }
        Proveedor p = this.proveedorService.getProveedorDtoByEmail(userFront.getUserName());
        if (p !=null) {
            String estado = p.getIdEstadoProveedor().getCodigoEstadoProveedor();
            Integer id = p.getIdProveedor();
            if (estado.equalsIgnoreCase("REG") || estado.equalsIgnoreCase("RDM")) {
                logger.error("No cuenta con permisos para ingresar al módulo");
                throw new PortalException("No cuenta con permisos para ingresar al módulo");
            } else {
                logger.error("Proveedor valido");
                return this.processObject(homologacionService.getListHomologacionByIdProveedorResponder(p.getIdProveedor()));
            }
        } else {
            logger.error("Proveedor no existe");
            return this.processObject(null);
        }
    }

    @RequestMapping(value = "/proveedor/publicacion/responder",
            method = RequestMethod.POST, produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Map> getListHomologacion(HttpServletRequest request, @RequestBody UserSessionFront userFront) throws PortalException {

        if (!Optional.ofNullable(userFront.getUserName()).isPresent()) {
            throw new PortalException("El usuario no esta registrado como proveedor");
        }
        Proveedor p = this.proveedorService.getProveedorDtoByEmail(userFront.getUserName());
        if (p !=null) {
            String estado = p.getIdEstadoProveedor().getCodigoEstadoProveedor();
            Integer id = p.getIdProveedor();
            if (estado.equalsIgnoreCase("REG") || estado.equalsIgnoreCase("RDM") ||
                    estado.equalsIgnoreCase("ADM")) {
                logger.error("No cuenta con permisos para ingresar al módulo");
                throw new PortalException("No cuenta con permisos para ingresar al módulo");
            } else {
                logger.error("Proveedor valido");
                return this.processObject(p);
            }
        } else {
            logger.error("Proveedor no existe");
            return this.processObject(null);
        }
    }

    //Inicio Prueba
    @RequestMapping(value = "/proveedor/pruebita/ppo",
            method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> getPruebita(HttpServletRequest request) throws PortalException {
        UserSession userSession = this.getUserSession();
        if (userSession !=null)
            return this.processObject(userSession);
        else
            return this.processObject(null);
    }
    @RequestMapping(value = "/proveedor/pruebita/responder",
            method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> getPruebita2(HttpServletRequest request) throws PortalException {

        Proveedor p = this.proveedorService.getProveedorByRuc("10420686442");
        if (p !=null)
            return this.processObject(homologacionService.getListHomologacionByIdProveedorResponder(p.getIdProveedor()));
        else
            return this.processObject(null);

    }
    //Fin prueba

    @RequestMapping(value = "/proveedor/{idProveedor}/verNota",
            method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<ProveedorVerNotaDto> verNota(@PathVariable("idProveedor") Integer idProveedor) throws PortalException {
        try {
            Proveedor p = proveedorRepository.getOne(idProveedor);
            if (!Optional.ofNullable(idProveedor).isPresent()) {
                throw new PortalException("El proveedor no existe");
            }
            logger.error("Ingresando verNota proveedor 01 p: " + p.toString());
            ProveedorVerNotaDto proveedorVerNotaDto = this.homologacionService.verNota(idProveedor);
            logger.error("Ingresando verNota proveedor 02 proveedorVerNotaDto: " + proveedorVerNotaDto.toString());
            logger.error("Ingresando verNota proveedor 03 p: " + p.toString());
            p.setEvaluacionHomologacion(proveedorVerNotaDto.getEvaluacionHomologacion());
            p.setFechaModificacion(DateUtils.obtenerFechaHoraActual());
            this.proveedorRepository.save(p);

            return Optional.ofNullable(proveedorVerNotaDto)
                    .map(l -> new ResponseEntity<>(l, HttpStatus.OK))
                    .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));

        } catch (Exception e) {
            String error = Utils.obtieneMensajeErrorException(e);
            throw new RuntimeException(error);
        }
    }

    @RequestMapping(value = "/proveedor/{idProveedor}/evaluar",
            method = RequestMethod.PATCH, produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE})
    public HomologacionNroAcreedorDto evaluarProveedor(@PathVariable("idProveedor") Integer idProveedor) throws Exception {
        try {
            logger.debug("Find by id proveedor : {}", idProveedor);
            Proveedor p = this.proveedorRepository.getProveedorByIdProveedor(idProveedor);
            HomologacionNroAcreedorDto respuesta = this.homologacionService.homologarProveedor(p.getIdProveedor(), "", "");
            return respuesta;
        } catch (Exception e) {
            EstadoProveedor estadoProveedor = estadoProveedorRepository.getByCodigoEstadoProveedor(
                    EstadoProveedorEnum.PENDIENTE_EVALUACION_MAESTRA.getCodigo());
            proveedorMapperMybatis.updateEstadoProveedor(idProveedor,estadoProveedor.getId());
            throw new RuntimeException(e);
        }
    }

    @RequestMapping(value = "/proveedor/{idProveedor}/{fechaIni}/{fechaFin}/actualizarHom",
            method = RequestMethod.PATCH, produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE})
    public HomologacionNroAcreedorDto actualizarHom(@PathVariable("idProveedor") Integer idProveedor, @PathVariable("fechaIni") String fechaIni , @PathVariable("fechaFin") String fechaFin) throws Exception {
        logger.debug("Find by id proveedor : {}", idProveedor);
        Proveedor p = this.proveedorRepository.getProveedorByIdProveedor(idProveedor);
        HomologacionNroAcreedorDto respuesta =   this.homologacionService.homologarProveedor(p.getIdProveedor(), fechaIni, fechaFin);
        return respuesta;
    }

    @RequestMapping(value = "/proveedor/{idProveedor}/homologacionExterna",
            method = RequestMethod.POST, produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> homologarProveedor(@PathVariable("idProveedor") Integer idProveedor) throws Exception {
        Proveedor p = this.proveedorRepository.getProveedorByIdProveedor(idProveedor);
        Object respuesta =   this.homologacionService.homologacionExternoProveedor(p.getIdProveedor());
        return  this.processObject(respuesta);
    }

    @RequestMapping(value = "/proveedor/{idProveedor}/lineas",
            method = RequestMethod.POST, produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> LineasProveedor(@PathVariable("idProveedor") Integer idProveedor) throws Exception {
        List <ProveedorLineaComercial> proveedorLineaComercial = this.proveedorLineaComercialRepository.getListLineaComercialByIdProveedor(idProveedor);


        List <String> list = new ArrayList<> ();
        String respuesta ="";
        Integer homologado = 0;
        Optional<Proveedor> p = this.proveedorRepository.findById(idProveedor);
        Proveedor rp = p.get();
        for(ProveedorLineaComercial item : proveedorLineaComercial){
            if(item.getFamilia().getIndHomExt().equals("1")){
                System.out.println(item.getFamilia().getIndHomExt());
                homologado = homologado + 1;
                list.add(item.getFamilia().getDescripcion());

            }
        }

        if(homologado == 0) {
            String respuestaCorreo1 = "";
            respuestaCorreo1 = this.homologacionAprobadaNotificacion.enviar(this.parametroMapper.getMailSetting(), rp);
            respuesta  = "actualizado correctamente";
            LogTransaccion logTransaccion = new LogTransaccion();
            logTransaccion.setEnvioTrama("homologacionAprobadaNotificacion");
            logTransaccion.setRespuestaCodigo(respuestaCorreo1);
            logTransaccion.setTipoRegistro("homologacionAprobadaNotificacion");
            this.logTransaccionRepository.save(logTransaccion);
        }else{
            String respuestaCorreo = "";
            respuestaCorreo = this.homologacionAprobadaNotificacionHomExt.enviar(this.parametroMapper.getMailSetting(),rp,list);
            respuesta = "Actualizado,necesita homologacion externa";

            LogTransaccion logTransaccion1 = new LogTransaccion();
            logTransaccion1.setEnvioTrama("homologacionAprobadaNotificacionHomExt");
            logTransaccion1.setRespuestaCodigo(respuestaCorreo);
            logTransaccion1.setTipoRegistro("Correo homologacionAprobadaNotificacionHomExt");
            this.logTransaccionRepository.save(logTransaccion1);
        }

        return  this.processObject(respuesta);
    }


    @RequestMapping(value = "/proveedor/{idProveedor}/actualizar",
            method = RequestMethod.PATCH, produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> actualizarProveedor(@PathVariable("idProveedor") Integer idProveedor) throws Exception {
        logger.debug("Find by id proveedor : {}", idProveedor);
        Proveedor p = this.proveedorRepository.getProveedorByIdProveedor(idProveedor);
        Object respuesta =   this.homologacionService.actualizarProveedor(p.getIdProveedor());
        return  this.processObject(respuesta);
    }

    @RequestMapping(value = "",
            method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> getAllHomologacion() {
        return this.processList(homologacionFacade.getListAll());
    }

    @RequestMapping(value = "/{idHomologacion}",
            method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> getHomologacionById(@PathVariable("idHomologacion") Integer id) {
        return this.processObject(homologacionFacade.getHomologacionDto(id));
    }

    @RequestMapping(value = "",
            method = RequestMethod.POST,
            produces = {MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> save(@Valid @RequestBody HomologacionDto dto, BindingResult result) {
//        HomologacionNewDto newHlg = new HomologacionNewDto();
//        List<TipoHomologacionDto> lista = new ArrayList<>();

//        if(dto.getIdHomologacion() == null){
//            Integer getId = homologacionMapper.getIdSequence();
//
//            newHlg.setEstado("1");
//            newHlg.setPeso(dto.getPeso());
//            newHlg.setPregunta(dto.getPregunta());
//            newHlg.setUsuarioCreacion(1);
//            newHlg.setIdLineaComercial(dto.getIdLineaComercial());
//            newHlg.setIndAdjunto("0");
//            String indicador = "0";
//            lista = dto.getTipo();
//            for (TipoHomologacionDto t: lista){
//                if(t.isSeleccionado() && t.getDescripcion().equals(TipoHomologacionEnum.Adjunto)){
//                    newHlg.setIndAdjunto("1");
//                }
//            }
//
//            homologacionMapper.getCrearHomologacion(newHlg);
//            dto.setIdHomologacion(getId);
//        }

        getValidator().validate(dto, result);
//        dto.setEstado("1");

        if (result.hasErrors()) {
            PortalException ex = new PortalException("Información inválida");
            result.getFieldErrors().stream().forEach(f -> ex.addDetail(f.getField(), f.getDefaultMessage()));
            throw ex;
        }
        return this.processObject(this.homologacionFacade.guardar(dto));
    }

    @RequestMapping(value = "/desactivarPregunta/{idHomologacion}",
            method = RequestMethod.POST, produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE})
    public String desactivarPregunta(HttpServletRequest request,
                                     @PathVariable("idHomologacion") int idHomologacion) throws PortalException {
        homologacionService.updateHomologacion(idHomologacion);

        return "Succes";
    }

    @RequestMapping(value = "/activarPregunta/{idHomologacion}",
            method = RequestMethod.POST, produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE})
    public String activarPregunta(HttpServletRequest request,
                                     @PathVariable("idHomologacion") int idHomologacion) throws PortalException {
        homologacionService.updateHomologacionActivar(idHomologacion);

        return "Succes";
    }

    @Autowired
    private EstadoProveedorRepository estadoProveedorRepository;

    @RequestMapping(value = "/proveedor/{idProveedor}/rechazoHomologacion",
            method = RequestMethod.PATCH, produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Object> rechazarProveedorHom(@PathVariable("idProveedor") Integer idProveedor, @RequestBody String motivo) throws PortalException {
        try {
            Proveedor p = proveedorRepository.getOne(idProveedor);
            if (!Optional.ofNullable(idProveedor).isPresent()) {
                throw new PortalException("El proveedor no existe");
            }
            EstadoProveedor estadoProveedor = estadoProveedorRepository.getByCodigoEstadoProveedor("RHM");//rechazado proveedor
            p.setIdEstadoProveedor(estadoProveedor);
            p.setFechaModificacion(DateUtils.getCurrentTimestamp());
            this.proveedorRepository.save(p);

            return Optional.ofNullable(this.homologacionService.homologarRechazoProveedor(p,motivo))
                    .map(l -> new ResponseEntity<>(l, HttpStatus.OK))
                    .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));

        } catch (Exception e) {
            String error = Utils.obtieneMensajeErrorException(e);
            throw new RuntimeException(error);
        }
    }


    @RequestMapping(value = "/respuestas/{email}",
            method = RequestMethod.POST, produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<ProveedorHomologacion>> respuestasProveedor(HttpServletRequest request,
                                                                           @PathVariable("email") String email) throws Exception {
        Proveedor proveedor = proveedorRepository.getProveedorByEmail(email);
        List<ProveedorHomologacion> proveedorHomologacions = null;
        if (proveedor.getIdProveedor() != null){
            proveedorHomologacions = homologacionService.getListRespuestasProveedor(proveedor.getIdProveedor());

        }else{
            throw  new PortalException("proveedor no encontrado");
        }
        return Optional.ofNullable(proveedorHomologacions)
                .map(l -> new ResponseEntity<>(l, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));

    }

}
