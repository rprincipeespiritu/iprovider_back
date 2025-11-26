package com.incloud.hcp.rest;

import com.incloud.hcp.domain.OrdenCompra;
import com.incloud.hcp.domain.Proveedor;
import com.incloud.hcp.dto.DocumentoAceptacionEntradaDto;
import com.incloud.hcp.enums.OpcionGenericaEnum;
import com.incloud.hcp.exception.InvalidOptionException;
import com.incloud.hcp.myibatis.mapper.ParametroMapper;
import com.incloud.hcp.pdf.bean.FieldConformidadServicioPdfDTO;
import com.incloud.hcp.pdf.bean.FieldEntradaMercaderiaPdfDTO;
import com.incloud.hcp.pdf.bean.ParameterConformidadServicioPdfDTO;
import com.incloud.hcp.pdf.bean.ParameterEntradaMercaderiaPdfDTO;
import com.incloud.hcp.repository.DocumentoAceptacionDetalleRepository;
import com.incloud.hcp.repository.DocumentoAceptacionRepository;
import com.incloud.hcp.service.DocumentoAceptacionService;
import com.incloud.hcp.service.OrdenCompraService;
import com.incloud.hcp.service.ProveedorService;
import com.incloud.hcp.service.SociedadService;
import com.incloud.hcp.service.notificacion.MailSetting;
import com.incloud.hcp.util.DateUtils;
import com.incloud.hcp.util.Utils;
import com.itextpdf.tool.xml.XMLWorkerHelper;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.HtmlEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.incloud.hcp.domain.DocumentoAceptacion;
import com.incloud.hcp.domain.DocumentoAceptacionDetalle;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.HttpServletResponse;
import java.io.*;


@RestController
@RequestMapping(value = "/api/DocumentoAceptacion")
public class DocumentoAceptacionRest {

    private static final String OPCION_INVALIDA = "'%s' no es una opción valida. Las opciones aceptadas son '%s' y '%s'.";

    private DocumentoAceptacionService documentoAceptacionService;
    private DocumentoAceptacionRepository documentoAceptacionRepository;
    private DocumentoAceptacionDetalleRepository documentoAceptacionDetalleRepository;
    private OrdenCompraService ordenCompraService;
    private SociedadService sociedadService;
    private ProveedorService proveedorService;

    @Autowired
    private ParametroMapper parametroMapper;

    @Autowired
    public DocumentoAceptacionRest(DocumentoAceptacionService documentoAceptacionService, OrdenCompraService ordenCompraService, SociedadService sociedadService, ProveedorService proveedorService, DocumentoAceptacionDetalleRepository documentoAceptacionDetalleRepository) {
        this.documentoAceptacionService = documentoAceptacionService;
        this.ordenCompraService = ordenCompraService;
        this.sociedadService = sociedadService;
        this.proveedorService = proveedorService;
        this.documentoAceptacionDetalleRepository = documentoAceptacionDetalleRepository;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ResponseEntity<List<DocumentoAceptacion>> getAllDocumentoAceptacion() {
        List<DocumentoAceptacion> documentoAceptacionList = documentoAceptacionService.getAllDocumentoAceptacion();

        if (documentoAceptacionList.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(documentoAceptacionList, HttpStatus.OK);
    }

    @GetMapping(value = "/getDocumentoAceptacionList/{FechaInicio}/{FechaFin}")
    public ResponseEntity<List<DocumentoAceptacion>> getDocumentoAceptacionList(
            @PathVariable("FechaInicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fechaInicio,
            @PathVariable("FechaFin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fechaFin,
            @RequestParam(value = "email", required = false) String email) {
        try {
            Date fechaHasta =  DateUtils.sumarRestarDias(fechaFin,1);
            List<DocumentoAceptacion> documentoAceptacionList =
                    documentoAceptacionService.getDocumentoAceptacionPorFechasAndRuc(fechaInicio, fechaHasta, email);
            if (documentoAceptacionList.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(documentoAceptacionList, HttpStatus.OK);
        } catch (Exception e) {
            String error = Utils.obtieneMensajeErrorException(e);
            throw new RuntimeException(error);
        }
    }

    @PostMapping(value = "/getPostDocumentoAceptacionList")
    public ResponseEntity<List<DocumentoAceptacion>> getPostDocumentoAceptacionList(@RequestBody DocumentoAceptacionEntradaDto bean) {
        try {
            List<DocumentoAceptacion> documentoAceptacionList = documentoAceptacionService.getDocumentoAceptacionList(bean);

            if (documentoAceptacionList.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(documentoAceptacionList, HttpStatus.OK);
        } catch (Exception e) {
            String error = Utils.obtieneMensajeErrorException(e);
            throw new RuntimeException(error);
        }
    }


    @PostMapping(value = "extraerDocumentoAceptacionMasivo/{fechaInicio}/{fechaFin}/{aprobarOrdenCompraOpcion}/{enviarCorreoAprobacionOpcion}")
    public ResponseEntity<Void> extraerDocumentoAceptacionMasivo(@PathVariable(value = "fechaInicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fechaInicio,
                                                                 @PathVariable(value = "fechaFin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fechaFin,
                                                                 @PathVariable(value = "aprobarOrdenCompraOpcion") OpcionGenericaEnum aprobarOrdenCompraOpcion,
                                                                 @PathVariable(value = "enviarCorreoAprobacionOpcion") OpcionGenericaEnum enviarCorreoAprobacionOpcion) {
        String opcionAprobarOC = aprobarOrdenCompraOpcion.toString().trim().toUpperCase();
        boolean aprobarOrdenCompra = OpcionGenericaEnum.NO.getValor();

        if (!opcionAprobarOC.equals(OpcionGenericaEnum.SI.toString()) && !opcionAprobarOC.equals(OpcionGenericaEnum.NO.toString())) {
            throw new InvalidOptionException(String.format(OPCION_INVALIDA, opcionAprobarOC, OpcionGenericaEnum.SI.toString(), OpcionGenericaEnum.NO.toString()));
        }else {
            if (opcionAprobarOC.equals(OpcionGenericaEnum.SI.toString()))
                aprobarOrdenCompra = OpcionGenericaEnum.SI.getValor();
        }

        String opcionEnviarCorreo = enviarCorreoAprobacionOpcion.toString().trim().toUpperCase();
        boolean enviarCorreoAprobacion = OpcionGenericaEnum.NO.getValor();

        if (!opcionEnviarCorreo.equals(OpcionGenericaEnum.SI.toString()) && !opcionEnviarCorreo.equals(OpcionGenericaEnum.NO.toString())) {
            throw new InvalidOptionException(String.format(OPCION_INVALIDA, opcionEnviarCorreo, OpcionGenericaEnum.SI.toString(), OpcionGenericaEnum.NO.toString()));
        }else {
            if (opcionEnviarCorreo.equals(OpcionGenericaEnum.SI.toString()))
                enviarCorreoAprobacion = OpcionGenericaEnum.SI.getValor();
        }

        try {
            LocalDate localDateFechaInicio = DateUtils.utilDateToLocalDate(fechaInicio);
            LocalDate localDateFechaFin = DateUtils.utilDateToLocalDate(fechaFin);

            documentoAceptacionService.extraerDocumentoAceptacionMasivoByRangoFechas(localDateFechaInicio, localDateFechaFin, aprobarOrdenCompra, enviarCorreoAprobacion);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            String error = Utils.obtieneMensajeErrorException(e);
            throw new RuntimeException(error);
        }
    }


    @PostMapping(value = "extraerDocumentoAceptacionPorNumOrdenCompraYNumDocAceptacion/{numeroOrdenCompra}/{numeroDocumentoAceptacion}/{aprobarOrdenCompraOpcion}/{enviarCorreoAprobacionOpcion}")
    public ResponseEntity<String> extraerDocumentoAceptacionPorNumOrdenCompraYNumDocAceptacion(@PathVariable(value = "numeroOrdenCompra") String numeroOrdenCompra,
                                                                                     @PathVariable(value = "numeroDocumentoAceptacion") String numeroDocumentoAceptacion,
                                                                                     @PathVariable(value = "aprobarOrdenCompraOpcion") OpcionGenericaEnum aprobarOrdenCompraOpcion,
                                                                                     @PathVariable(value = "enviarCorreoAprobacionOpcion") OpcionGenericaEnum enviarCorreoAprobacionOpcion) {
        String opcionAprobarOC = aprobarOrdenCompraOpcion.toString().trim().toUpperCase();
        boolean aprobarOrdenCompra = OpcionGenericaEnum.NO.getValor();

        if (!opcionAprobarOC.equals(OpcionGenericaEnum.SI.toString()) && !opcionAprobarOC.equals(OpcionGenericaEnum.NO.toString())) {
            throw new InvalidOptionException(String.format(OPCION_INVALIDA, opcionAprobarOC, OpcionGenericaEnum.SI.toString(), OpcionGenericaEnum.NO.toString()));
        }else {
            if (opcionAprobarOC.equals(OpcionGenericaEnum.SI.toString()))
                aprobarOrdenCompra = OpcionGenericaEnum.SI.getValor();
        }

        String opcionEnviarCorreo = enviarCorreoAprobacionOpcion.toString().trim().toUpperCase();
        boolean enviarCorreoAprobacion = OpcionGenericaEnum.NO.getValor();

        if (!opcionEnviarCorreo.equals(OpcionGenericaEnum.SI.toString()) && !opcionEnviarCorreo.equals(OpcionGenericaEnum.NO.toString())) {
            throw new InvalidOptionException(String.format(OPCION_INVALIDA, opcionEnviarCorreo, OpcionGenericaEnum.SI.toString(), OpcionGenericaEnum.NO.toString()));
        }else {
            if (opcionEnviarCorreo.equals(OpcionGenericaEnum.SI.toString()))
                enviarCorreoAprobacion = OpcionGenericaEnum.SI.getValor();
        }

        try {
            String respuesta = documentoAceptacionService.extraerDocumentoAceptacionByNumOrdenCompraAndNumDocAceptacion(numeroOrdenCompra, numeroDocumentoAceptacion, aprobarOrdenCompra, enviarCorreoAprobacion);
            return new ResponseEntity<>(respuesta, HttpStatus.OK);
        } catch (Exception e) {
            String error = Utils.obtieneMensajeErrorException(e);
            throw new RuntimeException(error);
        }
    }


    /*@PostMapping(value = "entregaMercaderiaPdf/{idDocumentoAceptacion}")
    public String getEntregaMercaderiaPdf(@PathVariable(value = "idDocumentoAceptacion") Integer idEntregaMercaderia) {
        try {
            Optional<DocumentoAceptacion> doc = documentoAceptacionService.getDocumentoAceptacionId(idEntregaMercaderia);
            Integer idTipoDoc = doc.get().getIdTipoDocumentoAceptacion();
            DocumentoAceptacion documentoAceptacion;
            if (idTipoDoc == 3){
                documentoAceptacion = documentoAceptacionService.getDocumentoAceptacionbyId(3, idEntregaMercaderia);
            } else {
                documentoAceptacion = documentoAceptacionService.getDocumentoAceptacionbyId(1, idEntregaMercaderia);
            }
            //DocumentoAceptacion documentoAceptacion = documentoAceptacionService.getDocumentoAceptacionbyId(1, idEntregaMercaderia);
            OrdenCompra ordenCompra;
            if (documentoAceptacion.getNumeroOrdenCompra() != null) {
                //ordenCompra = ordenCompraService.getOrdenCompraById(documentoAceptacion.getIdOrdenCompra());
                ordenCompra = ordenCompraService.getNroOrdenCompraById(documentoAceptacion.getNumeroOrdenCompra());
            } else {
                throw new NullPointerException("El id del documento de aceptación no es válido.");
            }

            ParameterEntradaMercaderiaPdfDTO parameterEntradaMercaderiaPdfDTO = new ParameterEntradaMercaderiaPdfDTO();
            Proveedor proveedor = Optional
                    .ofNullable(proveedorService.getProveedorByRuc(documentoAceptacion.getProveedorRuc()))
                    .orElse(new Proveedor());

            parameterEntradaMercaderiaPdfDTO.setNroRuc(documentoAceptacion.getProveedorRuc());
            parameterEntradaMercaderiaPdfDTO.setNroGuia(documentoAceptacion.getNumeroDocumentoAceptacion());
            parameterEntradaMercaderiaPdfDTO.setRucCliente(ordenCompra.getInfoSociedad().getRuc());
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            parameterEntradaMercaderiaPdfDTO.setFechaEmision(formatter.format(documentoAceptacion.getFechaEmision()));
            parameterEntradaMercaderiaPdfDTO.setRazonSocialCliente(ordenCompra.getInfoSociedad().getRazonSocial());
            parameterEntradaMercaderiaPdfDTO.setDocumentoMaterial(documentoAceptacion.getNumeroDocumentoAceptacion());
            parameterEntradaMercaderiaPdfDTO.setDescripcionProveedor(documentoAceptacion.getProveedorRazonSocial());
            parameterEntradaMercaderiaPdfDTO.setUbicacionProveedor(proveedor.getDireccionFiscal() != null ? proveedor.getDireccionFiscal() : "");
            parameterEntradaMercaderiaPdfDTO.setTelefonoProveedor(proveedor.getTelefono() != null ? proveedor.getTelefono() : "-");

            List<FieldEntradaMercaderiaPdfDTO> fieldEntradaMercaderiaPdfList = new ArrayList<>();

            if (documentoAceptacion.getDocumentoAceptacionDetalleList() != null && documentoAceptacion.getDocumentoAceptacionDetalleList().size() > 0) {
                AtomicInteger index = new AtomicInteger();
                documentoAceptacion.getDocumentoAceptacionDetalleList().forEach(documentoAceptacionDetalle -> {
                    FieldEntradaMercaderiaPdfDTO fieldEntradaMercaderiaPdf = new FieldEntradaMercaderiaPdfDTO();
                    Integer nroItem = index.getAndIncrement() + 1 ;
                    fieldEntradaMercaderiaPdf.setNroItem(documentoAceptacionDetalle.getNumeroItem() != null ? documentoAceptacionDetalle.getNumeroItem().toString() : String.valueOf(nroItem) );
                    fieldEntradaMercaderiaPdf.setNroOC(documentoAceptacionDetalle.getNumeroOrdenCompra());
                    fieldEntradaMercaderiaPdf.setNroItemOC(documentoAceptacionDetalle.getPosicionOrdenCompra());
                    fieldEntradaMercaderiaPdf.setCodigoProducto(documentoAceptacionDetalle.getCodigoSapBienServicio().replaceFirst("^0+(?!$)", ""));
                    fieldEntradaMercaderiaPdf.setDescripcionProducto(documentoAceptacionDetalle.getDescripcionBienServicio());
                    fieldEntradaMercaderiaPdf.setCantAceptableCliente(documentoAceptacionDetalle.getCantidadAceptadaCliente().setScale(2, BigDecimal.ROUND_HALF_UP).toString());
                    fieldEntradaMercaderiaPdf.setUndMedida(documentoAceptacionDetalle.getUnidadMedida());
                    fieldEntradaMercaderiaPdf.setCantPedientePedido(documentoAceptacionDetalle.getCantidadPendiente() != null ? documentoAceptacionDetalle.getCantidadPendiente().setScale(2, BigDecimal.ROUND_HALF_UP).toString() :"0");
                    fieldEntradaMercaderiaPdf.setUndMedidaPedido(documentoAceptacionDetalle.getUnidadMedida());

                    fieldEntradaMercaderiaPdfList.add(fieldEntradaMercaderiaPdf);
                });
            }
            parameterEntradaMercaderiaPdfDTO.setFieldEntradaMercaderiaPdfDTOList(fieldEntradaMercaderiaPdfList);
            if (idTipoDoc == 3) {
                return documentoAceptacionService.getDevolucionesGenerateContent(parameterEntradaMercaderiaPdfDTO);
            } else {
                return documentoAceptacionService.getEntregaMercaderiaGenerateContent(parameterEntradaMercaderiaPdfDTO);
            }
        } catch (Exception e) {
            String error = Utils.obtieneMensajeErrorException(e);
            throw new RuntimeException(error);
        }
    }*/

    @GetMapping(value = "entregaMercaderiaPdf/{idDocumentoAceptacion}")
    public ResponseEntity<?> getEntregaMercaderiaPdf(@PathVariable(value = "idDocumentoAceptacion") Integer idEntregaMercaderia){
        if (idEntregaMercaderia == null || idEntregaMercaderia.equals(0))
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        try{
            DocumentoAceptacion documentoAceptacion = documentoAceptacionService.getDocumentoAceptacionbyId(1, idEntregaMercaderia);

            List<DocumentoAceptacionDetalle> documentoAceptacionDetalle = documentoAceptacionDetalleRepository.getByIdDocumentoAceptacion(idEntregaMercaderia);

            String nombre =  documentoAceptacion.getNumeroDocumentoAceptacion() + "_" + documentoAceptacion.getProveedorRuc();

            //String html = ordenCompraService.getOrdenCompraPdfContent(numeroOrdenCompra);
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + nombre + ".pdf");
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
            headers.add("Pragma", "no-cache");
            headers.add("Expires", "0");
            String htmlSource = documentoAceptacionService.getConformidadServicioGenerateContent(documentoAceptacion, documentoAceptacionDetalle);

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
            return new ResponseEntity<>(data, headers, HttpStatus.OK);
        } catch (Exception e) {
            String error = Utils.obtieneMensajeErrorException(e);
            throw new RuntimeException(error);
        }
    }

    /*@PostMapping(value = "conformidadServicioPdf/{idDocumentoAceptacion}")
    public String getConformidadServicioPdf(@PathVariable(value = "idDocumentoAceptacion") Integer idConformidadServicio) {
        try {
            DocumentoAceptacion documentoAceptacion = documentoAceptacionService.getDocumentoAceptacionbyId(2, idConformidadServicio);

            List<DocumentoAceptacionDetalle> documentoAceptacionDetalle = documentoAceptacionDetalleRepository.getByIdDocumentoAceptacion(documentoAceptacion.getId());
            
            return documentoAceptacionService.getConformidadServicioGenerateContent(documentoAceptacion, documentoAceptacionDetalle);
        } catch (Exception e) {
            String error = Utils.obtieneMensajeErrorException(e);
            throw new RuntimeException(error);
        }
    }*/

    @GetMapping(value = "conformidadServicioPdf/{idDocumentoAceptacion}")
    public ResponseEntity<?> getOrdenCompraPdfByNumero(@PathVariable("idDocumentoAceptacion") Integer idConformidadServicio){
        if (idConformidadServicio == null || idConformidadServicio.equals(0))
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        try{
            DocumentoAceptacion documentoAceptacion = documentoAceptacionService.getDocumentoAceptacionbyId(2, idConformidadServicio);

            List<DocumentoAceptacionDetalle> documentoAceptacionDetalle = documentoAceptacionDetalleRepository.getByIdDocumentoAceptacion(idConformidadServicio);

            String nombre =  documentoAceptacion.getNumeroDocumentoAceptacion() + "_" + documentoAceptacion.getProveedorRuc();

            //String html = ordenCompraService.getOrdenCompraPdfContent(numeroOrdenCompra);
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + nombre + ".pdf");
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
            headers.add("Pragma", "no-cache");
            headers.add("Expires", "0");
            String htmlSource = documentoAceptacionService.getConformidadServicioGenerateContent(documentoAceptacion, documentoAceptacionDetalle);

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


            return new ResponseEntity<>(data, headers, HttpStatus.OK);
        } catch (Exception e) {
            String error = Utils.obtieneMensajeErrorException(e);
            throw new RuntimeException(error);
        }
    }


    @RequestMapping(value = "/actaSustento/{idActaSustento}", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> getByActaSustento(@PathVariable("idActaSustento") Integer idActaSustento) throws Exception {

        Object response =this.documentoAceptacionService.getByActaSustento(idActaSustento);
        return Optional.of(response)
                .map(l -> new ResponseEntity<>(l, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NO_CONTENT));
    }


    @RequestMapping(value = "/actaSustento/proveedor/{idProveedor}", method = RequestMethod.GET, produces = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<?> getByArchivo(@PathVariable("idProveedor") Integer idProveedor) throws Exception {

        Object response =this.documentoAceptacionService.getByArchivoActaSustento(idProveedor);
        return Optional.of(response)
                .map(l -> new ResponseEntity<>(l, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NO_CONTENT));
    }

    @PostMapping(value = "/enviarEmailTest")
    public ResponseEntity<List<DocumentoAceptacion>> enviarEmailTest() {
        try {
            //List<DocumentoAceptacion> documentoAceptacionList = documentoAceptacionService.getDocumentoAceptacionList(bean);

            try {
                HtmlEmail htmlMail = new HtmlEmail();
                MailSetting mailSetting = this.parametroMapper.getMailSetting();
                htmlMail.setHostName(mailSetting.getHost());
                htmlMail.setSmtpPort(Integer.parseInt(mailSetting.getPort()));
                htmlMail.setAuthenticator(new DefaultAuthenticator(mailSetting.getUser(), mailSetting.getPassword()));
                htmlMail.addTo("aanton@growbiz.la");
                htmlMail.setFrom(mailSetting.getEmailFrom(), mailSetting.getNameFrom());
                htmlMail.setSubject("TEST EMAIL");
                htmlMail.setHtmlMsg("<body><strong>HOLA</strong><p>TEST</p></body>");
                htmlMail.setCharset("UTF-8");
                htmlMail.setStartTLSEnabled(true);
                htmlMail.setDebug(true);
                //htmlMail.setTLS(false);

                htmlMail.send();

                String mensaje = "Correcto";
                //respuesta = "Correo Publicacion OC enviado";
            //this.enviarCorreoSMTP(emailContacto, String.format(ASUNTO,_vel_NroOrdenCompra), body, this.parametroMapper.getMailSetting());
            } catch (Exception ex) {
                String mensaje = ex.getMessage();
                //respuesta = ex.getMessage();
            }

            /*if (documentoAceptacionList.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }*/
            
            return new ResponseEntity<>(null, HttpStatus.OK);

        } catch (Exception e) {
            String error = Utils.obtieneMensajeErrorException(e);
            throw new RuntimeException(error);
        }
    }
}

