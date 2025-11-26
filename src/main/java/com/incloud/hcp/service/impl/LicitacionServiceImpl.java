package com.incloud.hcp.service.impl;

import com.incloud.hcp.bean.*;
import com.incloud.hcp.domain.*;
import com.incloud.hcp.dto.CuentaBancariaDto;
import com.incloud.hcp.dto.LicitacionAdjuntoBase64Dto;
import com.incloud.hcp.enums.EstadoLicitacionEnum;
import com.incloud.hcp.exception.PortalException;
import com.incloud.hcp.myibatis.mapper.*;
import com.incloud.hcp.repository.*;
import com.incloud.hcp.service.ExportDataService;
import com.incloud.hcp.service.LicitacionService;
import com.incloud.hcp.service._framework.BaseServiceImpl;
import com.incloud.hcp.service.cmiscf.CmisBaseService;
import com.incloud.hcp.service.cmiscf.bean.CmisFile;
import com.incloud.hcp.service.notificacion.*;
import com.incloud.hcp.util.BASE64DecodedMultipartFile;
import com.incloud.hcp.util.Constant;
import com.incloud.hcp.util.DateUtils;
import com.incloud.hcp.util.constant.LicitacionConstant;
import org.apache.commons.collections4.map.LinkedMap;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Created by USER on 29/08/2017.
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class LicitacionServiceImpl extends BaseServiceImpl implements LicitacionService {

    private static Logger logger = LoggerFactory.getLogger(LicitacionServiceImpl.class);

    @Value("${sm.portal.dev}")
    private Boolean isDev;

    private static final String DOCUMENT_FOLDER = "temp";
    SimpleDateFormat ft_diamesanio = new SimpleDateFormat("dd/MM/yyyy");
    SimpleDateFormat ft_diamesanio_time = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    private static final String STRING_HYPHEN = "-";

    @Autowired
    private LicitacionRepository licitacionRepository;

    @Autowired
    private LicitacionDetalleRepository licitacionDetalleRepository;

    @Autowired
    private LicitacionProveedorRepository licitacionProveedorRepository;

    @Autowired
    private LicitacionProveedorPreguntaRepository licitacionProveedorPreguntaRepository;

    @Autowired
    private LicitacionGrupoCondicionLicRepository licitacionGrupoCondicionLicRepository;

    @Autowired
    private GrupoCondicionLicRespuestaRepository grupoCondicionLicRespuestaRepository;

    @Autowired
    private LicitacionMapper licitacionMapper;

    @Autowired
    private CondicionMapper condicionMapper;

    @Autowired
    private RespuestaCondicionMapper respuestaCondicionMapper;

    @Autowired
    private LicitacionProveedorMapper licitacionProveedorMapper;

    @Autowired
    private ParametroRepository parametroRepository;

    @Autowired
    private ProveedorRepository proveedorRepository;

    @Autowired
    private LicitacionAdjuntoRepository licitacionAdjuntoRepository;

    @Autowired
    private LicitacionSubetapaRepository licitacionSubetapaRepository;

    @Autowired
    private LicitacionProveedorNotificacion licitacionProveedorNotificacion;

    @Autowired
    private LicitacionAmpliarFechaCierreNotificacion licitacionAmpliarFechaCierreNotificacion;

    @Autowired
    private LicitacionAnularNotificacion licitacionAnularNotificacion;

    @Autowired
    private LicitacionControlCambioRepository licitacionControlCambioRepository;

    @Autowired
    private ControlCambioCampoRepository controlCambioCampoRepository;

    @Autowired
    private LicitacionProveedorRenegociacionRepository licitacionProveedorRenegociacionRepository;

    @Autowired
    private ParametroMapper parametroMapper;

    @Autowired
    private MtrTipoDocumentoRepository mtrTipoDocumentoRepository;

    //@Autowired
    //private CmisService cmisService;

    @Autowired
    private CmisBaseService cmisServiceCf;

    @Autowired
    private LicitacionAdjuntoRespuestaRepository licitacionAdjuntoRespuestaRepository;

    @Autowired
    private CotizacionRepository cotizacionRepository;

    @Autowired
    private CotizacionAdjuntoRepository cotizacionAdjuntoRepository;

    @Autowired
    private SubetapaRepository subetapaRepository;

    @Autowired
    private ExportDataService exportDataService;


    @Override
    public List<Licitacion> getListaLicitacionByFiltro(Map<String, Object> json) {

        ArrayList<String> listaEstadoLicitacion = (ArrayList<String>) json.get("listaEstadoLicitacion");
        String puntoEntrega = (String) json.get("puntoEntrega");
        String numeroLicitacionString = (String) json.get("numeroLicitacion");
        Integer annioLicitacion = null;
        Integer numeroLicitacion = null;
       /* if (numeroLicitacionString != null && !(numeroLicitacionString.isEmpty())) {
            String texto = ("000000000000" + numeroLicitacionString);
            String stringNum = texto.substring(texto.length() - 12, texto.length());

            annioLicitacion = Integer.parseInt(stringNum.substring(0, 4));
            numeroLicitacion = Integer.parseInt(stringNum.substring(4));
        }*/
        String titulo = (String) json.get("titulo");
        String moneda = (String) json.get("moneda");
        String estadoTerminal = (String) json.get("estadoTerminal");

        String solicitudPedido = (String) json.get("solicitudPedido");
        String codigoSap = (String) json.get("codigoSap");
        ArrayList<String> listaCentroLogistico = (ArrayList<String>) json.get("listaCentroLogistico");
        ArrayList<String> listaAlmacen = (ArrayList<String>) json.get("listaAlmacen");

        ArrayList<String> listaClaseDocumento = (ArrayList<String>) json.get("listaClaseDocumento");
        ArrayList<String> listaTipoLicitacion = (ArrayList<String>) json.get("listaTipoLicitacion");
        ArrayList<String> listaTipoCuestionario = (ArrayList<String>) json.get("listaTipoCuestionario");

        String idProveedor = (String) json.get("idProveedor");
        String numeroRUC = (String) json.get("numeroRUC");
        String razonSocial = (String) json.get("razonSocial");
        String descripcion = (String) json.get("descripcion");
        ArrayList<String> listaRegion = (ArrayList<String>) json.get("listaRegion");
        if (StringUtils.isNotBlank(estadoTerminal)) {
            listaEstadoLicitacion = new ArrayList<String>();
            listaEstadoLicitacion.add(LicitacionConstant.ESTADO_ADJUDICADA);
            listaEstadoLicitacion.add(LicitacionConstant.ESTADO_ANULADA);
            listaEstadoLicitacion.add(LicitacionConstant.ESTADO_ENVIADO_SAP);
        }
        String emailUsuario = (String) json.get("emailUsuario");

        List<Licitacion> lista = this.licitacionMapper.getListLicitacionByFiltro(
            titulo,
            listaEstadoLicitacion,
            numeroLicitacion,
            annioLicitacion,
            puntoEntrega,
            moneda,
            listaCentroLogistico,
            listaAlmacen,
            solicitudPedido,
            codigoSap,
            listaClaseDocumento,
            listaTipoLicitacion,
            listaTipoCuestionario,
            idProveedor,
            numeroRUC,
            razonSocial,
            descripcion,
            listaRegion,
            emailUsuario,
            numeroLicitacionString);

        return lista;
    }

    @Override
    public List<Licitacion> getListaLicitacionByFiltroPaginado(Map<String, Object> json) {

        ArrayList<String> listaEstadoLicitacion = (ArrayList<String>) json.get("listaEstadoLicitacion");
        String puntoEntrega = (String) json.get("puntoEntrega");
        String numeroLicitacionString = (String) json.get("numeroLicitacion");
        String indEjecucionSapOk = (String) json.get("indEjecucionSapOk");
        Integer annioLicitacion = null;
        String numeroLicitacion = null;
        if (numeroLicitacionString != null && !(numeroLicitacionString.isEmpty())) {
            String texto = ("000000000000" + numeroLicitacionString);
            String stringNum = texto.substring(texto.length() - 12, texto.length());

            annioLicitacion = Integer.parseInt(stringNum.substring(0, 4));
            //numeroLicitacion = Integer.parseInt(stringNum.substring(4));
            numeroLicitacion =  stringNum.substring(4) ;
        }
        String moneda = (String) json.get("moneda");
        String estadoTerminal = (String) json.get("estadoTerminal");

        String solicitudPedido = (String) json.get("solicitudPedido");
        String codigoSap = (String) json.get("codigoSap");

        /*if (codigoSap != null && codigoSap != ""){
            codigoSap = "000000000"+ (String) json.get("codigoSap");

        }*/
        ArrayList<String> listaCentroLogistico = (ArrayList<String>) json.get("listaCentroLogistico");
        ArrayList<String> listaAlmacen = (ArrayList<String>) json.get("listaAlmacen");

        ArrayList<String> listaClaseDocumento = (ArrayList<String>) json.get("listaClaseDocumento");
        ArrayList<String> listaTipoLicitacion = (ArrayList<String>) json.get("listaTipoLicitacion");
        ArrayList<String> listaTipoCuestionario = (ArrayList<String>) json.get("listaTipoCuestionario");

        String idProveedor = (String) json.get("idProveedor");
        String numeroRUC = (String) json.get("numeroRUC");
        String razonSocial = (String) json.get("razonSocial");
        String descripcion = (String) json.get("descripcion");
        ArrayList<String> listaRegion = (ArrayList<String>) json.get("listaRegion");
        ArrayList<String> listaPais = (ArrayList<String>) json.get("listaPais");
       /* if (StringUtils.isNotBlank(estadoTerminal)) {
            listaEstadoLicitacion = new  ArrayList<String>();
            listaEstadoLicitacion.add(LicitacionConstant.ESTADO_ADJUDICADA);
            listaEstadoLicitacion.add(LicitacionConstant.ESTADO_ANULADA);
            listaEstadoLicitacion.add(LicitacionConstant.ESTADO_ENVIADO_SAP);
        }*/
        Integer nroRegistros = (Integer) json.get("nroRegistros");
        Integer paginaMostrar = (Integer) json.get("paginaMostrar");
        paginaMostrar = new Integer((paginaMostrar.intValue() - 1) * nroRegistros);
        String emailUsuario = (String) json.get("emailUsuario");
        String nombreLicitacion = (String) json.get("nombreLicitacion");
        List<Licitacion> lista = this.licitacionMapper.getListLicitacionByFiltroPaginado(
            listaEstadoLicitacion,
            numeroLicitacion,
            annioLicitacion,
            puntoEntrega,
            moneda,
            listaCentroLogistico,
            listaAlmacen,
            solicitudPedido,
            codigoSap,
            listaClaseDocumento,
            listaTipoLicitacion,
            listaTipoCuestionario,
            idProveedor,
            numeroRUC,
            razonSocial,
            descripcion,
            listaRegion,
            nroRegistros,
            indEjecucionSapOk,
            emailUsuario,
            paginaMostrar,
            nombreLicitacion,
            estadoTerminal,
            listaPais);

        return lista;
    }

    @Override
    public LicitacionRequest saveLicitacion(LicitacionRequest objLicitacion) throws Exception {
        List<LicitacionSubetapa> listaSubEtapa = objLicitacion.getSubetapaLicitacion();
        List<LicitacionSubetapa> sortedListSubEtapa = listaSubEtapa.stream()
            .sorted(Comparator.comparing(LicitacionSubetapa::getFechaCierreSubetapa))
            .collect(Collectors.toList());

        logger.error("Ingresando CBAZALAR saveLicitacion 0");
        if (objLicitacion == null) {
            throw new PortalException("Licitacion recibida es NULL");
        }
        //Validaciones previas
        Date fechaActual = DateUtils.obtenerFechaHoraActual();
        String sFechaActual = DateUtils.convertDateToString("yyyy/MM/dd HH:mm:ss", fechaActual);
        for (LicitacionSubetapa bean : sortedListSubEtapa) {
            Subetapa subetapa = this.subetapaRepository.findById(bean.getIdSubetapa().getId()).get();
            bean.setIdSubetapa(subetapa);
            Timestamp timestamp = this.convertToTimestamp(bean.getFechaCierreSubetapaString(), true);
            if (timestamp.compareTo(fechaActual) < 0) {
                throw new PortalException("Fecha ingresada: " +
                    bean.getFechaCierreSubetapaString() +
                    " correspondiente a la Etapa: " +
                    bean.getIdSubetapa().getDescripcionSubetapa() + " es menor a la Fecha Actual del Sistema: " + sFechaActual);
            }
        }

        boolean validar = false;
        List<Subetapa> subetapaListObligatorio = this.subetapaRepository.findByIndObligatorio(Constant.UNO);
        for (Subetapa subetapaObligatorio : subetapaListObligatorio) {
            validar = false;
            for (LicitacionSubetapa bean : sortedListSubEtapa) {
                if (bean.getIdSubetapa().getId().intValue() == subetapaObligatorio.getId().intValue()) {
                    validar = true;
                }
            }
            if (!validar) {
                throw new PortalException("Se debe ingresar Etapa: " + subetapaObligatorio.getDescripcionSubetapa() + ", debido que dicha Etapa es obligatoria en toda Licitación");
            }
        }

        List<LicitacionDetalle> licitacionDetalleList = objLicitacion.getDetalleLicitacion();
        logger.error("GUARDAR LICITACION Id: " + objLicitacion.getIdLicitacion() + " // licitacionDetalleList: " + licitacionDetalleList.toString());
        if (licitacionDetalleList == null && licitacionDetalleList.size() > 0) {
            throw new PortalException("Debe ingresar el Detalle de la Licitación");
        }
//        for(LicitacionDetalle beanDetalle : licitacionDetalleList) {
//            if (!Optional.ofNullable(beanDetalle.getIdCentro()).isPresent()) {
//                throw new PortalException("Debe ingresar Centro en el Detalle de la Licitación");
//            }
//            if (!Optional.ofNullable(beanDetalle.getIdCentro().getIdCentroAlmacen()).isPresent()) {
//                throw new PortalException("Debe ingresar Centro en el Detalle de la Licitación");
//            }
//            if (!Optional.ofNullable(beanDetalle.getIdAlmacen()).isPresent()) {
//                throw new PortalException("Debe ingresar Almacén en el Detalle de la Licitación");
//            }
//            if (!Optional.ofNullable(beanDetalle.getIdAlmacen().getIdCentroAlmacen()).isPresent()) {
//                throw new PortalException("Debe ingresar Almacén en el Detalle de la Licitación");
//            }
//        }

        //Verificando si tiene Confirmacion de Participacion
        String fechaConfirmacionString = "";
        for (LicitacionSubetapa bean : sortedListSubEtapa) {
            Subetapa subetapa = this.subetapaRepository.findById(bean.getIdSubetapa().getId()).get();
            String indConfirmacion = bean.getIdSubetapa().getIndConfirmacionParticipacion();
            if (Optional.ofNullable(indConfirmacion).isPresent()) {
                if (indConfirmacion.equals(Constant.UNO)) {
                    fechaConfirmacionString = bean.getFechaCierreSubetapaString();
                }
            }
        }

        //Verificando si tiene Consulta de Preguntas
        String fechaConsultaPreguntaString = "";
        for (LicitacionSubetapa bean : sortedListSubEtapa) {
            Subetapa subetapa = this.subetapaRepository.findById(bean.getIdSubetapa().getId()).get();
            String indConfirmacion = bean.getIdSubetapa().getIndRecepcionConsulta();
            if (Optional.ofNullable(indConfirmacion).isPresent()) {
                if (indConfirmacion.equals(Constant.UNO)) {
                    fechaConsultaPreguntaString = bean.getFechaCierreSubetapaString();
                }
            }
        }

        int longitudListaEtapa = sortedListSubEtapa.size();
        LicitacionSubetapa licitacionSubetapaFinal = sortedListSubEtapa.get(longitudListaEtapa - 1);
        String indicadorSubetapaFinal = licitacionSubetapaFinal.getIdSubetapa().getIndSubetapaFinal();
        validar = false;
        if (Optional.ofNullable(indicadorSubetapaFinal).isPresent()) {
            if (indicadorSubetapaFinal.equals(Constant.UNO)) {
                validar = true;
            }
        }
        if (!validar) {
            throw new PortalException("La última Etapa debe ser: Presentación de Propuestas");
        }

        //Grabacion
        String fechaInicioCierreRecepcionString = DateUtils.
            convertDateToString("dd/MM/yyyy HH:mm:ss", DateUtils.obtenerFechaHoraActual());
        LicitacionRequest response = new LicitacionRequest();

        Moneda moneda = new Moneda();
        ClaseDocumento claseDocumento = new ClaseDocumento();
        if (Optional.ofNullable(objLicitacion.getIdClaseDocumento()).isPresent()) {
            if (objLicitacion.getIdClaseDocumento() != 0) {
                logger.error("INGRESSNDO GESPINOZA");
                claseDocumento.setIdClaseDocumento(objLicitacion.getIdClaseDocumento());
            }
        }

        moneda.setIdMoneda(objLicitacion.getIdMoneda());

        UserSession userSession = this.getUserSession();

        if (longitudListaEtapa > 1) {
            LicitacionSubetapa licitacionSubetapaPreFinal = sortedListSubEtapa.get(longitudListaEtapa - 2);
            fechaInicioCierreRecepcionString = licitacionSubetapaPreFinal.getFechaCierreSubetapaString();
        }

        if (objLicitacion.getIdLicitacion() != null) {
            Licitacion licitacion = this.licitacionRepository.findById(objLicitacion.getIdLicitacion()).get();

            //Actualizar
            if (("GE").equals(licitacion.getEstadoLicitacion())) {
                if (Optional.ofNullable(licitacion.getClaseDocumento()).isPresent()) {
                    if (objLicitacion.getIdClaseDocumento() != 0) {

                        logger.error("Actualizando LIcitacion");
                        licitacion.setClaseDocumento(claseDocumento);
                    }
                }

                licitacion.setMoneda(moneda);
                licitacion.setFechaEntregaInicio(this.convertToTimestamp(objLicitacion.getFechaEntregaInicio(), false));
                licitacion.setFechaCierreRecepcionOferta(
                    this.convertToTimestamp(licitacionSubetapaFinal.getFechaCierreSubetapaString(), true));
                licitacion.setFechaInicioRecepcionOferta(
                    this.convertToTimestamp(fechaInicioCierreRecepcionString, true)
                );
                licitacion.setFechaCierreConfirmacionParticipacion(
                    this.convertToTimestamp(fechaConfirmacionString, true)
                );
                licitacion.setFechaCierreConsultaPregunta(
                    this.convertToTimestamp(fechaConsultaPreguntaString, true)
                );

                Timestamp fechaCierre = licitacion.getFechaCierreRecepcionOferta();
                if (fechaCierre.compareTo(DateUtils.obtenerFechaHoraActual()) < 0) {
                    throw new PortalException("Fecha de Cierre de Recepción de Ofertas: " + licitacionSubetapaFinal.getFechaCierreSubetapaString() + " no puede ser menor a la Fecha Actual del Sistema");
                }
                licitacion.setFechaUltimaRenegociacion(licitacion.getFechaCierreRecepcionOferta());
                licitacion.setUsuarioPublicacionName(objLicitacion.getUsuarioPublicacionName());
                licitacion.setNecesidadUrgencia(objLicitacion.getCodGradoUrgencia());
                licitacion.setUsuarioModificacion(userSession.getId());
                licitacion.setFechaModificacion(new Timestamp(new Date().getTime()));
                licitacion.setComentarioLicitacion(objLicitacion.getComentarioLicitacion());
                licitacion.setPuntoEntrega(objLicitacion.getCodPuntoEntrega());
                licitacion.setNombreLicitacion(objLicitacion.getNombreLicitacion());
                if (objLicitacion.getIdAlmacen() != null) {
                    if (objLicitacion.getIdAlmacen().equals(0)) {

                    } else {
                        CentroAlmacen almacen = new CentroAlmacen();
                        almacen.setIdCentroAlmacen(objLicitacion.getIdAlmacen());
                        licitacion.setCentroAlmacen2(almacen); //almacen

                    }
                }
                if (objLicitacion.getIdCentroLogistico() != null || objLicitacion.getIdCentroLogistico() != 0) {
                    CentroAlmacen centro = new CentroAlmacen();
                    centro.setIdCentroAlmacen(objLicitacion.getIdCentroLogistico());
                    licitacion.setCentroAlmacen1(centro); //centro
                }
                logger.error(licitacion.toString());
                Licitacion resultLicitacion = licitacionRepository.save(licitacion);
                this.guardarCondicionLicitacion(licitacion, objLicitacion.getCondicionLicitacion(), "UPD");
                this.guardarDetalleLicitacion(licitacion, objLicitacion.getDetalleLicitacion(), "UPD");
                this.guardarProveedorLicitacion(licitacion, objLicitacion.getProveedorLicitacion(), "UPD");

                //Cargar Archvios Licitacion
                if (objLicitacion.getAdjuntoLicitacion().size() > 0) {
                    objLicitacion.getAdjuntoLicitacion().forEach(item -> {
                        licitacionAdjuntoRepository.deleteLicitacionAdjuntoByLicitacion(resultLicitacion);
                    });

                    objLicitacion.getAdjuntoLicitacion().forEach(item -> {
                        item.setIdLicitacionAdjunto(null);
                        licitacionAdjuntoRepository.save(item);
                    });

                } else {
                    licitacionAdjuntoRepository.deleteLicitacionAdjuntoByLicitacion(resultLicitacion);
                }

                // EAAR: de ONBASE  a  DOCUMENT MANAGER
                // Cargar Archvios Licitacion

                List<LicitacionAdjunto> listAdjuntos = this.cargarAdjuntosLicitacion(licitacion, objLicitacion.getLicitacionAdjuntoBase64Dto());

                List<LicitacionSubetapa> listaSubEtapas = this.guardarLicitacionSubEtapa(
                    licitacion,
                    sortedListSubEtapa,
                    "UPD");
                response.setSubetapaLicitacion(listaSubEtapas);
                response.setIdLicitacion(objLicitacion.getIdLicitacion());
                response.setNroLicitacionString(objLicitacion.getNroLicitacionString());
                response.setEstadoLicitacion(objLicitacion.getEstadoLicitacion());
                response.setAdjuntoLicitacion(listAdjuntos);
            }
            logger.error("Ingresando CBAZALAR saveLicitacion 2");
        } else {
            //Insertar Nuevo
            Licitacion licitacion = new Licitacion();

            if (Optional.ofNullable(objLicitacion.getIdClaseDocumento()).isPresent()) {
                if (objLicitacion.getIdClaseDocumento() != 0) {
                    logger.error("Nuevo Lictacion");
                    licitacion.setClaseDocumento(claseDocumento);
                }
            }

            licitacion.setMoneda(moneda);
            licitacion.setFechaEntregaInicio(this.convertToTimestamp(objLicitacion.getFechaEntregaInicio(), false));
            licitacion.setFechaCierreRecepcionOferta(
                this.convertToTimestamp(licitacionSubetapaFinal.getFechaCierreSubetapaString(), true));
            licitacion.setFechaInicioRecepcionOferta(
                this.convertToTimestamp(fechaInicioCierreRecepcionString, true)
            );
            licitacion.setFechaCierreConfirmacionParticipacion(
                this.convertToTimestamp(fechaConfirmacionString, true)
            );
            licitacion.setFechaCierreConsultaPregunta(
                this.convertToTimestamp(fechaConsultaPreguntaString, true)
            );

            Timestamp fechaCierre = licitacion.getFechaCierreRecepcionOferta();
            if (fechaCierre.compareTo(DateUtils.obtenerFechaHoraActual()) < 0) {
                throw new PortalException("Fecha de Cierre de Recepción de Ofertas: " + licitacionSubetapaFinal.getFechaCierreSubetapaString() + " no puede ser menor a la Fecha Actual del Sistema");
            }
            licitacion.setFechaUltimaRenegociacion(licitacion.getFechaCierreRecepcionOferta());

            licitacion.setNecesidadUrgencia(objLicitacion.getCodGradoUrgencia());
            licitacion.setEstadoLicitacion("GE");
            licitacion.setUsuarioCreacion(userSession.getId());
            licitacion.setFechaCreacion(new Timestamp(new Date().getTime()));
            licitacion.setComentarioLicitacion(objLicitacion.getComentarioLicitacion());
            licitacion.setPuntoEntrega(objLicitacion.getCodPuntoEntrega());
            licitacion.setUsuarioPublicacionName(objLicitacion.getUsuarioPublicacionName());

            if (objLicitacion.getIdAlmacen() != null && objLicitacion.getIdAlmacen() != 0) {
                CentroAlmacen almacen = new CentroAlmacen();
                almacen.setIdCentroAlmacen(objLicitacion.getIdAlmacen());
                licitacion.setCentroAlmacen2(almacen); //almacen
            }
            if (objLicitacion.getIdCentroLogistico() != null && objLicitacion.getIdCentroLogistico() != 0) {
                CentroAlmacen centro = new CentroAlmacen();
                centro.setIdCentroAlmacen(objLicitacion.getIdCentroLogistico());
                licitacion.setCentroAlmacen1(centro); //centro
            }

            Integer annioLicitacion = new GregorianCalendar().get(Calendar.YEAR);
            Integer idAutogenerado = licitacionRepository.getMaxNroLicitacionAnio(annioLicitacion) + 1;
            licitacion.setNroLicitacion(idAutogenerado);
            licitacion.setAnioLicitacion(annioLicitacion);
            licitacion.setNumeroPeticionOfertaLicitacionSap(objLicitacion.getNumeroPeticionOfertaLicitacionSap());
            licitacion.setNombreLicitacion(objLicitacion.getNombreLicitacion());

            Licitacion licitacionGenerada = licitacionRepository.save(licitacion);
            if (objLicitacion.getCondicionLicitacion() != null && objLicitacion.getCondicionLicitacion().size() > 0) {
                this.guardarCondicionLicitacion(licitacionGenerada, objLicitacion.getCondicionLicitacion(), "INS");
            }
            if (objLicitacion.getDetalleLicitacion() != null && objLicitacion.getDetalleLicitacion().size() > 0) {
                this.guardarDetalleLicitacion(licitacionGenerada, objLicitacion.getDetalleLicitacion(), "INS");
            }
            if (objLicitacion.getProveedorLicitacion() != null && objLicitacion.getProveedorLicitacion().size() > 0) {
                this.guardarProveedorLicitacion(licitacionGenerada, objLicitacion.getProveedorLicitacion(), "INS");
            }

            // EAAR: de ONBASE  a  DOCUMENT MANAGER
            // Cargar Archvios Licitacion

            List<LicitacionAdjunto> listAdjuntos = this.cargarAdjuntosLicitacion(licitacion, objLicitacion.getLicitacionAdjuntoBase64Dto());

            List<LicitacionSubetapa> listaSubEtapas = this.guardarLicitacionSubEtapa(
                licitacionGenerada, sortedListSubEtapa, "INS");

            response.setSubetapaLicitacion(listaSubEtapas);
            response.setIdLicitacion(licitacionGenerada.getIdLicitacion());
            response.setNroLicitacionString(this.getNroLicitacionString(
                licitacionGenerada.getNroLicitacion(),
                licitacionGenerada.getAnioLicitacion()));
            response.setEstadoLicitacion(licitacionGenerada.getEstadoLicitacion());
            response.setAdjuntoLicitacion(listAdjuntos);
            logger.error("Ingresando CBAZALAR saveLicitacion 3");
        }
        logger.error("Ingresando CBAZALAR saveLicitacion fin");
        return response;
    }

    @Override
    public LicitacionRequest getLicitacionById(Integer idLicitacion) {
        LicitacionRequest licitacionRequest = new LicitacionRequest();
        Licitacion licitacion = this.licitacionRepository.findById(idLicitacion).get();

        licitacionRequest.setIdLicitacion(licitacion.getIdLicitacion());
        licitacionRequest.setNroLicitacionString(this.getNroLicitacionString(
            licitacion.getNroLicitacion(),
            licitacion.getAnioLicitacion()));
        licitacionRequest.setNroLicitacion(licitacion.getNroLicitacion());
        licitacionRequest.setAnioLicitacion(licitacion.getAnioLicitacion());
        if (Optional.ofNullable(licitacion.getClaseDocumento()).isPresent()) {
            logger.error("Ingresando a CLASE DOCUMENTO");

            licitacionRequest.setIdClaseDocumento(licitacion.getClaseDocumento().getIdClaseDocumento());
            licitacionRequest.setDesClaseDocumento(licitacion.getClaseDocumento().getDescripcion());
        }


        licitacionRequest.setCodGradoUrgencia(licitacion.getNecesidadUrgencia());
        String desGradoUrg = ("01").equals(licitacion.getNecesidadUrgencia()) ? "Normal" : "Urgente";
        licitacionRequest.setDesGradoUrgencia(desGradoUrg);
        licitacionRequest.setIdMoneda(licitacion.getMoneda().getIdMoneda());
        licitacionRequest.setDesMoneda(licitacion.getMoneda().getTextoBreve());
        licitacionRequest.setCodPuntoEntrega(licitacion.getPuntoEntrega());
        licitacionRequest.setDesPuntoEntrega(parametroRepository.getParametroByModuloAndTipoAndCodigo(
            LicitacionConstant.PARAM_MODULO_LIC, LicitacionConstant.PARAM_TIPO_PTOENTREGA, licitacion.getPuntoEntrega()
        ).getValor());
        licitacionRequest.setFechaEntregaInicio(this.ft_diamesanio.format(licitacion.getFechaEntregaInicio()));
        licitacionRequest.setFechaRecepcionOferta(this.ft_diamesanio_time.format(licitacion.getFechaCierreRecepcionOferta()));
        licitacionRequest.setFechaUltimaRenegociacion(this.ft_diamesanio_time.format(licitacion.getFechaUltimaRenegociacion()));
        licitacionRequest.setEstadoLicitacion(licitacion.getEstadoLicitacion());

        licitacionRequest.setComentarioAnulacion(licitacion.getComentarioAnulacion());
        licitacionRequest.setComentarioLicitacion(licitacion.getComentarioLicitacion());
        if (licitacion.getFechaPublicacion() != null) {
            licitacionRequest.setFechaPublicacion(ft_diamesanio.format(licitacion.getFechaPublicacion()));
        }
        licitacionRequest.setUsuarioPublicacionId(licitacion.getUsuarioPublicacionId());
        licitacionRequest.setUsuarioPublicacionName(licitacion.getUsuarioPublicacionName());
        licitacionRequest.setUsuarioPublicacionEmail(licitacion.getUsuarioPublicacionEmail());

        List<LicitacionDetalle> listDetalleLicitacion = this.licitacionDetalleRepository.
            findByIdLicitacionOrdenado(licitacion.getIdLicitacion());

        List<Condicion> listCondicion = this.condicionMapper.getListCondicionByIdLicitacion(licitacion.getIdLicitacion());
        listCondicion.forEach(item -> {
            List<RespuestaCondicion> listRespuestaCondicion =
                this.respuestaCondicionMapper.getListRespuestaCondicionLic(item.getIdGrupoCondicionLic());
            item.setListaRespuestas(listRespuestaCondicion);

            if (listRespuestaCondicion.size() > 0) {
                listRespuestaCondicion.forEach(respuesta -> {
                    if (("1").equals(respuesta.getIndPredefinido())) {
                        item.setRespuestaPredefinida(respuesta.getOpcion());
                    }
                });
            }
        });

        List<ProveedorCustom> listProveedorLicitacion =
            this.licitacionProveedorMapper.getListProveedorByIdLicitacion1(licitacion.getIdLicitacion());
        for (int i = 0; i < listProveedorLicitacion.size() - 1; i++) {
            for (int j = listProveedorLicitacion.size() - 1; j > i; j--) {
                if (listProveedorLicitacion.get(j).getRuc().equals(listProveedorLicitacion.get(i).getRuc())) {
                    listProveedorLicitacion.remove(j);
                }
            }
        }
        List<LicitacionAdjunto> listAdjuntoLicitacion = this.licitacionAdjuntoRepository.findByLicitacionOrderByArchivoNombre(licitacion);

        //Lista Sub Etapas

        List<LicitacionSubetapa> listaLicitacionSubEtapas = this.licitacionSubetapaRepository.
            findLicitacionSubetapaByIdLicitacionOrderByFechaCierreSubetapa(licitacion);
        licitacionRequest.setSubetapaLicitacion(this.SubEtapaConvertido(listaLicitacionSubEtapas));
        licitacionRequest.setCondicionLicitacion(listCondicion);
        licitacionRequest.setProveedorLicitacion(listProveedorLicitacion);
        licitacionRequest.setDetalleLicitacion(listDetalleLicitacion);
        licitacionRequest.setAdjuntoLicitacion(listAdjuntoLicitacion);
        licitacionRequest.setNombreLicitacion(licitacion.getNombreLicitacion());

        //almacen
        if (licitacion.getCentroAlmacen1() != null) {
            licitacionRequest.setIdCentroLogistico(licitacion.getCentroAlmacen1().getIdCentroAlmacen());
        } else {
            licitacionRequest.setIdCentroLogistico(0);
        }

        if (licitacion.getCentroAlmacen2() != null) {
            licitacionRequest.setIdAlmacen(licitacion.getCentroAlmacen2().getIdCentroAlmacen());
        } else {
            licitacionRequest.setIdAlmacen(0);
        }

        return licitacionRequest;
    }

    @Override
    public String sendMail(Integer idLicitacion, String emailPublicacion) {
        String rptaProceso = "exito";
        //UserSession userSession = this.getUserSession();

        Licitacion licitacion = licitacionRepository.findById(idLicitacion).get();
        List<ProveedorCustom> listProveedorLicitacion = this.licitacionProveedorMapper.getListProveedorByIdLicitacion(idLicitacion);
        List<LicitacionDetalle> listDetalleLicitacion = this.licitacionDetalleRepository.findByLicitacionOrdenado(licitacion);
        List<Condicion> listCondicion = this.condicionMapper.getListCondicionByIdLicitacion(licitacion.getIdLicitacion());

        String nroLicitacionString = getNroLicitacionString(licitacion.getNroLicitacion(),
            licitacion.getAnioLicitacion());

        licitacion.setNroLicitacionString(nroLicitacionString);


        if (("GE").equals(licitacion.getEstadoLicitacion()) && listProveedorLicitacion.size() > 0 &&
            listDetalleLicitacion.size() > 0 && listCondicion.size() > 0) {

            Date fechaActual = DateUtils.obtenerFechaHoraActual();
            List<LicitacionSubetapa> licitacionSubetapaList = this.licitacionSubetapaRepository.
                findLicitacionSubetapaByIdLicitacionOrderByFechaCierreSubetapa(licitacion);
            if (licitacionSubetapaList != null && licitacionSubetapaList.size() > 0) {
                LicitacionSubetapa licitacionSubetapa = licitacionSubetapaList.get(0);
                Date fechaSubetapa = licitacionSubetapa.getFechaCierreSubetapa();
                if (fechaSubetapa.compareTo(fechaActual) < 0) {
                    throw new PortalException("No se puede PUBLICAR porque la Fecha de Confirmación de Propuestas es menor a la Fecha Actual. Debe modificar los datos de la Etapa de la Licitación");
                }
            }

            licitacion.setEstadoLicitacion(EstadoLicitacionEnum.PENDIENTE_RESPUESTA.getCodigo());
            licitacion.setFechaPublicacion(new Timestamp(fechaActual.getTime()));
//            licitacion.setUsuarioPublicacionId(userSession.getId());
//            licitacion.setUsuarioPublicacionName(userSession.getDisplayName());
            licitacion.setUsuarioPublicacionEmail(emailPublicacion);

            //this.licitacionRepository.updateLicitacionOnPublicacion(idLicitacion, "PE", new Date(), userSession.getId(), userSession.getDisplayName(), userSession.getMail());
            Licitacion licitacionGen = this.licitacionRepository.save(licitacion);
            logger.debug("Se actualizó estado de Licitacion a PENDIENTE DE RESPUESTA");

            for (LicitacionSubetapa bean : licitacionSubetapaList) {
                Date fechaCierre = bean.getFechaCierreSubetapa();
                String sFechaCierre = DateUtils.convertDateToString("dd/MM/yyyy HH:mm:ss", fechaCierre);
                bean.setFechaCierreSubetapaString(sFechaCierre);
            }

            logger.debug("INICIO: Envio de email a proveedores ..");
            for (ProveedorCustom proveedor : listProveedorLicitacion) {
                licitacionProveedorNotificacion.enviar(
                    parametroMapper.getMailSetting(),
                    proveedor,
                    licitacionGen,
                    licitacionSubetapaList);
            }
            logger.debug("FIN: Envio de email a proveedores ..");
            rptaProceso = "exito";

        } else {
            logger.error("No es posible publicar la licitacion");
            rptaProceso = "No es posible publicar la licitacion";
        }
        return rptaProceso;
    }

    @Override
    public String anularLicitacion(Map<String, Object> json) {

        String rptaProceso = "";
        UserSession userSession = this.getUserSession();

        Integer idLicitacion = (Integer) json.get("idLicitacion");
        String comentarioAnulacion = (String) json.get("comentarioAnulacion");

        Licitacion licitacion = licitacionRepository.findById(idLicitacion).get();
        if ((EstadoLicitacionEnum.ADJUDICADA.getCodigo()).equals(licitacion.getEstadoLicitacion()) ||
            (EstadoLicitacionEnum.ENVIADO_SAP.getCodigo()).equals(licitacion.getEstadoLicitacion()) ||
            (EstadoLicitacionEnum.ANULADA.getCodigo()).equals(licitacion.getEstadoLicitacion())) {
            return "No es posible anular la licitacion. Estado Actual: " + licitacion.getEstadoLicitacion();
        }

        String estadoActualLicitacion = licitacion.getEstadoLicitacion();

        licitacion.setComentarioAnulacion(comentarioAnulacion);
        licitacion.setUsuarioAnulacionId(userSession.getId());
        licitacion.setEstadoLicitacion(EstadoLicitacionEnum.ANULADA.getCodigo());
        licitacion.setUsuarioModificacion(userSession.getId());
        licitacion.setFechaModificacion(new Timestamp(new Date().getTime()));
        licitacionRepository.save(licitacion);

        List<ProveedorCustom> listProveedorLicitacion = this.licitacionProveedorMapper.getListProveedorByIdLicitacion(idLicitacion);
        logger.debug("Se actualizó estado de Licitación a ANULADA");


        if (listProveedorLicitacion != null && listProveedorLicitacion.size() > 0 && !(EstadoLicitacionEnum.GENERADA.getCodigo()).equals(estadoActualLicitacion)) {
            logger.debug("INICIO: Envio de email a proveedores ..");
            for (ProveedorCustom proveedor : listProveedorLicitacion) {
                licitacionAnularNotificacion.enviar(parametroMapper.getMailSetting(), proveedor, licitacion, userSession);
            }
            logger.debug("FIN: Envio de email a proveedores ..");
        }
        rptaProceso = "anulada";

        return rptaProceso;


    }

    @Override
    public String republicarLicitacion(Map<String, Object> json) {

        String termino = "";

        Integer idLicitacion = (Integer) json.get("idLicitacion");
        String fechaInicioNuevo = json.get("fechaInicioNuevo") != null ? (String) json.get("fechaInicioNuevo") : "";
        String fechaCierreNuevo = json.get("fechaCierreNuevo") != null ? (String) json.get("fechaCierreNuevo") : "";
        String comentarioControlCambio = (String) json.get("comentario");

        Licitacion licitacion = licitacionRepository.getOne(idLicitacion);
        List<ProveedorCustom> listProveedorLicitacion = this.licitacionProveedorMapper.getListProveedorByIdLicitacion(idLicitacion);
        UserSession userSession = this.getUserSession();
        String nroLicitacionString = getNroLicitacionString(licitacion.getNroLicitacion(),
            licitacion.getAnioLicitacion());

        licitacion.setNroLicitacionString(nroLicitacionString);

        if ((EstadoLicitacionEnum.PENDIENTE_RESPUESTA.getCodigo()).equals(licitacion.getEstadoLicitacion())) {

            //guardar licitacioncontrolcambios
            LicitacionControlCambio licitacionControlCambio = new LicitacionControlCambio();
            licitacionControlCambio.setLicitacion(licitacion);
            licitacionControlCambio.setComentario(comentarioControlCambio);
            licitacionControlCambio.setUsuarioCreacion(userSession.getDisplayName());
            licitacionControlCambio.setFechaCreacion(new Timestamp(new Date().getTime()));
            licitacionControlCambioRepository.save(licitacionControlCambio);
            logger.debug("LicitacionControlCambio se ha guardado");

            //guardar controlcambiocampos
            String camposEdit = "";

            //guardando fecha_cierre_recepcion_oferta
            if (!fechaCierreNuevo.trim().isEmpty() && fechaCierreNuevo.trim() != "") {

                logger.debug("Ingresando. Guardar FechaCierreNuevo con valor en ControlCambioCampo");
                controlCambioCampoRepository.save(
                    new ControlCambioCampo("fecha_cierre_recepcion_oferta", this.ft_diamesanio_time.format(licitacion.getFechaCierreRecepcionOferta()),
                        fechaCierreNuevo, licitacionControlCambio)
                );
                licitacion.setFechaCierreRecepcionOferta(this.convertToTimestamp(fechaCierreNuevo, true));
                camposEdit = camposEdit.concat("Fecha Cierre Recepción Ofertas: " + fechaCierreNuevo);
            }

            //guardando fecha_entrega_inicio
            if (!fechaInicioNuevo.trim().isEmpty() && fechaInicioNuevo.trim() != "") {

                logger.debug("Ingresando. Guardar FechaInicioNuevo con valor en ControlCambioCampo");
                controlCambioCampoRepository.save(
                    new ControlCambioCampo("fecha_entrega_inicio", this.ft_diamesanio.format(licitacion.getFechaEntregaInicio()),
                        fechaInicioNuevo, licitacionControlCambio)
                );
                licitacion.setFechaEntregaInicio(this.convertToTimestamp(fechaInicioNuevo, false));
                String separator = !fechaCierreNuevo.trim().isEmpty() && fechaCierreNuevo.trim() != "" ? ", " : "";
                camposEdit = camposEdit.concat(separator);
                camposEdit = camposEdit.concat("Fecha Entrega/ Inicio Servicio: " + fechaInicioNuevo);
            }

            logger.debug("ControlCambioCampo se ha guardado");
            //actualizar licitacion
            licitacion.setIndRepublicado("1");
            licitacionRepository.save(licitacion);
            logger.debug("Se actualizó licitacion");

            //notificar proveedores
            for (ProveedorCustom proveedor : listProveedorLicitacion) {
                //licitacionProveedorNotificacion.enviar(parametroMapper.getMailSetting(), proveedor, licitacionGen);
                licitacionAmpliarFechaCierreNotificacion.enviar(parametroMapper.getMailSetting(), proveedor, licitacion,
                    camposEdit, userSession.getDisplayName(), userSession.getMail());
            }
            logger.debug("Se ha notificado nuevamente a los proveedores");

        }
        termino = "exito";

        return termino;
    }

    @Override
    public Workbook reporteLicitacion(Integer idLicitacion) {

        LicitacionRequest licitacionRequestExcel = this.getLicitacionById(idLicitacion);

        List<Map<String, Object>> mapEstado = new ArrayList<>();

        Map<String, Object> map = new LinkedMap<>();

        String estado = Optional.ofNullable(licitacionRequestExcel.getEstadoLicitacion())
            .map(codigo -> {
                switch (licitacionRequestExcel.getEstadoLicitacion()) {
                    case "PE":
                        return "PENDIENTE DE RESPUESTA";
                    case "GE":
                        return "GENERADA";
                    case "EV":
                        return "EN EVALUACION";
                    case "AD":
                        return "ADJUDICADA";
                    case "AN":
                        return "ANULADA";
                    default:
                        return "-";
                }
            }).orElse(STRING_HYPHEN);

        map.put("NUMERO LICITACION", Optional.ofNullable(licitacionRequestExcel.getNroLicitacionString()).orElse(STRING_HYPHEN));
        map.put("FECHA CIERRE RECEPCION OFERTA", Optional.ofNullable(licitacionRequestExcel.getFechaRecepcionOferta()).orElse(STRING_HYPHEN));
        map.put("FECHA CIERRE ULTIMA RENEGOCIACION", Optional.ofNullable(licitacionRequestExcel.getFechaUltimaRenegociacion()).orElse(STRING_HYPHEN));
        map.put("ESTADO LICITACION", estado);
        map.put("FECHA PUBLICACION", Optional.ofNullable(licitacionRequestExcel.getFechaPublicacion()).orElse(STRING_HYPHEN));
        mapEstado.add(map);
        String titulo = "Licitacion N° " + licitacionRequestExcel.getNroLicitacionString();
        Map<String, Object> dataEstado = new LinkedMap<>();
        dataEstado.put("fileName", "Estado"); // sheet-name
        dataEstado.put("data", mapEstado);

        ////Datos Generales

        Workbook workbook = this.exportDataService.mapToWorkbook(titulo, dataEstado);
        this.exportDataService.addSheet(workbook, this.getDatosGenerales(licitacionRequestExcel), "Información General de Licitación");
        this.exportDataService.addSheet(workbook, this.getListaBienesServicios(licitacionRequestExcel), "Bienes y Servicios Seleccionados");
        this.exportDataService.addSheet(workbook, this.getListaProveedores(licitacionRequestExcel), "Proveedores Seleccionados");
        this.exportDataService.addSheet(workbook, this.getListaCondiciones(licitacionRequestExcel), "Condiciones de la Licitación");
        this.exportDataService.addSheet(workbook, this.getListaAdjuntos(licitacionRequestExcel), "Adjuntos Licitacion");
        this.exportDataService.addSheet(workbook, this.getListaEtapas(licitacionRequestExcel), "Lista Etapas");
        this.exportDataService.addSheet(workbook, this.getListaRenegociaciones(idLicitacion), "Renegociaciones");
        this.exportDataService.addSheet(workbook, this.getListaConsultas(idLicitacion), "Lista Consultas");
        this.exportDataService.addSheet(workbook, this.getListaAbsolucionConsultas(idLicitacion), "Respuesta de Consultas");


        return workbook;
    }

    /**
     * JRAMOS - UPDATE
     */
    @Override
    public String mailForwarding(List<Integer> proveedors, Integer idLicitacion, String cuerpo) {
        String rptaProceso = "exito";

        Licitacion licitacion = licitacionRepository.getOne(idLicitacion);

        List<ProveedorCustom> listProveedorLicitacion =
            proveedors.stream().map(p -> {
                return this.licitacionProveedorMapper.getProveedorCustomByIdProveedor(p, idLicitacion);
            }).collect(Collectors.toList());

        String nroLicitacionString = getNroLicitacionString(licitacion.getNroLicitacion(),
            licitacion.getAnioLicitacion());

        licitacion.setNroLicitacionString(nroLicitacionString);


        if (listProveedorLicitacion.size() > 0) {

            List<LicitacionSubetapa> licitacionSubetapaList = this.licitacionSubetapaRepository.
                findLicitacionSubetapaByIdLicitacionOrderByFechaCierreSubetapa(licitacion);

            for (LicitacionSubetapa bean : licitacionSubetapaList) {
                Date fechaCierre = bean.getFechaCierreSubetapa();
                String sFechaCierre = DateUtils.convertDateToString("dd/MM/yyyy HH:mm:ss", fechaCierre);
                bean.setFechaCierreSubetapaString(sFechaCierre);
            }

            logger.debug("INICIO: Envio de email a proveedores ..");
            for (ProveedorCustom proveedor : listProveedorLicitacion) {
                licitacionProveedorNotificacion.reenviar(parametroMapper.getMailSetting(), proveedor, licitacion, cuerpo);
            }
            logger.debug("FIN: Envio de email a proveedores ..");
            rptaProceso = "exito";

        } else {
            logger.error("No es posible publicar la licitacion");
            rptaProceso = "No es posible publicar la licitacion";
        }
        return rptaProceso;
    }


    public Map<String, Object> getDatosGenerales(LicitacionRequest licitacionRequest) {

        List<Map<String, Object>> list = new ArrayList<>();


        Map<String, Object> map = new LinkedMap<>();

        map.put("CLASE DOCUMENTO", Optional.ofNullable(licitacionRequest.getDesClaseDocumento()).orElse(STRING_HYPHEN));
        map.put("MONEDA", Optional.ofNullable(licitacionRequest.getDesMoneda()).orElse(STRING_HYPHEN));
        map.put("PUNTO DE ENTREGA", Optional.ofNullable(licitacionRequest.getDesPuntoEntrega()).orElse(STRING_HYPHEN));
        map.put("GRADO DE URGENCIA", Optional.ofNullable(licitacionRequest.getDesGradoUrgencia()).orElse(STRING_HYPHEN));
        map.put("COMENTARIOS", Optional.ofNullable(licitacionRequest.getComentarioLicitacion()).orElse(STRING_HYPHEN));
        list.add(map);

        Map<String, Object> data = new LinkedMap<>();
        data.put("fileName", "General"); // sheet-name
        data.put("data", list);

        return data;


    }

    public Map<String, Object> getListaBienesServicios(LicitacionRequest licitacionRequest) {

        List<LicitacionDetalle> listaLicitacionServcios = licitacionRequest.getDetalleLicitacion();

        List<Map<String, Object>> list = new ArrayList<>();


        for (LicitacionDetalle beanDetalle : listaLicitacionServcios) {
            Map<String, Object> map = new LinkedMap<>();

            map.put("SOLICITUD PEDIDO", Optional.ofNullable(beanDetalle.getSolicitudPedido()).orElse(STRING_HYPHEN));
            map.put("POSICION", Optional.ofNullable(beanDetalle.getPosicionSolicitudPedido()).orElse(STRING_HYPHEN));
            map.put("CENTRO", beanDetalle.getIdCentro() != null ? (Optional.ofNullable(beanDetalle.getIdCentro().getDenominacion()).orElse(STRING_HYPHEN)) : STRING_HYPHEN);
            map.put("ALMACEN", beanDetalle.getIdAlmacen() != null ? Optional.ofNullable(beanDetalle.getIdAlmacen().getDenominacion()).orElse(STRING_HYPHEN) : STRING_HYPHEN);
            String codigoBienServicio = "";

            if (Optional.ofNullable(beanDetalle.getBienServicio()).isPresent()) {
                codigoBienServicio = beanDetalle.getBienServicio().getCodigoSap();
            }
            map.put("CODIGO SAP", Optional.ofNullable(codigoBienServicio).orElse(STRING_HYPHEN));
            map.put("DESCRIPCION", Optional.ofNullable(beanDetalle.getDescripcion()).orElse(STRING_HYPHEN));
            map.put("CANTIDAD", Optional.ofNullable(beanDetalle.getCantidadSolicitada()).orElse(new BigDecimal(0.0)));
            list.add(map);
        }

        Map<String, Object> data = new LinkedMap<>();
        data.put("fileName", "Bienes y Servicios"); // sheet-name
        data.put("data", list);

        return data;


    }

    public Map<String, Object> getListaProveedores(LicitacionRequest licitacionRequest) {

        List<ProveedorCustom> listaProveedor = licitacionRequest.getProveedorLicitacion();

        List<Map<String, Object>> list = new ArrayList<>();


        for (ProveedorCustom beanProveedor : listaProveedor) {
            Map<String, Object> map = new LinkedMap<>();


            map.put("RUC", Optional.ofNullable(beanProveedor.getRuc()).orElse(STRING_HYPHEN));
            map.put("RAZON SOCIAL", Optional.ofNullable(beanProveedor.getRazonSocial()).orElse(STRING_HYPHEN));
            map.put("TIPO PROVEEDOR", Optional.ofNullable(beanProveedor.getTipoProveedor()).orElse(STRING_HYPHEN));
            map.put("TIPO PERSONA", Optional.ofNullable(beanProveedor.getTipoPersona()).orElse(STRING_HYPHEN));
            map.put("CORREO ELECTRONICO", Optional.ofNullable(beanProveedor.getEmail()).orElse(STRING_HYPHEN));

            list.add(map);
        }

        Map<String, Object> data = new LinkedMap<>();
        data.put("fileName", "Proveedores"); // sheet-name
        data.put("data", list);

        return data;


    }

    public Map<String, Object> getListaCondiciones(LicitacionRequest licitacionRequest) {

        List<Condicion> listaCondicion = licitacionRequest.getCondicionLicitacion();

        List<Map<String, Object>> list = new ArrayList<>();


        for (Condicion beanCondicion : listaCondicion) {
            Map<String, Object> map = new LinkedMap<>();


            map.put("TIPO LICITACION", Optional.ofNullable(beanCondicion.getDescripcionTipoLicitacion()).orElse(STRING_HYPHEN));
            map.put("TIPO CUESTIONARIO", Optional.ofNullable(beanCondicion.getDescripcionTipoCuestionario()).orElse(STRING_HYPHEN));
            map.put("DESCRIPCION", Optional.ofNullable(beanCondicion.getPregunta()).orElse(STRING_HYPHEN));
            map.put("RESPUESTA DEFINIDA", Optional.ofNullable(beanCondicion.getRespuestaPredefinida()).orElse(STRING_HYPHEN));


            list.add(map);
        }

        Map<String, Object> data = new LinkedMap<>();
        data.put("fileName", "Condiciones"); // sheet-name
        data.put("data", list);

        return data;


    }

    public Map<String, Object> getListaAdjuntos(LicitacionRequest licitacionRequest) {

        List<LicitacionAdjunto> listaAdjuntos = licitacionRequest.getAdjuntoLicitacion();

        List<Map<String, Object>> list = new ArrayList<>();


        for (LicitacionAdjunto beanAdjuntos : listaAdjuntos) {
            Map<String, Object> map = new LinkedMap<>();


            map.put("ARCHIVO", Optional.ofNullable(beanAdjuntos.getArchivoNombre()).orElse(STRING_HYPHEN));
            map.put("TIPO", Optional.ofNullable(beanAdjuntos.getArchivoTipo()).orElse(STRING_HYPHEN));


            list.add(map);
        }

        Map<String, Object> data = new LinkedMap<>();
        data.put("fileName", "Adjuntos"); // sheet-name
        data.put("data", list);

        return data;


    }

    public Map<String, Object> getListaEtapas(LicitacionRequest licitacionRequest) {

        List<LicitacionSubetapa> listaEtapas = licitacionRequest.getSubetapaLicitacion();

        List<Map<String, Object>> list = new ArrayList<>();


        for (LicitacionSubetapa beanSubEtapas : listaEtapas) {
            Map<String, Object> map = new LinkedMap<>();


            map.put("DESCRIPCION", Optional.ofNullable(beanSubEtapas.getIdSubetapa().getDescripcionSubetapa()).orElse(STRING_HYPHEN));
            map.put("FECHA CIERRE", Optional.ofNullable(beanSubEtapas.getFechaCierreSubetapaString()).orElse(STRING_HYPHEN));


            list.add(map);
        }

        Map<String, Object> data = new LinkedMap<>();
        data.put("fileName", "Etapas"); // sheet-name
        data.put("data", list);

        return data;


    }

    public Map<String, Object> getListaRenegociaciones(Integer idLicitacion) {

        List<LicitacionProveedorRenegociacion> listaRenegociaciones = licitacionProveedorRenegociacionRepository.findByLicitacionOrderByProveedorFechaCierreRecepcion(idLicitacion);

        List<Map<String, Object>> list = new ArrayList<>();


        for (LicitacionProveedorRenegociacion beanRenegociacion : listaRenegociaciones) {
            Map<String, Object> map = new LinkedMap<>();


            map.put("DESCRIPCION", Optional.ofNullable(beanRenegociacion.getIdLicitacion().getProveedor().getRazonSocial()).orElse(STRING_HYPHEN));
            map.put("FECHA CIERRE", Optional.ofNullable(this.ft_diamesanio_time.format(beanRenegociacion.getFechaCierreRecepcion())).orElse(STRING_HYPHEN));
            map.put("MOTIVO", Optional.ofNullable(beanRenegociacion.getMotivoRenegociacion()).orElse(STRING_HYPHEN));


            list.add(map);
        }

        Map<String, Object> data = new LinkedMap<>();
        data.put("fileName", "Renegociaciones"); // sheet-name
        data.put("data", list);

        return data;


    }

    public Map<String, Object> getListaConsultas(Integer idLicitacion) {

        List<LicitacionProveedorPregunta> listaLicitacionProveedorPregunta = licitacionProveedorPreguntaRepository.findByIdLicitacionOrderByIdProveedor(idLicitacion);

        List<Map<String, Object>> list = new ArrayList<>();

        for (LicitacionProveedorPregunta licitacionProveedorPregunta : listaLicitacionProveedorPregunta) {
            Proveedor proveedor = this.proveedorRepository.getOne(licitacionProveedorPregunta.getIdProveedor());
            Licitacion licitacion = this.licitacionRepository.getOne(licitacionProveedorPregunta.getIdLicitacion());
            licitacionProveedorPregunta.setProveedor(proveedor);
            licitacionProveedorPregunta.setLicitacion(licitacion);
        }

        for (LicitacionProveedorPregunta licitacionProveedorPregunta : listaLicitacionProveedorPregunta) {
            Map<String, Object> map = new LinkedMap<>();

            map.put("PROVEEDOR", licitacionProveedorPregunta.getProveedor().getRazonSocial());
            map.put("PREGUNTA", licitacionProveedorPregunta.getPregunta());


            list.add(map);
        }

        Map<String, Object> data = new LinkedMap<>();
        data.put("fileName", "Consultas"); // sheet-name
        data.put("data", list);

        return data;


    }

    public Map<String, Object> getListaAbsolucionConsultas(Integer idLicitacion) {

        List<LicitacionAdjuntoRespuesta> listaAdjuntosRespuesta = licitacionAdjuntoRespuestaRepository.findByIdLicitacionOrderByArchivoId(idLicitacion);

        List<Map<String, Object>> list = new ArrayList<>();


        for (LicitacionAdjuntoRespuesta beanAdjuntoRespuesta : listaAdjuntosRespuesta) {
            Map<String, Object> map = new LinkedMap<>();


            map.put("ARCHIVO", Optional.ofNullable(beanAdjuntoRespuesta.getArchivoNombre()).orElse(STRING_HYPHEN));
            map.put("TIPO", Optional.ofNullable(beanAdjuntoRespuesta.getArchivoTipo()).orElse(STRING_HYPHEN));


            list.add(map);
        }

        Map<String, Object> data = new LinkedMap<>();
        data.put("fileName", "Absolucion Consultas"); // sheet-name
        data.put("data", list);

        return data;


    }

    public void guardarCondicionLicitacion(Licitacion licitacionGen, List<Condicion> listaCondicionLicitacion, String operacion) {

        TipoLicitacion tipoLicitacion = new TipoLicitacion();
        TipoLicitacion tipoCuestionario = new TipoLicitacion();

        if (("UPD").equals(operacion)) {
            List<Condicion> lstGrupoCondicion = condicionMapper.getListCondicionByIdLicitacion(licitacionGen.getIdLicitacion());
            if (lstGrupoCondicion.size() > 0 && lstGrupoCondicion != null) {
                lstGrupoCondicion.forEach(item -> {
                    grupoCondicionLicRespuestaRepository.deleteGrupoRespuestaByGrupoCondicion(item.getIdGrupoCondicionLic());
                });
                licitacionGrupoCondicionLicRepository.deleteGrupoCondicionByLicitacion(licitacionGen.getIdLicitacion());
            }
        }

        listaCondicionLicitacion.forEach(item -> {
            tipoLicitacion.setIdTipoLicitacion(item.getIdTipoLicitacion());
            tipoCuestionario.setIdTipoLicitacion(item.getIdTipoCuestionario());
            String strTipoCampo = ("Lista").equals(item.getTipoCampo()) ? "L" : "T";

            LicitacionGrupoCondicionLic grupoCondicion = licitacionGrupoCondicionLicRepository.save(
                new LicitacionGrupoCondicionLic(item.getIdCondicion(), item.getIndBloqueadoProveedor(), item.getPeso(), item.getPregunta(),
                    strTipoCampo, licitacionGen, tipoLicitacion, tipoCuestionario));

            item.getListaRespuestas().forEach(rpta -> {
                grupoCondicionLicRespuestaRepository.save(new GrupoCondicionLicRespuesta(rpta.getIndPredefinido(), rpta.getOpcion(),
                    rpta.getPuntaje(), grupoCondicion));
            });
        });

    }

    public void guardarDetalleLicitacion(Licitacion licitacionGen, List<LicitacionDetalle> listDetalle, String operacion) {

        if (("UPD").equals(operacion)) {
            this.licitacionDetalleRepository.deleteDetailByLicitacion(licitacionGen.getIdLicitacion());
        }

        listDetalle.forEach(item -> {
            logger.error("GUARDAR LICITACION Id: " + licitacionGen.getIdLicitacion() + " // LicitacionDetalle: " + item.toString());
//            this.licitacionDetalleRepository.save(new LicitacionDetalle(
//                    item.getCantidadSolicitada(), item.getCantidadSolped(), item.getDescripcion(), item.getEspecificacion(),
//                    item.getNumeroParte(), item.getPosicionSolicitudPedido(), item.getSolicitudPedido(), item.getGrupoArticulo(), item.getBienServicio(),
//                    licitacionGen, item.getUnidadMedida(),
//                    item.getIdCentro(),
//                    item.getIdAlmacen()
//            ));
            LicitacionDetalle licitacionDetalle = new LicitacionDetalle();

            licitacionDetalle.setIdRubroBien(item.getIdRubroBien());

            licitacionDetalle.setSolicitudPedido(item.getSolicitudPedido());
            licitacionDetalle.setCantidadSolped(item.getCantidadSolped());
            licitacionDetalle.setIdAlmacen(item.getIdAlmacen());
            licitacionDetalle.setCantidadSolicitada(item.getCantidadSolicitada());
            licitacionDetalle.setDescripcion(item.getDescripcion());
            licitacionDetalle.setEspecificacion(item.getEspecificacion());
            licitacionDetalle.setNumeroParte(item.getNumeroParte());
            licitacionDetalle.setPosicionSolicitudPedido(item.getPosicionSolicitudPedido());
            licitacionDetalle.setGrupoArticulo(item.getGrupoArticulo());
            licitacionDetalle.setPrecioSolped(item.getPrecioSolped());
            if (item.getIdCentro() != null)
                licitacionDetalle.setIdNumberCentro(item.getIdCentro().getIdCentroAlmacen());
//            licitacionDetalle.setBienServicio(item.getBienServicio());
            if (item.getBienServicio() != null)
                licitacionDetalle.setIdNumberBienServicio(item.getBienServicio().getIdBienServicio());
            licitacionDetalle.setIdNumberLicitacion(licitacionGen.getIdLicitacion());
            licitacionDetalle.setUnidadMedida(item.getUnidadMedida());
            licitacionDetalle.setSociedad(item.getSociedad());

            licitacionDetalle.setAreaSolicitante(item.getAreaSolicitante());

            logger.error("GUARDAR LICITACION Id: " + licitacionGen.getIdLicitacion() + " // new LicitacionDetalle: " + licitacionDetalle.toString());
            this.licitacionDetalleRepository.save(licitacionDetalle);
        });
    }

    public void guardarProveedorLicitacion(
        Licitacion licitacionGen,
        List<ProveedorCustom> listProveedor,
        String operacion)
        throws PortalException {

        Timestamp fechaSistema = new Timestamp(new Date().getTime());
        if (("UPD").equals(operacion)) {
            this.licitacionProveedorRepository.deleteLicitacionProveedorByIdLicitacion(licitacionGen.getIdLicitacion());
        }
        Timestamp fechaCierre = licitacionGen.getFechaCierreRecepcionOferta();
        Proveedor oProveedor;
        for (ProveedorCustom customProveedor : listProveedor) {
            oProveedor = proveedorRepository.getOne(customProveedor.getIdProveedor());
            if (oProveedor == null) {
                throw new PortalException("No se encuentra proveedor NULL");
            }
            if (("1").equals(oProveedor.getIndBlackList())) {
                throw new PortalException("Se identifico Proveedor Observado: " + oProveedor.getRuc() + ". No puede participar");
            }
            this.licitacionProveedorRepository.save(
                new LicitacionProveedor(
                    new LicitacionProveedorPK(oProveedor.getIdProveedor(), licitacionGen.getIdLicitacion()),
                    fechaSistema,
                    licitacionGen,
                    oProveedor,
                    fechaCierre,
                    Constant.N
                ));
        }
    }

    public List<LicitacionAdjunto> guardarAdjuntoLicitacion(Licitacion licitacionGen, List<LicitacionAdjunto> listAdjunto, String operacion) throws Exception {

        //Creo una segunda lista con los adjuntos no guardados
        List<com.incloud.hcp.service.cmiscf.bean.CmisFile> listAdjuntoNew = new ArrayList<com.incloud.hcp.service.cmiscf.bean.CmisFile>();
        if (listAdjunto != null && listAdjunto.size() > 0) {
            listAdjunto.forEach(item -> {
                if (item.getIdLicitacionAdjunto() == null) {
                    listAdjuntoNew.add(new com.incloud.hcp.service.cmiscf.bean.CmisFile(item.getArchivoId(), item.getArchivoNombre(), item.getRutaAdjunto(), item.getArchivoTipo()));
                }
            });
        }

        //Se crea folder destino -> Nro de licitacion
        String newFolder = this.getNroLicitacionString(
            licitacionGen.getNroLicitacion(),
            licitacionGen.getAnioLicitacion());

        String folderId = cmisServiceCf.createFolder(newFolder).getId();
        logger.debug("FOLDER_DESTINO: " + folderId);

        //Se mueven los adjuntos al folder destino y se obtiene la lista de los mismos con su nuevo URL
        Optional<List<com.incloud.hcp.service.cmiscf.bean.CmisFile>> listAdjuntoMove = Optional.ofNullable(listAdjuntoNew)
            .map(list -> {
                logger.debug("Actualizando la version de los archivos adjuntos");
                return cmisServiceCf.updateFileAndMoveVerificar(listAdjuntoNew, folderId);
            });

        //Se guardan en la base de datos los adjuntos movidos
        if (listAdjuntoMove != null && listAdjuntoMove.isPresent()) {
            List<CmisFile> list = listAdjuntoMove.get();
            list.forEach(file -> {
                LicitacionAdjunto obj = licitacionAdjuntoRepository.save(new LicitacionAdjunto(licitacionGen, file.getUrl(), file.getId(), file.getName(), file.getType()));
            });
        }

        //Eliminando adjuntos ausentes en el request
        List<LicitacionAdjunto> listaAdjuntosActual = licitacionAdjuntoRepository.findByLicitacionOrderByDescripcion(licitacionGen);
        if (listaAdjuntosActual != null && listaAdjuntosActual.size() > 0) {
            for (LicitacionAdjunto obj1 : listaAdjuntosActual) {
                Boolean encontrado = false;
                for (LicitacionAdjunto obj2 : listAdjunto) {
                    if ((obj1.getArchivoId()).equals(obj2.getArchivoId())) {
                        encontrado = true;
                        break;
                    }
                }
                if (!encontrado) {
                    licitacionAdjuntoRepository.deleteLicitacionAdjuntoByLicitacionArchivoId(licitacionGen, obj1.getArchivoId());
                    //cmisServiceCf.deleteFiles(obj1.getArchivoId());
                    continue;
                }
            }
        }

        //Se obtiene lista de adjuntos final
        List<LicitacionAdjunto> listaAdjuntosResult = licitacionAdjuntoRepository.findByLicitacionOrderByDescripcion(licitacionGen);

        return listaAdjuntosResult;

    }

    public List<LicitacionSubetapa> guardarLicitacionSubEtapa(
        Licitacion licitacionGen,
        List<LicitacionSubetapa> licitacionSubetapas,
        String operacion) {
        if (("UPD").equals(operacion)) {
            this.licitacionSubetapaRepository.deleteDetailByLicitacion(licitacionGen.getIdLicitacion());
        }

        List<LicitacionSubetapa> lista = new ArrayList<>();
        licitacionSubetapas.forEach(item -> {
            Subetapa subetapa = item.getIdSubetapa();
            String indCorreo = subetapa.getIndEnvioCorreoRecordatorio();
            Date fechaEnvioCorreoRecordatorio = null;
            if (Optional.ofNullable(indCorreo).isPresent()) {
                if (indCorreo.equals(Constant.UNO)) {
                    fechaEnvioCorreoRecordatorio = this.convertToTimestamp(item.getFechaCierreSubetapaString(), true);
                    Integer deltaFechaCorreo = subetapa.getDiasEnvioCorreoRecordatorio();
                    fechaEnvioCorreoRecordatorio = DateUtils.sumarRestarDias(fechaEnvioCorreoRecordatorio, -1 * deltaFechaCorreo);
                    String fechaEnvioCorreoRecordatorioString = DateUtils.convertDateToString("dd/MM/yyyy", fechaEnvioCorreoRecordatorio);
                    fechaEnvioCorreoRecordatorio = DateUtils.convertStringToDate02("dd/MM/yyyy", fechaEnvioCorreoRecordatorioString);
                }
            }
            LicitacionSubetapa licitacionSubetapa = licitacionSubetapaRepository.save(
                new LicitacionSubetapa(
                    this.convertToTimestamp(item.getFechaCierreSubetapaString(), true),
                    fechaEnvioCorreoRecordatorio,
                    item.getIdSubetapa(),
                    licitacionGen,
                    item.getOrden()
                ));
            lista.add(licitacionSubetapa);
        });
        return lista;

    }

    public Timestamp convertToTimestamp(String strFecha, Boolean indTime) {
        if (StringUtils.isBlank(strFecha))
            return null;
        Timestamp timeStampDate = null;
        DateFormat formatter;

        if (indTime) {
            formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        } else {
            formatter = new SimpleDateFormat("dd/MM/yyyy");
        }

        Date date = null;
        try {
            date = (Date) formatter.parse(strFecha);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        timeStampDate = new Timestamp(date.getTime());

        return timeStampDate;
    }

    public String getNroLicitacionString(Integer numero, Integer annio) {
        String texto = ("00000000" + numero);
        String nroLicitacionString = annio + texto.substring(texto.length() - 8, texto.length());

        return nroLicitacionString;
    }

    public List<LicitacionSubetapa> SubEtapaConvertido(List<LicitacionSubetapa> lista) {
        List<LicitacionSubetapa> newLista = new ArrayList<>();
        for (LicitacionSubetapa licitacionSubEtapa : lista
        ) {
            licitacionSubEtapa.setFechaCierreSubetapaString(this.ft_diamesanio_time.format(licitacionSubEtapa.getFechaCierreSubetapa()));
            newLista.add(licitacionSubEtapa);
        }
        return newLista;
    }

    @Override
    public void updateLicitacionEstadoPorEvaluar() {

        ZoneId zona = ZoneId.systemDefault();
        Date fechaActual = DateUtils.obtenerFechaHoraActual();
        logger.error("Ingresando updateLicitacionEstadoPorEvaluar fechaActual con new Date(): " + fechaActual);

        String estadoLicitacion = LicitacionConstant.ESTADO_PUBLICADA;
        String estadoLicitacionPorEvaluar = LicitacionConstant.ESTADO_POR_EVALUAR;

        List<Licitacion> listaActualizar = this.licitacionRepository.findByLicitacionUltimaRenegociacion(estadoLicitacion, fechaActual);
        logger.error("Size listaActualizar: " + listaActualizar.size());
        for (Licitacion item : listaActualizar) {
            logger.debug("item Id: ", item.getIdLicitacion() + " Nro: " + item.getNroLicitacion());
            this.licitacionRepository.updateEstadoLicitacion02(
                estadoLicitacionPorEvaluar, fechaActual, item.getIdLicitacion());

            List<LicitacionProveedor> licitacionProveedorList = this.licitacionProveedorRepository.findByLicitacion(item);
            if (licitacionProveedorList != null && licitacionProveedorList.size() > 0) {
                for (LicitacionProveedor beanLicitacionProveedor : licitacionProveedorList) {
                    Cotizacion cotizacion = this.cotizacionRepository.findByLicitacionAndProveedor(
                        item.getIdLicitacion(),
                        beanLicitacionProveedor.getProveedor().getIdProveedor());
                    if (!Optional.ofNullable(cotizacion).isPresent()) {

                        /* Insertando Cotizacion (NO PARTICIPA) */
//                        Date fechaHoraActual = DateUtils.obtenerFechaHoraActual();
//                        Cotizacion cabecera = new Cotizacion();
//                        Moneda moneda = new Moneda();
//                        moneda.setIdMoneda(1);
//                        cabecera.setLicitacion(item);
//                        cabecera.setProveedor(beanLicitacionProveedor.getProveedor());
//                        cabecera.setMoneda(moneda);
//                        cabecera.setUsuarioCreacion(0);
//                        cabecera.setIdCotizacion(null);
//                        cabecera.setFechaCreacion( new Timestamp(fechaHoraActual.getTime()));
//                        cabecera.setIndGanador("0");
//                        cabecera.setEstadoCotizacion(CotizacionConstant.ESTADO_NO_PARTICIPAR);
//                        cabecera.setImporteTotal(new BigDecimal(0));
//                        this.cotizacionRepository.save(cabecera);

                        /* Actualizando Indicador No participa en S */
                        beanLicitacionProveedor.setIndSiParticipa(Constant.N);
                        this.licitacionProveedorRepository.save(beanLicitacionProveedor);
                    }
                }
            }

        }

    }

    public List<LicitacionAdjunto> cargarAdjuntosLicitacion
        (Licitacion licitacionGen, List<LicitacionAdjuntoBase64Dto> listAdjunto) throws Exception {

        System.out.println("");
        List<LicitacionAdjunto> lista = new ArrayList<>();
        if (listAdjunto != null) {
            if (!listAdjunto.isEmpty()) {
                //adjunto actual
                listAdjunto.forEach(item -> {

                    //Cargar adjunto onBase
                    try {
                        //Object o = cmisServiceCf.getDocumentoLicitacion(item, licitacionGen);

                        // Despues
                        if (StringUtils.isNotBlank(item.getBase64File()) && StringUtils.isNotBlank(item.getNombreDocumento())) {

                            MultipartFile file = BASE64DecodedMultipartFile.base64ToMultipart(item.getBase64File());
                            String folderMainId = cmisServiceCf.createFolder("licitaciones").getId();
                            String newFolder = licitacionGen.getNroLicitacionCero();
                            String folderId = cmisServiceCf.SubCreateFolder(folderMainId,newFolder).getId();
                            CmisFile cmisFileCF = cmisServiceCf.createDocumento("/"+folderId, file,"licitaciones",item.getFileExtencion());

                            //String folderId = cmisServiceCf.createFolder(DOCUMENT_FOLDER).getId();
                            //CmisFile cmisFileCF = cmisServiceCf.createDocumento(folderId, file, item.getNombreDocumento());
                            logger.debug("Archivo cargado al repositorio : " + cmisFileCF);

                            LicitacionAdjunto obj = new LicitacionAdjunto();
                            obj.setArchivoId(cmisFileCF.getId());
                            obj.setArchivoNombre(item.getNombreDocumento());
                            obj.setArchivoTipo(item.getFileExtencion());
                            obj.setDescripcion(item.getNombreDocumento());
                            obj.setRutaAdjunto(cmisFileCF.getUrl());
                            MtrTipoDocumento mtrTipoDocumento = this.mtrTipoDocumentoRepository.findByCodigoTipoDocumento(item.getCodigoDocumento());
                           obj.setMtrTipoDocumento(mtrTipoDocumento);
                            obj.setLicitacion(licitacionGen);
                            LicitacionAdjunto ladj = this.licitacionAdjuntoRepository.save(obj);
                            lista.add(obj);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        try {
                            //Object o = cmisServiceCf.getDocumentoLicitacion(item, licitacionGen);
                        } catch (Exception exception) {
                            exception.printStackTrace();
                            throw new PortalException("No fue posible cargar el adjunto :" + e.getMessage());
                        }
                        throw new PortalException("No fue posible cargar el adjunto :" + item.getNombreDocumento() + "." + item.getFileExtencion() + "--" + e.getMessage());
                    }
                });
            }
        } else {
            throw new PortalException("Debe Adjuntar un documento.");
        }

        // EAAR
        //List<LicitacionAdjunto> licitacionAdjuntos = licitacionAdjuntoRepository.findByLicitacionOrderByDescripcion(licitacionGen);
        //System.out.println("");

        //return licitacionAdjuntos;
        return lista;

    }

    public Integer verificarEsLicitacionMaterialLibre(Integer idLicitacion) {
        Integer exito = new Integer(1);
        Licitacion licitacion = this.licitacionRepository.getOne(idLicitacion);
        List<LicitacionDetalle> licitacionDetalleList = this.licitacionDetalleRepository.findByLicitacion(licitacion);
        if (licitacionDetalleList != null && licitacionDetalleList.size() > 0) {
            for (LicitacionDetalle beanDetalle : licitacionDetalleList) {
                if (Optional.ofNullable(beanDetalle.getBienServicio()).isPresent()) {
                    exito = 0;
                }
            }
        } else {
            exito = 0;
        }
        return exito;
    }

    @Autowired
    private EnviarAgradecimientoNoAdjudicadoNotificacion noAdjudicadoNotificacion;

    @Autowired
    private CcomparativoProveedorRepository ccomparativoProveedorRepository;

    public Licitacion getCerrarLicitacion(Map<String, Object> json) throws Exception {
        Integer idLicitacion = (Integer) json.get("idLicitacion");
        Licitacion licitacion = licitacionRepository.getById(idLicitacion);


        List<LicitacionProveedor> licitacionProveedorList = this.licitacionProveedorRepository.
            findByLicitacionSiParticipa(licitacion, Constant.S);
        for (LicitacionProveedor bean : licitacionProveedorList) {
            Proveedor proveedor = bean.getProveedor();
            List<LicitacionDetalle> licitacionDetalles =
                licitacionDetalleRepository.findByIdLicitacionOrdenadoProveedor(idLicitacion, proveedor.getIdProveedor().toString());

            if (licitacionDetalles.size() == 0) {
                this.noAdjudicadoNotificacion.enviar(
                    this.parametroMapper.getMailSetting(),
                    licitacion,
                    proveedor
                );
            }
        }
        licitacion.setEstadoLicitacion("AD");
        licitacionRepository.save(licitacion);

        return licitacion;
    }

}
