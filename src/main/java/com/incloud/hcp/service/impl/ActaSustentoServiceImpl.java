package com.incloud.hcp.service.impl;


import com.incloud.hcp.domain.*;
import com.incloud.hcp.dto.*;
import com.incloud.hcp.exception.PortalException;
import com.incloud.hcp.jco.emHes.service.JCOEmHesService;
import com.incloud.hcp.jco.ordenCompra.dto.OrdenCompraRPA;
import com.incloud.hcp.jco.ordenCompra.dto.OrdenCompraRequestRPA;
import com.incloud.hcp.jco.ordenCompra.dto.OrdenCompraResponseRPA;
import com.incloud.hcp.myibatis.mapper.ActaSustentoMapper;
import com.incloud.hcp.myibatis.mapper.ParametroMapper;
import com.incloud.hcp.repository.*;
import com.incloud.hcp.service.ActaSustentoService;
import com.incloud.hcp.service._framework.BaseServiceImpl;
import com.incloud.hcp.service.cmiscf.CmisBaseService;
import com.incloud.hcp.service.notificacion.ActaSustentoNotificacion;
import com.incloud.hcp.service.notificacion.MailSetting;
import com.incloud.hcp.util.DateUtils;
import com.incloud.hcp.util.Utils;
import com.incloud.hcp.util.constant.ActaSustentoConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


@Service
@Transactional(propagation= Propagation.REQUIRED, rollbackFor=Exception.class)
public class ActaSustentoServiceImpl extends BaseServiceImpl implements ActaSustentoService {

    private static Logger logger = LoggerFactory.getLogger(ActaSustentoServiceImpl.class);

    @Autowired
    private ParametroMapper parametroMapper;

    @Autowired
    private OrdenCompraRepository ordenCompraRepository;

    @Autowired
    private OrdenCompraDetalleRepository ordenCompraDetalleRepository;

    @Autowired
    private ActaSustentoRepository actaSustentoRepository;

    @Autowired
    private ProveedorRepository proveedorRepository;

    @Autowired
    private ActaSustentoDetalleRepository actaSustentoDetalleRepository;

    @Autowired
    private CmisBaseService cmisBaseService;

    @Autowired
    private ActaSustentoMapper actaSustentoMapper;

    @Autowired
    private ActaSustentoNotificacion actaSustentoNotificacion;

    @Autowired
    private ActaSustentoEvaluacionProveedorRepository actaSustentoEvaluacionProveedorRepository;

    @Autowired
    private CriterioRepository criterioRepository;

    @Autowired
    private DocumentoAceptacionRepository documentoAceptacionRepository;

    @Autowired
    private EstadoDocumentoAceptacionRepository estadoDocumentoAceptacionRepository;

    @Autowired
    private DocumentoAceptacionDetalleRepository documentoAceptacionDetalleRepository;

    @Autowired
    private JCOEmHesService jcoEmHesService;

    @Autowired
    private LicitacionDetalleRepository licitacionDetalleRepository;

    @Autowired
    private  UsuarioRepository usuarioRepository;

    public Object generarActa(GenerarActaDto bean) throws  Exception{

        //CONSULTAR OC
        OrdenCompra ordenCompra = ordenCompraRepository.getById(bean.getIdOrdenCompra());
        //CONSULTAR PROVEEDOR
        Proveedor proveedor = proveedorRepository.getProveedorByEmail(bean.getCorreoProveedor());
        Proveedor proveedorOc = null;


        //REGISTRAR ACTA SUSTENTO
        ActaSustento actaSustento = new ActaSustento();
        actaSustento.setProveedor(proveedor);
        if(proveedor == null){
            Usuario usuario = usuarioRepository.findByEmail(bean.getCorreoProveedor());
            if(usuario !=null){
                proveedorOc = proveedorRepository.getProveedorByRuc(ordenCompra.getProveedorRuc());
                actaSustento.setProveedor(proveedorOc);
                actaSustento.setUsuarioId(usuario.getIdUsuario());

            }
        }
        actaSustento.setOrdenCompra(ordenCompra);
        Integer annioActaSustento = new GregorianCalendar().get(Calendar.YEAR);
        Integer idAutogenerado = actaSustentoRepository.getMaxNroActaSustento() + 1;
        actaSustento.setNumeroActaSustento(Integer.toString(idAutogenerado));

        actaSustento.setFechaRegistro(DateUtils.obtenerFechaHoraActual());
        actaSustento.setEstado(1);//Creada
        actaSustento.setAnioActaSustento(Integer.toString(annioActaSustento));
        //Guardar acta sustento
        ActaSustento sustento =  actaSustentoRepository.save(actaSustento);

        //CREAR DOCUMENTOS PARA ACTA SUSTENTO

        bean.getActaSustentoBase64Dto().forEach(adjunto->{
            System.out.println("");
            try {
                cmisBaseService.crearDocumentoActaSustento(adjunto,actaSustento);
            } catch (Exception e) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                String sStackTrace = sw.toString();
                logger.info(sStackTrace);
                throw new PortalException("No fue posible cargar el Adjunto :"+ adjunto.getNombreDocumento() +"." +adjunto.getFileExtencion() +" Error => "+ sStackTrace);

            }
        });


        //CREAR ACTA SUSTENTO DETALLE
        bean.getOrdenCompraDetalle().forEach(item->{
            //Crear registros
            ActaSustentoDetalle actaSustentoDetalle = new ActaSustentoDetalle();
            actaSustentoDetalle.setActaSustento(sustento);
            actaSustentoDetalle.setOrdenCompraDetalle(item);
            actaSustentoDetalle.setEstado("AC");
            this.actaSustentoDetalleRepository.save(actaSustentoDetalle);
        });


        //ACtualizar ORden Compra DEtalle

        bean.getOrdenCompraDetalle().forEach(ocd->{
            System.out.println("");
            OrdenCompraDetalle detalleoc = ordenCompraDetalleRepository.getById(ocd.getId());
            detalleoc.setIndSeleccionado("X");
            this.ordenCompraDetalleRepository.save(detalleoc);
        });

        Usuario usuariocompras = usuarioRepository.findByCodigoSap(sustento.getOrdenCompra().getCompradorUsuarioSap());
        if(proveedor !=null){
            if(usuariocompras!= null){
                this.actaSustentoNotificacion.enviar(this.parametroMapper.getMailSetting(),proveedor,sustento,bean.getOrdenCompraDetalle(),usuariocompras);
            }
        }
        if(proveedorOc !=null){
            if(usuariocompras!=null){
                this.actaSustentoNotificacion.enviar(this.parametroMapper.getMailSetting(),proveedorOc,sustento,bean.getOrdenCompraDetalle(),usuariocompras);
            }
        }
        return sustento;
    }




    public List<ActaSustento> getListaActaByFiltroPaginado(Map<String, Object> json) {

        String numeroActaSustentoString = (String) json.get("numeroActa");
        String idProveedor = (String)json.get("idProveedor");
        String nombreProveedor = (String)json.get("nombreProveedor");
        Integer nroRegistros = (Integer)json.get("nroRegistros");
        Integer paginaMostrar = (Integer)json.get("paginaMostrar");
        Integer annioActa = null;
        paginaMostrar = new Integer((paginaMostrar.intValue() - 1) * nroRegistros);


        List<ActaSustento> lista = this.actaSustentoMapper.getListActaSustentoByFiltroPaginado(
                numeroActaSustentoString,
                annioActa,
                idProveedor,
                nombreProveedor,
                nroRegistros,
                paginaMostrar);

        lista.forEach(item->{
            Proveedor proveedor = this.proveedorRepository.getById(item.getProveedor().getIdProveedor());
            item.setProveedor(proveedor);
            OrdenCompra ordenCompra = this.ordenCompraRepository.getById(item.getOrdenCompra().getId());
            item.setOrdenCompra(ordenCompra);
        });



        return lista;
    }

    public Object evaluarActa(Integer idActaSustento, String estado, ActaSustentoRechazarDto bean) throws  Exception{
        ResponseGeneralDto responseGeneralDto = new ResponseGeneralDto();
        if(!estado.isEmpty()){
            //obtener el acta sustento
            ActaSustento actaSustento = this.actaSustentoRepository.getById(idActaSustento);
            //Evaluar Acta
            if(estado.equals("APROBAR")){
                actaSustento.setEstado(ActaSustentoConstant.ESTADO_APROBADA); //Aprobado
                actaSustentoNotificacion.enviarActaPreAprobar(parametroMapper.getMailSetting(), actaSustento.getProveedor(), actaSustento,bean.getMotivo());
            }else{
                    actaSustento.setMotivo(bean.getMotivo());//motivo de rechazo
                    actaSustento.setEstado(ActaSustentoConstant.ESTADO_RECHAZADA); //Rechazada


                    List<ActaSustentoDetalle> actaSustentoDetalles = actaSustentoDetalleRepository.findbyIdActaSustento(idActaSustento);
                    OrdenCompra ordenCompra = actaSustento.getOrdenCompra();


                    ordenCompra.setIdEstadoOrdenCompra(1);//activa
                    ordenCompraRepository.save(ordenCompra);

                    actaSustentoDetalles.forEach(actaDetalle->{
                        OrdenCompraDetalle ordenCompraDetalle = actaDetalle.getOrdenCompraDetalle();
                        ordenCompraDetalle.setIndSeleccionado("");
                        ordenCompraDetalleRepository.save(ordenCompraDetalle);
                    });

                    actaSustentoNotificacion.enviarActaRechazada( parametroMapper.getMailSetting(), actaSustento.getProveedor(), actaSustento,bean.getMotivo());
            }

           this.actaSustentoRepository.save(actaSustento);
            responseGeneralDto.setMensaje("proceso realizado correctamente.");
            responseGeneralDto.setStatus("1");

        }else{
            throw new Exception("DEBE INGRESAR ESTADO");
        }

        return responseGeneralDto;
    }


    public List<ActaSustento> getListaActaByFiltroPaginadoProveedor(String email,Map<String, Object> json) {
        List<ActaSustento> actaSustentoList = null;
            String numeroActaSustentoString = (String) json.get("numeroActa");
            String idProveedor = (String)json.get("idProveedor");
            String nombreProveedor = (String)json.get("nombreProveedor");
            Integer nroRegistros = (Integer)json.get("nroRegistros");
            Integer paginaMostrar = (Integer)json.get("paginaMostrar");
            Integer annioActa = null;
            paginaMostrar = new Integer((paginaMostrar.intValue() - 1) * nroRegistros);
        if(!email.isEmpty()){
            actaSustentoList = this.actaSustentoMapper.getListActaSustentoByFiltroPaginadoProveedor(
                    numeroActaSustentoString,
                    annioActa,
                    idProveedor,
                    nombreProveedor,
                    nroRegistros,
                    paginaMostrar
            );
            //Proveedor proveedor = this.proveedorRepository.getProveedorByEmail(email);
            //actaSustentoList  = this.actaSustentoRepository.getListActaSustentoByFiltroPaginadoProveedor();

        }else{
            actaSustentoList = this.actaSustentoMapper.getListActaSustentoByFiltroPaginadoProveedor(
                    numeroActaSustentoString,
                    annioActa,
                    idProveedor,
                    nombreProveedor,
                    nroRegistros,
                    paginaMostrar
            );
            //actaSustentoList  = this.actaSustentoRepository.getListActaSustentoByFiltroPaginadoProveedor();

            //Proveedor proveedor = this.proveedorRepository.getProveedorByEmail(email);

            //actaSustentoList  = this.actaSustentoRepository.getListActaSustentoByFiltroPaginadoProveedor(1);

        }
        return  actaSustentoList;
    }

    public List<ActaSustento> getListaActaByFiltroPaginadoProveedorRechazada(Map<String, Object> json) {
        List<ActaSustento> actaSustentoList = null;
        Integer numeroActaSustentoString = (Integer) json.get("numeroActa");
        String idProveedor = (String)json.get("idProveedor");
        String email = (String)json.get("email");
        String nombreProveedor = (String)json.get("nombreProveedor");
        Integer nroRegistros = (Integer)json.get("nroRegistros");
        Integer paginaMostrar = (Integer)json.get("paginaMostrar");
        Integer annioActa = null;
        paginaMostrar = new Integer((paginaMostrar.intValue() - 1) * nroRegistros);
        if(!email.isEmpty()){
            Proveedor proveedor = proveedorRepository.getProveedorByEmail(email);
            if(proveedor == null){
                throw  new PortalException("Proveedor no encontrado");
            }
            actaSustentoList = this.actaSustentoMapper.getListActaSustentoByFiltroPaginadoProveedorRechazada(
                    numeroActaSustentoString,
                    annioActa,
                    String.valueOf(proveedor.getIdProveedor()),
                    nombreProveedor,
                    nroRegistros,
                    paginaMostrar
            );

        }
        return  actaSustentoList;
    }


    public ActaSustento evaluarActaSustentoProveedor(ActaSustentoEvaluacionDto bean) throws  Exception {

        //EVALUAR EL ACTA DE SUSTENTO Y CREAR REGISTRO
        ActaSustento actaSustento = this.actaSustentoRepository.getById(bean.getIdActaSustento());

        //REGISTRO PARA CRITERIO MATERIAL
            if(!bean.getCriterioM().getCriterio().isEmpty()){
                //VALIDAR SI EXISTE ALGUNA EVALUACION PARA EL ACTA
                CriterioEvaluacionDto criterioEvaluacionDto = bean.getCriterioM();
                criterioEvaluacionDto.getCriterio().forEach(item -> {
                    //validar si existe idEvaluacionProveedor se actualiza
                    if(item.getIdActaEvaluacionProveedor() != null){
                        ActaSustentoEvaluacionProveedor actaSustentoEvaluacionProveedor =
                                actaSustentoEvaluacionProveedorRepository.getById(item.getIdActaEvaluacionProveedor());
                        actaSustentoEvaluacionProveedor.setComentario(criterioEvaluacionDto.getComentario());
                        actaSustentoEvaluacionProveedor.setTotalIndividual(item.getTotal());
                        this.actaSustentoEvaluacionProveedorRepository.save(actaSustentoEvaluacionProveedor);
                    }else{
                        //Consultar el criterio
                        Criterio criterio = this.criterioRepository.getById(item.getIdCriterio());
                        //Setear valores para insertar evaluacion
                        ActaSustentoEvaluacionProveedor actaSustentoEvaluacionProveedor = new ActaSustentoEvaluacionProveedor();
                        actaSustentoEvaluacionProveedor.setActaSustento(actaSustento);
                        actaSustentoEvaluacionProveedor.setTotalIndividual(item.getTotal());
                        actaSustentoEvaluacionProveedor.setComentario(criterioEvaluacionDto.getComentario());
                        actaSustentoEvaluacionProveedor.setCriterio(criterio);
                        this.actaSustentoEvaluacionProveedorRepository.save(actaSustentoEvaluacionProveedor);

                    }
                });
            }
            //REGISTRO PARA CRITERIO SERVICIO
            if(!bean.getCriterioS().getCriterio().isEmpty()){
                //VALIDAR SI EXISTE ALGUNA EVALUACION PARA EL ACTA
                CriterioEvaluacionDto criterioEvaluacionDto = bean.getCriterioS();
                criterioEvaluacionDto.getCriterio().forEach(item -> {
                    //validar si existe idEvaluacionProveedor se actualiza
                    if(item.getIdActaEvaluacionProveedor() != null){
                        ActaSustentoEvaluacionProveedor actaSustentoEvaluacionProveedor =
                                actaSustentoEvaluacionProveedorRepository.getById(item.getIdActaEvaluacionProveedor());
                        actaSustentoEvaluacionProveedor.setTotalIndividual(item.getTotal());
                        actaSustentoEvaluacionProveedor.setComentario(criterioEvaluacionDto.getComentario());
                        this.actaSustentoEvaluacionProveedorRepository.save(actaSustentoEvaluacionProveedor);
                    }else{
                        //Consultar el criterio
                        Criterio criterio = this.criterioRepository.getById(item.getIdCriterio());
                        //Setear valores para insertar evaluacion
                        ActaSustentoEvaluacionProveedor actaSustentoEvaluacionProveedor = new ActaSustentoEvaluacionProveedor();
                        actaSustentoEvaluacionProveedor.setActaSustento(actaSustento);
                        actaSustentoEvaluacionProveedor.setTotalIndividual(item.getTotal());
                        actaSustentoEvaluacionProveedor.setComentario(criterioEvaluacionDto.getComentario());
                        actaSustentoEvaluacionProveedor.setCriterio(criterio);
                        this.actaSustentoEvaluacionProveedorRepository.save(actaSustentoEvaluacionProveedor);

                    }
                });
            }
            //ESTADO DOCUMENTO DE ACEPTACION
            EstadoDocumentoAceptacion estadoDocumentoAceptacion =
                    this.estadoDocumentoAceptacionRepository.getById(1);


            //INSERTAR VALORES PARA LOS FORMULARIO EM
            ActaSustentoFormularioEvaluacionDto formularioEm =  bean.getFormularioEm();
            DocumentoAceptacion documentoAceptacionEm = new DocumentoAceptacion();
            if(formularioEm.getIdDocumentoAceptacion() != null){
                DocumentoAceptacion documentoAceptacion  = documentoAceptacionRepository.getById(formularioEm.getIdDocumentoAceptacion());
                documentoAceptacion.setFechaDocumento(formularioEm.getFechaDocumento());
                documentoAceptacion.setFechaContabilizacion(formularioEm.getFechaContabilidad());
                documentoAceptacion.setNotaEntrega(formularioEm.getNotaEntrega());
                documentoAceptacion.setTextoCabecera(formularioEm.getTextoCabecera());
                documentoAceptacion.setIdTipoDocumentoAceptacion(1); //MATERIALES
                documentoAceptacion.setEstadoDocumentoAceptacion(estadoDocumentoAceptacion);
                documentoAceptacion.setCodigoMoneda(actaSustento.getOrdenCompra().getCodigoMondeda());
                documentoAceptacionEm = documentoAceptacionRepository.save(documentoAceptacion);
            }else{
                if(!formularioEm.getActaSustentoDetalle().isEmpty()){
                    DocumentoAceptacion documentoAceptacion  = new DocumentoAceptacion();
                    documentoAceptacion.setFechaDocumento(formularioEm.getFechaDocumento());
                    documentoAceptacion.setFechaContabilizacion(formularioEm.getFechaContabilidad());
                    documentoAceptacion.setTextoCabecera(formularioEm.getTextoCabecera());
                    documentoAceptacion.setNumeroOrdenCompra(actaSustento.getOrdenCompra().getNumeroOrdenCompra());
                    documentoAceptacion.setNotaEntrega(formularioEm.getNotaEntrega());
                    documentoAceptacion.setFechaEmision(new Date());
                    documentoAceptacion.setActaSustento(actaSustento);
                    documentoAceptacion.setEstadoDocumentoAceptacion(estadoDocumentoAceptacion); //Activa
                    documentoAceptacion.setProveedorRazonSocial(actaSustento.getProveedor().getRazonSocial());
                    documentoAceptacion.setProveedorRuc(actaSustento.getProveedor().getRuc());
                    documentoAceptacion.setIdEstadoDocumentoAceptacion(1);
                    documentoAceptacion.setIdTipoDocumentoAceptacion(1); // ? MATERIALES
                    documentoAceptacion.setNumeroDocumentoAceptacion(actaSustento.getNumeroActaSustento()); //numero acta de sustento
                    documentoAceptacion.setCodigoMoneda(actaSustento.getOrdenCompra().getCodigoMondeda());
                    try {
                        documentoAceptacionEm= this.documentoAceptacionRepository.save(documentoAceptacion);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

            }

            //INSERTAR VALORES PARA LOS FORMULARIO HES
            ActaSustentoFormularioEvaluacionDto formularioHes =  bean.getFormularioHes();
            DocumentoAceptacion documentoAceptacionHes = new DocumentoAceptacion();

            if(formularioHes.getIdDocumentoAceptacion() != null){
                DocumentoAceptacion documentoAceptacion  = documentoAceptacionRepository.getById(formularioHes.getIdDocumentoAceptacion());
                documentoAceptacion.setFechaDocumento(formularioHes.getFechaDocumento());
                documentoAceptacion.setFechaContabilizacion(formularioHes.getFechaContabilidad());
                documentoAceptacion.setReferencia(formularioHes.getNotaEntrega());
                documentoAceptacion.setTextoCabecera(formularioHes.getTextoCabecera());
                documentoAceptacion.setIdTipoDocumentoAceptacion(2); //SERVICIOS
                documentoAceptacion.setEstadoDocumentoAceptacion(estadoDocumentoAceptacion);
                documentoAceptacion.setCodigoMoneda(actaSustento.getOrdenCompra().getCodigoMondeda());
                documentoAceptacionHes = documentoAceptacionRepository.save(documentoAceptacion);
            }else{
                if(!formularioHes.getActaSustentoDetalle().isEmpty()){
                    DocumentoAceptacion documentoAceptacion  = new DocumentoAceptacion();
                    documentoAceptacion.setFechaDocumento(formularioHes.getFechaDocumento());
                    documentoAceptacion.setFechaContabilizacion(formularioHes.getFechaContabilidad());
                    documentoAceptacion.setTextoCabecera(formularioHes.getTextoCabecera());
                    documentoAceptacion.setReferencia(formularioHes.getNotaEntrega());
                    documentoAceptacion.setNumeroOrdenCompra(actaSustento.getOrdenCompra().getNumeroOrdenCompra());
                    documentoAceptacion.setActaSustento(actaSustento);
                    documentoAceptacion.setFechaEmision(new Date());
                    documentoAceptacion.setEstadoDocumentoAceptacion(estadoDocumentoAceptacion); //Activa
                    documentoAceptacion.setProveedorRazonSocial(actaSustento.getProveedor().getRazonSocial());
                    documentoAceptacion.setProveedorRuc(actaSustento.getProveedor().getRuc());
                    documentoAceptacion.setIdEstadoDocumentoAceptacion(1);
                    documentoAceptacion.setIdTipoDocumentoAceptacion(2); // ? SERVICIOS
                    documentoAceptacion.setNumeroDocumentoAceptacion(actaSustento.getNumeroActaSustento()); //numero acta de sustento
                    documentoAceptacion.setCodigoMoneda(actaSustento.getOrdenCompra().getCodigoMondeda());
                    try {
                        documentoAceptacionHes =  this.documentoAceptacionRepository.save(documentoAceptacion);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

            }

            // MODIFICAR O CREAR EL ITEM DOCUMENTO ACEPTACION DETALLE EM
            AtomicInteger indexEM = new AtomicInteger();
            AtomicInteger indexHES = new AtomicInteger();
            List<ActaSustentoDetalle> actaSustentoDetalleEm = bean.getFormularioEm().getActaSustentoDetalle();
            DocumentoAceptacion finalDocumentoAceptacionEm = documentoAceptacionEm;
            actaSustentoDetalleEm.forEach(item->{
                System.out.println("");
                if(item.getIdDocumentoDetalle() != null){
                    //ACTUALIZAR ITEM DETALLE  EM
                    //CREAR ITEM DETALLE EM
                    BigDecimal cantidadIngresada =  item.getOrdenCompraDetalle().getCantidad();
                    OrdenCompraDetalle ordenCompraDetalle = ordenCompraDetalleRepository.getById(item.getOrdenCompraDetalle().getId());
                    BigDecimal cantidadOcDetalle = ordenCompraDetalle.getCantidad();
                    DocumentoAceptacionDetalle documentoAceptacionDetalle = this.documentoAceptacionDetalleRepository.getById(item.getIdDocumentoDetalle());
                    if(cantidadIngresada.intValue() < cantidadOcDetalle.intValue() ){
                        //REGRESAR A GENERAR ACTA PARCIAL
                        //Asignar nueva cantidad
                        BigDecimal nuevaCantidad = ordenCompraDetalle.getCantidad().subtract(cantidadIngresada);
                        ordenCompraDetalle.setIndSeleccionado("");
                        ordenCompraDetalle.setCantidad(nuevaCantidad);
                        //nuevo precio unitario
                        BigDecimal precio = ordenCompraDetalle.getPrecioTotal().subtract(item.getOrdenCompraDetalle().getPrecioTotal());
                        ordenCompraDetalle.setPrecioTotal(precio);
                        ordenCompraDetalleRepository.save(ordenCompraDetalle);
                        documentoAceptacionDetalle.setCantidadPendiente(nuevaCantidad);
                    }else{
                        ordenCompraDetalle.setCantidad(ordenCompraDetalle.getCantidadOriginal());
                        ordenCompraDetalle.setPrecioTotal(ordenCompraDetalle.getPrecioTotalOriginal());
                        ordenCompraDetalleRepository.save(ordenCompraDetalle);
                    }
                    documentoAceptacionDetalle.setPosicionOrdenCompra(item.getOrdenCompraDetalle().getPosicionOc());
                    documentoAceptacionDetalle.setCodigoSapBienServicio(item.getOrdenCompraDetalle().getCodigoSapBienServicio());
                    documentoAceptacionDetalle.setDescripcionBienServicio(item.getOrdenCompraDetalle().getDescripcionBienServicio());
                    documentoAceptacionDetalle.setDescripcionUnidadMedida(item.getOrdenCompraDetalle().getDescripcionUnidadMedida());
                    documentoAceptacionDetalle.setDocumentoAceptacion(finalDocumentoAceptacionEm);
                    documentoAceptacionDetalle.setIdEstadoDocumentoAceptacionDetalle(1);
                    documentoAceptacionDetalle.setIndicadorImpuesto(item.getOrdenCompraDetalle().getIndicadorImpuesto());
                    documentoAceptacionDetalle.setKardex(item.getOrdenCompraDetalle().getKardex());
                    documentoAceptacionDetalle.setNumeroOrdenCompra(actaSustento.getOrdenCompra().getNumeroOrdenCompra());
                    documentoAceptacionDetalle.setUnidadMedida(item.getOrdenCompraDetalle().getUnidadMedidaBienServicio());
                    documentoAceptacionDetalle.setValorRecibido(item.getOrdenCompraDetalle().getCantidad());
                    documentoAceptacionDetalle.setPrecioUnitario(item.getOrdenCompraDetalle().getPrecioUnitario());
                    documentoAceptacionDetalle.setIdDocumentoAceptacion(finalDocumentoAceptacionEm.getId());
                    documentoAceptacionDetalle.setNumeroItem((indexEM.getAndIncrement() + 1));
                    documentoAceptacionDetalleRepository.save(documentoAceptacionDetalle);

                }else {
                    //CREAR ITEM DETALLE EM
                    BigDecimal cantidadIngresada =  item.getOrdenCompraDetalle().getCantidad();
                    OrdenCompraDetalle ordenCompraDetalle = ordenCompraDetalleRepository.getById(item.getOrdenCompraDetalle().getId());
                    BigDecimal cantidadOcDetalle = ordenCompraDetalle.getCantidad();

                    DocumentoAceptacionDetalle documentoAceptacionDetalle = new DocumentoAceptacionDetalle();
                    if(cantidadIngresada.intValue() < cantidadOcDetalle.intValue() ){
                        //REGRESAR A GENERAR ACTA PARCIAL
                        //Asignar nueva cantidad
                        BigDecimal nuevaCantidad = ordenCompraDetalle.getCantidad().subtract(cantidadIngresada);
                        ordenCompraDetalle.setIndSeleccionado("");
                        ordenCompraDetalle.setCantidad(nuevaCantidad);
                        //nuevo precio unitario
                        BigDecimal precio = ordenCompraDetalle.getPrecioTotal().subtract(item.getOrdenCompraDetalle().getPrecioTotal());
                        ordenCompraDetalle.setPrecioTotal(precio);
                        ordenCompraDetalleRepository.save(ordenCompraDetalle);
                        documentoAceptacionDetalle.setCantidadPendiente(nuevaCantidad);
                    }else{
                        ordenCompraDetalle.setCantidad(ordenCompraDetalle.getCantidadOriginal());
                        ordenCompraDetalleRepository.save(ordenCompraDetalle);
                    }
                    documentoAceptacionDetalle.setNumeroItem((indexEM.getAndIncrement() + 1));
                    documentoAceptacionDetalle.setPosicionOrdenCompra(item.getOrdenCompraDetalle().getPosicionOc());
                    documentoAceptacionDetalle.setCantidadAceptadaCliente(item.getOrdenCompraDetalle().getCantidad());
                    documentoAceptacionDetalle.setCodigoSapBienServicio(item.getOrdenCompraDetalle().getCodigoSapBienServicio());
                    documentoAceptacionDetalle.setDescripcionBienServicio(item.getOrdenCompraDetalle().getDescripcionBienServicio());
                    documentoAceptacionDetalle.setDescripcionUnidadMedida(item.getOrdenCompraDetalle().getDescripcionUnidadMedida());
                    documentoAceptacionDetalle.setDocumentoAceptacion(finalDocumentoAceptacionEm);
                    documentoAceptacionDetalle.setIdEstadoDocumentoAceptacionDetalle(1);
                    documentoAceptacionDetalle.setIndicadorImpuesto(item.getOrdenCompraDetalle().getIndicadorImpuesto());
                    documentoAceptacionDetalle.setKardex(item.getOrdenCompraDetalle().getKardex());
                    documentoAceptacionDetalle.setNumeroOrdenCompra(actaSustento.getOrdenCompra().getNumeroOrdenCompra());
                    documentoAceptacionDetalle.setUnidadMedida(item.getOrdenCompraDetalle().getUnidadMedidaBienServicio());
                    documentoAceptacionDetalle.setValorRecibido(item.getOrdenCompraDetalle().getCantidad());
                    documentoAceptacionDetalle.setPrecioUnitario(item.getOrdenCompraDetalle().getPrecioUnitario());
                    documentoAceptacionDetalle.setIdDocumentoAceptacion(finalDocumentoAceptacionEm.getId());
                    documentoAceptacionDetalle.setActaSustentoDetalle(item);//acta sustento detalle
                    documentoAceptacionDetalleRepository.save(documentoAceptacionDetalle);
                }
            });

            // MODIFICAR O CREAR EL ITEM DOCUMENTO ACEPTACION DETALLE HES
            List<ActaSustentoDetalle> actaSustentoDetalleHes = bean.getFormularioHes().getActaSustentoDetalle();
            DocumentoAceptacion finalDocumentoAceptacionHes = documentoAceptacionHes;
            actaSustentoDetalleHes.forEach(item->{
                System.out.println("");
                if(item.getIdDocumentoDetalle() != null){
                    //ACTUALIZAR ITEM DETALLE  HES
                    DocumentoAceptacionDetalle documentoAceptacionDetalle = this.documentoAceptacionDetalleRepository.getById(item.getIdDocumentoDetalle());
                    documentoAceptacionDetalle.setNumeroItem((indexHES.getAndIncrement() + 1));
                    documentoAceptacionDetalle.setPosicionOrdenCompra(item.getOrdenCompraDetalle().getPosicionOc());
                    documentoAceptacionDetalle.setCodigoSapBienServicio(item.getOrdenCompraDetalle().getCodigoSapBienServicio());
                    documentoAceptacionDetalle.setDescripcionBienServicio(item.getOrdenCompraDetalle().getDescripcionBienServicio());
                    documentoAceptacionDetalle.setDescripcionUnidadMedida(item.getOrdenCompraDetalle().getDescripcionUnidadMedida());
                    documentoAceptacionDetalle.setDocumentoAceptacion(finalDocumentoAceptacionHes);
                    documentoAceptacionDetalle.setIdEstadoDocumentoAceptacionDetalle(1);
                    documentoAceptacionDetalle.setIndicadorImpuesto(item.getOrdenCompraDetalle().getIndicadorImpuesto());
                    documentoAceptacionDetalle.setKardex(item.getOrdenCompraDetalle().getKardex());
                    documentoAceptacionDetalle.setNumeroOrdenCompra(actaSustento.getOrdenCompra().getNumeroOrdenCompra());
                    documentoAceptacionDetalle.setUnidadMedida(item.getOrdenCompraDetalle().getUnidadMedidaBienServicio());
                    documentoAceptacionDetalle.setValorRecibido(item.getOrdenCompraDetalle().getCantidad());
                    documentoAceptacionDetalle.setPrecioUnitario(item.getOrdenCompraDetalle().getPrecioUnitario());
                    documentoAceptacionDetalle.setIdDocumentoAceptacion(finalDocumentoAceptacionHes.getId());
                    documentoAceptacionDetalleRepository.save(documentoAceptacionDetalle);

                }else {
                    //CREAR ITEM DETALLE HES
                    DocumentoAceptacionDetalle documentoAceptacionDetalle = new DocumentoAceptacionDetalle();
                    documentoAceptacionDetalle.setNumeroItem((indexHES.getAndIncrement() + 1));
                    documentoAceptacionDetalle.setPosicionOrdenCompra(item.getOrdenCompraDetalle().getPosicionOc());
                    documentoAceptacionDetalle.setCodigoSapBienServicio(item.getOrdenCompraDetalle().getCodigoSapBienServicio());
                    documentoAceptacionDetalle.setDescripcionBienServicio(item.getOrdenCompraDetalle().getDescripcionBienServicio());
                    documentoAceptacionDetalle.setDescripcionUnidadMedida(item.getOrdenCompraDetalle().getDescripcionUnidadMedida());
                    documentoAceptacionDetalle.setDocumentoAceptacion(finalDocumentoAceptacionHes);
                    documentoAceptacionDetalle.setIdEstadoDocumentoAceptacionDetalle(1);
                    documentoAceptacionDetalle.setCantidadAceptadaCliente(item.getOrdenCompraDetalle().getCantidad());
                    documentoAceptacionDetalle.setIndicadorImpuesto(item.getOrdenCompraDetalle().getIndicadorImpuesto());
                    documentoAceptacionDetalle.setKardex(item.getOrdenCompraDetalle().getKardex());
                    documentoAceptacionDetalle.setNumeroOrdenCompra(actaSustento.getOrdenCompra().getNumeroOrdenCompra());
                    documentoAceptacionDetalle.setUnidadMedida(item.getOrdenCompraDetalle().getUnidadMedidaBienServicio());
                    documentoAceptacionDetalle.setValorRecibido(item.getOrdenCompraDetalle().getCantidad());
                    documentoAceptacionDetalle.setPrecioUnitario(item.getOrdenCompraDetalle().getPrecioUnitario());
                    documentoAceptacionDetalle.setIdDocumentoAceptacion(finalDocumentoAceptacionHes.getId());
                    documentoAceptacionDetalle.setActaSustentoDetalle(item);//acta sustento detalle
                    documentoAceptacionDetalleRepository.save(documentoAceptacionDetalle);
                }
            });

            //DARA PARA ENVIAR AL JCO SAP
            if(documentoAceptacionEm.getId() != null){
                DocumentoAceptacion documentoAceptacionEmFinal =
                        jcoEmHesService.crearEm(documentoAceptacionEm);
                documentoAceptacionEmFinal.setFechaAceptacion(DateUtils.obtenerFechaActual());
                documentoAceptacionRepository.save(documentoAceptacionEmFinal);
                actaSustento.setCodigoSapEm(documentoAceptacionEm.getNumeroDocumentoAceptacion());
                documentoAceptacionDetalleRepository
                        .insertNumeroAceptacion(documentoAceptacionEm.getNumeroDocumentoAceptacion(),documentoAceptacionEm.getId());

                actaSustentoRepository.save(actaSustento);
            }
            if(documentoAceptacionHes.getId() != null){
                DocumentoAceptacion documentoAceptacionHesFinal =
                        jcoEmHesService.crearHes(documentoAceptacionHes);
                documentoAceptacionHesFinal.setFechaAceptacion(DateUtils.obtenerFechaActual());
                documentoAceptacionRepository.save(documentoAceptacionHesFinal);
                actaSustento.setCodigoSapHes(documentoAceptacionHes.getNumeroDocumentoAceptacion());

                documentoAceptacionDetalleRepository
                        .insertNumeroAceptacion(documentoAceptacionHes.getNumeroDocumentoAceptacion(),documentoAceptacionHes.getId());
                actaSustentoRepository.save(actaSustento);
            }
            //ACTUALIZAR LA ACTA DE SUSTENTO A EVALUADA POR PROVEEDOR
            actaSustento.setEstado(ActaSustentoConstant.ESTADO_EVALUADA_POR_PROVEEDOR);
            actaSustentoRepository.save(actaSustento);


        if(actaSustento.getIdActaSustento() != null){
            //CREAR FICHA SUSTENTO
            AdjuntoActaSustentoBase64Dto adjuntoActaSustentoBase64Dto = new AdjuntoActaSustentoBase64Dto();
            adjuntoActaSustentoBase64Dto.setBase64File("");
            adjuntoActaSustentoBase64Dto.setFileExtencion("");
            boolean es = false;
            for (int i = 0; i < 2; i++) {
                try{
                    this.cmisBaseService.crearDocumentoFichaActaSustento(adjuntoActaSustentoBase64Dto,actaSustento);
                    es = true;
                }catch (Exception e){
                    Thread.sleep(500);
                    e.printStackTrace();
                    System.out.println("continuar ejecucion....");
                    continue;
                }
                if(es){
                    break;
                }
            }
        }

        return  actaSustento;
    }


    public ActaSustento anularActaSustentoProveedor(Integer idActaSustento) throws  Exception {

        ActaSustento actaSustento =  actaSustentoRepository.getById(idActaSustento);
        actaSustento.setEstado(ActaSustentoConstant.ESTADO_APROBADA);

        Optional<DocumentoAceptacion> documentoAceptacionEm = documentoAceptacionRepository.findByNumeroDocumentoAceptacion(actaSustento.getCodigoSapEm());
        if(!documentoAceptacionEm.isPresent()){
            Optional<DocumentoAceptacion> documentoAceptacionHes = documentoAceptacionRepository.findByNumeroDocumentoAceptacion(actaSustento.getCodigoSapHes());
            if(documentoAceptacionHes.isPresent()){
                documentoAceptacionHes.get().setIdEstadoDocumentoAceptacion(5);//anulada
                documentoAceptacionRepository.save(documentoAceptacionHes.get());
            }
        }else {
            documentoAceptacionEm.get().setIdEstadoDocumentoAceptacion(5);//anulada
            documentoAceptacionRepository.save(documentoAceptacionEm.get());
        }


        try {
//            List<ActaSustentoDetalle> actaSustentoDetalle = actaSustentoDetalleRepository.findbyIdActaSustento(idActaSustento);
//            if(actaSustentoDetalle.size() > 0){
//                for (int i = 0; i < actaSustentoDetalle.size(); i++) {
//                   ActaSustentoDetalle actaSustentoDetalle1= actaSustentoDetalle.get(i);
//                   if(actaSustentoDetalle1.getOrdenCompraDetalle().getTipoPosicion().equals("M")){
//                       DocumentoAceptacionDetalle documentoAceptacionDetalle = documentoAceptacionDetalleRepository.getByIdActaSustentoDetalle(actaSustentoDetalle1.getIdActaSustentoDetalle());
//                        OrdenCompraDetalle ordenCompraDetalle = actaSustentoDetalle1.getOrdenCompraDetalle();
//
//                        BigDecimal cantidadOcDetalle = ordenCompraDetalle.getCantidad();
//                        BigDecimal cantidadActaDetalle =documentoAceptacionDetalle.getCantidadAceptadaCliente();
//
//                        BigDecimal cantidadNueva = cantidadOcDetalle.subtract(cantidadActaDetalle);
//                        ordenCompraDetalle.setCantidad(cantidadNueva);
//                        ordenCompraDetalleRepository.save(ordenCompraDetalle);
//                   }
//                }
//            }
            actaSustentoRepository.save(actaSustento);
        }catch (Exception e){
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }

        return actaSustento;
    }

    public EntradaMercanciaRequestRPA getEntradaMercanciaRPA() throws Exception {
        EntradaMercanciaRequestRPA entradaMercanciaRequestRPA = new EntradaMercanciaRequestRPA();

        List<EntradaMercanciaRPA> entradaMercanciaRPAList = new ArrayList<>();

        EntradaMercanciaRPA entradaMercanciaRPA = new EntradaMercanciaRPA();
        entradaMercanciaRPA.setTexto("H|4590036039|0019|20210917|20210917|TEXTO CABECERA|TEXTO ENTREGA\u2028I|000000000820000964|6801|UND|4590036039|00010|X");
        entradaMercanciaRPA.setIdentificador("1");
        entradaMercanciaRPAList.add(entradaMercanciaRPA);

        EntradaMercanciaRPA entradaMercanciaRPA1 = new EntradaMercanciaRPA();
        entradaMercanciaRPA1.setTexto("H|4590036040|0019|20210917|20210917|TEXTO CABECERA|TEXTO ENTREGA\u2028I|000000000820000964|6801|UND|4590036040|00010|X");
        entradaMercanciaRPA1.setIdentificador("1");
        entradaMercanciaRPAList.add(entradaMercanciaRPA1);

        EntradaMercanciaRPA entradaMercanciaRPA2 = new EntradaMercanciaRPA();
        entradaMercanciaRPA2.setTexto("H|4590036041|0019|20210917|20210917|TEXTO CABECERA|TEXTO ENTREGA\u2028I|000000000820000964|6801|UND|4590036041|00010|X");
        entradaMercanciaRPA2.setIdentificador("1");
        entradaMercanciaRPAList.add(entradaMercanciaRPA2);

        EntradaMercanciaRPA entradaMercanciaRPA3 = new EntradaMercanciaRPA();
        entradaMercanciaRPA3.setTexto("H|4590036036|0019|20210917|20210917|TEXTO CABECERA|TEXTO ENTREGA\u2028I|000000000820000964|6801|UND|4590036042|00010|X");
        entradaMercanciaRPA3.setIdentificador("1");
        entradaMercanciaRPAList.add(entradaMercanciaRPA3);

        EntradaMercanciaRPA entradaMercanciaRPA4 = new EntradaMercanciaRPA();
        entradaMercanciaRPA4.setTexto("H|4590036036|0019|20210917|20210917|TEXTO CABECERA|TEXTO ENTREGA\u2028I|000000000820000964|6801|UND|4590036043|00010|X");
        entradaMercanciaRPA4.setIdentificador("1");
        entradaMercanciaRPAList.add(entradaMercanciaRPA4);

        EntradaMercanciaRPA entradaMercanciaRPA5 = new EntradaMercanciaRPA();
        entradaMercanciaRPA5.setTexto("H|4590036044|0019|20210917|20210917|TEXTO CABECERA|TEXTO ENTREGA\u2028I|000000000820000964|6801|UND|4590036044|00010|X");
        entradaMercanciaRPA5.setIdentificador("1");
        entradaMercanciaRPAList.add(entradaMercanciaRPA5);

        EntradaMercanciaRPA entradaMercanciaRPA6 = new EntradaMercanciaRPA();
        entradaMercanciaRPA6.setTexto("H|4590036045|0019|20210917|20210917|TEXTO CABECERA|TEXTO ENTREGA\u2028I|000000000820000964|6801|UND|4590036045|00010|X");
        entradaMercanciaRPA6.setIdentificador("1");
        entradaMercanciaRPAList.add(entradaMercanciaRPA6);

        EntradaMercanciaRPA entradaMercanciaRPA7 = new EntradaMercanciaRPA();
        entradaMercanciaRPA7.setTexto("H|4590036046|0019|20210917|20210917|TEXTO CABECERA|TEXTO ENTREGA\u2028I|000000000820000964|6801|UND|4590036046|00010|X");
        entradaMercanciaRPA7.setIdentificador("1");
        entradaMercanciaRPAList.add(entradaMercanciaRPA7);

        entradaMercanciaRequestRPA.setEntradaMercanciaRPAList(entradaMercanciaRPAList);

        return entradaMercanciaRequestRPA;
    }

    public EntradaMercanciaResponseRPA setEntradaMercanciaRPA(EntradaMercanciaRequestRespuestaRPA entradaMercanciaRequestRespuestaRPA) throws Exception {
        EntradaMercanciaResponseRPA entradaMercanciaResponseRPAS = new EntradaMercanciaResponseRPA();
        entradaMercanciaResponseRPAS.setMensaje("00");
        entradaMercanciaResponseRPAS.setCodigoRespuesta("Proceso correcto");

        return entradaMercanciaResponseRPAS;
    }

    public HESRequestRPA getHESRPA() throws Exception {
        HESRequestRPA hesRequestRPA = new HESRequestRPA();

        List<HESRPA> hesRPAList = new ArrayList<>();

        HESRPA hesRPA = new HESRPA();
        hesRPA.setTexto("H|4590036060|0010|20210918|20210918|TEXTOCABECERA|TEXTO REFERENCIA\u2028S|0000000001|0000000010||0000000002|||||||||\u2028S|0000000002|0000000020|0000000010||000000001100000475|1.000|SRV|1|5000.0000|SRVINDEPENDIZACION DE PROYECTO EN ETAPA|0000000002|000009315|5000.0000");
        hesRPA.setIdentificador("1");
        hesRPAList.add(hesRPA);

        HESRPA hesRPA1 = new HESRPA();
        hesRPA1.setTexto("H|4590036061|0010|20210918|20210918|TEXTOCABECERA|TEXTO REFERENCIA\u2028S|0000000001|0000000010||0000000002|||||||||\u2028S|0000000002|0000000020|0000000010||000000001100000475|1.000|SRV|1|5000.0000|SRVINDEPENDIZACION DE PROYECTO EN ETAPA|0000000002|000009315|5000.0000");
        hesRPA1.setIdentificador("1");
        hesRPAList.add(hesRPA1);

        HESRPA hesRPA2 = new HESRPA();
        hesRPA2.setTexto("H|4590036063|0010|20210918|20210918|TEXTOCABECERA|TEXTO REFERENCIA\u2028S|0000000001|0000000010||0000000002|||||||||\u2028S|0000000002|0000000020|0000000010||000000001100000475|1.000|SRV|1|5000.0000|SRVINDEPENDIZACION DE PROYECTO EN ETAPA|0000000002|000009315|5000.0000");
        hesRPA2.setIdentificador("1");
        hesRPAList.add(hesRPA2);

        HESRPA hesRPA3 = new HESRPA();
        hesRPA3.setTexto("H|4590036064|0010|20210918|20210918|TEXTOCABECERA|TEXTO REFERENCIA\u2028S|0000000001|0000000010||0000000002|||||||||\u2028S|0000000002|0000000020|0000000010||000000001100000475|1.000|SRV|1|5000.0000|SRVINDEPENDIZACION DE PROYECTO EN ETAPA|0000000002|000009315|5000.0000");
        hesRPA3.setIdentificador("1");
        hesRPAList.add(hesRPA3);

        HESRPA hesRPA4 = new HESRPA();
        hesRPA4.setTexto("H|4590036065|0010|20210918|20210918|TEXTOCABECERA|TEXTO REFERENCIA\u2028S|0000000001|0000000010||0000000002|||||||||\u2028S|0000000002|0000000020|0000000010||000000001100000475|1.000|SRV|1|5000.0000|SRVINDEPENDIZACION DE PROYECTO EN ETAPA|0000000002|000009315|5000.0000");
        hesRPA4.setIdentificador("1");
        hesRPAList.add(hesRPA4);

        HESRPA hesRPA5 = new HESRPA();
        hesRPA5.setTexto("H|4590036067|0010|20210918|20210918|TEXTOCABECERA|TEXTO REFERENCIA\u2028S|0000000001|0000000010||0000000002|||||||||\u2028S|0000000002|0000000020|0000000010||000000001100000475|1.000|SRV|1|5000.0000|SRVINDEPENDIZACION DE PROYECTO EN ETAPA|0000000002|000009315|5000.0000");
        hesRPA5.setIdentificador("1");
        hesRPAList.add(hesRPA5);

        HESRPA hesRPA6 = new HESRPA();
        hesRPA6.setTexto("H|4590036068|0010|20210918|20210918|TEXTOCABECERA|TEXTO REFERENCIA\u2028S|0000000001|0000000010||0000000002|||||||||\u2028S|0000000002|0000000020|0000000010||000000001100000475|1.000|SRV|1|5000.0000|SRVINDEPENDIZACION DE PROYECTO EN ETAPA|0000000002|000009315|5000.0000");
        hesRPA6.setIdentificador("1");
        hesRPAList.add(hesRPA6);

        HESRPA hesRPA7 = new HESRPA();
        hesRPA7.setTexto("H|4590036069|0010|20210918|20210918|TEXTOCABECERA|TEXTO REFERENCIA\u2028S|0000000001|0000000010||0000000002|||||||||\u2028S|0000000002|0000000020|0000000010||000000001100000475|1.000|SRV|1|5000.0000|SRVINDEPENDIZACION DE PROYECTO EN ETAPA|0000000002|000009315|5000.0000");
        hesRPA7.setIdentificador("1");
        hesRPAList.add(hesRPA7);

        HESRPA hesRPA8 = new HESRPA();
        hesRPA8.setTexto("H|4590036070|0010|20210918|20210918|TEXTOCABECERA|TEXTO REFERENCIA\u2028S|0000000001|0000000010||0000000002|||||||||\u2028S|0000000002|0000000020|0000000010||000000001100000475|1.000|SRV|1|5000.0000|SRVINDEPENDIZACION DE PROYECTO EN ETAPA|0000000002|000009315|5000.0000");
        hesRPA8.setIdentificador("1");
        hesRPAList.add(hesRPA8);

        HESRPA hesRPA9 = new HESRPA();
        hesRPA9.setTexto("H|4590036071|0010|20210918|20210918|TEXTOCABECERA|TEXTO REFERENCIA\u2028S|0000000001|0000000010||0000000002|||||||||\u2028S|0000000002|0000000020|0000000010||000000001100000475|1.000|SRV|1|5000.0000|SRVINDEPENDIZACION DE PROYECTO EN ETAPA|0000000002|000009315|5000.0000");
        hesRPA9.setIdentificador("1");
        hesRPAList.add(hesRPA9);

        hesRequestRPA.setHESRPAList(hesRPAList);

        return hesRequestRPA;
    }

    public HESResponseRPA setHESRPA(HESRequestRespuestaRPA hesRequestRespuestaRPA) throws Exception {
        HESResponseRPA hesResponseRPA = new HESResponseRPA();
        hesResponseRPA.setMensaje("00");
        hesResponseRPA.setCodigoRespuesta("Proceso correcto");

        return hesResponseRPA;
    }
}
