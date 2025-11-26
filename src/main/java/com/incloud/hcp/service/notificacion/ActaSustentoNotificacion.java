package com.incloud.hcp.service.notificacion;

import com.incloud.hcp.domain.*;
import com.incloud.hcp.dto.ActaSustentoEvaluacionDto;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.HtmlEmail;
import org.apache.velocity.VelocityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Administrador on 13/11/2017.
 */
@Component
public class ActaSustentoNotificacion extends NotificarMail {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final String ASUNTO = "IProvider - Registro Acta de Sustento";
    private final String ASUNTOEVALUACION = "IProvider - Evaluación de Acta de Sustento";
    private final String LOGO = "com/incloud/hcp/image/sapCompanyLogo.png";
    private final String LOGO_HEADER = "com/incloud/hcp/templates/img/header.png";
    private final String LOGO_CALL = "com/incloud/hcp/templates/img/call-to-action.png";
    private final String LOGO_FOOTER = "com/incloud/hcp/templates/img/jrclogo.jpg";
    private final String TEMPLATE = "com/incloud/hcp/templates/portal/ActaSustento.html";
    private final String TEMPLATEAPROBACION = "com/incloud/hcp/templates/portal/ActaSustentoAprobada.html";
    private final String TEMPLATEPREAPROBACION = "com/incloud/hcp/templates/portal/ActaSustentoPreAprobada.html";
    private final String TEMPLATERECHAZO= "com/incloud/hcp/templates/portal/ActaSustentoRechazada.html";

//    @Value("${sm.portal.url}")
    @Value("${cfg.portal.url}")
    private  String urlPortal;

    @Value("${cfg.notificacion.consulta.url}")
    private String urlConsulta;


    public String enviar(MailSetting mailSetting, Proveedor proveedor, ActaSustento sustento, List<OrdenCompraDetalle> ordenCompraDetalleList,Usuario usuario){
        String contactoProveedor = proveedor.getContacto();
        String rucProveedor = proveedor.getRuc();
        String nombreProveedor = proveedor.getRazonSocial();
        String nroActaSustento = sustento.getNumeroActaSustento();

        HtmlEmail htmlMail = new HtmlEmail();
        String respuesta = "";
        // Insertar la imagen y obtener el ID de contenido
//        String cid = this.generateCidResourceUtils(htmlMail, LOGO);
//        cid = cid == null ? generateCidFromUrlExt(htmlMail) : cid;
//        logger.error("Error al generar nuevo cid de Internet, generando local");
//        cid = generateCidResourceUtils(htmlMail, LOGO);
//        logger.error("El nuevo cid:" + cid);

        ////////////////////// BEGIN METODO VELPA //////////////////////

       String body = "";
        body = body + "<table cellspacing='0' cellpadding='0' border='1px #deb887'>";
        for(OrdenCompraDetalle lic : ordenCompraDetalleList) {
            body = body + "<tr style='border-collapse: collapse;width: 600px;padding: 0;margin: 0; border: 1px #2b3f7b '>";
            body = body + "<td align='center' ";
            body = body + "style='border-collapse: collapse;width: 300px;padding: 0;margin-left: 5px;padding-top: 36px;padding-bottom: 12px'>";
            body = body + "Código Posición : "+lic.getOpSolicitudCompra();
            body = body + "</td>";
            body = body + "<td align='center' ";
            body = body + "style='border-collapse: collapse;width: 300px;padding: 0;margin-left: 5px;padding-top: 36px;padding-bottom: 12px'>";
            body = body + "Posición : "+ lic.getPosicion();
            body = body + "</td>";
            body = body + "</tr>";
        }
        body = body + "</table>";

        //////////////////////  END  METODO VELPA //////////////////////

        try {

            VelocityContext context = new VelocityContext();

            String LOGO_H = generateCidResourceUtilsNuevo(htmlMail, LOGO_HEADER,"header");

            String LOGO_CAL = generateCidResourceUtilsNuevo(htmlMail, LOGO_CALL,"call");

            String LOGO_FOOT = generateCidResourceUtilsNuevo(htmlMail, LOGO_FOOTER,"footer");


            context.put("contacto", contactoProveedor);
            //context.put("vendor_header", LOGO_H);
            //context.put("vendor_call_action",LOGO_CAL );
            context.put("vendor_footer", LOGO_FOOT);
            context.put("urlportal", urlPortal);
            context.put("nroActaSustento", nroActaSustento);
            context.put("ordenCompra", sustento.getOrdenCompra().getNumeroOrdenCompra());
            context.put("lista", body);

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
            htmlMail.addTo(proveedor.getEmail());
            htmlMail.addTo(proveedor.getEmail());
            htmlMail.addTo(usuario.getEmail());
            htmlMail.setFrom(mailSetting.getEmailFrom(), mailSetting.getNameFrom());
            htmlMail.setSubject(ASUNTO);
            htmlMail.setHtmlMsg(content);
            htmlMail.setCharset("UTF-8");
            htmlMail.setStartTLSEnabled(true);
            htmlMail.setDebug(true);
            htmlMail.setTLS(true);

            htmlMail.send();

            respuesta = "Correo ProveedorPotencialAprobadoNotificacion enviado";
            //this.enviarCorreoSMTP(proveedor.getEmail(), ASUNTO, body, mailSetting);
        } catch (Exception ex) {
            respuesta = ex.getMessage();
            logger.error("Error al enviar el correo por ProveedorPotencialAprobadoNotificacion al proveedor " + nombreProveedor + " con RUC " + rucProveedor, ex);
        }
        return respuesta;
    }

    public String enviarCorreoProveedorActaEmHes(MailSetting mailSetting, Proveedor proveedor, String nroActaSustento, ActaSustentoEvaluacionDto bean,ActaSustento actaSustento){
        String contactoProveedor = proveedor.getContacto();
        String rucProveedor = proveedor.getRuc();
        String nombreProveedor = proveedor.getRazonSocial();
        AtomicInteger totalAcpetado = new AtomicInteger(0);
        String moneda = actaSustento.getOrdenCompra().getCodigoMondeda();
        String respuesta = "";
        if(bean.getFormularioEm() != null){
            if(bean.getFormularioEm().getActaSustentoDetalle().size()> 0){
                bean.getFormularioEm().getActaSustentoDetalle().forEach(item->{
                     totalAcpetado.set(item.getOrdenCompraDetalle().getPrecioTotal().intValue());
                });
            }
        }

        if(bean.getFormularioHes() != null){
            if(bean.getFormularioHes().getActaSustentoDetalle().size()> 0){
                bean.getFormularioHes().getActaSustentoDetalle().forEach(item->{
                    totalAcpetado.set(item.getOrdenCompraDetalle().getPrecioTotal().intValue());
                });
            }
        }

        HtmlEmail htmlMail = new HtmlEmail();

        // Insertar la imagen y obtener el ID de contenido
//        String cid = this.generateCidResourceUtils(htmlMail, LOGO);
//        cid = cid == null ? generateCidFromUrlExt(htmlMail) : cid;
//        cid = generateCidResourceUtils(htmlMail, LOGO);
//        logger.error("El nuevo cid: " + cid);

        ////////////////////// BEGIN METODO VELPA //////////////////////
        String body = "<!DOCTYPE html>";
        body = body + "<html lang='en'>";
        body = body + "<head>";
        body = body + "<meta http-equiv='Content-Type' content='text/html; charset=UTF-8'/>";
        body = body + "</head>";
        body = body + "<body style='width: 600px;margin: auto;padding: 0;font-family: Arial;font-size: 14px;color: #333'>";

        body = body + "<table cellspacing='0' cellpadding='0' width='600px'>";
        body = body + "<tr style='border-collapse: collapse;width: 600px;padding: 0;margin: 0'>";
        body = body + "<td class='main_first' ";
        body = body + "style='border-collapse: collapse;width: 600px;padding: 0;margin: 0;padding-top: 12px;padding-bottom: 12px'>";
        body = body + "<h1 style='font-family: Arial;font-weight: bold;font-size: 16px;color: #555;margin-bottom: 24px'>";
        body = body + "Estimado/a " + contactoProveedor + "&#44;</h1>";
        body = body + "<p style='font-family: Arial;font-size: 14px;line-height: 18px'>";
        body = body + "Se ha Evaluado el acta de sustento Nro. "+nroActaSustento+",se encuentra Aprobada y el monto aceptado.  &#44;<br/>";
        body = body + "por favor ingrese al siguiente enlace para continuar con el proceso, para que pueda presentar sus facturas :<br/>";
        body = body + "<a href=\"" + urlPortal + "\">Click para ingresar a su cuenta</a><br/><br/>";
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
        body = body + "<p style='font-family: Arial;font-size: 12px;line-height: 13px;color: #888'>";
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
        body = body + "<p style='font-family: Arial;font-size: 14px;line-height: 18px'>";
        body = body + "Atentamente&#44;<br/>";
        body = body + "Equipo IProvider<br/>";
        body = body + "JRC<br/>";
        body = body + "<img src='cid:' align='LEFT' width='148' height='46'>";
        body = body + "</p>";
        body = body + "</td>";
        body = body + "</tr>";
        body = body + "</table>";

        body = body + "</body>";
        body = body + "</html>";

        //////////////////////  END  METODO VELPA //////////////////////
        try {


            VelocityContext context = new VelocityContext();

            String LOGO_H = generateCidResourceUtilsNuevo(htmlMail, LOGO_HEADER,"header");

            String LOGO_CAL = generateCidResourceUtilsNuevo(htmlMail, LOGO_CALL,"call");

            String LOGO_FOOT = generateCidResourceUtilsNuevo(htmlMail, LOGO_FOOTER,"footer");


            context.put("contacto", contactoProveedor);
            //context.put("vendor_header", LOGO_H);
            //context.put("vendor_call_action",LOGO_CAL );
            context.put("vendor_footer", LOGO_FOOT);
            context.put("urlportal", urlPortal);
            context.put("nroActa", nroActaSustento);
            context.put("totalAceptado", totalAcpetado);
            context.put("moneda", moneda);

            String content = Optional.ofNullable(TEMPLATEAPROBACION)
                    .map(url -> url + "")
                    .map(template -> {
                        int i = 0;
                        return getContentMail(context, template);
                    })
                    .orElse("");



            htmlMail.setHostName(mailSetting.getHost());
            htmlMail.setSmtpPort(Integer.parseInt(mailSetting.getPort()));
            htmlMail.setAuthenticator(new DefaultAuthenticator(mailSetting.getUser(), mailSetting.getPassword()));
            htmlMail.addTo(proveedor.getEmail());
            htmlMail.setFrom(mailSetting.getEmailFrom(), mailSetting.getNameFrom());
            htmlMail.setSubject(ASUNTOEVALUACION);
            htmlMail.setHtmlMsg(content);
            htmlMail.setCharset("UTF-8");
            htmlMail.setStartTLSEnabled(true);
            htmlMail.setDebug(true);
            htmlMail.setTLS(true);

            htmlMail.send();
            respuesta = "Correo ProveedorPotencialAprobadoNotificacion enviado";
            //this.enviarCorreoSMTP(proveedor.getEmail(), ASUNTO, body, mailSetting);
        } catch (Exception ex) {
            respuesta = ex.getMessage();
            logger.error("Error al enviar el correo por ProveedorPotencialAprobadoNotificacion al proveedor " + nombreProveedor + " con RUC " + rucProveedor, ex);
        }

        return respuesta;
    }

    public String enviarActaRechazada(MailSetting mailSetting, Proveedor proveedor, ActaSustento actaSustento,String motivo) {
        String contactoProveedor = proveedor.getContacto();
        String rucProveedor = proveedor.getRuc();
        String nombreProveedor = proveedor.getRazonSocial();
        String nroActaSustento = actaSustento.getNumeroActaSustento();
        String ordenCompra = actaSustento.getOrdenCompra().getNumeroOrdenCompra();
        String respuesta = "";
        HtmlEmail htmlMail = new HtmlEmail();

        //////////////////////  END  METODO VELPA //////////////////////
        try {


            VelocityContext context = new VelocityContext();

            String LOGO_H = generateCidResourceUtilsNuevo(htmlMail, LOGO_HEADER,"header");

            String LOGO_CAL = generateCidResourceUtilsNuevo(htmlMail, LOGO_CALL,"call");

            String LOGO_FOOT = generateCidResourceUtilsNuevo(htmlMail, LOGO_FOOTER,"footer");


            context.put("contacto", contactoProveedor);
            //context.put("vendor_header", LOGO_H);
            //context.put("vendor_call_action",LOGO_CAL );
            context.put("vendor_footer", LOGO_FOOT);
            context.put("urlportal", urlPortal);
            context.put("nroActa", nroActaSustento);
            context.put("motivo", motivo);
            context.put("ordenCompra", ordenCompra);

            String content = Optional.ofNullable(TEMPLATERECHAZO)
                    .map(url -> url + "")
                    .map(template -> {
                        int i = 0;
                        return getContentMail(context, template);
                    })
                    .orElse("");



            htmlMail.setHostName(mailSetting.getHost());
            htmlMail.setSmtpPort(Integer.parseInt(mailSetting.getPort()));
            htmlMail.setAuthenticator(new DefaultAuthenticator(mailSetting.getUser(), mailSetting.getPassword()));
            htmlMail.addTo(proveedor.getEmail());
            htmlMail.setFrom(mailSetting.getEmailFrom(), mailSetting.getNameFrom());
            htmlMail.setSubject("IProvider - Acta de Sustento Rechazada");
            htmlMail.setHtmlMsg(content);
            htmlMail.setCharset("UTF-8");
            htmlMail.setStartTLSEnabled(true);
            htmlMail.setDebug(true);
            htmlMail.setTLS(true);

            htmlMail.send();
            respuesta = "Correo ProveedorPotencialAprobadoNotificacion enviado";
            //this.enviarCorreoSMTP(proveedor.getEmail(), ASUNTO, body, mailSetting);
        } catch (Exception ex) {
            respuesta = ex.getMessage();
            logger.error("Error al enviar el correo por ProveedorPotencialAprobadoNotificacion al proveedor " + nombreProveedor + " con RUC " + rucProveedor, ex);
        }
        return respuesta;
    }

    public String enviarActaPreAprobar(MailSetting mailSetting, Proveedor proveedor, ActaSustento actaSustento,String motivo) {
        String contactoProveedor = proveedor.getContacto();
        String rucProveedor = proveedor.getRuc();
        String nombreProveedor = proveedor.getRazonSocial();
        String nroActaSustento = actaSustento.getNumeroActaSustento();
        String ordenCompra = actaSustento.getOrdenCompra().getNumeroOrdenCompra();
        String respuesta = "";
        HtmlEmail htmlMail = new HtmlEmail();

        //////////////////////  END  METODO VELPA //////////////////////
        try {


            VelocityContext context = new VelocityContext();

            String LOGO_H = generateCidResourceUtilsNuevo(htmlMail, LOGO_HEADER,"header");

            String LOGO_CAL = generateCidResourceUtilsNuevo(htmlMail, LOGO_CALL,"call");

            String LOGO_FOOT = generateCidResourceUtilsNuevo(htmlMail, LOGO_FOOTER,"footer");


            context.put("contacto", contactoProveedor);
            //context.put("vendor_header", LOGO_H);
            //context.put("vendor_call_action",LOGO_CAL );
            context.put("vendor_footer", LOGO_FOOT);
            context.put("urlportal", urlPortal);
            context.put("nroActa", nroActaSustento);
            context.put("motivo", motivo);
            context.put("ordenCompra", ordenCompra);

            String content = Optional.ofNullable(TEMPLATEPREAPROBACION)
                    .map(url -> url + "")
                    .map(template -> {
                        int i = 0;
                        return getContentMail(context, template);
                    })
                    .orElse("");



            htmlMail.setHostName(mailSetting.getHost());
            htmlMail.setSmtpPort(Integer.parseInt(mailSetting.getPort()));
            htmlMail.setAuthenticator(new DefaultAuthenticator(mailSetting.getUser(), mailSetting.getPassword()));
            htmlMail.addTo(proveedor.getEmail());
            htmlMail.setFrom(mailSetting.getEmailFrom(), mailSetting.getNameFrom());
            htmlMail.setSubject("IProvider - Acta de Sustento Aprobada");
            htmlMail.setHtmlMsg(content);
            htmlMail.setCharset("UTF-8");
            htmlMail.setStartTLSEnabled(true);
            htmlMail.setDebug(true);
            htmlMail.setTLS(true);

            htmlMail.send();
            respuesta = "Correo ProveedorPotencialAprobadoNotificacion enviado";
            //this.enviarCorreoSMTP(proveedor.getEmail(), ASUNTO, body, mailSetting);
        } catch (Exception ex) {
            respuesta = ex.getMessage();
            logger.error("Error al enviar el correo por ProveedorPotencialAprobadoNotificacion al proveedor " + nombreProveedor + " con RUC " + rucProveedor, ex);
        }
        return respuesta;
    }
}
