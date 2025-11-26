package com.incloud.hcp.service.notificacion;

import com.incloud.hcp.domain.OrdenCompra;
import com.incloud.hcp.domain.OrdenCompraDetalle;
import com.incloud.hcp.domain.Sociedad;
import com.incloud.hcp.domain.Usuario;
import com.incloud.hcp.jco.ordenCompra.service.JCOOrdenCompraPdfService;
import com.incloud.hcp.myibatis.mapper.ParametroMapper;
import com.incloud.hcp.repository.SociedadRepository;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import com.itextpdf.text.DocumentException;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.HtmlEmail;
import org.apache.velocity.VelocityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;

@Component
public class ContactoPublicadaOCNotificacion extends NotificarMail {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final String LOGO = "com/incloud/hcp/templates/img/jrclogo.jpg";
    private final String LOGO_VENDOR = "com/incloud/hcp/image/vendor_logo.png";
    private final String LOGO_CFG = "com/incloud/hcp/image/cfg_logo.png";
    private final String TEMPLATE = "com/incloud/hcp/templates/portal/TmpPublicacionOC.html";
    private final String TEMPLATEMOD = "com/incloud/hcp/templates/portal/TmpPublicacionModOC.html";
    private final String TEMPLATEDOCADJUNTO = "com/incloud/hcp/templates/portal/TmpAdjuntosOcCreacion.html";
    private static final String ASUNTO = "Publicación de Orden de Compra N° %s";
    private static final String ASUNTODOCSOPORTES = "Creacion de documentos soporte %s";
    private final String LOGO_HEADER = "com/incloud/hcp/templates/img/header.png";
    private final String LOGO_CALL = "com/incloud/hcp/templates/img/call-to-action.png";
    private final String LOGO_FOOTER = "com/incloud/hcp/templates/img/jrclogo.jpg";

    @Autowired
    private JCOOrdenCompraPdfService jcoOrdenCompraPdfService;

    @Value("${cfg.portal.url}")
    private String urlPortalProveedor;

    @Value("${cfg.portal.url.centenario}")
    private String urlPortalCentenario;

    @Value("${cfg.portal.url.public.oc}")
    private String urlPortalPublicacionOC;

    @Value("${cfg.notificacion.consulta.url}")
    private String urlConsulta;


    @Autowired
    private SociedadRepository sociedadRepository;

    @Autowired
    private ParametroMapper parametroMapper;


    public String enviar(OrdenCompra ordenCompra, Usuario proveedor, Usuario comprador, List<OrdenCompraDetalle> ordenCompraDetalles) {
        //Sociedad sociedad = sociedadRepository.getByCodigoSociedad(ordenCompra.getSociedad());
        Sociedad sociedad = new Sociedad();
        sociedad.setRazonSocial(""); // temporal

        String emailContacto = "";
        String respuesta = "";
        ////////////////////// VARIABLES METODO VELPA //////////////////////

        String _vel_Contacto = "";
        String _vel_NombreDestinatario = "";
        String _vel_TextoEmpresa = "";
        String _vel_NroOrdenCompra = ordenCompra.getNumeroOrdenCompra();
        String _vel_UrlPublicacionOC = urlPortalPublicacionOC;
        String urlPortal = "";

        HtmlEmail htmlMail = new HtmlEmail();

        // Insertar la imagen y obtener el ID de contenido
        //String cid = this.generateCidResourceUtils(htmlMail, LOGO);
        //cid = cid == null ? generateCidFromUrlExt(htmlMail) : cid;
        //cid = generateCidResourceUtils(htmlMail, LOGO);
        //logger.error("El nuevo cid: " + cid);

        ////////////////////// VARIABLES METODO VELPA //////////////////////

        if (proveedor == null && comprador != null) { // email a comprador (cliente)
            emailContacto = comprador.getEmail();
            _vel_Contacto = comprador.getNombre() + " " + comprador.getApellido();
            _vel_NombreDestinatario = comprador.getNombre() + " " + comprador.getApellido();
            _vel_TextoEmpresa = "para la empresa proveedora " + ordenCompra.getProveedorRazonSocial();
            //sociedad.setRazonSocial(comprador.getNombre() + " " + comprador.getApellido());
            urlPortal = urlPortalCentenario;
        }
        else if(proveedor != null && comprador == null){ // email a responsable de compras (proveedor)
//            emailContacto = proveedor.getEmailPersonaCompra();
//            _vel_Contacto = proveedor.getNombrePersonaCompra();
            emailContacto = proveedor.getEmail();
            _vel_Contacto = proveedor.getApellido();
            _vel_NombreDestinatario = ordenCompra.getProveedorRazonSocial();
            _vel_TextoEmpresa = "de la empresa " + sociedad.getRazonSocial();
            urlPortal = urlPortalProveedor;
        }
        else {
            logger.error("Error en los datos de proveedor/comprador para el envio de correo de Publicacion de OC: " + ordenCompra.getNumeroOrdenCompra());
            return "Error en los datos de proveedor/comprador para el envio de correo de Publicacion de OC";
        }

        ////////////////////// BEGIN METODO VELPA //////////////////////
        /*
        String body = "<!DOCTYPE html>";
        body = body + "<html lang='en'>";
        body = body + "<head>";
        body = body + "<meta http-equiv='Content-Type' content='text/html; charset=UTF-8'/>";
        body = body + "</head>";
        body = body + "<body style='width: 600px;margin: auto;padding: 0;font-family: Arial&#44; Helvetica&#44; sans-serif;font-size: 14px;color: #333'>";

        body = body + "<table cellspacing='0' cellpadding='0' width='600px'>";
        body = body + "<tr style='border-collapse: collapse;width: 600px;padding: 0;margin: 0'>";
        body = body + "<td class='main_first'";
        body = body + "style='border-collapse: collapse;width: 600px;padding: 0;margin: 0;padding-top: 12px;padding-bottom: 12px'>";
        body = body + "<h1 style='font-family: Arial&#44; Helvetica&#44; sans-serif;font-weight: bold;font-size: 16px;color: #555;margin-bottom: 24px'>";
        body = body + "Estimados Señores:<br/>";
        body = body + _vel_NombreDestinatario + "</h1>";
        body = body + "<p style='font-family: Arial&#44; Helvetica&#44; sans-serif;font-size: 14px;line-height: 18px'>";
        body = body + "La Orden de Compra N° " + _vel_NroOrdenCompra + " " + _vel_TextoEmpresa;
        body = body + " ha sido publicada satisfactoriamente en el Portal de IProvider JRC.<br/>";
        body = body + "</p>";
        body = body + "<p style='font-family: Arial&#44; Helvetica&#44; sans-serif;font-size: 14px;line-height: 18px'>";
        body = body + "Para visualizar la OC creada puede ingresar a la siguiente dirección:<br/>";
        body = body + "<a href=\"" + _vel_UrlPublicacionOC + "\">Publicación de Ordenes de Compra</a><br/><br/>";
        body = body + "*Nota: En caso de presentar problemas con su inicio de sesión, copie y pegue el siguiente enlace en un nuevo navegador en modo incógnito:<br/>";
        body = body + "<a href='" + urlPortal + "'>" + urlPortal + "</a><br/>";
        body = body + "</p>";
        body = body + "</td>";
        body = body + "</tr>";
        body = body + "</table>";

        body = body + "<table cellpadding='0' cellspacing='0' width='600px'>";
        body = body + "<tr style='border-collapse: collapse;width: 600px;padding: 0;margin: 0'>";
        body = body + "<td class='colophon' ";
        body = body + "style='border-collapse: collapse;width: 600px;padding: 0;margin: 0;font-size: 11px;color: #888;line-height: 13px'>";
        body = body + "<p style='font-family: Arial&#44; Helvetica&#44; sans-serif;font-size: 12px;line-height: 13px;color: #888'>";
        body = body + "AGRADECEREMOS NO RESPONDER ESTE CORREO. SI LO DESEA ENVIE SU CONSULTA A:<br/>";
        body = body + urlConsulta;
        body = body + "</p>";
        body = body + "</td>";
        body = body + "</tr>";
        body = body + "</table>";

        body = body + "<table cellspacing='0' cellpadding='0' width='600px'>";
        body = body + "<tr style='border-collapse: collapse;width: 600px;padding: 0;margin: 0'>";
        body = body + "<td class='main_last' ";
        body = body + "style='border-collapse: collapse;width: 600px;padding: 0;margin: 0;padding-top: 12px;padding-bottom: 36px'>";
        body = body + "<p style='font-family: Arial&#44; Helvetica&#44; sans-serif;font-size: 14px;line-height: 18px'>";
        body = body + "Atentamente&#44;<br/>";
        body = body + "IProvider - JRC<br/>";
        body = body + "<img align='LEFT' width='148' height='46' src='cid:"+cid+"' />";
        body = body + "</p>";
        body = body + "</td>";
        body = body + "</tr>";
        body = body + "</table>";

        body = body + "</body>";
        body = body + "</html>"; */

        //////////////////////  END  METODO VELPA //////////////////////////

        try {
            
            VelocityContext context = new VelocityContext();

            String LOGO_H = generateCidResourceUtilsNuevo(htmlMail, LOGO_HEADER,"header");

            String LOGO_CAL = generateCidResourceUtilsNuevo(htmlMail, LOGO_CALL,"call");

            String LOGO_FOOT = generateCidResourceUtilsNuevo(htmlMail, LOGO_FOOTER,"footer");


            context.put("nombreProveedor", _vel_NombreDestinatario);
            //context.put("vendor_header", LOGO_H);
            //context.put("vendor_call_action",LOGO_CAL );
            context.put("vendor_footer", LOGO_FOOT);
            context.put("urlportal", urlPortal);
            context.put("nroOrdenCompra", _vel_NroOrdenCompra);

            String content = Optional.ofNullable(TEMPLATE)
                    .map(url -> url + "")
                    .map(template -> {
                        int i = 0;
                        return getContentMail(context, template);
                    })
                    .orElse("");

            MailSetting mailSetting = this.parametroMapper.getMailSetting();
            htmlMail.setHostName(mailSetting.getHost());
            htmlMail.setSmtpPort(Integer.parseInt(mailSetting.getPort()));
            htmlMail.setAuthenticator(new DefaultAuthenticator(mailSetting.getUser(), mailSetting.getPassword()));
            htmlMail.addTo(emailContacto);
            htmlMail.setFrom(mailSetting.getEmailFrom(), mailSetting.getNameFrom());
            htmlMail.setSubject(String.format(ASUNTO,_vel_NroOrdenCompra));
            htmlMail.setHtmlMsg(content);
            htmlMail.setCharset("UTF-8");
            htmlMail.setStartTLSEnabled(true);
            htmlMail.setDebug(true);
            htmlMail.setTLS(true);

            //implementacion de adjunto
            if(ordenCompraDetalles != null){
                String pdfBase64Oc = jcoOrdenCompraPdfService.obtenerBase64OCAdjuntoEmail(ordenCompra, ordenCompraDetalles);
                byte[] pdfBytesOc = this.converterToPdfBase64(pdfBase64Oc);

                DataSource dataSource = new ByteArrayDataSource(pdfBytesOc, "application/pdf");
                String nombreArchivo = ordenCompra.getNumeroOrdenCompra() + ".pdf";
                htmlMail.attach(dataSource, nombreArchivo, ordenCompra.getNumeroOrdenCompra());

            }

            htmlMail.send();
            respuesta = "Correo Publicacion OC enviado";
            //this.enviarCorreoSMTP(emailContacto, String.format(ASUNTO,_vel_NroOrdenCompra), body, this.parametroMapper.getMailSetting());
        } catch (Exception ex) {
            logger.error("Error al enviar Correo por Publicacion OC a " + emailContacto, ex);
            respuesta = ex.getMessage();
        }
        return respuesta;
    }

    public String enviarCreacionDocAdjuntos(OrdenCompra ordenCompra, Usuario comprador, String tipo) {
        //Sociedad sociedad = sociedadRepository.getByCodigoSociedad(ordenCompra.getSociedad());
        Sociedad sociedad = new Sociedad();
        sociedad.setRazonSocial(""); // temporal

        String emailContacto = "";
        String respuesta = "";
        ////////////////////// VARIABLES METODO VELPA //////////////////////

        String _vel_Contacto = "";
        String _vel_NombreDestinatario = "";
        String _vel_TextoEmpresa = "";
        String _vel_NroOrdenCompra = "Le informamos que se han "+ tipo + " los documentos soportes para la ORDEN DE COMPRA Nro. "+ordenCompra.getNumeroOrdenCompra() +". Puede visualizarlos ingresando con su cuenta al portal por medio del siguiente enlace:";
        String _vel_UrlPublicacionOC = urlPortalPublicacionOC;
        String urlPortal = "";

        HtmlEmail htmlMail = new HtmlEmail();



        ////////////////////// VARIABLES METODO VELPA //////////////////////

        // email a comprador (cliente)
            emailContacto = comprador.getEmail();
            _vel_Contacto = comprador.getNombre() + " " + comprador.getApellido();
            _vel_NombreDestinatario = comprador.getNombre() + " " + comprador.getApellido();
            _vel_TextoEmpresa = "para la empresa proveedora " + ordenCompra.getProveedorRazonSocial();
            //sociedad.setRazonSocial(comprador.getNombre() + " " + comprador.getApellido());
            urlPortal = urlPortalCentenario;

        try {

            VelocityContext context = new VelocityContext();

            String LOGO_H = generateCidResourceUtilsNuevo(htmlMail, LOGO_HEADER,"header");

            String LOGO_CAL = generateCidResourceUtilsNuevo(htmlMail, LOGO_CALL,"call");

            String LOGO_FOOT = generateCidResourceUtilsNuevo(htmlMail, LOGO_FOOTER,"footer");


            context.put("nombreProveedor", _vel_NombreDestinatario);
            //context.put("vendor_header", LOGO_H);
            //context.put("vendor_call_action",LOGO_CAL );
            context.put("vendor_footer", LOGO_FOOT);
            context.put("urlportal", urlPortal);
            context.put("nroOrdenCompra", _vel_NroOrdenCompra);

            String content = Optional.ofNullable(TEMPLATEDOCADJUNTO)
                    .map(url -> url + "")
                    .map(template -> {
                        int i = 0;
                        return getContentMail(context, template);
                    })
                    .orElse("");

            MailSetting mailSetting = this.parametroMapper.getMailSetting();
            htmlMail.setHostName(mailSetting.getHost());
            htmlMail.setSmtpPort(Integer.parseInt(mailSetting.getPort()));
            htmlMail.setAuthenticator(new DefaultAuthenticator(mailSetting.getUser(), mailSetting.getPassword()));
            htmlMail.addTo(emailContacto);
            htmlMail.setFrom(mailSetting.getEmailFrom(), mailSetting.getNameFrom());
            htmlMail.setSubject(String.format(ASUNTODOCSOPORTES,ordenCompra.getNumeroOrdenCompra()));
            htmlMail.setHtmlMsg(content);
            htmlMail.setCharset("UTF-8");
            htmlMail.setStartTLSEnabled(true);
            htmlMail.setDebug(true);
            htmlMail.setTLS(true);

              htmlMail.send();
            respuesta = "Correo creacion documentos soporte OC enviado";
            //this.enviarCorreoSMTP(emailContacto, String.format(ASUNTO,_vel_NroOrdenCompra), body, this.parametroMapper.getMailSetting());
        } catch (Exception ex) {
            logger.error("Error al enviar creacion documentos soporte  OC a " + emailContacto, ex);
            respuesta = ex.getMessage();
        }
        return respuesta;
    }


    public String enviarOcModificada(OrdenCompra ordenCompra, Usuario proveedor, Usuario comprador, List<OrdenCompraDetalle> ordenCompraDetalles) {
        //Sociedad sociedad = sociedadRepository.getByCodigoSociedad(ordenCompra.getSociedad());
        Sociedad sociedad = new Sociedad();
        sociedad.setRazonSocial(""); // temporal

        String emailContacto = "";
        String respuesta = "";
        ////////////////////// VARIABLES METODO VELPA //////////////////////

        String _vel_Contacto = "";
        String _vel_NombreDestinatario = "";
        String _vel_TextoEmpresa = "";
        String _vel_NroOrdenCompra = ordenCompra.getNumeroOrdenCompra();
        String _vel_UrlPublicacionOC = urlPortalPublicacionOC;
        String urlPortal = "";

        HtmlEmail htmlMail = new HtmlEmail();



        if (proveedor == null && comprador != null) { // email a comprador (cliente)
            emailContacto = comprador.getEmail();
            _vel_Contacto = comprador.getNombre() + " " + comprador.getApellido();
            _vel_NombreDestinatario = comprador.getNombre() + " " + comprador.getApellido();
            _vel_TextoEmpresa = "para la empresa proveedora " + ordenCompra.getProveedorRazonSocial();
            //sociedad.setRazonSocial(comprador.getNombre() + " " + comprador.getApellido());
            urlPortal = urlPortalCentenario;
        }
        else if(proveedor != null && comprador == null){ // email a responsable de compras (proveedor)
//            emailContacto = proveedor.getEmailPersonaCompra();
//            _vel_Contacto = proveedor.getNombrePersonaCompra();
            emailContacto = proveedor.getEmail();
            _vel_Contacto = proveedor.getApellido();
            _vel_NombreDestinatario = ordenCompra.getProveedorRazonSocial();
            _vel_TextoEmpresa = "de la empresa " + sociedad.getRazonSocial();
            urlPortal = urlPortalProveedor;
        }
        else {
            logger.error("Error en los datos de proveedor/comprador para el envio de correo de Publicacion de OC: " + ordenCompra.getNumeroOrdenCompra());
            return "Error en los datos de proveedor/comprador para el envio de correo de Publicacion de OC";
        }


        try {

            VelocityContext context = new VelocityContext();

            String LOGO_H = generateCidResourceUtilsNuevo(htmlMail, LOGO_HEADER,"header");

            String LOGO_CAL = generateCidResourceUtilsNuevo(htmlMail, LOGO_CALL,"call");

            String LOGO_FOOT = generateCidResourceUtilsNuevo(htmlMail, LOGO_FOOTER,"footer");


            context.put("nombreProveedor", _vel_NombreDestinatario);
            //context.put("vendor_header", LOGO_H);
            //context.put("vendor_call_action",LOGO_CAL );
            context.put("vendor_footer", LOGO_FOOT);
            context.put("urlportal", urlPortal);
            context.put("nroOrdenCompra", _vel_NroOrdenCompra);

            String content = Optional.ofNullable(TEMPLATEMOD)
                    .map(url -> url + "")
                    .map(template -> {
                        int i = 0;
                        return getContentMail(context, template);
                    })
                    .orElse("");

            MailSetting mailSetting = this.parametroMapper.getMailSetting();
            htmlMail.setHostName(mailSetting.getHost());
            htmlMail.setSmtpPort(Integer.parseInt(mailSetting.getPort()));
            htmlMail.setAuthenticator(new DefaultAuthenticator(mailSetting.getUser(), mailSetting.getPassword()));
            htmlMail.addTo(emailContacto);
            htmlMail.setFrom(mailSetting.getEmailFrom(), mailSetting.getNameFrom());
            htmlMail.setSubject(String.format(ASUNTO,_vel_NroOrdenCompra));
            htmlMail.setHtmlMsg(content);
            htmlMail.setCharset("UTF-8");
            htmlMail.setStartTLSEnabled(true);
            htmlMail.setDebug(true);
            htmlMail.setTLS(true);

            //implementacion de adjunto
            if(ordenCompraDetalles != null){
                String pdfBase64Oc = jcoOrdenCompraPdfService.obtenerBase64OCAdjuntoEmail(ordenCompra, ordenCompraDetalles);
                byte[] pdfBytesOc = this.converterToPdfBase64(pdfBase64Oc);

                DataSource dataSource = new ByteArrayDataSource(pdfBytesOc, "application/pdf");
                String nombreArchivo = ordenCompra.getNumeroOrdenCompra() + ".pdf";
                htmlMail.attach(dataSource, nombreArchivo, ordenCompra.getNumeroOrdenCompra());

            }

            htmlMail.send();
            respuesta = "Correo Publicacion OC enviado";
            //this.enviarCorreoSMTP(emailContacto, String.format(ASUNTO,_vel_NroOrdenCompra), body, this.parametroMapper.getMailSetting());
        } catch (Exception ex) {
            logger.error("Error al enviar Correo por Publicacion OC a " + emailContacto, ex);
            respuesta = ex.getMessage();
        }
        return respuesta;
    }



    public String enviarLogs(String uuidRegister, List<String> Logs, String emailContacto) {

        HtmlEmail htmlMail = new HtmlEmail();
        String respuesta = "";
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String fechaHoraActual = LocalDateTime.now().format(formatter);
            StringBuilder sb = new StringBuilder();

            sb.append("<html>");
            sb.append("<head>");
            sb.append("<style>");
            sb.append("body { font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px; }");
            sb.append(".container { background: #ffffff; padding: 20px; border-radius: 8px; box-shadow: 0 0 10px rgba(0,0,0,0.1); }");
            sb.append("h2 { color: #d9534f; }");
            sb.append("p { font-size: 14px; color: #555555; }");
            sb.append("ul { background: #f8f9fa; padding: 15px; border: 1px solid #dee2e6; border-radius: 5px; }");
            sb.append("li { margin-bottom: 10px; font-size: 14px; }");
            sb.append("</style>");
            sb.append("</head>");
            sb.append("<body>");
            sb.append("<div class='container'>");
            sb.append("<h2>Reporte Log</h2>");
            sb.append("<p><strong>Fecha y hora:</strong> ").append(fechaHoraActual).append("</p>");
            sb.append("<p>Estimado usuario,</p>");
            sb.append("<p>Se ha generado el siguiente listado de Mensajes durante la ejecución de carga masiva de OC con el codigo de UUID :</p>").append("<b>"+uuidRegister +"</b>");
            sb.append("<ul>");

            for (String log : Logs) {
                sb.append("<li>").append(log).append("</li>");
            }

            sb.append("</ul>");
            sb.append("<p>Por favor, revise esta información y contáctese con el equipo técnico si requiere soporte adicional.</p>");
            sb.append("<p>Este mensaje fue enviado automáticamente por el sistema.</p>");
            sb.append("</div>");
            sb.append("</body>");
            sb.append("</html>");

            String content = sb.toString();

            MailSetting mailSetting = this.parametroMapper.getMailSetting();
            htmlMail.setHostName(mailSetting.getHost());
            htmlMail.setSmtpPort(Integer.parseInt(mailSetting.getPort()));
            htmlMail.setAuthenticator(new DefaultAuthenticator(mailSetting.getUser(), mailSetting.getPassword()));
            htmlMail.addTo(emailContacto);
            htmlMail.setFrom(mailSetting.getEmailFrom(), mailSetting.getNameFrom());
            htmlMail.setSubject(String.format("Carga masiva de OC"));
            htmlMail.setHtmlMsg(content);
            htmlMail.setCharset("UTF-8");
            htmlMail.setStartTLSEnabled(true);
            htmlMail.setDebug(true);
            htmlMail.setTLS(true);
            htmlMail.send();
            respuesta = "1";
        } catch (Exception ex) {
            logger.error("Error al enviar Correo por Publicacion OC a " + emailContacto, ex);
            ex.printStackTrace();
        }
        return respuesta;
    }


    private byte[] converterToPdfBase64(String htmlSource) throws IOException, DocumentException {
        OutputStream file = new FileOutputStream(new File("HTMLtoPDF.pdf"));
        com.itextpdf.text.Document document = new com.itextpdf.text.Document();
        document.setMargins(20, 20, 20, 20);
        com.itextpdf.text.pdf.PdfWriter writer = com.itextpdf.text.pdf.PdfWriter.getInstance(document, file);
        document.open();
        InputStream is = new ByteArrayInputStream(htmlSource.getBytes(StandardCharsets.UTF_8));
        XMLWorkerHelper.getInstance().parseXHtml(writer, document, is);
        document.close();
        file.close();

// 2. Leer el PDF generado
        Path path = Paths.get("HTMLtoPDF.pdf");

        return Files.readAllBytes(path);
    }
}