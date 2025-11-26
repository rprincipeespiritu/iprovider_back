package com.incloud.hcp.service.notificacion;

import com.incloud.hcp.domain.*;
import com.incloud.hcp.enums.OrdenCompraEstadoEnum;
import com.incloud.hcp.repository.SociedadRepository;
import com.incloud.hcp.service.DocumentoAceptacionService;
import com.incloud.hcp.util.StrUtils;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.HtmlEmail;
import org.apache.velocity.VelocityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Component
public class ContactoAprobadaRechazadaOCNotificacion extends NotificarMail {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final String LOGO_HEADER = "com/incloud/hcp/templates/img/header.png";
    private final String LOGO_FOOTER = "com/incloud/hcp/templates/img/jrclogo.jpg";
    private final String TEMPLATE = "com/incloud/hcp/templates/portal/TmpCompradorAprobadaRechazadaOrdenCompra.html";
    private static final String MENSAJE_RECHAZO = "Motivo: %s";
    private static final String ASUNTO = "%s de Orden de Compra N° %s";

    @Value("${cfg.portal.url}")
    private String urlPortal;

    @Value("${cfg.portal.url.centenario}")
    private String urlPortalCentenario;

    @Value("${cfg.notificacion.consulta.url}")
    private String urlConsulta;

    @Autowired
    private SociedadRepository sociedadRepository;

    @Autowired
    @Lazy
    private DocumentoAceptacionService documentoAceptacionService;


    public String enviar(MailSetting mailSetting, OrdenCompra ordenCompra, Usuario proveedor, Usuario comprador){
//        Sociedad sociedad = sociedadRepository.getByCodigoSociedad(ordenCompra.getSociedad());
//        String mensajeEstado = "la aprobación";
        String asunto = "";
        String accion = "";
//        String _vel_NroOrdenCompra = ordenCompra.getNumeroOrdenCompra();
//
//        String tipoDestinatario = "";
        String emailContacto = "";
        String mensajeRechazo = "";
        String nombreDestinatario = "";
//        String _vel_TextoEmpresa = "";
        String urlLink = "";

        HtmlEmail htmlMail = new HtmlEmail();

        if (proveedor == null && comprador != null) { // email a comprador (cliente)
//            tipoDestinatario = "comprador";
            emailContacto = comprador.getEmail();
            nombreDestinatario = ordenCompra.getProveedorRazonSocial();
//            _vel_TextoEmpresa = "para la empresa proveedora " + ordenCompra.getProveedorRazonSocial();
        }
        else if(proveedor != null && comprador == null){ // email a responsable de compras (proveedor)
//            tipoDestinatario = "proveedor";
            emailContacto = proveedor.getEmail();
            nombreDestinatario = proveedor.getApellido();
//            _vel_TextoEmpresa = "de la empresa " + sociedad.getRazonSocial();
        } else {
            String mensaje = "Error en los datos de proveedor/comprador para el envio de correo de Publicacion de OC: " + ordenCompra.getNumeroOrdenCompra();
            logger.error(mensaje);
            return mensaje;
        }

        if(OrdenCompraEstadoEnum.RECHAZADA.getId() == ordenCompra.getIdEstadoOrdenCompra().intValue()){
            mensajeRechazo = String.format(MENSAJE_RECHAZO, StrUtils.escapeComma(ordenCompra.getMotivoRechazo()));
            accion = "rechazada";
            asunto = "Rechazo de Orden de Compra N° " + ordenCompra.getNumeroOrdenCompra();
        } else {
            mensajeRechazo = "";
            accion = "aprobada";
            asunto = "Aprobación de Orden de Compra N° " + ordenCompra.getNumeroOrdenCompra();
        }

        ////////////////////// BEGIN METODO VELPA //////////////////////

//        String body = "<!DOCTYPE html>";
//        body = body + "<html lang='en'>";
//        body = body + "<head>";
//        body = body + "<meta http-equiv='Content-Type' content='text/html; charset=UTF-8'/>";
//        body = body + "</head>";
//        body = body + "<body style='width: 600px;margin: auto;padding: 0;font-family: Arial;font-size: 14px;color: #333'>";
//
//        body = body + "<table cellspacing='0' cellpadding='0' width='600px'>";
//        body = body + "<tr style='border-collapse: collapse;width: 600px;padding: 0;margin: 0'>";
//        body = body + "<td class='main_first' ";
//        body = body + "style='border-collapse: collapse;width: 600px;padding: 0;margin: 0;padding-top: 12px;padding-bottom: 12px'>";
//        body = body + "<h1 style='font-family: Arial;font-weight: bold;font-size: 16px;color: #555;margin-bottom: 24px'>";
//        body = body + "Estimados Señores:<br/>";
//        body = body + nombreDestinatario + "</h1>";
//        body = body + "<p style='font-family: Arial;font-size: 14px;line-height: 18px'>";
//        body = body + "La Orden de Compra N° " + _vel_NroOrdenCompra + " " + _vel_TextoEmpresa;
//        body = body + " ha sido " + _vel_accion + " exitosamente en el Portal de IProvider Topitop.<br/>";
//        body = body + _vel_mensajeRechazo;
//        body = body + "</p>";
//        body = body + "</td>";
//        body = body + "</tr>";
//        body = body + "</table>";
//
//        body = body + "<table cellpadding='0' cellspacing='0' width='600px'>";
//        body = body + "<tr style='border-collapse: collapse;width: 600px;padding: 0;margin: 0'>";
//        body = body + "<td class='colophon' ";
//        body = body + "style='border-collapse: collapse;width: 600px;padding: 0;margin: 0;font-size: 11px;color: #888;line-height: 13px'>";
//        body = body + "<p style='font-family: Arial;font-size: 12px;line-height: 13px;color: #888'>";
//        body = body + "AGRADECEREMOS NO RESPONDER ESTE CORREO. SI LO DESEA ENVIE SU CONSULTA A:<br/>";
//        body = body + urlConsulta;
//        body = body + "</p>";
//        body = body + "</td>";
//        body = body + "</tr>";
//        body = body + "</table>";
//
//        body = body + "<table cellspacing='0' cellpadding='0' width='600px'>";
//        body = body + "<tr style='border-collapse: collapse;width: 600px;padding: 0;margin: 0'>";
//        body = body + "<td class='main_last' ";
//        body = body + "style='border-collapse: collapse;width: 600px;padding: 0;margin: 0;padding-top: 12px;padding-bottom: 36px'>";
//        body = body + "<p style='font-family: Arial;font-size: 14px;line-height: 18px'>";
//        body = body + "Atentamente&#44;<br/>";
//        body = body + "IProvider CFG - Topitop<br/>";
//        body = body + "<img align='LEFT' width='148' height='46' src='cid:"+cid+"' />";
//        body = body + "</p>";
//        body = body + "</td>";
//        body = body + "</tr>";
//        body = body + "</table>";
//
//        body = body + "</body>";
//        body = body + "</html>";

        //////////////////////  END  METODO VELPA //////////////////////

        try{
            VelocityContext context = new VelocityContext();

            String LOGO_H = generateCidResourceUtilsNuevo(htmlMail, LOGO_HEADER,"header");

            String LOGO_FOOT = generateCidResourceUtilsNuevo(htmlMail, LOGO_FOOTER,"footer");

            context.put("nombreProveedor", nombreDestinatario);
            //context.put("vendor_header", LOGO_H);
            context.put("vendor_footer", LOGO_FOOT);
            context.put("nroOrdenCompra", ordenCompra.getNumeroOrdenCompra());
            context.put("estadoOrdenCompra", accion);
            context.put("mensajeRechazo", mensajeRechazo);

            String content = Optional.ofNullable(TEMPLATE)
                    .map(url -> url + "")
                    .map(template -> {
                        int i = 0;
                        return getContentMail(context, template);
                    })
                    .orElse("");

            htmlMail.setHostName(mailSetting.getHost());
            htmlMail.setSmtpPort(Integer.parseInt(mailSetting.getPort()));
            htmlMail.setAuthenticator(new DefaultAuthenticator(mailSetting.getUser(), mailSetting.getPassword()));
            htmlMail.addTo(emailContacto);
            htmlMail.setFrom(mailSetting.getEmailFrom(), mailSetting.getNameFrom());
            htmlMail.setSubject(asunto);
            htmlMail.setHtmlMsg(content);
            htmlMail.setCharset("UTF-8");
            htmlMail.setStartTLSEnabled(true);
            htmlMail.setDebug(true);
            htmlMail.setTLS(true);

            htmlMail.send();
//            this.enviarCorreoSap(emailContacto,String.format(ASUNTO,subAsunto,_vel_NroOrdenCompra),body);
        }catch (Exception ex){
            logger.error("Error al enviar el Correo de " + accion + " de la orden de compra " + ordenCompra.getNumeroOrdenCompra(), ex);
        }
        return "Se envio correctamente el correo de " + accion + " de la orden de compra " + ordenCompra.getNumeroOrdenCompra() + " al " + emailContacto;
    }

    public String enviarDocumentoAceptacion(String tipoDocAceptacion, MailSetting mailSetting, DocumentoAceptacion documentoAceptacion, Usuario proveedor, Usuario comprador, List<DocumentoAceptacionDetalle> detalleDocumentoAceptacion){
        //        Sociedad sociedad = sociedadRepository.getByCodigoSociedad(ordenCompra.getSociedad());
        //        String mensajeEstado = "la aprobación";
                String templates = "";
                String asunto = "";
                String accion = "";
        //        String _vel_NroOrdenCompra = ordenCompra.getNumeroOrdenCompra();
        //
        //        String tipoDestinatario = "";
                String emailContacto = "";
                String mensajeRechazo = "";
                String nombreDestinatario = "";
        //        String _vel_TextoEmpresa = "";
                String urlLink = "";
        
                HtmlEmail htmlMail = new HtmlEmail();
                String TipoDocumento = "";//documentoAceptacion.getTipoDocumentoAceptacion().getDescripcion();  
                if(tipoDocAceptacion.equals("EM")){
                    TipoDocumento = "ENTRADA DE MERCADERÍA";
                    asunto = "PUBLICACIÓN DE ENTRADA DE MERCADERÍA";
                    templates = "com/incloud/hcp/templates/portal/TmpPublicacionEM.html";
                }else{
                    TipoDocumento = "HES";
                    asunto = "PUBLICACIÓN DE HES";
                    templates = "com/incloud/hcp/templates/portal/TmpPublicacionHSE.html";
                }
                
                String NroDocumento = documentoAceptacion.getNumeroDocumentoAceptacion();

                if (proveedor == null && comprador != null) { // email a comprador (cliente)
                //            tipoDestinatario = "comprador";
                            emailContacto = comprador.getEmail();
                            nombreDestinatario = documentoAceptacion.getProveedorRazonSocial();
                //            _vel_TextoEmpresa = "para la empresa proveedora " + ordenCompra.getProveedorRazonSocial();
                }
                else if(proveedor != null && comprador == null){ // email a responsable de compras (proveedor)
                //            tipoDestinatario = "proveedor";
                            emailContacto = proveedor.getEmail();
                            nombreDestinatario = proveedor.getApellido();
                //            _vel_TextoEmpresa = "de la empresa " + sociedad.getRazonSocial();
                } else {
                            String mensaje = "Error en los datos de proveedor/comprador para el envio de correo de Publicacion de OC: " + documentoAceptacion.getNumeroOrdenCompra();
                            logger.error(mensaje);
                            return mensaje;
                }
                        
                try{
                    VelocityContext context = new VelocityContext();
        
                    String LOGO_H = generateCidResourceUtilsNuevo(htmlMail, LOGO_HEADER,"header");
        
                    String LOGO_FOOT = generateCidResourceUtilsNuevo(htmlMail, LOGO_FOOTER,"footer");
        
                    context.put("nombreProveedor", nombreDestinatario);
                    //context.put("vendor_header", LOGO_H);
                    context.put("vendor_footer", LOGO_FOOT);
                    context.put("TipoDocumento", TipoDocumento);
                    context.put("NroDocumento", NroDocumento);
                    context.put("urlportal", urlPortal);
        
                    String content = Optional.ofNullable(templates)
                            .map(url -> url + "")
                            .map(template -> {
                                int i = 0;
                                return getContentMail(context, template);
                            })
                            .orElse("");
        
                    htmlMail.setHostName(mailSetting.getHost());
                    htmlMail.setSmtpPort(Integer.parseInt(mailSetting.getPort()));
                    htmlMail.setAuthenticator(new DefaultAuthenticator(mailSetting.getUser(), mailSetting.getPassword()));
                    htmlMail.addTo(emailContacto);
                    htmlMail.setFrom(mailSetting.getEmailFrom(), mailSetting.getNameFrom());
                    htmlMail.setSubject(asunto);
                    htmlMail.setHtmlMsg(content);
                    htmlMail.setCharset("UTF-8");
                    htmlMail.setStartTLSEnabled(true);
                    htmlMail.setDebug(true);
                    htmlMail.setTLS(true);



                    //implementacion de adjunto
                    if(detalleDocumentoAceptacion != null){
                        String htmlSource = documentoAceptacionService.getConformidadServicioGenerateContent(documentoAceptacion, detalleDocumentoAceptacion);
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
                        byte[] pdfBytesOc = Files.readAllBytes(path);

                        DataSource dataSource = new ByteArrayDataSource(pdfBytesOc, "application/pdf");
                        String nombreArchivo = documentoAceptacion.getNumeroDocumentoAceptacion() + ".pdf";
                        htmlMail.attach(dataSource, nombreArchivo, documentoAceptacion.getNumeroDocumentoAceptacion());

                    }
        
                    htmlMail.send();
        //            this.enviarCorreoSap(emailContacto,String.format(ASUNTO,subAsunto,_vel_NroOrdenCompra),body);
                }catch (Exception ex){
                    logger.error("Error al enviar el Correo de " + accion + " del Documento de Aceptación " + NroDocumento, ex);
                }
                return "Se envio correctamente el correo de " + accion + " del Documento de Aceptación " + NroDocumento + " al " + emailContacto;
            }
}