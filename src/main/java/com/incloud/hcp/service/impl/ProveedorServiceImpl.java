package com.incloud.hcp.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.incloud.hcp.service.cmiscf.bean.CmisFile;
import com.incloud.hcp.bean.ProveedorCustom;
import com.incloud.hcp.bean.ProveedorFiltro;
import com.incloud.hcp.bean.UserSession;
import com.incloud.hcp.domain.*;
import com.incloud.hcp.dto.*;
import com.incloud.hcp.dto.blacklist.SolicitudBlackListDTOMapper;
import com.incloud.hcp.dto.mapper.*;
import com.incloud.hcp.enums.EstadoBlackListEnum;
import com.incloud.hcp.enums.EstadoProveedorEnum;
import com.incloud.hcp.exception.PortalException;
import com.incloud.hcp.exception.ServiceException;
import com.incloud.hcp.jco.consultaProveedor.service.JCOConsultaProveedorService;
import com.incloud.hcp.myibatis.mapper.*;
import com.incloud.hcp.populate.Populater;
import com.incloud.hcp.repository.*;
import com.incloud.hcp.rest.bean.ProveedorDatosGeneralesDTO;
import com.incloud.hcp.sap.proveedor.ProveedorBeanSAP;
import com.incloud.hcp.sap.proveedor.ProveedorResponse;
import com.incloud.hcp.sap.proveedor.ProveedorWebService;
import com.incloud.hcp.service.ProveedorService;
import com.incloud.hcp.service.cmiscf.CmisBaseService;
import com.incloud.hcp.service.cmiscf.bean.CmisFolder;
import com.incloud.hcp.service.delta.UsuarioLineaComercialDeltaService;
import com.incloud.hcp.service.notificacion.ProveedorDataMaestraNotificacion;
import com.incloud.hcp.service.notificacion.ProveedorPotencialAprobadoNotificacion;
import com.incloud.hcp.util.*;
import com.incloud.hcp.util.constant.LicitacionConstant;
import com.incloud.hcp.util.constant.ParametroConstant;
import com.incloud.hcp.util.constant.ParametroTipoConstant;
import com.incloud.hcp.util.constant.TipoSolicitudConstant;
import com.incloud.hcp.wsdl.inside.InSiteResponse;
import com.incloud.hcp.wsdl.inside.InSiteService;
import fr.opensagres.poi.xwpf.converter.pdf.PdfConverter;
import fr.opensagres.poi.xwpf.converter.pdf.PdfOptions;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import okhttp3.*;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
/**
 * Created by Administrador on 29/08/2017.
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class ProveedorServiceImpl implements ProveedorService {

    private static final String DOCUMENT_FOLDER = "temp";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final int NRO_EJECUCIONES = 10;
    @Value("${sm.portal.dev}")
    private Boolean isDev;

    @Value("${API_URL_SAP}")
    private String urlSap;
    @Value("${USERNAME_SAP}")
    private String userSap;
    @Value("${PASSWORD_SAP}")
    private String passwordSap;

    @Value("${USERNAME_SAP}")
    private String USERNAME;
    @Value("${PASSWORD_SAP}")
    private String PASSWORD;
    @Value("${API_URL_SAP}")
    private String SAP_API_URL;
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ProveedorRepository proveedorRepository;

    @Autowired
    private MigProveedorRepository migProveedorRepository;

    @Autowired
    private ProveedorCanalContactoRepository proveedorCanalContactoRepository;

    @Autowired
    private ProveedorAdjuntoSunatRepository proveedorAdjuntoSunatRepository;

    @Autowired
    private ProveedorCuentaBancoRepository proveedorCuentaBancoRepository;

    @Autowired
    private ProveedorLineaComercialRepository proveedorLineaComercialRepository;

    @Autowired
    private ProveedorProductoRepository proveedorProductoRepository;

    @Autowired
    private ProveedorEvaluacionRepository proveedorEvaluacionRepository;

    @Autowired
    private SolicitudBlackListRepository solicitudBlackListRepository;

    @Autowired
    private ProveedorCatalogoRepository getProveedorCatalogoRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProveedorHomologacionMapper proveedorHomologacionMapper;

    @Autowired
    private JCOConsultaProveedorService jcoConsultaProveedorService;

    @Autowired
    private ContactoMapper contactoMapper;
    @Autowired
    private LogTransaccionRepository logTransaccionRepository;

    @Autowired
    private CuentaBancariaMapper cuentaBancariaMapper;

    @Autowired
    private BancoRepository bancoRepository;

    @Autowired
    private MonedaRepository monedaRepository;

    @Autowired
    protected MessageSource messageSource;

    @Autowired
    private UbigeoMapper ubigeoMapper;

    @Autowired
    private ProveedorMapper proveedorMapperMybatis;

    @Autowired
    private CondicionPagoReposity condicionPagoReposity;

    @Autowired
    private SectorTrabajoRepository sectorTrabajoRepository;

    @Autowired
    private TipoComprobanteRepository tipoComprobanteRepository;

    @Autowired
    private TipoProveedorRepository tipoProveedorRepository;

    @Autowired
    private LineaComercialRepository lineaComercialRepository;

    @Autowired
    private ProveedorCatalogoRepository proveedorCatalogoRepository;

    @Autowired
    private UbigeoRepository ubigeoRepository;

    @Autowired
    private PreRegistroProveedorRepository preRegistroProveedorRepository;

    @Autowired
    private LicitacionProveedorRepository licitacionProveedorRepository;

    @Autowired
    private LicitacionProveedorMapper licitacionProveedorMapper;

    @Autowired
    private BlackListMapper blackListMapper;

    @Autowired
    private ProveedorWebService proveedorWebService;

    @Autowired
    private HomologacionMapper homologacionMapper;


    @Autowired
    private ParametroMapper parametroMapper;

    @Autowired
    private ProveedorSectorTrabajoRepository proveedorSectorTrabajoRepository;

    @Autowired
    private EstadoProveedorRepository estadoProveedorRepository;

    @Autowired
    private ProveedorInstalacionRepository proveedorInstalacionRepository;

    @Autowired
    private ProveedorFuncionarioRepository proveedorFuncionarioRepository;

    @Autowired
    private ProveedorPreguntaInformacionRepository proveedorPreguntaInformacionRepository;

    @Autowired
    private ProveedorClienteRepository proveedorClienteRepository;

    @Autowired
    private ProveedorPermisoRepository proveedorPermisoRepository;

    @Autowired
    private PreguntaInformacionRepository preguntaInformacionRepository;

    @Autowired
    private PreguntaInformacionRespuestaRepository preguntaInformacionRespuestaRepository;

    @Autowired
    private ProveedorHomologacionRepository proveedorHomologacionRepository;

    @Autowired
    private ProveedorInstalacionMapper proveedorInstalacionMapper;

    @Autowired
    private ProveedorPermisoMapper proveedorPermisoMapper;

    @Autowired
    private ProveedorPreguntaInformacionMapper proveedorPreguntaInformacionMapper;

    @Autowired
    private ProveedorClienteMapper proveedorClienteMapper;

    @Autowired
    private ProveedorFuncionarioMapper proveedorFuncionarioMapper;

    @Autowired
    private UsuarioLineaComercialDeltaService usuarioLineaComercialDeltaService;

    @Autowired
    private ProveedorDataMaestraNotificacion proveedorDataMaestraNotificacion;

    @Autowired
    private CmisBaseService cmisBaseServicecf;

    @Autowired
    private InSiteService inSiteService;

    @Autowired
    private ProveedorDeclaracionJuradaRepository proveedorDeclaracionJuradaRepository;

    @Autowired
    private ProveedorAntecedenteRepository proveedorAntecedenteRepository;

    @Autowired
    private ProveedorAdjuntoCuentaBancariaRepository proveedorAdjuntoCuentaBancariaRepository;

    @Autowired
    private ProveedorCuentaBancariaRepository proveedorCuentaBancariaRepository;

    @Autowired
    private ProveedorPepRepository proveedorPepRepository;

    @Autowired
    private ProveedorRepresentanteLegalRepository proveedorRepresentanteLegalRepository;

    @Autowired
    private ProveedorParientePepRepository proveedorParientePepRepository;

    private Populater<SectorTrabajo, SectorTrabajoDto> sectorTrabajoPopulater;

    @Autowired
    private ProveedorAntecedenteRepresentanteLegalRepository proveedorAntecedenteRepresentanteLegalRepository;

    @Autowired
    private ProveedorAccionistasAsociadosRepository accionistasAsociadosRepository;
    @Autowired
    private ProveedorService proveedorService;
    @Autowired
    private ProveedorAccionistaPepRepository proveedorAccionistaPepRepository;

    @Autowired
    private ProveedorAccionistaParientePepRepository proveedorAccionistaParientePepRepository;

    @Autowired
    private ProveedorPotencialAprobadoNotificacion proveedorPotencialAprobadoNotificacion;

    @Autowired
    private ParametroRepository parametroRepository;

    @Autowired
    private AreaComprasRepository areaComprasRepository;

    public ProveedorServiceImpl() {
    }

    @Autowired
    @Qualifier(value = "sectorTrabajoPopulate")
    public void setSectorTrabajoPopulater(Populater<SectorTrabajo, SectorTrabajoDto> sectorTrabajoPopulater) {
        this.sectorTrabajoPopulater = sectorTrabajoPopulater;
    }

    @Autowired
    public void setProveedorSectorTrabajoRepository(ProveedorSectorTrabajoRepository proveedorSectorTrabajoRepository) {
        this.proveedorSectorTrabajoRepository = proveedorSectorTrabajoRepository;
    }

    private Proveedor getOne(Integer idProveedor) {
        Proveedor proveedor = this.proveedorRepository.getProveedorByIdProveedor(idProveedor);
        if (Optional.ofNullable(proveedor).isPresent()) {
            if (proveedor.getTipoProveedor().getDescripcion().equals("Nacional")) {
                if (Optional.ofNullable(proveedor.getRuc()).isPresent()) {

                    InSiteResponse response = null;
                    for (int contador = 0; contador < NRO_EJECUCIONES; contador++) {
                        try {
                            response = inSiteService.getConsultaRuc(proveedor.getRuc());
                            break;
                        } catch (Exception e) {
                            if (contador == NRO_EJECUCIONES - 1) {
                                throw new PortalException(e.getMessage());
                            }
                        }
                    }

                    if (Optional.ofNullable(response).isPresent()) {
                        BeanUtils.copyProperties(response, proveedor);
                        Optional.ofNullable(response.isEstado()).map(v -> v ? "1" : "0").ifPresent(proveedor::setIndActivoSunat);
                        Optional.ofNullable(response.isCondicion()).map(v -> v ? "1" : "0").ifPresent(proveedor::setIndHabidoSunat);
                    }

//                try {
//                    InSiteResponse response = inSiteService.getConsultaRuc(proveedor.getRuc());
//                    if (response != null) {
//                        BeanUtils.copyProperties(response, proveedor);
////                        proveedor.setFechaInicioActiSunat(response.getFechaInicioActiSunat());
////                        proveedor.setCodigoComprobantePago(response.getCodigoComprobantePago());
////                        proveedor.setCodigoSistemaEmisionElect(response.getCodigoSistemaEmisionElect());
////                        proveedor.setCodigoPadron(response.getCodigoPadron());
//                        Optional.ofNullable(response.isEstado()).map(v -> v ? "1" : "0").ifPresent(proveedor::setIndActivoSunat);
//                        Optional.ofNullable(response.isCondicion()).map(v -> v ? "1" : "0").ifPresent(proveedor::setIndHabidoSunat);
//                    }
//                } catch (InSiteException ex) {
//
//                }
                }
            }
        }


        return proveedor;
    }

    @Override
    @Transactional(readOnly = true)
    public ProveedorDto getProveedorDtoById(Integer idProveedor) throws PortalException {
        logger.error("Ingresando getProveedorDtoById 00");
        return Optional.ofNullable(this)
                .map(r -> r.getOne(idProveedor))
                .map(this::toDto)
                .orElse(new ProveedorDto());
    }

    @Override
    @Transactional(readOnly = true)
    public Proveedor getProveedorById(Integer idProveedor) throws PortalException {
        return Optional.ofNullable(proveedorRepository)
                .map(r -> r.getOne(idProveedor))
                .orElse(null);
    }

    public void eliminarDatosProveedor(Integer idProveedor) {
        Proveedor p = this.getProveedorById(idProveedor);
        if (!Optional.ofNullable(p).isPresent()) {
            return;
        }
        this.proveedorHomologacionRepository.deleteRespuestaByIdProveedor(p.getIdProveedor());
        this.proveedorCanalContactoRepository.deleteCanalContactoByIdProveedor(p.getIdProveedor());
        this.proveedorCuentaBancoRepository.deleteContactoByIdProveedor(p.getIdProveedor());
        this.proveedorLineaComercialRepository.deleteLineaComercialByIdProveedor(p.getIdProveedor());
        this.proveedorProductoRepository.deleteProductoByIdProveedor(p.getIdProveedor());
        this.proveedorSectorTrabajoRepository.deleteSectorTrabajoByIdProveedor(p.getIdProveedor());

        this.proveedorInstalacionRepository.deleteInstalacionByIdProveedor(p.getIdProveedor());
        this.proveedorFuncionarioRepository.deleteFuncionarioByIdProveedor(p.getIdProveedor());
        this.proveedorPreguntaInformacionRepository.deletePreguntaInformacionByIdProveedor(p.getIdProveedor());
        this.proveedorClienteRepository.deleteClienteByIdProveedor(p.getIdProveedor());
        this.proveedorPermisoRepository.deletePermisoByIdProveedor(p.getIdProveedor());
        this.proveedorRepository.deleteById(p.getIdProveedor());
        return;

    }


    @Override
    public Proveedor save(Proveedor documento) {
        documento.setFechaModificacion(DateUtils.getCurrentTimestamp());
        return proveedorRepository.save(documento);
    }

    @Override
    public ProveedorDto saveDto(ProveedorDto dto) throws Exception {
        if (dto == null) {
            throw new Exception("ProveedorDto ingresado es NULL");
        }
        if (dto.getIdProveedor() == null || dto.getIdProveedor() == 0) {
            Proveedor proveedorRuc = this.proveedorRepository.getProveedorByRuc(dto.getRuc());
            if (proveedorRuc != null && proveedorRuc.getIdProveedor() != null) {
                dto.setIdProveedor(proveedorRuc.getIdProveedor());
            }
        }
        logger.error("Ingresando GRABAR PROVEEDOR Service 1");
        ProveedorDTOMapper proveedorDTOMapper = new ProveedorDTOMapper(
                this.condicionPagoReposity,
                this.ubigeoMapper,
                this.monedaRepository,
                this.tipoComprobanteRepository,
                this.tipoProveedorRepository);
        Optional<Proveedor> encontrado = Optional.ofNullable(dto.getIdProveedor())
                .map(this.proveedorRepository::getOne);
        Proveedor p;
        Proveedor data = proveedorDTOMapper.toEntity(dto);
        logger.error("Ingresando GRABAR PROVEEDOR Service 4 data: " + data.toString());
        //EstadoProveedor estadoProveedor = this.estadoProveedorRepository.
        //getByCodigoEstadoProveedor(EstadoProveedorEnum.REGISTRADO.getCodigo());

        if (encontrado.isPresent()) {
            logger.error("Ingresando GRABAR PROVEEDOR Service 5");
            p = encontrado.get();
            BeanUtils.copyProperties(data, p);
            p.setUsuarioCreacion(0);
            p.setFechaModificacion(new Date());
            //p.setIndHomologado("0");
            //p.setEvaluacionHomologacion(new BigDecimal(0));

            this.proveedorCanalContactoRepository.deleteCanalContactoByIdProveedor(p.getIdProveedor());
            this.proveedorCuentaBancoRepository.deleteContactoByIdProveedor(p.getIdProveedor());
            this.proveedorLineaComercialRepository.deleteLineaComercialByIdProveedor(p.getIdProveedor());
            this.proveedorProductoRepository.deleteProductoByIdProveedor(p.getIdProveedor());
            this.proveedorSectorTrabajoRepository.deleteSectorTrabajoByIdProveedor(p.getIdProveedor());

            this.proveedorInstalacionRepository.deleteInstalacionByIdProveedor(p.getIdProveedor());
            this.proveedorFuncionarioRepository.deleteFuncionarioByIdProveedor(p.getIdProveedor());
            this.proveedorPreguntaInformacionRepository.deletePreguntaInformacionByIdProveedor(p.getIdProveedor());
            this.proveedorClienteRepository.deleteClienteByIdProveedor(p.getIdProveedor());
            this.proveedorPermisoRepository.deletePermisoByIdProveedor(p.getIdProveedor());
            logger.error("Ingresando GRABAR PROVEEDOR Service 6");

//            EstadoProveedor estadoProveedorActual = p.getIdEstadoProveedor();
//            if (estadoProveedorActual.getCodigoEstadoProveedor().equals(EstadoProveedorEnum.RECHAZADO_DATA_MAESTRA.getCodigo())) {
//                p.setIdEstadoProveedor(estadoProveedor);
//            }
//            if (estadoProveedorActual.getCodigoEstadoProveedor().equals(EstadoProveedorEnum.MIGRADO_DE_SAP.getCodigo())) {
//                p.setIdEstadoProveedor(estadoProveedor);
//
//            }
        } else {
            logger.error("Ingresando GRABAR PROVEEDOR Service 7");


            p = new Proveedor();
            BeanUtils.copyProperties(data, p);
            //p.setIdEstadoProveedor(estadoProveedor);
            p.setFechaCreacion(new Date());
            p.setIndProveedorComunidad("0");
            p.setEvaluacionDesempeno(new BigDecimal(0));
            p.setEvaluacionHomologacion(new BigDecimal(0));
            p.setIndBlackList("0");
            p.setIndBloqueadoSap("0");
            p.setIndHomologado("0");
            p.setIndSujetoRetencion("0");
            p.setUsuarioCreacion(0);
            p.setFechaCreacion(new Date());
            p.setFechaModificacion(DateUtils.getCurrentTimestamp());
            logger.error("Ingresando GRABAR PROVEEDOR Service 8");
        }

        logger.error("Ingresando GRABAR PROVEEDOR Service 9 p: " + p.toString());
        final Proveedor proveedor = this.proveedorRepository.save(p);
        logger.error("Ingresando GRABAR PROVEEDOR Service 10");

        /**
         * Contactos por canales de distribución
         */
        CanalContactoDTOMapper canalContactoDTOMapper = new CanalContactoDTOMapper(this.ubigeoMapper);
        Optional.ofNullable(dto.getCanales())
                .ifPresent(l -> l.stream()
                        .map(canalContactoDTOMapper::toEntity)
                        .forEach(c -> {
                            c.setProveedor(proveedor);
                            this.proveedorCanalContactoRepository.save(c);
                        }));

        /**
         * Cuentas de Banco
         */
        CuentaBancoDTOMapper bancoDTOMapper = new CuentaBancoDTOMapper(this.bancoRepository, this.monedaRepository);
        Optional.ofNullable(dto.getCuentasBanco())
                .ifPresent(l -> l.stream()
                        .map(bancoDTOMapper::toEntity)
                        .forEach(b -> {
                            ProveedorCuentaBancaria pcb = new ProveedorCuentaBancaria();
                            pcb.setIdCuenta(null);
                            Optional.ofNullable(b.getTipoCuenta())
                                    .map(t -> t.getCodigo()).ifPresent(pcb::setClaveControlBanco);
                            pcb.setContacto(b.getContacto());
                            pcb.setBanco(b.getBanco());
                            pcb.setMoneda(b.getMoneda());
                            pcb.setNumeroCuenta(b.getNumeroCuenta());
                            pcb.setNumeroCuentaCci(b.getNumeroCuentaCci());
                            pcb.setArchivoId(b.getArchivoId());
                            pcb.setArchivoNombre(b.getArchivoNombre());
                            pcb.setArchivoTipo(b.getArchivoTipo());
                            pcb.setRutaAdjunto(b.getRutaAdjunto());
                            pcb.setProveedor(proveedor);
                            this.proveedorCuentaBancoRepository.save(pcb);
                        }));
        /**
         * Lineas Comerciales
         */
        ProveedorLineaComercialDTOMapper lineaDtoMapper = new ProveedorLineaComercialDTOMapper(this.lineaComercialRepository);
        Optional.ofNullable(dto.getLineasComercial())
                .ifPresent(l -> l.stream()
                        .map(lineaDtoMapper::toEntity)
                        .forEach(n -> {
                            n.setProveedor(proveedor);
                            this.proveedorLineaComercialRepository.save(n);
                        }));
        /**
         * Productos
         */
        ProveedorProductoDTOMapper productoDTOMapper = new ProveedorProductoDTOMapper();
        Optional.ofNullable(dto.getProductos())
                .ifPresent(l -> l.stream()
                        .map(productoDTOMapper::toEntity)
                        .forEach(pr -> {
                            pr.setProveedor(proveedor);
                            this.proveedorProductoRepository.save(pr);
                        }));

        /**
         * Instalaciones
         */
        Proveedor proveedorPadre = new Proveedor();
        proveedorPadre.setIdProveedor(proveedor.getIdProveedor());
        List<ProveedorInstalacion> proveedorInstalacionList = dto.getInstalaciones();
        if (Optional.ofNullable(proveedorInstalacionList).isPresent()) {
            for (ProveedorInstalacion bean : proveedorInstalacionList) {
                bean.setIdProveedor(proveedorPadre);
                logger.error("Instalaciones bean instalacion: " + bean.toString());
            }
            logger.error("Instalaciones proveedorInstalacionList: " + proveedorInstalacionList.toString());
            this.proveedorInstalacionRepository.saveAll(proveedorInstalacionList);
        }

        /**
         * Permisos
         */
        List<ProveedorPermiso> proveedorPermisoList = dto.getPermisos();
        if (Optional.ofNullable(proveedorPermisoList).isPresent()) {
            for (ProveedorPermiso bean : proveedorPermisoList) {
                bean.setIdProveedor(proveedorPadre);
            }
            this.proveedorPermisoRepository.saveAll(proveedorPermisoList);
        }

        /**
         * Principales
         */
        List<ProveedorCliente> proveedorClienteList = dto.getPrincipales();
        if (Optional.ofNullable(proveedorClienteList).isPresent()) {
            for (ProveedorCliente bean : proveedorClienteList) {
                bean.setIdProveedor(proveedorPadre);
            }
            this.proveedorClienteRepository.saveAll(proveedorClienteList);
        }

        /**
         * Adicionales
         */
        List<ProveedorFuncionario> proveedorFuncionarioList = dto.getAdicionales();
        if (Optional.ofNullable(proveedorFuncionarioList).isPresent()) {
            for (ProveedorFuncionario bean : proveedorFuncionarioList) {
                bean.setIdProveedor(proveedorPadre);
            }
            this.proveedorFuncionarioRepository.saveAll(proveedorFuncionarioList);
        }

        /**
         * Pregunta Informacion
         */
        List<ProveedorPreguntaInformacion> proveedorPreguntaInformacionList = dto.getPreguntaInformacion();
        if (Optional.ofNullable(proveedorPreguntaInformacionList).isPresent()) {
            for (ProveedorPreguntaInformacion bean : proveedorPreguntaInformacionList) {
                bean.setIdProveedor(proveedorPadre);
                PreguntaInformacion preguntaInformacion = bean.getIdPreguntaInformacion();
                if (preguntaInformacion.isRespuestaSiNo()) {
                    bean.setRespuesta(Constant.N);
                    if (bean.isRespuestaSiNo()) {
                        bean.setRespuesta(Constant.S);
                    }
                }
            }
            this.proveedorPreguntaInformacionRepository.saveAll(proveedorPreguntaInformacionList);
        }

        /**
         *  Sector de trabajo
         */
        Optional.ofNullable(dto.getSectorTrabajos())
                .ifPresent(list -> list.stream()
                        .map(SectorTrabajoDto::getIdSectorTrabajo)
                        .map(id -> this.sectorTrabajoRepository.getOne(id))
                        .forEach(st -> {
                            final ProveedorSectorTrabajo pst = new ProveedorSectorTrabajo();
                            pst.setSectorTrabajo(st);
                            pst.setProveedor(proveedor);
                            this.proveedorSectorTrabajoRepository.save(pst);
                        }));

        logger.error("Finalizando GRABAR Sector trabajo");
        /////guardar AdjuntoSunat

        List<ProveedorAdjuntoSunat> listAdjuntosSunat = this.guardarAdjuntoSunat(proveedor.getIdProveedor(), dto.getAdjuntosSunat(), null);
        logger.error("Finalizando GRABAR AdjuntoSunat");


        ////guardar Catalogos
        List<ProveedorCatalogo> listCatalogo = this.guardarAdjuntoCatalogo(proveedor, dto.getCatalogos(), null);
        logger.error("Finalizando GRABAR catalogos");


        dto.setIdProveedor(proveedor.getIdProveedor());
        //dto.setAdjuntosSunat(listAdjuntosSunat);
        logger.error("Finalizando GRABAR PROVEEDOR Service");
        return dto;
    }

    @Override
    public List<Proveedor> getListProveedor() {
        return proveedorRepository.findAll();
    }

    @Override
    public List<ProveedorCustom> getListProveedorByFiltro(ProveedorFiltro proveedorFiltro) {

        return proveedorMapperMybatis.getListProveedorByFiltro(
                proveedorFiltro.getIdsPais(),
                proveedorFiltro.getRazonSocial(),
                proveedorFiltro.getIdsRegion(),
                proveedorFiltro.getIdsProvincia(),
                proveedorFiltro.getNroRuc(),
                proveedorFiltro.getTipoProveedor(),
                proveedorFiltro.getTipoPersona(),
                proveedorFiltro.getIndHomologado(),
                proveedorFiltro.getMarca(),
                proveedorFiltro.getProducto(),
                proveedorFiltro.getDescripcionAdicional(),
                proveedorFiltro.getIdsLinea(),
                proveedorFiltro.getIdsFamilia(),
                proveedorFiltro.getIdsSubFamilia(),
                proveedorFiltro.getEstadoProveedor(),
                proveedorFiltro.getIdAreaCompra()
        );
    }

    @Override
    public List<ProveedorCustom> getListProveedorByFiltroPaginado(ProveedorFiltro proveedorFiltro) {
        Integer paginaMostrar = new Integer((proveedorFiltro.getPaginaMostrar().intValue() - 1) * proveedorFiltro.getNroRegistros());
        proveedorFiltro.setPaginaMostrar(paginaMostrar);
        return proveedorMapperMybatis.getListProveedorByFiltroPaginado(
                proveedorFiltro.getIdsPais(),
                proveedorFiltro.getRazonSocial(),
                proveedorFiltro.getIdsRegion(),
                proveedorFiltro.getIdsProvincia(),
                proveedorFiltro.getNroRuc(),
                proveedorFiltro.getTipoProveedor(),
                proveedorFiltro.getTipoPersona(),
                proveedorFiltro.getIndHomologado(),
                proveedorFiltro.getMarca(),
                proveedorFiltro.getProducto(),
                proveedorFiltro.getDescripcionAdicional(),
                proveedorFiltro.getIdsLinea(),
                proveedorFiltro.getIdsFamilia(),
                proveedorFiltro.getIdsSubFamilia(),
                proveedorFiltro.getEstadoProveedor(),
                proveedorFiltro.getNroRegistros(),
                proveedorFiltro.getPaginaMostrar()
        );
    }

    @Override
    public List<ProveedorCustom> getListProveedorByFiltroValidacion(UserSession userSession, ProveedorFiltro proveedorFiltro) {
        List<LineaComercial> lineaComercialList = this.usuarioLineaComercialDeltaService.
                devuelveLineaComercial(userSession);
        if (lineaComercialList == null || lineaComercialList.size() <= 0) {
            return null;
        }
        ArrayList<String> idsFamilia = new ArrayList<String>();
        for (LineaComercial bean : lineaComercialList) {
            String idLinea = bean.getIdLineaComercial().toString();
            idsFamilia.add(idLinea);
        }
        //proveedorFiltro.setIdsFamilia(idsFamilia);
        //proveedorFiltro.setIdsLinea(idsFamilia);

//        idsFamilia.add("2");//SERVICIOS
//        idsFamilia.add("1");//BIENES
//        proveedorFiltro.setIdsLinea(idsFamilia);

        List<ProveedorCustom> pc = proveedorMapperMybatis.getListProveedorByFiltroValidacion(
                proveedorFiltro.getIdsPais(),
                proveedorFiltro.getIdsRegion(),
                proveedorFiltro.getIdsProvincia(),
                proveedorFiltro.getNroRuc(),
                proveedorFiltro.getRazonSocial(),
                proveedorFiltro.getTipoProveedor(),
                proveedorFiltro.getTipoPersona(),
                proveedorFiltro.getIndHomologado(),
                proveedorFiltro.getMarca(),
                proveedorFiltro.getProducto(),
                proveedorFiltro.getDescripcionAdicional(),
                proveedorFiltro.getIdsLinea(),
                proveedorFiltro.getIdsFamilia(),
                proveedorFiltro.getIdsSubFamilia(),
                proveedorFiltro.getEstadoProveedor(),
                proveedorFiltro.getIdAreaCompra()
        );
        System.out.println("");
        return pc;
    }

    @Override
    public List<ProveedorCustom> getListProveedorByFiltroLicitacion(ProveedorFiltro proveedorFiltro) {
        return proveedorMapperMybatis.getListProveedorByFiltroLicitacion(
                proveedorFiltro.getIdsPais(),
                proveedorFiltro.getRazonSocial(),
                proveedorFiltro.getIdsRegion(),
                proveedorFiltro.getIdsProvincia(),
                proveedorFiltro.getNroRuc(),
                proveedorFiltro.getTipoProveedor(),
                proveedorFiltro.getTipoPersona(),
                proveedorFiltro.getIndHomologado(),
                proveedorFiltro.getMarca(),
                proveedorFiltro.getProducto(),
                proveedorFiltro.getDescripcionAdicional(),
                proveedorFiltro.getIdsLinea(),
                proveedorFiltro.getIdsFamilia(),
                proveedorFiltro.getIdsSubFamilia()
        );
    }

    @Override
    public List<ProveedorCustom> getListProveedorByFiltroLicitacionPaginado(ProveedorFiltro proveedorFiltro) {


        Integer paginaMostrar = new Integer((proveedorFiltro.getPaginaMostrar().intValue() - 1) * proveedorFiltro.getNroRegistros());
        proveedorFiltro.setPaginaMostrar(paginaMostrar);
        return proveedorMapperMybatis.getListProveedorByFiltroLicitacionPaginado(
                proveedorFiltro.getIdsPais(),
                proveedorFiltro.getRazonSocial(),
                proveedorFiltro.getIdsRegion(),
                proveedorFiltro.getIdsProvincia(),
                proveedorFiltro.getNroRuc(),
                proveedorFiltro.getTipoProveedor(),
                proveedorFiltro.getTipoPersona(),
                proveedorFiltro.getIndHomologado(),
                proveedorFiltro.getMarca(),
                proveedorFiltro.getProducto(),
                proveedorFiltro.getDescripcionAdicional(),
                proveedorFiltro.getIdsLinea(),
                proveedorFiltro.getIdsFamilia(),
                proveedorFiltro.getIdsSubFamilia(),
                proveedorFiltro.getNroRegistros(),
                proveedorFiltro.getPaginaMostrar()
        );
    }

    @Override
    public List<ProveedorCustom> getListProveedorByRuc(String ruc) {
        return proveedorMapperMybatis.getListProveedorByRuc(ruc);
    }


    @Override
    public List<ProveedorCustom> getListProveedorSinHcpID() {
        return proveedorMapperMybatis.getListProveedorMigrados();
    }

    @Override
    public List<LineaComercialDto> getListLineaComercialByIdProveedor(Integer idProveedor) {
        return Optional.ofNullable(homologacionMapper.getListHomologacionByIdProveedor(idProveedor))
                .map(l -> {
                    List<LineaComercialDto> list = new ArrayList<>();
                    l.stream().map(linea -> {
                        LineaComercialDto dto = new LineaComercialDto();
                        dto.setIdLinea(linea.getIdLinea());
                        dto.setLinea(linea.getLinea());
                        return dto;
                    }).forEach(list::add);
                    return list;
                })
                .orElse(new ArrayList<>());
    }

    @Override
    public List<ProveedorCatalogoDto> getListCatalogosByIdProveedor(Integer idProveedor) {
        ProveedorCatalogoDTOMapper catalogoMapper = new ProveedorCatalogoDTOMapper();

        return Optional.ofNullable(proveedorCatalogoRepository)
                .map(r -> r.getProveedorCatalogoByIdProveedor(idProveedor))
                .map(l -> {
                    List<ProveedorCatalogoDto> list = new ArrayList<>();
                    l.stream().map(catalogoMapper::toDto).forEach(list::add);
                    return list;
                })
                .orElse(new ArrayList<>());
    }

    public ProveedorDto toDtoResponder(Proveedor proveedor) {
        /**
         * Información del proveedor
         */
        logger.error("TO DTO RESPONDER: " + proveedor);
        ProveedorDTOMapper proveedorDtoMapper = new ProveedorDTOMapper(this.condicionPagoReposity,
                this.ubigeoMapper,
                this.monedaRepository,
                this.tipoComprobanteRepository,
                this.tipoProveedorRepository);
        Optional<ProveedorDto> oDto = Optional.ofNullable(proveedor)
                .map(proveedorDtoMapper::toDto)
                .map(dto -> {
                    if (dto.getIdProveedor() == null) {
                        return dto;
                    }

                    int idProveedor = dto.getIdProveedor();
                    logger.error("TO DTO RESPONDER 01 : " + idProveedor);
                    /**
                     *  Contactos por canal de distribución
                     */
                    Optional.ofNullable(this.contactoMapper)
                            .map(r -> {
                                List<CanalContactoDto> list = r.getListContactoByIdProveedor(idProveedor);
                                return list;
                            }).ifPresent(l -> l.stream().forEach(dto::addCanalContacto));
                    logger.error("TO DTO RESPONDER 02");
                    /**
                     *  Cuentas de Banco
                     */

                    CuentaBancoDTOMapper bancoDTOMapper = new CuentaBancoDTOMapper(this.bancoRepository,
                            this.monedaRepository);
                    Optional.ofNullable(this.cuentaBancariaMapper)
                            .map(r -> r.getListCuentaByIdProveedor(ParametroConstant.CUENTA_BANCO,
                                    ParametroTipoConstant.TIPO,
                                    dto.getIdProveedor()))
                            .ifPresent(l -> l.stream()
                                    .map(bancoDTOMapper::toDto)
                                    .forEach(dto::addCuentaBanco));
                    logger.error("TO DTO RESPONDER 03");
                    /**
                     * Líneas comerciales
                     */
                    ProveedorLineaComercialDTOMapper lineaDtoMapper = new ProveedorLineaComercialDTOMapper(this.lineaComercialRepository);
                    Optional.ofNullable(this.proveedorLineaComercialRepository)
                            .map(r -> r.getListLineaComercialByIdProveedor(idProveedor))
                            .ifPresent(l -> l.stream().map(lineaDtoMapper::toDto).forEach(dto::addLineaComercial));
                    logger.error("TO DTO RESPONDER 04");
                    /**
                     * Evaluacion de Homologación
                     */

                    Optional.ofNullable(homologacionMapper)
                            .map(r -> r.getListHomologacionByIdProveedorResponder(idProveedor))
                            .ifPresent(l -> {
                                l.stream().forEach(lch -> {
                                    lch.getPreguntas().forEach(p -> {
                                        final ProveedorHomologacionDto data = new ProveedorHomologacionDto();
                                        data.setIdLineaComercial(lch.getIdLinea());
                                        data.setLineaComercial(lch.getLinea());
                                        data.setIdHomologacion(p.getIdHomologacion());
                                        data.setPregunta(p.getPregunta());
                                        data.setIntAdjunto(p.getIndicadorAdjunto());
                                        data.setEstado(p.getEstado());
                                        data.setIndEstado(false);
                                        data.setValorRespuestaLibre(p.getValorRespuestaLibre());
                                        if (data.getEstado().equals("1")) {
                                            data.setIndEstado(true);
                                        }
                                        Optional.ofNullable(p.getRespuestaProveedor())
                                                .ifPresent(resp -> {
                                                    data.setRutaAdjunto(resp.getRutaAdjunto());
                                                    data.setArchivoId(resp.getArchivoId());
                                                    data.setArchivoTipo(resp.getArchivoTipo());
                                                    data.setArchivoNombre(resp.getNombreArchivo());
                                                    p.getOpciones().stream()
                                                            .filter(opt -> opt.getIdHomologacionRespuesta().equals(resp.getIdHomologacionRespuesta()))
                                                            .findFirst()
                                                            .map(f -> f.getRespuesta())
                                                            .ifPresent(data::setRespuesta);
                                                });
                                        dto.addRespuestaHomologacion(data);
                                    });
                                });
                            });
                    logger.error("TO DTO RESPONDER 05");
                    /**
                     * Productos
                     */
                    ProveedorProductoDTOMapper productoDtoMapper = new ProveedorProductoDTOMapper();
                    Optional.ofNullable(proveedorProductoRepository)
                            .map(r -> r.getListProductoByIdProveedor(idProveedor))
                            .ifPresent(l -> l.stream()
                                    .map(productoDtoMapper::toDto)
                                    .forEach(dto::addProducto));
                    logger.error("TO DTO RESPONDER 06");
                    /**
                     * Evaluación de desempeño
                     */
                    EvaluacionDTOMapper evaluacionDtoMapper = new EvaluacionDTOMapper();
                    Optional.ofNullable(proveedorEvaluacionRepository)
                            .map(r -> r.getProveedorEvaluacionByIdProveedor(idProveedor))
                            .ifPresent(l -> l.stream()
                                    .map(evaluacionDtoMapper::toDto)
                                    .forEach(dto::addEvaluacionDesempenio));
                    logger.error("TO DTO RESPONDER 07");
                    /**
                     * No conformes
                     */

                    SolicitudBlackListDTOMapper solicitudDtoMapper = new SolicitudBlackListDTOMapper();
                    Optional.ofNullable(solicitudBlackListRepository)
                            .map(r -> r.getListNoConformeByIdProveedorAndEstado(idProveedor,
                                    TipoSolicitudConstant.REGISTRO_NO_CONFORME.getId(),
                                    EstadoBlackListEnum.APROBADA.getCodigo()))
                            .ifPresent(l -> l.stream().map(solicitudDtoMapper::toDto).forEach(s -> {
                                dto.addNoConforme(s);
                            }));
                    logger.error("TO DTO RESPONDER 08");
                    /**
                     * Catálogos
                     */

                    ProveedorCatalogoDTOMapper catalogoMapper = new ProveedorCatalogoDTOMapper();
                    Optional.ofNullable(proveedorCatalogoRepository)
                            .map(r -> r.getProveedorCatalogoByIdProveedor(idProveedor))
                            .ifPresent(l -> l.stream().map(catalogoMapper::toDto)
                                    .forEach(dto::addCatalogo));
                    logger.error("TO DTO RESPONDER 08");

                    ///adjuntoSunat
                    ProveedorAdjuntoSunatDTOMapper adjuntoSunatDTOMapper = new ProveedorAdjuntoSunatDTOMapper();
                    Optional.ofNullable(proveedorAdjuntoSunatRepository)
                            .map(r -> r.getProveedorAdjuntoSunatByIdProveedor(idProveedor))
                            .ifPresent(l -> l.stream().map(adjuntoSunatDTOMapper::toDto)
                                    .forEach(dto::addAdjuntoSunat));
                    logger.error("TO DTO RESPONDER 10");
                  /*  List<ProveedorAdjuntoSunat> proveedorAdjuntoSunatList =this.proveedorAdjuntoSunatRepository.getProveedorAdjuntoSunatByIdProveedor(idProveedor);
                    dto.setAdjuntosSunat(proveedorAdjuntoSunatList);*/
                    /**
                     * Instalacion
                     */
                    ProveedorInstalacion proveedorInstalacion = new ProveedorInstalacion();
                    proveedorInstalacion.setIdBuscarProveedor(idProveedor);
                    List<ProveedorInstalacion> proveedorInstalacionList =
                            this.proveedorInstalacionMapper.getProveedorInstalacion(proveedorInstalacion);

                    dto.setInstalaciones(proveedorInstalacionList);
                    logger.error("TO DTO RESPONDER 11");
                    /**
                     * Permisos
                     */
                    ProveedorPermiso proveedorPermiso = new ProveedorPermiso();
                    proveedorPermiso.setIdBuscarProveedor(idProveedor);
                    List<ProveedorPermiso> proveedorPermisoList =
                            this.proveedorPermisoMapper.getProveedorPermiso(proveedorPermiso);
                    dto.setPermisos(proveedorPermisoList);
                    logger.error("TO DTO RESPONDER 12");
                    /**
                     * Principales
                     */
                    ProveedorCliente proveedorCliente = new ProveedorCliente();
                    proveedorCliente.setIdBuscarProveedor(idProveedor);
                    List<ProveedorCliente> proveedorClienteList =
                            this.proveedorClienteMapper.getProveedorCliente(proveedorCliente);
                    dto.setPrincipales(proveedorClienteList);
                    logger.error("TO DTO RESPONDER 13");
                    /**
                     * Adicionales
                     */
                    ProveedorFuncionario proveedorFuncionario = new ProveedorFuncionario();
                    proveedorFuncionario.setIdBuscarProveedor(idProveedor);
                    List<ProveedorFuncionario> proveedorFuncionarioList =
                            this.proveedorFuncionarioMapper.getProveedorFuncionario(proveedorFuncionario);
                    dto.setAdicionales(proveedorFuncionarioList);
                    logger.error("toDto 05 Adicionales " + proveedorFuncionarioList.toString());
                    logger.error("TO DTO RESPONDER 14");
                    /**
                     * Pregunta Informacion
                     */
                    List<PreguntaInformacion> preguntaInformacionList =
                            this.preguntaInformacionRepository.findAll(Sort.by("orden"));

                    System.out.println(preguntaInformacionList);
                    List<ProveedorPreguntaInformacion> proveedorPreguntaInformacionList = new ArrayList<ProveedorPreguntaInformacion>();
                    for (PreguntaInformacion bean : preguntaInformacionList) {
                        ProveedorPreguntaInformacion beanBuscar = new ProveedorPreguntaInformacion();
                        beanBuscar.setIdBuscarProveedor(idProveedor);
                        beanBuscar.setIdBuscarPreguntaInformacion(bean.getId());
                        List<ProveedorPreguntaInformacion> lista =
                                this.proveedorPreguntaInformacionMapper.getProveedorPreguntaInformacion(beanBuscar);

                        ProveedorPreguntaInformacion proveedorPreguntaInformacion = new ProveedorPreguntaInformacion();
                        proveedorPreguntaInformacion.setRespuestaSiNo(false);
                        proveedorPreguntaInformacion.setRespuestas(false);
                        if (lista != null && lista.size() > 0) {
                            proveedorPreguntaInformacion = lista.get(0);
                        }
                        if (Optional.ofNullable(proveedorPreguntaInformacion.getRespuesta()).isPresent()) {
                            if (proveedorPreguntaInformacion.getRespuesta().equals(Constant.S)) {
                                proveedorPreguntaInformacion.setRespuestaSiNo(true);
                            } else {
                                proveedorPreguntaInformacion.setRespuestas(true);
                            }


                        }
                        List<PreguntaInformacionRespuesta> list =
                                this.preguntaInformacionRespuestaRepository.consultarPreguntaInformacionRespuesta(bean.getId());
                        System.out.println(list);
                        if (list != null && list.size() > 0) {
                            proveedorPreguntaInformacion.setPreguntaInformacionRespuestas(list);
                        } else {
                            list = new ArrayList<>();
                            proveedorPreguntaInformacion.setPreguntaInformacionRespuestas(list);

                        }
                        proveedorPreguntaInformacion.setIdPreguntaInformacion(bean);
                        proveedorPreguntaInformacionList.add(proveedorPreguntaInformacion);
                    }
                    dto.setPreguntaInformacion(proveedorPreguntaInformacionList);
                    logger.error("TO DTO RESPONDER 15");
                    /**
                     * Sector de trabajo
                     */
                    Optional.ofNullable(proveedorSectorTrabajoRepository)
                            .map(r -> r.getListSectorTrabajoByIdProveedor(idProveedor))
                            .ifPresent(l -> {
                                List<SectorTrabajoDto> listDto = new ArrayList<>();
                                l.stream()
                                        .map(ProveedorSectorTrabajo::getSectorTrabajo)
                                        .map(sectorTrabajo -> sectorTrabajoPopulater.toDto(sectorTrabajo))
                                        .forEach(listDto::add);
                                dto.setSectorTrabajos(listDto);
                            });
                    logger.error("TO DTO RESPONDER 16");


                    return dto;
                });

        return oDto.isPresent() ? oDto.get() : null;
    }

    public ProveedorDto toDto(Proveedor proveedor) {
        /**
         * Información del proveedor
         */
        ProveedorDTOMapper proveedorDtoMapper = new ProveedorDTOMapper(this.condicionPagoReposity,
                this.ubigeoMapper,
                this.monedaRepository,
                this.tipoComprobanteRepository,
                this.tipoProveedorRepository);
        Optional<ProveedorDto> oDto = Optional.ofNullable(proveedor)
                .map(proveedorDtoMapper::toDto)
                .map(dto -> {
                    if (dto.getIdProveedor() == null) {
                        return dto;
                    }

                    int idProveedor = dto.getIdProveedor();
                    logger.error("toDto 01 idProveedor: " + idProveedor);

                    /**
                     *  Contactos por canal de distribución
                     */
                    Optional.ofNullable(this.contactoMapper)
                            .map(r -> {
                                List<CanalContactoDto> list = r.getListContactoByIdProveedor(idProveedor);
                                return list;
                            }).ifPresent(l -> l.stream().forEach(dto::addCanalContacto));

                    /**
                     *  Cuentas de Banco
                     */

                    CuentaBancoDTOMapper bancoDTOMapper = new CuentaBancoDTOMapper(this.bancoRepository,
                            this.monedaRepository);
                    Optional.ofNullable(this.cuentaBancariaMapper)
                            .map(r -> r.getListCuentaByIdProveedor(ParametroConstant.CUENTA_BANCO,
                                    ParametroTipoConstant.TIPO,
                                    dto.getIdProveedor()))
                            .ifPresent(l -> l.stream()
                                    .map(bancoDTOMapper::toDto)
                                    .forEach(dto::addCuentaBanco));
                    /**
                     * Líneas comerciales
                     */
                    ProveedorLineaComercialDTOMapper lineaDtoMapper = new ProveedorLineaComercialDTOMapper(this.lineaComercialRepository);
                    Optional.ofNullable(this.proveedorLineaComercialRepository)
                            .map(r -> r.getListLineaComercialByIdProveedor(idProveedor))
                            .ifPresent(l -> l.stream()
                                    .map(lineaDtoMapper::toDto)
                                    .forEach(dto::addLineaComercial));

                    /**
                     * Evaluacion de Homologación
                     */
                    Optional.ofNullable(homologacionMapper)
                            .map(r -> r.getListHomologacionByIdProveedor(idProveedor))
                            .ifPresent(l -> {
                                l.stream().forEach(lch -> {
                                    lch.getPreguntas().forEach(p -> {
                                        final ProveedorHomologacionDto data = new ProveedorHomologacionDto();
                                        data.setIdLineaComercial(lch.getIdLinea());
                                        data.setLineaComercial(lch.getLinea());
                                        data.setIdHomologacion(p.getIdHomologacion());
                                        data.setPregunta(p.getPregunta());
                                        data.setPeso(p.getPeso());
                                        data.setIntAdjunto(p.getIndicadorAdjunto());
                                        data.setEstado(p.getEstado());
                                        data.setIndEstado(false);
                                        data.setValorRespuestaLibre(p.getValorRespuestaLibre());
                                        if (data.getEstado().equals("1")) {
                                            data.setIndEstado(true);
                                        }
                                        Optional.ofNullable(p.getRespuestaProveedor())
                                                .ifPresent(resp -> {
                                                    data.setRutaAdjunto(resp.getRutaAdjunto());
                                                    data.setArchivoId(resp.getArchivoId());
                                                    data.setArchivoTipo(resp.getArchivoTipo());
                                                    data.setArchivoNombre(resp.getNombreArchivo() + "." + resp.getArchivoTipo());
                                                    p.getOpciones().stream()
                                                            .filter(opt -> opt.getIdHomologacionRespuesta().equals(resp.getIdHomologacionRespuesta()))
                                                            .findFirst()
                                                            .map(f -> f.getRespuesta())
                                                            .ifPresent(data::setRespuesta);
                                                });
                                        dto.addRespuestaHomologacion(data);
                                    });
                                });
                            });
                    /**
                     * Productos
                     */
                    ProveedorProductoDTOMapper productoDtoMapper = new ProveedorProductoDTOMapper();
                    Optional.ofNullable(proveedorProductoRepository)
                            .map(r -> r.getListProductoByIdProveedor(idProveedor))
                            .ifPresent(l -> l.stream()
                                    .map(productoDtoMapper::toDto)
                                    .forEach(dto::addProducto));

                    /**
                     * Evaluación de desempeño
                     */
                    EvaluacionDTOMapper evaluacionDtoMapper = new EvaluacionDTOMapper();
                    Optional.ofNullable(proveedorEvaluacionRepository)
                            .map(r -> r.getProveedorEvaluacionByIdProveedor(idProveedor))
                            .ifPresent(l -> l.stream()
                                    .map(evaluacionDtoMapper::toDto)
                                    .forEach(dto::addEvaluacionDesempenio));
                    /**
                     * No conformes
                     */

                    SolicitudBlackListDTOMapper solicitudDtoMapper = new SolicitudBlackListDTOMapper();
                    Optional.ofNullable(solicitudBlackListRepository)
                            .map(r -> r.getListNoConformeByIdProveedorAndEstado(idProveedor,
                                    TipoSolicitudConstant.REGISTRO_NO_CONFORME.getId(),
                                    EstadoBlackListEnum.APROBADA.getCodigo()))
                            .ifPresent(l -> l.stream().map(solicitudDtoMapper::toDto).forEach(s -> {
                                dto.addNoConforme(s);
                            }));

                    /**
                     * Catálogos
                     */

                    ProveedorCatalogoDTOMapper catalogoMapper = new ProveedorCatalogoDTOMapper();
                    Optional.ofNullable(proveedorCatalogoRepository)
                            .map(r -> r.getProveedorCatalogoByIdProveedor(idProveedor))
                            .ifPresent(l -> l.stream().map(catalogoMapper::toDto)
                                    .forEach(dto::addCatalogo));

                    logger.error("toDto 02 ");

                    //adjuntoSunat

                    ProveedorAdjuntoSunatDTOMapper adjuntoSunatDTOMapper = new ProveedorAdjuntoSunatDTOMapper();
                    Optional.ofNullable(proveedorAdjuntoSunatRepository)
                            .map(r -> r.getProveedorAdjuntoSunatByIdProveedor(idProveedor))
                            .ifPresent(l -> l.stream().map(adjuntoSunatDTOMapper::toDto)
                                    .forEach(dto::addAdjuntoSunat));

                    /*
                    List<ProveedorAdjuntoSunat> adjuntoSunatList= this.proveedorAdjuntoSunatRepository.getProveedorAdjuntoSunatByIdProveedor(idProveedor);
                    dto.setAdjuntosSunat(adjuntoSunatList);*/
                    /**
                     * Instalacion
                     */
                    ProveedorInstalacion proveedorInstalacion = new ProveedorInstalacion();
                    proveedorInstalacion.setIdBuscarProveedor(idProveedor);
                    List<ProveedorInstalacion> proveedorInstalacionList =
                            this.proveedorInstalacionMapper.getProveedorInstalacion(proveedorInstalacion);

                    dto.setInstalaciones(proveedorInstalacionList);
                    logger.error("toDto 03 Instalacion " + proveedorInstalacionList.toString());

                    /**
                     * Permisos
                     */
                    ProveedorPermiso proveedorPermiso = new ProveedorPermiso();
                    proveedorPermiso.setIdBuscarProveedor(idProveedor);
                    List<ProveedorPermiso> proveedorPermisoList =
                            this.proveedorPermisoMapper.getProveedorPermiso(proveedorPermiso);
                    dto.setPermisos(proveedorPermisoList);
                    logger.error("toDto 03 Permisos " + proveedorPermisoList.toString());

                    /**
                     * Principales
                     */
                    ProveedorCliente proveedorCliente = new ProveedorCliente();
                    proveedorCliente.setIdBuscarProveedor(idProveedor);
                    List<ProveedorCliente> proveedorClienteList =
                            this.proveedorClienteMapper.getProveedorCliente(proveedorCliente);
                    dto.setPrincipales(proveedorClienteList);
                    logger.error("toDto 04 Principales " + proveedorClienteList.toString());

                    /**
                     * Adicionales
                     */
                    ProveedorFuncionario proveedorFuncionario = new ProveedorFuncionario();
                    proveedorFuncionario.setIdBuscarProveedor(idProveedor);
                    List<ProveedorFuncionario> proveedorFuncionarioList =
                            this.proveedorFuncionarioMapper.getProveedorFuncionario(proveedorFuncionario);
                    dto.setAdicionales(proveedorFuncionarioList);
                    logger.error("toDto 05 Adicionales " + proveedorFuncionarioList.toString());

                    /**
                     * Pregunta Informacion
                     */
                    List<PreguntaInformacion> preguntaInformacionList =
                            this.preguntaInformacionRepository.findAll(Sort.by("orden"));
                    System.out.println(preguntaInformacionList);
                    List<ProveedorPreguntaInformacion> proveedorPreguntaInformacionList = new ArrayList<ProveedorPreguntaInformacion>();
                    for (PreguntaInformacion bean : preguntaInformacionList) {
                        ProveedorPreguntaInformacion beanBuscar = new ProveedorPreguntaInformacion();
                        beanBuscar.setIdBuscarProveedor(idProveedor);
                        beanBuscar.setIdBuscarPreguntaInformacion(bean.getId());
                        List<ProveedorPreguntaInformacion> lista =
                                this.proveedorPreguntaInformacionMapper.getProveedorPreguntaInformacion(beanBuscar);

                        ProveedorPreguntaInformacion proveedorPreguntaInformacion = new ProveedorPreguntaInformacion();
                        proveedorPreguntaInformacion.setRespuestaSiNo(false);
                        proveedorPreguntaInformacion.setRespuestas(false);
                        if (lista != null && lista.size() > 0) {
                            proveedorPreguntaInformacion = lista.get(0);
                        }
                        if (Optional.ofNullable(proveedorPreguntaInformacion.getRespuesta()).isPresent()) {
                            if (proveedorPreguntaInformacion.getRespuesta().equals(Constant.S)) {
                                proveedorPreguntaInformacion.setRespuestaSiNo(true);
                            } else {
                                proveedorPreguntaInformacion.setRespuestas(true);
                            }


                        }
                        List<PreguntaInformacionRespuesta> list =
                                this.preguntaInformacionRespuestaRepository.consultarPreguntaInformacionRespuesta(bean.getId());
                        System.out.println(list);
                        if (list != null && list.size() > 0) {
                            proveedorPreguntaInformacion.setPreguntaInformacionRespuestas(list);
                        } else {
                            list = new ArrayList<>();
                            proveedorPreguntaInformacion.setPreguntaInformacionRespuestas(list);

                        }
                        proveedorPreguntaInformacion.setIdPreguntaInformacion(bean);
                        proveedorPreguntaInformacionList.add(proveedorPreguntaInformacion);
                    }
                    dto.setPreguntaInformacion(proveedorPreguntaInformacionList);
                    logger.error("toDto 06 Pregunta " + proveedorPreguntaInformacionList.toString());

                    /**
                     * Sector de trabajo
                     */
                    Optional.ofNullable(proveedorSectorTrabajoRepository)
                            .map(r -> r.getListSectorTrabajoByIdProveedor(idProveedor))
                            .ifPresent(l -> {
                                List<SectorTrabajoDto> listDto = new ArrayList<>();
                                l.stream()
                                        .map(ProveedorSectorTrabajo::getSectorTrabajo)
                                        .map(sectorTrabajo -> sectorTrabajoPopulater.toDto(sectorTrabajo))
                                        .forEach(listDto::add);
                                dto.setSectorTrabajos(listDto);
                            });

                    logger.error("toDto 07 dto: " + dto.toString());
                    return dto;
                });

        return oDto.isPresent() ? oDto.get() : null;
    }

    @Override
    @Transactional(readOnly = true)
    public Proveedor getProveedorByRuc(String ruc) {
        return this.proveedorRepository.getProveedorByRuc(ruc);
    }

    @Override
    @Transactional(readOnly = true)
    public Proveedor getProveedorDtoByEmail(String email) {
        return this.proveedorRepository.getProveedorByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public ProveedorDto getProveedorByAcreedorCodigoSap(String codigoSAP) throws PortalException {
        return Optional.ofNullable(proveedorRepository)
                .map(r -> r.getProveedorByAcreedorCodigoSap(codigoSAP))
                .map(this::toDto)
                .orElse(new ProveedorDto());
    }

    @Override
    @Transactional(readOnly = true)
    public ProveedorDto getProveedorDtoByIdHcp(String idHcp) throws PortalException {
        return Optional.ofNullable(proveedorRepository)
                .map(r -> r.getProveedorByIdHcp(idHcp))
                .map(this::toDto)
                .orElse(new ProveedorDto());
    }

    @Override
    @Transactional(readOnly = true)
    public Proveedor getProveedorByIdHcp(String idHcp) throws PortalException {
        return Optional.ofNullable(proveedorRepository)
                .map(r -> r.getProveedorByIdHcp(idHcp))
                .orElse(new Proveedor());
    }

    @Override
    public ProveedorDto getProveedorDtoByRuc(String ruc) {
        return Optional.ofNullable(proveedorRepository)
                .map(r -> r.getProveedorByRuc(ruc))
                .map(this::toDto)
                .orElse(null);
    }

    @Override
    public ProveedorDto getProveedorDtoByRucResponder(String ruc) {
        return Optional.ofNullable(proveedorRepository)
                .map(r -> r.getProveedorByRuc(ruc))
                .map(this::toDtoResponder)
                .orElse(null);
    }

    @Override
    public ProveedorDto getProveedorDtoByEmailResponder(String email) {
        return Optional.ofNullable(proveedorRepository)
                .map(r -> r.getProveedorByEmail(email))
                .map(this::toDtoResponder)
                .orElse(null);
    }


    public List<ProveedorDatosGeneralesDTO> getProveedorDatosGenerales(
            String fechaCreacionIni, String fechaCreacionFin) throws PortalException {
        Date dFechaCreacionIni = new Date();
        Date dFechaCreacionFin = new Date();
        int indicador = 0;

        List<Proveedor> listaProveedor = new ArrayList<Proveedor>();
        if (StringUtils.isNotBlank(fechaCreacionIni)) {
            indicador = 1;
            if (StringUtils.isNotBlank(fechaCreacionFin))
                indicador = 2;
        }

        try {
            switch (indicador) {
                case 1:
                    dFechaCreacionIni = DateUtils.convertStringToDate(fechaCreacionIni);
                    break;
                case 2:
                    dFechaCreacionIni = DateUtils.convertStringToDate(fechaCreacionIni);
                    dFechaCreacionFin = DateUtils.convertStringToDate(fechaCreacionFin);
                    break;
            }
        } catch (ParseException e) {
            indicador = 0;
        }


        switch (indicador) {
            case 0:
                listaProveedor = this.proveedorRepository.findAllByOrderByRuc();
                break;
            case 1:
                listaProveedor = this.proveedorRepository.findByFechaCreacionGreaterThanEqualOrderByRuc(dFechaCreacionIni);
                break;
            case 2:
                listaProveedor = this.proveedorRepository.findByFechaCreacionBetweenOrderByRuc(dFechaCreacionIni, dFechaCreacionFin);
                break;
        }

        List<ProveedorDatosGeneralesDTO> listaProveedorDatosGenerales = new ArrayList<ProveedorDatosGeneralesDTO>();
        for (Proveedor item : listaProveedor) {
            ProveedorDatosGeneralesDTO bean = new ProveedorDatosGeneralesDTO();
            bean.setProveedor(item);

            Integer licitacionesParticipo = this.licitacionProveedorRepository.
                    countByProveedorAndEstadoLicitacionLicitada(item, LicitacionConstant.ESTADO_ADJUDICADA, LicitacionConstant.ESTADO_ENVIADO_SAP);
            Integer licitacionesGanadas = this.licitacionProveedorMapper.countByLicitacionesGanadas(item.getIdProveedor());
            Integer licitacionesPerdidas = licitacionesParticipo.intValue() - licitacionesGanadas.intValue();
            Integer noConformesRegistrados = this.blackListMapper.countByBlackListRegistrados(
                    item.getIdProveedor(), Constant.CODIGO_TIPO_SOLICITUD_NO_CONFORME);
            bean.setLicitacionesParticipo(licitacionesParticipo);
            bean.setLicitacionesGanadas(licitacionesGanadas);
            bean.setLicitacionesPerdidas(licitacionesPerdidas);
            bean.setNoConformesRegistrados(noConformesRegistrados);
            listaProveedorDatosGenerales.add(bean);
        }

        return listaProveedorDatosGenerales;
    }

    @Override
    public ProveedorDto sendToSap(Integer idProveedor) throws PortalException {
        Optional<Proveedor> oProveedor = Optional.ofNullable(proveedorRepository)
                .map(r -> r.getOne(idProveedor));

        if (!oProveedor.isPresent()) {
            throw new PortalException("El proveedor no existe");
        }
        Proveedor proveedor = oProveedor.get();
        if (Optional.ofNullable(proveedor.getAcreedorCodigoSap()).isPresent()) {
            throw new PortalException("El proveedor posee código SAP: " + proveedor.getAcreedorCodigoSap() + " por lo que no se ha enviado a SAP");
        }

        logger.error("Ingresando sendToSap: " + oProveedor.toString());
        ProveedorResponse response = proveedorWebService.grabarProveedorSAP(Arrays.asList(oProveedor.get()));
        logger.error("Ingresando sendToSap 01");
        Optional<ProveedorBeanSAP> oProveedorResponse = Optional.ofNullable(response.getListaProveedorSAPResult())
                .filter(list -> list.size() == 1)
                .map(list -> list.get(0));
        logger.error("Ingresando sendToSap 02");
        if (!oProveedorResponse.isPresent()) {
            throw new PortalException("La respuesta de la sincronización a SAP es nula");
        }
        logger.error("Ingresando sendToSap 03");
        if (response.isTieneError()) {
            String error = oProveedorResponse.map(r -> r.getSapLogProveedor()).map(log -> log.getMesaj()).orElse("-");
            throw new PortalException(error);
        }

        return toDto(oProveedorResponse.get().getProveedorSAP());
    }

    public void evaluarDataMaestra(Integer idProveedor) throws PortalException {
        Optional<Proveedor> oProveedor = Optional.ofNullable(proveedorRepository)
                .map(r -> r.getOne(idProveedor));

        if (!oProveedor.isPresent()) {
            throw new PortalException("El proveedor no existe");
        }
        Proveedor proveedorActual = oProveedor.get();
        EstadoProveedor estadoProveedorActual = proveedorActual.getIdEstadoProveedor();
        if (estadoProveedorActual.getCodigoEstadoProveedor().equals(EstadoProveedorEnum.RECHAZADO_DATA_MAESTRA.getCodigo())) {
            throw new PortalException("No es posible Aprobar Data Maestra, debido que dicho Proveedor actualmente esta RECHAZADO x DATA MAESTRA");
        }

        EstadoProveedor estadoProveedor = this.estadoProveedorRepository.
                getByCodigoEstadoProveedor(EstadoProveedorEnum.APROBADO_DATA_MAESTRA.getCodigo());
        this.proveedorMapperMybatis.updateEstadoProveedor(
                oProveedor.get().getIdProveedor(),
                estadoProveedor.getId());

        //enviar correo

        proveedorDataMaestraNotificacion.enviar(this.parametroMapper.getMailSetting(), oProveedor.get(), "APR", "");


    }

    public void rechazarDataMaestra(Integer idProveedor, String motivo) throws PortalException {
        Optional<Proveedor> oProveedor = Optional.ofNullable(proveedorRepository)
                .map(r -> r.getOne(idProveedor));

        if (!oProveedor.isPresent()) {
            throw new PortalException("El proveedor no existe");
        }
        Proveedor proveedorActual = oProveedor.get();
        EstadoProveedor estadoProveedorActual = proveedorActual.getIdEstadoProveedor();
        if (estadoProveedorActual.getCodigoEstadoProveedor().equals(EstadoProveedorEnum.APROBADO_DATA_MAESTRA.getCodigo())) {
            throw new PortalException("No es posible Rechazar Data Maestra, debido que dicho Proveedor actualmente esta APROBADO x DATA MAESTRA");
        }

        EstadoProveedor estadoProveedor = this.estadoProveedorRepository.
                getByCodigoEstadoProveedor(EstadoProveedorEnum.RECHAZADO_DATA_MAESTRA.getCodigo());
        this.proveedorMapperMybatis.updateEstadoProveedor(
                oProveedor.get().getIdProveedor(),
                estadoProveedor.getId());
        //correo


        String respuesta = proveedorDataMaestraNotificacion.enviar(this.parametroMapper.getMailSetting(), oProveedor.get(), "REC", motivo);
        LogTransaccion logTransaccion = new LogTransaccion();
        logTransaccion.setEnvioTrama(idProveedor.toString());
        logTransaccion.setRespuestaCodigo(respuesta);
        logTransaccion.setTipoRegistro("Correo proveedorDataMaestraNotificacion");
        this.logTransaccionRepository.save(logTransaccion);

    }

    @Override
    public Integer updateProveedorIDHCP(ListProveedorHCP listProveedorHCP) {
        List<ProveedorCustom> listaProveedoresSinHCP = listProveedorHCP.getListaProveedorsinIDHCP();
        Integer nroProveedor = listaProveedoresSinHCP.size();
        if (listaProveedoresSinHCP.size() > 0) {
            for (ProveedorCustom obj : listaProveedoresSinHCP) {
                this.proveedorRepository.updateIdHCP(obj.getIdHCP(), obj.getRuc());
            }
        }
        return nroProveedor;
    }

    protected boolean depurarUploadExcel(ProveedorDto dto) {
        if (StringUtils.isBlank(dto.getAcreedorCodigoSap())) {
            return false;
        }
//        if(StringUtils.isBlank(dto.getEmail())){
//            return false;
//        }
        if (StringUtils.isBlank(dto.getTipoPersona())) {
            return false;
        }
        return true;
    }

    protected String validacionesPrevias(ProveedorDto dto) {
        String mensaje = "";

        if (!Optional.ofNullable(dto.getAcreedorCodigoSap()).isPresent()) {
            String msj = this.messageSource.getMessage("message.AcreedorSap.noIngresado", null,
                    LocaleContextHolder.getLocale());
            mensaje += "* " + msj + " ";
        }
        if (!Optional.ofNullable(dto.getEmail()).isPresent()) {
            String msj = this.messageSource.getMessage("message.Email.noIngresado", null,
                    LocaleContextHolder.getLocale());
            mensaje += "* " + msj + " ";
        }
        if (!Optional.ofNullable(dto.getTipoPersona()).isPresent()) {
            String msj = this.messageSource.getMessage("message.TipoPersona.noIngresado", null,
                    LocaleContextHolder.getLocale());
            mensaje += "* " + msj + " ";
        }

        return mensaje;
    }

    protected ProveedorDto setUploadExcel(Cell currentCell, ProveedorDto proveedorDtoParam, int contador, DataFormatter dataFormatter) {
        Double valorNumerico = new Double(0);
        BigDecimal valorDecimal = new BigDecimal(0);
        String valorCadena = "";
        switch (contador) {
            case 1:
                try {
                    if (currentCell.getCellType() == CellType.STRING) {
                        valorCadena = currentCell.getStringCellValue().trim();
                    } else if (currentCell.getCellType() == CellType.NUMERIC) {
                        valorCadena = dataFormatter.formatCellValue(currentCell);
                    }
//                    valorCadena = currentCell.getStringCellValue();
                    if (valorCadena.length() > 10) {
                        throw new ServiceException("Valor Campo Acreedor contiene mas de 10 caracter(es)");
                    }
                    proveedorDtoParam.setAcreedorCodigoSap(valorCadena);
                } catch (Exception e) {
//                    throw new ServiceException("Valor Campo Acreedor está en formato incorrecto");
                    String error = StrUtils.obtieneMensajeErrorExceptionCustom(e);
                    logger.error("plantilla Excel migracion proveedores SAP / codigoAcreedorSap: " + currentCell.getStringCellValue() + " / EXCEPTION: " + error);
                    throw new ServiceException(error);
                }
                break;
            case 2:
                try {
                    valorCadena = currentCell.getStringCellValue();

                    if (!Utils.validarEmail(valorCadena)) {
                        throw new ServiceException("Valor Campo Email tiene formato incorrecto");
                    }
                    proveedorDtoParam.setEmail(valorCadena);
                } catch (Exception e) {
//                    throw new ServiceException("Valor Campo Email está en formato incorrecto");
                    String error = StrUtils.obtieneMensajeErrorExceptionCustom(e);
                    logger.error("plantilla Excel migracion proveedores SAP / email: " + currentCell.getStringCellValue() + " / EXCEPTION: " + error);
                    throw new ServiceException(error);
                }
                break;
            case 3:
                try {
                    valorCadena = currentCell.getStringCellValue();

                    if (valorCadena.length() != 1 || (!valorCadena.equals("J") && !valorCadena.equals("N"))) {
                        throw new ServiceException("Valor Campo Tipo Persona tiene formato incorrecto");
                    }
                    proveedorDtoParam.setTipoPersona(valorCadena);
                } catch (Exception e) {
//                    throw new ServiceException("Valor Tipo Persona está en formato incorrecto");
                    String error = StrUtils.obtieneMensajeErrorExceptionCustom(e);
                    logger.error("plantilla Excel migracion proveedores SAP / tipoPersona: " + currentCell.getStringCellValue() + " / EXCEPTION: " + error);
                    throw new ServiceException(error);
                }
                break;
            default:
                break;
        }
        return proveedorDtoParam;
    }

    public List<ProveedorXLSXDTO> uploadExcelData(InputStream in) {
        logger.debug("Ingresando uploadExcel");
        List<ProveedorXLSXDTO> mapaProveedorParamMasivoDTOArrayList = new ArrayList<ProveedorXLSXDTO>();
        DataFormatter dataFormatter = new DataFormatter(new Locale("en", "US"));
        int inicioRegistroData = 2;
        /*AppParametria appParametriaData = this.appParametriaDeltaRepository.getByModuloAndLabelAndStatus(AppParametriaModuloEnum.CARGA_EXCEL.getEstado(),
                AppParametriaLabelEnum.INICIO_REGISTRO_DATA.getEstado(), Constant.UNO);
        if (Optional.of(appParametriaData).isPresent()) {
            inicioRegistroData = new Integer(appParametriaData.getValue1()).intValue();
        }*/
        try {
            Workbook workbook = new XSSFWorkbook(in);
            try {
                Sheet datatypeSheet = workbook.getSheetAt(0);
                Iterator<Row> iterator = datatypeSheet.iterator();
                int contadorRegistro = 1;
                while (iterator.hasNext()) {
                    if (contadorRegistro < inicioRegistroData) {
                        contadorRegistro++;
                        Row currentRow = iterator.next();
                        continue;
                    }
                    ProveedorXLSXDTO proveedorXLSXDTO = new ProveedorXLSXDTO();
                    ProveedorDto proveedorParam = new ProveedorDto();
                    proveedorParam.setIdProveedor(null);
                    Row currentRow = iterator.next();
                    Iterator<Cell> cellIterator = currentRow.iterator();
                    boolean error = false;
                    try {
                        while (cellIterator.hasNext()) {
                            Cell currentCell = cellIterator.next();
                            proveedorParam = this.setUploadExcel(currentCell, proveedorParam, currentCell.getColumnIndex() + 1, dataFormatter);
                        }
                        String mensaje = this.validacionesPrevias(proveedorParam);
                        if (StringUtils.isNotBlank(mensaje)) {
                            throw new ServiceException(mensaje);
                        }
                        if (this.depurarUploadExcel(proveedorParam)) {
                            proveedorXLSXDTO.setProveedor(proveedorParam);
                            proveedorXLSXDTO.setMensaje("");
                            proveedorXLSXDTO.setError(false);
                        }
                    } catch (Exception e) {
                        proveedorXLSXDTO.setProveedor(proveedorParam);
                        proveedorXLSXDTO.setMensaje(Utils.obtieneMensajeErrorException(e));
                        proveedorXLSXDTO.setError(true);
                    }
                    mapaProveedorParamMasivoDTOArrayList.add(proveedorXLSXDTO);
                }
            } catch (Exception exw) {

            } finally {
                workbook.close();
            }
        } catch (Exception ex) {

        }
        return mapaProveedorParamMasivoDTOArrayList;
    }

    @Override
    public List<ProveedorXLSXDTO> uploadExcel(InputStream in) {
        logger.error("Ingresando uploadExcelDelta ");
        List<ProveedorXLSXDTO> listaUpload = this.uploadExcelData(in);
        logger.error("Ingresando after uploadExcel: ");
        if (listaUpload == null || listaUpload.size() <= 0) {
            return null;
        }

        logger.error("Fin uploadExcelDelta ");
        return listaUpload;
    }

    public List<ProveedorAdjuntoSunat> guardarAdjuntoSunat(Integer IdProveedor, List<ProveedorAdjuntoSunatDto> listAdjunto, String operacion) throws Exception {

        if (isDev) {
            return null;
        }
        Proveedor proveedor = this.proveedorService.getProveedorById(IdProveedor);

        logger.error("iniciando GRABAR AdjuntoSunat");
        //Creo una segunda lista con los adjuntos no guardados
        List<CmisFile> listAdjuntoNew = new ArrayList<CmisFile>();
        if (listAdjunto.size() > 0) {
            listAdjunto.forEach(item -> {
                if (item.getId() == null) {
                    listAdjuntoNew.add(new CmisFile(item.getArchivoId(), item.getArchivoNombre(), item.getRutaAdjunto(), item.getArchivoTipo()));
                    logger.error("Creo una segunda lista con los adjuntos no guardados");
                }
            });
        }

        //Se crea folder destino -> Nro de licitacion
        String newFolder = proveedor.getRuc();

        CmisFolder cmisFolder = cmisBaseServicecf.createFolder(newFolder);//cmisService.createFolder(newFolder);
        logger.debug("FOLDER_DESTINO: " + cmisFolder.getId());

        //Se mueven los adjuntos al folder destino y se obtiene la lista de los mismos con su nuevo URL
        Optional<List<CmisFile>> listAdjuntoMove = Optional.ofNullable(listAdjuntoNew)
                .map(list -> {
                    logger.debug("Actualizando la version de los archivos adjuntos");
                    return cmisBaseServicecf.updateFileAndMoveVerificar(listAdjuntoNew, newFolder); //cmisService.updateFileAndMoveVerificar(listAdjuntoNew, folderId);
                });

        //Se guardan en la base de datos los adjuntos movidos
        if (listAdjuntoMove.isPresent()) {
            List<CmisFile> list = listAdjuntoMove.get();
            list.forEach(file -> {
                ProveedorAdjuntoSunat obj = this.proveedorAdjuntoSunatRepository.save(new ProveedorAdjuntoSunat(proveedor, file.getUrl(), file.getId(), file.getName(), file.getType()));
            });
            logger.debug("Se guardan en la base de datos los adjuntos movidos");
        }

        //Eliminando adjuntos ausentes en el request
        List<ProveedorAdjuntoSunat> listaAdjuntosActual = this.proveedorAdjuntoSunatRepository.getProveedorAdjuntoSunatByIdProveedor(proveedor.getIdProveedor());
        for (ProveedorAdjuntoSunat obj1 : listaAdjuntosActual) {
            Boolean encontrado = false;
            for (ProveedorAdjuntoSunatDto obj2 : listAdjunto) {
                if ((obj1.getArchivoId()).equals(obj2.getArchivoId())) {
                    encontrado = true;
                    break;
                }
            }
            if (!encontrado) {
                this.proveedorAdjuntoSunatRepository.deleteProveedorAdjuntoSunatByLicitacionandAndArchivoId(proveedor.getIdProveedor(), obj1.getArchivoId());
                logger.debug("Eliminando adjuntos ausentes en el request");
                continue;
            }
        }

        //Se obtiene lista de adjuntos final
        List<ProveedorAdjuntoSunat> listaAdjuntosSunatResult = this.proveedorAdjuntoSunatRepository.getProveedorAdjuntoSunatByIdProveedor(proveedor.getIdProveedor());

        return listaAdjuntosSunatResult;

    }

    @Override
    public List<ProveedorAdjuntoSunat> eliminarAdjunto(Integer idProveedor, String idAdjunto) {

        this.proveedorAdjuntoSunatRepository.deleteProveedorAdjuntoSunatByLicitacionandAndArchivoId(idProveedor, idAdjunto);
        List<ProveedorAdjuntoSunat> proveedorAdjuntoSunats = this.proveedorAdjuntoSunatRepository.getProveedorAdjuntoSunatByIdProveedor(idProveedor);

        logger.debug("Eliminando adjuntos ausentes en el request");
        return proveedorAdjuntoSunats;
    }

    @Override
    public List<ProveedorCatalogo> eliminarAdjuntoCatalogo(Integer idProveedor, String idAdjunto) {

        this.getProveedorCatalogoRepository.deleteCatalogoByIdProveedorCatalogoById(idProveedor, idAdjunto);
        List<ProveedorCatalogo> proveedorAdjuntoCatalogo = this.getProveedorCatalogoRepository.getProveedorCatalogoByIdProveedor(idProveedor);

        logger.debug("Eliminando adjuntos ausentes en el request");
        return proveedorAdjuntoCatalogo;
    }

    public List<ProveedorCatalogo> guardarAdjuntoCatalogo(Proveedor proveedor, List<ProveedorCatalogoDto> catalogosList, String operacion) throws Exception {

        if (isDev) {
            return null;
        }
        logger.error("iniciando GRABAR catalogos");
        //Creo una segunda lista con los adjuntos no guardados
        List<CmisFile> listAdjuntoNew = new ArrayList<CmisFile>();
        if (catalogosList.size() > 0) {
            catalogosList.forEach(item -> {
                if (item.getId() == null) {
                    listAdjuntoNew.add(new CmisFile(item.getArchivoId(), item.getArchivoNombre(), item.getRutaCatalogo(), item.getArchivoTipo()));

                    logger.error("Creo una segunda lista con los adjuntos no catalogos");
                }
            });
        }


        String newFolder = proveedor.getRuc() + "-catalogos";

        String folderId = cmisBaseServicecf.createFolder(newFolder).getId();
        logger.error("FOLDER_DESTINO: " + folderId);

        //Se mueven los adjuntos al folder destino y se obtiene la lista de los mismos con su nuevo URL
        Optional<List<CmisFile>> listAdjuntoMove = Optional.ofNullable(listAdjuntoNew)
                .map(list -> {
                    logger.error("Actualizando la version de los archivos catalogos");
                    return cmisBaseServicecf.updateFileAndMoveVerificar(listAdjuntoNew, newFolder); //cmisService.updateFileAndMoveVerificar(listAdjuntoNew, folderId);
                });

        //Se guardan en la base de datos los adjuntos movidos
        if (listAdjuntoMove.isPresent()) {
            List<CmisFile> list = listAdjuntoMove.get();
            list.forEach(file -> {
                ProveedorCatalogo obj = this.proveedorCatalogoRepository.save(new ProveedorCatalogo(proveedor, file.getUrl(), file.getId(), file.getName(), file.getType()));
            });
            logger.error("Se guardan en la base de datos los catalogos movidos");
        }

        //Eliminando adjuntos ausentes en el request
        logger.error(proveedor.getIdProveedor().toString());
        List<ProveedorCatalogo> listaAdjuntosActual = this.proveedorCatalogoRepository.getProveedorCatalogoByIdProveedor(proveedor.getIdProveedor());
        for (ProveedorCatalogo obj1 : listaAdjuntosActual) {
            Boolean encontrado = false;
            for (ProveedorCatalogoDto obj2 : catalogosList) {
                if ((obj1.getArchivoId()).equals(obj2.getArchivoId())) {
                    encontrado = true;
                    break;
                }
            }
            if (!encontrado) {
                this.proveedorCatalogoRepository.deleteCatalogoByIdProveedorCatalogoById(proveedor.getIdProveedor(), obj1.getArchivoId());
                logger.debug("Eliminando adjuntos ausentes en el request");
                continue;
            }
        }

        //Se obtiene lista de adjuntos final
        List<ProveedorCatalogo> listaCatalogos = this.proveedorCatalogoRepository.getProveedorCatalogoByIdProveedor(proveedor.getIdProveedor());

        return listaCatalogos;

    }

    @Override
    public String saveEmail(String ruc, String email) {
        String respuesta = "";

        if (!email.isEmpty()) {
            Proveedor proveedor = proveedorRepository.getProveedorByRuc(ruc);

            if (proveedor != null) {
                proveedor.setEmail(email);
                proveedor.setFechaModificacion(DateUtils.getCurrentTimestamp());
                proveedor = proveedorRepository.save(proveedor);
                respuesta = "Se modificó correctamente la dirección de correo a: " + proveedor.getEmail();
            } else {
                respuesta = "No se encontró proveedor con RUC: " + ruc;
            }
        } else {
            respuesta = "La dirección de correo enviada está vacía";
        }

        return respuesta;
    }

    @Override
    public void saveProveedorSHana() {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        okhttp3.MediaType mediaType = okhttp3.MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");
        Request request = new Request.Builder()
                .url("https://my417543-api.s4hana.cloud.sap/sap/opu/odata/sap/YY1_PROVEEDOR_TRANSPORTE_CDS/YY1_Proveedor_Transporte")
                .get()
                .addHeader("Authorization", "Basic TV9UUkFOU1BPUlRFOl1oMnBnYnFRVnhHQUhLS1JRbW1tZHVkVHVNdllQV0xuQnJ4c3Jiams=")
                .addHeader("Accept", "application/json")
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String jsonResponse = response.body().string();
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(jsonResponse);
                JsonNode resultsNode = rootNode.path("d").path("results");

                // Filtrar por ruc
                List<JsonNode> filteredResults = new ArrayList<>();
                Set<String> uniqueBPTaxNumbers = new HashSet<>();

                resultsNode.forEach(node -> {
                    JsonNode bpTaxNumberNode = node.path("BPTaxNumber");
                    String bpTaxNumber = bpTaxNumberNode.asText();
                    if (bpTaxNumber != null && !bpTaxNumber.isEmpty() && uniqueBPTaxNumbers.add(bpTaxNumber)) {
                        filteredResults.add(node);
                    }
                });

                List<Proveedor> proveedores = new ArrayList<>();

                for (JsonNode resultNode : filteredResults) {
                    Proveedor proveedor = new Proveedor();
                    String ruc = resultNode.path("BPTaxNumber").asText(null);

                    if( ruc != null && !ruc.isEmpty() && this.proveedorRepository.findByRuc(ruc) != null){

                        String bpTaxNumber = resultNode.path("BPTaxNumber").asText(null);
                        if (bpTaxNumber != null && bpTaxNumber.startsWith("10")) {
                            proveedor.setTipoPersona("N");
                        } else {
                            proveedor.setTipoPersona("J");
                        }

                        String fechaCreacionStr = resultNode.path("FechaCreacion").asText(null);
                        if (fechaCreacionStr != null) {
                            try {
                                // Definir el formato esperado de la fecha
                                SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd"); // Ajusta el formato según tus necesidades
                                Date fechaCreacion = formatoFecha.parse(fechaCreacionStr);
                                proveedor.setFechaCreacion(fechaCreacion);
                            } catch (ParseException e) {
                                System.out.println("El valor de FechaCreacion no tiene un formato válido.");
                            }
                        }else{
                            proveedor.setFechaCreacion(new Date());
                        }

                        proveedor.setAcreedorCodigoSap(resultNode.path("Supplier").asText(null));
                        proveedor.setRazonSocial(resultNode.path("OrganizationBPName1").asText(null));
                        proveedor.setRuc(resultNode.path("BPTaxNumber").asText(null));

                        EstadoProveedor estado = this.estadoProveedorRepository.getByCodigoEstadoProveedor("MIG");
                        proveedor.setIdEstadoProveedor(estado);
                        Moneda moneda = this.monedaRepository.getByCodigoMoneda("PEN");
                        proveedor.setMoneda(moneda);

                        proveedor.setDireccionFiscal("d");
                        proveedor.setEvaluacionDesempeno(BigDecimal.valueOf(0));
                        proveedor.setEvaluacionHomologacion(BigDecimal.valueOf(0));
                        TipoProveedor tipo = this.tipoProveedorRepository.getById(1);
                        proveedor.setTipoProveedor(tipo);
                        TipoComprobante comprobante = this.tipoComprobanteRepository.getById(1);
                        proveedor.setTipoComprobante(comprobante);
                        proveedor.setActivo("1");
                        proveedor.setFlagActivo(1);
                        proveedor.setUsuarioCreacion(1);
                        CondicionPago cpago = this.condicionPagoReposity.getById(39);
                        proveedor.setCondicionPago(cpago);



                        proveedores.add(proveedor);

                        this.proveedorRepository.save(proveedor);
                    }

                }
                System.out.println("Respuesta: " + jsonResponse);

            } else {
                System.err.println("Error en la solicitud: " + response.code() + " - " + response.message());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public List<ProveedorCustom> devuelveProveedor(String emailProveedor) {
        List<ProveedorCustom> proveedorDato = new ArrayList<>();
        proveedorDato = proveedorMapperMybatis.devuelveProveedor(emailProveedor);
        return proveedorDato.isEmpty() ? null : proveedorDato;
    }

    @Override
    public ProveedorOutDto inspeccionProveedor(ProveedorInDto bean) throws Exception {
        ProveedorOutDto result = new ProveedorOutDto();
        ProveedorInDto beanCentro = new ProveedorInDto();
        ProveedorOutDto proveedorExiste = new ProveedorOutDto();

        beanCentro.setEmail(bean.getEmail());

        //Valida Proveedor
        proveedorExiste = proveedorMapperMybatis.validarProveedorCorreo(beanCentro.getEmail());

        //Crea Proveedor
        if(proveedorExiste == null){
            logger.error("PROVEEDOR NO EXISTE");
            beanCentro.setIdProveedor(bean.getIdProveedor());
            beanCentro.setCargoPersonaTesoreria(bean.getCargoPersonaTesoreria());
            beanCentro.setCargoPersonaCreditoCobranza(bean.getCargoPersonaCreditoCobranza());
            beanCentro.setCargoRepresentanteLegal(bean.getCargoRepresentanteLegal());
            beanCentro.setCelular(bean.getCelular());
            beanCentro.setCelularPersonaCompra(bean.getCelularPersonaCompra());
            beanCentro.setContacto(bean.getContacto());
            beanCentro.setCodigoPostal(bean.getCodigoPostal());
            beanCentro.setDireccionFiscal(bean.getDireccionFiscal());
            beanCentro.setEmail(bean.getEmail());
            beanCentro.setBancoExtranjero(bean.getBancoExtranjero());
            beanCentro.setIdAreaCompra(bean.getIdAreaCompra());

            beanCentro.setEmailPersonaCompra(bean.getEmailPersonaCompra());
            beanCentro.setEmailPersonaCompra2(bean.getEmailPersonaCompra2());
            beanCentro.setEmailPersonaCompra3(bean.getEmailPersonaCompra3());
            beanCentro.setEmailPersonaCompra4(bean.getEmailPersonaCompra4());

            beanCentro.setEmailPersonaCreditoCobranza(bean.getEmailPersonaCreditoCobranza());
            beanCentro.setEmailPersonaCreditoCobranza2(bean.getEmailPersonaCreditoCobranza2());
            beanCentro.setEmailPersonaCreditoCobranza3(bean.getEmailPersonaCreditoCobranza3());
            beanCentro.setEmailPersonaCreditoCobranza4(bean.getEmailPersonaCreditoCobranza4());

            beanCentro.setEmailPersonaTesoreria(bean.getEmailPersonaTesoreria());
            beanCentro.setEmailPersonaTesoreria2(bean.getEmailPersonaTesoreria2());
            beanCentro.setEmailPersonaTesoreria3(bean.getEmailPersonaTesoreria3());
            beanCentro.setEmailPersonaTesoreria4(bean.getEmailPersonaTesoreria4());

            beanCentro.setEmailRepresentanteLegal(bean.getEmailRepresentanteLegal());
            beanCentro.setEmailRepresentanteLegal2(bean.getEmailRepresentanteLegal2());
            beanCentro.setEmailRepresentanteLegal3(bean.getEmailRepresentanteLegal3());
            beanCentro.setEmailRepresentanteLegal4(bean.getEmailRepresentanteLegal4());

            beanCentro.setIdPais(bean.getIdPais());
            beanCentro.setIdRegion(bean.getIdRegion());
            beanCentro.setIdProvincia(bean.getIdProvincia());
            beanCentro.setIdDistrito(bean.getIdDistrito());
            beanCentro.setIdTipoProveedor(bean.getIdTipoProveedor());
            beanCentro.setIdCondicionPago(bean.getIdCondicionPago());
            beanCentro.setIdMoneda(bean.getIdMoneda());
            beanCentro.setIdTipoComprobante(bean.getIdTipoComprobante());
            beanCentro.setIndTipoVentaBien(bean.getIndTipoVentaBien());
            beanCentro.setIndTipoVentaServicio(bean.getIndTipoVentaServicio());
            beanCentro.setIndTipoFacturacionManual(bean.getIndTipoFacturacionManual());
            beanCentro.setIndTipoFacturacionElect(bean.getIndTipoFacturacionElect());
            beanCentro.setNombreRepresentanteLegal(bean.getNombreRepresentanteLegal());
            beanCentro.setNroDocumRepresentanteLegal(bean.getNroDocumRepresentanteLegal());
            beanCentro.setNombrePersonaTesoreria(bean.getNombrePersonaTesoreria());
            beanCentro.setNroDocumPersonaTesoreria(bean.getNroDocumPersonaTesoreria());
            beanCentro.setNombrePersonaCreditoCobranza(bean.getNombrePersonaCreditoCobranza());
            beanCentro.setNroDocumPersonaCreditoCobranza(bean.getNroDocumPersonaCreditoCobranza());
            beanCentro.setNombrePersonaCompra(bean.getNombrePersonaCompra());
            beanCentro.setOperacionesAfectas(bean.getOperacionesAfectas());
            beanCentro.setRazonSocial(bean.getRazonSocial());
            beanCentro.setRuc(bean.getRuc());
            beanCentro.setTelefono(bean.getTelefono());
            beanCentro.setTipoPersona(bean.getTipoPersona());
            beanCentro.setAcredorCodigoSap(bean.getAcredorCodigoSap());
            beanCentro.setActivo(bean.getActivo());
            beanCentro.setEmailRetencion(bean.getEmailRetencion());
            beanCentro.setEvalHomologacion(bean.getEvalHomologacion());
            beanCentro.setEvalDesempeno(bean.getEvalDesempeno());
            beanCentro.setFechaCreacion(bean.getFechaCreacion());
            beanCentro.setIdHcp(bean.getIdHcp());
            beanCentro.setTerritAmazonia(bean.getTerritAmazonia());
            beanCentro.setUsuarioCreacion(bean.getUsuarioCreacion());
            beanCentro.setIdEstadoProveedor(bean.getIdEstadoProveedor());
            beanCentro.setIndAceptacion(bean.getIndAceptacion());
            beanCentro.setIdBanco(bean.getIdBanco());
            beanCentro.setFechaModificacion(bean.getFechaModificacion());
            beanCentro.setIndDetraccion(bean.getIndDetraccion());



            //nuevos campos
            beanCentro.setIdTipoBeneficiario(bean.getIdTipoBeneficiario());
            beanCentro.setCuentaBeneficiario(bean.getCuentaBeneficiario());
            beanCentro.setNombreBeneficiario(bean.getNombreBeneficiario());
            beanCentro.setFlagCuentabancaria(bean.getFlagCuentabancaria());
            beanCentro.setDireccionBeneficiario(bean.getDireccionBeneficiario());
            beanCentro.setCiudadBeneficiario(bean.getCiudadBeneficiario());
            beanCentro.setReferenciaBeneficiario(bean.getReferenciaBeneficiario());
            beanCentro.setNombreBancoCtaExtranjero(bean.getNombreBancoCtaExtranjero());
            beanCentro.setCiudadBancoCtaExtranjero(bean.getCiudadBancoCtaExtranjero());
            beanCentro.setDireccionBancoCtaExtranjero(bean.getDireccionBancoCtaExtranjero());
            beanCentro.setTipoCodigoBancoCtaExtranjero(bean.getTipoCodigoBancoCtaExtranjero());
            beanCentro.setCodigoBancoCtaExtranjero(bean.getCodigoBancoCtaExtranjero());
            beanCentro.setPaisBeneficiario(bean.getPaisBeneficiario());
            beanCentro.setEstadoBeneficiario(bean.getEstadoBeneficiario());
            beanCentro.setPaisBancoExtranjero(bean.getPaisBancoExtranjero());
            beanCentro.setEstadoBancoExtranjero(bean.getEstadoBancoExtranjero());
            beanCentro.setIndEmiteRecibo(bean.getIndEmiteRecibo());

            proveedorMapperMybatis.getCreaProveedor(beanCentro);

            result.setIdProveedor(bean.getIdProveedor());
            result.setCargoPersonaTesoreria(bean.getCargoPersonaTesoreria());
            result.setCargoPersonaCreditoCobranza(bean.getCargoPersonaCreditoCobranza());
            result.setCargoRepresentanteLegal(bean.getCargoRepresentanteLegal());
            result.setCelular(bean.getCelular());
            result.setCelularPersonaCompra(bean.getCelularPersonaCompra());
            result.setContacto(bean.getContacto());
            result.setCodigoPostal(bean.getCodigoPostal());
            result.setDireccionFiscal(bean.getDireccionFiscal());
            result.setEmail(bean.getEmail());
            result.setBancoExtranjero(bean.getBancoExtranjero());
            result.setIdAreaCompra(bean.getIdAreaCompra());

            result.setEmailPersonaCompra(bean.getEmailPersonaCompra());
            result.setEmailPersonaCompra2(bean.getEmailPersonaCompra2());
            result.setEmailPersonaCompra3(bean.getEmailPersonaCompra3());
            result.setEmailPersonaCompra4(bean.getEmailPersonaCompra4());

            result.setEmailPersonaCreditoCobranza(bean.getEmailPersonaCreditoCobranza());
            result.setEmailPersonaCreditoCobranza2(bean.getEmailPersonaCreditoCobranza2());
            result.setEmailPersonaCreditoCobranza3(bean.getEmailPersonaCreditoCobranza3());
            result.setEmailPersonaCreditoCobranza4(bean.getEmailPersonaCreditoCobranza4());

            result.setEmailPersonaTesoreria(bean.getEmailPersonaTesoreria());
            result.setEmailPersonaTesoreria2(bean.getEmailPersonaTesoreria2());
            result.setEmailPersonaTesoreria3(bean.getEmailPersonaTesoreria3());
            result.setEmailPersonaTesoreria4(bean.getEmailPersonaTesoreria4());

            result.setEmailRepresentanteLegal(bean.getEmailRepresentanteLegal());
            result.setEmailRepresentanteLegal2(bean.getEmailRepresentanteLegal2());
            result.setEmailRepresentanteLegal3(bean.getEmailRepresentanteLegal3());
            result.setEmailRepresentanteLegal4(bean.getEmailRepresentanteLegal4());

            result.setIdPais(bean.getIdPais());
            result.setIdRegion(bean.getIdRegion());
            result.setIdProvincia(bean.getIdProvincia());
            result.setIdDistrito(bean.getIdDistrito());
            result.setIdTipoProveedor(bean.getIdTipoProveedor());
            result.setIdCondicionPago(bean.getIdCondicionPago());
            result.setIdMoneda(bean.getIdMoneda());
            result.setIdTipoComprobante(bean.getIdTipoComprobante());
            result.setIndTipoVentaBien(bean.getIndTipoVentaBien());
            result.setIndTipoVentaServicio(bean.getIndTipoVentaServicio());
            result.setIndTipoFacturacionManual(bean.getIndTipoFacturacionManual());
            result.setIndTipoFacturacionElect(bean.getIndTipoFacturacionElect());
            result.setNombreRepresentanteLegal(bean.getNombreRepresentanteLegal());
            result.setNroDocumRepresentanteLegal(bean.getNroDocumRepresentanteLegal());
            result.setNombrePersonaTesoreria(bean.getNombrePersonaTesoreria());
            result.setNroDocumPersonaTesoreria(bean.getNroDocumPersonaTesoreria());
            result.setNombrePersonaCreditoCobranza(bean.getNombrePersonaCreditoCobranza());
            result.setNroDocumPersonaCreditoCobranza(bean.getNroDocumPersonaCreditoCobranza());
            result.setNombrePersonaCompra(bean.getNombrePersonaCompra());
            result.setOperacionesAfectas(bean.getOperacionesAfectas());
            result.setRazonSocial(bean.getRazonSocial());
            result.setRuc(bean.getRuc());
            result.setTelefono(bean.getTelefono());
            result.setTipoPersona(bean.getTipoPersona());
            result.setAcredorCodigoSap(bean.getAcredorCodigoSap());
            result.setActivo(bean.getActivo());
            result.setEmailRetencion(bean.getEmailRetencion());
            result.setEvalHomologacion(bean.getEvalHomologacion());
            result.setEvalDesempeno(bean.getEvalDesempeno());
            result.setFechaCreacion(bean.getFechaCreacion());
            result.setIdHcp(bean.getIdHcp());
            result.setTerritAmazonia(bean.getTerritAmazonia());
            result.setUsuarioCreacion(bean.getUsuarioCreacion());
            result.setIdEstadoProveedor(bean.getIdEstadoProveedor());
            result.setIndAceptacion(bean.getIndAceptacion());
            result.setFechaCreacion(bean.getFechaModificacion());
            result.setIndEmiteRecibo(bean.getIndEmiteRecibo());



            //Actualiza Proveedor
            result.setIdTipoBeneficiario(bean.getIdTipoBeneficiario());
            result.setCuentaBeneficiario(bean.getCuentaBeneficiario());
            result.setNombreBeneficiario(bean.getNombreBeneficiario());
            result.setFlagCuentabancaria(bean.getFlagCuentabancaria());
            result.setDireccionBeneficiario(bean.getDireccionBeneficiario());
            result.setCiudadBeneficiario(bean.getCiudadBeneficiario());
            result.setReferenciaBeneficiario(bean.getReferenciaBeneficiario());
            result.setNombreBancoCtaExtranjero(bean.getNombreBancoCtaExtranjero());
            result.setCiudadBancoCtaExtranjero(bean.getCiudadBancoCtaExtranjero());
            result.setDireccionBancoCtaExtranjero(bean.getDireccionBancoCtaExtranjero());
            result.setTipoCodigoBancoCtaExtranjero(bean.getTipoCodigoBancoCtaExtranjero());
            result.setCodigoBancoCtaExtranjero(bean.getCodigoBancoCtaExtranjero());
            result.setIdPaisBeneficiario(bean.getPaisBeneficiario());
            result.setIdEstadoBeneficiario(bean.getEstadoBeneficiario());
            result.setIdPaisBancoExtranjero(bean.getPaisBancoExtranjero());
            result.setIdEstadoBancoExtranjero(bean.getEstadoBancoExtranjero());
            result.setIdBanco(bean.getIdBanco());
            result.setIndDetraccion(bean.getIndDetraccion());

        }else{
            logger.info("PROVEEDOR SI EXISTE");
            if(bean.getIdEstadoProveedor() == 3){ //DATA RECHAZADA
                beanCentro.setIdEstadoProveedor(6); // DATA EN EVALUACION
            }
            else if(bean.getIdEstadoProveedor() == 1){
                beanCentro.setIdEstadoProveedor(6); // DATA EN EVALUACION
            }
            else if(bean.getIdEstadoProveedor() == 4){ //homologado
                if(bean.getIndDeclaracion() == 1){ //si viene nueva PN y PJ
                    beanCentro.setIdEstadoProveedor(6); // DATA EN EVALUACION
                }else{
                    beanCentro.setIdEstadoProveedor(bean.getIdEstadoProveedor());//homologado
                }
            }
            else{
                beanCentro.setIdEstadoProveedor(bean.getIdEstadoProveedor());
            }
            beanCentro.setIdProveedor(proveedorExiste.getIdProveedor());
            beanCentro.setCargoPersonaTesoreria(bean.getCargoPersonaTesoreria());
            beanCentro.setCargoPersonaCreditoCobranza(bean.getCargoPersonaCreditoCobranza());
            beanCentro.setCargoRepresentanteLegal(bean.getCargoRepresentanteLegal());
            beanCentro.setCelular(bean.getCelular());
            beanCentro.setCelularPersonaCompra(bean.getCelularPersonaCompra());
            beanCentro.setContacto(bean.getContacto());
            beanCentro.setCodigoPostal(bean.getCodigoPostal());
            beanCentro.setDireccionFiscal(bean.getDireccionFiscal());
            beanCentro.setEmail(bean.getEmail());
            beanCentro.setIdAreaCompra(bean.getIdAreaCompra());

            beanCentro.setEmailPersonaCompra(bean.getEmailPersonaCompra());
            beanCentro.setEmailPersonaCompra2(bean.getEmailPersonaCompra2());
            beanCentro.setEmailPersonaCompra3(bean.getEmailPersonaCompra3());
            beanCentro.setEmailPersonaCompra4(bean.getEmailPersonaCompra4());

            beanCentro.setEmailPersonaCreditoCobranza(bean.getEmailPersonaCreditoCobranza());
            beanCentro.setEmailPersonaCreditoCobranza2(bean.getEmailPersonaCreditoCobranza2());
            beanCentro.setEmailPersonaCreditoCobranza3(bean.getEmailPersonaCreditoCobranza3());
            beanCentro.setEmailPersonaCreditoCobranza4(bean.getEmailPersonaCreditoCobranza4());

            beanCentro.setEmailPersonaTesoreria(bean.getEmailPersonaTesoreria());
            beanCentro.setEmailPersonaTesoreria2(bean.getEmailPersonaTesoreria2());
            beanCentro.setEmailPersonaTesoreria3(bean.getEmailPersonaTesoreria3());
            beanCentro.setEmailPersonaTesoreria4(bean.getEmailPersonaTesoreria4());

            beanCentro.setEmailRepresentanteLegal(bean.getEmailRepresentanteLegal());
            beanCentro.setEmailRepresentanteLegal2(bean.getEmailRepresentanteLegal2());
            beanCentro.setEmailRepresentanteLegal3(bean.getEmailRepresentanteLegal3());
            beanCentro.setEmailRepresentanteLegal4(bean.getEmailRepresentanteLegal4());

            beanCentro.setIdPais(bean.getIdPais());
            beanCentro.setIdRegion(bean.getIdRegion());
            beanCentro.setBancoExtranjero(bean.getBancoExtranjero());
            beanCentro.setIdProvincia(bean.getIdProvincia());
            beanCentro.setIdDistrito(bean.getIdDistrito());
            beanCentro.setIdTipoProveedor(bean.getIdTipoProveedor());
            beanCentro.setIdCondicionPago(bean.getIdCondicionPago());
            beanCentro.setIdMoneda(bean.getIdMoneda());
            beanCentro.setIdTipoComprobante(bean.getIdTipoComprobante());
            beanCentro.setIndTipoVentaBien(bean.getIndTipoVentaBien());
            beanCentro.setIndTipoVentaServicio(bean.getIndTipoVentaServicio());
            beanCentro.setIndTipoFacturacionManual(bean.getIndTipoFacturacionManual());
            beanCentro.setIndTipoFacturacionElect(bean.getIndTipoFacturacionElect());
            beanCentro.setNombreRepresentanteLegal(bean.getNombreRepresentanteLegal());
            beanCentro.setNroDocumRepresentanteLegal(bean.getNroDocumRepresentanteLegal());
            beanCentro.setNombrePersonaTesoreria(bean.getNombrePersonaTesoreria());
            beanCentro.setNroDocumPersonaTesoreria(bean.getNroDocumPersonaTesoreria());
            beanCentro.setNombrePersonaCreditoCobranza(bean.getNombrePersonaCreditoCobranza());
            beanCentro.setNroDocumPersonaCreditoCobranza(bean.getNroDocumPersonaCreditoCobranza());
            beanCentro.setNombrePersonaCompra(bean.getNombrePersonaCompra());
            beanCentro.setOperacionesAfectas(bean.getOperacionesAfectas());
            beanCentro.setRazonSocial(bean.getRazonSocial());
            beanCentro.setRuc(bean.getRuc());
            beanCentro.setTelefono(bean.getTelefono());
            beanCentro.setTipoPersona(bean.getTipoPersona());
            beanCentro.setAcredorCodigoSap(bean.getAcredorCodigoSap());
            beanCentro.setActivo(bean.getActivo());
            beanCentro.setEmailRetencion(bean.getEmailRetencion());
            beanCentro.setEvalHomologacion(bean.getEvalHomologacion());
            beanCentro.setEvalDesempeno(bean.getEvalDesempeno());
            beanCentro.setFechaCreacion(bean.getFechaCreacion());
            beanCentro.setFechaModificacion(DateUtils.getCurrentTimestamp());
            beanCentro.setIdHcp(bean.getIdHcp());
            beanCentro.setTerritAmazonia(bean.getTerritAmazonia());
            beanCentro.setUsuarioCreacion(bean.getUsuarioCreacion());
            beanCentro.setIndAceptacion(bean.getIndAceptacion());
            beanCentro.setIdTipoBeneficiario(bean.getIdTipoBeneficiario());
            beanCentro.setCuentaBeneficiario(bean.getCuentaBeneficiario());
            beanCentro.setNombreBeneficiario(bean.getNombreBeneficiario());
            beanCentro.setFlagCuentabancaria(bean.getFlagCuentabancaria());
            beanCentro.setDireccionBeneficiario(bean.getDireccionBeneficiario());
            beanCentro.setCiudadBeneficiario(bean.getCiudadBeneficiario());
            beanCentro.setReferenciaBeneficiario(bean.getReferenciaBeneficiario());
            beanCentro.setNombreBancoCtaExtranjero(bean.getNombreBancoCtaExtranjero());
            beanCentro.setCiudadBancoCtaExtranjero(bean.getCiudadBancoCtaExtranjero());
            beanCentro.setDireccionBancoCtaExtranjero(bean.getDireccionBancoCtaExtranjero());
            beanCentro.setTipoCodigoBancoCtaExtranjero(bean.getTipoCodigoBancoCtaExtranjero());
            beanCentro.setCodigoBancoCtaExtranjero(bean.getCodigoBancoCtaExtranjero());
            beanCentro.setPaisBeneficiario(bean.getPaisBeneficiario());
            beanCentro.setEstadoBeneficiario(bean.getEstadoBeneficiario());
            beanCentro.setPaisBancoExtranjero(bean.getPaisBancoExtranjero());
            beanCentro.setEstadoBancoExtranjero(bean.getEstadoBancoExtranjero());
            beanCentro.setIdBanco(bean.getIdBanco());
            beanCentro.setIndDetraccion(bean.getIndDetraccion());
            beanCentro.setIndEmiteRecibo(bean.getIndEmiteRecibo());

            proveedorMapperMybatis.getActualizaProveedor(beanCentro);

            //crear-actualizar ficha proveedor onBase
            //this.cmisBaseServicecf.crearFichaProveedor(beanCentro);

            result.setCargoPersonaTesoreria(bean.getCargoPersonaTesoreria());
            result.setCargoPersonaCreditoCobranza(bean.getCargoPersonaCreditoCobranza());
            result.setCargoRepresentanteLegal(bean.getCargoRepresentanteLegal());
            result.setCelular(bean.getCelular());
            result.setCelularPersonaCompra(bean.getCelularPersonaCompra());
            result.setContacto(bean.getContacto());
            result.setCodigoPostal(bean.getCodigoPostal());
            result.setDireccionFiscal(bean.getDireccionFiscal());
            result.setEmail(bean.getEmail());
            result.setBancoExtranjero(bean.getBancoExtranjero());
            result.setIdAreaCompra(bean.getIdAreaCompra());

            result.setEmailPersonaCompra(bean.getEmailPersonaCompra());
            result.setEmailPersonaCompra2(bean.getEmailPersonaCompra2());
            result.setEmailPersonaCompra3(bean.getEmailPersonaCompra3());
            result.setEmailPersonaCompra4(bean.getEmailPersonaCompra4());

            result.setEmailPersonaCreditoCobranza(bean.getEmailPersonaCreditoCobranza());
            result.setEmailPersonaCreditoCobranza2(bean.getEmailPersonaCreditoCobranza2());
            result.setEmailPersonaCreditoCobranza3(bean.getEmailPersonaCreditoCobranza3());
            result.setEmailPersonaCreditoCobranza4(bean.getEmailPersonaCreditoCobranza4());

            result.setEmailPersonaTesoreria(bean.getEmailPersonaTesoreria());
            result.setEmailPersonaTesoreria2(bean.getEmailPersonaTesoreria2());
            result.setEmailPersonaTesoreria3(bean.getEmailPersonaTesoreria3());
            result.setEmailPersonaTesoreria4(bean.getEmailPersonaTesoreria4());

            result.setEmailRepresentanteLegal(bean.getEmailRepresentanteLegal());
            result.setEmailRepresentanteLegal2(bean.getEmailRepresentanteLegal2());
            result.setEmailRepresentanteLegal3(bean.getEmailRepresentanteLegal3());
            result.setEmailRepresentanteLegal4(bean.getEmailRepresentanteLegal4());

            result.setIdPais(bean.getIdPais());
            result.setIdRegion(bean.getIdRegion());
            result.setIdProvincia(bean.getIdProvincia());
            result.setIdDistrito(bean.getIdDistrito());
            result.setIdTipoProveedor(bean.getIdTipoProveedor());
            result.setIdCondicionPago(bean.getIdCondicionPago());
            result.setIdMoneda(bean.getIdMoneda());
            result.setIdTipoComprobante(bean.getIdTipoComprobante());
            result.setIndTipoVentaBien(bean.getIndTipoVentaBien());
            result.setIndTipoVentaServicio(bean.getIndTipoVentaServicio());
            result.setIndTipoFacturacionManual(bean.getIndTipoFacturacionManual());
            result.setIndTipoFacturacionElect(bean.getIndTipoFacturacionElect());
            result.setNombreRepresentanteLegal(bean.getNombreRepresentanteLegal());
            result.setNroDocumRepresentanteLegal(bean.getNroDocumRepresentanteLegal());
            result.setNombrePersonaTesoreria(bean.getNombrePersonaTesoreria());
            result.setNroDocumPersonaTesoreria(bean.getNroDocumPersonaTesoreria());
            result.setNombrePersonaCreditoCobranza(bean.getNombrePersonaCreditoCobranza());
            result.setNroDocumPersonaCreditoCobranza(bean.getNroDocumPersonaCreditoCobranza());
            result.setNombrePersonaCompra(bean.getNombrePersonaCompra());
            result.setOperacionesAfectas(bean.getOperacionesAfectas());
            result.setRazonSocial(bean.getRazonSocial());
            result.setRuc(bean.getRuc());
            result.setTelefono(bean.getTelefono());
            result.setTipoPersona(bean.getTipoPersona());
            result.setAcredorCodigoSap(bean.getAcredorCodigoSap());
            result.setActivo(bean.getActivo());
            result.setEmailRetencion(bean.getEmailRetencion());
            result.setEvalHomologacion(bean.getEvalHomologacion());
            result.setEvalDesempeno(bean.getEvalDesempeno());
            result.setFechaCreacion(bean.getFechaCreacion());
            result.setFechaModificacion(DateUtils.getCurrentTimestamp());
            result.setIdHcp(bean.getIdHcp());
            result.setTerritAmazonia(bean.getTerritAmazonia());
            result.setUsuarioCreacion(bean.getUsuarioCreacion());
            result.setIndAceptacion(bean.getIndAceptacion());
            result.setIdTipoBeneficiario(bean.getIdTipoBeneficiario());
            result.setCuentaBeneficiario(bean.getCuentaBeneficiario());
            result.setNombreBeneficiario(bean.getNombreBeneficiario());
            result.setFlagCuentabancaria(bean.getFlagCuentabancaria());
            result.setDireccionBeneficiario(bean.getDireccionBeneficiario());
            result.setCiudadBeneficiario(bean.getCiudadBeneficiario());
            result.setReferenciaBeneficiario(bean.getReferenciaBeneficiario());
            result.setNombreBancoCtaExtranjero(bean.getNombreBancoCtaExtranjero());
            result.setCiudadBancoCtaExtranjero(bean.getCiudadBancoCtaExtranjero());
            result.setDireccionBancoCtaExtranjero(bean.getDireccionBancoCtaExtranjero());
            result.setTipoCodigoBancoCtaExtranjero(bean.getTipoCodigoBancoCtaExtranjero());
            result.setCodigoBancoCtaExtranjero(bean.getCodigoBancoCtaExtranjero());
            result.setIdPaisBeneficiario(bean.getPaisBeneficiario());
            result.setIdEstadoBeneficiario(bean.getEstadoBeneficiario());
            result.setIdPaisBancoExtranjero(bean.getPaisBancoExtranjero());
            result.setIdEstadoBancoExtranjero(bean.getEstadoBancoExtranjero());
            result.setIdBanco(bean.getIdBanco());
            result.setIndDetraccion(bean.getIndDetraccion());
            result.setIndEmiteRecibo(bean.getIndEmiteRecibo());
        }

        return result;
    }

    @Override
    public ProveedorOutDto inspeccionProveedorParcial(ProveedorInDto bean) throws Exception {
        ProveedorOutDto result = new ProveedorOutDto();
        ProveedorInDto beanCentro = new ProveedorInDto();
        ProveedorOutDto proveedorExiste = new ProveedorOutDto();

        beanCentro.setEmail(bean.getEmail());

        //Valida Proveedor
        proveedorExiste = proveedorMapperMybatis.validarProveedorCorreo(beanCentro.getEmail());

        //Crea Proveedor
        if(proveedorExiste == null){
            logger.error("PROVEEDOR NO EXISTE");
            beanCentro.setIdProveedor(bean.getIdProveedor());
            beanCentro.setCargoPersonaTesoreria(bean.getCargoPersonaTesoreria());
            beanCentro.setCargoPersonaCreditoCobranza(bean.getCargoPersonaCreditoCobranza());
            beanCentro.setCargoRepresentanteLegal(bean.getCargoRepresentanteLegal());
            beanCentro.setCelular(bean.getCelular());
            beanCentro.setCelularPersonaCompra(bean.getCelularPersonaCompra());
            beanCentro.setContacto(bean.getContacto());
            beanCentro.setCodigoPostal(bean.getCodigoPostal());
            beanCentro.setDireccionFiscal(bean.getDireccionFiscal());
            beanCentro.setEmail(bean.getEmail());
            beanCentro.setBancoExtranjero(bean.getBancoExtranjero());
            beanCentro.setIdAreaCompra(bean.getIdAreaCompra());

            beanCentro.setEmailPersonaCompra(bean.getEmailPersonaCompra());
            beanCentro.setEmailPersonaCompra2(bean.getEmailPersonaCompra2());
            beanCentro.setEmailPersonaCompra3(bean.getEmailPersonaCompra3());
            beanCentro.setEmailPersonaCompra4(bean.getEmailPersonaCompra4());

            beanCentro.setEmailPersonaCreditoCobranza(bean.getEmailPersonaCreditoCobranza());
            beanCentro.setEmailPersonaCreditoCobranza2(bean.getEmailPersonaCreditoCobranza2());
            beanCentro.setEmailPersonaCreditoCobranza3(bean.getEmailPersonaCreditoCobranza3());
            beanCentro.setEmailPersonaCreditoCobranza4(bean.getEmailPersonaCreditoCobranza4());

            beanCentro.setEmailPersonaTesoreria(bean.getEmailPersonaTesoreria());
            beanCentro.setEmailPersonaTesoreria2(bean.getEmailPersonaTesoreria2());
            beanCentro.setEmailPersonaTesoreria3(bean.getEmailPersonaTesoreria3());
            beanCentro.setEmailPersonaTesoreria4(bean.getEmailPersonaTesoreria4());

            beanCentro.setEmailRepresentanteLegal(bean.getEmailRepresentanteLegal());
            beanCentro.setEmailRepresentanteLegal2(bean.getEmailRepresentanteLegal2());
            beanCentro.setEmailRepresentanteLegal3(bean.getEmailRepresentanteLegal3());
            beanCentro.setEmailRepresentanteLegal4(bean.getEmailRepresentanteLegal4());

            beanCentro.setIdPais(bean.getIdPais());
            beanCentro.setIdRegion(bean.getIdRegion());
            beanCentro.setIdProvincia(bean.getIdProvincia());
            beanCentro.setIdDistrito(bean.getIdDistrito());
            beanCentro.setIdTipoProveedor(bean.getIdTipoProveedor());
            beanCentro.setIdCondicionPago(bean.getIdCondicionPago());
            beanCentro.setIdMoneda(bean.getIdMoneda());
            beanCentro.setIdTipoComprobante(bean.getIdTipoComprobante());
            beanCentro.setIndTipoVentaBien(bean.getIndTipoVentaBien());
            beanCentro.setIndTipoVentaServicio(bean.getIndTipoVentaServicio());
            beanCentro.setIndTipoFacturacionManual(bean.getIndTipoFacturacionManual());
            beanCentro.setIndTipoFacturacionElect(bean.getIndTipoFacturacionElect());
            beanCentro.setNombreRepresentanteLegal(bean.getNombreRepresentanteLegal());
            beanCentro.setNroDocumRepresentanteLegal(bean.getNroDocumRepresentanteLegal());
            beanCentro.setNombrePersonaTesoreria(bean.getNombrePersonaTesoreria());
            beanCentro.setNroDocumPersonaTesoreria(bean.getNroDocumPersonaTesoreria());
            beanCentro.setNombrePersonaCreditoCobranza(bean.getNombrePersonaCreditoCobranza());
            beanCentro.setNroDocumPersonaCreditoCobranza(bean.getNroDocumPersonaCreditoCobranza());
            beanCentro.setNombrePersonaCompra(bean.getNombrePersonaCompra());
            beanCentro.setOperacionesAfectas(bean.getOperacionesAfectas());
            beanCentro.setRazonSocial(bean.getRazonSocial());
            beanCentro.setRuc(bean.getRuc());
            beanCentro.setTelefono(bean.getTelefono());
            beanCentro.setTipoPersona(bean.getTipoPersona());
            beanCentro.setAcredorCodigoSap(bean.getAcredorCodigoSap());
            beanCentro.setActivo(bean.getActivo());
            beanCentro.setEmailRetencion(bean.getEmailRetencion());
            beanCentro.setEvalHomologacion(bean.getEvalHomologacion());
            beanCentro.setEvalDesempeno(bean.getEvalDesempeno());
            beanCentro.setFechaCreacion(bean.getFechaCreacion());
            beanCentro.setIdHcp(bean.getIdHcp());
            beanCentro.setTerritAmazonia(bean.getTerritAmazonia());
            beanCentro.setUsuarioCreacion(bean.getUsuarioCreacion());
            beanCentro.setIdEstadoProveedor(bean.getIdEstadoProveedor());
            beanCentro.setIndAceptacion(bean.getIndAceptacion());
            beanCentro.setIdBanco(bean.getIdBanco());
            beanCentro.setFechaModificacion(bean.getFechaModificacion());



            //nuevos campos
            beanCentro.setIdTipoBeneficiario(bean.getIdTipoBeneficiario());
            beanCentro.setCuentaBeneficiario(bean.getCuentaBeneficiario());
            beanCentro.setNombreBeneficiario(bean.getNombreBeneficiario());
            beanCentro.setFlagCuentabancaria(bean.getFlagCuentabancaria());
            beanCentro.setDireccionBeneficiario(bean.getDireccionBeneficiario());
            beanCentro.setCiudadBeneficiario(bean.getCiudadBeneficiario());
            beanCentro.setReferenciaBeneficiario(bean.getReferenciaBeneficiario());
            beanCentro.setNombreBancoCtaExtranjero(bean.getNombreBancoCtaExtranjero());
            beanCentro.setCiudadBancoCtaExtranjero(bean.getCiudadBancoCtaExtranjero());
            beanCentro.setDireccionBancoCtaExtranjero(bean.getDireccionBancoCtaExtranjero());
            beanCentro.setTipoCodigoBancoCtaExtranjero(bean.getTipoCodigoBancoCtaExtranjero());
            beanCentro.setCodigoBancoCtaExtranjero(bean.getCodigoBancoCtaExtranjero());
            beanCentro.setPaisBeneficiario(bean.getPaisBeneficiario());
            beanCentro.setEstadoBeneficiario(bean.getEstadoBeneficiario());
            beanCentro.setPaisBancoExtranjero(bean.getPaisBancoExtranjero());
            beanCentro.setEstadoBancoExtranjero(bean.getEstadoBancoExtranjero());
            beanCentro.setIndDetraccion(bean.getIndDetraccion());
            beanCentro.setIndEmiteRecibo(bean.getIndEmiteRecibo());


            proveedorMapperMybatis.getCreaProveedor(beanCentro);

            result.setIdProveedor(bean.getIdProveedor());
            result.setCargoPersonaTesoreria(bean.getCargoPersonaTesoreria());
            result.setCargoPersonaCreditoCobranza(bean.getCargoPersonaCreditoCobranza());
            result.setCargoRepresentanteLegal(bean.getCargoRepresentanteLegal());
            result.setCelular(bean.getCelular());
            result.setCelularPersonaCompra(bean.getCelularPersonaCompra());
            result.setContacto(bean.getContacto());
            result.setCodigoPostal(bean.getCodigoPostal());
            result.setDireccionFiscal(bean.getDireccionFiscal());
            result.setEmail(bean.getEmail());
            result.setBancoExtranjero(bean.getBancoExtranjero());
            result.setIdAreaCompra(bean.getIdAreaCompra());

            result.setEmailPersonaCompra(bean.getEmailPersonaCompra());
            result.setEmailPersonaCompra2(bean.getEmailPersonaCompra2());
            result.setEmailPersonaCompra3(bean.getEmailPersonaCompra3());
            result.setEmailPersonaCompra4(bean.getEmailPersonaCompra4());

            result.setEmailPersonaCreditoCobranza(bean.getEmailPersonaCreditoCobranza());
            result.setEmailPersonaCreditoCobranza2(bean.getEmailPersonaCreditoCobranza2());
            result.setEmailPersonaCreditoCobranza3(bean.getEmailPersonaCreditoCobranza3());
            result.setEmailPersonaCreditoCobranza4(bean.getEmailPersonaCreditoCobranza4());

            result.setEmailPersonaTesoreria(bean.getEmailPersonaTesoreria());
            result.setEmailPersonaTesoreria2(bean.getEmailPersonaTesoreria2());
            result.setEmailPersonaTesoreria3(bean.getEmailPersonaTesoreria3());
            result.setEmailPersonaTesoreria4(bean.getEmailPersonaTesoreria4());

            result.setEmailRepresentanteLegal(bean.getEmailRepresentanteLegal());
            result.setEmailRepresentanteLegal2(bean.getEmailRepresentanteLegal2());
            result.setEmailRepresentanteLegal3(bean.getEmailRepresentanteLegal3());
            result.setEmailRepresentanteLegal4(bean.getEmailRepresentanteLegal4());

            result.setIdPais(bean.getIdPais());
            result.setIdRegion(bean.getIdRegion());
            result.setIdProvincia(bean.getIdProvincia());
            result.setIdDistrito(bean.getIdDistrito());
            result.setIdTipoProveedor(bean.getIdTipoProveedor());
            result.setIdCondicionPago(bean.getIdCondicionPago());
            result.setIdMoneda(bean.getIdMoneda());
            result.setIdTipoComprobante(bean.getIdTipoComprobante());
            result.setIndTipoVentaBien(bean.getIndTipoVentaBien());
            result.setIndTipoVentaServicio(bean.getIndTipoVentaServicio());
            result.setIndTipoFacturacionManual(bean.getIndTipoFacturacionManual());
            result.setIndTipoFacturacionElect(bean.getIndTipoFacturacionElect());
            result.setNombreRepresentanteLegal(bean.getNombreRepresentanteLegal());
            result.setNroDocumRepresentanteLegal(bean.getNroDocumRepresentanteLegal());
            result.setNombrePersonaTesoreria(bean.getNombrePersonaTesoreria());
            result.setNroDocumPersonaTesoreria(bean.getNroDocumPersonaTesoreria());
            result.setNombrePersonaCreditoCobranza(bean.getNombrePersonaCreditoCobranza());
            result.setNroDocumPersonaCreditoCobranza(bean.getNroDocumPersonaCreditoCobranza());
            result.setNombrePersonaCompra(bean.getNombrePersonaCompra());
            result.setOperacionesAfectas(bean.getOperacionesAfectas());
            result.setRazonSocial(bean.getRazonSocial());
            result.setRuc(bean.getRuc());
            result.setTelefono(bean.getTelefono());
            result.setTipoPersona(bean.getTipoPersona());
            result.setAcredorCodigoSap(bean.getAcredorCodigoSap());
            result.setActivo(bean.getActivo());
            result.setEmailRetencion(bean.getEmailRetencion());
            result.setEvalHomologacion(bean.getEvalHomologacion());
            result.setEvalDesempeno(bean.getEvalDesempeno());
            result.setFechaCreacion(bean.getFechaCreacion());
            result.setIdHcp(bean.getIdHcp());
            result.setTerritAmazonia(bean.getTerritAmazonia());
            result.setUsuarioCreacion(bean.getUsuarioCreacion());
            result.setIdEstadoProveedor(bean.getIdEstadoProveedor());
            result.setIndAceptacion(bean.getIndAceptacion());
            result.setFechaCreacion(bean.getFechaModificacion());




            //Actualiza Proveedor
            result.setIdTipoBeneficiario(bean.getIdTipoBeneficiario());
            result.setCuentaBeneficiario(bean.getCuentaBeneficiario());
            result.setNombreBeneficiario(bean.getNombreBeneficiario());
            result.setFlagCuentabancaria(bean.getFlagCuentabancaria());
            result.setDireccionBeneficiario(bean.getDireccionBeneficiario());
            result.setCiudadBeneficiario(bean.getCiudadBeneficiario());
            result.setReferenciaBeneficiario(bean.getReferenciaBeneficiario());
            result.setNombreBancoCtaExtranjero(bean.getNombreBancoCtaExtranjero());
            result.setCiudadBancoCtaExtranjero(bean.getCiudadBancoCtaExtranjero());
            result.setDireccionBancoCtaExtranjero(bean.getDireccionBancoCtaExtranjero());
            result.setTipoCodigoBancoCtaExtranjero(bean.getTipoCodigoBancoCtaExtranjero());
            result.setCodigoBancoCtaExtranjero(bean.getCodigoBancoCtaExtranjero());
            result.setIdPaisBeneficiario(bean.getPaisBeneficiario());
            result.setIdEstadoBeneficiario(bean.getEstadoBeneficiario());
            result.setIdPaisBancoExtranjero(bean.getPaisBancoExtranjero());
            result.setIdEstadoBancoExtranjero(bean.getEstadoBancoExtranjero());
            result.setIdBanco(bean.getIdBanco());
            result.setIndDetraccion(bean.getIndDetraccion());
            result.setIndEmiteRecibo(bean.getIndEmiteRecibo());
        }else{
            logger.info("PROVEEDOR SI EXISTE");
            beanCentro.setIdEstadoProveedor(bean.getIdEstadoProveedor());
            beanCentro.setIdProveedor(proveedorExiste.getIdProveedor());
            beanCentro.setCargoPersonaTesoreria(bean.getCargoPersonaTesoreria());
            beanCentro.setCargoPersonaCreditoCobranza(bean.getCargoPersonaCreditoCobranza());
            beanCentro.setCargoRepresentanteLegal(bean.getCargoRepresentanteLegal());
            beanCentro.setCelular(bean.getCelular());
            beanCentro.setCelularPersonaCompra(bean.getCelularPersonaCompra());
            beanCentro.setContacto(bean.getContacto());
            beanCentro.setCodigoPostal(bean.getCodigoPostal());
            beanCentro.setDireccionFiscal(bean.getDireccionFiscal());
            beanCentro.setEmail(bean.getEmail());
            beanCentro.setIdAreaCompra(bean.getIdAreaCompra());

            beanCentro.setEmailPersonaCompra(bean.getEmailPersonaCompra());
            beanCentro.setEmailPersonaCompra2(bean.getEmailPersonaCompra2());
            beanCentro.setEmailPersonaCompra3(bean.getEmailPersonaCompra3());
            beanCentro.setEmailPersonaCompra4(bean.getEmailPersonaCompra4());

            beanCentro.setEmailPersonaCreditoCobranza(bean.getEmailPersonaCreditoCobranza());
            beanCentro.setEmailPersonaCreditoCobranza2(bean.getEmailPersonaCreditoCobranza2());
            beanCentro.setEmailPersonaCreditoCobranza3(bean.getEmailPersonaCreditoCobranza3());
            beanCentro.setEmailPersonaCreditoCobranza4(bean.getEmailPersonaCreditoCobranza4());

            beanCentro.setEmailPersonaTesoreria(bean.getEmailPersonaTesoreria());
            beanCentro.setEmailPersonaTesoreria2(bean.getEmailPersonaTesoreria2());
            beanCentro.setEmailPersonaTesoreria3(bean.getEmailPersonaTesoreria3());
            beanCentro.setEmailPersonaTesoreria4(bean.getEmailPersonaTesoreria4());

            beanCentro.setEmailRepresentanteLegal(bean.getEmailRepresentanteLegal());
            beanCentro.setEmailRepresentanteLegal2(bean.getEmailRepresentanteLegal2());
            beanCentro.setEmailRepresentanteLegal3(bean.getEmailRepresentanteLegal3());
            beanCentro.setEmailRepresentanteLegal4(bean.getEmailRepresentanteLegal4());

            beanCentro.setIdPais(bean.getIdPais());
            beanCentro.setIdRegion(bean.getIdRegion());
            beanCentro.setBancoExtranjero(bean.getBancoExtranjero());
            beanCentro.setIdProvincia(bean.getIdProvincia());
            beanCentro.setIdDistrito(bean.getIdDistrito());
            beanCentro.setIdTipoProveedor(bean.getIdTipoProveedor());
            beanCentro.setIdCondicionPago(bean.getIdCondicionPago());
            beanCentro.setIdMoneda(bean.getIdMoneda());
            beanCentro.setIdTipoComprobante(bean.getIdTipoComprobante());
            beanCentro.setIndTipoVentaBien(bean.getIndTipoVentaBien());
            beanCentro.setIndTipoVentaServicio(bean.getIndTipoVentaServicio());
            beanCentro.setIndTipoFacturacionManual(bean.getIndTipoFacturacionManual());
            beanCentro.setIndTipoFacturacionElect(bean.getIndTipoFacturacionElect());
            beanCentro.setNombreRepresentanteLegal(bean.getNombreRepresentanteLegal());
            beanCentro.setNroDocumRepresentanteLegal(bean.getNroDocumRepresentanteLegal());
            beanCentro.setNombrePersonaTesoreria(bean.getNombrePersonaTesoreria());
            beanCentro.setNroDocumPersonaTesoreria(bean.getNroDocumPersonaTesoreria());
            beanCentro.setNombrePersonaCreditoCobranza(bean.getNombrePersonaCreditoCobranza());
            beanCentro.setNroDocumPersonaCreditoCobranza(bean.getNroDocumPersonaCreditoCobranza());
            beanCentro.setNombrePersonaCompra(bean.getNombrePersonaCompra());
            beanCentro.setOperacionesAfectas(bean.getOperacionesAfectas());
            beanCentro.setRazonSocial(bean.getRazonSocial());
            beanCentro.setRuc(bean.getRuc());
            beanCentro.setTelefono(bean.getTelefono());
            beanCentro.setTipoPersona(bean.getTipoPersona());
            beanCentro.setAcredorCodigoSap(bean.getAcredorCodigoSap());
            beanCentro.setActivo(bean.getActivo());
            beanCentro.setEmailRetencion(bean.getEmailRetencion());
            beanCentro.setEvalHomologacion(bean.getEvalHomologacion());
            beanCentro.setEvalDesempeno(bean.getEvalDesempeno());
            beanCentro.setFechaCreacion(bean.getFechaCreacion());
            beanCentro.setFechaModificacion(DateUtils.getCurrentTimestamp());
            beanCentro.setIdHcp(bean.getIdHcp());
            beanCentro.setTerritAmazonia(bean.getTerritAmazonia());
            beanCentro.setUsuarioCreacion(bean.getUsuarioCreacion());
            beanCentro.setIndAceptacion(bean.getIndAceptacion());
            beanCentro.setIdTipoBeneficiario(bean.getIdTipoBeneficiario());
            beanCentro.setCuentaBeneficiario(bean.getCuentaBeneficiario());
            beanCentro.setNombreBeneficiario(bean.getNombreBeneficiario());
            beanCentro.setFlagCuentabancaria(bean.getFlagCuentabancaria());
            beanCentro.setDireccionBeneficiario(bean.getDireccionBeneficiario());
            beanCentro.setCiudadBeneficiario(bean.getCiudadBeneficiario());
            beanCentro.setReferenciaBeneficiario(bean.getReferenciaBeneficiario());
            beanCentro.setNombreBancoCtaExtranjero(bean.getNombreBancoCtaExtranjero());
            beanCentro.setCiudadBancoCtaExtranjero(bean.getCiudadBancoCtaExtranjero());
            beanCentro.setDireccionBancoCtaExtranjero(bean.getDireccionBancoCtaExtranjero());
            beanCentro.setTipoCodigoBancoCtaExtranjero(bean.getTipoCodigoBancoCtaExtranjero());
            beanCentro.setCodigoBancoCtaExtranjero(bean.getCodigoBancoCtaExtranjero());
            beanCentro.setPaisBeneficiario(bean.getPaisBeneficiario());
            beanCentro.setEstadoBeneficiario(bean.getEstadoBeneficiario());
            beanCentro.setPaisBancoExtranjero(bean.getPaisBancoExtranjero());
            beanCentro.setEstadoBancoExtranjero(bean.getEstadoBancoExtranjero());
            beanCentro.setIdBanco(bean.getIdBanco());
            beanCentro.setIndDetraccion(bean.getIndDetraccion());
            beanCentro.setIndEmiteRecibo(bean.getIndEmiteRecibo());

            proveedorMapperMybatis.getActualizaProveedor(beanCentro);


            result.setCargoPersonaTesoreria(bean.getCargoPersonaTesoreria());
            result.setCargoPersonaCreditoCobranza(bean.getCargoPersonaCreditoCobranza());
            result.setCargoRepresentanteLegal(bean.getCargoRepresentanteLegal());
            result.setCelular(bean.getCelular());
            result.setCelularPersonaCompra(bean.getCelularPersonaCompra());
            result.setContacto(bean.getContacto());
            result.setCodigoPostal(bean.getCodigoPostal());
            result.setDireccionFiscal(bean.getDireccionFiscal());
            result.setEmail(bean.getEmail());
            result.setBancoExtranjero(bean.getBancoExtranjero());
            result.setIdAreaCompra(bean.getIdAreaCompra());

            result.setEmailPersonaCompra(bean.getEmailPersonaCompra());
            result.setEmailPersonaCompra2(bean.getEmailPersonaCompra2());
            result.setEmailPersonaCompra3(bean.getEmailPersonaCompra3());
            result.setEmailPersonaCompra4(bean.getEmailPersonaCompra4());

            result.setEmailPersonaCreditoCobranza(bean.getEmailPersonaCreditoCobranza());
            result.setEmailPersonaCreditoCobranza2(bean.getEmailPersonaCreditoCobranza2());
            result.setEmailPersonaCreditoCobranza3(bean.getEmailPersonaCreditoCobranza3());
            result.setEmailPersonaCreditoCobranza4(bean.getEmailPersonaCreditoCobranza4());

            result.setEmailPersonaTesoreria(bean.getEmailPersonaTesoreria());
            result.setEmailPersonaTesoreria2(bean.getEmailPersonaTesoreria2());
            result.setEmailPersonaTesoreria3(bean.getEmailPersonaTesoreria3());
            result.setEmailPersonaTesoreria4(bean.getEmailPersonaTesoreria4());

            result.setEmailRepresentanteLegal(bean.getEmailRepresentanteLegal());
            result.setEmailRepresentanteLegal2(bean.getEmailRepresentanteLegal2());
            result.setEmailRepresentanteLegal3(bean.getEmailRepresentanteLegal3());
            result.setEmailRepresentanteLegal4(bean.getEmailRepresentanteLegal4());

            result.setIdPais(bean.getIdPais());
            result.setIdRegion(bean.getIdRegion());
            result.setIdProvincia(bean.getIdProvincia());
            result.setIdDistrito(bean.getIdDistrito());
            result.setIdTipoProveedor(bean.getIdTipoProveedor());
            result.setIdCondicionPago(bean.getIdCondicionPago());
            result.setIdMoneda(bean.getIdMoneda());
            result.setIdTipoComprobante(bean.getIdTipoComprobante());
            result.setIndTipoVentaBien(bean.getIndTipoVentaBien());
            result.setIndTipoVentaServicio(bean.getIndTipoVentaServicio());
            result.setIndTipoFacturacionManual(bean.getIndTipoFacturacionManual());
            result.setIndTipoFacturacionElect(bean.getIndTipoFacturacionElect());
            result.setNombreRepresentanteLegal(bean.getNombreRepresentanteLegal());
            result.setNroDocumRepresentanteLegal(bean.getNroDocumRepresentanteLegal());
            result.setNombrePersonaTesoreria(bean.getNombrePersonaTesoreria());
            result.setNroDocumPersonaTesoreria(bean.getNroDocumPersonaTesoreria());
            result.setNombrePersonaCreditoCobranza(bean.getNombrePersonaCreditoCobranza());
            result.setNroDocumPersonaCreditoCobranza(bean.getNroDocumPersonaCreditoCobranza());
            result.setNombrePersonaCompra(bean.getNombrePersonaCompra());
            result.setOperacionesAfectas(bean.getOperacionesAfectas());
            result.setRazonSocial(bean.getRazonSocial());
            result.setRuc(bean.getRuc());
            result.setTelefono(bean.getTelefono());
            result.setTipoPersona(bean.getTipoPersona());
            result.setAcredorCodigoSap(bean.getAcredorCodigoSap());
            result.setActivo(bean.getActivo());
            result.setEmailRetencion(bean.getEmailRetencion());
            result.setEvalHomologacion(bean.getEvalHomologacion());
            result.setEvalDesempeno(bean.getEvalDesempeno());
            result.setFechaCreacion(bean.getFechaCreacion());
            result.setFechaModificacion(DateUtils.getCurrentTimestamp());
            result.setIdHcp(bean.getIdHcp());
            result.setTerritAmazonia(bean.getTerritAmazonia());
            result.setUsuarioCreacion(bean.getUsuarioCreacion());
            result.setIndAceptacion(bean.getIndAceptacion());
            result.setIdTipoBeneficiario(bean.getIdTipoBeneficiario());
            result.setCuentaBeneficiario(bean.getCuentaBeneficiario());
            result.setNombreBeneficiario(bean.getNombreBeneficiario());
            result.setFlagCuentabancaria(bean.getFlagCuentabancaria());
            result.setDireccionBeneficiario(bean.getDireccionBeneficiario());
            result.setCiudadBeneficiario(bean.getCiudadBeneficiario());
            result.setReferenciaBeneficiario(bean.getReferenciaBeneficiario());
            result.setNombreBancoCtaExtranjero(bean.getNombreBancoCtaExtranjero());
            result.setCiudadBancoCtaExtranjero(bean.getCiudadBancoCtaExtranjero());
            result.setDireccionBancoCtaExtranjero(bean.getDireccionBancoCtaExtranjero());
            result.setTipoCodigoBancoCtaExtranjero(bean.getTipoCodigoBancoCtaExtranjero());
            result.setCodigoBancoCtaExtranjero(bean.getCodigoBancoCtaExtranjero());
            result.setIdPaisBeneficiario(bean.getPaisBeneficiario());
            result.setIdEstadoBeneficiario(bean.getEstadoBeneficiario());
            result.setIdPaisBancoExtranjero(bean.getPaisBancoExtranjero());
            result.setIdEstadoBancoExtranjero(bean.getEstadoBancoExtranjero());
            result.setIdBanco(bean.getIdBanco());
            result.setIndDetraccion(bean.getIndDetraccion());
            result.setIndEmiteRecibo(bean.getIndEmiteRecibo());
        }

        return result;
    }



    public ProveedorDto inspeccionProveedorCompleto(ProveedorDto bean) throws Exception {


        Proveedor resultProveedor =  proveedorRepository.getProveedorByIdProveedor(bean.getIdProveedor());
        if(resultProveedor.getIdProveedor() != null) {
            //CANALES
            proveedorCanalContactoRepository.deleteCanalContactoByIdProveedor(resultProveedor.getIdProveedor());
            if (bean.getCanales().size() > 0) {
                bean.getCanales().forEach(item -> {
                    //insertar canales
                    ProveedorCanalContacto proveedorCanalContacto = new ProveedorCanalContacto();
                    proveedorCanalContacto.setAreaEmpresa(item.getCodigoArea());
                    proveedorCanalContacto.setContacto(item.getContacto());
                    proveedorCanalContacto.setDireccion(item.getDireccion());
                    proveedorCanalContacto.setEmail(item.getEmail());
                    proveedorCanalContacto.setTelefono(item.getTelefono());
                    if (item.getCodigoPais() != null) {
                        Optional<Ubigeo> paisCanal = ubigeoRepository.findById(item.getCodigoPais());
                        proveedorCanalContacto.setPais(paisCanal.isPresent() ? paisCanal.get() : null);
                    }
                    proveedorCanalContacto.setProveedor(resultProveedor);
                    if (item.getCodigoProvincia() != null) {
                        Optional<Ubigeo> proviniciaCanal = ubigeoRepository.findById(Integer.valueOf(item.getCodigoProvincia()));
                        proveedorCanalContacto.setProvincia(proviniciaCanal.isPresent() ? proviniciaCanal.get() : null);
                    }
                    if (item.getCodigoRegion() != null) {
                        Optional<Ubigeo> regionCanal = ubigeoRepository.findById(item.getCodigoRegion());
                        proveedorCanalContacto.setRegion(regionCanal.isPresent() ? regionCanal.get() : null);
                    }
                    try{
                    proveedorCanalContactoRepository.save(proveedorCanalContacto);
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("ERROR proveedorCanalContacto");
                    }
                });
            }

            //cuentas bancarias
            //proveedorCuentaBancoRepository.deleteContactoByIdProveedor(resultProveedor.getIdProveedor());
            if (bean.getCuentasBanco().size() > 0) {

                bean.getCuentasBanco().forEach(item -> {

                    if(item.getArchivoBase64() != null){

                        ProveedorCuentaBancaria proveedorCuentaBancaria = new ProveedorCuentaBancaria();

                        if(item.getIdCuenta() != null){
                            proveedorCuentaBancaria.setIdCuenta(item.getIdCuenta());
                        }

                        proveedorCuentaBancaria.setClaveControlBanco(item.getCodigoTipoCuenta());
                        proveedorCuentaBancaria.setContacto(item.getContacto());
                        proveedorCuentaBancaria.setNumeroCuenta(item.getNumeroCuenta());
                        proveedorCuentaBancaria.setNumeroCuentaCci(item.getNumeroCuentaCci());
                        proveedorCuentaBancaria.setArchivoId(item.getArchivoId());
                        proveedorCuentaBancaria.setArchivoNombre(item.getArchivoNombre());
                        proveedorCuentaBancaria.setArchivoTipo(item.getArchivoTipo());
                        proveedorCuentaBancaria.setValidacionCuenta(item.getValidacionCuenta());
                        proveedorCuentaBancaria.setRutaAdjunto(item.getRutaAdjunto());

                        if (item.getTipoCuenta().equals("Detracción")) {
                            proveedorCuentaBancaria.setIndCuentaDetraccion("SI");
                        } else if (item.getTipoCuenta().equals("Corriente") || item.getTipoCuenta().equals("Maestra")) {
                            proveedorCuentaBancaria.setIndCuentaDetraccion("CC");
                        } else {
                            proveedorCuentaBancaria.setIndCuentaDetraccion("NO");
                        }

                        //proveedorCuentaBancaria.setIndCuentaDetraccion(item.getTipoCuenta().equals("Detracción") ? "": "NO");

                        Banco banco = new Banco();
                        banco.setIdBanco(item.getIdBanco());
                        proveedorCuentaBancaria.setBanco(banco);

                        Moneda moneda = monedaRepository.getByCodigoMoneda(item.getMoneda());
                        proveedorCuentaBancaria.setMoneda(moneda);

                        proveedorCuentaBancaria.setProveedor(resultProveedor);
                        try{
                        proveedorCuentaBancoRepository.save(proveedorCuentaBancaria);
                        } catch (Exception e) {
                            e.printStackTrace();
                            System.out.println("ERROR proveedorCuentaBancaria");
                        }

                    }
                    /*try {
                        cmisBaseServicecf.crearDocumentoCuentaBancaria(proveedorCuentaBancaria.getIdCuenta(), item.getAdjuntoCargaDto(), resultProveedor);
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("ERROR");
                    }*/
                });


            }

            //LINEAS COMERCIALES
            proveedorLineaComercialRepository.deleteLineaComercialByIdProveedor(resultProveedor.getIdProveedor());
            if (bean.getLineasComercial().size() > 0) {
                bean.getLineasComercial().forEach(item -> {
                    ProveedorLineaComercial proveedorLineaComercial = new ProveedorLineaComercial();
                    proveedorLineaComercial.setOtrosLineaComercial(item.getOtraLinea());

                    LineaComercial familia = new LineaComercial();
                    LineaComercial linea = lineaComercialRepository.getById(item.getIdLinea());
                    LineaComercial subfamilia = lineaComercialRepository.getById(item.getIdFamilia());
                    LineaComercial subfamilia1 = lineaComercialRepository.getById(subfamilia.getIdPadre());
                    familia.setIdLineaComercial(item.getIdFamilia());
                    proveedorLineaComercial.setFamilia(familia);
                    LineaComercial lineaComercial = new LineaComercial();
                    lineaComercial.setIdLineaComercial(linea.getIdPadre() != null ? linea.getIdPadre() : item.getIdLinea());
                    proveedorLineaComercial.setLineaComercial(lineaComercial);
                    proveedorLineaComercial.setProveedor(resultProveedor);
                    proveedorLineaComercial.setSubFamilia(subfamilia1);
                    try{
                    proveedorLineaComercialRepository.save(proveedorLineaComercial);
                    } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("ERROR proveedorLineaComercial");
                     }
                });
            }

            //productos
            proveedorProductoRepository.deleteProductoByIdProveedor(resultProveedor.getIdProveedor());
            if (bean.getProductos().size() > 0) {
                bean.getProductos().forEach(item -> {
                    ProveedorProducto proveedorProducto = new ProveedorProducto();
                    proveedorProducto.setDescripcionAdicional(item.getDescripcionAdicional());
                    proveedorProducto.setMarca(item.getMarca());
                    proveedorProducto.setProducto(item.getProducto());
                    proveedorProducto.setProveedor(resultProveedor);
                    try {
                    proveedorProductoRepository.save(proveedorProducto);
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("ERROR proveedorProducto");
                    }

                });
            }

            //  INSTALACIONES
            proveedorInstalacionRepository.deleteInstalacionByIdProveedor(resultProveedor.getIdProveedor());
            if (bean.getInstalaciones().size() > 0) {
                bean.getInstalaciones().forEach(item -> {
                    ProveedorInstalacion proveedorInstalacion = new ProveedorInstalacion();
                    proveedorInstalacion.setCodigoTipoInstalacion(item.getCodigoTipoInstalacion());
                    proveedorInstalacion.setDireccion(item.getDireccion());
                    proveedorInstalacion.setTelefono(item.getTelefono());
                    proveedorInstalacion.setIdProveedor(resultProveedor);
                    try {
                    proveedorInstalacionRepository.save(proveedorInstalacion);
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("ERROR proveedorInstalacion");
                    }
                });
            }


            //PRINCIPALES
            proveedorClienteRepository.deleteClienteByIdProveedor(resultProveedor.getIdProveedor());
            if (bean.getPrincipales().size() > 0) {
                bean.getPrincipales().forEach(item -> {
                    ProveedorCliente proveedorCliente = new ProveedorCliente();
                    proveedorCliente.setCodigoTipoProveedorCliente(item.getCodigoTipoProveedorCliente());
                    proveedorCliente.setEmail(item.getEmail());
                    proveedorCliente.setPersonaContacto(item.getPersonaContacto());
                    proveedorCliente.setPorcentajeParticipacion(item.getPorcentajeParticipacion());
                    proveedorCliente.setRazonSocial(item.getRazonSocial());
                    proveedorCliente.setRubro(item.getRubro());
                    proveedorCliente.setRuc(item.getRuc());
                    proveedorCliente.setTelefono(item.getTelefono());
                    proveedorCliente.setIdProveedor(resultProveedor);
                    try {
                    proveedorClienteRepository.save(proveedorCliente);
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("ERROR proveedorCliente");
                    }
                });
            }


            //adicionales
            proveedorFuncionarioRepository.deleteFuncionarioByIdProveedor(resultProveedor.getIdProveedor());
            if (bean.getAdicionales().size() > 0) {
                bean.getAdicionales().forEach(item -> {
                    ProveedorFuncionario proveedorFuncionario = new ProveedorFuncionario();
                    proveedorFuncionario.setApellidosNombres(item.getApellidosNombres());
                    proveedorFuncionario.setCargo(item.getCargo());
                    proveedorFuncionario.setRuc(item.getRuc());
                    proveedorFuncionario.setIdProveedor(resultProveedor);
                    proveedorFuncionario.setTipoDocumento(item.getTipoDocumento());
                    try{
                    proveedorFuncionarioRepository.save(proveedorFuncionario);
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("ERROR proveedorFuncionario");
                    }
                });
            }

            //preguntas informacion
            proveedorPreguntaInformacionRepository.deletePreguntaInformacionByIdProveedor(resultProveedor.getIdProveedor());
            if (bean.getPreguntaInformacion().size() > 0) {
                bean.getPreguntaInformacion().forEach(item -> {
                    ProveedorPreguntaInformacion proveedorPreguntaInformacion = new ProveedorPreguntaInformacion();
                    proveedorPreguntaInformacion.setRespuesta(item.getRespuesta());
                    proveedorPreguntaInformacion.setIdPreguntaInformacion(item.getIdPreguntaInformacion());
                    proveedorPreguntaInformacion.setIdProveedor(resultProveedor);
                    try {
                    proveedorPreguntaInformacionRepository.save(proveedorPreguntaInformacion);
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("ERROR proveedorPreguntaInformacion");
                    }
                });
            }


            //catalogos
            proveedorCatalogoRepository.deleteCatalogoByIdProveedor(resultProveedor.getIdProveedor());
            if (bean.getCatalogos().size() > 0) {
                bean.getCatalogos().forEach(item -> {
                    ProveedorCatalogo proveedorCatalogo = new ProveedorCatalogo();
                    proveedorCatalogo.setArchivoId(item.getArchivoId());
                    proveedorCatalogo.setArchivoNombre(item.getArchivoNombre());
                    proveedorCatalogo.setArchivoTipo(item.getArchivoTipo());
                    proveedorCatalogo.setRutaCatalogo(item.getRutaCatalogo());
                    proveedorCatalogo.setProveedor(resultProveedor);
                    try {
                    proveedorCatalogoRepository.save(proveedorCatalogo);
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("ERROR proveedorCatalogo");
                    }
                });
            }


        }

        return bean;
    }


    public String getTipoDocumento(Integer idProveedor) throws Exception {
        String data = "word/tabla.html";
        VelocityContext context = new VelocityContext();
        context.put("param1", "TEXTO de prueba");
        Optional<Proveedor> proveedorOp = proveedorRepository.findById(idProveedor);
        if (!proveedorOp.isPresent()) {
            throw new PortalException("no fue posible encontrar al Proveedor ");
        }


        Proveedor proveedor = proveedorOp.get();
        List<ProveedorDeclaracionJurada> proveedorDeclaracionJurada =
                proveedorDeclaracionJuradaRepository.findIdProveedor(idProveedor);
        //insertar Tabla antecedentes penales y policiales
        String pattern = "dd-MM-yyyy";//yyyy-MM-dd
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        if (proveedorDeclaracionJurada.size() > 0) {
            //ARMAR LA TRAMA PARA CREAR EL DOCUMENTO
            ProveedorDeclaracionJurada declaracionJurada = proveedorDeclaracionJurada.get(proveedorDeclaracionJurada.size() - 1);

            context.put("param1", declaracionJurada.getNombres() != null ? declaracionJurada.getNombres() : "");
            context.put("param2", declaracionJurada.getApellidos() != null ? declaracionJurada.getApellidos() : "");
            context.put("param3", declaracionJurada.getNacionalidad().equals(1) ? "PERUANA" : "");
            context.put("param4", declaracionJurada.getNacionalidadOtro() != null ? declaracionJurada.getNacionalidadOtro() : "");
            context.put("param5", declaracionJurada.getEstadoCivil().getDescripcion());
            context.put("param6", declaracionJurada.getNombresConyugeConviviente() != null ? declaracionJurada.getNombresConyugeConviviente() : "");

            //TIPO DOCUMENTO
            String tipoDocumento = declaracionJurada.getTipoDocumento().equals(1) ? "DNI" :
                    declaracionJurada.getTipoDocumento().equals(2) ? "Pasaporte" :
                            declaracionJurada.getTipoDocumento().equals(3) ? "Carnet Extranjería" : "";

            context.put("param7", tipoDocumento != null ? tipoDocumento : "");
            context.put("param8", declaracionJurada.getNumeroDocumento() != null ? declaracionJurada.getNumeroDocumento() : "");
            context.put("param9", proveedor.getRuc() != null ? proveedor.getRuc() : "");

            if (declaracionJurada.getDomicilio() == null) {
                context.put("param10", declaracionJurada.getDomicilioOficina() != null ? declaracionJurada.getDomicilioOficina() : "");
                context.put("param11", declaracionJurada.getDistritoOficina() != null ? declaracionJurada.getDistritoOficina().getDescripcion() : "");
                context.put("param12", declaracionJurada.getProvinciaOficina() != null ? declaracionJurada.getProvinciaOficina().getDescripcion() : "");
                context.put("param13", declaracionJurada.getDepartamentoOficina() != null ? declaracionJurada.getDepartamentoOficina().getDescripcion() : "");
                context.put("param14", declaracionJurada.getPaisOficina() != null ? declaracionJurada.getPaisOficina().getDescripcion() : "");
            } else {
                if (declaracionJurada.getDomicilio().equals("")) {
                    context.put("param10", declaracionJurada.getDomicilioOficina() != null ? declaracionJurada.getDomicilioOficina() : "");
                    context.put("param11", declaracionJurada.getDistritoOficina() != null ? declaracionJurada.getDistritoOficina().getDescripcion() : "");
                    context.put("param12", declaracionJurada.getProvinciaOficina() != null ? declaracionJurada.getProvinciaOficina().getDescripcion() : "");
                    context.put("param13", declaracionJurada.getDepartamentoOficina() != null ? declaracionJurada.getDepartamentoOficina().getDescripcion() : "");
                    context.put("param14", declaracionJurada.getPaisOficina() != null ? declaracionJurada.getPaisOficina().getDescripcion() : "");
                } else {
                    context.put("param10", declaracionJurada.getDomicilio());
                    context.put("param11", declaracionJurada.getDistrito() != null ? declaracionJurada.getDistrito().getDescripcion() : "");
                    context.put("param12", declaracionJurada.getProvincia() != null ? declaracionJurada.getProvincia().getDescripcion() : "");
                    context.put("param13", declaracionJurada.getDepartamento() != null ? declaracionJurada.getDepartamento().getDescripcion() : "");
                    context.put("param14", declaracionJurada.getPais() != null ? declaracionJurada.getPais().getDescripcion() : "");
                }
            }


            context.put("param15", declaracionJurada.getDomicilioOficina() != null ? declaracionJurada.getDomicilioOficina() : "");
            if (declaracionJurada.getDistritoOficina() != null) {
                context.put("param16", declaracionJurada.getDistritoOficina() != null ? declaracionJurada.getDistritoOficina().getDescripcion() : "");
            } else {
                context.put("param16", "");
            }
            if (declaracionJurada.getProvinciaOficina() != null) {
                context.put("param17", declaracionJurada.getProvinciaOficina() != null ? declaracionJurada.getProvinciaOficina().getDescripcion() : "");

            } else {
                context.put("param17", "");
            }
            context.put("param18", declaracionJurada.getDepartamentoOficina() != null ? declaracionJurada.getDepartamentoOficina().getDescripcion() : "");
            context.put("param19", declaracionJurada.getPaisOficina() != null ? declaracionJurada.getPaisOficina().getDescripcion() : "");
            context.put("param20", declaracionJurada.getActividadEconomica() != null ? declaracionJurada.getActividadEconomica() : "");
            context.put("param21", declaracionJurada.getAniosExperiencia() != null ? declaracionJurada.getAniosExperiencia() : "");
            context.put("param22", declaracionJurada.getRubrosOpera() != null ? declaracionJurada.getRubrosOpera() : "");

            context.put("param23", declaracionJurada.getEsPep().equals(1) ? "SI" : "NO");
            //CREAR TABLA PEP
            List<ProveedorPep> proveedorPep = proveedorPepRepository.findIdDeclaracionJurada(declaracionJurada.getIdDeclaracionJurada());
            if (!proveedorPep.isEmpty()) {
                ArrayList<ArrayList> arrayLists = new ArrayList<>();

                final String[] tabla2 = {"<table class='tftable' border='1'>" +
                        "<tr>" +
                        "<th>Cargo ejercido</th>" +
                        "<th>Entidad</th>" +
                        "<th>Fecha de inicio</th>" +
                        "<th>Fecha de fin</th>" +
                        "</tr>"};

                proveedorPep.forEach(item -> {
                    ArrayList<String> array = new ArrayList<>();
                    array.add(item.getCargoEjercido() != null ? item.getCargoEjercido() : "");
                    array.add(item.getEntidad() != null ? item.getEntidad() : "");
                    array.add(item.getFechaInicioFin() != null ? item.getFechaInicioFin() : "");
                    array.add(item.getFechaFin() != null ? item.getFechaFin() : "");

                    tabla2[0] += "<tr>" +
                            "<td>" + (item.getCargoEjercido() != null ? item.getCargoEjercido() : "") + "</td>" +
                            "<td>" + (item.getEntidad() != null ? item.getEntidad() : "") + "</td>" +
                            "<td>" + (item.getFechaInicioFin() != null ? item.getFechaInicioFin() : "") + "</td>" +
                            "<td>" + (item.getFechaFin() != null ? item.getFechaFin() : "") + "</td>" +
                            "</tr>";
                    arrayLists.add(array);
                });

                tabla2[0] += "</table>";
                context.put("tabla2", tabla2[0]);
                //Utils.remplazarTextoDocumentoTablaPep(document,"{tabla2",arrayLists);
            } else {
                context.put("tabla2", "");
            }

            //CREAR TABLA PARIENTE PEP
            List<ProveedorParientePep> proveedorParientePeps =
                    proveedorParientePepRepository.findIdDeclaracionJurada(declaracionJurada.getIdDeclaracionJurada());
            if (!proveedorParientePeps.isEmpty()) {
                ArrayList<ArrayList> arrayLists = new ArrayList<>();

                final String[] tabla3 = {"<table class='tftable' border='1'>" +
                        "<tr>" +
                        "<th>Vínculo parental</th>" +
                        "<th>Nombres y apellidos del Pariente PEP</th>" +
                        "<th>Cargo ejercido</th>" +
                        "<th>Entidad</th>" +
                        "<th>Fecha de inicio</th>" +
                        "<th>Fecha de fin</th>" +
                        "<th>Nacionalidad</th>" +
                        "<th>DNI/Carné Ext/Pass</th>" +
                        "</tr>"};

                proveedorParientePeps.forEach(item -> {
                    ArrayList<String> array = new ArrayList<>();
                    array.add(item.getVinculoParental() != null ? item.getVinculoParental() : "");
                    array.add(item.getNombresParientePep() != null ? item.getNombresParientePep() : "");
                    array.add(item.getCargoEjercidoPariente() != null ? item.getCargoEjercidoPariente() : "");
                    array.add(item.getEntidadPariente() != null ? item.getEntidadPariente() : "");
                    array.add(item.getFechaInicioFinPariente() != null ? item.getFechaInicioFinPariente() : "");
                    array.add(item.getFechaFin() != null ? item.getFechaFin() : "");
                    array.add(item.getNacionalidadPariente() != null ? item.getNacionalidadPariente() : "");
                    array.add(item.getDniPariente() != null ? item.getDniPariente() : "");

                    tabla3[0] += "<tr>" +
                            "<td>" + (item.getVinculoParental() != null ? item.getVinculoParental() : "") + "</td>" +
                            "<td>" + (item.getNombresParientePep() != null ? item.getNombresParientePep() : "") + "</td>" +
                            "<td>" + (item.getCargoEjercidoPariente() != null ? item.getCargoEjercidoPariente() : "") + "</td>" +
                            "<td>" + (item.getEntidadPariente() != null ? item.getEntidadPariente() : "") + "</td>" +
                            "<td>" + (item.getFechaInicioFinPariente() != null ? item.getFechaInicioFinPariente() : "") + "</td>" +
                            "<td>" + (item.getFechaFin() != null ? item.getFechaFin() : "") + "</td>" +
                            "<td>" + (item.getNacionalidadPariente() != null ? item.getNacionalidadPariente() : "") + "</td>" +
                            "<td>" + (item.getDniPariente() != null ? item.getDniPariente() : "") + "</td>" +
                            "</tr>";

                    arrayLists.add(array);
                });

                tabla3[0] += "</table>";
                context.put("tabla3", tabla3[0]);
                //Utils.remplazarTextoDocumentoTablaParientePep(document,"{tabla3",arrayLists);

            } else {
                context.put("tabla3", "");
            }
            context.put("param24", declaracionJurada.getCargoEjercido() != null ? declaracionJurada.getCargoEjercido() : "");
            context.put("param25", declaracionJurada.getEntidad() != null ? declaracionJurada.getEntidad() : "");
            context.put("param26", declaracionJurada.getFechaInicioFin() != null ? declaracionJurada.getFechaInicioFin() : "");
            context.put("param27", declaracionJurada.getEsParientePep().equals(1) ? "SI" : "NO");
            context.put("param28", declaracionJurada.getVinculoParental() != null ? declaracionJurada.getVinculoParental() : "");
            context.put("param29", declaracionJurada.getNombresParientePep() != null ? declaracionJurada.getNombresParientePep() : "");
            context.put("param30", declaracionJurada.getCargoEjercidoPariente() != null ? declaracionJurada.getCargoEjercidoPariente() : "");
            context.put("param31", declaracionJurada.getEntidadPariente() != null ? declaracionJurada.getEntidadPariente() : "");
            context.put("param32", declaracionJurada.getFechaInicioFinPariente() != null ? declaracionJurada.getFechaInicioFinPariente() : "");
            context.put("param33", declaracionJurada.getNacionalidadPariente() != null ? declaracionJurada.getNacionalidadPariente() : "");

            List<ProveedorAntecedente> proveedorAntecedente =
                    proveedorAntecedenteRepository.findProveedorDeclaracionJurada(declaracionJurada.getIdDeclaracionJurada());
            if (!proveedorAntecedente.isEmpty()) {

                ArrayList<ArrayList> arrayLists = new ArrayList<>();
                final String[] tabla1 = {"<table class='tftable' border='1'>" +
                        "<tr>" +
                        "<th>Número de antecedente</th>" +
                        "<th>Delitos involucrados en los casos</th>" +
                        "<th>Breve descripción del estado de los casos</th>" +
                        "</tr>"};
                proveedorAntecedente.forEach(item -> {
                    ArrayList<String> array = new ArrayList<>();

                    if (item.getTipo().equals("POL")) {
                        array.add(item.getNumeroAntecedente() != null ? item.getNumeroAntecedente() : "");//1
                        array.add(item.getDelitosInvolucrados() != null ? item.getDelitosInvolucrados() : "");//2
                        array.add(item.getDescripcionEstadoCaso() != null ? item.getDescripcionEstadoCaso() : "");//3

                        tabla1[0] = tabla1[0] + "<tr>" +
                                "<td>" + (item.getNumeroAntecedente() != null ? item.getNumeroAntecedente() : "") + "</td>" +
                                "<td>" + (item.getDelitosInvolucrados() != null ? item.getDelitosInvolucrados() : "") + "</td>" +
                                "<td>" + (item.getDescripcionEstadoCaso() != null ? item.getDescripcionEstadoCaso() : "") + "</td>" +
                                "</tr>";
                        arrayLists.add(array);
                    }
                });

                tabla1[0] += "</table>";

                if (arrayLists.size() <= 0) {
                    context.put("tabla1", "");
                } else {
                    try {
                        //Utils.remplazarTextoDocumentoTabla(document,"{tabla1",arrayLists);
                        context.put("tabla1", tabla1[0]);
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new PortalException(e.getMessage());
                    }
                }
            } else {
                context.put("tabla1", "");
            }

            List<ProveedorAntecedente> proveedorAntecedentePenal =
                    proveedorAntecedenteRepository.findProveedorDeclaracionJurada(declaracionJurada.getIdDeclaracionJurada());
            if (!proveedorAntecedentePenal.isEmpty()) {

                ArrayList<ArrayList> arrayLists = new ArrayList<>();
                final String[] tablaAntecedentePenal = {"<table class='tftable' border='1'>" +
                        "<tr>" +
                        "<th>Número de condena</th>" +
                        "<th>Fecha Condena</th>" +
                        "<th>Delitos involucrados</th>" +
                        "</tr>"};
                proveedorAntecedentePenal.forEach(item -> {
                    ArrayList<String> array = new ArrayList<>();

                    if (item.getTipo().equals("PEN")) {
                        array.add(item.getNumeroAntecedente() != null ? item.getNumeroAntecedente() : "");//1
                        array.add(item.getFechaCondena() != null ? simpleDateFormat.format(item.getFechaCondena()) : "");//2
                        array.add(item.getDelitosInvolucrados() != null ? item.getDelitosInvolucrados() : "");//2


                        tablaAntecedentePenal[0] = tablaAntecedentePenal[0] + "<tr>" +
                                "<td>" + (item.getNumeroAntecedente() != null ? item.getNumeroAntecedente() : "") + "</td>" +
                                "<td>" + (item.getFechaCondena() != null ? simpleDateFormat.format(item.getFechaCondena()) : "") + "</td>" +
                                "<td>" + (item.getDelitosInvolucrados() != null ? item.getDelitosInvolucrados() : "") + "</td>" +
                                "</tr>";
                        arrayLists.add(array);
                    }
                });

                tablaAntecedentePenal[0] += "</table>";

                if (arrayLists.size() <= 0) {
                    context.put("tablaAntecedentePenal", "");
                } else {
                    try {
                        //Utils.remplazarTextoDocumentoTabla(document,"{tabla1",arrayLists);
                        context.put("tablaAntecedentePenal", tablaAntecedentePenal[0]);
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new PortalException(e.getMessage());
                    }
                }
            } else {
                context.put("tablaAntecedentePenal", "");
            }

            context.put("param34", declaracionJurada.getIndAntecedentesPenales().equals(1) ? "SI" : "NO");
            context.put("param35", declaracionJurada.getNumeroFechaCondena() != null ? declaracionJurada.getNumeroFechaCondena() : "");
            context.put("param36", declaracionJurada.getDelitosInvolucrados() != null ? declaracionJurada.getDelitosInvolucrados() : "");
            context.put("param37", declaracionJurada.getIndAntecedentesPoliciales().equals(1) ? "SI" : "NO");
            context.put("param38", declaracionJurada.getNumeroAntecedentes() != null ? declaracionJurada.getNumeroAntecedentes() : "");
            context.put("param39", declaracionJurada.getDelitosInvolucradosPoliciales() != null ? declaracionJurada.getDelitosInvolucradosPoliciales() : "");
            context.put("param40", declaracionJurada.getDescripcionEstadoCaso() != null ? declaracionJurada.getDescripcionEstadoCaso() : "");
        } else {
            throw new Exception("No fue posible encontrar al proveedor :" + idProveedor);
        }


        String content = Optional.ofNullable(data)
                .map(url -> url + "")
                .map(template -> {
                    int i = 0;
                    return getContentMail(context, template);
                })
                .orElse("");
        System.out.println("content");

        return content;
    }

    public String getTipoDocumentoPJHTML(Integer idProveedor) throws Exception {
        String data = "word/PJ-BACKEND-3.html";
        //String data = new String(Files.readAllBytes(Paths.get("src/main/resources/word/PJ-BACKEND-3.html")));

        VelocityContext context = new VelocityContext();

        Optional<Proveedor> proveedor = proveedorRepository.findById(idProveedor);
        if (!proveedor.isPresent()) {
            throw new PortalException("No se encontró al proveedor ");
        }
        Proveedor proveedorActual = proveedor.get();

        List<ProveedorDeclaracionJurada> proveedorDeclaracionJurada =
                proveedorDeclaracionJuradaRepository.findIdProveedorTipoPersona(idProveedor, "PJ");

        if (proveedorDeclaracionJurada.size() > 0) {
            //ARMAR LA TRAMA PARA CREAR EL DOCUMENTO
            String pattern = "dd-MM-yyyy";//dd-MM-yyyy
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            ProveedorDeclaracionJurada declaracionJurada = proveedorDeclaracionJurada.get(proveedorDeclaracionJurada.size() - 1);

            context.put("param1", proveedorActual.getRazonSocial());
            context.put("param2", simpleDateFormat.format(declaracionJurada.getFechaSociedad()));
            context.put("param3", declaracionJurada.getNombreComercial());
            context.put("param4", declaracionJurada.getGrupoEconomico());
            //TIPO DOCUMENTO
            String tipoDocumento = declaracionJurada.getTipoDocumento().equals(4) ? "RUC" :
                    declaracionJurada.getTipoDocumento().equals(5) ? "OTRO" : "N/A";
            context.put("param5", tipoDocumento);
            context.put("param6", proveedorActual.getRuc());

            //Informacion fiscal
            context.put("param7", declaracionJurada.getPartidaRegistral());
            context.put("param8", declaracionJurada.getOficinaRegistral());

            if (declaracionJurada.getProveedor().getDireccionFiscal() == null) {
                logger.info("Dom 0");
                context.put("param10", declaracionJurada.getProveedor().getDireccionFiscal());
                context.put("param11", declaracionJurada.getProveedor().getDistrito() != null ? declaracionJurada.getProveedor().getDistrito().getDescripcion() : "");
                context.put("param12", declaracionJurada.getProveedor().getProvincia() != null ? declaracionJurada.getProveedor().getProvincia().getDescripcion() : "");
                context.put("param13", declaracionJurada.getProveedor().getRegion() != null ? declaracionJurada.getProveedor().getRegion().getDescripcion() : "");
                context.put("param14", declaracionJurada.getProveedor().getPais() != null ? declaracionJurada.getProveedor().getPais().getDescripcion() : "");
            } else {
                if (declaracionJurada.getProveedor().getDireccionFiscal().equals("")) {
                    logger.info("Dom 1");
                    context.put("param10", declaracionJurada.getDomicilioOficina());
                    context.put("param11", declaracionJurada.getDistritoOficina() != null ? declaracionJurada.getDistritoOficina().getDescripcion() : "");
                    context.put("param12", declaracionJurada.getProvinciaOficina() != null ? declaracionJurada.getProvinciaOficina().getDescripcion() : "");
                    context.put("param13", declaracionJurada.getDepartamentoOficina() != null ? declaracionJurada.getDepartamentoOficina().getDescripcion() : "");
                    context.put("param14", declaracionJurada.getPaisOficina() != null ? declaracionJurada.getPaisOficina().getDescripcion() : "");

                } else {
                    logger.info("Dom 2");
                    context.put("param10", declaracionJurada.getProveedor().getDireccionFiscal());
                    context.put("param11", declaracionJurada.getProveedor().getDistrito() != null ? declaracionJurada.getProveedor().getDistrito().getDescripcion() : "");
                    context.put("param12", declaracionJurada.getProveedor().getProvincia() != null ? declaracionJurada.getProveedor().getProvincia().getDescripcion() : "");
                    context.put("param13", declaracionJurada.getProveedor().getRegion() != null ? declaracionJurada.getProveedor().getRegion().getDescripcion() : "");
                    context.put("param14", declaracionJurada.getProveedor().getPais() != null ? declaracionJurada.getProveedor().getPais().getDescripcion() : "");
                }
            }


//            context.put("param10",proveedorActual.getDireccionFiscal());
//            context.put("param11",proveedorActual.getDistrito() != null ? proveedorActual.getDistrito().getDescripcion() : "");
//            context.put("param12",proveedorActual.getProvincia()!= null ?proveedorActual.getProvincia().getDescripcion():"");
//            context.put("param13",proveedorActual.getRegion()!= null ? proveedorActual.getRegion().getDescripcion():"");
//            context.put("param14",proveedorActual.getPais()!= null ?proveedorActual.getPais().getDescripcion():"");
            //Informacion oficina fiscal
            context.put("param15", declaracionJurada.getDomicilioOficina());
            context.put("param16", declaracionJurada.getDistritoOficina() != null ? declaracionJurada.getDistritoOficina().getDescripcion() : "");
            context.put("param17", declaracionJurada.getProvinciaOficina() != null ? declaracionJurada.getProvinciaOficina().getDescripcion() : "");
            context.put("param18", declaracionJurada.getDepartamentoOficina() != null ? declaracionJurada.getDepartamentoOficina().getDescripcion() : "");
            context.put("param19", declaracionJurada.getPaisOficina() != null ? declaracionJurada.getPaisOficina().getDescripcion() : "");
            //actividad economica
            context.put("param20", declaracionJurada.getActividadEconomica());
            context.put("param21", declaracionJurada.getAniosExperiencia());
            context.put("param22", declaracionJurada.getRubrosOpera());


            //insertar Tabla antecedentes penales y policiales
            context.put("param23", declaracionJurada.getIndAntecedentesPenales().equals(1) ? "SI" : "NO");
            context.put("param24", declaracionJurada.getIndAntecedentesPoliciales().equals(1) ? "SI" : "NO");


            List<ProveedorAntecedente> proveedorAntecedente =
                    proveedorAntecedenteRepository.findProveedorDeclaracionJurada(declaracionJurada.getIdDeclaracionJurada());
            if (!proveedorAntecedente.isEmpty() && declaracionJurada.getIndAntecedentesPoliciales().equals(1)) {

                ArrayList<ArrayList> arrayLists = new ArrayList<>();
                final String[] tabla1 = {"<table class='tftable' border='1'>" +
                        "<tr>" +
                        "<th>Número de antecedente</th>" +
                        "<th>Delitos involucrados en los casos</th>" +
                        "<th>Breve descripción del estado de los casos</th>" +
                        "</tr>"};
                proveedorAntecedente.forEach(item -> {
                    ArrayList<String> array = new ArrayList<>();

                    if (item.getTipo().equals("POL")) {
                        array.add(item.getNumeroAntecedente() != null ? item.getNumeroAntecedente() : "");//1
                        array.add(item.getDelitosInvolucrados() != null ? item.getDelitosInvolucrados() : "");//2
                        array.add(item.getDescripcionEstadoCaso() != null ? item.getDescripcionEstadoCaso() : "");//3

                        tabla1[0] = tabla1[0] + "<tr>" +
                                "<td>" + (item.getNumeroAntecedente() != null ? item.getNumeroAntecedente() : "") + "</td>" +
                                "<td>" + (item.getDelitosInvolucrados() != null ? item.getDelitosInvolucrados() : "") + "</td>" +
                                "<td>" + (item.getDescripcionEstadoCaso() != null ? item.getDescripcionEstadoCaso() : "") + "</td>" +
                                "</tr>";
                        arrayLists.add(array);
                    }
                });

                tabla1[0] += "</table>";

                if (arrayLists.size() <= 0) {
                    context.put("tabla1", "");
                } else {
                    try {
                        //Utils.remplazarTextoDocumentoTabla(document,"{tabla1",arrayLists);
                        context.put("tabla1", tabla1[0]);
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new PortalException(e.getMessage());
                    }
                }
            } else {
                context.put("tabla1", "");
            }

            List<ProveedorAntecedente> proveedorAntecedentePenal =
                    proveedorAntecedenteRepository.findProveedorDeclaracionJurada(declaracionJurada.getIdDeclaracionJurada());
            if (!proveedorAntecedentePenal.isEmpty() && declaracionJurada.getIndAntecedentesPenales().equals(1)) {

                ArrayList<ArrayList> arrayLists = new ArrayList<>();
                final String[] tablaAntecedentePenal = {"<table class='tftable' border='1'>" +
                        "<tr>" +
                        "<th>Número de condena</th>" +
                        "<th>Fecha Condena</th>" +
                        "<th>Delitos involucrados</th>" +
                        "</tr>"};
                proveedorAntecedentePenal.forEach(item -> {
                    ArrayList<String> array = new ArrayList<>();

                    if (item.getTipo().equals("PEN")) {
                        array.add(item.getNumeroAntecedente() != null ? item.getNumeroAntecedente() : "");//1
                        array.add(item.getFechaCondena() != null ? simpleDateFormat.format(item.getFechaCondena()) : "");//2
                        array.add(item.getDelitosInvolucrados() != null ? item.getDelitosInvolucrados() : "");//2


                        tablaAntecedentePenal[0] = tablaAntecedentePenal[0] + "<tr>" +
                                "<td>" + (item.getNumeroAntecedente() != null ? item.getNumeroAntecedente() : "") + "</td>" +
                                "<td>" + (item.getFechaCondena() != null ? simpleDateFormat.format(item.getFechaCondena()) : "") + "</td>" +
                                "<td>" + (item.getDelitosInvolucrados() != null ? item.getDelitosInvolucrados() : "") + "</td>" +
                                "</tr>";
                        arrayLists.add(array);
                    }
                });

                tablaAntecedentePenal[0] += "</table>";

                if (arrayLists.size() <= 0) {
                    context.put("tablaAntecedentePenal", "");
                } else {
                    try {
                        //Utils.remplazarTextoDocumentoTabla(document,"{tabla1",arrayLists);
                        context.put("tablaAntecedentePenal", tablaAntecedentePenal[0]);
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new PortalException(e.getMessage());
                    }
                }
            } else {
                context.put("tablaAntecedentePenal", "");
            }

            //POLÍTICAS DE CUMPLIMIENTO DE LA SOCIEDAD:
            context.put("param25", declaracionJurada.getPoliticaCumplimientoPregunta1() != null ?
                    declaracionJurada.getPoliticaCumplimientoPregunta1().equals("1") ? "NO" :
                            declaracionJurada.getPoliticaCumplimientoPregunta1().equals("0") ? "SI" :
                                    declaracionJurada.getPoliticaCumplimientoPregunta1().equals("2") ? "EN PROCESO" : "-"
                    : "");
            context.put("param26", declaracionJurada.getPoliticaCumplimientoPregunta2() != null ?
                    declaracionJurada.getPoliticaCumplimientoPregunta2().equals("1") ? "NO" :
                            declaracionJurada.getPoliticaCumplimientoPregunta2().equals("0") ? "SI" :
                                    declaracionJurada.getPoliticaCumplimientoPregunta2().equals("2") ? "EN PROCESO" : "-"
                    : "");
            context.put("param27", declaracionJurada.getPoliticaCumplimientoPregunta3() != null ?
                    declaracionJurada.getPoliticaCumplimientoPregunta3().equals("1") ? "NO" :
                            declaracionJurada.getPoliticaCumplimientoPregunta3().equals("0") ? "SI" :
                                    declaracionJurada.getPoliticaCumplimientoPregunta3().equals("2") ? "EN PROCESO" : "-"
                    : "");
            context.put("param28", declaracionJurada.getPoliticaCumplimientoPregunta4() != null ?
                    declaracionJurada.getPoliticaCumplimientoPregunta4().equals("1") ? "NO" :
                            declaracionJurada.getPoliticaCumplimientoPregunta4().equals("0") ? "SI" :
                                    declaracionJurada.getPoliticaCumplimientoPregunta4().equals("2") ? "EN PROCESO" : "-"
                    : "");
            context.put("param29", declaracionJurada.getPoliticaCumplimientoPregunta5() != null ?
                    declaracionJurada.getPoliticaCumplimientoPregunta5().equals("1") ? "NO" :
                            declaracionJurada.getPoliticaCumplimientoPregunta5().equals("0") ? "SI" :
                                    declaracionJurada.getPoliticaCumplimientoPregunta5().equals("2") ? "EN PROCESO" : "-"
                    : "");
            context.put("param30", declaracionJurada.getPoliticaCumplimientoPregunta6() != null ?
                    declaracionJurada.getPoliticaCumplimientoPregunta6().equals("1") ? "NO" :
                            declaracionJurada.getPoliticaCumplimientoPregunta6().equals("0") ? "SI" :
                                    declaracionJurada.getPoliticaCumplimientoPregunta6().equals("2") ? "EN PROCESO" : "-"
                    : "");
            context.put("param31", declaracionJurada.getPoliticaCumplimientoPregunta7() != null ?
                    declaracionJurada.getPoliticaCumplimientoPregunta7().equals("1") ? "NO" :
                            declaracionJurada.getPoliticaCumplimientoPregunta7().equals("0") ? "SI" :
                                    declaracionJurada.getPoliticaCumplimientoPregunta7().equals("2") ? "EN PROCESO" : "-"
                    : "");

            //REPRESENTANTE LEGAL.
            //DATOS GENERALES DEL REPRESENTANTE LEGAL.
            Optional<ProveedorRepresentanteLegal> proveedorRepresentanteLegal =
                    proveedorRepresentanteLegalRepository.findIdDeclaracionJurada(declaracionJurada.getIdDeclaracionJurada());
            if (proveedorRepresentanteLegal.isPresent()) {
                ProveedorRepresentanteLegal representanteLegal = proveedorRepresentanteLegal.get();
                context.put("param32", representanteLegal.getNombres() != null ? representanteLegal.getNombres() : "");
                context.put("param33", representanteLegal.getApellidos() != null ? representanteLegal.getApellidos() : "");
                context.put("param34", representanteLegal.getNacionalidad().equals(1) ? "PERUANA" : "");
                context.put("param35", representanteLegal.getNacionalidadOtro() != null ? representanteLegal.getNacionalidadOtro() : "");
                context.put("param36", representanteLegal.getEstadoCivil().getDescripcion() != null ? representanteLegal.getEstadoCivil().getDescripcion() : "");
                //conyuque informacion
                context.put("param37", representanteLegal.getNombreConyugeConviviente() != null ? representanteLegal.getNombreConyugeConviviente() : "");

                //TIPO DOCUMENTO
                String tipoDocumentoCo = representanteLegal.getDocumentoIdentidad().equals(1) ? "DNI" :
                        representanteLegal.getDocumentoIdentidad().equals(2) ? "Pasaporte" :
                                representanteLegal.getDocumentoIdentidad().equals(3) ? "Carnet Extranjería" : "";
                context.put("param38", tipoDocumentoCo);
                context.put("param39", representanteLegal.getNumeroDocumento() != null ? representanteLegal.getNumeroDocumento() : "");
            } else {

            }
            //CREAR TABLA PEP
            context.put("param40", declaracionJurada.getEsPep().equals(1) ? "SI" : "NO");
            List<ProveedorPep> proveedorPep = proveedorPepRepository.findIdDeclaracionJurada(declaracionJurada.getIdDeclaracionJurada());
            if (!proveedorPep.isEmpty() && declaracionJurada.getEsPep().equals(1)) {
                ArrayList<ArrayList> arrayLists = new ArrayList<>();

                final String[] tabla2 = {"<table class='tftable' border='1'>" +
                        "<tr>" +
                        "<th>Cargo ejercido</th>" +
                        "<th>Entidad</th>" +
                        "<th>Fecha de inicio</th>" +
                        "<th>Fecha de fin</th>" +
                        "</tr>"};

                proveedorPep.forEach(item -> {
                    ArrayList<String> array = new ArrayList<>();
                    array.add(item.getCargoEjercido() != null ? item.getCargoEjercido() : "");
                    array.add(item.getEntidad() != null ? item.getEntidad() : "");
                    array.add(item.getFechaInicioFin() != null ? item.getFechaInicioFin() : "");
                    array.add(item.getFechaFin() != null ? item.getFechaFin() : "");


                    tabla2[0] += "<tr>" +
                            "<td>" + (item.getCargoEjercido() != null ? item.getCargoEjercido() : "") + "</td>" +
                            "<td>" + (item.getEntidad() != null ? item.getEntidad() : "") + "</td>" +
                            "<td>" + (item.getFechaInicioFin() != null ? item.getFechaInicioFin() : "") + "</td>" +
                            "<td>" + (item.getFechaFin() != null ? item.getFechaFin() : "") + "</td>" +
                            "</tr>";
                    arrayLists.add(array);
                });

                tabla2[0] += "</table>";
                context.put("tabla2", tabla2[0]);
                //Utils.remplazarTextoDocumentoTablaPep(document,"{tabla2",arrayLists);
            } else {
                context.put("tabla2", "");
            }
            //CREAR TABLA PARIENTE PEP
            context.put("param41", declaracionJurada.getEsParientePep() != null ? declaracionJurada.getEsParientePep().equals(1) ? "SI" : "NO" : "NO");


            List<ProveedorParientePep> proveedorParientePeps =
                    proveedorParientePepRepository.findIdDeclaracionJurada(declaracionJurada.getIdDeclaracionJurada());
            if (!proveedorParientePeps.isEmpty() && declaracionJurada.getEsParientePep().equals(1)) {
                ArrayList<ArrayList> arrayLists = new ArrayList<>();

                final String[] tabla3 = {"<table class='tftable' border='1'>" +
                        "<tr>" +
                        "<th>Vínculo parental</th>" +
                        "<th>Nombres y apellidos del Pariente PEP</th>" +
                        "<th>Cargo ejercido</th>" +
                        "<th>Entidad</th>" +
                        "<th>Fecha de inicio</th>" +
                        "<th>Fecha de fin</th>" +
                        "<th>Nacionalidad</th>" +
                        "<th>DNI/Carné Ext/Pass</th>" +
                        "</tr>"};

                proveedorParientePeps.forEach(item -> {
                    ArrayList<String> array = new ArrayList<>();
                    array.add(item.getVinculoParental() != null ? item.getVinculoParental() : "");
                    array.add(item.getNombresParientePep() != null ? item.getNombresParientePep() : "");
                    array.add(item.getCargoEjercidoPariente() != null ? item.getCargoEjercidoPariente() : "");
                    array.add(item.getEntidadPariente() != null ? item.getEntidadPariente() : "");
                    array.add(item.getFechaInicioFinPariente() != null ? item.getFechaInicioFinPariente() : "");
                    array.add(item.getFechaFin() != null ? item.getFechaFin() : "");
                    array.add(item.getNacionalidadPariente() != null ? item.getNacionalidadPariente() : "");
                    array.add(item.getDniPariente() != null ? item.getDniPariente() : "");

                    tabla3[0] += "<tr>" +
                            "<td>" + (item.getVinculoParental() != null ? item.getVinculoParental() : "") + "</td>" +
                            "<td>" + (item.getNombresParientePep() != null ? item.getNombresParientePep() : "") + "</td>" +
                            "<td>" + (item.getCargoEjercidoPariente() != null ? item.getCargoEjercidoPariente() : "") + "</td>" +
                            "<td>" + (item.getEntidadPariente() != null ? item.getEntidadPariente() : "") + "</td>" +
                            "<td>" + (item.getFechaInicioFinPariente() != null ? item.getFechaInicioFinPariente() : "") + "</td>" +
                            "<td>" + (item.getFechaFin() != null ? item.getFechaFin() : "") + "</td>" +
                            "<td>" + (item.getNacionalidadPariente() != null ? item.getNacionalidadPariente() : "") + "</td>" +
                            "<td>" + (item.getDniPariente() != null ? item.getDniPariente() : "") + "</td>" +
                            "</tr>";

                    arrayLists.add(array);
                });

                tabla3[0] += "</table>";
                context.put("tabla3", tabla3[0]);
                //Utils.remplazarTextoDocumentoTablaParientePep(document,"{tabla3",arrayLists);

            } else {
                context.put("tabla3", "");
            }

            context.put("param42", declaracionJurada.getIndAntecedentesPenales().equals(1) ? "SI" : "NO");
            context.put("param43", declaracionJurada.getIndAntecedentesPoliciales().equals(1) ? "SI" : "NO");

            //CREAR TABLA ANTECEDENTES POLICIAL Y PENAL

            List<ProveedorAntecedenteRepresentanteLegal> antecedenteRepresentanteLegal =
                    proveedorAntecedenteRepresentanteLegalRepository.findRepresentanteLegal(proveedorRepresentanteLegal.get().getIdProveedorRepresentanteLegal());
            if (!antecedenteRepresentanteLegal.isEmpty() && declaracionJurada.getIndAntecedentesPoliciales().equals(1)) {

                ArrayList<ArrayList> arrayLists = new ArrayList<>();
                final String[] tabla5 = {"<table class='tftable' border='1'>" +
                        "<tr>" +
                        "<th>Número de antecedente</th>" +
                        "<th>Delitos involucrados en los casos</th>" +
                        "<th>Breve descripción del estado de los casos</th>" +
                        "</tr>"};
                antecedenteRepresentanteLegal.forEach(item -> {
                    ArrayList<String> array = new ArrayList<>();

                    if (item.getTipo().equals("POL")) {
                        array.add(item.getNumeroAntecedente() != null ? item.getNumeroAntecedente() : "");//1
                        array.add(item.getDelitosInvolucrados() != null ? item.getDelitosInvolucrados() : "");//2
                        array.add(item.getDescripcionEstadoCaso() != null ? item.getDescripcionEstadoCaso() : "");//3

                        tabla5[0] = tabla5[0] + "<tr>" +
                                "<td>" + (item.getNumeroAntecedente() != null ? item.getNumeroAntecedente() : "") + "</td>" +
                                "<td>" + (item.getDelitosInvolucrados() != null ? item.getDelitosInvolucrados() : "") + "</td>" +
                                "<td>" + (item.getDescripcionEstadoCaso() != null ? item.getDescripcionEstadoCaso() : "") + "</td>" +
                                "</tr>";
                        arrayLists.add(array);
                    }
                });

                tabla5[0] += "</table>";

                if (arrayLists.size() <= 0) {
                    context.put("tabla5", "");
                } else {
                    try {
                        //Utils.remplazarTextoDocumentoTabla(document,"{tabla1",arrayLists);
                        context.put("tabla5", tabla5[0]);
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new PortalException(e.getMessage());
                    }
                }
            } else {
                context.put("tabla5", "");
            }


            List<ProveedorAntecedenteRepresentanteLegal> antecedenteRepresentanteLegalPenal =
                    proveedorAntecedenteRepresentanteLegalRepository.findRepresentanteLegal(proveedorRepresentanteLegal.get().getIdProveedorRepresentanteLegal());
            if (!antecedenteRepresentanteLegalPenal.isEmpty() && declaracionJurada.getIndAntecedentesPenales().equals(1)) {

                ArrayList<ArrayList> arrayLists = new ArrayList<>();
                final String[] tablaAntecedentePenal1 = {"<table class='tftable' border='1'>" +
                        "<tr>" +
                        "<th>Número de condena</th>" +
                        "<th>Fecha Condena</th>" +
                        "<th>Delitos involucrados</th>" +
                        "</tr>"};
                antecedenteRepresentanteLegalPenal.forEach(item -> {
                    ArrayList<String> array = new ArrayList<>();

                    if (item.getTipo().equals("PEN")) {
                        array.add(item.getNumeroAntecedente() != null ? item.getNumeroAntecedente() : "");//1
                        array.add(item.getFechaCondena() != null ? simpleDateFormat.format(item.getFechaCondena()) : "");//2
                        array.add(item.getDelitosInvolucrados() != null ? item.getDelitosInvolucrados() : "");//2


                        tablaAntecedentePenal1[0] = tablaAntecedentePenal1[0] + "<tr>" +
                                "<td>" + (item.getNumeroAntecedente() != null ? item.getNumeroAntecedente() : "") + "</td>" +
                                "<td>" + (item.getFechaCondena() != null ? simpleDateFormat.format(item.getFechaCondena()) : "") + "</td>" +
                                "<td>" + (item.getDelitosInvolucrados() != null ? item.getDelitosInvolucrados() : "") + "</td>" +
                                "</tr>";
                        arrayLists.add(array);
                    }
                });

                tablaAntecedentePenal1[0] += "</table>";

                if (arrayLists.size() <= 0) {
                    context.put("tablaAntecedentePenal1", "");
                } else {
                    try {
                        //Utils.remplazarTextoDocumentoTabla(document,"{tabla1",arrayLists);
                        context.put("tablaAntecedentePenal1", tablaAntecedentePenal1[0]);
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new PortalException(e.getMessage());
                    }
                }
            } else {
                context.put("tablaAntecedentePenal1", " ");
            }

            List<ProveedorAccionistasAsociados> accionistasAsociados =
                    accionistasAsociadosRepository.findIdDeclaracionJurada(declaracionJurada.getIdDeclaracionJurada());
            ArrayList<ArrayList> arrayListsAcionistaPep = new ArrayList<>();
            ArrayList<ArrayList> arrayListsAcionistaParientePep = new ArrayList<>();
            if (!accionistasAsociados.isEmpty()) {
                ArrayList<ArrayList> arrayLists = new ArrayList<>();

                final String[] tablaPep1 = {"<table class='tftable' border='1'>" +
                        "<tr>" +
                        "<th>Cargo ejercido</th>" +
                        "<th>Entidad</th>" +
                        "<th>Fecha de Inicio </th>" +
                        "<th>Fecha de Fin</th>" +
                        "</tr>"};

                final String[] tablaParientePep1 = {"<table class='tftable' border='1'>" +
                        "<tr>" +
                        "<th>Vínculo parental</th>" +
                        "<th>Nombres y apellidos del Pariente PEP</th>" +
                        "<th>Cargo ejercido</th>" +
                        "<th>Entidad</th>" +
                        "<th>Fecha de inicio</th>" +
                        "<th>Fecha de fin</th>" +
                        "<th>Nacionalidad</th>" +
                        "<th>DNI/Carné Ext/Pass</th>" +
                        "</tr>"};

                final String[] tablaAccionistas1 = {"<table class='tftable' border='1'>" +
                        "<tr>" +
                        "<th>Nombres y apellidos / Razón o Denominación Social (*)</th>" +
                        "<th>Nacionalidad</th>" +
                        "<th>Otra</th>" +
                        "<th>Documento de identidad</th>" +
                        "<th>No.</th>" +
                        "<th>R.U.C. No. o equivalente.</th>" +
                        "<th>Porcentaje de participación</th>" +
                        "</tr>"};

                accionistasAsociados.forEach(accionista -> {
                    ArrayList<String> array = new ArrayList<>();

                    array.add(accionista.getNombreRazonSocial() != null ? accionista.getNombreRazonSocial() : "");
                    array.add(accionista.getNacionalidad().equals(1) ? "PERUANA" : "OTRA");
                    array.add(accionista.getNacionalidadOtro() != null ? accionista.getNacionalidadOtro() : "");
                    //TIPO DOCUMENTO
                    String tipoDocumentoAc = accionista.getDocumentoIdentidad().equals("1") ? "DNI" :
                            accionista.getDocumentoIdentidad().equals("2") ? "Pasaporte" :
                                    accionista.getDocumentoIdentidad().equals("3") ? "Carnet Extranjería" : "";
                    array.add(tipoDocumentoAc);
                    array.add(accionista.getNumeroDocumento() != null ? accionista.getNumeroDocumento() : "");
                    array.add(accionista.getRucEquivalente() != null ? accionista.getRucEquivalente() : "");
                    array.add(accionista.getProcentajeParticipacion() != null ? accionista.getProcentajeParticipacion() : "");

                    tablaAccionistas1[0] += "<tr>" +
                            "<td>" + (accionista.getNombreRazonSocial() != null ? accionista.getNombreRazonSocial() : "") + "</td>" +
                            "<td>" + (accionista.getNacionalidad().equals(1) ? "PERUANA" : "OTRA") + "</td>" +
                            "<td>" + (accionista.getNacionalidadOtro() != null ? accionista.getNacionalidadOtro() : "") + "</td>" +
                            "<td>" + (tipoDocumentoAc) + "</td>" +
                            "<td>" + (accionista.getNumeroDocumento() != null ? accionista.getNumeroDocumento() : "") + "</td>" +
                            "<td>" + (accionista.getRucEquivalente() != null ? accionista.getRucEquivalente() : "") + "</td>" +
                            "<td>" + (accionista.getProcentajeParticipacion() != null ? accionista.getProcentajeParticipacion() : "") + "</td>" +
                            "</tr>";

                    arrayLists.add(array);


                    List<ProveedorAccionistasPep> accionistasPep =
                            proveedorAccionistaPepRepository.findIdProveedorAccionista(accionista.getIdProveedorAccionistasAsociados());

                    //PEP ACCIONISTA
                    if (accionistasPep.size() > 0) {

                        accionistasPep.forEach(item -> {

                            ArrayList<String> arrayPep = new ArrayList<>();
                            arrayPep.add(item.getCargoEjercido() != null ? item.getCargoEjercido() : "");
                            arrayPep.add(item.getEntidad() != null ? item.getEntidad() : "");
                            arrayPep.add(item.getFechaInicioFin() != null ? item.getFechaInicioFin().replace("T05:00:00.000Z", "") : "");

                            tablaPep1[0] += "<tr>" +
                                    "<td>" + (item.getCargoEjercido() != null ? item.getCargoEjercido() : "") + "</td>" +
                                    "<td>" + (item.getEntidad() != null ? item.getEntidad() : "") + "</td>" +
                                    "<td>" + (item.getFechaInicioFin() != null ? item.getFechaInicioFin().replace("T05:00:00.000Z", "") : "") + "</td>" +
                                    "<td>" + (item.getFechaFin() != null ? item.getFechaFin().replace("T05:00:00.000Z", "") : "") + "</td>" +
                                    "</tr>";

                            arrayListsAcionistaPep.add(arrayPep);
                        });

                    }


                    //PARIENTE PEP
                    List<ProveedorAccionistasParientePep> accionistasParientePeps =
                            proveedorAccionistaParientePepRepository.findIdProveedorAccionistaPariente(accionista.getIdProveedorAccionistasAsociados());
                    if (accionistasParientePeps.size() > 0) {

                        accionistasParientePeps.forEach(item -> {
                            ArrayList<String> arrayParientePep = new ArrayList<>();
                            arrayParientePep.add(item.getVinculoParental() != null ? item.getVinculoParental() : "");
                            arrayParientePep.add(item.getNombresParientePep() != null ? item.getNombresParientePep() : "");
                            arrayParientePep.add(item.getCargoEjercidoPariente() != null ? item.getCargoEjercidoPariente() : "");
                            arrayParientePep.add(item.getEntidadPariente() != null ? item.getEntidadPariente() : "");
                            arrayParientePep.add(item.getFechaInicioFinPariente() != null ? item.getFechaInicioFinPariente().replace("T05:00:00.000Z", "") : "");
                            arrayParientePep.add(item.getFechaFinPariente() != null ? item.getFechaFinPariente().replace("T05:00:00.000Z", "") : "");
                            arrayParientePep.add(item.getNacionalidadPariente() != null ? item.getNacionalidadPariente() : "");
                            arrayParientePep.add(item.getDniPariente() != null ? item.getDniPariente() : "");

                            tablaParientePep1[0] += "<tr>" +
                                    "<td>" + (item.getVinculoParental() != null ? item.getVinculoParental() : "") + "</td>" +
                                    "<td>" + (item.getNombresParientePep() != null ? item.getNombresParientePep() : "") + "</td>" +
                                    "<td>" + (item.getCargoEjercidoPariente() != null ? item.getCargoEjercidoPariente() : "") + "</td>" +
                                    "<td>" + (item.getEntidadPariente() != null ? item.getEntidadPariente() : "") + "</td>" +
                                    "<td>" + (item.getFechaInicioFinPariente() != null ? item.getFechaInicioFinPariente().replace("T05:00:00.000Z", "") : "") + "</td>" +
                                    "<td>" + (item.getFechaFinPariente() != null ? item.getFechaFinPariente().replace("T05:00:00.000Z", "") : "") + "</td>" +
                                    "<td>" + (item.getNacionalidadPariente() != null ? item.getNacionalidadPariente() : "") + "</td>" +
                                    "<td>" + (item.getDniPariente() != null ? item.getDniPariente() : "") + "</td>" +
                                    "</tr>";

                            arrayListsAcionistaParientePep.add(arrayParientePep);
                        });


                    }
                });

                tablaParientePep1[0] += "</table>";
                tablaPep1[0] += "</table>";
                tablaAccionistas1[0] += "</table>";


                if (arrayLists.size() > 0) {
                    System.out.println("");
                    //Utils.remplazarTextoDocumentoTablaAccionistas(document,"{tablaAccionistas1",arrayLists);
                    context.put("tablaAccionistas1", tablaAccionistas1[0]);
                } else {
                    context.put("tablaAccionistas1", "");
                }

                //agregar Pep accionista
                if (arrayListsAcionistaPep.size() > 0) {
                    System.out.println("");
                    //Utils.remplazarTextoDocumentoTablaPepAccionista(document,"{tablaPep1",arrayListsAcionistaPep);
                    context.put("tablaPep1", tablaPep1[0]);
                } else {
                    context.put("tablaPep1", "");
                }

//                agregar al documento Pariente accionista Pep
                if (arrayListsAcionistaParientePep.size() > 0) {
                    System.out.println("");
                    context.put("tablaParientePep1", tablaParientePep1[0]);
                    //Utils.remplazarTextoDocumentoTablaParientePepAccionista(document,"{tablaParientePep1",arrayListsAcionistaParientePep);
                } else {
                    context.put("tablaParientePep1", "");
                }


            } else {
                context.put("tablaPep1", "");
                context.put("tablaParientePep1", "");
                context.put("tablaAccionistas1", "");
            }

        } else {
            throw new Exception("No fue posible encontrar al proveedor :" + idProveedor);
        }
        String content = Optional.ofNullable(data)
                .map(url -> url + "")
                .map(template -> {
                    int i = 0;
                    return getContentMail(context, template);
                })
                .orElse("");
        System.out.println("content");

        return content;
    }

    //documento PJ
    public XWPFDocument getTipoDocumentoPJ(Integer idProveedor) throws Exception {

        InputStream is = this.getClass().getResourceAsStream("/word/PJ-BACKEND-3.docx");
        //String file ="src/main/resources/word/PJ-BACKEND-3.docx";
        XWPFDocument document = new XWPFDocument(is);

        Optional<Proveedor> proveedor = proveedorRepository.findById(idProveedor);
        if (!proveedor.isPresent()) {
            throw new PortalException("No se encontró al proveedor ");
        }
        Proveedor proveedorActual = proveedor.get();

        List<ProveedorDeclaracionJurada> proveedorDeclaracionJurada =
                proveedorDeclaracionJuradaRepository.findIdProveedorTipoPersona(idProveedor, "PJ");

        if (proveedorDeclaracionJurada.size() > 0) {
            //ARMAR LA TRAMA PARA CREAR EL DOCUMENTO
            String pattern = "dd-MM-yyyy";//dd-MM-yyyy
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            ProveedorDeclaracionJurada declaracionJurada = proveedorDeclaracionJurada.get(0);

            Utils.remplazarTextoDocumento(document, "{param1", proveedorActual.getRazonSocial());
            Utils.remplazarTextoDocumento(document, "{param2", simpleDateFormat.format(declaracionJurada.getFechaSociedad()));
            Utils.remplazarTextoDocumento(document, "{param3", declaracionJurada.getNombreComercial());
            Utils.remplazarTextoDocumento(document, "{param4", declaracionJurada.getGrupoEconomico());
            //TIPO DOCUMENTO
            String tipoDocumento = declaracionJurada.getTipoDocumento().equals(4) ? "RUC" :
                    declaracionJurada.getTipoDocumento().equals(5) ? "OTRO" : "N/A";
            Utils.remplazarTextoDocumento(document, "{param5", tipoDocumento);
            Utils.remplazarTextoDocumento(document, "{param6", proveedorActual.getRuc());

            //Informacion fiscal
            Utils.remplazarTextoDocumento(document, "{param7", declaracionJurada.getPartidaRegistral());
            Utils.remplazarTextoDocumento(document, "{param8", declaracionJurada.getOficinaRegistral());
            Utils.remplazarTextoDocumento(document, "{param10", proveedorActual.getDireccionFiscal());
            Utils.remplazarTextoDocumento(document, "{param11", proveedorActual.getDistrito() != null ? proveedorActual.getDistrito().getDescripcion() : "");
            Utils.remplazarTextoDocumento(document, "{param12", proveedorActual.getProvincia() != null ? proveedorActual.getProvincia().getDescripcion() : "");
            Utils.remplazarTextoDocumento(document, "{param13", proveedorActual.getRegion() != null ? proveedorActual.getRegion().getDescripcion() : "");
            Utils.remplazarTextoDocumento(document, "{param14", proveedorActual.getPais() != null ? proveedorActual.getPais().getDescripcion() : "");
            //Informacion oficina fiscal
            Utils.remplazarTextoDocumento(document, "{param15", declaracionJurada.getDomicilioOficina());
            Utils.remplazarTextoDocumento(document, "{param16", declaracionJurada.getDistritoOficina() != null ? declaracionJurada.getDistritoOficina().getDescripcion() : "");
            Utils.remplazarTextoDocumento(document, "{param17", declaracionJurada.getProvinciaOficina() != null ? declaracionJurada.getProvinciaOficina().getDescripcion() : "");
            Utils.remplazarTextoDocumento(document, "{param18", declaracionJurada.getDepartamentoOficina() != null ? declaracionJurada.getDepartamentoOficina().getDescripcion() : "");
            Utils.remplazarTextoDocumento(document, "{param19", declaracionJurada.getPaisOficina() != null ? declaracionJurada.getPaisOficina().getDescripcion() : "");
            //actividad economica
            Utils.remplazarTextoDocumento(document, "{param20", declaracionJurada.getActividadEconomica());
            Utils.remplazarTextoDocumento(document, "{param21", declaracionJurada.getAniosExperiencia());
            Utils.remplazarTextoDocumento(document, "{param22", declaracionJurada.getRubrosOpera());


            //insertar Tabla antecedentes penales y policiales
            Utils.remplazarTextoDocumento(document, "{param23", declaracionJurada.getIndAntecedentesPenales().equals(1) ? "SI" : "NO");
            Utils.remplazarTextoDocumento(document, "{param24", declaracionJurada.getIndAntecedentesPoliciales().equals(1) ? "SI" : "NO");


            List<ProveedorAntecedente> proveedorAntecedente =
                    proveedorAntecedenteRepository.findProveedorDeclaracionJurada(declaracionJurada.getIdDeclaracionJurada());
            if (!proveedorAntecedente.isEmpty()) {

                ArrayList<ArrayList> arrayLists = new ArrayList<>();
                proveedorAntecedente.forEach(item -> {
                    ArrayList<String> array = new ArrayList<>();
                    array.add(item.getNumeroAntecedente() != null ? item.getNumeroAntecedente() : "");//1
                    array.add(item.getDelitosInvolucrados() != null ? item.getDelitosInvolucrados() : "");//2
                    array.add(item.getDescripcionEstadoCaso() != null ? item.getDescripcionEstadoCaso() : "");//3
                    array.add(item.getFechaCondena() != null ? simpleDateFormat.format(item.getFechaCondena()) : "");//4
                    array.add(item.getTipo().equals("POL") ? "Policial" : "Penal");//5
                    arrayLists.add(array);
                });
                if (arrayLists.size() <= 0) {
                    Utils.remplazarTextoDocumento(document, "{tabla1", "");
                } else {
                    try {
                        Utils.remplazarTextoDocumentoTabla(document, "{tabla1", arrayLists);
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new PortalException(e.getMessage());
                    }
                }
            } else {
                Utils.remplazarTextoDocumento(document, "{tabla1", "");
            }

            //POLÍTICAS DE CUMPLIMIENTO DE LA SOCIEDAD:
            Utils.remplazarTextoDocumento(document, "{param25", declaracionJurada.getPoliticaCumplimientoPregunta1() != null ?
                    declaracionJurada.getPoliticaCumplimientoPregunta1().equals("1") ? "NO" :
                            declaracionJurada.getPoliticaCumplimientoPregunta1().equals("0") ? "SI" :
                                    declaracionJurada.getPoliticaCumplimientoPregunta1().equals("2") ? "EN PROCESO" : "-"
                    : "");
            Utils.remplazarTextoDocumento(document, "{param26", declaracionJurada.getPoliticaCumplimientoPregunta2() != null ?
                    declaracionJurada.getPoliticaCumplimientoPregunta2().equals("1") ? "NO" :
                            declaracionJurada.getPoliticaCumplimientoPregunta2().equals("0") ? "SI" :
                                    declaracionJurada.getPoliticaCumplimientoPregunta2().equals("2") ? "EN PROCESO" : "-"
                    : "");
            Utils.remplazarTextoDocumento(document, "{param27", declaracionJurada.getPoliticaCumplimientoPregunta3() != null ?
                    declaracionJurada.getPoliticaCumplimientoPregunta3().equals("1") ? "NO" :
                            declaracionJurada.getPoliticaCumplimientoPregunta3().equals("0") ? "SI" :
                                    declaracionJurada.getPoliticaCumplimientoPregunta3().equals("2") ? "EN PROCESO" : "-"
                    : "");
            Utils.remplazarTextoDocumento(document, "{param28", declaracionJurada.getPoliticaCumplimientoPregunta4() != null ?
                    declaracionJurada.getPoliticaCumplimientoPregunta4().equals("1") ? "NO" :
                            declaracionJurada.getPoliticaCumplimientoPregunta4().equals("0") ? "SI" :
                                    declaracionJurada.getPoliticaCumplimientoPregunta4().equals("2") ? "EN PROCESO" : "-"
                    : "");
            Utils.remplazarTextoDocumento(document, "{param29", declaracionJurada.getPoliticaCumplimientoPregunta5() != null ?
                    declaracionJurada.getPoliticaCumplimientoPregunta5().equals("1") ? "NO" :
                            declaracionJurada.getPoliticaCumplimientoPregunta5().equals("0") ? "SI" :
                                    declaracionJurada.getPoliticaCumplimientoPregunta5().equals("2") ? "EN PROCESO" : "-"
                    : "");
            Utils.remplazarTextoDocumento(document, "{param30", declaracionJurada.getPoliticaCumplimientoPregunta6() != null ?
                    declaracionJurada.getPoliticaCumplimientoPregunta6().equals("1") ? "NO" :
                            declaracionJurada.getPoliticaCumplimientoPregunta6().equals("0") ? "SI" :
                                    declaracionJurada.getPoliticaCumplimientoPregunta6().equals("2") ? "EN PROCESO" : "-"
                    : "");
            Utils.remplazarTextoDocumento(document, "{param31", declaracionJurada.getPoliticaCumplimientoPregunta7() != null ?
                    declaracionJurada.getPoliticaCumplimientoPregunta7().equals("1") ? "NO" :
                            declaracionJurada.getPoliticaCumplimientoPregunta7().equals("0") ? "SI" :
                                    declaracionJurada.getPoliticaCumplimientoPregunta7().equals("2") ? "EN PROCESO" : "-"
                    : "");

            //REPRESENTANTE LEGAL.
            //DATOS GENERALES DEL REPRESENTANTE LEGAL.
            Optional<ProveedorRepresentanteLegal> proveedorRepresentanteLegal =
                    proveedorRepresentanteLegalRepository.findIdDeclaracionJurada(declaracionJurada.getIdDeclaracionJurada());
            if (proveedorRepresentanteLegal.isPresent()) {
                ProveedorRepresentanteLegal representanteLegal = proveedorRepresentanteLegal.get();
                Utils.remplazarTextoDocumento(document, "{param32", representanteLegal.getNombres() != null ? representanteLegal.getNombres() : "");
                Utils.remplazarTextoDocumento(document, "{param33", representanteLegal.getApellidos() != null ? representanteLegal.getApellidos() : "");
                Utils.remplazarTextoDocumento(document, "{param34", representanteLegal.getNacionalidad().equals(1) ? "PERUANA" : "");
                Utils.remplazarTextoDocumento(document, "{param35", representanteLegal.getNacionalidadOtro() != null ? representanteLegal.getNacionalidadOtro() : "");
                Utils.remplazarTextoDocumento(document, "{param36", representanteLegal.getEstadoCivil().getDescripcion() != null ? representanteLegal.getEstadoCivil().getDescripcion() : "");
                //conyuque informacion
                Utils.remplazarTextoDocumento(document, "{param37", representanteLegal.getNombreConyugeConviviente() != null ? representanteLegal.getNombreConyugeConviviente() : "");

                //TIPO DOCUMENTO
                String tipoDocumentoCo = representanteLegal.getDocumentoIdentidad().equals(1) ? "DNI" :
                        representanteLegal.getDocumentoIdentidad().equals(2) ? "Pasaporte" :
                                representanteLegal.getDocumentoIdentidad().equals(3) ? "Carnet Extranjería" : "";
                Utils.remplazarTextoDocumento(document, "{param38", tipoDocumentoCo);
                Utils.remplazarTextoDocumento(document, "{param39", representanteLegal.getNumeroDocumento() != null ? representanteLegal.getNumeroDocumento() : "");
            } else {

            }
            //CREAR TABLA PEP
            Utils.remplazarTextoDocumento(document, "{param40", declaracionJurada.getEsPep().equals(1) ? "SI" : "NO");
            List<ProveedorPep> proveedorPep = proveedorPepRepository.findIdDeclaracionJurada(declaracionJurada.getIdDeclaracionJurada());
            if (!proveedorPep.isEmpty()) {
                ArrayList<ArrayList> arrayLists = new ArrayList<>();
                proveedorPep.forEach(item -> {
                    ArrayList<String> array = new ArrayList<>();
                    array.add(item.getCargoEjercido() != null ? item.getCargoEjercido() : "");
                    array.add(item.getEntidad() != null ? item.getEntidad() : "");
                    array.add(item.getFechaInicioFin() != null ? item.getFechaInicioFin() : "");
                    arrayLists.add(array);
                });
                Utils.remplazarTextoDocumentoTablaPep(document, "{tabla2", arrayLists);
            } else {
                Utils.remplazarTextoDocumento(document, "{tabla2", "");
            }
            //CREAR TABLA PARIENTE PEP
            Utils.remplazarTextoDocumento(document, "{param41", declaracionJurada.getEsParientePep() != null ? declaracionJurada.getEsParientePep().equals(1) ? "SI" : "NO" : "NO");
            List<ProveedorParientePep> proveedorParientePeps =
                    proveedorParientePepRepository.findIdDeclaracionJurada(declaracionJurada.getIdDeclaracionJurada());
            if (!proveedorParientePeps.isEmpty()) {
                ArrayList<ArrayList> arrayLists = new ArrayList<>();
                proveedorParientePeps.forEach(item -> {
                    ArrayList<String> array = new ArrayList<>();
                    array.add(item.getVinculoParental() != null ? item.getVinculoParental() : "");
                    array.add(item.getNombresParientePep() != null ? item.getNombresParientePep() : "");
                    array.add(item.getCargoEjercidoPariente() != null ? item.getCargoEjercidoPariente() : "");
                    array.add(item.getEntidadPariente() != null ? item.getEntidadPariente() : "");
                    array.add(item.getFechaInicioFinPariente() != null ? item.getFechaInicioFinPariente() : "");
                    array.add(item.getNacionalidadPariente() != null ? item.getNacionalidadPariente() : "");
                    array.add(item.getDniPariente() != null ? item.getDniPariente() : "");
                    arrayLists.add(array);
                });
                Utils.remplazarTextoDocumentoTablaParientePep(document, "{tabla3", arrayLists);
            } else {
                Utils.remplazarTextoDocumento(document, "{tabla3", "");
            }
            Utils.remplazarTextoDocumento(document, "{param42", declaracionJurada.getIndAntecedentesPenales().equals(1) ? "SI" : "NO");
            Utils.remplazarTextoDocumento(document, "{param43", declaracionJurada.getIndAntecedentesPoliciales().equals(1) ? "SI" : "NO");

            //CREAR TABLA ANTECEDENTES POLICIAL Y PENAL
            List<ProveedorAntecedenteRepresentanteLegal> antecedenteRepresentanteLegal =
                    proveedorAntecedenteRepresentanteLegalRepository.findRepresentanteLegal(proveedorRepresentanteLegal.get().getIdProveedorRepresentanteLegal());
            if (!antecedenteRepresentanteLegal.isEmpty()) {

                ArrayList<ArrayList> arrayLists = new ArrayList<>();
                antecedenteRepresentanteLegal.forEach(item -> {
                    ArrayList<String> array = new ArrayList<>();
                    array.add(item.getNumeroAntecedente() != null ? item.getNumeroAntecedente() : "");//1
                    array.add(item.getDelitosInvolucrados() != null ? item.getDelitosInvolucrados() : "");//2
                    array.add(item.getDescripcionEstadoCaso() != null ? item.getDescripcionEstadoCaso() : "");//3
                    array.add(item.getFechaCondena() != null ? simpleDateFormat.format(item.getFechaCondena()) : "");//4
                    array.add(item.getTipo().equals("POL") ? "Policial" : "Penal");//5
                    arrayLists.add(array);
                });
                if (arrayLists.size() <= 0) {
                    Utils.remplazarTextoDocumento(document, "{tabla5", "");
                } else {
                    try {
                        Utils.remplazarTextoDocumentoTabla5Representante(document, "{tabla5", arrayLists);
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new PortalException(e.getMessage());
                    }
                }
            } else {
                Utils.remplazarTextoDocumento(document, "{tabla5", "");
            }

            List<ProveedorAccionistasAsociados> accionistasAsociados =
                    accionistasAsociadosRepository.findIdDeclaracionJurada(declaracionJurada.getIdDeclaracionJurada());
            ArrayList<ArrayList> arrayListsAcionistaPep = new ArrayList<>();
            ArrayList<ArrayList> arrayListsAcionistaParientePep = new ArrayList<>();
            if (!accionistasAsociados.isEmpty()) {
                ArrayList<ArrayList> arrayLists = new ArrayList<>();
                accionistasAsociados.forEach(accionista -> {
                    ArrayList<String> array = new ArrayList<>();

                    array.add(accionista.getNombreRazonSocial() != null ? accionista.getNombreRazonSocial() : "");
                    array.add(accionista.getNacionalidad().equals(1) ? "PERUANA" : "OTRA");
                    array.add(accionista.getNacionalidadOtro() != null ? accionista.getNacionalidadOtro() : "");
                    //TIPO DOCUMENTO
                    String tipoDocumentoAc = accionista.getDocumentoIdentidad().equals("1") ? "DNI" :
                            accionista.getDocumentoIdentidad().equals("2") ? "Pasaporte" :
                                    accionista.getDocumentoIdentidad().equals("3") ? "Carnet Extranjería" : "";
                    array.add(tipoDocumentoAc);
                    array.add(accionista.getNumeroDocumento() != null ? accionista.getNumeroDocumento() : "");
                    array.add(accionista.getRucEquivalente() != null ? accionista.getRucEquivalente() : "");
                    array.add(accionista.getProcentajeParticipacion() != null ? accionista.getProcentajeParticipacion() : "");
                    arrayLists.add(array);
                    List<ProveedorAccionistasPep> accionistasPep =
                            proveedorAccionistaPepRepository.findIdProveedorAccionista(accionista.getIdProveedorAccionistasAsociados());
                    //PEP ACCIONISTA
                    if (accionistasPep.size() > 0) {
                        accionistasPep.forEach(item -> {
                            ArrayList<String> arrayPep = new ArrayList<>();
                            arrayPep.add(item.getCargoEjercido() != null ? item.getCargoEjercido() : "");
                            arrayPep.add(item.getEntidad() != null ? item.getEntidad() : "");
                            arrayPep.add(item.getFechaInicioFin() != null ? item.getFechaInicioFin().replace("T05:00:00.000Z", "") : "");
                            arrayListsAcionistaPep.add(arrayPep);
                        });
                    }
                    //PARIENTE PEP
                    List<ProveedorAccionistasParientePep> accionistasParientePeps =
                            proveedorAccionistaParientePepRepository.findIdProveedorAccionistaPariente(accionista.getIdProveedorAccionistasAsociados());
                    if (accionistasParientePeps.size() > 0) {
                        accionistasParientePeps.forEach(item -> {
                            ArrayList<String> arrayParientePep = new ArrayList<>();
                            arrayParientePep.add(item.getVinculoParental() != null ? item.getVinculoParental() : "");
                            arrayParientePep.add(item.getNombresParientePep() != null ? item.getNombresParientePep() : "");
                            arrayParientePep.add(item.getCargoEjercidoPariente() != null ? item.getCargoEjercidoPariente() : "");
                            arrayParientePep.add(item.getEntidadPariente() != null ? item.getEntidadPariente() : "");
                            arrayParientePep.add(item.getFechaInicioFinPariente() != null ? item.getFechaInicioFinPariente().replace("T05:00:00.000Z", "") : "");
                            arrayParientePep.add(item.getNacionalidadPariente() != null ? item.getNacionalidadPariente() : "");
                            arrayParientePep.add(item.getDniPariente() != null ? item.getDniPariente() : "");
                            arrayListsAcionistaParientePep.add(arrayParientePep);
                        });
                    }
                });

                Utils.remplazarTextoDocumentoTablaAccionistas(document, "{tablaAccionistas1", arrayLists);
                //agregar Pep accionista
                if (arrayListsAcionistaPep.size() > 0) {
                    System.out.println("");
                    Utils.remplazarTextoDocumentoTablaPepAccionista(document, "{tablaPep1", arrayListsAcionistaPep);
                } else {
                    Utils.remplazarTextoDocumento(document, "{tablaPep1", "");
                }

//                agregar al documento Pariente accionista Pep
                if (arrayListsAcionistaParientePep.size() > 0) {
                    System.out.println("");
                    Utils.remplazarTextoDocumentoTablaParientePepAccionista(document, "{tablaParientePep1", arrayListsAcionistaParientePep);
                } else {
                    Utils.remplazarTextoDocumento(document, "{tablaParientePep1", "");
                }


            } else {
                Utils.remplazarTextoDocumento(document, "{tablaPep1", "");
                Utils.remplazarTextoDocumento(document, "{tablaParientePep1", "");
                Utils.remplazarTextoDocumento(document, "{tablaAccionistas1", "");
            }


        } else {
            throw new Exception("No fue posible encontrar al proveedor :" + idProveedor);
        }
        System.out.println("" + document);
        return document;
    }


    public byte[] docxToPdf(InputStream docxStream) throws Exception {
        ByteArrayOutputStream targetStream = null;
        XWPFDocument doc = null;
        try {
            doc = new XWPFDocument(docxStream);

            PdfOptions options = PdfOptions.create();

            targetStream = new ByteArrayOutputStream();
            doc.createNumbering();
            PdfConverter.getInstance().convert(doc, targetStream, options);

            return targetStream.toByteArray();
        } catch (IOException e) {
            throw new Exception(e);
        } finally {
            IOUtils.closeQuietly(targetStream);
        }
    }

    String getContentMail(VelocityContext context, String template) {
        logger.error("Generando el contenido de la notificación");
        VelocityEngine velocity = getVelocityEngine();
        Template t = velocity.getTemplate(template);
        StringWriter w = new StringWriter();
        t.merge(context, w);
        return w.toString();
    }

    VelocityEngine getVelocityEngine() {
        logger.error("Inicialización de VelocityEngine");
        VelocityEngine velocity = new VelocityEngine();
        velocity.setProperty(Velocity.RESOURCE_LOADER, "classpath");
        velocity.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        velocity.setProperty("input.encoding", "UTF-8");
        velocity.init();
        return velocity;
    }


    @Override
    public ProveedorAdjuntoCuentaBancaria uploadAdjuntoCuentaBancaria(Integer idProveedor, MultipartFile file) throws Exception {
        Proveedor proveedor = proveedorRepository.getById(idProveedor);
        String folderMainId = cmisBaseServicecf.createFolder("proveedores").getId();
        String newFolder = proveedor.getRuc() + "_" + proveedor.getIdProveedor();
        String folderId = cmisBaseServicecf.SubCreateFolder(folderMainId, newFolder).getId();
        String folderFinalId = cmisBaseServicecf.SubCreateFolder(folderId, "cta_bancaria").getId();
        String nameFolderFinal = "proveedores/" + newFolder + "";
        logger.error("paso uno adjuntar "+nameFolderFinal);
        CmisFile cmisFile = cmisBaseServicecf.createDocumento("/" + folderFinalId, file, nameFolderFinal, null);
        //CmisFile cmisFile = cmisBaseServicecf.createDocumento("adjuntosCtaBancariaProveedor", file);
        ProveedorAdjuntoCuentaBancaria proveedorAdjuntoCuentaBancaria = new ProveedorAdjuntoCuentaBancaria();
        proveedorAdjuntoCuentaBancaria.setArchivoId(cmisFile.getId());
        proveedorAdjuntoCuentaBancaria.setArchivoNombre(cmisFile.getName());
        proveedorAdjuntoCuentaBancaria.setArchivoExtension(cmisFile.getExtension());
        proveedorAdjuntoCuentaBancaria.setArchivoTipo(cmisFile.getType());
        proveedorAdjuntoCuentaBancaria.setRutaAdjunto(cmisFile.getUrl());
        proveedorAdjuntoCuentaBancaria.setIdProveedor(proveedor);

        ProveedorAdjuntoCuentaBancaria proveedorAdjuntoCuentaBancariaNew = proveedorAdjuntoCuentaBancariaRepository.save(proveedorAdjuntoCuentaBancaria);

        return proveedorAdjuntoCuentaBancariaNew;
    }

    /*@Override
    public ProveedorCuentaBancaria cargarAdjuntoCuentaBancaria(Proveedor proveedor,Integer idCuenta, AdjuntoCargaDto adjuntoCargaDto) throws Exception {
        Object cmisFile =  cmisBaseServicecf.crearDocumentoCuentaBancaria(adjuntoCargaDto,proveedor);
        ProveedorCuentaBancaria proveedorCuentaBancaria = proveedorCuentaBancariaRepository.getById(idCuenta);
        proveedorCuentaBancaria.setArchivoId(cmisFile.getId());
        proveedorCuentaBancaria.setArchivoNombre(cmisFile.getName());
        proveedorCuentaBancaria.setArchivoTipo(cmisFile.getType());
        proveedorCuentaBancaria.setRutaAdjunto(cmisFile.getUrl());
        ProveedorCuentaBancaria proveedorCuentaBancariaNew = proveedorCuentaBancariaRepository.save(proveedorCuentaBancaria);

        return proveedorCuentaBancariaNew;
    }*/

    @Override
    public List<ProveedorAdjuntoCuentaBancaria> getListAdjuntoCuentaBancariaByProveedor(Integer idProveedor) throws Exception {
        List<ProveedorAdjuntoCuentaBancaria> proveedorAdjuntoCuentaBancariaList = new ArrayList<ProveedorAdjuntoCuentaBancaria>();
        Proveedor proveedor = proveedorRepository.getById(idProveedor) == null ? null : proveedorRepository.getById(idProveedor);
        if (proveedor != null) {
            proveedorAdjuntoCuentaBancariaList = proveedorAdjuntoCuentaBancariaRepository.findAllByIdProveedor(proveedor);
        }
        return proveedorAdjuntoCuentaBancariaList;
    }

    @Override
    public List<Proveedor> insertMigrProveedortoProveedor(InputStream inputStream) {
        try {
            this.migProveedorRepository.deleteAll();
            List<MigProveedor> lista = CsvUtils.readPuntoComa(MigProveedor.class, inputStream);

            for (MigProveedor bean : lista) {
                this.migProveedorRepository.save(bean);
            }
            List<MigProveedor> bdList = migProveedorRepository.findAll();
            List<Proveedor> proveedorList = new ArrayList<>();
            bdList.forEach(mig -> {
                Proveedor proveedor = new Proveedor();
                proveedor.setAcreedorCodigoSap(mig.getAcreedorCodigoSap());
                proveedor.setTelefono(mig.getTelefono());
                proveedor.setDireccionFiscal(mig.getDireccionFiscal());
                proveedor.setEmail(mig.getEmail());
                proveedor.setEmailRetencion(mig.getEmailRetencion());
                proveedor.setEvaluacionDesempeno(new BigDecimal(0));
                proveedor.setEvaluacionHomologacion(new BigDecimal(0));
                proveedor.setFechaCreacion(new Date());
                proveedor.setRazonSocial(mig.getRazonSocial());
                proveedor.setRuc(mig.getRuc());
                proveedor.setTipoPersona(mig.getTipoPersona());
                proveedor.setUsuarioCreacion(1);

                List <Parametro> parametro = this.parametroRepository.findByModuloAndTipoAndCodigo("DETRACCION","INDICADOR_DETRACCION",mig.getIndDetraccion());
                if (parametro.size() != 0) {
                    String idDetraccion = parametro.get(0).getIdParametro().toString();
                    proveedor.setIndDetraccion(idDetraccion);
                }
                if(mig.getCodAreaCompra() != "" && mig.getCodAreaCompra() != null){
                    AreaCompras areaCompras = this.areaComprasRepository.findByCodigo(mig.getCodAreaCompra());
                    if(areaCompras != null){
                        proveedor.setIdAreaCompra(areaCompras.getIdAreaCompra());
                    }
                }


                CondicionPago condicionPago = this.condicionPagoReposity.findByCodigoSapOrderByIdCondicionPago(mig.getIdCondicionPago());
                proveedor.setCondicionPago(condicionPago);

                //Se cambia el estado para que los deje Homologados (HOM) Registrado (REG)
                EstadoProveedor estadoProveedor = this.estadoProveedorRepository.getByCodigoEstadoProveedor("HOM");
                proveedor.setIdEstadoProveedor(estadoProveedor);

                Moneda moneda = this.monedaRepository.getByCodigoMoneda(mig.getIdMoneda());
                proveedor.setMoneda(moneda);

                TipoComprobante tipoComprobante = this.tipoComprobanteRepository.findByCodigoTipoComprobante("FA");
                proveedor.setTipoComprobante(tipoComprobante);


                TipoProveedor tipoProveedor = new TipoProveedor();
                tipoProveedor.setIdTipoProveedor(mig.getIdTipoProveedor().equals("1") ? 1 : 2);
                proveedor.setTipoProveedor(tipoProveedor);

                proveedor.setFlagActivo(mig.getEmailRetencion().equals("HABILITADO") ? 1 : 0);

                Proveedor proveedorSave = proveedorRepository.save(proveedor);
                postUserSap(proveedorSave);
                proveedorPotencialAprobadoNotificacion
                        .enviarMigrado(this.parametroMapper.getMailSetting(), proveedorSave,"12345678ab$");

                proveedorList.add(proveedorSave);

            });

            return proveedorList;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    public void postUserSap(Proveedor proveedor) {
        //request url
        String urlPost = Constant.URL_SAP + "scim/Users";
        //create an instance of RestTemplate
        RestTemplate restTemplate = new RestTemplate();
        //create headers
        HttpHeaders headers = new HttpHeaders();
        //Set content-type
        headers.set("Content-Type", "application/scim+json");
        headers.setBasicAuth(Constant.USER_SAP, Constant.PASS_SAP);

        String[] schemas = new String[2];
        schemas[0] = "urn:ietf:params:scim:schemas:core:2.0:User";
        schemas[1] = "urn:ietf:params:scim:schemas:extension:sap:2.0:User";

        String emailProv = proveedor.getEmail();
        String razonSocialProv = proveedor.getRazonSocial();
        Object objectMail = proveedor.getEmail();
        Object objectRazonSocial = proveedor.getRazonSocial();

        Map<String, Object> map = new HashMap<>();
        map.put("schemas", schemas);
        //map.put("userName", objectMail);
        map.put("userName", emailProv);
        map.put("password", "12345678ab$");

        NameDto nameDto = new NameDto();
        nameDto.setFamilyName("apellido");
        nameDto.setGivenName("nombre");

        map.put("name", nameDto);
        //map.put("displayName", razonSocialProv);
        map.put("displayName", "displayName");
        map.put("active", true);

        List<EmailsDto> emailsDtoList = new ArrayList<>();
        EmailsDto emailsDto = new EmailsDto();
        emailsDto.setType("work");
        emailsDto.setValue(emailProv);
        emailsDto.setDisplay(emailProv);
        emailsDto.setPrimary(true);
        emailsDtoList.add(emailsDto);

        map.put("emails", emailsDtoList);

        SendMailDto sendMailDto = new SendMailDto();
        sendMailDto.setSendMail(false);
        map.put("urn:ietf:params:scim:schemas:extension:sap:2.0:User", sendMailDto);
        try {
            //build the request
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map, headers);
            //send POST request
            ResponseEntity<?> response = restTemplate.postForEntity(urlPost, entity, String.class);

            // check response
            if (response.getStatusCode() == HttpStatus.CREATED) {
                //building object to get userId
                Object obj = response.getBody();
                JSONObject obj1 = new JSONObject(obj.toString());
                JSONObject obj2 = new JSONObject(obj1.get("urn:ietf:params:scim:schemas:extension:sap:2.0:User").toString());
                String userId = obj2.get("userId").toString();
                System.out.println("HOLA" + userId);
                patchUserId(userId);
            }
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.CONFLICT) {
                // Manejar el caso de conflicto (409)
                //No hace nada
                System.out.println("------ YA EXISTE PROVEEDOR EN SAP IAS ----");
            } else {
                // Relanzar la excepción para otros códigos de error no controlados
                throw e;
            }
        } catch (Exception e) {
            // Manejar otras excepciones generales
            System.err.println("Error inesperado: " + e.getMessage());
            e.printStackTrace();
        }

    }

    public void patchUserId(String userId) {
        String urlPatch = Constant.URL_SAP + "service/scim/Users/" + userId;
        //create an instance of RestTemplate
        RestTemplate restTemplate = new RestTemplate();
        //create headers
        HttpHeaders headers = new HttpHeaders();
        //Set content-type
        headers.set("Content-Type", "application/scim+json");
        headers.setBasicAuth(Constant.USER_SAP, Constant.PASS_SAP);

        List<GroupsDto> groupsDtoList = new ArrayList<>();
        GroupsDto groupsDto = new GroupsDto();
        groupsDto.setDisplay("group_proveedor");
        groupsDto.setValue("group_proveedor");
        groupsDtoList.add(groupsDto);

        Map<String, Object> map = new HashMap<>();
        map.put("id", userId);
        map.put("groups", groupsDtoList);

        //build the request
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map, headers);
        //send POST request
//        ResponseEntity<?> response = restTemplate.postForEntity(urlPatch, entity, String.class);
        ResponseEntity<?> response = restTemplate.exchange(urlPatch, HttpMethod.PUT, entity, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            //building object to get userId
            Object obj = response.getBody();
            System.out.println("HOLA" + response.getBody());
        }

    }

    @Override
    public List<FacturaSapDto> getEstadoCuentaProveedor(String email)   {
        try {

            String codigoAcreedor = "";

            Proveedor proveedorRuc = this.proveedorRepository.getProveedorByEmail(email);
            if (proveedorRuc != null && proveedorRuc.getIdProveedor() != null) {
                codigoAcreedor = proveedorRuc.getAcreedorCodigoSap();
            }

            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            okhttp3.MediaType mediaType = okhttp3.MediaType.parse("text/plain");
            RequestBody body = RequestBody.create(mediaType, "");
            Request request = new Request.Builder()
                    //.url("https://my417543-api.s4hana.cloud.sap/sap/opu/odata/sap/API_OPLACCTGDOCITEMCUBE_SRV/A_OperationalAcctgDocItemCube?$filter=(AccountingDocumentType eq 'KR' or AccountingDocumentType eq 'RE' or AccountingDocumentType eq 'KG' or AccountingDocumentType eq 'KZ') and Supplier eq '" +  codigoAcreedor + "'")
                    .url(urlSap + "/sap/opu/odata/sap/API_OPLACCTGDOCITEMCUBE_SRV/A_OperationalAcctgDocItemCube?$filter=(AccountingDocumentType eq 'KR' or AccountingDocumentType eq 'RE' or AccountingDocumentType eq 'KG' or AccountingDocumentType eq 'KZ') and Supplier eq '" +  codigoAcreedor + "'")
                    .get()
                    .addHeader("Authorization", "Basic TV9UUkFOU1BPUlRFOl1oMnBnYnFRVnhHQUhLS1JRbW1tZHVkVHVNdllQV0xuQnJ4c3Jiams=")
                    .addHeader("Accept", "application/json")
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    String jsonResponse = response.body().string();
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode rootNode = objectMapper.readTree(jsonResponse);
                    JsonNode resultsNode = rootNode.path("d").path("results");

                    List<FacturaSapDto> facturas = new ArrayList<>();

                    for (JsonNode resultNode : resultsNode) {
                        FacturaSapDto factura = new FacturaSapDto();

                        factura.setMonto(resultNode.path("AmountInBalanceTransacCrcy").asDouble(0.0));
                        factura.setMoneda(resultNode.path("BalanceTransactionCurrency").asText(null));
                        factura.setNroFactura(resultNode.path("DocumentReferenceID").asText(null));
                        factura.setFechaCreacionFactura(resultNode.path("PostingDate").asText(null));
                        factura.setProveedor(resultNode.path("Supplier").asText(null));
                        factura.setNombreProveedor(resultNode.path("SupplierName").asText(null));
                        factura.setFechaVencimiento(resultNode.path("DueCalculationBaseDate").asText(null));
                        factura.setDocumentoContabilizacion(resultNode.path("ClearingAccountingDocument").asText(null));
                        facturas.add(factura);
                    }
                    System.out.println("Respuesta: " + jsonResponse);
                    return facturas;
                } else {
                    System.err.println("Error en la solicitud: " + response.code() + " - " + response.message());
                    List<FacturaSapDto> facturaSapDtoList = new ArrayList<>();
                    return facturaSapDtoList;
                }
            } catch (IOException e) {
                e.printStackTrace();
                List<FacturaSapDto> facturaSapDtoList = new ArrayList<>();
                return facturaSapDtoList;
            }

        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void extraerProveedorSap() throws Exception {
        String url = SAP_API_URL + "/sap/opu/odata/sap/YY1_PROVEEDOR_DM_CDS/YY1_Proveedor_DM";


        try{
            HttpHeaders headers = new HttpHeaders();

            // Crear encabezados con autenticación básica
            String auth = USERNAME + ":" + PASSWORD;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());

            headers.set("Authorization", "Basic " + encodedAuth);
            headers.set("X-csrf-token", "Fetch");
            headers.set("Accept", "application/json");

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<ProveedorSapDto> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    ProveedorSapDto.class);

            if(response.getStatusCode().is2xxSuccessful() &&  response.getBody() != null){
                List<ProveedorSapDto.Result> results = response.getBody().getD().getResults();

                for(ProveedorSapDto.Result result: results){
                    Proveedor proveedor = new Proveedor();

                    if(proveedorRepository.getProveedorByRuc(result.getRuc()) != null){
                        proveedor = proveedorRepository.getProveedorByRuc(result.getRuc());
                        System.out.println(result.getAcredorCodigoSap());
                        List<ProveedorEmailDto.Result> emailsProveedor = this.emailProveedor(result.getAcredorCodigoSap(), result.getIdDireccion());
                        for (ProveedorEmailDto.Result email: emailsProveedor){
                            System.out.println(email.getEmail());
                        }
                        List<ProveedorBancoDTO.ResultBanco> bancos = this.BancosProveedor(result.getAcredorCodigoSap());

                        proveedor.setAcreedorCodigoSap(result.getAcredorCodigoSap());
                        proveedor.setDireccionFiscal(result.getNombreCalle() + " " + result.getNumeroCasa());
                        proveedor.setTelefono(result.getTelefono());
                        proveedor.setEmail(emailsProveedor.get(0).getEmail());

                        CondicionPago condicionPago = this.condicionPagoReposity.findByCodigoSapOrderByIdCondicionPago(result.getIdCondicionPago());
                        proveedor.setCondicionPago(condicionPago);

                        EstadoProveedor estadoProveedor = this.estadoProveedorRepository.getByCodigoEstadoProveedor("HOM");
                        proveedor.setIdEstadoProveedor(estadoProveedor);

                        Moneda moneda = this.monedaRepository.getByCodigoMoneda(result.getIdMoneda());
                        proveedor.setMoneda(moneda);

                        TipoComprobante tipoComprobante = this.tipoComprobanteRepository.findByCodigoTipoComprobante("FA");
                        proveedor.setTipoComprobante(tipoComprobante);


                        TipoProveedor tipoProveedor = new TipoProveedor();
                        tipoProveedor.setIdTipoProveedor(result.getIdTipoProveedor().equals("1") ? 1 : 2);
                        proveedor.setTipoProveedor(tipoProveedor);
                        //proveedorSave
                        Proveedor proveedorSave = proveedor;
                        for(ProveedorBancoDTO.ResultBanco banco: bancos ){

                            Optional<ProveedorCuentaBancaria> cta = this.proveedorCuentaBancoRepository.
                                    getProveedorCuentaBancariaByIdProveedorAndClaveBancoAndNumeroCuenta(proveedorSave.getIdProveedor(),
                                        banco.getClaveControlBanco(),
                                        banco.getNumeroCuenta());

                            if(!cta.isPresent()){
                                ProveedorCuentaBancaria proveedorCuentaBancaria = new ProveedorCuentaBancaria();
                                proveedorCuentaBancaria.setNumeroCuenta(banco.getNumeroCuenta());
                                proveedorCuentaBancaria.setNumeroCuentaCci(banco.getNumeroCuentaCci());

                                Moneda monedaBanco = this.monedaRepository.getByCodigoMoneda(banco.getIdMoneda());
                                proveedorCuentaBancaria.setMoneda(monedaBanco);

                                Banco bancoSave = this.bancoRepository.getByClaveBanco(banco.getClaveControlBanco());
                                proveedorCuentaBancaria.setBanco(bancoSave);

                                proveedorCuentaBancaria.setProveedor(proveedor);
                                proveedorCuentaBancaria.setClaveControlBanco(banco.getClaveControlBanco());
                                //this.proveedorCuentaBancoRepository.save(proveedorCuentaBancaria);
                            }


                            //pendiente pais del banco

                            System.out.println(banco.getNumeroCuenta());
                        }

                    }else {
                        System.out.println(result.getAcredorCodigoSap());
                        List<ProveedorEmailDto.Result> emailsProveedor = this.emailProveedor(result.getAcredorCodigoSap(), result.getIdDireccion());
                        for (ProveedorEmailDto.Result email: emailsProveedor){
                            System.out.println(email.getEmail());
                        }

                        proveedor.setAcreedorCodigoSap(result.getAcredorCodigoSap());
                        proveedor.setDireccionFiscal(result.getNombreCalle() + " " + result.getNumeroCasa());
                        proveedor.setRuc(result.getRuc());
                        proveedor.setRazonSocial(result.getRazonSocial());
                        proveedor.setTelefono(result.getTelefono());
                        proveedor.setEmail(emailsProveedor.get(0).getEmail());

                        CondicionPago condicionPago = this.condicionPagoReposity.findByCodigoSapOrderByIdCondicionPago(result.getIdCondicionPago());
                        proveedor.setCondicionPago(condicionPago);

                        EstadoProveedor estadoProveedor = this.estadoProveedorRepository.getByCodigoEstadoProveedor("HOM");
                        proveedor.setIdEstadoProveedor(estadoProveedor);

                        Moneda moneda = this.monedaRepository.getByCodigoMoneda(result.getIdMoneda());
                        proveedor.setMoneda(moneda);

                        TipoComprobante tipoComprobante = this.tipoComprobanteRepository.findByCodigoTipoComprobante("FA");
                        proveedor.setTipoComprobante(tipoComprobante);


                        TipoProveedor tipoProveedor = new TipoProveedor();
                        tipoProveedor.setIdTipoProveedor(result.getIdTipoProveedor().equals("1") ? 1 : 2);
                        proveedor.setTipoProveedor(tipoProveedor);
                        System.out.println(result.getRuc());

                        //proveedor a salvar
                        Proveedor proveedorSave = proveedor;
                        List<ProveedorBancoDTO.ResultBanco> bancos = this.BancosProveedor(proveedorSave.getAcreedorCodigoSap());
                        for(ProveedorBancoDTO.ResultBanco banco: bancos ){
                            Optional<ProveedorCuentaBancaria> cta = this.proveedorCuentaBancoRepository.
                                    getProveedorCuentaBancariaByIdProveedorAndClaveBancoAndNumeroCuenta(proveedorSave.getIdProveedor(),
                                            banco.getClaveControlBanco(),
                                            banco.getNumeroCuenta());

                            if(!cta.isPresent()){
                                ProveedorCuentaBancaria proveedorCuentaBancaria = new ProveedorCuentaBancaria();
                                proveedorCuentaBancaria.setNumeroCuenta(banco.getNumeroCuenta());
                                proveedorCuentaBancaria.setNumeroCuentaCci(banco.getNumeroCuentaCci());

                                Moneda monedaBanco = this.monedaRepository.getByCodigoMoneda(banco.getIdMoneda());
                                proveedorCuentaBancaria.setMoneda(monedaBanco);

                                Banco bancoSave = this.bancoRepository.getByClaveBanco(banco.getClaveControlBanco());
                                proveedorCuentaBancaria.setBanco(bancoSave);

                                proveedorCuentaBancaria.setProveedor(proveedor);
                                proveedorCuentaBancaria.setClaveControlBanco(banco.getClaveControlBanco());
                                //this.proveedorCuentaBancoRepository.save(proveedorCuentaBancaria);
                            }
                        }
                    }

                }

            }else {
                throw new Exception("Respuesta no satisfactoria del servicio SAP: " + response.getStatusCode());
            }
        } catch (Exception e) {
            System.err.println("Error al realizar la solicitud al servicio SAP: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    private List<ProveedorBancoDTO.ResultBanco> BancosProveedor(String bp) throws Exception {
        String url = SAP_API_URL + "/sap/opu/odata/sap/API_BUSINESS_PARTNER/A_BusinessPartner('"+ bp + "')/to_BusinessPartnerBank";

        try{
            HttpHeaders headers = new HttpHeaders();

            // Crear encabezados con autenticación básica
            String auth = USERNAME + ":" + PASSWORD;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());

            headers.set("Authorization", "Basic " + encodedAuth);
            headers.set("X-csrf-token", "Fetch");
            headers.set("Accept", "application/json");

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<ProveedorBancoDTO> responseBanco = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    ProveedorBancoDTO.class);

            if(responseBanco.getStatusCode().is2xxSuccessful() &&  responseBanco.getBody() != null){
                List<ProveedorBancoDTO.ResultBanco> results = responseBanco.getBody().getD().getResults();

                return results;

            }else {
                throw new Exception("Respuesta no satisfactoria del servicio SAP: " + responseBanco.getStatusCode());
            }
        } catch (Exception e) {
            System.err.println("Error al realizar la solicitud al servicio SAP: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    private List<ProveedorEmailDto.Result> emailProveedor(String bp, String adressId) throws Exception {
        String url = SAP_API_URL + "/sap/opu/odata/sap/API_BUSINESS_PARTNER/A_BusinessPartnerAddress(BusinessPartner='"+ bp + "',AddressID='"+ adressId +"')/to_EmailAddress";

        try{
            HttpHeaders headers = new HttpHeaders();

            // Crear encabezados con autenticación básica
            String auth = USERNAME + ":" + PASSWORD;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());

            headers.set("Authorization", "Basic " + encodedAuth);
            headers.set("X-csrf-token", "Fetch");
            headers.set("Accept", "application/json");

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<ProveedorEmailDto> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    ProveedorEmailDto.class);

            if(response.getStatusCode().is2xxSuccessful() &&  response.getBody() != null){
                List<ProveedorEmailDto.Result> results = response.getBody().getD().getResults();

                return results;

            }else {
                throw new Exception("Respuesta no satisfactoria del servicio SAP: " + response.getStatusCode());
            }
        } catch (Exception e) {
            System.err.println("Error al realizar la solicitud al servicio SAP: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}
