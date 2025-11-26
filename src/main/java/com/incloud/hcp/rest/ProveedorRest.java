package com.incloud.hcp.rest;

import com.documents4j.api.DocumentType;
import com.documents4j.api.IConverter;
import com.documents4j.job.LocalConverter;
import com.incloud.hcp.bean.*;
import com.incloud.hcp.domain.*;
import com.incloud.hcp.dto.*;
import com.incloud.hcp.enums.EstadoProveedorEnum;
import com.incloud.hcp.exception.PortalException;
import com.incloud.hcp.facade.PreRegistroFacade;
import com.incloud.hcp.jco.consultaProveedor.service.JCOConsultaProveedorService;
import com.incloud.hcp.repository.*;
import com.incloud.hcp.rest._framework.AppRest;
import com.incloud.hcp.rest.bean.ProveedorDatosGeneralesDTO;
import com.incloud.hcp.service.BancoService;
import com.incloud.hcp.service.PreRegistroProveedorService;
import com.incloud.hcp.service.ProveedorService;
import com.incloud.hcp.service.UbigeoService;
import com.incloud.hcp.service.cmiscf.CmisBaseService;
import com.incloud.hcp.service.cmiscf.bean.CmisFile;
import com.incloud.hcp.util.BASE64DecodedMultipartFile;
import com.incloud.hcp.util.DateUtils;
import com.incloud.hcp.util.StrUtils;
import com.incloud.hcp.util.Utils;
import com.incloud.hcp.wsdl.inside.InSiteResponse;
import com.incloud.hcp.wsdl.inside.InSiteService;
import com.itextpdf.html2pdf.attach.ITagWorker;
import com.itextpdf.html2pdf.attach.ProcessorContext;
import com.itextpdf.html2pdf.attach.impl.DefaultTagWorkerFactory;
import com.itextpdf.html2pdf.attach.impl.tags.HtmlTagWorker;
import com.itextpdf.html2pdf.html.TagConstants;


import com.itextpdf.styledxmlparser.node.IElementNode;
import io.swagger.annotations.ApiOperation;
//import org.apache.poi.xwpf.converter.pdf.PdfConverter;
//import org.apache.poi.xwpf.converter.pdf.PdfOptions;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.itextpdf.text.Document;
import com.itextpdf.tool.xml.XMLWorkerHelper;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Created by Administrador on 29/08/2017.
 */
@RestController
@RequestMapping(value = "/api/proveedor")
public class ProveedorRest extends AppRest {

    private final int NRO_EJECUCIONES = 10;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CmisBaseService cmisBaseServicecf;

    @Autowired
    private InSiteService inSiteService;

    @Autowired
    private ProveedorRepository proveedorRepository;

    @Autowired
    private ProveedorService proveedorService;

    @Autowired
    private PreRegistroProveedorService preRegistroProveedorService;

    @Autowired
    private ProveedorAdjuntoSunatRepository proveedorAdjuntoSunatRepository;
    @Autowired
    private JCOConsultaProveedorService jcoConsultaProveedorService;

    @Autowired
    private TipoProveedorRepository tipoProveedorRepository;

    @Autowired
    private UbigeoService ubigeoService;

    @Autowired
    private BancoService bancoService;

    @Autowired
    private PreRegistroFacade preRegistroFacade;

    @Autowired
    private EstadoProveedorRepository estadoProveedorRepository;

    @Autowired
    private BancoRepository bancoRepository;

    @Autowired
    private RestTemplate restTemplateAuth;

    @RequestMapping(value = "/saveProveedorSHana",
            method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<String> saveProveedorSHana() throws PortalException {

        try {
           this.proveedorService.saveProveedorSHana();
            return new ResponseEntity<>("OK", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/sap",
            method = RequestMethod.POST, produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> enviarInformacionASap(@RequestBody Map<String, Integer> json) throws PortalException {
        logger.error("Ingresando enviarInformacionASap");
        Integer idProveedor = json.get("idProveedor");
        ProveedorDto dto = proveedorService.sendToSap(idProveedor);
        logger.error("finalizando enviarInformacionASap: " + dto);
        return this.processObject(dto);
    }

    @RequestMapping(value = "/get", // default GET ""
            method = RequestMethod.POST, produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Map> getProveedor(HttpServletRequest request, @RequestBody UserSessionFront userFront) throws PortalException {
        logger.debug("[rest] /proveedor");
        logger.error("Ingresando GET PROVEEDOR");

        UserSession userSession = this.getUserSession(userFront);
        if (!Optional.ofNullable(userSession.getRuc()).isPresent()) {
            return this.processObject(new ProveedorDto());
        }
        Optional<ProveedorDto> oProveedor = Optional.ofNullable(this.proveedorService.getProveedorDtoByRuc(userSession.getRuc()));

        if (oProveedor.isPresent()) {
            oProveedor.get().setIdHcp(userSession.getId());
            //Actualizar Datos del Ubigeo para los Migrados
            if (oProveedor.get().getIdEstadoProveedor().getCodigoEstadoProveedor().equals(EstadoProveedorEnum.MIGRADO_DE_SAP.getCodigo())) {
                logger.error("INGRESANDO MIGRADO");
                Optional<PreRegistroProveedorDto> preRegistro = Optional.ofNullable(preRegistroFacade.getPreRegistroByIdHcp(userSession.getId()));
                //PreRegistroProveedorDto preRegistro = preRegistroFacade.getPreRegistroByIdHcp(userSession.getId());
                if (preRegistro.isPresent()) {
                    logger.error("EXISTE EN PRE REGISTRO");
                    // ProveedorDto temp = new ProveedorDto();
                    //BeanUtils.copyProperties(preRegistro, temp);
                    oProveedor.get().setDireccionFiscal(preRegistro.get().getDireccion());
                    oProveedor.get().setActivo(preRegistro.get().getActivo());
                    oProveedor.get().setHabido(preRegistro.get().getHabido());
                    oProveedor.get().setLineasComercial(preRegistro.get().getLineasComercial());
                    oProveedor.get().setCodigoSistemaEmisionElect(preRegistro.get().getCodigoSistemaEmisionElect());
                    oProveedor.get().setCodigoComprobantePago(preRegistro.get().getCodigoComprobantePago());
                    oProveedor.get().setCodigoPadron(preRegistro.get().getCodigoPadron());
                    oProveedor.get().setFechaInicioActiSunat(preRegistro.get().getFechaInicioActiSunat());

                    Optional.ofNullable(preRegistro.get().getIdTipoProveedor())
                            .map(id -> tipoProveedorRepository.getOne(id))
                            .ifPresent(tipoProveedor -> {
                                if (tipoProveedor.getDescripcion().toUpperCase().equals("NACIONAL")) {
                                    Optional.ofNullable(this.ubigeoService.getUbigeoByCodigo("PE"))
                                            .ifPresent(ubigeo -> oProveedor.get().setCodigoPais(ubigeo.getIdUbigeo()));
                                    if (preRegistro.get().getUbigeo().length() > 6) {
                                        Integer nUbigeo = new Integer(preRegistro.get().getUbigeo());
                                        String sUbigeo = nUbigeo.toString();
                                        preRegistro.get().setUbigeo(sUbigeo);
                                    }
                                    Optional.ofNullable(preRegistro.get().getUbigeo())
                                            .filter(u -> !u.isEmpty())
                                            .filter(u -> u.length() == 6)
                                            .ifPresent(codigo -> {
                                                Optional.ofNullable(this.ubigeoService.getUbigeoByCodigo(codigo))
                                                        .ifPresent(ubigeo -> oProveedor.get().setCodigoDistrito(ubigeo.getIdUbigeo()));

                                                Optional.ofNullable(this.ubigeoService.getUbigeoByCodigo(codigo.substring(0, 4)))
                                                        .ifPresent(ubigeo -> oProveedor.get().setCodigoProvincia(ubigeo.getIdUbigeo()));

                                                Optional.ofNullable(this.ubigeoService.getUbigeoByCodigo(codigo.substring(0, 2)))
                                                        .ifPresent(ubigeo -> oProveedor.get().setCodigoRegion(ubigeo.getIdUbigeo()));
                                            });
                                }
                            });

                }

                this.obtenerDatosSunat(userSession, oProveedor);
            }
            return this.processObject(oProveedor.get());
        } else {
            PreRegistroProveedorDto preRegistro = preRegistroFacade.getPreRegistroByIdHcp(userSession.getId());
            ProveedorDto temp = new ProveedorDto();
            EstadoProveedor estadoProveedor = this.estadoProveedorRepository.
                    getByCodigoEstadoProveedor(EstadoProveedorEnum.REGISTRADO.getCodigo());
            temp.setIdEstadoProveedor(estadoProveedor);


            BeanUtils.copyProperties(preRegistro, temp);
            temp.setDireccionFiscal(preRegistro.getDireccion());
            temp.setCelular(preRegistro.getTelefono());
            temp.setTipoPersona("J");
            temp.setEvaluacionHomologacion(new BigDecimal(0.0));


            Optional.ofNullable(preRegistro.getIdTipoProveedor())
                    .map(id -> tipoProveedorRepository.getOne(id))
                    .ifPresent(tipoProveedor -> {
                        if (tipoProveedor.getDescripcion().toUpperCase().equals("NACIONAL")) {
                            Optional.ofNullable(this.ubigeoService.getUbigeoByCodigo("PE"))
                                    .ifPresent(ubigeo -> temp.setCodigoPais(ubigeo.getIdUbigeo()));

                            Optional.ofNullable(preRegistro.getUbigeo())
                                    .filter(u -> !u.isEmpty())
                                    .filter(u -> u.length() == 6)
                                    .ifPresent(codigo -> {
                                        Optional.ofNullable(this.ubigeoService.getUbigeoByCodigo(codigo))
                                                .ifPresent(ubigeo -> temp.setCodigoDistrito(ubigeo.getIdUbigeo()));

                                        Optional.ofNullable(this.ubigeoService.getUbigeoByCodigo(codigo.substring(0, 4)))
                                                .ifPresent(ubigeo -> temp.setCodigoProvincia(ubigeo.getIdUbigeo()));

                                        Optional.ofNullable(this.ubigeoService.getUbigeoByCodigo(codigo.substring(0, 2)))
                                                .ifPresent(ubigeo -> temp.setCodigoRegion(ubigeo.getIdUbigeo()));
                                    });
                        }
                    });

            this.obtenerDatosSunat(userSession, temp);
            return this.processObject(temp);
        }

    }

    @RequestMapping(value = "/dummy",
            method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Map> getProveedorDummy(HttpServletRequest request) throws PortalException {
        logger.debug("[rest] /proveedor");
        logger.error("Ingresando GET PROVEEDOR");
        return this.processObject(new ProveedorDto());
    }


    @RequestMapping(value = "/devuelveProveedor",
            method = RequestMethod.POST, produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Map> devuelveProveedor(HttpServletRequest request, @RequestBody UserSessionFront userFront) throws PortalException {
        logger.debug("[rest] /proveedor");

        UserSession userSession = this.getUserSession(userFront);
        if (!Optional.ofNullable(userSession.getRuc()).isPresent()) {
            return this.processObject(new ProveedorDto());
        }

        Optional<ProveedorDto> oProveedor = Optional.ofNullable(this.proveedorService.getProveedorDtoByRuc(userSession.getRuc()));
        if (oProveedor.isPresent()) {
            oProveedor.get().setIdHcp(userSession.getId());
            this.obtenerDatosSunat(userSession, oProveedor);
            return this.processObject(oProveedor.get());
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/devuelveProveedor/{EmailProveedor}",
            method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<ProveedorCustom>> devuelveProveedor(
            @PathVariable("EmailProveedor") String emailProveedor, HttpServletRequest request) throws PortalException {

        HttpHeaders headers = new HttpHeaders();

        List<ProveedorCustom> lista = this.proveedorService.devuelveProveedor(emailProveedor);
        return Optional.ofNullable(lista).map(l -> new ResponseEntity<>(l, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));

        //return this.processObject(this.proveedorService.devuelveProveedor(rucProveedor));

    }

    @RequestMapping(value = "/devuelveProveedorCotizacion/{EmailProveedor}",
            method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<ProveedorCustom>> devuelveProveedorCotizacion(
            @PathVariable("EmailProveedor") String emailProveedor, HttpServletRequest request) throws PortalException {

        HttpHeaders headers = new HttpHeaders();
        List<ProveedorCustom> lista = null;
        Proveedor proveedor = this.proveedorRepository.getProveedorByEmail(emailProveedor);
        if (proveedor != null) {
            String estado = proveedor.getIdEstadoProveedor().getCodigoEstadoProveedor();
            if (estado.equalsIgnoreCase("HOM")) {
                logger.error("Proveedor valido");
                lista = this.proveedorService.devuelveProveedor(emailProveedor);
                return Optional.ofNullable(lista).map(l -> new ResponseEntity<>(l, HttpStatus.OK))
                        .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
            } else {
                logger.error("No cuenta con permisos para ingresar al módulo");
                throw new PortalException("No cuenta con permisos para ingresar al módulo");
            }
        }
        //return this.processObject(this.proveedorService.devuelveProveedor(rucProveedor));

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);

    }

    @RequestMapping(value = "/devuelveProveedorNew",
            method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Map> devuelveProveedorNew(HttpServletRequest request) throws PortalException {
        logger.debug("[rest] /proveedor");

        UserSession userSession = this.getUserSession();
        if (!Optional.ofNullable(userSession.getRuc()).isPresent()) {
            return this.processObject(new ProveedorDto());
        }

        Optional<ProveedorDto> oProveedor = Optional.ofNullable(this.proveedorService.getProveedorDtoByRuc(userSession.getRuc()));
        if (oProveedor.isPresent()) {
            oProveedor.get().setIdHcp(userSession.getId());
            this.obtenerDatosSunat(userSession, oProveedor);
            return this.processObject(oProveedor.get());
        } else {
            ProveedorDto proveedorDto = new ProveedorDto();
            this.obtenerDatosSunat(userSession, proveedorDto);
            return this.processObject(proveedorDto);
        }
    }

    @RequestMapping(value = "/devuelveProveedorResponder",
            method = RequestMethod.POST, produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Map> devuelveProveedorResponder(HttpServletRequest request, @RequestBody UserSessionFront userFront) throws PortalException {
        logger.debug("[rest] /devuelveProveedorResponder");

        UserSession userSession = this.getUserSession(userFront);
        if (!Optional.ofNullable(userSession.getUserName()).isPresent()) {
            return this.processObject(new ProveedorDto());
        }

        Optional<ProveedorDto> oProveedor = Optional.ofNullable(this.proveedorService.getProveedorDtoByEmailResponder(userSession.getUserName()));
        if (oProveedor.isPresent()) {
            oProveedor.get().setIdHcp(userSession.getId());
            userSession.setRuc(oProveedor.get().getRuc());
            this.obtenerDatosSunat(userSession, oProveedor);
            return this.processObject(oProveedor.get());
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private void obtenerDatosSunat(UserSession userSession, Optional<ProveedorDto> oProveedorDto) {
        InSiteResponse response = null;
        for (int contador = 0; contador < NRO_EJECUCIONES; contador++) {
            try {
                response = inSiteService.getConsultaRuc(userSession.getRuc());
                break;
            } catch (Exception e) {
                if (contador == NRO_EJECUCIONES - 1) {
                }
            }
        }
        if (Optional.ofNullable(response).isPresent()) {
            oProveedorDto.get().setActivo(response.getEstado());
            oProveedorDto.get().setHabido(response.getCondicion());
            oProveedorDto.get().setCodigoPadron(response.getCodigoPadron());
            oProveedorDto.get().setCodigoComprobantePago(response.getCodigoComprobantePago());
            oProveedorDto.get().setFechaInicioActiSunat(response.getFechaInicioActiSunat());
            oProveedorDto.get().setCodigoActividadEconomica(response.getActividadEconomica());
        }

    }

    private void obtenerDatosSunat(UserSession userSession, ProveedorDto proveedorDto) {
        InSiteResponse response = null;
        for (int contador = 0; contador < NRO_EJECUCIONES; contador++) {
            try {
                response = inSiteService.getConsultaRuc(userSession.getRuc());
                break;
            } catch (Exception e) {
                if (contador == NRO_EJECUCIONES - 1) {
                }
            }
        }
        if (Optional.ofNullable(response).isPresent()) {
            proveedorDto.setActivo(response.getEstado());
            proveedorDto.setHabido(response.getCondicion());
            proveedorDto.setCodigoPadron(response.getCodigoPadron());
            proveedorDto.setCodigoComprobantePago(response.getCodigoComprobantePago());
            proveedorDto.setFechaInicioActiSunat(response.getFechaInicioActiSunat());
            proveedorDto.setCodigoActividadEconomica(response.getActividadEconomica());
        }

    }

    @RequestMapping(value = "/{idProveedor}",
            method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Map> getProveedorByIdProveedor(@PathVariable("idProveedor") Integer idProveedor) throws PortalException {
        logger.error("Ingresando getProveedorByIdProveedor 00");
        return this.processObject(proveedorService.getProveedorDtoById(idProveedor));
    }

    @RequestMapping(value = "/{idProveedor}/lineas-comerciales",
            method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> getListLineaComercialByIdProveedor(@PathVariable("idProveedor") Integer idProveedor) throws PortalException {
        return this.processList(proveedorService.getListLineaComercialByIdProveedor(idProveedor));
    }

    @RequestMapping(value = "/ruc/{rucProveedor}",
            method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> getProveedorByRucProveedor(@PathVariable("rucProveedor") String ruc) throws PortalException {
        return this.processList(this.proveedorService.getListProveedorByRuc(ruc));
    }


    @RequestMapping(value = "/obtenerProveedor/{email}",
            method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> obtenerProveedorByRuc(@PathVariable("email") String email) throws PortalException {
        Proveedor proveedor = this.proveedorRepository.getProveedorByEmail(email);
        if (proveedor != null) {
            logger.error("Proveedor valido");
            return ResponseEntity.ok().body(proveedor);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "/obtenerProveedorCotizacion/{email}",
            method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> obtenerProveedorByEmailCotizacion(@PathVariable("email") String email) throws PortalException {
        Proveedor proveedor = this.proveedorRepository.getProveedorByEmail(email);
        if (proveedor != null) {
            String estado = proveedor.getIdEstadoProveedor().getCodigoEstadoProveedor();
            if (estado.equalsIgnoreCase("HOM")) {
                logger.error("Proveedor valido");
                return ResponseEntity.ok().body(proveedor);
            } else {
                logger.error("No cuenta con permisos para ingresar al módulo");
                throw new PortalException("No cuenta con permisos para ingresar al módulo");
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


    @RequestMapping(value = "/filtro", method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<?> devuelveListaLicitacionByFiltro(@RequestBody ProveedorFiltro filtro) {
        return this.processList(this.proveedorService.getListProveedorByFiltro(filtro));
    }

    @RequestMapping(value = "/filtroPaginado", method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<?> devuelveListaLicitacionByFiltroPaginado(@RequestBody ProveedorFiltro filtro) {
        return this.processList(this.proveedorService.getListProveedorByFiltroPaginado(filtro));
    }

    @RequestMapping(value = "/filtroDataMaestra", method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<?> filtroDataMaestra(@RequestBody ProveedorFiltro filtro) {
        filtro.setEstadoProveedor(EstadoProveedorEnum.PENDIENTE_EVALUACION_MAESTRA.getCodigo());
        return this.processList(this.proveedorService.getListProveedorByFiltro(filtro));
    }

    @RequestMapping(value = "/filtroValidacion", method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<?> filtroValidacion(@RequestBody FiltroValidacionTercera filtro) {
        UserSession userSession = this.getUserSession(filtro.getUserSessionFront());
        return this.processList(this.proveedorService.getListProveedorByFiltroValidacion(userSession, filtro.getProveedorFiltro()));
    }

    @RequestMapping(value = "/filtroLicitacion", method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<?> filtroLicitacion(@RequestBody ProveedorFiltro filtro) {
        UserSession userSession = this.getUserSession();
        return this.processList(this.proveedorService.getListProveedorByFiltroLicitacion(filtro));
    }

    @RequestMapping(value = "/filtroLicitacionPaginado", method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<?> filtroLicitacionPaginado(@RequestBody ProveedorFiltro filtro) {
        UserSession userSession = this.getUserSession();
        return this.processList(this.proveedorService.getListProveedorByFiltroLicitacionPaginado(filtro));
    }


    @RequestMapping(value = "/validarDataMaestra/{idProveedor}",
            method = RequestMethod.PATCH, produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> validarDataMaestra(@PathVariable("idProveedor") Integer idProveedor) throws PortalException {
        this.proveedorService.evaluarDataMaestra(idProveedor);
        return new ResponseEntity<>("OK", HttpStatus.OK);
    }

    @RequestMapping(value = "/rechazarDataMaestra/{idProveedor}",
            method = RequestMethod.PATCH, produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> rechazarDataMaestra(@PathVariable("idProveedor") Integer idProveedor, @RequestBody String motivo) throws PortalException {
        this.proveedorService.rechazarDataMaestra(idProveedor, motivo);


        return new ResponseEntity<>("OK", HttpStatus.OK);
    }


    @ApiOperation(value = "Elimina todos los datos del proveedor", produces = "application/json")
    @DeleteMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> eliminaDatosProveedor(@PathVariable Integer id) throws URISyntaxException {
        logger.debug("Delete by id eliminaDatosProveedor : {}", id);
        try {
            this.proveedorService.eliminarDatosProveedor(id);
            return ResponseEntity.ok().build();
        } catch (Exception x) {
            // todo: dig exception, most likely
            String error = Utils.obtieneMensajeErrorException(x);
            throw new RuntimeException(error);
        }
    }

    @RequestMapping(value = "",
            method = RequestMethod.POST, produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Map> save(@RequestBody ProveedorDto proveedorDto,
                                    BindingResult result) {
        logger.error("Ingresando GRABAR PROVEEDOR: " + proveedorDto);

        try {
            return this.processObject(proveedorService.saveDto(proveedorDto));
        } catch (Exception ex) {
            ex.printStackTrace();
            PortalException pex = new PortalException(Utils.obtieneMensajeErrorException(ex));
            throw pex;
        }
    }


    @RequestMapping(value = "/id/{idProveedor}",
            method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Proveedor> getProveedorById(@PathVariable("idProveedor") Integer idProveedor) throws PortalException {
        Proveedor proveedor = this.proveedorRepository.getOne(idProveedor);
        return ResponseEntity.ok().body(proveedor);
    }

    @RequestMapping(value = "/getProveedorDatosGenerales",
            method = RequestMethod.POST, produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<ProveedorDatosGeneralesDTO>> getProveedorDatosGenerales(
            @RequestBody Map<String, Object> json) throws PortalException {
        String fechaCreacionIni = (String) json.get("fechaCreacionIni");
        String fechaCreacionFin = (String) json.get("fechaCreacionFin");
        List<ProveedorDatosGeneralesDTO> listaProveedorDatosGenerales = this.proveedorService.getProveedorDatosGenerales(fechaCreacionIni, fechaCreacionFin);

        return ResponseEntity.ok().body(listaProveedorDatosGenerales);
    }

    @RequestMapping(value = "/guardarAdjuntoSunat/{idProveedor}/{listAdjunto}",
            method = RequestMethod.POST, produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<ProveedorAdjuntoSunat>> guardarAdjuntoSunat(
            @PathVariable("idProveedor") Integer idProveedor, @PathVariable("listAdjunto") List<ProveedorAdjuntoSunatDto> listAdjunto) throws Exception {
        List<ProveedorAdjuntoSunat> proveedorAdjuntoSunats = this.proveedorService.guardarAdjuntoSunat(idProveedor, listAdjunto, "");
        System.out.println(proveedorAdjuntoSunats);
        return ResponseEntity.ok().body(proveedorAdjuntoSunats);
    }

    @RequestMapping(value = "/eliminarAdjunto/{idProveedor}/{idAdjunto}",
            method = RequestMethod.POST, produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<ProveedorAdjuntoSunat>> eliminarAdjunto(
            @PathVariable("idProveedor") Integer idProveedor, @PathVariable("idAdjunto") String idAdjunto) throws Exception {
        List<ProveedorAdjuntoSunat> proveedorAdjuntoSunats = this.proveedorService.eliminarAdjunto(idProveedor, idAdjunto);
        return ResponseEntity.ok().body(proveedorAdjuntoSunats);
    }

    @RequestMapping(value = "/eliminarAdjuntoCatalogo/{idProveedor}/{idAdjunto}",
            method = RequestMethod.POST, produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<ProveedorCatalogo>> eliminarAdjuntoCatalogo(
            @PathVariable("idProveedor") Integer idProveedor, @PathVariable("idAdjunto") String idAdjunto) throws Exception {
        List<ProveedorCatalogo> proveedorAdjuntoCatalogo = this.proveedorService.eliminarAdjuntoCatalogo(idProveedor, idAdjunto);
        return ResponseEntity.ok().body(proveedorAdjuntoCatalogo);
    }

    @RequestMapping(value = "/obtenerAdjuntos/{idProveedor}",
            method = RequestMethod.POST, produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<ProveedorAdjuntoSunat>> obtenerAdjuntos(
            @PathVariable("idProveedor") Integer idProveedor) throws Exception {
        List<ProveedorAdjuntoSunat> proveedorAdjuntoSunats = this.proveedorAdjuntoSunatRepository.getProveedorAdjuntoSunatByIdProveedor(idProveedor);
        return ResponseEntity.ok().body(proveedorAdjuntoSunats);
    }

    @RequestMapping(value = "/actualizarAdjuntos",
            method = RequestMethod.POST, produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<ProveedorAdjuntoSunat>> actualizarAdjuntos(
            @RequestBody ProveedorAdjuntoSunat idAdjunto) throws Exception {
        ProveedorAdjuntoSunat proveedorAdjuntoSunats = this.proveedorAdjuntoSunatRepository.save(idAdjunto);
        List<ProveedorAdjuntoSunat> proveedorAdjuntoSunats1 = this.proveedorAdjuntoSunatRepository.getProveedorAdjuntoSunatByIdProveedor(idAdjunto.getIdProveedor().getIdProveedor());

        return ResponseEntity.ok().body(proveedorAdjuntoSunats1);
    }

    @RequestMapping(value = "/listaProveedorSinHCPID",
            method = RequestMethod.POST, produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<ProveedorCustom>> getListaProveedoresSinHcpID() throws PortalException {

        List<ProveedorCustom> listaProveedores = this.proveedorService.getListProveedorSinHcpID();

        return ResponseEntity.ok().body(listaProveedores);
    }

    @RequestMapping(value = "/actualizarIDHCPProveedor", method = RequestMethod.PUT, headers = "Accept=application/json")
    public ResponseEntity<Integer> devuelveCountByEstadoLicitacion(@RequestBody ListProveedorHCP listProveedorHCP) {
        Integer nroProveedoresActualizados = this.proveedorService.updateProveedorIDHCP(listProveedorHCP);
        return ResponseEntity.ok().body(nroProveedoresActualizados);
    }


    @RequestMapping(value = "/actualizarEmailProveedor",
            method = RequestMethod.POST, produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<String> saveEmail(@RequestParam(value = "ruc") String ruc,
                                            @RequestParam(value = "email") String email,
                                            BindingResult result) {
        logger.error("Ingresando MODIFICAR EMAIL PROVEEDOR: RUC = " + ruc + ", nuevoEmail = " + email);
        try {
            String respuesta = proveedorService.saveEmail(ruc, email);
            logger.error("Respuesta MODIFICAR EMAIL PROVEEDOR: " + respuesta);
            return new ResponseEntity<>(respuesta, HttpStatus.OK);
        } catch (Exception ex) {
            String error = StrUtils.obtieneMensajeErrorExceptionCustom(ex);
            logger.error("Error MODIFICAR EMAIL PROVEEDOR: " + error);
            throw new PortalException(error);
        }
    }

    @PostMapping("/xlsx/file/")
    public ResponseEntity<List<ProveedorXLSXDTO>> uploadExcel(@RequestParam("file") MultipartFile file) {
        try {
            InputStream in = file.getInputStream();
            List<ProveedorXLSXDTO> result = this.proveedorService.uploadExcel(in);
            for (ProveedorXLSXDTO beanUpload : result) {
                if (beanUpload.isError()) {
                    continue;
                }
                ProveedorDto bean = beanUpload.getProveedor();
                try {
                    logger.error("Ingresando bean :" + bean.toString());
                    this.jcoConsultaProveedorService.listaProveedorByRFC(bean.getAcreedorCodigoSap(), "", "", bean.getEmail(), bean.getTipoPersona());
                    beanUpload.setError(false);
                    beanUpload.setMensaje("SE AGREGO EL PROVEEDOR A LA BD " + bean.getAcreedorCodigoSap());
                    beanUpload.setProveedor(null);
                } catch (Exception e) {
                    beanUpload.setError(true);
                    beanUpload.setMensaje(Utils.obtieneMensajeErrorException(e));

                    logger.error("Ingresando bean error uploadExcel:" + e.getMessage());
                    e.printStackTrace();
                }
            }
            return Optional.of(result).map(l -> new ResponseEntity<>(l, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NO_CONTENT));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @RequestMapping(value = "/get-proveedor/{email}",
            method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Map> getProveedor(@PathVariable("email") String email) throws PortalException {
        logger.debug("[rest] /proveedor");
        logger.error("Ingresando GET PROVEEDOR");

        UserSession userSession = this.getUserSession();
//        if (!Optional.ofNullable(userSession.getRuc()).isPresent()) {
//            return this.processObject(new ProveedorDto());
//        }
        logger.error("Ingresando GET PROVEEDOR 01");
        Optional<ProveedorDto> oProveedor;
        try {
            oProveedor = Optional.ofNullable(this.proveedorService.getProveedorDtoByEmailResponder(email));
            logger.error("Ingresando GET PROVEEDOR 02");

        } catch (Exception e) {
            e.printStackTrace();
            throw new PortalException(e.getMessage());

        }

        if (oProveedor.isPresent()) {
            logger.error("Ingresando GET PROVEEDOR 03");
            oProveedor.get().setIdHcp(userSession.getId());
            //Actualizar Datos del Ubigeo para los Migrados
            if (oProveedor.get().getIdEstadoProveedor().getCodigoEstadoProveedor().equals(EstadoProveedorEnum.MIGRADO_DE_SAP.getCodigo())) {
                logger.error("Ingresando GET PROVEEDOR 04");
                Optional<PreRegistroProveedorDto> preRegistro = Optional.ofNullable(preRegistroFacade.getPreRegistroByEmail(email));
                //PreRegistroProveedorDto preRegistro = preRegistroFacade.getPreRegistroByIdHcp(userSession.getId());
                if (preRegistro.isPresent()) {
                    logger.error("Ingresando GET PROVEEDOR 05");
                    // ProveedorDto temp = new ProveedorDto();
                    //BeanUtils.copyProperties(preRegistro, temp);
                    oProveedor.get().setDireccionFiscal(preRegistro.get().getDireccion());
                    oProveedor.get().setActivo(preRegistro.get().getActivo());
                    oProveedor.get().setHabido(preRegistro.get().getHabido());
                    oProveedor.get().setLineasComercial(preRegistro.get().getLineasComercial());
                    oProveedor.get().setCodigoSistemaEmisionElect(preRegistro.get().getCodigoSistemaEmisionElect());
                    oProveedor.get().setCodigoComprobantePago(preRegistro.get().getCodigoComprobantePago());
                    oProveedor.get().setCodigoPadron(preRegistro.get().getCodigoPadron());
                    oProveedor.get().setFechaInicioActiSunat(preRegistro.get().getFechaInicioActiSunat());


                    Optional.ofNullable(preRegistro.get().getIdTipoProveedor())
                            .map(id -> tipoProveedorRepository.getOne(id))
                            .ifPresent(tipoProveedor -> {
                                if (tipoProveedor.getDescripcion().toUpperCase().equals("NACIONAL")) {
                                    Optional.ofNullable(this.ubigeoService.getUbigeoByCodigo("PE"))
                                            .ifPresent(ubigeo -> oProveedor.get().setCodigoPais(ubigeo.getIdUbigeo()));
                                    if (preRegistro.get().getUbigeo().length() > 6) {
                                        Integer nUbigeo = new Integer(preRegistro.get().getUbigeo());
                                        String sUbigeo = nUbigeo.toString();
                                        preRegistro.get().setUbigeo(sUbigeo);
                                    }
                                    Optional.ofNullable(preRegistro.get().getUbigeo())
                                            .filter(u -> !u.isEmpty())
                                            .filter(u -> u.length() == 6)
                                            .ifPresent(codigo -> {
                                                Optional.ofNullable(this.ubigeoService.getUbigeoByCodigo(codigo))
                                                        .ifPresent(ubigeo -> oProveedor.get().setCodigoDistrito(ubigeo.getIdUbigeo()));

                                                Optional.ofNullable(this.ubigeoService.getUbigeoByCodigo(codigo.substring(0, 4)))
                                                        .ifPresent(ubigeo -> oProveedor.get().setCodigoProvincia(ubigeo.getIdUbigeo()));

                                                Optional.ofNullable(this.ubigeoService.getUbigeoByCodigo(codigo.substring(0, 2)))
                                                        .ifPresent(ubigeo -> oProveedor.get().setCodigoRegion(ubigeo.getIdUbigeo()));
                                            });
                                }
                            });

                }

            }
            logger.error("Ingresando GET PROVEEDOR 06");
            ProveedorDto proveedorValidar = oProveedor.get();
            try {
                ProveedorDto nproveedorDto = this.obtenerDatosSunatValidar(proveedorValidar.getRuc(), oProveedor.get());
                logger.error("Ingresando GET PROVEEDOR FIN TABLA proveedor: " + nproveedorDto.toString());
                return this.processObject(nproveedorDto);
            } catch (Exception ex) {
                return this.processObject(proveedorValidar);
            }


        } else {
            logger.error("Ingresando GET PROVEEDOR 07");
            PreRegistroProveedorDto preRegistro = preRegistroFacade.getPreRegistroByEmail(email);
            ProveedorDto temp = new ProveedorDto();
            EstadoProveedor estadoProveedor = this.estadoProveedorRepository.
                    getByCodigoEstadoProveedor(EstadoProveedorEnum.REGISTRADO.getCodigo());
            temp.setIdEstadoProveedor(estadoProveedor);

            logger.error("Ingresando GET PROVEEDOR 08");
            BeanUtils.copyProperties(preRegistro, temp);
            temp.setDireccionFiscal(preRegistro.getDireccion());
            temp.setCelular(preRegistro.getTelefono());
            temp.setTipoPersona("J");
            temp.setEvaluacionHomologacion(new BigDecimal(0.0));
            logger.error("Ingresando GET PROVEEDOR 09");
            Optional.ofNullable(preRegistro.getIdTipoProveedor())
                    .map(id -> tipoProveedorRepository.getOne(id))
                    .ifPresent(tipoProveedor -> {
                        if (tipoProveedor.getDescripcion().toUpperCase().equals("NACIONAL")) {
                            Optional.ofNullable(this.ubigeoService.getUbigeoByCodigo("PE"))
                                    .ifPresent(ubigeo -> temp.setCodigoPais(ubigeo.getIdUbigeo()));
                            if (preRegistro.getUbigeo().length() > 6) {
                                Integer nUbigeo = new Integer(preRegistro.getUbigeo());
                                String sUbigeo = nUbigeo.toString();
                                preRegistro.setUbigeo(sUbigeo);
                            }
                            Optional.ofNullable(preRegistro.getUbigeo())
                                    .filter(u -> !u.isEmpty())
                                    .filter(u -> u.length() == 6)
                                    .ifPresent(codigo -> {
                                        Optional.ofNullable(this.ubigeoService.getUbigeoByCodigo(codigo))
                                                .ifPresent(ubigeo -> temp.setCodigoDistrito(ubigeo.getIdUbigeo()));

                                        Optional.ofNullable(this.ubigeoService.getUbigeoByCodigo(codigo.substring(0, 4)))
                                                .ifPresent(ubigeo -> temp.setCodigoProvincia(ubigeo.getIdUbigeo()));

                                        Optional.ofNullable(this.ubigeoService.getUbigeoByCodigo(codigo.substring(0, 2)))
                                                .ifPresent(ubigeo -> temp.setCodigoRegion(ubigeo.getIdUbigeo()));
                                    });
                        }
                    });
            logger.error("Ingresando GET PROVEEDOR 10");
            try {
                ProveedorDto temp02 = this.obtenerDatosSunatValidar(temp.getRuc(), temp);
                logger.error("Ingresando GET PROVEEDOR FIN TABLA preproveedor: " + temp02.toString());
                return this.processObject(temp02);
            } catch (Exception ex) {
                return this.processObject(temp);
            }


        }

    }


    private ProveedorDto obtenerDatosSunatValidar(String ruc, ProveedorDto proveedorDto) {
        InSiteResponse response = null;
        for (int contador = 0; contador < NRO_EJECUCIONES; contador++) {
            try {
                response = inSiteService.getConsultaRuc(ruc);
                break;
            } catch (Exception e) {
                if (contador == NRO_EJECUCIONES - 1) {
                }
            }
        }
        logger.error("Ingresando obtenerDatosSunat 01 ProveedorDto: " + response.toString());
        if (Optional.ofNullable(response).isPresent()) {
            logger.error("Ingresando obtenerDatosSunat 01-A INGRESO OK");
            proveedorDto.setActivo(response.getEstado());
            proveedorDto.setHabido(response.getCondicion());
            proveedorDto.setCodigoSistemaEmisionElect(response.getCodigoSistemaEmisionElect());
            proveedorDto.setCodigoPadron(response.getCodigoPadron());
            proveedorDto.setCodigoComprobantePago(response.getCodigoComprobantePago());
            proveedorDto.setFechaInicioActiSunat(response.getFechaInicioActiSunat());
            proveedorDto.setCodigoActividadEconomica(response.getActividadEconomica());

        }
        logger.error("Ingresando obtenerDatosSunat 02 ProveedorDto: " + proveedorDto.toString());
        return proveedorDto;
    }

    @ApiOperation(value = "Servicio  Proveedor Create Update", produces = "application/json")
    @PostMapping(value = "/_inspeccionProveedor", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<ProveedorOutDto> inspeccionProveedor(
            @RequestBody ProveedorInDto bean) throws Exception {
        HttpHeaders headers = new HttpHeaders();


        ProveedorOutDto lista = proveedorService.inspeccionProveedor(bean);
        return Optional.ofNullable(lista).map(l -> new ResponseEntity<>(l, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));


    }

    @ApiOperation(value = "Servicio  Proveedor Create Update", produces = "application/json")
    @PostMapping(value = "/_inspeccionProveedorParcial", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<ProveedorOutDto> inspeccionProveedorParcial(
            @RequestBody ProveedorInDto bean) throws Exception {
        HttpHeaders headers = new HttpHeaders();


        ProveedorOutDto lista = proveedorService.inspeccionProveedorParcial(bean);
        return Optional.ofNullable(lista).map(l -> new ResponseEntity<>(l, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));


    }

    @RequestMapping(value = "/_fileBase64-to-file/upload",
        method = RequestMethod.POST, produces = {
        MediaType.APPLICATION_JSON_VALUE,
        MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<CuentaBancariaDto> fileBase64ToFileUpload(@RequestParam("file") String fileBase64,
                                                                    @RequestParam("fileName") String fileName) throws Exception {
        // EAAR 11/12/2021: DOCUMENT MANAGER TEST Carga de archivo Base64 a MultipartFile

        MultipartFile file =   BASE64DecodedMultipartFile.base64ToMultipart(fileBase64);

        String newFolder = "temp";
        //String folderId = cmisService.createFolder(newFolder);
        //CmisFile cmisFile = cmisService.createDocumento(folderId, file);
        String folderId = cmisBaseServicecf.createFolder(newFolder).getId();
        CmisFile cmisFileCF = cmisBaseServicecf.createDocumento(folderId, file,fileName);
        logger.debug("Archivo cargado al repositorio : " + cmisFileCF);
        CuentaBancariaDto cuentaBancariaDto = new CuentaBancariaDto();
        cuentaBancariaDto.setArchivoId(cmisFileCF.getId());
        cuentaBancariaDto.setArchivoNombre(cmisFileCF.getNameFinal());
        cuentaBancariaDto.setRutaAdjunto(cmisFileCF.getUrl());
        cuentaBancariaDto.setArchivoTipo(cmisFileCF.getType());

        return new ResponseEntity<>(cuentaBancariaDto, HttpStatus.OK);
    }

    @ApiOperation(value = "Servicio  Proveedor Create Update", produces = "application/json")
    @PostMapping(value = "/_inspeccionProveedorList", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<ProveedorDto> insertList(@RequestBody ProveedorDto bean) {
        // EAAR 11/12/2021: DOCUMENT MANAGER
        HttpHeaders headers = new HttpHeaders();

        ProveedorDto response=null;
        try {
          response = proveedorService.inspeccionProveedorCompleto(bean);
        } catch (Exception e) {
            e.printStackTrace();
            throw new PortalException(e.getMessage());
        }

        return Optional.ofNullable(response).map(l -> new ResponseEntity<>(l, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));

    }

    @ApiOperation(value = "Crea Documento en el Repository", produces = "application/json")
    @PostMapping(value = "/uploadAdjuntoCuentaBancaria/{idProveedor}/", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<ProveedorAdjuntoCuentaBancaria> uploadAdjuntoCuentaBancaria(@PathVariable Integer idProveedor, @RequestParam("file") MultipartFile file) {
        try {
            logger.error("paso uno adjuntar");
            ProveedorAdjuntoCuentaBancaria result = this.proveedorService.uploadAdjuntoCuentaBancaria(idProveedor, file);
            return Optional.ofNullable(result).map(l -> new ResponseEntity<>(l, HttpStatus.OK))
                    .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @RequestMapping(value = "/obtenerEstadoCuentaProveedor/{email}",
        method = RequestMethod.GET, produces = {
        MediaType.APPLICATION_JSON_VALUE,
        MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<List<FacturaSapDto>> getListaDetalle(
        @PathVariable("email") String email
        ) throws Exception {

        List<FacturaSapDto> facturaSapDtoList = this.proveedorService.getEstadoCuentaProveedor(email);
        return ResponseEntity.ok().body(facturaSapDtoList);
    }

    @ApiOperation(value = "Crea Documento en el Repository", produces = "application/json")
    @PostMapping(value = "/getAdjuntosCuentaBancariaPorProveedor/{idProveedor}/", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ProveedorAdjuntoCuentaBancaria>> getAdjuntosCuentaBancariaPorProveedor(@PathVariable Integer idProveedor) {
        try {
            List<ProveedorAdjuntoCuentaBancaria> result = this.proveedorService.getListAdjuntoCuentaBancariaByProveedor(idProveedor);
            return Optional.ofNullable(result).map(l -> new ResponseEntity<>(l, HttpStatus.OK))
                    .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @ApiOperation(value = "Servicio para crear PDF PN", produces = "application/json")
    @GetMapping(value = "/pdfPN/{idProveedor}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> pdfPN(@PathVariable("idProveedor") Integer idProveedor, HttpServletResponse response) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        String nombreFile = "proveedor_pn_" + idProveedor;

        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + nombreFile + ".pdf");
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");

        String htmlSource = proveedorService.getTipoDocumento(idProveedor);

        OutputStream file = new FileOutputStream(new File("HTMLtoPDFPN.pdf"));
        com.itextpdf.text.Document document = new com.itextpdf.text.Document();
        document.setMargins(12,12,12,12);
        com.itextpdf.text.pdf.PdfWriter writer = com.itextpdf.text.pdf.PdfWriter.getInstance(document, file);
        document.open();
        InputStream is = new ByteArrayInputStream(htmlSource.toString().getBytes());
        XMLWorkerHelper.getInstance().parseXHtml(writer, document, is);
        document.close();
        file.close();


        Path path = Paths.get("HTMLtoPDFPN.pdf");
        byte[] data = Files.readAllBytes(path);

        //==============================================================
        response.setHeader("Content-Disposition", "attachment; filename=" + nombreFile + ".pdf");
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    public class CustomTagWorkerFactory extends DefaultTagWorkerFactory {
        public ITagWorker getCustomTagWorker(IElementNode tag, ProcessorContext context) {
            if (TagConstants.HTML.equals(tag.name())) {
                return new ZeroMarginHtmlTagWorker(tag, context);
            }
            return null;
        }
    }

    public class ZeroMarginHtmlTagWorker extends HtmlTagWorker {
        public ZeroMarginHtmlTagWorker(IElementNode element, ProcessorContext context) {
            super(element, context);
            Document doc = (Document) getElementResult();
            doc.setMargins(0, 0, 0, 0);
        }
    }

    @ApiOperation(value = "Servicio para crear PDF PJ", produces = "application/json")
    @GetMapping(value = "/pdfPJ/{idProveedor}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> pdfPJ2(@PathVariable("idProveedor") Integer idProveedor, HttpServletResponse response) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        String nombreFile = "proveedor_pj_" + idProveedor;

        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + nombreFile + ".pdf");
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");

        String htmlSource = proveedorService.getTipoDocumentoPJHTML(idProveedor);

        OutputStream file = new FileOutputStream(new File("HTMLtoPDF.pdf"));
        com.itextpdf.text.Document document = new com.itextpdf.text.Document();
        document.setMargins(15,15,15,15);
        com.itextpdf.text.pdf.PdfWriter writer = com.itextpdf.text.pdf.PdfWriter.getInstance(document, file);
        document.open();
        InputStream is = new ByteArrayInputStream(htmlSource.toString().getBytes());
        XMLWorkerHelper.getInstance().parseXHtml(writer, document, is);
        document.close();
        file.close();


        Path path = Paths.get("HTMLtoPDF.pdf");
        byte[] data = Files.readAllBytes(path);

        //==============================================================
        response.setHeader("Content-Disposition", "attachment; filename=" + nombreFile + ".pdf");
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @ApiOperation(value = "Servicio para crear PDF PJ", produces = "application/json")
    @GetMapping(value = "/pdfPJ2/{idProveedor}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> pdfPJ(@PathVariable("idProveedor") Integer idProveedor, HttpServletResponse response) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        String nombreFile = "proveedor_pj_" + idProveedor;

        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + nombreFile + ".pdf");
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");

        XWPFDocument document = proveedorService.getTipoDocumentoPJ(idProveedor);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        document.write(out);
        byte[] xwpfDocumentBytes = out.toByteArray();
        // do something with the byte array
        System.out.println(xwpfDocumentBytes);
        out.close();
        document.close();
        //==============================================================
        //==============================================================
        FileOutputStream File = new FileOutputStream("juridica.docx");
        File.write(out.toByteArray());
        File.close();
        InputStream docxInputStream = new FileInputStream(new File("juridica.docx"));
        OutputStream outputStream = new FileOutputStream(new File("juridica.pdf"));
        IConverter converter = LocalConverter.builder().build();
        converter.convert(docxInputStream).as(DocumentType.DOCX).to(outputStream).as(DocumentType.PDF).execute();
        outputStream.close();

        Path path = Paths.get("juridica.pdf");
        byte[] data = Files.readAllBytes(path);
        //convert a pdf
        //byte[] pdfData = proveedorService.docxToPdf( new ByteArrayInputStream(xwpfDocumentBytes));
        //==============================================================
        response.setHeader("Content-Disposition", "attachment; filename=" + nombreFile + ".pdf");
        return new ResponseEntity<>(data, HttpStatus.OK);
    }


    @RequestMapping(value = "/flagActivo/{idProveedor}/{estado}",
            method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Proveedor> getProveedorById(@PathVariable("idProveedor") Integer idProveedor,
                                                      @PathVariable("estado") Integer estado) throws PortalException {
        Proveedor proveedorResult = null;
        try {
         Proveedor proveedor = this.proveedorRepository.getById(idProveedor);
         if(proveedor != null){
            if(estado.equals(0)){ //inactivo
                proveedor.setFlagActivo(0); //inactivo
                proveedor.setFechaModificacion(DateUtils.getCurrentTimestamp());
                proveedorResult  =this.proveedorRepository.save(proveedor);
            }
            if(estado.equals(1)){
                proveedor.setFlagActivo(1); //activo
                proveedor.setFechaModificacion(DateUtils.getCurrentTimestamp());
                proveedorResult  =this.proveedorRepository.save(proveedor);
            }
         }else{
             throw new PortalException("El proveedor no existe :" +idProveedor);
         }
        }catch (Exception ex){
            throw new PortalException("error:" + ex.getMessage());
        }

        return Optional.ofNullable(proveedorResult).map(l -> new ResponseEntity<>(l, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    @GetMapping("/extraer-proveedor-sap")
    public void extraerProveedor(){
        try {
            proveedorService.extraerProveedorSap();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}









