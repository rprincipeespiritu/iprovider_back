package com.incloud.hcp.job;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


import com.incloud.hcp.domain.LogTransaccion;
import com.incloud.hcp.domain.ModificacionSolped;
import com.incloud.hcp.dto.ConstanciaDetraccionProveedorDto;
import com.incloud.hcp.dto.ConsultaOrdenCompra;
import com.incloud.hcp.jco.banco.service.JCOBancoService;
import com.incloud.hcp.jco.centro.service.JCOCentroServiceNew;
import com.incloud.hcp.jco.centroAlmacen.service.JCOCentroAlmacenService;
import com.incloud.hcp.jco.documentoAceptacion.service.JCODocumentoAceptacionService;
import com.incloud.hcp.jco.grupoArticulo.service.JCOGrupoArticuloService;
import com.incloud.hcp.jco.materiales.service.JCOMaterialesService;
import com.incloud.hcp.jco.materiales.service.JCOMaterialesServiceNew;
import com.incloud.hcp.jco.ordenCompra.service.JCOOrdenCompraPublicacionService;
import com.incloud.hcp.jco.proveedor.service.JCOProveedorService;
import com.incloud.hcp.jco.servicios.service.JCOServiciosService;
import com.incloud.hcp.jco.servicios.service.JCOServiciosServiceNew;
import com.incloud.hcp.jco.solped.dto.SolicitudPedidoRFCResponseDto;
import com.incloud.hcp.jco.solped.service.JCOSolicitudPedidoService;
import com.incloud.hcp.jco.tipoCambio.service.JCOTipoCambioService;
import com.incloud.hcp.jco.unidadMedida.service.JCOUnidadMedidaServiceNew;
import com.incloud.hcp.repository.AppProcesoLogRepository;
import com.incloud.hcp.repository.LogTransaccionRepository;
import com.incloud.hcp.repository.ModificacionSolpedRepository;
import com.incloud.hcp.service.BienServicioService;
import com.incloud.hcp.service.ConstanciaDetraccionDetalleService;
import com.incloud.hcp.service.ConstanciaRetencionService;
import com.incloud.hcp.service.LicitacionService;
import com.incloud.hcp.service.delta.LicitacionSubetapaDeltaService;
import com.incloud.hcp.service.extractor.ExtractorAlmacenService;
import com.incloud.hcp.service.extractor.ExtractorBienServicioService;
import com.incloud.hcp.util.DateUtils;
import com.incloud.hcp.util.Utils;
import com.incloud.hcp.ws.enums.CodEmpresaEnum;

@Profile("!devlocal")
@EnableAsync
@Component
public class ScheduledTasks {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ScheduledTasks.class);

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    private Boolean bloqueo = false;

    private Boolean bloqueoHes = false;

    @Autowired
    private LicitacionService licitacionService;

    @Autowired
    private BienServicioService bienServicioService;

    @Autowired
    private AppProcesoLogRepository appProcesoLogRepository;

    @Autowired
    private LicitacionSubetapaDeltaService licitacionSubetapaDeltaService;

    @Autowired
    private JCOGrupoArticuloService jcoGrupoArticuloService;

    @Autowired
    private JCOTipoCambioService jcoTipoCambioService;

    @Autowired
    private JCOMaterialesService jcoMaterialesService;

    @Autowired
    private JCOServiciosService jcoServiciosService;

    @Autowired
    private JCOCentroAlmacenService jcoCentroAlmacenService;

    @Autowired
    private JCOCentroServiceNew jcoCentroServiceNew;

    @Autowired
    private JCOUnidadMedidaServiceNew jcoUnidadMedidaServiceNew;

    @Autowired
    private ExtractorAlmacenService extractorAlmacenService;

    @Autowired
    private ExtractorBienServicioService extractorBienServicioService;

    @Autowired
    private JCOSolicitudPedidoService jcoSolicitudPedidoService;

    @Autowired
    private ModificacionSolpedRepository modificacionSolpedRepository;

    @Autowired
    private JCOMaterialesServiceNew jcoMaterialesServiceNew;

    @Autowired
    private JCOServiciosServiceNew jcoServiciosServiceNew;

    @Autowired
    private JCOProveedorService jcoProveedorService;

    @Autowired
    private JCODocumentoAceptacionService jcoDocumentoAceptacionService;

    @Autowired
    private JCOBancoService jcoExtraerBancosService;
    @Autowired
    private JCOOrdenCompraPublicacionService jcoOrdenCompraPublicacionService;

    @Autowired
    private LogTransaccionRepository logTransaccionRepository;
    @Autowired
    private ConstanciaDetraccionDetalleService constanciaDetraccionDetalleService;
    @Autowired
    private ConstanciaRetencionService constanciaRetencionService;

    //    @Scheduled(cron = "* * * * * ?")
    public void scheduleAlmacen() {
        logger.error("Cron Task scheduleAlmacen:: Execution Time - {}", dateTimeFormatter.format(LocalDateTime.now()));
        try {
            // traer primero la lista de proveedores de la BD.
            this.extractorAlmacenService.obtenerAlmacen(CodEmpresaEnum.SILVESTRE.getValor(), "25/07/2020", "25/03/2021");
        } catch (Exception e) {
            logger.error("Cron Task Fin JOB scheduleAlmacen ERROR: " + Utils.obtieneMensajeErrorException(e));
        }
        logger.error("Cron Task Fin JOB scheduleAlmacen");
    }

    //@Scheduled(cron = "* * * * * ?")
    public void scheduleProducto() {
        logger.error("Cron Task scheduleProducto:: Execution Time - {}", dateTimeFormatter.format(LocalDateTime.now()));
        try {
            // traer primero la lista de proveedores de la BD.
            this.extractorBienServicioService.extraerBienServicio("01/01/2020", "01/01/2021");
        } catch (Exception e) {
            logger.error("Cron Task Fin JOB scheduleProducto ERROR: " + Utils.obtieneMensajeErrorException(e));
        }
        logger.error("Cron Task Fin JOB scheduleProducto");
    }

    // deshabilitado MOD01
    @Async
    @Scheduled(cron = "0/60 * * * * *")
    public void scheduleLicitacionEstadoPorEvaluar() {
        logger.error("Cron Task scheduleLicitacionEstadoPorEvaluar :: Execution Time - {}", dateTimeFormatter.format(LocalDateTime.now()));
        try {
            this.licitacionService.updateLicitacionEstadoPorEvaluar();
        } catch (Exception e) {
            logger.error("Cron Task Fin JOB scheduleLicitacionEstadoPorEvaluar ERROR: " + Utils.obtieneMensajeErrorException(e));
        }
        logger.error("Cron Task Fin JOB scheduleLicitacionEstadoPorEvaluar");
    }

    @Async
    //AAA @Scheduled(cron = "0 5,35 * * * ?")
    public void scheduleEnviarCorreoRecordatorio() {
        logger.error("Cron Task scheduleEnviarCorreoRecordatorio :: Execution Time - {}", dateTimeFormatter.format(LocalDateTime.now()));
        try {
            String respuesta = "";
            respuesta = this.licitacionSubetapaDeltaService.enviarCorreoRecordatorio();
            LogTransaccion logTransaccion = new LogTransaccion();
            logTransaccion.setEnvioTrama("enviarCorreoRecordatorio");
            logTransaccion.setRespuestaCodigo(respuesta);
            logTransaccion.setTipoRegistro("Correo scheduleEnviarCorreoRecordatorio");
            this.logTransaccionRepository.save(logTransaccion);
        } catch (Exception e) {
            logger.error("Cron Task Fin JOB scheduleEnviarCorreoRecordatorio ERROR: " + Utils.obtieneMensajeErrorException(e));
        }
    }

//    @Scheduled(cron = "0 45 * * * ?")
//    public void scheduleSincronizarBienServicio() {
//        logger.error("Cron Task scheduleSincronizarBienServicio :: Execution Time - {}", dateTimeFormatter.format(LocalDateTime.now()));
//        try {
//           this.bienServicioService.sincronizarBienServicioByLastDate();
//        }
//        catch (Exception e) {
//            logger.error("Cron Task Fin JOB scheduleSincronizarBienServicio ERROR: " + Utils.obtieneMensajeErrorException(e));
//        }
//        logger.error("Cron Task Fin JOB scheduleSincronizarBienServicio");
//    }

    // Ejecutar todos los dias 1 al 3 de cada mes desde las 6:00 a 9:00 am cada hora
    /*@Scheduled(cron = "0 0 6-9 1-3 * ?")
    public void scheduleEliminarAppProcesoLog() {
        logger.error("Cron Task scheduleEliminarAppProcesoLog :: Execution Time - {}", dateTimeFormatter.format(LocalDateTime.now()));
        try {
            Date fechaMenosMes = DateUtils.obtenerFechaActualMinusMonth(1);
            this.appProcesoLogRepository.deleteAllByFechaInicioEjecucionIsBefore(fechaMenosMes);
        }
        catch (Exception e) {
            logger.error("Cron Task Fin JOB scheduleEliminarAppProcesoLog ERROR: " + Utils.obtieneMensajeErrorException(e));
        }
    }*/

    /***********************************/
    /* Procesos de RFC                 */
    /***********************************/

    /*@Scheduled(cron = "0 0 5-7 * * ?")
    public void scheduleActualizarGrupoArticulos() {
        logger.error("Cron Task scheduleActualizarGrupoArticulos :: Execution Time - {}", dateTimeFormatter.format(LocalDateTime.now()));
        try {
            this.jcoGrupoArticuloService.actualizarGrupoArticulo();
        }
        catch (Exception e) {
            logger.error("Cron Task Fin JOB scheduleActualizarGrupoArticulos ERROR: " + Utils.obtieneMensajeErrorException(e));
        }
    }



    @Scheduled(cron = "0 45 5-12 * * ?")
    public void scheduleActualizarBienesServicio() {
        logger.error("Cron Task scheduleActualizarBienesServicio :: Execution Time - {}", dateTimeFormatter.format(LocalDateTime.now()));
        try {
            Date fecha = DateUtils.obtenerFechaActual();
            String sFecha = DateUtils.convertDateToString("yyyyMMdd", fecha);
            this.jcoUnidadMedidaServiceNew.actualizarUnidadMedida();
            this.jcoGrupoArticuloService.actualizarGrupoArticulo();
            this.jcoMaterialesService.actualizarMaterialesRFC(sFecha, sFecha);
            this.jcoServiciosService.actualizarMaterialesRFC(sFecha, sFecha);
        }
        catch (Exception e) {
            logger.error("Cron Task Fin JOB scheduleActualizarBienesServicio ERROR: " + Utils.obtieneMensajeErrorException(e));
        }
    }
    */
    @Scheduled(cron = "0 */2 * * * *")
    public void scheduleActualizarCentroAlmacen() {
        logger.error("Cron Task scheduleActualizarCentroAlmacen :: Execution Time - {}", dateTimeFormatter.format(LocalDateTime.now()));
        try {
            //this.jcoCentroServiceNew.actualizarCentro("");
            this.jcoCentroAlmacenService.actualizaCentroAlmacen("");
        }
        catch (Exception e) {
            logger.error("Cron Task Fin JOB scheduleActualizarCentroAlmacen ERROR: " + Utils.obtieneMensajeErrorException(e));
        }
    }

    @Async
    @Scheduled(cron = "0 30 5-12 * * ?")
    public void scheduleActualizarTasaCambio() {
        logger.error("Cron Task scheduleActualizarTasaCambio :: Execution Time - {}", dateTimeFormatter.format(LocalDateTime.now()));
        try {
            Date fecha = DateUtils.obtenerFechaActual();
            String sFecha = DateUtils.convertDateToString("dd.MM.yyyy", fecha);
            this.jcoTipoCambioService.actualizarTipoCambio(sFecha);
        }
        catch (Exception e) {
            logger.error("Cron Task Fin JOB scheduleActualizarTasaCambio ERROR: " + Utils.obtieneMensajeErrorException(e));
        }
    }

    //deshabilitado MOD01
    
    @Async
    //@Scheduled(cron = "*/60 * * * * *")//@Scheduled(cron = "0 30 5-12 * * ?")
    @Scheduled(cron = "0 */5 * * * *")
    public void scheduleObtenerOC() {
        if(!bloqueo) {
            bloqueo = true;
            logger.error("Cron Task scheduleObtenerOC :: Execution Time - {}", dateTimeFormatter.format(LocalDateTime.now()));
            try {

                Date fechaInicio = DateUtils.obtenerFechaDias(-1);//-1
                Date fechaFin = DateUtils.obtenerFechaActual();

                // Agregar un día a fechaFin
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(fechaFin);
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                fechaFin = calendar.getTime();

                String sFechaInicio = DateUtils.convertDateToString("yyyy-MM-dd'T'HH:mm:ss", fechaInicio);

                String sFechaFin = DateUtils.convertDateToString("yyyy-MM-dd'T'HH:mm:ss", fechaFin);

                this.jcoOrdenCompraPublicacionService.extraerOrdenCompraListRFC(sFechaInicio, sFechaFin, true); //Aqui era false
            } catch (Exception e) {
                logger.error("Cron Task Fin JOB scheduleObtenerOC ERROR: " + Utils.obtieneMensajeErrorException(e));
            }
            bloqueo = false;
        }
    }

    // deshabilitado MOD01
    @Async
    @Scheduled(cron = "0 */10 * * * *") //@Scheduled(cron = "0 30 5-12 * * ?")
    public void scheduleBancos() {
        logger.error("Cron Task scheduleObtenerOC :: Execution Time - {}", dateTimeFormatter.format(LocalDateTime.now()));
        try {
            Date fechaInicio = DateUtils.obtenerFechaDias(-5);
            Date fechaFin = DateUtils.obtenerFechaActual();

            String sFechaInicio = DateUtils.convertDateToString("yyyy-MM-dd'T'HH:mm:ss",fechaInicio);

            String sFechaFin = DateUtils.convertDateToString("yyyy-MM-dd'T'HH:mm:ss",fechaFin);

            //this.jcoOrdenCompraPublicacionService.extraerOrdenCompraListRFC(sFechaInicio, sFechaFin,false);
        }
        catch (Exception e) {
            logger.error("Cron Task Fin JOB scheduleObtenerOC ERROR: " + Utils.obtieneMensajeErrorException(e));
        }
    }

    // deshabilitado MOD01
    @Async
    //@Scheduled(cron = "*/60 * * * * *")
    @Scheduled(cron = "0 */10 * * * *")
    public void scheduleActualizarMateriales() {
        logger.error("Cron Task scheduleActualizarMateriales :: Execution Time - {}", dateTimeFormatter.format(LocalDateTime.now()));
        try {
            //Date fecha = DateUtils.obtenerFechaActual();
            //String sFecha = DateUtils.convertDateToString("dd.MM.yyyy",fecha);
            Date fechaInicio = DateUtils.obtenerFechaDias(-1);
            Date fechaFin = DateUtils.obtenerFechaActual();

            String sFechaInicio = DateUtils.convertDateToString("yyyy-MM-dd'T'HH:mm:ss",fechaInicio);

            String sFechaFin = DateUtils.convertDateToString("yyyy-MM-dd'T'HH:mm:ss",fechaFin);

            this.jcoMaterialesServiceNew.getListMaterialesRFC(sFechaInicio,sFechaFin);
        }
        catch (Exception e) {
            logger.error("Cron Task Fin JOB scheduleActualizarMateriales ERROR: " + Utils.obtieneMensajeErrorException(e));
        }
    }

    //deshabilitado MOD01
    @Async
    //@Scheduled(cron = "*/60 * * * * *")
    @Scheduled(cron = "0 */10 * * * *")
    public void scheduleActualizarServicio() {
        logger.error("Cron Task scheduleActualizarServicio :: Execution Time - {}", dateTimeFormatter.format(LocalDateTime.now()));
        try {
            //Date fecha = DateUtils.obtenerFechaActual();
            //String sFecha = DateUtils.convertDateToString("yyyyMMdd",fecha);

            Date fechaInicio = DateUtils.obtenerFechaDias(-1);
            Date fechaFin = DateUtils.obtenerFechaActual();

            String sFechaInicio = DateUtils.convertDateToString("yyyy-MM-dd'T'HH:mm:ss",fechaInicio);

            String sFechaFin = DateUtils.convertDateToString("yyyy-MM-dd'T'HH:mm:ss",fechaFin);

            this.jcoServiciosServiceNew.getListServicios(sFechaInicio,sFechaFin);
        }
        catch (Exception e) {
            logger.error("Cron Task Fin JOB scheduleActualizarServicio ERROR: " + Utils.obtieneMensajeErrorException(e));
        }
    }

    //
    // deshabilitado MOD01
    //@Async
    //@Scheduled(cron = "0 */7 * * * *")
    /*public void scheduleObtenerDocumentosAceptacion() {
        if (!bloqueo) {
            bloqueo = true;
            logger.error("Cron Task scheduleObtenerDocumentosAceptacion :: Execution Time - {}", dateTimeFormatter.format(LocalDateTime.now()));
            try {

                Date fechaInicio = DateUtils.obtenerFechaDias(-4);
                Date fechaFin = DateUtils.obtenerFechaActual();

                String sFechaInicio = DateUtils.convertDateToString("yyyy-MM-dd'T'HH:mm:ss", fechaInicio);

                String sFechaFin = DateUtils.convertDateToString("yyyy-MM-dd'T'HH:mm:ss", fechaFin);

                this.jcoDocumentoAceptacionService.extraerDocumentoAceptacionListRFC(sFechaInicio, sFechaFin, false, true, true);

            } catch (Exception e) {
                logger.error("Cron Task Fin JOB scheduleObtenerDocumentosAceptacion ERROR: " + Utils.obtieneMensajeErrorException(e));
            }
            bloqueo = false;
        }
    }*/

    @Async
    @Scheduled(cron = "0 */7 * * * *")
    public void scheduleObtenerDocumentosAceptacionHES() {
        if (!bloqueo) {
            bloqueo = true;
            logger.info("Cron Task scheduleObtenerDocumentosAceptacionHES :: Execution Time - {}", 
                        dateTimeFormatter.format(LocalDateTime.now()));
            try {
                // Fecha inicio: 4 días atrás a las 00:00:00Z
                Date fechaInicio = DateUtils.obtenerFechaDias(-2);
                Date fechaFin = DateUtils.obtenerFechaActual();
                Date fechaFinHes = DateUtils.obtenerFechaDias(+1);

                // Formato ISO 8601 con sufijo Z (UTC)
                String sFechaInicio = DateUtils.convertDateToString("yyyy-MM-dd'T'HH:mm:ss'Z'", fechaInicio);
                String sFechaFin = DateUtils.convertDateToString("yyyy-MM-dd'T'HH:mm:ss'Z'", fechaFin);
                String sFechaFinHes = DateUtils.convertDateToString("yyyy-MM-dd'T'HH:mm:ss'Z'", fechaFinHes);

                // --- Bloque 0: extracción EM ---
                try {
                    String sFechaInicioEM = DateUtils.convertDateToString("yyyy-MM-dd'T'HH:mm:ss", fechaInicio);
                    String sFechaFinEM = DateUtils.convertDateToString("yyyy-MM-dd'T'HH:mm:ss", fechaFin);

                    this.jcoDocumentoAceptacionService.extraerDocumentoAceptacionListRFC(sFechaInicioEM, sFechaFinEM, false, true, true);
                    logger.info(">>> Extracción de EM finalizada correctamente");
                } catch (Exception e) {
                    logger.error("Error en extracción de EM : {}", Utils.obtieneMensajeErrorException(e));
                }

                // --- Bloque 1: extracción HES ---
                try {
                    this.jcoDocumentoAceptacionService.extraerDocumentoAceptacionHES(sFechaInicio, sFechaFinHes, true, true);
                    logger.info(">>> Extracción de HES finalizada correctamente");
                } catch (Exception e) {
                    logger.error("Error en extracción de HES: {}", Utils.obtieneMensajeErrorException(e));
                }

                // --- Bloque 2: extracción Anuladas ---
                try {
                    this.jcoDocumentoAceptacionService.extraerDocumentoAceptacionAnuladasList(false);
                    logger.info(">>> Extracción de HES Anuladas finalizada correctamente");
                } catch (Exception e) {
                    logger.error("Error en extracción de HES Anuladas: {}", Utils.obtieneMensajeErrorException(e));
                }

            } catch (Exception e) {
                logger.error("Cron Task scheduleObtenerDocumentosAceptacionHES ERROR general: {}", 
                            Utils.obtieneMensajeErrorException(e));
            } finally {
                bloqueo = false;
            }
        }
    }


    //@Async
    //@Scheduled(cron = "0 */7 * * * *")
    /*public void scheduleObtenerDocumentosAceptacionAnulados() {

            logger.error("Cron Task scheduleObtenerDocumentosAceptacionAnulados :: Execution Time - {}", dateTimeFormatter.format(LocalDateTime.now()));
            try {

                this.jcoDocumentoAceptacionService.extraerDocumentoAceptacionAnuladasList(false);

            } catch (Exception e) {
                logger.error("Cron Task Fin JOB scheduleObtenerDocumentosAceptacionAnulados ERROR: " + Utils.obtieneMensajeErrorException(e));
            }
    }
    */

    @Async
    //@Scheduled(cron = "*/60 * * * * *")
    @Scheduled(cron = "0 */11 * * * *")
    public void scheduleObtenerBancos() {
        logger.error("Cron Task scheduleObtenerBancos :: Execution Time - {}", dateTimeFormatter.format(LocalDateTime.now()));
        try {

            Date fecha = DateUtils.obtenerFechaActual();
            String sFechaInicio = DateUtils.convertDateToString("yyyy-MM-dd",fecha);

            String sFechaFin = DateUtils.convertDateToString("yyyy-MM-dd",fecha);
            logger.error("fechas "+sFechaInicio+"-"+sFechaFin);
            this.jcoExtraerBancosService.extraerBancosRFC(sFechaInicio, sFechaFin);

        }
        catch (Exception e) {
            logger.error("Cron Task Fin JOB scheduleObtenerBancos ERROR: " + Utils.obtieneMensajeErrorException(e));
        }
    }



    //@Async
    //@Scheduled(cron = "*/60 * * * * *")

    public void scheduleActualizarEstadoSOLPED() {
        logger.error("Cron Task scheduleActualizarEstadoSOLPED :: Execution Time - {}", dateTimeFormatter.format(LocalDateTime.now()));
        try {

            List<ModificacionSolped> modificacionSolpeds = this.modificacionSolpedRepository.finbyEstado2();

            //crear array de rfc
            List<SolicitudPedidoRFCResponseDto> rfcResponseDto =new ArrayList<>();
            //Recorrer las solped
            if(modificacionSolpeds.size() > 0){
                modificacionSolpeds.forEach(item->{
                    //validar si la solped es diferente de estado X
                    if(!item.getEstadoSolped().equals("X")){
                        try {
                            SolicitudPedidoRFCResponseDto rfResponse = new SolicitudPedidoRFCResponseDto();
                            rfResponse =
                                    this.jcoSolicitudPedidoService.getSolpedResponseByCodigo(String.valueOf(item.getNroSolped()));

                            //validar si la solped viene en estado X
                            if(!rfResponse.getEstadoSolped().equals("X")){
                                modificacionSolpedRepository.modificarEstadoSolped("X",item.getNroSolped());
                            }

                            rfcResponseDto.add(rfResponse);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }else{
                        //modificarlas directamente
                        modificacionSolpedRepository.modificarEstadoSolped("X",item.getNroSolped());
                    }
                });
            }

            logger.info(rfcResponseDto.toString());

        }
        catch (Exception e) {
            logger.error("Cron Task Fin JOB scheduleActualizarEstadoSOLPED ERROR: " + Utils.obtieneMensajeErrorException(e));
        }
    }

    // deshabilitado MOD01
    @Async
    //AAA @Scheduled(cron = "0 0 13 ? * * ")

    public void notificarHomologacionVencida() {
        logger.error("Cron Task notificarHomologacionVencida :: Execution Time - {}", dateTimeFormatter.format(LocalDateTime.now()));
        try {

            this.jcoProveedorService.notificacionHomologacion();

        }
        catch (Exception e) {
            logger.error("Cron Task Fin JOB notificarHomologacionVencida ERROR: " + Utils.obtieneMensajeErrorException(e));
        }
    }
    @Async
    @Scheduled(cron = "0/5 * * * * ?")
    public void scheduleEnvioProovedoresDetraccion() {
        logger.error("Cron Task scheduleEnvioProovedoresDetraccion:: Execution Time - {}", dateTimeFormatter.format(LocalDateTime.now()));
        try {
            // Correo.
           String respuesta =  this.constanciaDetraccionDetalleService.enviarCorreoConstanciaDetraccion();
            System.out.println(respuesta);
        } catch (Exception e) {
            logger.error("Cron Task Fin JOB scheduleEnvioProovedoresDetraccion ERROR: " + Utils.obtieneMensajeErrorException(e));
        }
        logger.error("Cron Task Fin JOB scheduleEnvioProovedoresDetraccion");
    }

    @Async
    @Scheduled(cron = "0/8 * * * * ?")
    public void scheduleEnvioProovedoresRetencion() {
        logger.error("Cron Task scheduleEnvioProovedoresRetencion:: Execution Time - {}", dateTimeFormatter.format(LocalDateTime.now()));
        try {
            // Correo.
            String respuesta =  this.constanciaRetencionService.enviarCorreoConstanciaRetencion();
            System.out.println(respuesta);
        } catch (Exception e) {
            logger.error("Cron Task Fin JOB scheduleEnvioProovedoresRetencion ERROR: " + Utils.obtieneMensajeErrorException(e));
        }
        logger.error("Cron Task Fin JOB scheduleEnvioProovedoresRetencion");
    }


}
