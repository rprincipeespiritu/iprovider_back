package com.incloud.hcp.pdf.service.impl;

import com.incloud.hcp.domain.DocumentoAceptacion;
import com.incloud.hcp.domain.DocumentoAceptacionDetalle;
import com.incloud.hcp.domain.OrdenCompra;
import com.incloud.hcp.domain.OrdenCompraDetalle;
import com.incloud.hcp.domain.Proveedor;
import com.incloud.hcp.dto.ConstanciaDetraccionDetallePdfDto;
import com.incloud.hcp.jco.ordenCompra.dto.OrdenCompraPdfDto;
import com.incloud.hcp.pdf.bean.*;
import com.incloud.hcp.pdf.exception.PdfException;
import com.incloud.hcp.pdf.service.PdfGeneratorService;
import com.incloud.hcp.pdf.util.ResourceUtil;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class PdfGeneratorServiceJasperImpl extends GenericJasperGenerator implements PdfGeneratorService {

    private static PdfGeneratorService pdfService;
    private JasperReport jasperReport;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public static PdfGeneratorService getInstance(){
        synchronized (PdfGeneratorServiceJasperImpl.class){
            if(pdfService == null){
                pdfService =new PdfGeneratorServiceJasperImpl();
            }
            return pdfService;
        }
    }

    @Override
    public byte[] generateEntradaMercaderia(ParameterEntradaMercaderiaPdfDTO parameterEntradaMercaderiaPdfDTO) {
        try {
            EntregaMercaderiaMapBuilder entregaMercaderiaMapBuilder = EntregaMercaderiaMapBuilder.newEntradaMercaderiaMapBuilder(parameterEntradaMercaderiaPdfDTO);
            Map<String, Object> parametersEntregaMercaderia = entregaMercaderiaMapBuilder.buildParams();
            List<Map<String, Object>> fieldEntregaMercaderia = entregaMercaderiaMapBuilder.buildDetails();
            JRBeanCollectionDataSource fieldEntregaMercaderiaCollectionDataSource =new JRBeanCollectionDataSource(fieldEntregaMercaderia);
            this.loadTemplate("reportes/jasper/EntradaMercaderiaCFG.jasper");

            return this.build(jasperReport, parametersEntregaMercaderia, fieldEntregaMercaderiaCollectionDataSource);
        }catch (Exception e){
            logger.error(e.getMessage(), e.getCause());
            throw new PdfException(e.getMessage(),e.getCause());
        }
    }

    @Override
    public byte[] generateDevoluciones(ParameterEntradaMercaderiaPdfDTO parameterEntradaMercaderiaPdfDTO) {
        try {
            EntregaMercaderiaMapBuilder entregaMercaderiaMapBuilder = EntregaMercaderiaMapBuilder.newEntradaMercaderiaMapBuilder(parameterEntradaMercaderiaPdfDTO);
            Map<String, Object> parametersEntregaMercaderia = entregaMercaderiaMapBuilder.buildParams();
            List<Map<String, Object>> fieldEntregaMercaderia = entregaMercaderiaMapBuilder.buildDetails();
            JRBeanCollectionDataSource fieldEntregaMercaderiaCollectionDataSource =new JRBeanCollectionDataSource(fieldEntregaMercaderia);
            this.loadTemplate("reportes/jasper/DevolucionesCFG.jasper");

            return this.build(jasperReport, parametersEntregaMercaderia, fieldEntregaMercaderiaCollectionDataSource);
        }catch (Exception e){
            logger.error(e.getMessage(), e.getCause());
            throw new PdfException(e.getMessage(),e.getCause());
        }
    }

    @Override
    public byte[] generateConformidadServicio(ParameterConformidadServicioPdfDTO parameterConformidadServicioPdfDTO) {
        try {
            ConformidadServicioMapBuilder conformidadServicioMapBuilder = ConformidadServicioMapBuilder.newConformidadServicioMapBuilder(parameterConformidadServicioPdfDTO);
            Map<String, Object> parametersConformidadServicio = conformidadServicioMapBuilder.buildParams();
            List<Map<String, Object>> fieldConformidadServicio = conformidadServicioMapBuilder.buildDetails();
            JRBeanCollectionDataSource fieldConformidadServicioCollectionDataSource = new JRBeanCollectionDataSource(fieldConformidadServicio);
            this.loadTemplate("reportes/jasper/ConformidadServicioCFG.jasper");

            return this.build(jasperReport, parametersConformidadServicio, fieldConformidadServicioCollectionDataSource);
        }catch (Exception e){
            logger.error(e.getMessage(), e.getCause());
            throw new PdfException(e.getMessage(),e.getCause());
        }
    }

    @Override
    public String generateConformidadServicio(DocumentoAceptacion documentoAceptacion, List<DocumentoAceptacionDetalle> documentoAceptacionDetalle) throws Exception{

        try {
            String data = "word/template_doc_acept.html";
            //String data = new String(Files.readAllBytes(Paths.get("src/main/resources/word/PJ-BACKEND-3.html")));

            VelocityContext context = new VelocityContext();

            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

            //byte[] imageBytes = Files.readAllBytes(Paths.get("src/main/resources/logoCopeinca/JRC-Logo.jpg"));
            //String base64Image = Base64.getEncoder().encodeToString(imageBytes);
            //context.put("IMAGEN_BASE64", base64Image);
            context.put("FECHA", formatter.format(documentoAceptacion.getFechaEmision()));
            context.put("PROVEEDOR", documentoAceptacion.getProveedorRuc() + " - " + documentoAceptacion.getProveedorRazonSocial());
            context.put("FECHA_RECEPCION", formatter.format(documentoAceptacion.getFechaPublicacion()));
            context.put("NRO_OC", documentoAceptacion.getNumeroOrdenCompra());
            context.put("MONEDA", documentoAceptacion.getCodigoMoneda());
            

            String numeroRecepcion = (documentoAceptacion.getNumeroRecepcion() == null || documentoAceptacion.getNumeroRecepcion().trim().isEmpty()) ? "-" : documentoAceptacion.getNumeroRecepcion();
            String numeroLote = (documentoAceptacion.getNumeroLote() == null || documentoAceptacion.getNumeroLote().trim().isEmpty()) ? "-" : documentoAceptacion.getNumeroLote();
            String numeroGuia = (documentoAceptacion.getNumeroGuiaProveedor() == null || documentoAceptacion.getNumeroGuiaProveedor().trim().isEmpty()) ? "-" : documentoAceptacion.getNumeroGuiaProveedor();
            String Estado = (documentoAceptacion.getEntregaCompleta() == true) ? "Cerrado" : "Abierto";


            context.put("NRO_GUIA", numeroGuia);
            context.put("NRO_RECEPCION", numeroRecepcion);
            context.put("NRO_LOTE", numeroLote);
            context.put("ESTADO", Estado);
            /*Armamos detalle*/
           StringBuilder detallesx = new StringBuilder();
            AtomicInteger ind = new AtomicInteger(0); // Usamos AtomicInteger para manejar el índice

            documentoAceptacionDetalle.forEach(DA_detalle -> {
                int currentIndex = ind.incrementAndGet(); //Incrementa y obtiene el índice actual
                String htmlRow = 
                    "<td style=\"text-align: center; padding: 10px; font-family: Arial, sans-serif; font-size: 10px;\">" + DA_detalle.getCodigoSapBienServicio() + "</td>" +
                    "<td style=\"text-align: center; padding: 10px; font-family: Arial, sans-serif; font-size: 10px;\">" + escapeHtml(DA_detalle.getDescripcionBienServicio()) + "</td>" +
                    "<td style=\"text-align: center; padding: 10px; font-family: Arial, sans-serif; font-size: 10px;\">" + DA_detalle.getUnidadMedida() + "</td>" +
                    "<td style=\"text-align: center; padding: 10px; font-family: Arial, sans-serif; font-size: 10px;\">" + DA_detalle.getCantidadAceptadaCliente().setScale(2, BigDecimal.ROUND_HALF_UP) + "</td>";
                
                detallesx.append("<tr>").append(htmlRow).append("</tr>"); // Añadimos filas <tr> para estructurar la tabla
            });

            // Convierte el contenido a cadena si lo necesitas
            String detallesHtml = detallesx.toString();
            context.put("TRS", detallesHtml);

        
            String content = Optional.ofNullable(data)
                    .map(url -> url + "")
                    .map(template -> {
                        int i = 0;
                        return getContentMail(context, template);
                    })
                    .orElse("");
            System.out.println("content");

            //String base64Content = encodeToBase64(content);
            //return base64Content;

            return content;

            
        } catch (Exception e) {
            return "";
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
    public byte[] generateOrdenCompraPdfBytes(OrdenCompraPdfDto ordenCompraPdfDto) {
        try {
            OrdenCompraMapBuilder ordenCompraMapBuilder = OrdenCompraMapBuilder.newOrdenCompraMapBuilder(ordenCompraPdfDto);
            Map<String, Object> parametersOrdenCompra = ordenCompraMapBuilder.buildParams();
//            List<Map<String, Object>> fieldPosicionList = ordenCompraMapBuilder.buildDetails();
//            JRBeanCollectionDataSource fieldPosicionListCollectionDataSource = new JRBeanCollectionDataSource(fieldPosicionList);
            this.loadTemplate("reportes/jasper/OrdenCompraCFG.jasper");

            return this.build(jasperReport, parametersOrdenCompra, null);
        }catch (Exception e){
            logger.error(e.getMessage(), e.getCause());
            throw new PdfException(e.getMessage(),e.getCause());
        }
    }

    @Override
    public byte[] generateContratoMarcoPdfBytes(OrdenCompraPdfDto ordenCompraPdfDto) {
        try {
            OrdenCompraMapBuilder ordenCompraMapBuilder = OrdenCompraMapBuilder.newOrdenCompraMapBuilder(ordenCompraPdfDto);
            Map<String, Object> parametersContratoMarco = ordenCompraMapBuilder.buildParams();
            this.loadTemplate("reportes/jasper/ContratoMarcoCFG.jasper");

            return this.build(jasperReport, parametersContratoMarco, null);
        }catch (Exception e){
            logger.error(e.getMessage(), e.getCause());
            throw new PdfException(e.getMessage(),e.getCause());
        }
    }

    @Override
    public byte[] generatePrefacturaPdfBytes(PrefacturaPdfDto prefacturaPdfDto) {
        try {
            PrefacturaMapBuilder prefacturaMapBuilder = PrefacturaMapBuilder.newPrefacturaMapBuilder(prefacturaPdfDto);
            Map<String, Object> parametersPrefactura = prefacturaMapBuilder.buildParams();

            this.loadTemplate("reportes/jasper/PrefacturaCFG.jasper");

            return this.build(jasperReport, parametersPrefactura, null);
        }catch (Exception e){
            logger.error(e.getMessage(), e.getCause());
            throw new PdfException(e.getMessage(),e.getCause());
        }
    }


    @Override
    public byte[] generateComprobanteRetencion(ParameterComprobanteRetencionPdfDTO parameterComprobanteRetencionPdfDTO) {
        try {
            ComprobanteRetencionMapBuilder comprobanteRetencionMapBuilder = ComprobanteRetencionMapBuilder.newComprobanteRetencionMapBuilder(parameterComprobanteRetencionPdfDTO);
            Map<String, Object> parametersComprobanteRetencion = comprobanteRetencionMapBuilder.buildParams();
            List<Map<String, Object>> fieldsComprobanteRetencion = comprobanteRetencionMapBuilder.buildDetails();
            JRBeanCollectionDataSource fieldsComprobanteRetencionCollectionDataSource = new JRBeanCollectionDataSource(fieldsComprobanteRetencion);
            this.loadTemplate("reportes/jasper/CopeincaRetentionEs-20.jasper");

            return this.build(jasperReport, parametersComprobanteRetencion, fieldsComprobanteRetencionCollectionDataSource);
        }catch (Exception e){
            logger.error(e.getMessage(), e.getCause());
            throw new PdfException(e.getMessage(),e.getCause());
        }
    }

    @Override
    public byte[] generateConstanciaDetraccion(ConstanciaDetraccionDetallePdfDto constanciaDetraccionDetallePdfDto) {
        try{
            ConstanciaDetraccionMapBuilder constanciaDetraccionMapBuilder = ConstanciaDetraccionMapBuilder.newConstanciaDetraccionMapBuilder(constanciaDetraccionDetallePdfDto);
            Map<String,Object> parametersContanciaDetraccion = constanciaDetraccionMapBuilder.buildParams();
            this.loadTemplate("reportes/jasper/ReportDetraccion.jasper");

            return this.build(jasperReport, parametersContanciaDetraccion, null);


        }catch (Exception e){
            logger.error(e.getMessage(), e.getCause());
            throw new PdfException(e.getMessage(),e.getCause());
        }
    }

    private void loadTemplate(String templateFile){
        InputStream resourceAsStream = null;
        try {
            resourceAsStream = ResourceUtil.getResourceStream(PdfGeneratorServiceJasperImpl.class, templateFile);
            jasperReport  = (JasperReport) JRLoader.loadObject(resourceAsStream);
        } catch (JRException | FileNotFoundException e){
            logger.error(e.getMessage(), e.getCause());
            throw new PdfException("Error when loading PDF template: " + e.getMessage(), e.getCause());
        } finally {
            if(resourceAsStream != null){
                try{
                    resourceAsStream.close();
                }catch (IOException e){
                    logger.error("Error when close Stream:" + e.getMessage(), e.getCause());
                }
            }
        }
    }
    private String escapeHtml(String input) {
        if (input == null) return "";
        return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\\", "&#92;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }


}
