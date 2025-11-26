package com.incloud.hcp.rest;

import com.incloud.hcp.domain.LogTransaccion;
import com.incloud.hcp.domain.Proveedor;
import com.incloud.hcp.dto.*;
import com.incloud.hcp.enums.OpcionGenericaEnum;
import com.incloud.hcp.enums.OrdenCompraAprobacionEnum;
import com.incloud.hcp.exception.InvalidOptionException;
import com.incloud.hcp.exception.PortalException;
import com.incloud.hcp.jco.ordenCompra.dto.OrdenCompraRequestRPA;
import com.incloud.hcp.jco.ordenCompra.dto.OrdenCompraRequestRespuestaRPA;
import com.incloud.hcp.jco.ordenCompra.dto.OrdenCompraResponseRPA;
import com.incloud.hcp.jco.ordenCompra.dto.ordenCompraTramaJSON.ZPE_MM_GENERAR_OC;
import com.incloud.hcp.jco.ordenCompra.service.JCOOrdenCompraPublicacionService;
import com.incloud.hcp.jco.ordenCompra.service.JCOOrdenCompraService;
import com.incloud.hcp.repository.OrdenCompraRepository;
import com.incloud.hcp.repository.ProveedorRepository;
import com.incloud.hcp.service.OrdenCompraService;
import com.incloud.hcp.util.DateUtils;
import com.incloud.hcp.util.Utils;
import com.itextpdf.tool.xml.XMLWorkerHelper;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.incloud.hcp.domain.OrdenCompra;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.io.*;
import java.util.concurrent.CompletableFuture;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
@Slf4j
@RestController
@RequestMapping(value = "/api/OrdenCompra")
public class OrdenCompraRest {

    private static final String OPCION_INVALIDA = "'%s' no es una opción valida. Las opciones aceptadas son '%s' y '%s'.";
    private static final String RECHAZO_INVALIDO= "La opción '%s' es valida. Pero, el texto rechazo no puede ser vacio o nulo.";
    private OrdenCompraService ordenCompraService;
    private OrdenCompraRepository ordenCompraRepository;

    @Autowired
    JCOOrdenCompraService jcoOrdenCompraService;

    @Autowired
    private ProveedorRepository proveedorRepository;

    @Autowired
    private JCOOrdenCompraPublicacionService jcoOrdenCompraPublicacionService;

    @Autowired
    public OrdenCompraRest(OrdenCompraService ordenCompraService,
                           OrdenCompraRepository ordenCompraRepository) {
        this.ordenCompraService = ordenCompraService;
        this.ordenCompraRepository = ordenCompraRepository;
    }

    @PostMapping(value = "/getOrdenCompraList")
    public ResponseEntity<List<OrdenCompra>> getOrdenCompraList(
            @RequestParam(value = "email", required = false) String email,
            @RequestBody FiltroOrdenCompraDto filtroOrdenCompraDto){
        try{

            List<OrdenCompra> ordenCompraList = ordenCompraService.getOrdenCompraListPorFechasAndRuc(email,filtroOrdenCompraDto);

            if(ordenCompraList.isEmpty()){
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(ordenCompraList, HttpStatus.OK);
        } catch (Exception e) {
            String error = Utils.obtieneMensajeErrorException(e);
            throw new RuntimeException(error);
        }
    }


    @PostMapping(value = "/extraerOrdenCompraListRFC/{FechaInicio}/{FechaFin}")
    public ResponseEntity<ConsultaOrdenCompra> consultaOC(
            @PathVariable("FechaInicio") String fechaInicio,
            @PathVariable("FechaFin") String fechaFin
           ){
        try{
            ConsultaOrdenCompra ordenCompraList = jcoOrdenCompraPublicacionService.extraerOrdenCompraListRFC(fechaInicio, fechaFin,false);

            if(ordenCompraList != null){
                return  new ResponseEntity(ordenCompraList, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
        } catch (Exception e) {
            String error = Utils.obtieneMensajeErrorException(e);
            e.printStackTrace();
            throw new RuntimeException(error);
        }
    }

    @PutMapping(value = "/ActualizarFechaVisualizacionById/{idOrdenCompra}")
    public ResponseEntity<OrdenCompraRespuestaDto> actualizarFechaVisualizacionById(@PathVariable("idOrdenCompra") Integer idOrdenCompra){
        try{
            OrdenCompraRespuestaDto ordenCompra = ordenCompraService.updateOrdenCompraFechaVisualizacion(idOrdenCompra);
            if(ordenCompra != null){
                return  new ResponseEntity<>(ordenCompra, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
       } catch (Exception e) {
               String error = Utils.obtieneMensajeErrorException(e);
            e.printStackTrace();
               throw new RuntimeException(error);
        }
    }

    @PutMapping(value = "/AprobarRechazarOrdenCompra/{idOrdenCompra}/{aprobarRechazar}")
    public ResponseEntity<OrdenCompraRespuestaDto> aprobarRechazarOrdenCompra(@PathVariable("idOrdenCompra") Integer idOrdenCompra,
                                                                              @PathVariable("aprobarRechazar") OrdenCompraAprobacionEnum ordenCompraAprobacionEnum,
                                                                              @RequestBody(required = false) String textoRechazo){
        String opcion = ordenCompraAprobacionEnum.toString().trim().toUpperCase();

        if (!opcion.equals(OrdenCompraAprobacionEnum.APROBAR.toString()) && !opcion.equals(OrdenCompraAprobacionEnum.RECHAZAR.toString()))
            throw new InvalidOptionException(String.format(OPCION_INVALIDA, opcion, OrdenCompraAprobacionEnum.APROBAR.toString(), OrdenCompraAprobacionEnum.RECHAZAR.toString()));

        if(opcion.equals(OrdenCompraAprobacionEnum.RECHAZAR.toString()) && (textoRechazo == null || textoRechazo.isEmpty())){
            throw new InvalidOptionException(String.format(RECHAZO_INVALIDO, opcion));
        }

        int idAprobacionEnum = ordenCompraAprobacionEnum.getId();

       try {
           OrdenCompraRespuestaDto ordenCompraAprobacionRechazo = ordenCompraService.aprobarRechazarOrdenCompra(idOrdenCompra, idAprobacionEnum, textoRechazo);
            if(ordenCompraAprobacionRechazo != null){
                if (ordenCompraAprobacionRechazo.getMensajes().get(0) == "No se puede rechazar ya que la Orden de Compra cuenta con EM y HES creadas.") {
                    return new ResponseEntity<>(ordenCompraAprobacionRechazo, HttpStatus.PRECONDITION_FAILED);
                }

                return new ResponseEntity<>(ordenCompraAprobacionRechazo, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
        }catch (Exception e) {
            String error = Utils.obtieneMensajeErrorException(e);
            throw new RuntimeException(error);
        }
    }


    @GetMapping(value = "/getOrdenCompraActivaByNumero/{numOrdenCompra}")
    public ResponseEntity<List<OrdenCompra>> getOrdenCompraActivaByNumero(@PathVariable("numOrdenCompra") String numOrdenCompra){
        try{
            List<OrdenCompra> opOrdenCompra = ordenCompraRepository.getOrdenCompraActivaByNumero1(numOrdenCompra);
            if(opOrdenCompra != null){
                return  new ResponseEntity<>(opOrdenCompra, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
        } catch (Exception e) {
            String error = Utils.obtieneMensajeErrorException(e);
            throw new RuntimeException(error);
        }
    }

    @GetMapping(value = "/getOrdenCompraActivaByNumeroProveedor/{numOrdenCompra}/{email}")
    public ResponseEntity< List<OrdenCompra>> getOrdenCompraActivaByNumeroProveedor(@PathVariable("numOrdenCompra") String numOrdenCompra,
                                                                             @PathVariable("email") String email){
        try{
            if(email == null){
                throw new PortalException("debe ingresar un email");
            }
            Proveedor proveedor = proveedorRepository.getProveedorByEmail(email);
            if(proveedor == null){
                throw new PortalException("proveedor no encontrado");
            }
            List<OrdenCompra> opOrdenCompra = ordenCompraRepository.getOrdenCompraActivaByNumeroProveedor(numOrdenCompra,proveedor.getRuc());
            if(opOrdenCompra != null){
                return  new ResponseEntity<>(opOrdenCompra, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
        } catch (Exception e) {
            String error = Utils.obtieneMensajeErrorException(e);
            throw new RuntimeException(error);
        }
    }


    @GetMapping(value = "/getOrdenCompraActivaById/{idOrdenCompra}")
    public ResponseEntity<OrdenCompra> getOrdenCompraActivaById(@PathVariable("idOrdenCompra") Integer idOrdenCompra){
        try{
            Optional<OrdenCompra> opOrdenCompra = ordenCompraRepository.findByIdAndIsActive(idOrdenCompra);
            if(opOrdenCompra.isPresent()){
                return  new ResponseEntity<>(opOrdenCompra.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
        } catch (Exception e) {
            String error = Utils.obtieneMensajeErrorException(e);
            throw new RuntimeException(error);
        }
    }

    @PostMapping(value = "extraerOrdenCompraMasivo/{fechaInicio}/{fechaFin}/{enviarCorreoPublicacionOpcion}")
    public ResponseEntity<Void> extraerOrdenCompraMasivo(@PathVariable(value = "fechaInicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fechaInicio,
                                                         @PathVariable(value = "fechaFin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fechaFin,
                                                         @PathVariable(value = "enviarCorreoPublicacionOpcion") OpcionGenericaEnum enviarCorreoPublicacionOpcion){
        String opcion = enviarCorreoPublicacionOpcion.toString().trim().toUpperCase();
        boolean enviarCorreoPublicacion = OpcionGenericaEnum.NO.getValor();

        if (!opcion.equals(OpcionGenericaEnum.SI.toString()) && !opcion.equals(OpcionGenericaEnum.NO.toString()))
            throw new InvalidOptionException(String.format(OPCION_INVALIDA, opcion, OpcionGenericaEnum.SI.toString(), OpcionGenericaEnum.NO.toString()));

        if(opcion.equals(OpcionGenericaEnum.SI.toString()))
            enviarCorreoPublicacion = OpcionGenericaEnum.SI.getValor();

        try {
            ordenCompraService.extraerOrdenCompraMasivoByRangoFechas(fechaInicio, fechaFin, enviarCorreoPublicacion);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            String error = Utils.obtieneMensajeErrorException(e);
            throw new RuntimeException(error);
        }
    }


    @GetMapping(value = "/getOrdenCompraPdfByNumero/{numeroOrdenCompra}")
    public ResponseEntity<?> getOrdenCompraPdfByNumero(@PathVariable("numeroOrdenCompra") String numeroOrdenCompra/*, HttpServletResponse response*/){
        if (numeroOrdenCompra == null || numeroOrdenCompra.isEmpty())
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        try{
            Optional<OrdenCompra> opOrdenCompra = ordenCompraRepository.getOrdenCompraActivaByNumero(numeroOrdenCompra);

            if(opOrdenCompra.isPresent()){
                String nombre =  numeroOrdenCompra + "_" + opOrdenCompra.get().getProveedorRuc();

                //String html = ordenCompraService.getOrdenCompraPdfContent(numeroOrdenCompra);
                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + nombre + ".pdf");
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
                headers.add("Pragma", "no-cache");
                headers.add("Expires", "0");
                String htmlSource = ordenCompraService.getOrdenCompraPdfContent(numeroOrdenCompra);

                OutputStream file = new FileOutputStream(new File("HTMLtoPDF.pdf"));
                com.itextpdf.text.Document document = new com.itextpdf.text.Document();
                document.setMargins(20,20,20,20);
                com.itextpdf.text.pdf.PdfWriter writer = com.itextpdf.text.pdf.PdfWriter.getInstance(document, file);
                document.open();
                InputStream is = new ByteArrayInputStream(htmlSource.toString().getBytes());
                XMLWorkerHelper.getInstance().parseXHtml(writer, document, is);
                document.close();
                file.close();


                Path path = Paths.get("HTMLtoPDF.pdf");
                byte[] data = Files.readAllBytes(path);

                //==============================================================
                /*response.setHeader("Content-Disposition", "attachment; filename=" + nombre + ".pdf");*/
                return new ResponseEntity<>(data,headers ,HttpStatus.OK);
                
                
                //configuracion adjunto descarga
//                response.setHeader("Content-Disposition", "attachment; filename="+nombre);
                /*return new ResponseEntity<>(
                        ordenCompraService.getOrdenCompraPdfContent(numeroOrdenCompra),
                        HttpStatus.OK);*/


            } else {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
        } catch (Exception e) {
            String error = Utils.obtieneMensajeErrorException(e);
            throw new RuntimeException(error);
        }
    }


    @GetMapping(value = "/getContratoMarcoPdfByNumero/{numeroContratoMarco}")
    public ResponseEntity<String> getContratoMarcoPdfByNumero(@PathVariable("numeroContratoMarco") String numeroContratoMarco){
        if (numeroContratoMarco == null || numeroContratoMarco.isEmpty())
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        try{
            Optional<OrdenCompra> opOrdenCompra = ordenCompraRepository.getOrdenCompraActivaByNumero(numeroContratoMarco);

            if(opOrdenCompra.isPresent()){
                return new ResponseEntity<>(ordenCompraService.getContratoMarcoPdfContent(numeroContratoMarco),HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
        } catch (Exception e) {
            String error = Utils.obtieneMensajeErrorException(e);
            throw new RuntimeException(error);
        }
    }


    @PostMapping(value = "extraerContratoMarcoMasivo/{fechaInicio}/{fechaFin}/{enviarCorreoPublicacionOpcion}")
    public ResponseEntity<Void> extraerContratoMarcoMasivo(@PathVariable(value = "fechaInicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fechaInicio,
                                                           @PathVariable(value = "fechaFin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fechaFin,
                                                           @PathVariable(value = "enviarCorreoPublicacionOpcion") OpcionGenericaEnum enviarCorreoPublicacionOpcion){
        String opcion = enviarCorreoPublicacionOpcion.toString().trim().toUpperCase();
        boolean enviarCorreoPublicacion = OpcionGenericaEnum.NO.getValor();

        if (!opcion.equals(OpcionGenericaEnum.SI.toString()) && !opcion.equals(OpcionGenericaEnum.NO.toString()))
            throw new InvalidOptionException(String.format(OPCION_INVALIDA, opcion, OpcionGenericaEnum.SI.toString(), OpcionGenericaEnum.NO.toString()));

        if(opcion.equals(OpcionGenericaEnum.SI.toString()))
            enviarCorreoPublicacion = OpcionGenericaEnum.SI.getValor();

        try {
            ordenCompraService.extraerContratoMarcoMasivoByRangoFechas(fechaInicio, fechaFin, enviarCorreoPublicacion);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            String error = Utils.obtieneMensajeErrorException(e);
            throw new RuntimeException(error);
        }
    }

    @ApiOperation(value = "crear ordenes de compra por proveedor ", produces = "application/json")
    @PostMapping(value = "/crearOrdenCompra", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ProveedoresFinalDto>> crearOrdenCompra(@RequestBody OrdenCompraGenerarDto bean){

        try {
            List<ProveedoresFinalDto> oc =  ordenCompraService.crearOrdenCompraIprovider(bean);
            return new ResponseEntity<>(oc,HttpStatus.OK);
        } catch (Exception e) {
            String error = Utils.obtieneMensajeErrorException(e);
            e.printStackTrace();
            throw new RuntimeException(error);
        }
    }

    @ApiOperation(value = "Obtener JSON Ordenes de compra", produces = "application/json")
    @GetMapping(value = "/getJSONOrdenesCompras", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ZPE_MM_GENERAR_OC>> getJSONOrdenesCompras(){

        try {
            List<ZPE_MM_GENERAR_OC> list =  ordenCompraService.getOrdenCompraTramaJSON();
            return new ResponseEntity<>(list,HttpStatus.OK);
        } catch (Exception e) {
            String error = Utils.obtieneMensajeErrorException(e);
            throw new RuntimeException(error);
        }
    }

    @ApiOperation(value = "Obtener Ordenes de compra RPA", produces = "application/json")
    @GetMapping(value = "/getObtenerOCRPA", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<OrdenCompraRequestRPA> getOrdenComprasRPA(){

        try {
            OrdenCompraRequestRPA ordenCompraRequestRPAS =  ordenCompraService.getOrdenComprasRPA();
            return new ResponseEntity<>(ordenCompraRequestRPAS,HttpStatus.OK);
        } catch (Exception e) {
            String error = Utils.obtieneMensajeErrorException(e);
            throw new RuntimeException(error);
        }
    }

    @ApiOperation(value = "Setear Ordenes de compra RPA", produces = "application/json")
    @PostMapping(value = "/setOCRPA", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<OrdenCompraResponseRPA> setOrdenComprasRPA(OrdenCompraRequestRespuestaRPA ordenCompraRequestRespuestaRPA){

        try {
            OrdenCompraResponseRPA ordenCompraRequestRPAS =  ordenCompraService.setOrdenComprasRPA(ordenCompraRequestRespuestaRPA);
            return new ResponseEntity<>(ordenCompraRequestRPAS,HttpStatus.OK);
        } catch (Exception e) {
            String error = Utils.obtieneMensajeErrorException(e);
            throw new RuntimeException(error);
        }
    }
    @PostMapping(value = "/cargaMasiva", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> cargaMasivaOc(@RequestParam("file") MultipartFile excelFile, @RequestParam String email){
        Map<String, Object> response = new HashMap<>();
        if (excelFile.isEmpty() || !excelFile.getOriginalFilename().endsWith(".xlsx")){

            response.put("status", "error");
            response.put("message", "El archivo proporcionado debe ser un Excel válido.");

            return ResponseEntity.badRequest().body(response);
        }

        // Llamada asíncrona sin bloquear la respuesta
        CompletableFuture.runAsync(() -> {
            try {
                ordenCompraService.readExcel(excelFile, email);
                System.out.println("Archivo guardado exitosamente- FINALIZADA");
            } catch (Exception e) {
                e.printStackTrace();
                log.error("Error al procesar el archivo Excel: " + e.getMessage(), e);
            }
        });

        response.put("status", HttpStatus.OK.value());
        response.put("message", "Procesando archivo, se le notificará una vez este finalizado");

        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/logs-carga-masiva")
    public ResponseEntity<?> getLogsByIdentificador(@RequestParam String identificador){
        Map<String, Object> response = new HashMap<>();
        try {
            List<LogTransaccion> logTransaccionList =  ordenCompraService.getLogsTrasaccion(identificador);
            return new ResponseEntity<>(logTransaccionList, HttpStatus.OK);
        }catch (Exception e){
            response.put("status", HttpStatus.BAD_REQUEST);
            response.put("message", e.getMessage());
            return  new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

    }
}
